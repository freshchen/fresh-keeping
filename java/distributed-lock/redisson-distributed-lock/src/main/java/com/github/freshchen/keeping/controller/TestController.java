package com.github.freshchen.keeping.controller;

import com.github.freshchen.keeping.common.lib.model.JsonResult;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author darcy
 * @since 2021/01/14
 **/
@RestController
public class TestController {

    /**
     * 模拟抢红包 总共 10000 元，每次抢一元，抢完为止
     * 分析： 此场景无法做幂等，且并发场景会出现超卖
     */
    private Integer money = 10000;
    private static final String RED_PACKET_SN = "redPacketSn1";

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 优点：可靠性很高，等于串行化了，极大的解决了并发问题
     * 缺点：如果接口很慢，等待时间会很长，并且有可能死锁
     *
     * @return
     */
    @GetMapping("minus")
    public JsonResult<String> minus() {
        // getLock 返回的是非公平锁，也就是请求不分先后
        RLock lock = redissonClient.getLock(RED_PACKET_SN);
        // 1 拿到锁
        // 直接上锁，不指定任何参数，框架拿到锁会设置锁的超时时间为看门狗的超时时间，也就是 30s
        // 看门狗是个后台守护线程，每 10（30 / 3）秒把持有锁的超时时间设置为 30 秒
        // 只要业务没执行玩，比如数据库死锁了，就会一直续租，导致分布式锁也死锁了
        // 如果业务执行完没有主动释放锁，那就是 30 秒超时后自动释放
        // 2 拿不到锁，会一直等着
        lock.lock();
        try {
            assert money > 0;
            // 可以看 redis 中 RED_PACKET_SN 会续租
            sleepSeconds(15);
            money--;
        } finally {
            lock.unlock();
        }
        return JsonResult.ok(String.valueOf(money));
    }

    /**
     * 优点：但是不会死锁了，
     * 缺点：可靠性没有上面高
     *
     * @return
     */
    @GetMapping("minus1")
    public JsonResult<String> minus1() {
        RLock lock = redissonClient.getLock(RED_PACKET_SN);
        // 1 拿到锁
        // 超时时间 5 秒，只要指定了释放时间，看门狗就不会续期
        // 2 拿不到锁，会一直等着
        lock.lock(5, TimeUnit.SECONDS);
        try {
            assert money > 0;
            sleepSeconds(15);
            money--;
        } finally {
            // 由于 5 秒后自动释放锁，所以执行时间 15 秒。此时锁肯定被别的请求持有了，不能释放别人的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return JsonResult.ok(String.valueOf(money));
    }

    /**
     * 优点：如果不能立刻拿到锁，可以快速返回
     * 缺点：用户体验不好
     *
     * @return
     */
    @GetMapping("minus2")
    public JsonResult<String> minus2() {
        RLock lock = redissonClient.getLock(RED_PACKET_SN);
        // 1 拿到锁
        // 和 lock 类似，会有看门狗
        // 2 拿不到锁，返回 false
        boolean locked = lock.tryLock();
        if (locked) {
            try {
                assert money > 0;
                sleepSeconds(15);
                money--;
            } finally {
                lock.unlock();
            }
        } else {
            return JsonResult.ok("系统繁忙");
        }
        return JsonResult.ok(String.valueOf(money));
    }

    /**
     * 优点：用户体验好一点
     * 缺点：同样可靠性没有上面高，并发不断尝试拿锁会消耗很多 CPU
     *
     * @return
     */
    @GetMapping("minus3")
    public JsonResult<String> minus3() {
        RLock lock = redissonClient.getLock(RED_PACKET_SN);
        // 1 拿到锁
        // 超时时间 5 秒，只要指定了释放时间，看门狗就不会续期
        // 2 拿不到锁，会一直尝试拿锁 5 秒，超过5秒返回 false
        boolean locked = false;
        try {
            locked = lock.tryLock(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (locked) {
            try {
                assert money > 0;
                sleepSeconds(15);
                money--;
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            return JsonResult.ok("系统繁忙");
        }
        return JsonResult.ok(String.valueOf(money));
    }

    /**
     * 可重入
     *
     * @return
     */
    @GetMapping("minus4")
    public JsonResult<String> minus4() {
        RLock lock = redissonClient.getLock(RED_PACKET_SN);
        // 1 拿到锁
        // 超时时间 5 秒，只要指定了释放时间，看门狗就不会续期
        // 2 拿不到锁，会一直尝试拿锁 5 秒，超过5秒返回 false
        lock.lock();
        try {
            assert money > 0;
            money--;
            // 返回结果会减 4 元，耗时 1 分钟，也就是 minus2， minus3中的 tryLock 都能拿到锁并且执行
            minus();
            minus2();
            minus3();
            sleepSeconds(15);
        } finally {
            lock.unlock();
        }
        return JsonResult.ok(String.valueOf(money));
    }

    /**
     * 公平锁
     */
    @GetMapping("minus/{seq}")
    public JsonResult<String> minus5(@PathVariable Integer seq) {
        // 公平锁
        RLock lock = redissonClient.getFairLock(RED_PACKET_SN);
        // 如果拿不到锁，会根据初次尝试时间在redis中记录，并排好队
        lock.lock();
        try {
            assert money > 0;
            // 可以看 redis 中 RED_PACKET_SN 会续租
            sleepSeconds(15);
            money--;
        } finally {
            lock.unlock();
        }
        return JsonResult.ok(seq + ":" + money);
    }

    /**
     * 读写锁写
     */
    @GetMapping("minus6")
    public JsonResult<String> minus6() {
        // 公平锁
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(RED_PACKET_SN);
        RLock rLock = readWriteLock.readLock();
        RLock wLock = readWriteLock.writeLock();
        rLock.lock();
        wLock.lock();
        try {
            assert money > 0;
            // 可以看 redis 中 RED_PACKET_SN 会续租
            sleepSeconds(15);
            money--;
        } finally {
            rLock.unlock();
            wLock.unlock();
        }
        return JsonResult.ok(String.valueOf(money));
    }

    /**
     * 读写锁 读
     */
    @GetMapping("minus7")
    public JsonResult<String> minus7() {
        // 公平锁
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(RED_PACKET_SN);
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        try {
            assert money > 0;
        } finally {
            rLock.unlock();
        }
        return JsonResult.ok(String.valueOf(money));
    }

    private void sleepSeconds(Integer time) {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

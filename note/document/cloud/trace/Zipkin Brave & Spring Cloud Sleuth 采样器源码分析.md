---
begin: 2022-03-10
status: ongoing
rating: 1
---

# Zipkin Brave & Spring Cloud Sleuth 采样器源码分析

# 简介

引入分布式链路收集之后难免会有一定的开销，开销主要分为生成开销、收集开销。对于 Zipkin Brave 来说生成开销几乎是微不足道的，主要是在内存中创建一些链路上下文、跨度对象，且会增加微小的延时。但收集的开销就比较大，首先数据上报会占用业务系统一定的 CPU、内存、线程资源。然后数据落盘存储也会消耗一定的存储成本，对于互联网应用等数据量大且低延时要求高的系统来说影响是不可忽视的。
Dapper 论文中指出在 Google 在分布式链路追踪实际过程中，降低采样率是最直接也是收益最高的减少开销的方式，并且数据表明当时谷歌的量级，系统最微小的错误都会被无限放大，因此我们不用担心丢弃部分采样会让我们错过关键信息，谷歌实践过程中设置采样率为 1/1024 时就以及足够获得有用信息，并且开销也显著降低。

## 概念

首先采样决定了一条链路或者说一次请求是否需要采集。采样主要如下分为三种策略

| 对比 | 前置采样 | 后置采样| 单元采样 |
| :-- | :-- | :-- | :-- |
| 思路 | 链路开始的时候决定是否采样，一旦决定不采样，整个请求就不生成跨度信息，也不上报 | 同链路的跨度全部在系统中缓存，链路结束之后根据条件决定是否上报，链路结束之后根据条件决定是否上报。 |只上报出错的跨度，不会上传完成跨度|
| 链路完整 | 完整 | 完整                                                         |不完整|
| 开销 | 中 | 中                                                           |小|
| 实现复杂度 | 简单 | 困难                                                         |中等|
| 灵活性 | 低 | 中                                                           |强|

了解完基本的概念之后让我们一起看一下 zipkin brave 以及 spring cloud sleuth 为我们提供了哪些开箱即用的采样器

# 采样器源码分析

## Sampler

Sampler 是一个抽象类，属于前置采样，仅执行一次，相当于对链路的高考

```java
public abstract class Sampler {

  // 根据 traceId 决定是否采样，由于 Sampler 执行的很早，此时能用的信息只有 traceId
  public abstract boolean isSampled(long traceId);

  // 全部采样的默认实现
  public static final Sampler ALWAYS_SAMPLE = new Sampler() {
    @Override public boolean isSampled(long traceId) {
      return true;
    }

    @Override public String toString() {
      return "AlwaysSample";
    }
  };

  // 全不采样的默认实现
  public static final Sampler NEVER_SAMPLE = new Sampler() {
    @Override public boolean isSampled(long traceId) {
      return false;
    }

    @Override public String toString() {
      return "NeverSample";
    }
  };

  // 创建一个百分比采样器
  public static Sampler create(float probability) {
    return CountingSampler.create(probability);
  }
}
```

## CountingSampler

CountingSampler 是 Brave 中 Sampler 的按照比例采样的实现类，CountingSampler 基于计数器来实现，统计的百分比是精准的，适合小流量应用。如果流量很大，精确度要求不高，性能要求更高的场景不适用 CountingSampler。具体代码如下

```java
public final class CountingSampler extends Sampler {

  // 0.01 ~ 1
  public static Sampler create(final float probability) {
    if (probability == 0) return NEVER_SAMPLE;
    if (probability == 1.0) return ALWAYS_SAMPLE;
    if (probability < 0.01f || probability > 1) {
      throw new IllegalArgumentException(
        "probability should be between 0.01 and 1: was " + probability);
    }
    return new CountingSampler(probability);
  }

  // 计数器
  private final AtomicInteger counter;
  // 需要采样的位图，不直接用 hashset 为了提高性能
  private final BitSet sampleDecisions;

  CountingSampler(float probability) {
    this(probability, new Random());
  }

  CountingSampler(float probability, Random random) {
    counter = new AtomicInteger();
    int outOf100 = (int) (probability * 100.0f);
    this.sampleDecisions = randomBitSet(100, outOf100, random);
  }

  @Override
  public boolean isSampled(long traceIdIgnored) {
    // 先取模成 0 ~ 99 ，如果在预先生成的 bitset 里面则需要采样。注意并没有用到 traceId，且 sampleDecisions 是随机的，相同 traceId 不同服务采样结果可能不一样，因此一样决定采样要增加标识告知下面的跨度都需要采样
    return sampleDecisions.get(mod(counter.getAndIncrement(), 100));
  }

  @Override
  public String toString() {
    return "CountingSampler()";
  }

  /**
   * Returns a non-negative mod.
   */
  static int mod(int dividend, int divisor) {
    int result = dividend % divisor;
    return result >= 0 ? result : divisor + result;
  }

  /**
   * size 是100，如果 probability 是 0.1 则 cardinality 是 10。也就是 从 0 到 99 里面选择 10 个数字
   * http://stackoverflow.com/questions/12817946/generate-a-random-bitset-with-n-1s
   */
  static BitSet randomBitSet(int size, int cardinality, Random rnd) {
    BitSet result = new BitSet(size);
    int[] chosen = new int[cardinality];
    int i;
    for (i = 0; i < cardinality; ++i) {
      chosen[i] = i;
      result.set(i);
    }
    for (; i < size; ++i) {
      int j = rnd.nextInt(i + 1);
      if (j < cardinality) {
        result.clear(chosen[j]);
        result.set(i);
        chosen[j] = i;
      }
    }
    return result;
  }
}
```

## ProbabilityBasedSampler
ProbabilityBasedSampler 是 sleuth 中 Sampler 的按照比例采样的实现，CountingSampler 存在计数器可能越界，除了以下方法，其他和 CountingSampler 一样

```java
	@Override
	public boolean isSampled(long traceId) {
		if (this.probability.get() == 0) {
			return false;
		}
		else if (this.probability.get() == 1.0f) {
			return true;
		}
		synchronized (this) {
			final int i = this.counter.getAndIncrement();
			boolean result = this.sampleDecisions.get(i);
			if (i == 99) {
			    // 让计数器在 0 ~ 99 中循环，而不是取模
				this.counter.set(0);
			}
			return result;
		}
	}
```


## BoundarySampler

BoundarySampler 是 brave 中  Sampler 按照比例采样的实现，相比于 CountingSampler 和 ProbabilityBasedSampler，没有采样计数器，因此准确度不是特别高，适合大流量场景，支持万分之一的采用控制，并且直接对 traceId 取模，因此能保证同一个服务，traceId 相同采样结果相同，但也要求 traceId 的生成要足够分散

```java
public final class BoundarySampler extends Sampler {
  static final long SALT = new Random().nextLong();

  // 支持  0.0001 ~ 1
  public static Sampler create(float probability) {
    if (probability == 0) return Sampler.NEVER_SAMPLE;
    if (probability == 1.0) return ALWAYS_SAMPLE;
    if (probability < 0.0001f || probability > 1) {
      throw new IllegalArgumentException(
        "probability should be between 0.0001 and 1: was " + probability);
    }
    final long boundary = (long) (probability * 10000); // safe cast as less <= 1
    return new BoundarySampler(boundary);
  }

  private final long boundary;

  BoundarySampler(long boundary) {
    this.boundary = boundary;
  }

  // 扰乱后直接取余
  @Override
  public boolean isSampled(long traceId) {
    long t = Math.abs(traceId ^ SALT);
    return t % 10000 <= boundary;
  }

}
```

## 参考链接


##### 标签






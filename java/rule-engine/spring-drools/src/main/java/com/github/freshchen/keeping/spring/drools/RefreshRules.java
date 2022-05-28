package com.github.freshchen.keeping.spring.drools;

import com.github.freshchen.keeping.spring.drools.dao.RulesDao;
import com.github.freshchen.keeping.spring.drools.model.Rules;
import com.github.freshchen.keeping.spring.drools.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.cdi.KContainer;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.SequentialOption;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author darcy
 * @since 2022/05/01
 **/
@Service
@Slf4j
public class RefreshRules implements ApplicationRunner {

    private KieServices kieServices = KieServices.Factory.get();
    @Autowired
    private LoginService loginService;

    @KContainer
    private KieContainer kieContainer;

    @Autowired
    private RulesDao rulesDao;

    @Scheduled(fixedDelay = 1000)
    public void reload() {
        refresh();
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        refresh();
    }

    private void refresh() {
        List<Rules> rules = rulesDao.findAll();
        KieFileSystem kfs = kieServices.newKieFileSystem();

        KieModuleModel kieModuleModel = kieServices.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kieModuleModel.newKieBaseModel("rules")
                .setDefault(true).setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
                .setSequential(SequentialOption.YES)
                .setEventProcessingMode(EventProcessingOption.STREAM);
        kieBaseModel1.newKieSessionModel("all-rules").setDefault(true)
                .setType(KieSessionModel.KieSessionType.STATELESS)
                .setClockType(ClockTypeOption.get("realtime"));
        kfs.writeKModuleXML(kieModuleModel.toXML());
        rules.forEach(r -> {
            kfs.delete("src/main/resources/rules/login.drl");
            kfs.write("src/main/resources/rules/login.drl", r.getDrl());
        });
        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            System.out.println(results.getMessages());
            throw new IllegalStateException("### errors ###");
        }
        ReleaseId defaultReleaseId = kieServices.getRepository().getDefaultReleaseId();
        MemoryKieModule kieModule = (MemoryKieModule) kieServices.getRepository().getKieModule(defaultReleaseId);
        MemoryFileSystem memoryFileSystem = kieModule.getMemoryFileSystem();
        byte[] bytes = memoryFileSystem.writeAsBytes();


        Resource resource = kieServices.getResources().newByteArrayResource(bytes);
        KieModule addKieModule = kieServices.getRepository().addKieModule(resource);
        KieContainer container = kieServices.newKieContainer(addKieModule.getReleaseId());
//        KnowledgeBaseImpl kieBase = (KnowledgeBaseImpl) container.getKieBase("rules");
//
//        byte[] serialize = SerializationUtils.serialize(kieBase);
//
//        KnowledgeBaseImpl deserialize = SerializationUtils.deserialize(serialize);

        StatelessKieSession session = container.newStatelessKieSession();
//        try (ByteArrayOutputStream bo = new ByteArrayOutputStream();) {
//
//            KieServices.Factory.get().getMarshallers().newMarshaller(kieBase).marshall(bo, session);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        LoginFact loginFact = new LoginFact();
        loginFact.setAge(10);
        loginFact.setName("name");
        loginFact.setRisk(false);

        session.execute(loginFact);
        log.info(loginFact.toString());

    }

}

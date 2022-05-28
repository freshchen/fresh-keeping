package com.github.freshchen.keeping.drools;

import lombok.extern.slf4j.Slf4j;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.SequentialOption;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

/**
 * @author darcy
 * @since 2022/5/28
 */
@Slf4j
public class ProgramKieModuleStateless {

    private static final KieServices KIE = KieServices.Factory.get();
    private static byte[] jar = null;

    public static byte[] createJar(String code, String ruleDrl) {
        KieFileSystem kfs = KIE.newKieFileSystem();
        KieModuleModel kieModuleModel = KIE.newKieModuleModel();
        KieBaseModel baseModel = kieModuleModel.newKieBaseModel()
                .setDefault(true)
                .setSequential(SequentialOption.YES)
                .setEventProcessingMode(EventProcessingOption.STREAM);
        baseModel.newKieSessionModel(code)
                .setDefault(true)
                .setType(KieSessionModel.KieSessionType.STATELESS);
        kfs.writeKModuleXML(kieModuleModel.toXML());
        String filePath = "src/main/resources/rules/" + code + ".drl";
        kfs.delete(filePath);
        kfs.write(filePath, ruleDrl);
        KieBuilder kieBuilder = KIE.newKieBuilder(kfs).buildAll();
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            log.warn(results.getMessages().toString());
            return null;
        }
        ReleaseId defaultReleaseId = KIE.getRepository().getDefaultReleaseId();
        MemoryKieModule kieModule = (MemoryKieModule) KIE.getRepository().getKieModule(defaultReleaseId);
        MemoryFileSystem memoryFileSystem = kieModule.getMemoryFileSystem();
        return memoryFileSystem.writeAsBytes();
    }

    public static void save(byte[] jar) {
        ProgramKieModuleStateless.jar = jar;
    }

    public static StatelessKieSession getKieSession() {
        Resource resource = KIE.getResources().newByteArrayResource(ProgramKieModuleStateless.jar);
        KieModule addKieModule = KIE.getRepository().addKieModule(resource);
        KieContainer container = KIE.newKieContainer(addKieModule.getReleaseId());
        return container.newStatelessKieSession();
    }
}

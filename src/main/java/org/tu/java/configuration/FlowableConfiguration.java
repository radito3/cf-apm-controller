package org.tu.java.configuration;

import org.flowable.common.engine.impl.persistence.StrongUuidGenerator;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlowableConfiguration {

    @Bean
    public ProcessEngine processEngine() {
        ProcessEngineConfiguration cfg =
            new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
                .setJdbcUsername("sa")
                .setJdbcPassword("")
                .setJdbcDriver("org.h2.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        cfg.setIdGenerator(new StrongUuidGenerator());

        ProcessEngine processEngine = cfg.buildProcessEngine();

        processEngine.getRepositoryService()
                     .createDeployment()
                     .addClasspathResource("../bg-uploader.bpmn20.xml")
                     .deploy();

        return processEngine;
    }

}

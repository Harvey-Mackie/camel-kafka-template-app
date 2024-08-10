package com.example.accounting.rules;

import com.example.accounting.model.PaymentEntity;
import com.example.accounting.model.Rule;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.Consumer;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.mvel2.util.Make.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import java.util.List;
@Service
public class RulesEngineService {

    private static final Logger log = LoggerFactory.getLogger(RulesEngineService.class);

    //to-do - add rule groups - then can only apply if credit/debit or acc platform or outbound etc

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private List<Rule> rules;

    @SneakyThrows
    public KieSession createKieSession(PaymentEntity payload) {
        Resource drlResource = resourceLoader.getResource("classpath:com/example/accounting/rules/accounting-rules.drt");

        // Compile the rules from the provided list into a DRL string
        String compiledRules = new ObjectDataCompiler().compile(rules, drlResource.getInputStream());
        log.info("Compiled Rules:\n{}", compiledRules);

        // Create the KieSession and set the global entity
        KieSession kieSession = createKieSessionFromDRL(compiledRules);
        kieSession.setGlobal("entity", payload);

        return kieSession;
    }

    private KieSession createKieSessionFromDRL(String drl) {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, "com/example/accounting/rules/accounting-rules.drl");

        Results results = kieHelper.verify();

        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {
            results.getMessages(Message.Level.WARNING, Message.Level.ERROR).forEach(
                    message -> log.error("Error: {}", message.getText())
            );
            throw new IllegalStateException("Errors detected during DRL compilation.");
        }

        return kieHelper.build().newKieSession();
    }

    public PaymentEntity applyAllRules(PaymentEntity entity) {
        KieSession kieSession = createKieSession(entity);

        kieSession.insert(entity);
        kieSession.fireAllRules();
        kieSession.dispose();  // Dispose of the session when done

        return entity;
    }
}


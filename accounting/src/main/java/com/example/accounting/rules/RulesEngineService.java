package com.example.accounting.rules;

import com.example.accounting.model.DRLStyle;
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
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
@Service
public class RulesEngineService {

    private static final Logger log = LoggerFactory.getLogger(RulesEngineService.class);

    //to-do - add rule groups - then can only apply if credit/debit or acc platform or outbound etc

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private List<Rule> rules;

    @Autowired
    @Qualifier("debitRules")
    private List<Rule> debitRules;

    @Autowired
    @Qualifier("creditRules")
    private List<Rule> creditRules;

    @SneakyThrows
    private KieSession createKieSession(PaymentEntity payload, DRLStyle drlStyle) {

        Resource drlResource = null;
        List<Rule> applicableRules = List.of();
        if(drlStyle.equals(DRLStyle.WITH_EVAL_FILER)){
            drlResource = resourceLoader.getResource("classpath:com/example/accounting/rules/accounting-rules-with-filter.drt");
            applicableRules = getApplicableRulesWithFilter(payload);
        }
        else if(drlStyle.equals(DRLStyle.DEFAULT)){
             drlResource = resourceLoader.getResource("classpath:com/example/accounting/rules/accounting-rules.drt");
            applicableRules = getApplicableRules(payload);
        }

        String compiledRules = new ObjectDataCompiler().compile(applicableRules, drlResource.getInputStream());
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

    private List<Rule> getApplicableRulesWithFilter(PaymentEntity payload) {
        return rules.stream()
                // Evaluate each rule's condition and filter out those that evaluate to false
                .filter(rule -> {
                    boolean conditionResult = rule.getCondition().getRuleConditions()
                            .stream()
                            .allMatch(condition -> {
                                condition.setPaymentEntity(payload);
                                return Boolean.parseBoolean(condition.toString().replace("eval(", "").replace(")", ""));
                            });
                    return conditionResult;
                })
                .collect(Collectors.toList());
    }

    private List<Rule> getApplicableRules(PaymentEntity payload) {
        rules.forEach(rule -> rule.getCondition().getRuleConditions().forEach(condition -> condition.setPaymentEntity(payload)));
        return rules;
    }


    public PaymentEntity applyAllRules(PaymentEntity entity, DRLStyle drlStyle) {
        KieSession kieSession = createKieSession(entity, drlStyle);

        kieSession.insert(entity);
        kieSession.fireAllRules();
        kieSession.dispose();  // Dispose of the session when done

        return entity;
    }
}


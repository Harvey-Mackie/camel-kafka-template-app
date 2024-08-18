package com.example.accounting;

import com.example.accounting.model.DRLStyle;
import com.example.accounting.model.PaymentEntity;
import com.example.accounting.rules.RulesEngineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AccountingApplication.class)  // Specify your main application class here
class AccountingApplicationTests {

	@Autowired
	private RulesEngineService rulesEngineService;

	@Test
	void testApplyAllRules() {
		// Create a test PaymentEntity
		PaymentEntity paymentEntity = PaymentEntity.builder()
				.transactionType("DEBIT")
				.transactionId("12345")
				.customerId("CUST123")
				.amount(100.5)
				.currency("USD")
				.description("Payment for Invoice #12345")
				.charges(new ArrayList<>())
				.build();


		// Apply the rules to the PaymentEntity
		List<Long> memoryUsedInDefault = new ArrayList<>();
		for(int i = 0; i < 20; i++){
			memoryUsedInDefault.add(this.applyRules(paymentEntity, DRLStyle.DEFAULT));
		}

		List<Long> memoryUsedWithFilter = new ArrayList<>();
		for(int i = 0; i < 20; i++){
			memoryUsedWithFilter.add(this.applyRules(paymentEntity, DRLStyle.WITH_EVAL_FILER));
		}

		// Sort the lists to compute the median
		Collections.sort(memoryUsedInDefault);
		Collections.sort(memoryUsedWithFilter);

		// Calculate the median for each
		long medianDefault = calculateMedian(memoryUsedInDefault);
		long medianWithFilter = calculateMedian(memoryUsedWithFilter);

		// Calculate the mean for each
		long meanDefault = calculateMean(memoryUsedInDefault);
		long meanWithFilter = calculateMean(memoryUsedWithFilter);

		// Output the results
		System.out.println("Median memory used with DRLStyle.DEFAULT: " + medianDefault + " bytes");
		System.out.println("Median memory used with DRLStyle.WITH_EVAL_FILTER: " + medianWithFilter + " bytes");

		// Calculate percentage improvement
		double percentageImprovement = ((double)(medianDefault - medianWithFilter) / medianDefault) * 100;
		double percentageImprovementMean = ((double)(meanDefault - meanWithFilter) / medianDefault) * 100;

		// Print the percentage improvement
		System.out.printf("MEDIAN Memory improvement with DRLStyle.WITH_EVAL_FILTER: %.2f%%\n", percentageImprovement);
		System.out.printf("MEAN Memory improvement with DRLStyle.WITH_EVAL_FILTER: %.2f%%\n", percentageImprovementMean);


		// Compare the two medians
		if (medianDefault < medianWithFilter) {
			System.out.println("DRLStyle.DEFAULT is more memory efficient.");
		} else if (medianWithFilter < medianDefault) {
			System.out.println("DRLStyle.WITH_EVAL_FILTER is more memory efficient.");
		} else {
			System.out.println("Both DRLStyle.DEFAULT and DRLStyle.WITH_EVAL_FILTER use similar memory.");
		}

		// Optionally assert that one approach is more efficient if needed
		assertThat(medianWithFilter).isLessThan(medianDefault);
	}

	private long calculateMedian(List<Long> memoryUsages) {
		int size = memoryUsages.size();
		if (size % 2 == 1) {
			// If odd, return the middle element
			return memoryUsages.get(size / 2);
		} else {
			// If even, return the average of the two middle elements
			return (memoryUsages.get(size / 2 - 1) + memoryUsages.get(size / 2)) / 2;
		}
	}

	private long calculateMean(List<Long> memoryUsages) {
		long sum = 0;
		for (long memoryUsage : memoryUsages) {
			sum += memoryUsage;
		}
		return sum / memoryUsages.size();
	}

	private long applyRules(PaymentEntity paymentEntity, DRLStyle drlStyle){
		System.gc(); // Request GC before starting to minimize external interference
		// Memory before processing for this iteration
		long beforeProcessingMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

		rulesEngineService.applyAllRules(paymentEntity, drlStyle);

		long afterProcessingMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		// Log memory usage for this iteration
		long memoryUsedThisIteration = afterProcessingMemory - beforeProcessingMemory;

		System.out.println("Memory Used - " + memoryUsedThisIteration);

		return memoryUsedThisIteration;
	}
}

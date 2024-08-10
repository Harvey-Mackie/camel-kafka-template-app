package com.example.accounting;

import com.example.accounting.rules.DroolsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {DroolsConfig.class})
class AdapterApplicationTests {

	@Test
	void contextLoads() {
	}

}

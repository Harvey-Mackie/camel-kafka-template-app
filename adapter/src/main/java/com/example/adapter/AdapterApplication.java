package com.example.adapter;

import org.apache.camel.main.Main;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.adapter.routes.KafkaRoute;

@SpringBootApplication
public class AdapterApplication {
	public static void main(String[] args) throws Exception {
		Main main = new Main();
		main.configure().addRoutesBuilder(new KafkaRoute());
		main.run(args);
	}
}

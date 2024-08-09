package com.example.posting;

import org.apache.camel.main.Main;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.posting.routes.KafkaRoute;

@SpringBootApplication
public class PostingApplication {
	public static void main(String[] args) throws Exception {
		Main main = new Main();
		main.configure().addRoutesBuilder(new KafkaRoute());
		main.run(args);
	}
}

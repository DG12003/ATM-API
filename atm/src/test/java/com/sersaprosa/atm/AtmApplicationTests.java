package com.sersaprosa.atm;

import com.sersaprosa.atm.controller.AtmController;
import com.sersaprosa.atm.service.AtmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AtmApplicationTests {
	@Autowired
	private AtmController atmController;

	@Autowired
	private AtmService atmService;

	@Test
	void contextLoads() {
		assertThat(atmController).isNotNull();
		assertThat(atmService).isNotNull();
	}

}

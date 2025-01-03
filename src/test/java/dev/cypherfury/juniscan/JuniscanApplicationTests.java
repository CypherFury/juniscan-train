package dev.cypherfury.juniscan;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JuniscanApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void main_shouldCallSpringApplicationRun() {
		// Arrange
		String[] args = {};

		// Act
		JuniscanApplication.main(args);

		// Assert
	}

}

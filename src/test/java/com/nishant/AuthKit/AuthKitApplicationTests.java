package com.nishant.AuthKit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "jwt.secret=testsecretkeythatisatleast32characterslong",
    "jwt.expiration=86400000"
})
class AuthKitApplicationTests {

	@Test
	void contextLoads() {
	}

}

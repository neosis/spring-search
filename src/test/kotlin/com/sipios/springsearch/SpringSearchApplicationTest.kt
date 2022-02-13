package com.sipios.springsearch

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = [SpringSearchApplication::class])
@Transactional
class SpringSearchApplicationTest {

}

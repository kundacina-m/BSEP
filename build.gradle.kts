import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.1.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	kotlin("jvm") version "1.3.72"
	kotlin("plugin.spring") version "1.3.72"
	kotlin("plugin.jpa") version "1.3.72"
	id("org.owasp.dependencycheck") version "5.3.2.1"

}

group = "com.mkundacina"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8


repositories {
	mavenCentral()
}



dependencies {

	// Spring boot libs
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-mail")


	// Kotlin compatibility libs
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// Logger
	implementation("org.apache.logging.log4j","log4j-api","2.13.3")
	implementation("org.apache.logging.log4j","log4j-core","2.13.3")
	implementation("org.apache.logging.log4j","log4j-api-kotlin","1.0.0")


	// Test libs
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("org.springframework.security:spring-security-test")

	implementation("org.bouncycastle","bcprov-jdk15on", "1.64")
	implementation("org.bouncycastle","bcpkix-jdk15on", "1.64")

	implementation("io.jsonwebtoken","jjwt","0.6.0")

	// h2 database, available only in runtime -> temporary database
	runtimeOnly("com.h2database:h2")

}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

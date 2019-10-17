

plugins {
    java
    id("org.springframework.boot") version "2.2.0.RELEASE"
}

apply(plugin = "io.spring.dependency-management")

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}


repositories {
    jcenter()
    mavenCentral()
}

springBoot {

}
//bootJar {
//    baseName = "camera-iq-rest-service"
//    version = "0.0.1-SNAPSHOT"
//}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtime("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

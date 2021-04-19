plugins {
    kotlin("jvm") version "1.4.32"
}

group = "com.hornedheck"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    // https://mvnrepository.com/artifact/org.mockftpserver/MockFtpServer
    implementation("org.mockftpserver:MockFtpServer:2.7.1")

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j", "slf4j-api", "1.7.25")
    implementation("org.apache.logging.log4j", "log4j-api", "2.14.1")
    implementation("org.apache.logging.log4j", "log4j-core", "2.14.1")
    implementation("org.apache.logging.log4j", "log4j-slf4j-impl", "2.14.1")

}

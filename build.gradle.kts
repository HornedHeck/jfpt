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

}

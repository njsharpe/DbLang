plugins {
    id("java")
}

group = "net.njsharpe"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:+")

    compileOnly("org.projectlombok:lombok:+")
    annotationProcessor("org.projectlombok:lombok:+")

    implementation("org.xerial:sqlite-jdbc:+")
    implementation("org.apache.commons:commons-lang3:+")

    testCompileOnly("org.jetbrains:annotations:+")

    testCompileOnly("org.projectlombok:lombok:+")
    testAnnotationProcessor("org.projectlombok:lombok:+")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.slf4j:slf4j-simple:+")
}

tasks.test {
    useJUnitPlatform()
}
plugins {
    id("java")
    id("application")
}

application {
    mainClass.set("org.example.Main")
}

group = "org.example"
version = "1.0-SNAPSHOT"
val polypheny_jdbc_driver_version = "2.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.polypheny:polypheny-jdbc-driver:${polypheny_jdbc_driver_version}")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("com.konghq:unirest-java:3.14.5")
}

tasks.test {
    useJUnitPlatform()
}
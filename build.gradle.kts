import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("maven-publish")
}

group = "pl.sg"
version = "1.0-SNAPSHOT"

val output = ByteArrayOutputStream()
project.exec {
    commandLine = "aws codeartifact get-authorization-token --domain sg-repository --domain-owner 215372400964 --region eu-central-1 --query authorizationToken --output text".split(" ")
    standardOutput = output
}
val codeartifactToken = output.toString()

repositories {
    mavenCentral()
    maven {
        url = uri("https://sg-repository-215372400964.d.codeartifact.eu-central-1.amazonaws.com/maven/sg-repository/")
        credentials {
            username = "aws"
            password = codeartifactToken
        }
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")

}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "pl.sg"
            artifactId = "loans"
            version = "1.0.2"

            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("https://sg-repository-215372400964.d.codeartifact.eu-central-1.amazonaws.com/maven/sg-repository/")
            credentials {
                username = "aws"
                password = codeartifactToken
            }
        }
    }
}
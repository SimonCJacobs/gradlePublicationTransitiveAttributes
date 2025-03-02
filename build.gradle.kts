import org.gradle.api.internal.tasks.JvmConstants

plugins {
    java
    `maven-publish`
}

group = "myGroup"
version = "1.0"

val myConfiguration by configurations.registering {
    attributes {
        attribute<String>(Attribute.of(String::class.java), "myAttributeValue")
    }
}

dependencies {
    components {
        withModule("com.google.guava:guava") {
            attributes {
                attribute<String>(Attribute.of(String::class.java), "myAttributeValue")
            }
        }
    }
    @Suppress("UnstableApiUsage")
    myConfiguration("com.google.guava:guava:33.4.0-jre")
}


val javaComponent = components.named<AdhocComponentWithVariants>(JvmConstants.JAVA_MAIN_COMPONENT_NAME) {
    addVariantsFromConfiguration(myConfiguration.get()) {
        mapToMavenScope("compile")
    }
}

publishing {
    repositories {
        maven(layout.buildDirectory.dir("myRepo")) {
            name = "myRepo"
        }
    }
    publications {
        create<MavenPublication>("myPublication") {
            from(javaComponent.get())
        }
    }
}

tasks.wrapper {
    gradleVersion = "8.13"
}
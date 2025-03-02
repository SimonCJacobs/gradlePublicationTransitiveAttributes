import foo.Foo
import org.gradle.api.internal.tasks.JvmConstants

plugins {
    java
    `maven-publish`
}

group = "myGroup"
version = "1.0"

repositories {
    maven(layout.buildDirectory.dir("myRepo"))
    mavenCentral()
}

val myConfiguration by configurations.registering {
    attributes {
        attribute(Attribute.of(String::class.java), "myAttributeValue")
        attribute(Attribute.of(Foo::class.java), objects.named("22"))
    }
}



val myConfiguration2 by configurations.registering {
    attributes {
        attribute<String>(Attribute.of(String::class.java), "myAttributeValue")
        attribute(Attribute.of(Foo::class.java), objects.named("22"))
    }
}

dependencies {
    attributesSchema {
        attribute(Attribute.of(String::class.java))
        attribute(Attribute.of(Foo::class.java))
    }
    components {
        withModule("com.google.guava:guava") {
            attributes {
       //         attribute<String>(Attribute.of(String::class.java), "myAttributeValueX")
            }
        }
    }
    @Suppress("UnstableApiUsage")
    myConfiguration("com.google.guava:guava:33.4.0-jre")
    myConfiguration2("myGroup:gradleTransitive:1.0")
}


myConfiguration2.get().files.forEach {
    println("GOT ${it.canonicalPath}")
}
myConfiguration2.get().incoming.artifacts.artifacts.forEach { artifactResult ->
    println("Got ${artifactResult.id} with ${artifactResult.variant}")
}

val javaComponent = components.named<AdhocComponentWithVariants>(JvmConstants.JAVA_MAIN_COMPONENT_NAME) {
    addVariantsFromConfiguration(myConfiguration.get()) {
        mapToMavenScope("compile")
    }
}

artifacts {
    add(myConfiguration.name, tasks.jar)
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
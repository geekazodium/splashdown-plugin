plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("xyz.jpenilla.run-paper") version "2.1.0" // Adds runServer and runMojangMappedServer tasks for testing

    id ("com.diffplug.spotless") version "6.19.0"
}

group = "com.geekazodium"
version = "1.0.0-SNAPSHOT"
description = "Shooter game thing idfk"

repositories{
    flatDir{
        dir("LocalLibs")
    }
}

java {
  // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
  toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    implementation("me.libraryaddict.libsdisguises:LibsDisguises-10.0.37-Github")
    compileOnly("com.comphenix.protocollib:ProtocolLib")
  // paperweight.foliaDevBundle("1.20.1-R0.1-SNAPSHOT")
  // paperweight.devBundle("com.example.paperfork", "1.20.1-R0.1-SNAPSHOT")
}

tasks {
  // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        val props = mapOf(
              "name" to project.name,
              "version" to project.version,
              "description" to project.description,
              "apiVersion" to "1.20"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    /*
    reobfJar {
    // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
    // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
    outputJar.set(layout.buildDirectory.file("libs/PaperweightTestPlugin-${project.version}.jar"))
    }
    */

    spotless {
        // spotless config from wynntils/artemis rewritten in kotlin
        java {
            // define the steps to apply to Java source code
            importOrder()
            removeUnusedImports()
            palantirJavaFormat("2.33.0")
            trimTrailingWhitespace()
            endWithNewline()
            //          Custom rule from https://github.com/apache/geode
            custom("Refuse wildcard imports") {
                findIllegalPattern(it,"import [^\n]+\\*;","Don't use * imports.")
                it
            }
            //custom rule
            custom("Refuse static imports") {
                findIllegalPattern(it,"import static [^\n]+;","Don't use static imports.")
                it
            }

            custom("Refuse system out calls") {
                findIllegalPattern(it,"System[ \n]?.[ \n]?out","use Debug.Log instead.")
                it
            }

            custom("No empty line after opening curly brace") {
                it.replace(Regex("\\{\n\n"), "{\n")
            }
            licenseHeader(
                "/*\n" +
                    " * Copyright © Geekazodium \$YEAR.\n" +
                    " * This file is released under GPLv3. See LICENSE for full license details.\n" +
                    " */"
            ).updateYearWithLatest(true)
        }

        json {
            target("src/**/*.json")
            gson()
                    .indentWithSpaces(2)
                    .sortByKeys()
                    .version("2.10.1")
                trimTrailingWhitespace()
                endWithNewline()
            }
            format("lang") {
            target("src/main/resources/assets/wynntils/lang/*.json")
            custom("No empty language json files") {
                it.replace(Regex("^\\{\\}\n$"), "")
            }
        }
        kotlinGradle{
            target("**/*.gradle")
            trimTrailingWhitespace()
            endWithNewline()
        }
        format("misc") {
            // define the files to apply `misc` to
            target("*.gradle", "*.md", ".gitignore", "*.properties")
            targetExclude("CHANGELOG.md")

            // define the steps to apply to those files
            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }

    }
}

fun findIllegalPattern(it:String, pattern: String, errorPrefix: String){
    val regex = Regex(pattern);
    if (regex.containsMatchIn(it)) {
        val packageMatcher = Regex("package [^\n]+;")
        val classMatcher = Regex("\npublic {1,3}class ([^\n(){}]+)")
        val errorMessage =
            "from package: " + (packageMatcher.find(it)?.groups?.get(0)?.value ?: "unknown") +
                "\nclass: " + (classMatcher.find(it)?.groups?.get(1)?.value ?: "unknown") +
                "\nat line: " + regex.find(it)?.groups?.get(0)!!.value
        //I'M SO SORRY TO ANYONE WHO ACTUALLY CODES IN KOTLIN AND IS CRINGING AT THIS SPAGHETTI.
        //I'M SO SORRY PLEASE HAVE MERCY
        //I KNOW THIS IS WRONG I JUST NEEDED IT TO WORK PLEASE LEAVE ME ALONE I'M NOT HERE
        //TO STIR UP TROUBLE.
        throw AssertionError("$errorPrefix \n$errorMessage")
    }
}


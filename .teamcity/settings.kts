import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.buildSteps.exec
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.sharedResource
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.04"

project {

    buildType(Test)
    buildType(Test_2)

    params {
        param("DockerRegistryUser", "docker")
        password("DockerRegistryPassword", "credentialsJSON:4a98e93a-69e2-436e-8f1e-f472688e18a9")
        param("DockerRegistryUrl", "nexus.ru-central1.internal:8082")
    }

    features {
        sharedResource {
            id = "PROJECT_EXT_2"
            name = "asdasd"
            resourceType = infinite()
        }
    }
}

object Test : BuildType({
    name = "build"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        dockerCommand {
            name = "Docker Build"
            commandType = build {
                source = file {
                    path = "Dockerfile"
                }
                namesAndTags = "%DockerRegistryUrl%/hello-world:v%build.counter%"
            }
        }
        script {
            name = "Docker Login"
            scriptContent = "docker login %DockerRegistryUrl% -u %DockerRegistryUser% -p %DockerRegistryPassword%"
        }
        dockerCommand {
            name = "Docker Push"
            commandType = push {
                namesAndTags = "%DockerRegistryUrl%/hello-world:v%build.counter%"
            }
        }
    }

    triggers {
        vcs {
            branchFilter = "+:master"
        }
    }
})

object Test_2 : BuildType({
    name = "test"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        exec {
            name = "Go Test"
            path = "go"
            arguments = "test ./..."
            dockerImage = "golang:1.19"
            dockerRunParameters = "-v /opt/buildagent/work/:/opt/buildagent/work/"
            param("script.content", """
                #!/bin/bash
                
                go test ./...
            """.trimIndent())
        }
    }

    triggers {
        vcs {
            branchFilter = """
                +:*
                -:master
            """.trimIndent()
        }
    }
})

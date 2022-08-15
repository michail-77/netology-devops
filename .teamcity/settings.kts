import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.buildSteps.exec
import jetbrains.buildServer.configs.kotlin.buildSteps.script
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
    buildType(Build)

    template(BuildDocker)

    params {
        param("goVersion", "1.19")
    }
}

object Build : BuildType({
    templates(BuildDocker)
    name = "build"

    params {
        param("dockerImageName", "hello-world")
    }

    triggers {
        vcs {
            id = "TRIGGER_2"
            branchFilter = "+:<default>"
        }
    }
})

object Test : BuildType({
    name = "test"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        exec {
            name = "Test"
            path = "go"
            arguments = "test ./..."
            dockerImage = "golang:%goVersion%"
            param("script.content", "go test ./...")
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

    features {
        commitStatusPublisher {
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:5455382f-6c65-4d87-b74d-bcaf98a5a50f"
                }
            }
        }
    }
})

object BuildDocker : Template({
    name = "build-docker"

    params {
        param("dockerImageName", "")
        password("dockerRegistryPassword", "credentialsJSON:dda34e18-1ef7-4b0c-89b8-92127353f015", display = ParameterDisplay.HIDDEN)
        param("dockerRegistryUser", "docker")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        dockerCommand {
            name = "Docker Build"
            id = "RUNNER_2"
            commandType = build {
                source = file {
                    path = "Dockerfile"
                }
                namesAndTags = "nexus.ru-central1.internal:8082/%dockerImageName%:v%build.counter%"
            }
        }
        script {
            name = "Docker Login"
            id = "RUNNER_3"
            scriptContent = "docker login nexus.ru-central1.internal:8082 -u %dockerRegistryUser% -p %dockerRegistryPassword%"
        }
        dockerCommand {
            name = "Docker Push"
            id = "RUNNER_4"
            commandType = push {
                namesAndTags = "nexus.ru-central1.internal:8082/%dockerImageName%:v%build.counter%"
            }
        }
    }

    triggers {
        vcs {
            id = "TRIGGER_2"
            branchFilter = "+:master"
        }
    }
})

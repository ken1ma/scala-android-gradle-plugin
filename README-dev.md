# Testing directly in an Android project

1. Symlink the `plugin`directory as `buildSrc` in an Android project directory (where `settings.gradle` resides)
    1. `buildSrc` is built before the Android project
    1. In the Android project directory
        1. `./gradlew build` will build everything (TODO: check)
        2. `./gradlew assembleDebug` will build the debug variant only
        3. `./gradlew installDebug` will install to an Android device
    1. Append the followings to `build.gradle` in `plugin` directory to skip the tests

            test { enabled = false }
            functionalTest { enabled = false }

    1. `./gradlew assembleDebug -Dorg.gradle.debug=true` will launch a process that `jdb -attach 5005` can attach to ([doc](https://docs.gradle.org/current/userguide/troubleshooting.html))
        1. Issue `run` when attached


# Check points

1. Build all the variants
    1. Make sure `src/main` can be run
    1. Make sure `src/test` can be run
    1. Make sure `src/androidTest` can be run
    1. Add a custom variant and make sure it can be run

1. References
    1. `R` is available in `src/main/scala`
    2. `src/main/scala` and `src/main/java` are mix-compiled

1. Android Studio Debugging

1. Scalatest

1. Scaladoc

1. Proguard

1. Windows




# Misc.

## How Gradle Wrapper has beein installed

    gradle-6.7/bin/gradle wrapper

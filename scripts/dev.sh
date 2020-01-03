#!/usr/bin/env bash
if [ "$1" == "update-core" ]; then
        ./gradlew :core:clean :core:jar :core:install
        cp core/build/libs/core-0.1.0.jar ui/tool/lib/core.jar
        exit 0
elif [ "$1" == "update-spring-lib" ]; then
        ./gradlew :spring-lib:clean :spring-lib:install
        exit 0
elif [ "$1" == "ui-local-install" ]; then
        cd ui
        cd tool
        npm run build
        npm uninstall -g ui-expose-tool
        npm link
        exit 0
else
        echo "THIS IS THE VALID BLOCK"
fi

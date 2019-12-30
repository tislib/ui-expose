#!/usr/bin/env bash

set -e

cd ui
cd angular-lib
npm install
npm run build

#cd ../core
#npm install
#npm run build

cd ../tool
npm install
npm run build

cd ../..
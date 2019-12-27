#!/bin/bash
cd "$(dirname "$0")" || exit

cd ../core || exit
npm install
npm run build
npm version patch
npm publish --access public

cd ../angular-lib || exit
npm install
npm run build
cd dist/lib || exit
npm version patch
npm publish --access public
cd ../..

cd ../tool || exit
npm install
npm run build
npm version patch
npm publish --access public

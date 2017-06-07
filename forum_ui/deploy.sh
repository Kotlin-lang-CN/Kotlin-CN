#!/usr/bin/env bash

cp ../../release/Config.js src/assets/js/Config.js

cnpm install && npm run build

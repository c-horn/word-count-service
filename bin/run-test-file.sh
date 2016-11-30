#!/usr/bin/env bash

HERE="$(dirname "${BASH_SOURCE[0]}")"

curl -X POST -d @$HERE/../src/main/resources/words.txt http://localhost:8080/count-words

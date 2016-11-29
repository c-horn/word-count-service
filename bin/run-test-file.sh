#!/usr/bin/env bash

HERE="$(dirname "${BASH_SOURCE[0]}")"

curl -X POST -d @$HERE/../data/words.txt http://localhost:8080/count-words

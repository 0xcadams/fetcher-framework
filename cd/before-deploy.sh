#!/bin/bash
set -ev
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_7786718ab80b_key -iv $encrypted_7786718ab80b_iv -in cd/sk.asc.enc -out cd/sk.asc -d
    gpg --fast-import cd/sk.asc
fi
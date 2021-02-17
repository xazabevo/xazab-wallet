#!/bin/bash

DEPLOY_TYPE=$1

if [ "${DEPLOY_TYPE}" = "master" ] || [ "${TRAVIS_TAG:0:4}" = "NMA-" ] || [ "${TRAVIS_TAG:0:4}" = "dpl-" ]; then

  cd "$TRAVIS_BUILD_DIR" || exit

  if [ "${DEPLOY_TYPE}" = "master" ]; then
    TRAVIS_TAG="master"
  fi

  eval "$(ssh-agent -s)" # Start ssh-agent cache
  chmod 600 .deploy/id_rsa # Allow read access to the private key
  ssh-add .deploy/id_rsa # Add the private key to SSH

  echo -e "Let's deploy it!"
  echo "TRAVIS_TAG: $TRAVIS_TAG"
#  ls -l wallet/build/outputs/apk/_testNet3/debug/
  git clone git@github.com:xazab-mobile-team/xazab-wallet-staging.git
  DEPLOY_DIR=xazab-wallet-staging/"$TRAVIS_TAG"
  mkdir -p "$DEPLOY_DIR"
  cp wallet/build/outputs/apk/_testNet3/debug/xazab-wallet-_testNet3-debug.apk "$DEPLOY_DIR"/xazab-wallet-_testNet3-debug.apk
  cp wallet/build/outputs/apk/prod/debug/xazab-wallet-prod-debug.apk "$DEPLOY_DIR"/xazab-wallet-prod-debug.apk
#  cp wallet/build/outputs/apk/_testNet3/debug/xazab-wallet-_testNet3-debug.apk xazab-wallet-staging/"$TRAVIS_TAG"/xazab-wallet-_testNet3-debug.apk

  # generate README.md file
  README="$DEPLOY_DIR"/README.md
#  if [ "${TRAVIS_TAG:0:4}" = "NMA-" ]; then
#    printf 'https://xazab.atlassian.net/browse/%s\n\n' "$TRAVIS_TAG" > "$README"
#  fi
  {
    echo "### Test builds:"
    echo "* [xazab-wallet-prod-debug.apk](https://github.com/xazab-mobile-team/xazab-wallet-staging/raw/master/$TRAVIS_TAG/xazab-wallet-prod-debug.apk)"
    echo "* [xazab-wallet-_testNet3-debug.apk](https://github.com/xazab-mobile-team/xazab-wallet-staging/raw/master/$TRAVIS_TAG/xazab-wallet-_testNet3-debug.apk)"
    echo "### Deploy info:"
    # print the content of `git show "$TRAVIS_TAG"` into README.md until the first occurence of 'diff'
    # the very last line containing 'diff' is removed by | head -n -1
    git show "$TRAVIS_TAG" | grep -m1 -B20 "diff" | head -n -1
  } > "$README"

  cd xazab-wallet-staging || exit
  git add .
  git commit -m "Travis CI deploy $TRAVIS_TAG"
  git push origin master

  # clean up the mess
  cd "$TRAVIS_BUILD_DIR" || exit
  rm -rf xazab-wallet-staging
  rm -rf "$TRAVIS_BUILD_DIR"/app/build/outputs
  if ! [ "${TRAVIS_TAG}" = "master" ]; then
    echo "deleting tag $TRAVIS_TAG"
#    git tag -d "$TRAVIS_TAG"
    git push -q https://"$PERSONAL_ACCESS_TOKEN"@github.com/xazabevo/xazab-wallet --delete "refs/tags/$TRAVIS_TAG"
  fi
else
  echo "Only tags or master"
fi
echo "Deploy done"

# TaoTao

## Intro

Travis CI, Current version is 0.1

[![Build Status](https://travis-ci.org/vonsy/TaoTao.svg?branch=master)](https://travis-ci.org/vonsy/TaoTao)

## Features

签到签退

### Debian & Ubuntu

#### 定时任务
```
crontab -e
MAILTO=your@email.com
45 7,17 * * 1,2,3,4,5 /taotao.sh
```
```
taotao.sh
java -jar -Dfile.encoding=UTF-8 -Dhttps.protocols=TLSv1.2 -Djavax.net.debug=all -Dhttps.cipherSuites=TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256 /home/taotao/TaoTao.jar | tee -a /home/taotao/taotao.log
```

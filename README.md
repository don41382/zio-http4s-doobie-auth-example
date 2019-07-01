# zio-http4s-doobie-auth-example

Welcome to my ZIO playground. After I visited a scala meetup where [Wiem](https://github.com/wi101) presented the ZIO framework, I knew I had to dig into this. 

I included my favourite frameworks:

- http4s
- circe
- doobie
- postgres
- pureconfig

## What can I do with this?

It's a really simple app and just has to http access point:

1. Show alle existing users in a database via the [Root](http://127.0.0.1:8080) route
2. Allow existing users too authenticate via the [/secure](http://127.0.0.1:8080/secure) route

## How to run it?

As always, check out the git repo:

```git clone git@github.com:don41382/zio-http4s-doobie-auth-example.git```

Compose the docker instance, to have access to a postgres instance on port 54320

`docker-compose up -d`

And in the end, run sbt with the sbt-resolver plugin command

`sbt "~reStart"`

Done! Now you can play around. Change things and make your own picture of the awesomeness of zio.

Login to the [secure](http://127.0.0.1:8080/secure) area

1. Username: "Felix", password: "password"
2. Username: "Klaus", password: "123456"


## Issue

This project also helped me to understand, how to catch defect in http4s.

For more details, checkout the [issue](https://github.com/zio/zio/issues/1082) on this.


## Thanks

Just a quick thanks for the support to [Wiem](https://github.com/wi101) and [Kai](https://github.com/kaishh) for helping me to understand the error handling in ZIO/http4s.




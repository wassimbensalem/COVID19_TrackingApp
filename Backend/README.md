# Backend of Corona App from Internet Praktikum SS2020

## Overview

The backend consists of a nodejs application based on [Express](https://expressjs.com) and a [mongodb](https://www.mongodb.com/) database.
It offers a REST API for the Android Application.

## How to run backend

- Copy `docker/docker.env` to the root as `.env`
- run `docker-compose up`

This should start a mongodb server and our nodejs application.

## First admin user

- Register user account in App
- Connect to database `sudo docker exec -it backend_mongodb_1 mongo -u root -p secret corona`
- Make account admin `db.users.updateOne({"name":"###USERNAME###"},{$set: {"isAdmin": true}});`

## Risk calculation

The user risk currently doesn't update automatically. The online server has a cronjob, on development install you must call the endpoint yourself.
eg. by executing `curl -X POST http://127.0.0.1:80/gps/recalculateRisk` 
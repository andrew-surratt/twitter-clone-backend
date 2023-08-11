# Twitter Clone

## Prerequisites
Install Postgres
`brew install postgresql`

## Configuration

- Make a copy of [application.yml.example](application.yml.example) named `application.yml`
- Make a copy of [application-development.yml.example](application-development.yml.example) named `application-development.yml`, and update values for `<INSERT-PASSWORD>` to your desired local demo users passwords
- Make a copy of [application-test.yml.example](application-test.yml.example) named `application-test.yml` for integration test configuration

## Install Dependencies
`mvn clean install`

## Start

Start Postgres
`brew services start postgresql`

Start App
`mvn spring-boot:run`

## Test
`mvn clean test`

## Stop
Stop Postgres
`brew services stop postgresql`

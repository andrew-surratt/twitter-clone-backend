# Twitter Clone

## Prerequisites
Install Postgres
`brew install postgresql`

## Configuration
Create `application.yml` at the top-level with the following:
```yaml
spring:
  profiles:
    active: development
    group:
      development: development
```

## Install Dependencies
`mvn clean install`

## Start

Start Postgres
`brew services start postgresql`

Start App
`mvn spring-boot:run`

## Stop
Stop Postgres
`brew services stop postgresql`

# play2.6-swagger-issue

This projects is a reproducible example for issue [#185](https://github.com/swagger-api/swagger-play/issues/185) of [swagger-api/swagger-play](https://github.com/swagger-api/swagger-play) with Play 2.6. It builds upon the [playframework/play-scala-rest-api-example](https://github.com/playframework/play-scala-rest-api-example) (2.6.x branch) projects, and adds the following setup configuration (also described in the issue):

* Dependency in `build.sbt`: `"io.swagger" %% "swagger-play2" % "1.6.0"`
* Swagger Module enabled in `application.conf`: `play.modules.enabled += "play.modules.swagger.SwaggerModule"`
* `swagger-ui-dist` resources placed under `/public/swagger/`
* Swagger routes added to `conf/routes`:

```
GET   /swagger.json     controllers.ApiHelpController.getResources
GET   /docs/            controllers.Assets.at(path="/public/swagger",file="index.html")
GET   /docs/*file       controllers.Assets.at(path="/public/swagger",file)
```

Run this project to experience the error:

    sbt clean compile runProd
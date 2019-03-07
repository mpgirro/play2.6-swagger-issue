package v1.post

import io.swagger.annotations.{Api, ApiParam, ApiResponse, ApiResponses}
import javax.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class PostFormInput(title: String, body: String)

/**
  * Takes HTTP requests and produces JSON.
  */
@Api
class PostController @Inject()(cc: PostControllerComponents)(implicit ec: ExecutionContext)
    extends PostBaseController(cc) {

  private val logger = Logger(getClass)

  private val form: Form[PostFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "title" -> nonEmptyText,
        "body" -> text
      )(PostFormInput.apply)(PostFormInput.unapply)
    )
  }

  def index: Action[AnyContent] = PostAction.async { implicit request =>
    logger.trace("index: ")
    postResourceHandler.find.map { posts =>
      Ok(Json.toJson(posts))
    }
  }

  def process: Action[AnyContent] = PostAction.async { implicit request =>
    logger.trace("process: ")
    processJsonPost()
  }

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID supplied"),
    new ApiResponse(code = 404, message = "Post not found")))
  def show(
      @ApiParam(value = "ID of the Post to fetch") id: String): Action[AnyContent] =
    PostAction.async { implicit request =>
      logger.trace(s"show: id = $id")
      postResourceHandler.lookup(id).map { post =>
        Ok(Json.toJson(post))
    }
  }

  private def processJsonPost[A]()(implicit request: PostRequest[A]): Future[Result] = {
    def failure(badForm: Form[PostFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: PostFormInput) = {
      postResourceHandler.create(input).map { post =>
        Created(Json.toJson(post)).withHeaders(LOCATION -> post.link)
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}

package scenery.simple

import play.api.libs.json.{JsValue, Json}

/** A simple object in a scene to be read from JSON
  * Created by mader on 7/12/16.
  */
case class SceneObject(shape: String, texture: String,
                       x_pos: Float, y_pos: Float, z_pos: Float,
                       x_dim: Float, y_dim: Float, z_dim: Float
                      )

object SceneObject {

  object implicits {

    import play.api.libs.json._

    implicit val outFormat = Json.format[SceneObject]
  }

  def import_scene(fileobj: String) = {
    val in_file: JsValue = Json.parse(scala.io.Source.fromFile(fileobj).getLines().mkString("\n"))
    import implicits._
    Json.fromJson[Array[SceneObject]](in_file)
  }


}


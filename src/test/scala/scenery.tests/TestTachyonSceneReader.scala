package scenery.tests

import cleargl.GLVector
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.json._
import scenery._
import scenery.simple.providers._
import scenery.simple.{SceneObject, SimpleScene}

class TestTachyonSceneReader extends FunSuite with Matchers {

  val nn_demo_file = getClass().getResource("/scenery/tests/scenes/nn_demo.json")
  import SceneObject.implicits._

  test("Finding JSON resource") {
    nn_demo_file should not be null
    println(nn_demo_file.getFile)
  }

  test("Parse some simple JSON") {

    val in_text = Json.parse(
      """[{"shape": "box", "z_pos": 0.0, "x_dim": 0.25, "x_pos": -6.0, "y_pos": -6.0, "y_dim":
        | 0.25, "z_dim": 0.25, "texture": ""}, {"shape": "box", "z_pos": 0.0, "x_dim": 0.25, "x_pos": -6.0, "y_pos": -4.9090909090909083, "y_dim": 0.25, "z_dim": 0.25, "texture": ""}]""".stripMargin)

    val form_list = Json.fromJson[Array[SceneObject]](in_text)

    println(form_list)

    form_list match {
      case JsSuccess(cValue,_) => println(cValue.mkString(","))
      case JsError(errors) => fail(s"List was not parsed correctly ${errors.mkString(",")}")
    }

  }
  test("Reading the JSON file") {



    val form_list: JsResult[Array[SceneObject]] = SceneObject.import_scene(nn_demo_file.getFile)


    println(form_list)
    form_list match {
      case JsSuccess(cValue,_) => println(cValue.mkString(","))
      case JsError(errors) => fail(s"List was not parsed correctly ${errors.mkString(",")}")
    }

  }

  if (true) {
    test("Simple Figure") {

      val viewer = new SimpleScene("scenery - Neural Network Demo", 800, 600) with EmptyAction with
        StandardMaterials with JSONSceneProvider {
        /**
          * Load the scene as a list
          *
          * @return
          */
        override def import_scene(): JsResult[Array[SceneObject]] = SceneObject.import_scene(nn_demo_file.getFile)
      }
      viewer.main
    }
  }

}

object TestSceneThings {


  trait SingleBoxScene extends SceneProvider with SmartMaterialProvider {
    /**
      * Loads the scene
      *
      * @param cam
      */
    override def loadProvidedScene(scene: Scene, cam: Camera): Scene = {
      {
        val box: Box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f))
        box.setMaterial(getTexture("bw_173"))
        box.setPosition(new GLVector(-6.0f, -19.2f, 0.0f))
        scene.addChild(box)
      }
      {
        val box: Box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f))
        box.setMaterial(getTexture("bw_173"))
        box.setPosition(new GLVector(-6.0f, -18.4f, 0.0f))
        scene.addChild(box)
      }
      scene
    }
  }

  trait JSONBasedScene extends SceneProvider {
    import play.api.libs.json._



    /**
      * Loads the scene
      *
      * @param cam
      */
    override def loadProvidedScene(scene: Scene, cam: Camera) = {
      val nn_demo_file = classOf[JSONBasedScene].getResource("scenes/nn_demo.json").getFile
      val in_file = Json.parse(scala.io.Source.fromFile(nn_demo_file).getLines().mkString("\n"))
      scene
    }
  }




}
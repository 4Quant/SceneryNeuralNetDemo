package scenery.tests

import cleargl.GLVector
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.json.Json
import scenery._
import scenery.simple.SimpleScene
import scenery.simple.providers.{SmartMaterialProvider, EmptyAction, SceneProvider, StandardMaterials}
import scenery.tests.TestSceneThings.SingleBoxScene

class TestTachyonSceneReader extends FunSuite with Matchers {
  val nn_demo_file = getClass().getResource("scenes/nn_demo.json")
  test("Finding JSON resource") {
    nn_demo_file should not be null
    println(nn_demo_file.getFile)
  }
  test("Reading the JSON file") {


    val in_file = Json.parse(scala.io.Source.fromFile(nn_demo_file.getFile).getLines().mkString("\n"))
    println(in_file)
  }
  test("Simple Figure") {

    val viewer = new SimpleScene("scenery - Neural Network Demo", 800, 600) with SingleBoxScene with EmptyAction with
    StandardMaterials
    viewer.main
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
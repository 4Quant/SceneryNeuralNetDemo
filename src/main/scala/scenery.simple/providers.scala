
package scenery.simple

import scala.collection.JavaConverters._
import scenery._

object providers {

  import cleargl.GLVector

  import scala.collection.mutable

  trait MaterialProvider {
    /**
      * Provide a map of materials
      *
      * @return map of materials
      */
    def getMaterialList(): java.util.Map[String,Material]
    def getDefaultMaterial(): Material
  }

  trait SmartMaterialProvider extends MaterialProvider {
    val all_mats = getMaterialList()
    val def_mat = getDefaultMaterial()
    def getTexture(name: String) = {
      if(!all_mats.containsKey(name)) println(s"Missing material: $name")
      all_mats.getOrDefault(name,def_mat)
    }
  }

  trait SceneProvider extends MaterialProvider {
    /**
      * Loads the scene
      *
      * @param cam
      */
    def loadProvidedScene(scene: Scene, cam: Camera): Scene
  }

  trait ActionProvider {
    /**
      * Code ot run in a parallel thread to make the scene active
      *
      * @param scene
      * @param cam
      * @return
      */
    def doSomething(scene: Scene, cam: Camera): Scene
  }

  trait EmptyAction extends ActionProvider {
    /**
      * Code ot run in a parallel thread to make the scene active
      *
      * @param scene
      * @param cam
      * @return
      */
    override def doSomething(scene: Scene, cam: Camera): Scene = scene
  }


  trait StandardMaterials extends SmartMaterialProvider {

    protected def newMat(opacity: Double, specular: Double, color_r: Double, color_g: Double, color_b: Double): Material =
      newMat(opacity.toFloat, specular.toFloat, color_r.toFloat, color_g.toFloat, color_b.toFloat)


    protected def newMat(opacity: Float, specular: Float, color_r: Float, color_g: Float, color_b: Float): Material = {
      val tMat: Material = new Material
      tMat.setAmbient(new GLVector(1.0f, 0.0f, 0.0f))
      tMat.setDiffuse(new GLVector(color_r, color_g, color_b))
      tMat.setSpecular(new GLVector(specular, specular, specular))
      tMat.setOpacity(opacity)
      tMat
    }

    def getDefaultMaterial(): Material = newMat(1.0f, 0.5f, 0.5f, 0.5f, 0.5f)

    override def getMaterialList() = {

      val outMap = new mutable.HashMap[String, Material]()

      outMap.put("convolution2d", newMat(1f, .3f, .1f, .1f, 1f))

      outMap.put("activation", newMat(1, .3, 0, 0.8, 0.8))
      outMap.put("maxpooling2d", newMat(1, 0.3, 1, 0.8, 1))
      outMap.put("dropout", newMat(0.75, 1, 1, 0, 0))
      outMap.put("upsampling2d", newMat(1, 0, 0, 1, 1))

      for (i <- 0 to 256) {
        outMap.put("bw_%03d".format(i), newMat(0.1f + 0.9f * i / 255.0,.3f + 0.25f * i / 255.0,
          i / 255f, i / 255f, i / 255f))
      }

      for (i <- 0 to 11) {
        outMap.put("r_%02d".format(i), newMat(0.1f + 0.9f * i / 255.0,.3f + 0.25f * i / 255.0, i /
          10f, 0, 0))
        outMap.put("g_%02d".format(i), newMat(0.1f + 0.9f * i / 255.0,.3f + 0.25f * i / 255.0, 0, i / 10f, 0))
        outMap.put("b_%02d".format(i), newMat(0.1f + 0.9f * i / 255.0,.3f + 0.25f * i / 255.0, 0, 0, i / 10f))
        outMap.put("k_%02d".format(i), newMat(0.1f + 0.9f * i / 255.0,.3f + 0.25f * i / 255.0, i / 40f, i /
          40f, i / 40f))
      }


      outMap
    }.asJava

  }

  trait CameraRotationAction extends ActionProvider {
    override def doSomething(scene: Scene, cam: Camera) = {
      while (true) {
        cam.getRotation.rotateByAngleY(0.01f)
        cam.setNeedsUpdate(true)
        try {
          Thread.sleep(20)
        }
        catch {
          case e: InterruptedException => {
            e.printStackTrace
          }
        }
      }
      scene
    }
  }

}
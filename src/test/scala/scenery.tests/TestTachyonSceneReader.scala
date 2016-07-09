
import cleargl.{GLMatrix, GLVector}
import com.jogamp.opengl.GLAutoDrawable
import org.scalatest.{FunSuite, Matchers}
import scenery.rendermodules.opengl.DeferredLightingRenderer
import scenery._

class TestTachyonSceneReader extends FunSuite with Matchers {
  test("Simple Figure") {
    val viewer = new SimpleScene.NNDemoApp("scenery - Neural Network Demo", 800, 600)
    viewer.main
  }

}

object SimpleScene {

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

  val defaultMaterial: Material = newMat(1.0f, 0.5f, 0.5f, 0.5f, 0.5f)

  protected def makeTextures = {
    val outMap: java.util.HashMap[String, Material] = new java.util.HashMap[String, Material](300)

    outMap.put("convolution2d", newMat(1f, .3f, .1f, .1f, 1f))

    outMap.put("activation", newMat(1, .3, 0, 0.8, 0.8))
    outMap.put("maxpooling2d", newMat(1, 0.3, 1, 0.8, 1))
    outMap.put("dropout", newMat(0.75, 1, 1, 0, 0))
    outMap.put("upsampling2d", newMat(1, 0, 0, 1, 1))

    for(i <- 0 to 256) {
      outMap.put("bw_%03d".format(i), newMat(0.1f + 0.9f * i / 255.0,.3f + 0.25f * i / 255.0,
      i / 255f, i / 255f, i / 255f))
    }

    for(i <- 0 to 11) {
          outMap.put("r_%02d".format(i), newMat(0.1f + 0.9f * i / 255.0,.3f + 0.25f * i / 255.0, i /
            10f, 0, 0))
          outMap.put("g_%02d".format(i), newMat(0.1f + 0.9f * i / 255.0,.3f + 0.25f * i / 255.0, 0, i / 10f, 0))
          outMap.put("b_%02d".format(i), newMat(0.1f + 0.9f * i / 255.0,.3f + 0.25f * i / 255.0, 0, 0, i / 10f))
          outMap.put("k_%02d".format(i), newMat(0.1f + 0.9f * i / 255.0,.3f + 0.25f * i / 255.0, i / 40f, i /
            40f, i / 40f))
        }


    outMap
  }

  private def getTexture(tName: String, baseMap: java.util.Map[String, Material]): Material = {
    if (baseMap.containsKey(tName)) {
      return baseMap.get(tName).asInstanceOf[Material]
    }
    else {
      System.out.println("Texture Missing:" + tName)
      return defaultMaterial
    }
  }

  class NNDemoApp(applicationName: String, windowWidth: Int, windowHeight: Int) extends
    SceneryDefaultApplication(applicationName, windowWidth, windowHeight) {

    override def init(pDrawable: GLAutoDrawable) {
      setDeferredRenderer(new DeferredLightingRenderer(pDrawable.getGL.getGL4, getGlWindow.getWidth, getGlWindow.getHeight))
      getHub.add(SceneryElement.RENDERER, getDeferredRenderer)

      val cam: Camera = new DetachedHeadCamera
      cam.setPosition(new GLVector(0.0f, 0.0f, -5.0f))
      cam.setView(new GLMatrix().setCamera(cam.getPosition, cam.getPosition.plus(cam.getForward), cam.getUp))
      cam.setProjection(new GLMatrix().setPerspectiveProjectionMatrix((70.0f / 180.0f * java.lang.Math.PI).toFloat, 1024f / 1024f, 0.1f, 1000.0f))
      cam.setActive(true)
      getScene.addChild(cam)
      makeUglyScene(cam, makeTextures)
      val rotator: Thread = new Thread() {
        override def run {
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
        }
      }
      getDeferredRenderer.initializeScene(getScene)
    }

    protected def makeUglyScene(cam: Camera, matList: java.util.Map[String, Material]) {
    }
  }
}
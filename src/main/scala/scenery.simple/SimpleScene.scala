package scenery.simple

import cleargl.{GLMatrix, GLVector}
import com.jogamp.opengl.GLAutoDrawable
import scenery.rendermodules.opengl.DeferredLightingRenderer
import scenery.simple.providers.{ActionProvider, SceneProvider}
import scenery.{Camera, DetachedHeadCamera, SceneryDefaultApplication, SceneryElement}

/**
  * Create a Simple Scene with a given Scene, Material, and Action Provider
  * @param applicationName name of the window
  * @param windowWidth
  * @param windowHeight
  */
abstract class SimpleScene(applicationName: String, windowWidth: Int, windowHeight: Int) extends
  SceneryDefaultApplication(applicationName, windowWidth, windowHeight) with SceneProvider with ActionProvider {

  override def init(pDrawable: GLAutoDrawable) {
    setDeferredRenderer(new DeferredLightingRenderer(pDrawable.getGL.getGL4, getGlWindow.getWidth, getGlWindow.getHeight))
    getHub.add(SceneryElement.RENDERER, getDeferredRenderer)

    val cam: Camera = new DetachedHeadCamera
    cam.setPosition(new GLVector(0.0f, 0.0f, -5.0f))
    cam.setView(new GLMatrix().setCamera(cam.getPosition, cam.getPosition.plus(cam.getForward), cam.getUp))
    cam.setProjection(new GLMatrix().setPerspectiveProjectionMatrix((70.0f / 180.0f * java.lang.Math.PI).toFloat, 1024f / 1024f, 0.1f, 1000.0f))
    cam.setActive(true)
    getScene.addChild(cam)

    loadProvidedScene(getScene(), cam)

    val rotator: Thread = new Thread() {
      val cAction = () -> doSomething(getScene(), cam)
      override def run {
        cAction
      }
    }
    rotator.start()
    getDeferredRenderer.initializeScene(getScene)
  }








}
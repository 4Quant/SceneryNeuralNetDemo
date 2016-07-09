package scenery.tests.examples;

import org.junit.Test;
import cleargl.GLMatrix;
import cleargl.GLVector;
import com.jogamp.opengl.GLAutoDrawable;
import org.junit.Test;
import scenery.*;
import scenery.rendermodules.opengl.DeferredLightingRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mader on 7/9/16.
 */
public class TestTachyonSceneExample {
    @Test
    public void testExample() throws Exception {
        NNDemoApp viewer = new NNDemoApp( "scenery - Neural Network Demo", 800,
                600 );
        viewer.main();
    }
    static protected Material newMat(double opacity,double specular,double color_r, double color_g, double color_b) {
        return newMat((float) opacity, (float) specular, (float) color_r, (float) color_g, (float) color_b);
    }
    static protected Material newMat(float opacity,float specular,float color_r, float color_g, float color_b) {
        Material tMat = new Material();
        tMat.setAmbient( new GLVector(1.0f, 0.0f, 0.0f) );
        tMat.setDiffuse( new GLVector(color_r, color_g, color_b) );
        tMat.setSpecular( new GLVector(specular, specular, specular) );
        tMat.setOpacity(opacity);
        return tMat;
    }

    static final Material defaultMaterial = newMat(1.0f,0.5f,0.5f,0.5f,0.5f);

    static protected Map<String,Material> makeTextures() {
        HashMap<String,Material> outMap = new HashMap<String,Material>(300);
        outMap.put("convolution2d", newMat(1f,.3f,.1f,.1f,1f));
        outMap.put("activation", newMat(1, .3,0,0.8,0.8));
        outMap.put("maxpooling2d", newMat(1, 0.3,1,0.8,1));
        outMap.put("dropout", newMat(0.75, 1,1,0,0));
        outMap.put("upsampling2d", newMat(1, 0,0,1,1));
        for(int i=0;i<256;i++) {
            outMap.put(String.format("bw_%03d",i), newMat(0.1f+0.9f*i/255.f,.3f+0.25f*i/255.f,i/255f,i/255f,i/255f));
        }

        for(int i=0;i<11;i++) {
            outMap.put(String.format("r_%02d",i), newMat(0.1f+0.9f*i/255.f,.3f+0.25f*i/255.f,i/10f,0.1f,0.1f));
            outMap.put(String.format("g_%02d",i), newMat(0.1f+0.9f*i/255.f,.3f+0.25f*i/255.f,0.1f,i/10f,0.1f));
            outMap.put(String.format("b_%02d",i), newMat(0.1f+0.9f*i/255.f,.3f+0.25f*i/255.f,0.1f,0.1f,i/10f));
            outMap.put(String.format("k_%02d",i), newMat(0.1f+0.9f*i/255.f,.3f+0.25f*i/255.f,i/40f,i/40f,i/40f));
        }
        return outMap;
        /**
         T.texture('convolution2d', opacity=1, specular=.3, color = (.1,.1,1))
         T.texture('activation', opacity=1, specular=.3, color=(0,0.8,0.8))
         T.texture('maxpooling2d', opacity=1, specular=1, color=(1,.8,1), diffuse=0.2)
         T.texture('dropout', opacity=0.75, specular=1, diffuse=0.2, color = (1,0,0))
         T.texture('merge', opacity=0.75, specular=1, diffuse=0.2, color = (0.25,0.5,0.5))
         T.texture('upsampling2d', opacity=1, specular=0, diffuse=1, color=(0,1,1))
         T.texture('mirror', ambient=0.05, diffuse=0.05, specular=.9, opacity=0.9, color=(.8,.8,.8))
         T.texture('input', ambient=0.05, diffuse=0.05, specular=.9, opacity=0.9, color=(.8,.8,.8))
         T.texture('ref_rod', opacity=1, specular=1, color=(1,.8,1), diffuse=0.2)
         T.texture('floor', opacity=1, specular=0.25, color=(1,.8,1), diffuse=0.5)
         # we can afford 255 grayscale values

         for i in range(11):
         T.texture('k_%02d' % i, opacity=1, specular=.3, color = (i/40.,i/40.,i/40.))
         T.texture('r_%02d' % i, opacity=1, specular=0*i/10., color = (i/10.,0,0))
         T.texture('g_%02d' % i, opacity=1, specular=0*i/10., color = (0,i/10.,0))
         T.texture('b_%02d' % i, opacity=1, specular=0*i/10., color = (0,0,i/10.))
         return T
         */


    }
    static private Material getTexture(String tName, Map<String,Material> baseMap) {
        if(baseMap.containsKey(tName)) {
            return (Material) baseMap.get(tName);
        } else {
            System.out.println("Texture Missing:"+tName);   
            return defaultMaterial;
        }
    }


    private class NNDemoApp extends SceneryDefaultApplication {
        public NNDemoApp(String applicationName, int windowWidth, int windowHeight) {
            super(applicationName, windowWidth, windowHeight);
        }




        public void init(GLAutoDrawable pDrawable) {

            setDeferredRenderer( new DeferredLightingRenderer( pDrawable.getGL().getGL4(), getGlWindow().getWidth(), getGlWindow().getHeight() ) );
            getHub().add(SceneryElement.RENDERER, getDeferredRenderer());



            PointLight[] lights = new PointLight[2];

            for( int i = 0; i < lights.length; i++ ) {
                lights[i] = new PointLight();
                lights[i].setPosition( new GLVector(2.0f * i, 2.0f * i, 2.0f * i) );
                lights[i].setEmissionColor( new GLVector(1.0f, 0.0f, 1.0f) );
                lights[i].setIntensity( 0.2f*(i+1) );
                getScene().addChild( lights[i] );
            }

            final Camera cam = new DetachedHeadCamera();
            cam.setPosition( new GLVector(0.0f, 0.0f, -5.0f) );
            cam.setView( new GLMatrix().setCamera(cam.getPosition(), cam.getPosition().plus(cam.getForward()), cam.getUp()) );
            cam.setProjection( new GLMatrix().setPerspectiveProjectionMatrix( (float) (70.0f / 180.0f * java.lang.Math.PI), 1024f / 1024f, 0.1f, 1000.0f) );
            cam.setActive( true );
            getScene().addChild(cam);

            makeUglyScene(cam, makeTextures());

            Thread rotator = new Thread(){
                public void run() {
                    while (true) {
                        cam.getRotation().rotateByAngleY(0.01f);
                        cam.setNeedsUpdate(true);

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            //rotator.start();

            getDeferredRenderer().initializeScene(getScene());
        }

        protected void makeUglyScene(final Camera cam, Map<String,Material> matList) {
            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-6.0f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-5.2f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_172",matList) );
                box.setPosition( new GLVector(-5.2f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(-5.2f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(-5.2f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-5.2f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(-5.2f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-5.2f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(-5.2f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-5.2f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-5.2f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_172",matList) );
                box.setPosition( new GLVector(-5.2f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_172",matList) );
                box.setPosition( new GLVector(-5.2f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(-5.2f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_172",matList) );
                box.setPosition( new GLVector(-5.2f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_172",matList) );
                box.setPosition( new GLVector(-5.2f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-4.4f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-4.4f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-4.4f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-4.4f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(-4.4f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_142",matList) );
                box.setPosition( new GLVector(-4.4f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_141",matList) );
                box.setPosition( new GLVector(-4.4f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_149",matList) );
                box.setPosition( new GLVector(-4.4f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-4.4f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-4.4f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(-4.4f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-4.4f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-4.4f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(-4.4f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(-4.4f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-3.6f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(-3.6f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-3.6f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-3.6f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_154",matList) );
                box.setPosition( new GLVector(-3.6f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_157",matList) );
                box.setPosition( new GLVector(-3.6f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_112",matList) );
                box.setPosition( new GLVector(-3.6f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_158",matList) );
                box.setPosition( new GLVector(-3.6f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_153",matList) );
                box.setPosition( new GLVector(-3.6f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_129",matList) );
                box.setPosition( new GLVector(-3.6f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_159",matList) );
                box.setPosition( new GLVector(-3.6f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-3.6f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-3.6f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-3.6f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-3.6f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_172",matList) );
                box.setPosition( new GLVector(-3.6f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-2.8f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(-2.8f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.8f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_165",matList) );
                box.setPosition( new GLVector(-2.8f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_164",matList) );
                box.setPosition( new GLVector(-2.8f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.8f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.8f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.8f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.8f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.8f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_001",matList) );
                box.setPosition( new GLVector(-2.8f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-2.8f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(-2.8f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(-2.8f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(-2.8f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(-2.8f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-2.0f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(-2.0f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.0f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_161",matList) );
                box.setPosition( new GLVector(-2.0f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_131",matList) );
                box.setPosition( new GLVector(-2.0f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.0f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.0f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.0f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_016",matList) );
                box.setPosition( new GLVector(-2.0f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.0f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.0f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_164",matList) );
                box.setPosition( new GLVector(-2.0f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_164",matList) );
                box.setPosition( new GLVector(-2.0f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.0f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(-2.0f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(-2.0f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-1.2f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-1.2f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-1.2f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_156",matList) );
                box.setPosition( new GLVector(-1.2f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_146",matList) );
                box.setPosition( new GLVector(-1.2f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_154",matList) );
                box.setPosition( new GLVector(-1.2f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-1.2f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-1.2f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-1.2f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_062",matList) );
                box.setPosition( new GLVector(-1.2f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_159",matList) );
                box.setPosition( new GLVector(-1.2f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_162",matList) );
                box.setPosition( new GLVector(-1.2f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_165",matList) );
                box.setPosition( new GLVector(-1.2f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-1.2f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-1.2f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(-1.2f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(-0.4f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-0.4f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-0.4f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_152",matList) );
                box.setPosition( new GLVector(-0.4f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-0.4f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-0.4f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_009",matList) );
                box.setPosition( new GLVector(-0.4f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_105",matList) );
                box.setPosition( new GLVector(-0.4f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_146",matList) );
                box.setPosition( new GLVector(-0.4f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_153",matList) );
                box.setPosition( new GLVector(-0.4f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_157",matList) );
                box.setPosition( new GLVector(-0.4f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_158",matList) );
                box.setPosition( new GLVector(-0.4f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-0.4f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(-0.4f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(-0.4f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(-0.4f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(0.4f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(0.4f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(0.4f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_160",matList) );
                box.setPosition( new GLVector(0.4f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(0.4f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(0.4f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_051",matList) );
                box.setPosition( new GLVector(0.4f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_121",matList) );
                box.setPosition( new GLVector(0.4f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_099",matList) );
                box.setPosition( new GLVector(0.4f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_020",matList) );
                box.setPosition( new GLVector(0.4f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(0.4f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_157",matList) );
                box.setPosition( new GLVector(0.4f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(0.4f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(0.4f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(0.4f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(0.4f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(1.2f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(1.2f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_163",matList) );
                box.setPosition( new GLVector(1.2f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_156",matList) );
                box.setPosition( new GLVector(1.2f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(1.2f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_018",matList) );
                box.setPosition( new GLVector(1.2f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_158",matList) );
                box.setPosition( new GLVector(1.2f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_112",matList) );
                box.setPosition( new GLVector(1.2f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_132",matList) );
                box.setPosition( new GLVector(1.2f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_134",matList) );
                box.setPosition( new GLVector(1.2f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_164",matList) );
                box.setPosition( new GLVector(1.2f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(1.2f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(1.2f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(1.2f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(2.0f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(2.0f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.0f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_136",matList) );
                box.setPosition( new GLVector(2.0f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_162",matList) );
                box.setPosition( new GLVector(2.0f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_150",matList) );
                box.setPosition( new GLVector(2.0f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_093",matList) );
                box.setPosition( new GLVector(2.0f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_064",matList) );
                box.setPosition( new GLVector(2.0f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_157",matList) );
                box.setPosition( new GLVector(2.0f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_157",matList) );
                box.setPosition( new GLVector(2.0f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_164",matList) );
                box.setPosition( new GLVector(2.0f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(2.0f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(2.0f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(2.0f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(2.8f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(2.8f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(2.8f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(2.8f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_165",matList) );
                box.setPosition( new GLVector(2.8f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_079",matList) );
                box.setPosition( new GLVector(2.8f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_164",matList) );
                box.setPosition( new GLVector(2.8f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_096",matList) );
                box.setPosition( new GLVector(2.8f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_162",matList) );
                box.setPosition( new GLVector(2.8f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_095",matList) );
                box.setPosition( new GLVector(2.8f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_158",matList) );
                box.setPosition( new GLVector(2.8f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(2.8f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(2.8f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(2.8f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(2.8f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_172",matList) );
                box.setPosition( new GLVector(2.8f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(3.6f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(3.6f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(3.6f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(3.6f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(3.6f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_165",matList) );
                box.setPosition( new GLVector(3.6f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_149",matList) );
                box.setPosition( new GLVector(3.6f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_164",matList) );
                box.setPosition( new GLVector(3.6f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_159",matList) );
                box.setPosition( new GLVector(3.6f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_163",matList) );
                box.setPosition( new GLVector(3.6f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(3.6f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(3.6f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(3.6f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(3.6f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_172",matList) );
                box.setPosition( new GLVector(3.6f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(4.4f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(4.4f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(4.4f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(4.4f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(4.4f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(4.4f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_165",matList) );
                box.setPosition( new GLVector(4.4f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_169",matList) );
                box.setPosition( new GLVector(4.4f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(4.4f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(4.4f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_172",matList) );
                box.setPosition( new GLVector(4.4f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(4.4f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(5.2f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(5.2f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_172",matList) );
                box.setPosition( new GLVector(5.2f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(5.2f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(5.2f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(5.2f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(5.2f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(5.2f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(5.2f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(5.2f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(5.2f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(5.2f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_170",matList) );
                box.setPosition( new GLVector(5.2f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(5.2f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_171",matList) );
                box.setPosition( new GLVector(5.2f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(5.2f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -19.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -18.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -17.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -16.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -16.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -15.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -14.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -13.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -12.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -12.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -11.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -10.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -9.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -8.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -8.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_173",matList) );
                box.setPosition( new GLVector(6.0f, -7.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-6.0f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-5.2f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-4.4f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-3.6f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.8f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.8f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.8f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.8f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.8f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_165",matList) );
                box.setPosition( new GLVector(-2.8f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-2.8f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-2.8f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-2.8f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-2.8f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-2.8f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.8f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.8f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.8f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.8f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.8f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.0f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.0f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.0f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.0f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.0f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-2.0f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-2.0f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_164",matList) );
                box.setPosition( new GLVector(-2.0f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-2.0f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-2.0f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-2.0f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.0f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.0f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.0f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.0f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-2.0f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-1.2f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-1.2f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-1.2f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-1.2f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-1.2f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-1.2f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-1.2f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-1.2f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-1.2f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-1.2f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-1.2f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-1.2f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-1.2f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-1.2f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-1.2f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-1.2f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-0.4f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-0.4f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-0.4f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-0.4f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-0.4f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-0.4f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-0.4f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-0.4f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-0.4f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-0.4f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-0.4f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-0.4f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-0.4f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-0.4f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-0.4f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(-0.4f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(0.4f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(0.4f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(0.4f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(0.4f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(0.4f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(0.4f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(0.4f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(0.4f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(0.4f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(0.4f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(0.4f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(0.4f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(0.4f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(0.4f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(0.4f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(0.4f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(1.2f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(1.2f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(2.0f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.0f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.8f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.8f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.8f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.8f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.8f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(2.8f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.8f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.8f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.8f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.8f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.8f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.8f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.8f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.8f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.8f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(2.8f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(4.4f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(5.2f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, -6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, -5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, -4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, -3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, -2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, -2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, -1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, -0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, 0.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, 1.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, 2.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, 2.8f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, 3.6f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, 4.4f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, 5.2f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(6.0f, 6.0f, 0.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-6.0f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-5.2f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-4.4f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-4.4f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-4.4f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-4.4f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-4.4f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-4.4f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_146",matList) );
                box.setPosition( new GLVector(-4.4f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_145",matList) );
                box.setPosition( new GLVector(-4.4f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_153",matList) );
                box.setPosition( new GLVector(-4.4f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-4.4f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-4.4f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-4.4f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-4.4f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-4.4f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-4.4f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-4.4f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-3.6f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-3.6f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-3.6f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-3.6f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_158",matList) );
                box.setPosition( new GLVector(-3.6f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_161",matList) );
                box.setPosition( new GLVector(-3.6f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_115",matList) );
                box.setPosition( new GLVector(-3.6f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_162",matList) );
                box.setPosition( new GLVector(-3.6f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_157",matList) );
                box.setPosition( new GLVector(-3.6f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_133",matList) );
                box.setPosition( new GLVector(-3.6f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_163",matList) );
                box.setPosition( new GLVector(-3.6f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-3.6f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-3.6f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-3.6f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-3.6f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-3.6f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.8f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.8f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.8f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.8f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.8f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.8f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.8f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.8f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.8f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.8f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_004",matList) );
                box.setPosition( new GLVector(-2.8f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.8f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.8f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.8f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.8f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.8f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.0f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.0f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.0f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_165",matList) );
                box.setPosition( new GLVector(-2.0f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_135",matList) );
                box.setPosition( new GLVector(-2.0f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.0f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.0f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.0f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_019",matList) );
                box.setPosition( new GLVector(-2.0f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.0f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-2.0f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.0f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.0f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.0f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.0f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-2.0f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-1.2f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-1.2f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-1.2f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_160",matList) );
                box.setPosition( new GLVector(-1.2f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_150",matList) );
                box.setPosition( new GLVector(-1.2f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_158",matList) );
                box.setPosition( new GLVector(-1.2f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-1.2f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-1.2f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-1.2f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_065",matList) );
                box.setPosition( new GLVector(-1.2f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_163",matList) );
                box.setPosition( new GLVector(-1.2f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(-1.2f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-1.2f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-1.2f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-1.2f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-1.2f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-0.4f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-0.4f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-0.4f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_156",matList) );
                box.setPosition( new GLVector(-0.4f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-0.4f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(-0.4f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_012",matList) );
                box.setPosition( new GLVector(-0.4f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_108",matList) );
                box.setPosition( new GLVector(-0.4f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_150",matList) );
                box.setPosition( new GLVector(-0.4f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_157",matList) );
                box.setPosition( new GLVector(-0.4f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_161",matList) );
                box.setPosition( new GLVector(-0.4f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_162",matList) );
                box.setPosition( new GLVector(-0.4f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-0.4f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-0.4f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-0.4f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(-0.4f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(0.4f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(0.4f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(0.4f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_164",matList) );
                box.setPosition( new GLVector(0.4f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(0.4f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(0.4f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_054",matList) );
                box.setPosition( new GLVector(0.4f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_125",matList) );
                box.setPosition( new GLVector(0.4f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_103",matList) );
                box.setPosition( new GLVector(0.4f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_023",matList) );
                box.setPosition( new GLVector(0.4f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(0.4f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_161",matList) );
                box.setPosition( new GLVector(0.4f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(0.4f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(0.4f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(0.4f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(0.4f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(1.2f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(1.2f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(1.2f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(1.2f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_160",matList) );
                box.setPosition( new GLVector(1.2f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_000",matList) );
                box.setPosition( new GLVector(1.2f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_021",matList) );
                box.setPosition( new GLVector(1.2f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_162",matList) );
                box.setPosition( new GLVector(1.2f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_115",matList) );
                box.setPosition( new GLVector(1.2f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_136",matList) );
                box.setPosition( new GLVector(1.2f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_138",matList) );
                box.setPosition( new GLVector(1.2f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(1.2f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(1.2f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(1.2f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(1.2f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(1.2f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.0f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.0f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.0f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.0f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_140",matList) );
                box.setPosition( new GLVector(2.0f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(2.0f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_154",matList) );
                box.setPosition( new GLVector(2.0f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_096",matList) );
                box.setPosition( new GLVector(2.0f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_067",matList) );
                box.setPosition( new GLVector(2.0f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_161",matList) );
                box.setPosition( new GLVector(2.0f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_162",matList) );
                box.setPosition( new GLVector(2.0f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.0f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.0f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.0f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.0f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.0f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.8f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.8f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.8f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.8f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.8f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_082",matList) );
                box.setPosition( new GLVector(2.8f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.8f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_099",matList) );
                box.setPosition( new GLVector(2.8f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_166",matList) );
                box.setPosition( new GLVector(2.8f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_099",matList) );
                box.setPosition( new GLVector(2.8f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_162",matList) );
                box.setPosition( new GLVector(2.8f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.8f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.8f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.8f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.8f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(2.8f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(3.6f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(3.6f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(3.6f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(3.6f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(3.6f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(3.6f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(3.6f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_153",matList) );
                box.setPosition( new GLVector(3.6f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(3.6f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_163",matList) );
                box.setPosition( new GLVector(3.6f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_167",matList) );
                box.setPosition( new GLVector(3.6f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(3.6f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(3.6f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(3.6f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(3.6f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(3.6f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(4.4f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(5.2f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -19.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -18.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -17.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -16.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -16.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -15.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -14.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -13.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -12.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -12.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -11.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -10.4f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -9.6f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -8.8f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -8.0f, 10.0f) );
                getScene().addChild(box);
            }

            {
                final Box box = new Box(new GLVector(0.1875f, 0.1875f, 0.1875f) );
                box.setMaterial( getTexture("bw_168",matList) );
                box.setPosition( new GLVector(6.0f, -7.2f, 10.0f) );
                getScene().addChild(box);
            }

            {
                PointLight light = new PointLight();
                light.setPosition( new GLVector(-25.0f , 50.0f, -110.0f) );
                light.setEmissionColor( new GLVector(1.0f, 0.0f, 1.0f) );
                light.setIntensity( 1 );
                getScene().addChild( light );
            }
        }




    }
}

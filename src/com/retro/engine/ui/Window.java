package com.retro.engine.ui;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.retro.engine.Framework;
import com.retro.engine.RetroProcess;
import com.retro.engine.debug.Debug;
import com.retro.engine.debug.DebugMode;
import com.retro.engine.event.EventHandler;
import com.retro.engine.scene.SceneType;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jogamp.opengl.GL2;

/**
 * Created by Michael on 7/19/2016.
 */
public class Window extends JFrame {

    private int m_windowWidth;
    private int m_windowHeight;

    private static boolean m_windowResizable = false;

    private static float[] m_projectionMatrixDefault;

    private static void calculateProjectionMatrix(float fov, int width, int height, float zNear, float zFar){
        float aspect = (float)width / (float)height;


        m_projectionMatrixDefault = new float[]{
                fov/aspect, 0, 0, 0,
                0, fov, 0, 0,
                0, 0, ((zFar + zNear)/(zNear-zFar)), ((2*zFar*zNear)/(zNear-zFar)),
                0, 0, -1, 0
        };
    }

    public static float[] getProjectionMatrix(){
        return m_projectionMatrixDefault;
    }

    /******************************/
    private static GLU glu = new GLU();

    private static boolean initialized = false;

    public static GLU getGlu(){
        return glu;
    }
/*****************************************/
    /**
     * Create a display for a retroprocess
     * @param windowTitle Title to be given to the window.
     * @param w Width of the window
     * @param h Height of the window
     * @param g RetroProcess to base window off of
     */
    public Window(String windowTitle, int w, int h, RetroProcess g){
        super(windowTitle);

        g.setWindowHandle(this);

        Framework.initialize(g);

        this.setSize(w, h);
        this.setLocationRelativeTo(null);
        this.setResizable(m_windowResizable);

        if(Framework.usingOpengl())
        {
            GLProfile glProfile = GLProfile.getDefault();
            GLCapabilities glCapabilities = new GLCapabilities(glProfile);
            glCapabilities.setDoubleBuffered(true);
            final GLCanvas glCanvas = new GLCanvas(glCapabilities);
            glCanvas.addGLEventListener(new GLEventListener(){
                @Override
                public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height){

                    Debug.out("Should be initializing now.", DebugMode.DEBUG_NOTICE);

                    GL2 gl2 = glAutoDrawable.getGL().getGL2();
                    gl2.glViewport(0,0, width, height);
                    gl2.glMatrixMode( GL2.GL_PROJECTION );
                    gl2.glLoadIdentity();

                    /*/ coordinate system origin at lower left with width and height same as the window
                    GLU glu = new GLU();
                    glu.gluOrtho2D( 0.0f, width, 0.0f, height );*/
                    glu.gluPerspective(45.0f, (float)width/(float)height, 0.00001, 750.0);

                    gl2.glMatrixMode( GL2.GL_MODELVIEW );
                    gl2.glLoadIdentity();

                    //Window.calculateProjectionMatrix(45.0f, width, height, 0.00001f, 750.0f);
                    if(Framework.isNull()){
                    }else{
                        initialized = true;
                        Framework.getInstance().initializeGL(gl2);
                        Framework.getInstance().updateFrameDimensions();
                    }
                }
                @Override
                public void init(GLAutoDrawable glAutoDrawable){

                }
                @Override
                public void dispose(GLAutoDrawable glAutoDrawable){
                    Framework.getInstance().cleanUp(glAutoDrawable.getGL().getGL2());
                }
                @Override
                public void display(GLAutoDrawable glAutoDrawable){
                    if(initialized == false && Framework.isNull() == false){
                        initialized = true;
                        Framework.getInstance().initializeGL(glAutoDrawable.getGL().getGL2());
                        Framework.getInstance().updateFrameDimensions();
                    }

                    Framework.getInstance().render(glAutoDrawable.getGL().getGL2());

                    glAutoDrawable.getGL().getGL2().glFinish();
                }
            });

            EventHandler.initialize(glCanvas);
            Framework.setGlHandle(glCanvas);

            final JFrame frame = this;

            frame.add(glCanvas);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent windowEvent){
                    frame.remove(glCanvas);
                    frame.dispose();
                    System.exit(0);
                }
            });
        }else {
            this.setContentPane(Framework.getInstance());

            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        this.setVisible(true);
        this.requestFocus();
    }
}

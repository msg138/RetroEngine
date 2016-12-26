package com.retro.engine;

import com.jogamp.opengl.awt.GLCanvas;
import com.retro.engine.Messaging.RetroMessage;
import com.retro.engine.camera.Camera;
import com.retro.engine.component.ComponentType;
import com.retro.engine.debug.Debug;
import com.retro.engine.debug.DebugMode;
import com.retro.engine.defaultcomponent.*;
import com.retro.engine.defaultsystem.*;
import com.retro.engine.entity.EntityStorage;
import com.retro.engine.event.EventHandler;
import com.retro.engine.loading.LoadData;
import com.retro.engine.model.Matrix;
import com.retro.engine.model.shader.ShaderLoader;
import com.retro.engine.scene.SceneType;
import com.retro.engine.system.SystemManager;
import com.retro.engine.ui.*;
import com.retro.engine.ui.Canvas;
import com.retro.engine.util.collision.Quadtree;
import com.retro.engine.util.list.RetroList;
import com.retro.engine.util.time.UtilTime;

import com.jogamp.opengl.GL2;

import java.awt.*;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 7/19/2016.
 */
public class Framework extends Canvas {

    private static Framework m_instance;

    private int m_frameWidth;
    private int m_frameHeight;

    private static boolean m_usingOpengl = false;

    private static GLCanvas m_glHandle = null;
    private static Matrix m_viewMatrix = new Matrix();
    private static Matrix m_modelMatrix = new Matrix();

    private Camera m_camera;

    private int m_currentFPS = 0;
    private int m_currentRenderFPS = 0;

    private static final int c_threadSleep = 6;
    private static final int c_gcthreadSleep = 5000;

    private int m_gameWidth;
    private int m_gameHeight;

    private float m_wRatio;
    private float m_hRatio;

    private RetroProcess m_process;

    private SceneType m_scene;
    private SceneType m_nextScene;

    private Quadtree m_quadtree;

    private volatile EntityStorage m_eh;
    private volatile SystemManager m_sm;

    private long m_gameTime;
    private long m_lastTime;

    private List<IntBuffer> m_existingVBOs = new ArrayList<>();
    private List<IntBuffer> m_existingVAOs = new ArrayList<>();

    private Thread m_gameThread;
    private Thread m_renderThread;
    private Thread m_garbageCollectorManual;

    private boolean m_multithreaded = true;

    private Framework(RetroProcess process){
        super(m_usingOpengl);

        Debug.out(RetroEngine.getVersion() + "\n\t\t Initializing Engine...", DebugMode.DEBUG_NOTICE);

        Debug.out("Initializing entity storage (Main)", DebugMode.DEBUG_NOTICE);
        m_eh = new EntityStorage();
        Debug.out("Successfully initialized entity storage (Main)", DebugMode.DEBUG_NOTICE);
        Debug.out("Initializing system manager", DebugMode.DEBUG_NOTICE);
        m_sm = SystemManager.getInstance();
        Debug.out("Successfully initialized system manager", DebugMode.DEBUG_NOTICE);

        if(!usingOpengl()){
            EventHandler.initialize(this);
        }

        // TODO make quadtree and octree for collision detecion

        m_process = process;

        if(m_process != null)
        {
            m_gameWidth = m_process.getWidth();
            m_gameHeight = m_process.getHeight();

            m_camera = Camera.getInstance();

            m_quadtree = new Quadtree(0, new Rectangle(0, 0, m_gameWidth, m_gameHeight));

            if(m_gameWidth <= 0)
                m_gameWidth = 640;
            if(m_gameHeight <= 0)
                m_gameHeight = 480;

            m_frameWidth = 1;
            m_frameHeight = 1;

            m_wRatio = 1f;
            m_hRatio = 1f;

            m_scene = SceneType.SCENE_VISUALIZING;

            if(m_multithreaded){
                Debug.out("Starting game threads...", DebugMode.DEBUG_NOTICE);
                m_gameThread = new Thread(){
                    @Override
                    public void run(){
                        gameLoop();
                    }
                };
                m_renderThread = new Thread(){
                    @Override
                    public void run(){
                        long beginTime = 0;
                        long timeTaken = 0;
                        int rfps = 0;
                        while(!m_process.isDone())
                        {
                            beginTime = UtilTime.mill();
                            try {
                                if(m_scene != SceneType.SCENE_VISUALIZING) {
                                    if(usingOpengl() && m_glHandle != null)
                                        m_glHandle.display();
                                    else
                                        repaint();
                                }
                            }catch(Exception e){
                                Debug.out("Error was thrown: " + e.getMessage());
                                e.printStackTrace();
                            }
                            try{
                                Thread.sleep(c_threadSleep);
                            } catch(InterruptedException e){
                                e.printStackTrace();
                            }
                            timeTaken += UtilTime.mill() - beginTime;
                            rfps++;
                            if(timeTaken >= 1000){
                                m_currentRenderFPS = rfps;
                                rfps = 0;
                                timeTaken = 0;
                            }
                        }
                    }
                };
                /*m_garbageCollectorManual = new Thread(){
                    @Override
                    public void run(){
                        while(!m_process.isDone()){
                            System.gc();
                            try{
                                Thread.sleep(c_gcthreadSleep);
                            } catch(InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    }
                };
                m_garbageCollectorManual.start();*/
                m_gameThread.start();
                m_renderThread.start();
                Debug.out("Threads started.", DebugMode.DEBUG_NOTICE);
            }
        }

        /** Setup Components and System defaults */
        Debug.out("Setting up default systems and components...", DebugMode.DEBUG_NOTICE);
        new SystemRender();
        new SystemRenderRectangle();
        new SystemRenderImage();
        new SystemRenderText();
        new SystemCollision();
        ComponentType.initializeComponent(ComponentColor.class);
        ComponentType.initializeComponent(ComponentImage.class);
        ComponentType.initializeComponent(ComponentPosition.class);
        ComponentType.initializeComponent(ComponentRectangle.class);
        ComponentType.initializeComponent(ComponentRotation.class);
        ComponentType.initializeComponent(ComponentScale.class);
        ComponentType.initializeComponent(ComponentText.class);
        ComponentType.initializeComponent(ComponentVisible.class);
        ComponentType.initializeComponent(ComponentCollision.class);
        Debug.out("Successfully setup default systems and components.", DebugMode.DEBUG_NOTICE);

        m_instance = this;

        Debug.out("Initialization complete.", DebugMode.DEBUG_NOTICE);
    }

    public static Matrix getViewMatrix(){
        return m_viewMatrix;
    }
    public static Matrix getModelMatrix(){
        return m_modelMatrix;
    }

    public void addDeadVBO(IntBuffer b){
        synchronized (m_existingVBOs){
            m_existingVBOs.add(b);
        }
    }
    public void addDeadVAO(IntBuffer b){
        synchronized (m_existingVAOs){
            m_existingVAOs.add(b);
        }
    }

    public int getCurrentFPS(){
        return m_currentFPS;
    }
    public int getCurrentRenderFPS(){
        return m_currentRenderFPS;
    }

    public void initializeGL(GL2 gl){
        gl.glShadeModel(gl.GL_SMOOTH);
        gl.glClearColor(0.39f, 0.58f, 0.93f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(gl.GL_DEPTH_TEST);
        gl.glDepthFunc(gl.GL_LEQUAL);
        gl.glHint(gl.GL_PERSPECTIVE_CORRECTION_HINT, gl.GL_NICEST);
        gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(gl.GL_BLEND);

        ShaderLoader.loadDefaultShaders(gl);

        m_viewMatrix.setShaderOwner(ShaderLoader.getCurrentShader(gl));
        m_modelMatrix.setShaderOwner(ShaderLoader.getCurrentShader(gl));

        m_process.initializeGL(gl);

        changeScene(SceneType.SCENE_INITIALIZING);
    }

    public static void initialize(RetroProcess rp){
        if(m_instance == null)// Only allow initialization once.
        {
            new Framework(rp);
        }
    }

    public static boolean isNull(){
        return m_instance == null;
    }

    public static Framework getInstance(){
        if(m_instance == null)
            new Framework(null);
        return m_instance;
    }

    public void loadScene(SceneType nextScene){
        setNextScene(nextScene);
        changeScene(SceneType.SCENE_LOADING);
    }
    public void setNextScene(SceneType nextScene){
        m_nextScene = nextScene;
    }

    public Quadtree getQuadtree(){
        return m_quadtree;
    }

    public Camera getCamera(){ return m_camera; }

    public RetroProcess getProcess(){ synchronized (m_process){ return m_process; } }

    public static void useOpenGL(){
        m_usingOpengl = true;
    }

    public static void setGlHandle(GLCanvas glh){
        m_glHandle = glh;
    }

    public static GLCanvas getGlHandle(){ if(m_glHandle != null) synchronized (m_glHandle){ return m_glHandle; } else return null; }

    public static boolean usingOpengl(){
        return m_usingOpengl;
    }

    public float getWidthRatio(){
        return m_wRatio;
    }
    public float getHeightRatio(){
        return m_hRatio;
    }

    public int getGameWidth(){
        return m_gameWidth;
    }
    public int getGameHeight(){
        return m_gameHeight;
    }
    public int getFrameWidth(){
        return m_frameWidth;
    }
    public int getFrameHeight(){
        return m_frameHeight;
    }

    public void updateFrameDimensions(){
        m_frameWidth = (int)getGlHandle().getWidth();
        m_frameHeight = (int)getGlHandle().getHeight();

        m_wRatio = (float)m_frameWidth / (float)m_gameWidth;
        m_hRatio = (float)m_frameHeight / (float)m_gameHeight;
    }

    public EntityStorage getEntityStorage(){

        synchronized(m_eh){ return m_eh;}
    }

    public SystemManager getSystemManager(){
        synchronized(m_sm){ return m_sm;}
    }

    private boolean loadContent(LoadData ld, GL2 gl){
        if(ld == null)
            return true;// Obviously didn't want to load anything in the first place.
        ld.load(gl);
        return true;
    }

    private void handleResize(){
        m_frameHeight = this.getHeight();
        m_frameWidth = this.getWidth();

        m_wRatio = (float) m_frameWidth / m_gameWidth;
        m_hRatio = (float) m_frameHeight / m_gameHeight;
    }

    public void handleResizeGL(GL2 gl){
        ShaderLoader.defaultShaderImage2d.updateProjectionMatrixOrtho(gl, getFrameHeight(), getFrameHeight());
    }

    private void changeScene(SceneType sc){
        m_scene = sc;
    }

    private void gameLoop(){
        long beginTime, timeTaken = 0;
        int cfps =0;

        Debug.out("Running game loop...", DebugMode.DEBUG_NOTICE);
        while(!m_process.isDone()){
            beginTime = UtilTime.mill();
            m_camera.controlCamera();
            switch(m_scene){
                case SCENE_VISUALIZING:
                    /*
                    if((usingOpengl() || this.getWidth() > 1)){
                        handleResize();
                        Debug.out("Visualizing", DebugMode.DEBUG_NOTICE);
                    }*/
                    break;
                case SCENE_INITIALIZING:
                    Debug.out("Initiailizing", DebugMode.DEBUG_NOTICE);
                    try {
                        // Core initialization. Things that remain persistent throughout the lifespan of the app.
                        m_process.initialize();
                        // This should trigger loading
                        loadScene(SceneType.SCENE000);
                    }catch(Exception e){
                        Debug.out("Error was thrown: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case SCENE_LOADING:
                    try{
                        Thread.sleep(250);
                    }catch(Exception e){}
                    break;
                default:
                    m_gameTime += UtilTime.nanoTime() - m_lastTime;
                    try {
                        m_process.scene(m_scene);
                    }catch(Exception e){
                        Debug.out("Error was thrown: " + e.getMessage());
                        e.printStackTrace();
                    }
                    m_lastTime = UtilTime.nanoTime();
                    break;
            }
            if(m_scene != SceneType.SCENE_VISUALIZING)
                getSystemManager().handleMessage(new RetroMessage(getSystemManager().getSystem(ComponentType.getComponentType(ComponentCollision.class)).getID(), "RESETQUADTREE", "LOOP"));

            try {
                logic();
            }catch(Exception e){
                Debug.out("Error was thrown: " + e.getMessage());
                e.printStackTrace();
            }
            try{
                Thread.sleep(c_threadSleep);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
            timeTaken += UtilTime.mill() - beginTime;
            cfps ++;
            if(timeTaken >= 1000) {
                // timeLeft = 1000 - timeTaken;
                m_currentFPS = cfps;
                cfps = 0;
                timeTaken = 0;
            }
        }
    }

    public void render(Graphics2D g){
        for(int i=0;i<getEntityStorage().getEntityAmount();i++){
            getSystemManager().renderEntity(getEntityStorage().getEntity(i), g);
        }
    }
    public void render(GL2 g){
        if(m_scene == SceneType.SCENE_LOADING) {
            LoadData ld = m_process.load(m_nextScene);
            if (loadContent(ld, g)) {
                changeScene(m_nextScene);
                if(ld != null)
                    ld.lastMinuteLoads(g);
            }
        }
        // Get rid of existing VAOs and VBOs
        if(m_existingVAOs.size() > 0){
            synchronized (m_existingVAOs) {
                for (final IntBuffer i : m_existingVAOs) {
                    g.glDeleteVertexArrays(i.capacity(), i);
                    Thread tmp = new Thread(){
                        public void run(){
                            try{
                                Thread.sleep(2000);
                            }catch(Exception e){
                            }
                            for(int j=0;j<i.capacity();j++)
                                Debug.out("\t\t" + i.get(j));

                            Debug.out("Vertex buffer " + i.toString());
                        }
                    };
                    tmp.start();
                }Debug.out("Arrays deleted !!" + m_existingVAOs.size());
                m_existingVAOs.clear();
            }
        }
        if(m_existingVBOs.size() > 0){
            synchronized (m_existingVBOs) {
                for (final IntBuffer i : m_existingVBOs) {
                    g.glDeleteBuffers(i.capacity(), i);
                    Thread tmp = new Thread(){
                        public void run(){
                            try{
                                Thread.sleep(2000);
                            }catch(Exception e){

                            }
                            for(int j=0;j<i.capacity();j++)
                                Debug.out("\t\t" + i.get(j));

                            Debug.out("Vertex array, " + i.toString());
                        }
                    };
                    tmp.start();
                }Debug.out("Buffers deleted !!" + m_existingVBOs.size());
                m_existingVBOs.clear();
            }
        }

        g.glClear( g.GL_COLOR_BUFFER_BIT | g.GL_DEPTH_BUFFER_BIT);
        g.glLoadIdentity();

        ShaderLoader.getCurrentShader(g).activate(g);

        getModelMatrix().activateM(g);

        getViewMatrix().pushMatrix();
        if(m_camera.changedCamera())
            m_camera = Camera.getInstance();

        m_camera.updateCamera(g);
        getViewMatrix().activateV(g);

        for(int i=0;i<getEntityStorage().getEntityAmount();i++){
            if(i < getEntityStorage().getEntityAmount()) {
                if(getEntityStorage().getEntity(i) != null)
                    getSystemManager().renderEntity(getEntityStorage().getEntity(i), g);
            }
        }

        getViewMatrix().popMatrix();
    }
    public void logic(){
        for(int i=0;i<getEntityStorage().getEntityAmount();i++){
            if(i < getEntityStorage().getEntityAmount())
                if(getEntityStorage().getEntity(i) != null)
                    getSystemManager().processEntity(getEntityStorage().getEntity(i));
        }
    }

    public void cleanUp(GL2 g){
        m_process.finish();
        m_eh.clearEntities();

        if(m_existingVBOs.size() > 0){
            synchronized (m_existingVBOs) {
                for (IntBuffer i : m_existingVBOs)
                    g.glDeleteBuffers(i.capacity(), i);
                m_existingVBOs.clear();
                Debug.out("Buffers deleted !!");
            }
        }
        if(m_existingVAOs.size() > 0){
            synchronized (m_existingVAOs) {
                for (IntBuffer i : m_existingVAOs)
                    g.glDeleteVertexArrays(i.capacity(), i);
                m_existingVAOs.clear();
                Debug.out("Arrays deleted !!");
            }
        }
    }
}

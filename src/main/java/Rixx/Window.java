package Rixx;

import components.Fireball;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;
import physics2D.Physics2D;
import renderer.*;
import scenes.LevelEditorSceneInitializer;
import scenes.LevelSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;
import util.AssetPool;


import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {
    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;
    private FrameBuffer frameBuffer;
    private PickingTexture pickingTexture;
    private boolean runtimePlaying = false;
    private static String fileName;




    private static Window window = null;

    private long audioContext;
    private long audioDevice;


    private static Scene currentScene;


    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Rixx";
        fileName = "level.txt";
        EventSystem.addObserver(this);

    }

    public static void changeScene(SceneInitializer sceneInitializer, String newFile)
    {
        if(currentScene != null)
        {
         currentScene.destroy();

        }

        getImGuiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
        currentScene.load(newFile);
        fileName = newFile;
        currentScene.init();
        currentScene.start();
    }
    public static void changeScene(SceneInitializer sceneInitializer)
    {
        if(currentScene != null)
        {
            currentScene.destroy();

        }

        getImGuiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
        currentScene.load(fileName);
        currentScene.init();
        currentScene.start();
    }
    public static Window get() {
        if ( Window.window == null){
            Window.window = new Window();
        }
        return Window.window;
    }

    public static Physics2D getPhysics()
    {
        return currentScene.getPhysics2D();
    }

    public static Scene getScene()
    {
        return currentScene;
    }

    public void run (){
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        //Destroy audio context
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

        //Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //terminate glfw  and free error calback
        glfwTerminate();
        glfwSetErrorCallback(null ).free();

    }
    public void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLWF
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        //configure GLFW
       glfwDefaultWindowHints();
       glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
       // glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //create window
        glfwWindow = glfwCreateWindow(this.width,this.height,this.title,NULL,NULL);
        if(glfwWindow == NULL){
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback );
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback );
        glfwSetKeyCallback(glfwWindow,KeyListener::keyCallback );
        glfwSetWindowSizeCallback(glfwWindow,(w,newWidth , newHeight)->
        {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);

        });
        // make opengl context current
        glfwMakeContextCurrent(glfwWindow);
        //enable v-sync
        //TODO VSYNC/ peformance
        glfwSwapInterval(0);

        //make the window visible
        glfwShowWindow(glfwWindow);

        //Initialize audio device
        String defaultDeviceName = alcGetString(0,ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if(!alCapabilities.OpenAL10)
        {
            assert false : "AUDIO library not supported";
        }

        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);


        this.frameBuffer = new FrameBuffer(2560, 1440);
        this.pickingTexture = new PickingTexture(2560, 1440);

        this.imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imGuiLayer.initImGui();



        glViewport(0,0, 2560, 1440);

        Window.changeScene(new LevelEditorSceneInitializer(), "level.txt");
    }






    public void loop(){
        float beginTime =(float)glfwGetTime();
        float endTime ;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
         Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");


        while(!glfwWindowShouldClose(glfwWindow)){
            //poll events
            glfwPollEvents();

            //render pass 1: render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0, 0 , 2560, 1440 );

            glClearColor(0,0,0,0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();



            pickingTexture.disableWriting();
            glEnable(GL_BLEND);


            //Render pass 2: render actual game
            DebugDraw.beginFrame();

            this.frameBuffer.bind();
            Vector4f clearColor = currentScene.camera().clearColor;
            glClearColor(clearColor.x,clearColor.y,clearColor.z,clearColor.w);
            glClear(GL_COLOR_BUFFER_BIT);

            if(dt>=0)
            {

                Renderer.bindShader(defaultShader);
                if(runtimePlaying)
                {
                    currentScene.update(dt);
                }
                else
                {
                    currentScene.editorUpdate(dt);
                }

                currentScene.render();
                DebugDraw.draw();
            }
            this.frameBuffer.unbind();
            this.imGuiLayer.update(dt, currentScene);

            KeyListener.endFrame();
            MouseListener.endFrame();
            glfwSwapBuffers(glfwWindow);


            //FRAME RATE
            //----------------------------------------------------------------------------------\
          //  System.out.println(1/dt);
            //_________________________________________________________________________________

            endTime = (float) glfwGetTime();
             dt = endTime -beginTime;
            beginTime = endTime;

        }


    }
    public static int getWidth()
    {
        return 2560; //get().width;
    }
    public static int getHeight()
    {
        return  1440; // get().height;
    }
    public static void setWidth(int newWidth)
    {
        get().width = newWidth;
    }
    public static void setHeight(int newHeight)
    {
        get().height = newHeight;
    }

    public static FrameBuffer getFramebuffer()
    {
        return get().frameBuffer;
    }

    public static float getTargetAspectRatio()
    {
        return 16.0f/ 9.0f;
    }

    public static ImGuiLayer getImGuiLayer()
    {
        return  get().imGuiLayer;
    }

    @Override
    public void onNotify(GameObject object, Event event) {

        switch (event.type)
        {
            case GameEngineStartPlay:
                this.runtimePlaying = true;
                currentScene.save();
                Fireball.fireballCount = 0;
                Window.changeScene(new LevelSceneInitializer(), "menu.txt");
                break;
            case GameEngineStopPlay:
                this.runtimePlaying = false;
                Fireball.fireballCount = 0;
                Window.changeScene(new LevelEditorSceneInitializer(), fileName);
                sceneMusic.stopMusic();
                break;
            case LoadLevel:
                Window.changeScene(new LevelEditorSceneInitializer(), "menu.txt");
                break;
            case SaveLevel:
                currentScene.save();
                break;


        }


    }
}

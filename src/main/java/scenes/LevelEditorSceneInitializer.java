package scenes;

import Rixx.*;
import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics2D.components.Box2DCollider;
import physics2D.components.RigidBody2D;
import physics2D.enums.BodyType;
import physics2Dtmp.PhysicsSystem2D;
import physics2Dtmp.primitives.Circle;
import physics2Dtmp.rigidbody.Rigidbody2D;
import renderer.DebugDraw;

import util.AssetPool;

import java.io.File;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Collection;
import java.util.Collections;

public class LevelEditorSceneInitializer extends SceneInitializer {

    private Spritesheet sprites;

   private GameObject levelEditorStuff ;





    public LevelEditorSceneInitializer() {

    }

    @Override
    public void init(Scene scene) {


        sprites = AssetPool.getSpritesheet("assets/images/SpriteSheets/decorationsAndBlocks.png");
        Spritesheet gizmos = AssetPool.getSpritesheet("assets/images/SpriteSheets/gizmos.png");


        levelEditorStuff = scene.createGameObject("LevelEditor");
        levelEditorStuff.setNoSerialize();
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new KeyControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(scene.camera()));
        levelEditorStuff.addComponent(new GizmoSystem(gizmos));
        scene.addGameObjectToScene(levelEditorStuff);





    }
    @Override
    public void loadResources(Scene scene) {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/images/SpriteSheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/SpriteSheets/decorationsAndBlocks.png"),
                        16, 16, 81, 0));
        AssetPool.addSpritesheet("assets/images/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                        16, 16, 26, 0));
        AssetPool.addSpritesheet("assets/images/SpriteSheets/turtle.png",
                new Spritesheet(AssetPool.getTexture("assets/images/SpriteSheets/turtle.png"),
                        16, 24, 4, 0));
        AssetPool.addSpritesheet("assets/images/bigSpritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/bigSpritesheet.png"),
                        16, 32, 42, 0));
        AssetPool.addSpritesheet("assets/images/SpriteSheets/pipes.png",
                new Spritesheet(AssetPool.getTexture("assets/images/SpriteSheets/pipes.png"),
                        32, 32, 4, 0));
        AssetPool.addSpritesheet("assets/images/SpriteSheets/items.png",
                new Spritesheet(AssetPool.getTexture("assets/images/SpriteSheets/items.png"),
                        16, 16, 43, 0));
        AssetPool.addSpritesheet("assets/images/SpriteSheets/gizmos.png",
                new Spritesheet(AssetPool.getTexture("assets/images/SpriteSheets/gizmos.png"),
                        24, 48, 3, 0));
        AssetPool.addSpritesheet("assets/images/start.png",
                new Spritesheet(AssetPool.getTexture("assets/images/start.png"),
                        100, 100, 1, 0));

        AssetPool.getTexture("assets/images/blendImage2.png");

        AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", true);
        AssetPool.addSound("assets/sounds/flagpole.ogg", false);
        AssetPool.addSound("assets/sounds/break_block.ogg", false);
        AssetPool.addSound("assets/sounds/bump.ogg", false);
        AssetPool.addSound("assets/sounds/coin.ogg", false);
        AssetPool.addSound("assets/sounds/gameover.ogg", false);
        AssetPool.addSound("assets/sounds/jump-small.ogg", false);
        AssetPool.addSound("assets/sounds/mario_die.ogg", false);
        AssetPool.addSound("assets/sounds/pipe.ogg", false);
        AssetPool.addSound("assets/sounds/powerup.ogg", false);
        AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
        AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
        AssetPool.addSound("assets/sounds/stomp.ogg", false);
        AssetPool.addSound("assets/sounds/kick.ogg", false);
        AssetPool.addSound("assets/sounds/invincible.ogg", false);

        for (GameObject g : scene.getGameObjects()) {
            if (g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }
            if (g.getComponent(StateMachine.class) != null) {
                StateMachine stateMachine = g.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
                }

        }
    }





    @Override
    public void imgui() {


        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imgui();
        ImGui.end();

        ImGui.begin("Objects");
        if(ImGui.beginTabBar("WindowTabBar")) {
            if(ImGui.beginTabItem("Solid Blocks")) {


                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < sprites.size(); i++) {

                   //TODO REMOVE
                    if(i == 34) continue;
                    if(i >= 38 && i < 61) continue;


                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 4;
                    float spriteHeight = sprite.getHeight() * 4;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
                        //TODO REMOVE
                        RigidBody2D rb = new RigidBody2D();
                        rb.setBodyType(BodyType.Static);
                        object.addComponent(rb);
                        Box2DCollider b2d = new Box2DCollider();
                        b2d.setHalfSize(new Vector2f(0.25f, 0.25f));
                        object.addComponent(b2d);
                        object.addComponent(new Ground());
                        if(i == 12)
                        {
                            object.addComponent(new BreakableBrick());

                        }
                        levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }

            if(ImGui.beginTabItem("Decoration Blocks"))
            {
                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 34; i < 61; i++) {

                    //TODO REMOVE
                    if(i >= 35 & i < 38) continue;
                    if(i >= 42 &&  i < 45) continue;


                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 4;
                    float spriteHeight = sprite.getHeight() * 4;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
                        //TODO REMOVE


                        levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }

                ImGui.endTabItem();
            }

            if(ImGui.beginTabItem("Prefabs"))
            {
                int uID  =0;
                Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");
                Sprite sprite = playerSprites.getSprite(0);
                float spriteWidth = sprite.getWidth() * 4;
                float spriteHeight = sprite.getHeight() * 4;
                int id = sprite.getTexId();
                Vector2f[] texCoords = sprite.getTexCoords();

                ImGui.pushID(uID++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generateMario();
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                Spritesheet items = AssetPool.getSpritesheet("assets/images/SpriteSheets/items.png");
                 sprite = items.getSprite(0);

                id = sprite.getTexId();
                 texCoords = sprite.getTexCoords();
                 ImGui.pushID(uID++);


                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generateQuestionBlock();
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                sprite = playerSprites.getSprite(14);
                id = sprite.getTexId();
                texCoords = sprite.getTexCoords();
                ImGui.pushID(uID++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generateGoomba();
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                //turtles
                Spritesheet turtle = AssetPool.getSpritesheet("assets/images/SpriteSheets/turtle.png");
                sprite = turtle.getSprite(0);
                id = sprite.getTexId();
                texCoords = sprite.getTexCoords();
                ImGui.pushID(uID++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generateTurtle();
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();
                //win pole top

                sprite  = items.getSprite(6);
                id = sprite.getTexId();
                texCoords = sprite.getTexCoords();
                ImGui.pushID(uID++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generateFlagtop();
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();
                // win pole
                sprite  = items.getSprite(33);
                id = sprite.getTexId();
                texCoords = sprite.getTexCoords();
                ImGui.pushID(uID++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generateFlagPole();
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                //start button

                sprite  = AssetPool.getSpritesheet("assets/images/start.png").getSprite(0);
                id = sprite.getTexId();
                texCoords = sprite.getTexCoords();
                ImGui.pushID(uID++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generateStartButton();
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();




                //Pipes

                Spritesheet pipes = AssetPool.getSpritesheet("assets/images/SpriteSheets/pipes.png");
                sprite = pipes.getSprite(0);
                id = sprite.getTexId();
                texCoords = sprite.getTexCoords();
                ImGui.pushID(uID++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generatePipe(Direction.Down);
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                sprite = pipes.getSprite(1);
                id = sprite.getTexId();
                texCoords = sprite.getTexCoords();
                ImGui.pushID(uID++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generatePipe(Direction.Up);
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                sprite = pipes.getSprite(2);
                id = sprite.getTexId();
                texCoords = sprite.getTexCoords();
                ImGui.pushID(uID++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generatePipe(Direction.Right);
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                sprite = pipes.getSprite(3);
                id = sprite.getTexId();
                texCoords = sprite.getTexCoords();
                ImGui.pushID(uID++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generatePipe(Direction.Left);
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.popID();


                ImGui.endTabItem();
            }


            if(ImGui.beginTabItem("Sounds"))
            {
                Collection<Sound> sounds = AssetPool.getAllSounds();
                for(Sound sound : sounds)
                {
                    File tmp = new File(sound.getFilepath());
                    if(ImGui.button(tmp.getName()))
                    {
                        if(!sound.isPlaying())
                        {
                            System.out.println("hit");
                            sound.play();
                        }
                        else
                        {
                            sound.stop();
                        }
                    }
                    if(ImGui.getContentRegionAvailX() > 100)
                    {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }

        ImGui.end();
    }
}
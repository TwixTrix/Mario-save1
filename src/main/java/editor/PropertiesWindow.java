package editor;

import Rixx.GameObject;
import Rixx.MouseListener;
import components.NonPickable;
import components.SpriteRenderer;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import physics2D.components.Box2DCollider;
import physics2D.components.CircleCollider;
import physics2D.components.RigidBody2D;
import renderer.PickingTexture;
import scenes.Scene;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private List<GameObject> activeGameObjects;
    private List<Vector4f> activeGameObjectsOGColor;
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;


     public PropertiesWindow(PickingTexture pickingTexture)
     {
         this.activeGameObjects = new ArrayList<>();
         this.pickingTexture = pickingTexture;
         this.activeGameObjectsOGColor =new ArrayList<>();


     }




    public void imgui()
    {
        if(activeGameObjects.size() == 1 && activeGameObjects.get(0) != null)
        {
            activeGameObject = activeGameObjects.get(0);
            ImGui.begin("Properties");

            if(ImGui.beginPopupContextWindow("ComponentAdder"))
            {
                if(ImGui.menuItem("Add Rigidbody"))
                {
                    if(activeGameObject.getComponent(RigidBody2D.class) == null)
                    {
                        activeGameObject.addComponent(new RigidBody2D());
                    }
                }

                if(ImGui.menuItem("Add Box Collider"))
                {
                    if(activeGameObject.getComponent(Box2DCollider.class) == null && activeGameObject.getComponent(CircleCollider.class) == null)
                    {
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }
                if(ImGui.menuItem("Add Circle Collider"))
                {
                    if(activeGameObject.getComponent(CircleCollider.class) == null && activeGameObject.getComponent(Box2DCollider.class) == null)
                    {
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }
                ImGui.endPopup();
            }

            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject()
    {
        return activeGameObjects.size() == 1 ? this.activeGameObjects.get(0) : null;
    }

    public List<GameObject> getActiveGameObjects()
    {
       return this.activeGameObjects;
    }

    public void clearSelected()
    {
        if(activeGameObjectsOGColor.size() > 0)
        {
            int i = 0;
            for(GameObject go :  activeGameObjects)
            {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if(spr != null )
                {
                    spr.setColor(activeGameObjectsOGColor.get(i));

                }
                i++;
            }
        }
        this.activeGameObjects.clear();
        this.activeGameObjectsOGColor.clear();
    }

    public void setActiveGameObject(GameObject go)
    {
        if(go != null)
        {
            clearSelected();
            this.activeGameObjects.add(go);

        }
    }

    public void addActiveGameObject(GameObject go)
    {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if(spr != null)
        {
            this.activeGameObjectsOGColor.add(new Vector4f(spr.getColor()));
            spr.setColor(new Vector4f(0.8f, 0.8f, 0, 0.8f ));
        }
        else
        {
            this.activeGameObjectsOGColor.add(new Vector4f());
        }
        this.activeGameObjects.add(go);
    }
    public PickingTexture getPickingTexture() {
        return this.pickingTexture;
    }



}

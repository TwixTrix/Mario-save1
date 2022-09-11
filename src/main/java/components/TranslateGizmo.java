package components;

import Rixx.MouseListener;
import editor.PropertiesWindow;
import Rixx.GameObject;
import Rixx.Prefabs;
import Rixx.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class TranslateGizmo extends Gizmo {


    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
       super(arrowSprite, propertiesWindow);
    }



    @Override
    public void editorUpdate(float dt)
    {

        if (activeGameObject != null)
        {
            if(xAxisActive &&!yAxisActive)
            {
                activeGameObject.transform.position.x -= MouseListener.getWorldX();
            }
            else if(yAxisActive)
            {
                activeGameObject.transform.position.y -= MouseListener.getWorldY();
            }
        }


        super.editorUpdate(dt);
    }


}
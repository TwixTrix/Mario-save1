package editor;

import Rixx.GameObject;
import Rixx.Window;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

public class SceneHierarchyWindow {

    private static String payloadDragDropType = "SceneHierarchy";

    public void imgui()
    {
        ImGui.begin("SceneHierarchy");

        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        int index = 0;
        for(GameObject obj : gameObjects)
        {
            if(!obj.doSerialization())
            {
                continue;
            }

            ImGui.pushID(index);
            boolean treeNodeOpen = doTreeNode(index, obj);

            if(treeNodeOpen)
            {
                ImGui.treePop();
            }
            index ++;

        }
        ImGui.end();
    }

    private boolean doTreeNode(int index, GameObject obj)
    {
        boolean treeNodeOpen = ImGui.treeNodeEx(obj.name, ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth, obj.name );
        ImGui.popID();


    if(ImGui.beginDragDropSource())
    {
        ImGui.setDragDropPayloadObject(payloadDragDropType , obj);
        ImGui.text(obj.name);
        ImGui.endDragDropSource();
    }
    if(ImGui.beginDragDropTarget())
    {
        Object payloadObj = ImGui.acceptDragDropPayloadObject(payloadDragDropType);
        if(payloadObj != null)
        {
            if(payloadObj.getClass().isAssignableFrom(GameObject.class))
            {
                GameObject playerGameObj = (GameObject) payloadObj;
                System.out.println("Payload accepted " + playerGameObj.name);
            }
        }
        ImGui.endDragDropTarget();
    }

        return  treeNodeOpen;
    }
}

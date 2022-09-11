package renderer;

import Rixx.Camera;
import Rixx.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.AssetPool;
import util.RMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {

    private static int MAX_LINES = 3000;

    private static List<Line2D> Lines = new ArrayList<>();
    //6 floats per vertex, 2 vertices per line
    public static float[] vertexArray = new float[MAX_LINES*6*2];
    private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");

    private static int vaoID;
    private static int vboID;

    private static boolean started = false;

    public static void start()
    {
        // generate the vao

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //Create the bno and buffer memory

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER , vertexArray.length* Float.BYTES, GL_DYNAMIC_DRAW  );

        // Enable the vertex array attributes
        glVertexAttribPointer(0, 3,GL_FLOAT, false, 6*Float.BYTES, 0 );
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3,GL_FLOAT, false, 6*Float.BYTES, 3*Float.BYTES );
        glEnableVertexAttribArray(1);
        //=====LINE WIDTH===================================================
        glLineWidth(1.0f);
    }

    public static void beginFrame()
    {
        if(!started)
        {
            start();
            started = true;
        }

        //Remove dead lines
        for(int i = 0; i< Lines.size(); i++)
        {
            if(Lines.get(i).beginFrame() < 0)
            {
                Lines.remove(i);
                i--;
            }
        }

    }

    public static void draw()
    {
        if(Lines.size() <= 0) return;

        int index = 0;
        for(Line2D line : Lines)
        {
            for(int i = 0 ; i < 2; i++)
            {
                Vector2f position = i == 0 ? line.getFrom() : line.getTo();
                Vector3f color = line.getColor();

                // load postion
                vertexArray[index] = position.x;
                vertexArray[index + 1] = position.y;
                vertexArray[index + 2] = -10.0f;

                //load color
                vertexArray[index + 3] = color.x;
                vertexArray[index + 4] = color.y;
                vertexArray[index + 5] = color.z;
                index += 6;

            }
        }
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0 , Arrays.copyOfRange(vertexArray, 0, Lines.size()* 6* 2));

        //use shader
        shader.uploadMat4f("uProjection" , Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        // Bind the  vao

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //draw the batch

        //TODO : *2*6 or *2?
        glDrawArrays(GL_LINES, 0 ,Lines.size()  );

        //Disable location

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        //unbind shader
        shader.detach();

    }
    //==============================================
    // add line2D methods
    //==========================
    public static void addLine2D(Vector2f from, Vector2f to)
    {
        addLine2D(from, to, new Vector3f(0,1,0), 1);
    }
    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color)
    {
        addLine2D(from, to,color, 1);
    }
    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
        Camera camera = Window.getScene().camera();
        Vector2f cameraLeft = new Vector2f(camera.position).add(new Vector2f(-2.0f, -2.0f));
        Vector2f cameraRight = new Vector2f(camera.position).
                add(new Vector2f(camera.getProjectionSize()).mul(camera.getZoom())).
                add(new Vector2f(4.0f, 4.0f));
        boolean lineInView =
                ((from.x >= cameraLeft.x && from.x <= cameraRight.x) && (from.y >= cameraLeft.y && from.y <= cameraRight.y)) ||
                        ((to.x >= cameraLeft.x && to.x <= cameraRight.x) && (to.y >= cameraLeft.y && to.y <= cameraRight.y));
        if (Lines.size() >= MAX_LINES || !lineInView) {
            return;
        }
        DebugDraw.Lines.add(new Line2D(from, to, color, lifetime));
    }


    //====================================================
    // Add box2d methods
    //===================================================
    public static void addBox2D(Vector2f center,Vector2f dimensions ,float rotation)
    {
        addBox2D(center, dimensions, rotation, new Vector3f(0,1,0), 1);
    }
    public static void addBox2D(Vector2f center,Vector2f dimensions ,float rotation,  Vector3f color)
    {
        addBox2D(center, dimensions, rotation, color, 1);
    }
    public static void addBox2D(Vector2f center,Vector2f dimensions ,float rotation,  Vector3f color, int lifeTime)
    {
        Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).mul(0.5f));
        Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).mul(0.5f));

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y ), new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
        };

        if(rotation != 0.0f)
        {
            for ( Vector2f vert  : vertices)
            {
                RMath.rotate(vert , rotation , center);
            }
        }

        addLine2D(vertices[0], vertices[1], color, lifeTime);
        addLine2D(vertices[1], vertices[2], color, lifeTime);
        addLine2D(vertices[2], vertices[3], color, lifeTime);
        addLine2D(vertices[0], vertices[3], color, lifeTime);
    }
    //=======================================
    // add circle method
    //======================================

    public static void addCircle(Vector2f center , float radius , Vector3f color )
    {
        addCircle(center , radius , color, 1);
    }
    public static void addCircle(Vector2f center , float radius )
    {
        addCircle(center, radius, new Vector3f(0,1,0), 1);
    }
    public static void addCircle(Vector2f center , float radius , Vector3f color , int lifeTime )
    {
        Vector2f[] points = new Vector2f[20];
        float increment  = 360.0f / points.length;
        float currentAngle  = 0;
        for(int  i = 0; i < points.length ; i++ )
        {
            Vector2f tmp = new Vector2f(radius, 0);
            RMath.rotate(tmp, currentAngle , new Vector2f());
            points[i] = new Vector2f(tmp).add(center);

            if( i > 0)
            {
                addLine2D(points[ i -1], points[i], color, lifeTime);
            }
            currentAngle += increment;
        }
        addLine2D(points[points.length-1] , points[0], color, lifeTime);


    }





}

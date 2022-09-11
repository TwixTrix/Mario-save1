package physics2Dtmp.primitives;

import org.joml.Vector2f;
import physics2Dtmp.rigidbody.Rigidbody2D;
import util.RMath;

public class Box2D {
    private Vector2f size = new Vector2f();
    private Vector2f halfSize = new Vector2f();
    private Rigidbody2D rigidbody2D = null;

    public Box2D()
    {
        this.halfSize = new Vector2f(size).mul(0.5f);
    }



    public Box2D(Vector2f min, Vector2f max)
    {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(size).mul(0.5f);

    }


    public Vector2f getLocalMin()
    {
        return new Vector2f(this.rigidbody2D.getPosition()).sub(this.halfSize);
    }
    public Vector2f getLocalMax()
    {
        return new Vector2f(this.rigidbody2D.getPosition()).add(this.halfSize);
    }
    public Vector2f getHalfSize()
    {
        return this.halfSize;
    }

    public Vector2f[] getVertices()
    {
        Vector2f min = getLocalMin();
        Vector2f max = getLocalMax();

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), new Vector2f(min.x,max.y),
                new Vector2f(max.x, min.y), new Vector2f(max.x, max.y)
        };
        if(rigidbody2D.getRotation() != 0.0f)
        {
            for(Vector2f vert: vertices)
            {

                // ROTATES point Vector2f about center vector2f by rotation(float in degrees)
                 RMath.rotate(vert, this.rigidbody2D.getRotation() , this.rigidbody2D.getPosition());
            }
        }
        return vertices;
    }

    public Rigidbody2D getRigidbody2D()
    {
        return this.rigidbody2D;
    }

    public void setRigidbody2D(Rigidbody2D rb)
    {
        this.rigidbody2D = rb;
    }
    public void setSize(Vector2f size)
    {
        this.size.set(size);
        this.halfSize.set(size.x/ 2.0f, size.y/ 2.0f );
    }
}

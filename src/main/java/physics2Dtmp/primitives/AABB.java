package physics2Dtmp.primitives;


import org.joml.Vector2f;
import physics2Dtmp.rigidbody.Rigidbody2D;

//Axis alligned bounding box
public class AABB {

    private Vector2f size= new Vector2f();
    private Vector2f halfSize = new Vector2f();
    private Rigidbody2D rigidbody2D = null;

    public AABB()
    {
        this.halfSize = new Vector2f(size).mul(0.5f);
    }



    public AABB(Vector2f min, Vector2f max)
    {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(size).mul(0.5f);

    }

    public Vector2f getMin()
    {
        return new Vector2f(this.rigidbody2D.getPosition()).sub(this.halfSize);
    }
    public Vector2f getMax()
    {
        return new Vector2f(this.rigidbody2D.getPosition()).add(this.halfSize);
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

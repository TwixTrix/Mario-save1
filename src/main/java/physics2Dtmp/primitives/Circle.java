package physics2Dtmp.primitives;

import org.joml.Vector2f;
import physics2Dtmp.rigidbody.Rigidbody2D;

public class Circle extends Collider2D{

    private float radius = 1.0f;
    private Rigidbody2D rigidbody2D = null;

    public float getRadius() {
        return this.radius;
    }

    public Vector2f getCenter()
    {
       return rigidbody2D.getPosition();
    }

    public void setRadius(float r)
    {
        radius = r;
    }
    public void setRigidbody2D(Rigidbody2D rb)
    {
        this.rigidbody2D = rb;
    }

}

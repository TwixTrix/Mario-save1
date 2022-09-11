package physics2Dtmp.forces;

import org.joml.Vector2f;
import physics2Dtmp.rigidbody.Rigidbody2D;

public class Gravity2D implements ForceGenerator{
    private Vector2f gravity;
    public Gravity2D(Vector2f force)
    {
        this.gravity = new Vector2f(force);

    }



    @Override
    public void updateForce(Rigidbody2D rigidbody2D, float dt)
    {
        rigidbody2D.addForce(new Vector2f(gravity).mul(rigidbody2D.getMass()));
    }
}

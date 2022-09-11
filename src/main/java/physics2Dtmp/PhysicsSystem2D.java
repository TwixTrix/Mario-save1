package physics2Dtmp;

import org.joml.Vector2f;
import physics2Dtmp.forces.ForceRegistry;
import physics2Dtmp.forces.Gravity2D;
import physics2Dtmp.primitives.Collider2D;
import physics2Dtmp.rigidbody.CollisionManifold;
import physics2Dtmp.rigidbody.Collisions;
import physics2Dtmp.rigidbody.Rigidbody2D;

import java.util.ArrayList;
import java.util.List;

public class PhysicsSystem2D {

    private ForceRegistry forceRegistry;
    private Gravity2D gravity2D;

    private List<Rigidbody2D> rigidbody2DList;
    private List<Rigidbody2D> bodies1;
    private List<Rigidbody2D> bodies2;
    private List<CollisionManifold> collisions;

    private float fixedUpdate;
    private int impulseIterations = 6;

    public PhysicsSystem2D(float fixedUpdateDt, Vector2f gravity2D)
    {
         this.forceRegistry = new ForceRegistry();
        this.gravity2D = new Gravity2D(gravity2D);

        this.rigidbody2DList = new ArrayList<>();
        this.bodies1 = new ArrayList<>();
        this.bodies2 = new ArrayList<>();
        this.collisions = new ArrayList<>();



        this.fixedUpdate = fixedUpdateDt;

    }

    public void update(float dt)
    {
        fixedUpdate();
    }
    public void fixedUpdate()
    {
        bodies1.clear();
        bodies2.clear();
        collisions.clear();

        //find any collsions
        int size = rigidbody2DList.size();
        for(int i = 0; i < size ; i++)
        {
            for(int j = i; j < size ; j++)
            {
                if(i == j)continue;;

                CollisionManifold result = new CollisionManifold();
                Rigidbody2D r1 = rigidbody2DList.get(i);
                Rigidbody2D r2 = rigidbody2DList.get(j);
                Collider2D c1 = r1.getCollider();
                Collider2D c2 = r2.getCollider();

                if(c1 != null && c2 != null && !(r1.hasInfiniteMass() && r2.hasInfiniteMass()))
                {
                    result = Collisions.findCollisionFeatures(c1,c2);
                }

                if(result != null && result.isColliding())
                {
                    bodies1.add(r1);
                    bodies2.add(r2);
                    collisions.add(result);
                }
            }


        }

        //update the forces
        forceRegistry.updateForces(fixedUpdate);

        //resolve collisions via  iterative impulse resolution
        //iterate a certain amount of times to get approximate
        for(int k = 0; k <impulseIterations ; k++)
        {
            for(int i =0; i < collisions.size() ; i++)
            {
                int jSize = collisions.get(i).getContactPoints().size();
                for(int j = 0; j < jSize; j++)
                {
                    Rigidbody2D r1 = bodies1.get(i);
                    Rigidbody2D r2 = bodies2.get(i);
                    applyImpulse(r1 , r2 , collisions.get(i));
                }
            }
        }





        //update the velocity of all rigid bodies
        for( int i = 0;i < rigidbody2DList.size(); i++)
        {
            rigidbody2DList.get(i).physicsUpdate(fixedUpdate);
        }

    }


    private void applyImpulse(Rigidbody2D a ,Rigidbody2D b, CollisionManifold m )
    {
        //Linear velcotiy
        float inverseMass1 = a.getInverseMass();
        float inverseMass2 = b.getInverseMass();
        float inverseMassSum = inverseMass1 + inverseMass2;
        if(inverseMassSum == 0f)
        {
            return;
        }
        //relative veloctiy
        Vector2f relativeVelocity = new Vector2f(b.getVelocity()).sub(a.getVelocity());
        Vector2f relativeNormal = new Vector2f(m.getNormal()).normalize();
        //if moving away from each other do nothing
        if(relativeVelocity.dot(relativeNormal) > 0.0f)
        {
            return;
        }
        float e = Math.min(a.getCor(), b.getCor());
        float numerator = (-(1.0f + e) * relativeVelocity.dot(relativeNormal));
        float j = numerator / inverseMassSum;
        if(m.getContactPoints().size() > 0 && j != 0.0f)
        {
            j /=(float)m.getContactPoints().size();
        }

        Vector2f impulse = new Vector2f(relativeNormal).mul(j);
        a.setVelocity(new Vector2f(a.getVelocity()).add(new Vector2f(impulse).mul(inverseMass1).mul(-1f)));
        b.setVelocity(new Vector2f(b.getVelocity()).add(new Vector2f(impulse).mul(inverseMass2).mul(1f)));

    }

    public void addRigidbody(Rigidbody2D body, boolean addGravity)
    {
        this.rigidbody2DList.add(body);
        if(addGravity)
        {
            this.forceRegistry.add(body, gravity2D);
        }

        //register gravity

    }



}

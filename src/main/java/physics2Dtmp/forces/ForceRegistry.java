package physics2Dtmp.forces;

import physics2Dtmp.rigidbody.Rigidbody2D;

import java.util.ArrayList;
import java.util.List;

public class ForceRegistry {
    private List<ForceRegistration> registry;

    public ForceRegistry()
    {
        this.registry = new ArrayList<>();

    }

    public void add(Rigidbody2D rb, ForceGenerator fg)
    {
        ForceRegistration fr = new ForceRegistration(fg, rb);
        registry.add(fr);

    }

    public void remove(Rigidbody2D rb, ForceGenerator fg)
    {
        ForceRegistration fr = new ForceRegistration(fg, rb);
        registry.remove(fr);
    }

    public void clear()
    {
        registry.clear();
    }

    public void updateForces(float dt)
    {
        for(ForceRegistration fr: registry)
        {
            fr.forceGenerator.updateForce(fr.rigidbody2D, dt);

        }
    }

    public void zeroForces()
    {
        for(ForceRegistration fr: registry)
        {

            //TODO : implement
            //fr.rigidbody2D.zeroForces();
        }
    }

}

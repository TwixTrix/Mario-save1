package physics2Dtmp.forces;

import physics2Dtmp.rigidbody.Rigidbody2D;

public interface ForceGenerator {
    void updateForce(Rigidbody2D rigidbody2D , float dt);
}

package physics2Dtmp.forces;

import physics2Dtmp.rigidbody.Rigidbody2D;

public class ForceRegistration {

    public ForceGenerator forceGenerator ;
    public Rigidbody2D rigidbody2D;

    public ForceRegistration(ForceGenerator fg, Rigidbody2D rb)
    {
        this.forceGenerator = fg;
        this.rigidbody2D = rb;
    }

    @Override
    public boolean equals(Object other)
    {
        if( other == null ) return false;
        if( other.getClass() != ForceRegistration.class) return false;

        ForceRegistration fr = (ForceRegistration) other;
        return fr.rigidbody2D == this.rigidbody2D && fr.forceGenerator == this.forceGenerator;
    }

}

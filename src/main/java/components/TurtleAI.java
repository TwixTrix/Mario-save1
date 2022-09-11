package components;

import Rixx.Camera;
import Rixx.GameObject;
import Rixx.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2D.Physics2D;
import physics2D.components.RigidBody2D;
import util.AssetPool;

public class TurtleAI extends Component{

    private transient boolean goingRight = false;
    private transient RigidBody2D rb;
    private transient float walkSpeed = 0.6f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);
    private transient boolean onGround = false;
    private transient boolean isDead = false;
    private transient boolean isMoving = false;
    private transient StateMachine stateMachine;
    private float movingDebounce = 0.32f;

    private transient Camera camera;



    @Override
    public void start()
    {
        this.stateMachine = this.gameObject.getComponent(StateMachine.class);
        this.rb = this.gameObject.getComponent(RigidBody2D.class);
        this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;

        this.camera = Window.getScene().camera();

    }

    @Override
    public void update(float dt)
    {
        movingDebounce -= dt;

        if(this.gameObject.transform.position.x > camera.position.x + camera.getProjectionSize().x * camera.getZoom())
        {
            return;
        }

        if(!isDead || isMoving)
        {
            if(goingRight)
            {
                gameObject.transform.scale.x = -0.25f;
                velocity.x  = walkSpeed;
            }
            else
            {
                gameObject.transform.scale.x = 0.25f;
                velocity.x = -walkSpeed;
            }
        }
        else
        {
            velocity.x = 0;
        }

        checkOnGround();
        if(onGround)
        {
            this.acceleration.y = 0;
            this.velocity.y = 0;
        }
        else
        {
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        }
        this.velocity.y += this.acceleration.y * dt;
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -terminalVelocity.y);
        this.rb.setVelocity(velocity);

        if(this.gameObject.transform.position.x < camera.position.x -6.5f  /* || this.gameObject.transform.position.y < 0.0f*/)
        {
            this.gameObject.destroy();
            //fix deleation
        }



    }

    @Override
    public void beginCollision(GameObject obj, Contact contact, Vector2f contactNormal)
    {
        PlayerController playerController = obj.getComponent(PlayerController.class);
        if(playerController != null)
        {
            if(!isDead && !playerController.isDead() && !playerController.isHurtInvincible() && contactNormal.y > 0.58f)
            {
                playerController.enemyBounce();
                stomp();
                walkSpeed *= 3.0f;
            }
            else if(movingDebounce < 0 && !playerController.isDead() && !playerController.isHurtInvincible() && (isMoving || !isDead) && contactNormal.y < 0.58f)
            {

                playerController.die();
            }else if(!playerController.isDead() && !playerController.isHurtInvincible())
            {
                if(isDead && contactNormal.y > 0.58f)
                {
                    playerController.enemyBounce();
                    isMoving = !isMoving;
                    goingRight = contactNormal.x < 0;
                }
                else if(isDead && !isMoving)
                {
                    isMoving = true;
                    goingRight = contactNormal.x < 0;
                    movingDebounce = 0.32f;
                }
            }
        } else if(Math.abs(contactNormal.y ) < 0.1f && !obj.isDead())
        {
            goingRight = contactNormal.x < 0;
            if(isMoving && isDead)
            {
                AssetPool.getSound("assets/sounds/bump.ogg").play();
            }

        }

        if(obj.getComponent(Fireball.class) != null)
        {

                stomp();
            obj.getComponent(Fireball.class).disappear();
        }
    }
    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal)
    {
        GoombaAI goomba = obj.getComponent(GoombaAI.class);
        if(isDead && isMoving && goomba != null)
        {
            goomba.stomp();
            contact.setEnabled(false);
            AssetPool.getSound("assets/sounds/kick.ogg").play();
            return;
        }

        TurtleAI turtle = obj.getComponent(TurtleAI.class);
        if(isDead && isMoving && turtle != null)
        {

            //ToDO animation
            obj.destroy();
            System.out.println(true);
        }
    }

    public void stomp()
    {
        this.isDead = true;
        this.isMoving = false;
        this.velocity.zero();
        this.rb.setVelocity(this.velocity);
        this.rb.setAngularVelocity(0.0f);
        this.rb.setGravityScale(0.0f);
        this.stateMachine.trigger("SquashMe");
        AssetPool.getSound("assets/sounds/bump.ogg").play();
    }



    public void checkOnGround()
    {
        float innerPlayerWidth = 0.25f * 0.7f;
        float yVal = -0.2f;
        onGround  = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
    }
}

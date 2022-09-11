package components;

import Rixx.Camera;
import Rixx.GameObject;
import Rixx.Window;
import org.joml.Vector4f;

public class GameCamera extends Component {
    private transient GameObject player;
    private transient Camera sceneCamera;
    private transient float highestX = Float.MIN_VALUE;
    private transient float undergroundYLevel = 0.0f;
    private transient float cameraBuffer = 1.5f;
    private transient float playerBuffer = 0.25f;

    private Vector4f skyColor = new Vector4f(92.0f/255.0f, 148.0f / 255.0f , 252.0f / 255.0f, 1.0f );
    private Vector4f undergroundColor = new Vector4f(0,0,0,1);

    public GameCamera(Camera camera)
    {
        this.sceneCamera = camera;
    }

    @Override
    public void start()
    {
        this.player  = Window.getScene().getGameObjectWith(PlayerController.class);
        this.sceneCamera.clearColor.set(skyColor);
        this.undergroundYLevel = this.sceneCamera.position.y - this.sceneCamera.getProjectionSize().y - this.cameraBuffer;
    }

    @Override
    public void update(float dt)
    {

        if(player != null && !player.getComponent(PlayerController.class).hasWon())
        {
            sceneCamera.position.x = Math.max(player.transform.position.x -2.5f,  highestX);
            highestX = Math.max(highestX, sceneCamera.position.x);

            if(player.transform.position.y < -playerBuffer)
            {
                this.sceneCamera.position.y = undergroundYLevel;
                this.sceneCamera.clearColor.set(undergroundColor);
            } else if(player.transform.position.y >= 0.0f )
            {
                this.sceneCamera.position.y = 0;
                this.sceneCamera.clearColor.set(skyColor);
            }
        }
    }



}

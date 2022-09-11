package Rixx;

import util.AssetPool;

public class sceneMusic {
    private static transient String previousSong;

    public static void playMusic(String newSong)
    {
        if(previousSong == null)
        {
            AssetPool.getSound(newSong).play();
            previousSong = newSong;
            return;
        }

        AssetPool.getSound(previousSong).stop();
        AssetPool.getSound(newSong).play();
        previousSong = newSong;



    }

    public static void stopMusic()
    {
        if(previousSong == null)
        {
            return;
        }
        AssetPool.getSound(previousSong).stop();
    }






}

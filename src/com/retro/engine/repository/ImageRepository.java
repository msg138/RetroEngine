package com.retro.engine.repository;

import com.retro.engine.debug.Debug;

import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.HashMap;

/**
 * Created by Michael on 8/4/2016.
 */
public final class ImageRepository {

    private static ImageRepository m_instance;

    private HashMap<String, BufferedImage> m_images;
    private HashMap<String, Integer> m_imagesId;

    private ImageRepository(){
        m_images = new HashMap<>();
        m_imagesId = new HashMap<>();

        m_instance = this;
    }

    public static ImageRepository getInstance(){
        if(m_instance == null)
            new ImageRepository();
        return m_instance;
    }

    public boolean imageExists(String imageName){
        if(m_images.get(imageName) != null)
            return true;
        return false;
    }
    public boolean imageExistsId(String imageName){
        if(m_imagesId.get(imageName) != null)
            return true;
        return false;
    }

    public BufferedImage getExistingImage(String imageName){
        return m_images.get(imageName);
    }

    public int getExistingImageId(String fname){
        Debug.out("Existing image gotten! "+fname);
        return m_imagesId.get(fname);
    }

    public void addImage(String iName, BufferedImage image){
        m_images.put(iName, image);
        Debug.out("Texture added!" + iName);
    }
    public void addImageId(String iName, int textureId){
        m_imagesId.put(iName, textureId);
        Debug.out("Texture id added!" + iName);
    }
}

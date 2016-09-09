/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lazycodes.models;

import java.io.Serializable;
import javafx.scene.media.Media;

/**
 *
 * @author junel
 */
public class MP3File implements Serializable{
    
    private Media media;
    private String title;
    private String artist;
    
    public MP3File(Media media){
        this.media = media;
        this.title = media.getMetadata().get("title").toString();
        this.artist = media.getMetadata().get("artist").toString();
    }

    /**
     * @return the media
     */
    public Media getMedia() {
        return media;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the artist
     */
    public String getArtist() {
        return artist;
    }
    
}

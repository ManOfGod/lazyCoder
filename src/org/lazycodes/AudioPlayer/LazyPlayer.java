/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lazycodes.AudioPlayer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

/**
 *
 * @author junel
 */
public class LazyPlayer extends Application{
    
    private Scene scene;
    private JFXButton playButton;
    private GridPane controlsLayout;
    private JFXSlider mediaSeek;
    private Label time;
    private List<Media> playList;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;

    @Override
    public void start(Stage primaryStage){
        
        try{
            
            
            
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
    
    public static void main(String[]args){
        launch(args);
    }
    
}

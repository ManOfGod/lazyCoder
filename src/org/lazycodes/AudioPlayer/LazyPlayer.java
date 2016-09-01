/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lazycodes.AudioPlayer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.floor;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.lazycodes.service.FileManager;
import static java.lang.String.format;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author junel
 */
public class LazyPlayer extends Application{
    
    private Scene scene;
    private JFXButton playPauseButton;
    private JFXButton nextButton;
    private JFXButton previousButton;
    private GridPane controlsLayout;
    private JFXSlider slider;
    private Label time;
    private List<Media> playList;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private Duration duration;
    
    private final ImageView play = new ImageView(new Image("/org/lazycodes/resources/play.png", 50, 50, true, true));
    private final ImageView pause = new ImageView(new Image("/org/lazycodes/resources/pause.png", 50, 50, true, true));
    private final ImageView next = new ImageView(new Image("/org/lazycodes/resources/next.png", 50, 50, true, true));
    private final ImageView previous = new ImageView(new Image("/org/lazycodes/resources/previous.png", 50, 50, true, true));
    
    private int playCounter = 0;

    @Override
    public void start(Stage primaryStage){
        
        try{          
            initUI(primaryStage);                      
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
    
    private void initUI(Stage stage) throws IOException{
                
        playPauseButton = new JFXButton("", play);
        playPauseButton.setButtonType(JFXButton.ButtonType.FLAT);
        
        nextButton = new JFXButton("", next);
        nextButton.setButtonType(JFXButton.ButtonType.FLAT);
        
        previousButton = new JFXButton("", previous);
        previousButton.setButtonType(JFXButton.ButtonType.FLAT);
        
        controlHandlers();
        
        playList = new ArrayList<>();
        loadPlaylist(playList);
        
        mediaPlayer = new MediaPlayer(playList.get(0));
        mediaPlayer.currentTimeProperty().addListener((Observable ov) -> {
            updateValues();
        });

        mediaPlayer.setOnReady(() -> {
            duration = mediaPlayer.getMedia().getDuration();
            updateValues();
        });
        
        mediaPlayer.setOnEndOfMedia(() -> {
            playCounter++;
            if(playCounter > (playList.size()-1)){
                playCounter = 0;
            }
            gaplessPlayback();
        });

        mediaView = new MediaView(mediaPlayer);
        
        slider = new JFXSlider();
        HBox.setHgrow(slider, Priority.ALWAYS);
        slider.setCenterShape(true);
        slider.valueProperty().addListener((Observable observable) -> {
            if (slider.isValueChanging()) {
                // multiply duration by percentage calculated by slider position
                if (duration != null) {
                    mediaPlayer.seek(duration.multiply(slider.getValue() / 100.0));
                }
                updateValues();
            }
        });
  
        time = new Label();
        time.setTextFill(Color.BLUE);
        time.setPrefWidth(slider.getPrefWidth()); 
        time.setCenterShape(true);
        time.setAlignment(Pos.CENTER);
        time.setTextAlignment(TextAlignment.CENTER);
        
        controlsLayout = new GridPane();
        controlsLayout.setAlignment(Pos.CENTER);
        controlsLayout.setHgap(10);
        controlsLayout.setVgap(10);
        controlsLayout.setPadding(new Insets(25,25,25,25));
        
        controlsLayout.add(mediaView, 0, 0);
        
        controlsLayout.add(time, 0, 0, 3, 1);
        
        controlsLayout.add(slider, 0, 1, 3, 1);
        
        controlsLayout.add(previousButton, 0, 2);
        controlsLayout.add(playPauseButton, 1, 2);
        controlsLayout.add(nextButton, 2, 2);
        
        scene = new Scene(controlsLayout, 500, 400);
        
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> System.exit(0));
        stage.show();
        
    }
    
    private void loadPlaylist(List<Media> mediaList){
        
        FileManager files = new FileManager();
        files.loadMediaFiles(new File("C:\\Users\\junel\\Music\\iTunes\\iTunes Media\\Music"));
        
        files.getFiles().stream().forEach((media) -> {
            mediaList.add(new Media(media));
        });
        
    }
    
    private void gaplessPlayback(){
        
        mediaPlayer = new MediaPlayer(playList.get(playCounter));
        mediaPlayer.play();
        mediaPlayer.currentTimeProperty().addListener((Observable ov) -> {
            updateValues();
        });
        

        mediaPlayer.setOnReady(() -> {
            duration = mediaPlayer.getMedia().getDuration();
            updateValues();
        });
        
        mediaPlayer.setOnEndOfMedia(() -> {
            playCounter++;
            if(playCounter > (playList.size()-1)){
                playCounter = 0;
            }
            gaplessPlayback();
        });
        
    }
    
    private void controlHandlers(){
        
        playPauseButton.setOnAction((ActionEvent event) -> {
            if (mediaPlayer.getStatus() == Status.PAUSED || mediaPlayer.getStatus() == Status.READY || mediaPlayer.getStatus() == Status.STOPPED) {
                mediaPlayer.play();
                playPauseButton.setGraphic(pause);
            } else {
                mediaPlayer.pause();
                playPauseButton.setGraphic(play);
            }
        });
        
        nextButton.setOnAction((ActionEvent event) -> {
            playCounter++;
            mediaPlayer.stop();
            if(playCounter > (playList.size()-1)){
                playCounter = 0;
            }
            gaplessPlayback();
        });
        
        previousButton.setOnAction((ActionEvent event) -> {
            playCounter--;
            mediaPlayer.stop();
            if(playCounter < 0){
                playCounter = 0;
            }
            gaplessPlayback();
        });
        
    }
    
    protected void updateValues() {
        if (time != null && slider != null && duration != null) {
            Platform.runLater(() -> {
                Duration currentTime = mediaPlayer.getCurrentTime();
                time.setText(formatTime(currentTime, duration));
                slider.setDisable(duration.isUnknown());
                if (!slider.isDisabled() && duration.greaterThan(Duration.ZERO) && !slider.isValueChanging()) {
                    slider.setValue(currentTime.divide(duration).toMillis() * 100.0);
                }
            });
        }
    }
    
    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
        - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
            - durationMinutes * 60;
            if (durationHours > 0) {
                return format("%d:%02d:%02d/%d:%02d:%02d",
                elapsedHours, elapsedMinutes, elapsedSeconds,
                durationHours, durationMinutes, durationSeconds);
            } else {
                return format("%02d:%02d/%02d:%02d",
                elapsedMinutes, elapsedSeconds, durationMinutes,
                durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return format("%d:%02d:%02d", elapsedHours,
                    elapsedMinutes, elapsedSeconds);
            } else {
                return format("%02d:%02d", elapsedMinutes,
                    elapsedSeconds);
            }
        }
    }
    
    public static void main(String[]args){
        launch(args);
    }
    
}

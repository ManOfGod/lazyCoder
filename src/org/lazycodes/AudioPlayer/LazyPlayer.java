/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lazycodes.AudioPlayer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.skins.JFXSliderSkin;
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
import java.util.Iterator;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.SkinBase;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
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
    private JFXSlider volume;
    private Label time;
    private Label title;
    private List<Media> playList;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private Duration duration;
    private HBox controls;
    private HBox buttonContainer;
    private HBox mediaContainer;
    private VBox verticalLayout;
    private HBox volumeSliderContainer;
    private BorderPane layoutBorder;
    
    private final ImageView play = new ImageView(new Image("/org/lazycodes/resources/ic_play_circle_filled_black_48dp_2x.png", 50, 50, true, true));
    private final ImageView pause = new ImageView(new Image("/org/lazycodes/resources/ic_pause_circle_filled_black_48dp_2x.png", 50, 50, true, true));
    private final ImageView next = new ImageView(new Image("/org/lazycodes/resources/ic_skip_next_black_48dp_2x.png", 50, 50, true, true));
    private final ImageView previous = new ImageView(new Image("/org/lazycodes/resources/ic_skip_previous_black_48dp_2x.png", 50, 50, true, true));
    
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
        slider.setPrefWidth(350);
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
        time.setTextFill(Color.WHITE);
        time.setPrefWidth(slider.getPrefWidth()); 
        time.setAlignment(Pos.CENTER);
        time.setTextAlignment(TextAlignment.CENTER);
        
        title = new Label();
        title.setTextFill(Color.WHITE);
        title.setAlignment(Pos.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setTextOverrun(OverrunStyle.CLIP);
        title.setPrefWidth(350);
        
        buttonContainer = new HBox();
        buttonContainer.getChildren().add(previousButton);
        buttonContainer.getChildren().add(playPauseButton);
        buttonContainer.getChildren().add(nextButton);
        
        verticalLayout = new VBox();
        verticalLayout.setAlignment(Pos.CENTER);
        verticalLayout.getChildren().add(title);
        verticalLayout.getChildren().add(slider);
        verticalLayout.getChildren().add(time);
        verticalLayout.getChildren().add(mediaView);
        verticalLayout.setPadding(new Insets(5, 5, 5, 5));
        
        mediaContainer = new HBox();
        mediaContainer.setStyle("-fx-border-radius: 50px;");
        mediaContainer.setBackground(Background.EMPTY);
//        -fx-background-color: #4A4A4A; 
        mediaContainer.getChildren().add(verticalLayout);
                             
        volume = new JFXSlider();
        volume.setPrefWidth(130.0);
        volume.setCenterShape(true);
        volume.setValue(50.0);
        
        mediaPlayer.setVolume(volume.getValue() / 100.0);
        
        volumeSliderContainer = new HBox();
        volumeSliderContainer.setAlignment(Pos.CENTER);
        volumeSliderContainer.setPadding(new Insets(0, 0, 0, 35));
        volumeSliderContainer.getChildren().add(volume);
        
        controls = new HBox();
        controls.setPadding(new Insets(15, 12, 12, 12));
        controls.setSpacing(10);
        controls.setStyle("-fx-background-color: #404040;");
        controls.setEffect(new DropShadow(20, Color.BLACK));
        controls.getChildren().add(buttonContainer);
        controls.getChildren().add(mediaContainer);
        controls.getChildren().add(volumeSliderContainer);
        
        layoutBorder = new BorderPane();
        layoutBorder.setTop(controls);

        controlHandlers();        
        
        scene = new Scene(layoutBorder, 800, 600);
        
        stage.setScene(scene);
        stage.getIcons().add(new Image("/org/lazycodes/resources/app-logo.png"));
        stage.setResizable(false);
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
        System.out.println(mediaPlayer.getMedia().getMetadata().get("title") == null ? "empty" : mediaPlayer.getMedia().getMetadata().get("title").toString());
        System.out.println(mediaPlayer.getMedia().getMetadata().get("artist") == null ? "empty" : mediaPlayer.getMedia().getMetadata().get("artist").toString());
        System.out.println(mediaPlayer.getMedia().getMetadata().get("album") == null ? "empty" : mediaPlayer.getMedia().getMetadata().get("album").toString());
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
        
        volume.valueProperty().addListener((Observable ov) -> {
            if (volume.isValueChanging()) {
                mediaPlayer.setVolume(volume.getValue() / 100.0);
            }
        });
        
    }
    
    protected void updateValues() {
        if (time != null && slider != null && duration != null) {
            if(mediaPlayer.getMedia() != null){
                title.setText(mediaPlayer.getMedia().getMetadata().get("title") == null ? "" : mediaPlayer.getMedia().getMetadata().get("title").toString());                
            }
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

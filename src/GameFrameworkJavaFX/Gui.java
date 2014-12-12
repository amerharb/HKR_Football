/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GameFrameworkJavaFX;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 *
 * @author anc
 */
public class Gui extends Application {
    
    private static Gui gui;
    private Pane root;
    private Button menuButton;
    private Timeline worldTime;
    public Timeline gameTime;
    
    //AMER CODE: timeCoutner under test
    //this should not be here it should be inside class game
    public final int TotalGameTime = 400; 
    public int gameTimeCounter = TotalGameTime; 
    
    final Label scoreBoard = new Label();
    Image board = new Image(getClass().getResourceAsStream("/img/"+"Board.png"));

    //AMER CODE:
    //DEBUG
    final Label debugBoard = new Label();
    
    final Label throwInBoard = new Label();
    Image throwInPic = new Image(getClass().getResourceAsStream("/img/"+"ThrowIn.png"));

    final Label GoalBoard = new Label();
    Image GoalPic = new Image(getClass().getResourceAsStream("/img/"+"Goal.png"));

    final Label GoalKickBoard = new Label();
    Image GoalKickPic = new Image(getClass().getResourceAsStream("/img/"+"GoalKick.png"));
    
    final Label PauseBoard = new Label();
    Image PausePic = new Image(getClass().getResourceAsStream("/img/"+"Pause.png"));
    
    final Label SecondHalfBoard = new Label();
    Image SecondHalfPic = new Image(getClass().getResourceAsStream("/img/"+"SecondHalf.png"));
    
    final Label GameOverBoard = new Label();
    Image GameOverPic = new Image(getClass().getResourceAsStream("/img/"+"GameOver.png"));
    
    final Label IntroBoard = new Label();
    Image IntroPic = new Image(getClass().getResourceAsStream("/img/" + "Intro.png"));
    
    private boolean isWaitingButtonResponse = true;
    
    @Override
    public void start(Stage primaryStage) {
    
        gui = this;
        
        root = new Pane();
    
        Scene scene = new Scene(root, 300, 250);
        scene.getStylesheets().add("style.css");
        root.setId("pane_background");
        
        //AMER CODE: add event just for full click
//        scene.setOnKeyTyped((KeyEvent event) ->{
//            Game.getInstance().keyClick(event.getCode());
//        
//        });

        scene.setOnKeyPressed((KeyEvent event) -> {
            //System.out.println("KeyEvent character "+event.getCode());
            Game.getInstance().keyPressed(event.getCode());
        });
        
        scene.setOnKeyReleased((KeyEvent event) -> {
            //System.out.println("KeyEvent character "+event.getCode());
            Game.getInstance().keyReleased(event.getCode());
        });
        
        scene.setOnMousePressed((MouseEvent event) -> {
            Game.getInstance().mousePressed(event.getButton() == MouseButton.PRIMARY, (int)event.getX(), (int)event.getY());
        });
        
        //Make fullscreen...
        Screen screen = Screen.getPrimary();
        //AMER CODE: FULL SCREEN SO MAYBE IT WILL FIX 
        //javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
        //primaryStage.setX(bounds.getMinX());
        //primaryStage.setY(bounds.getMinY());
        //primaryStage.setWidth(bounds.getWidth());
        //primaryStage.setHeight(bounds.getHeight());
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.setWidth(1366);
        primaryStage.setHeight(728);

        primaryStage.setTitle("Five-a-side Footy 2014");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        menuButton = new Button();
        menuButton.setText("Click to Start Game");
        menuButton.setOnAction((ActionEvent event) -> {
            ButtomClickHere();
        });
        
        //menuButton.setLayoutX((bounds.getWidth()/2)-100);
        menuButton.setLayoutX((1366/2)-100);
        root.getChildren().add(menuButton);
        
        scoreBoard.relocate(700, 10);
        scoreBoard.setGraphic(new ImageView(board));
        scoreBoard.setTextFill(Color.web("#FFFFFF"));
        scoreBoard.contentDisplayProperty().set(ContentDisplay.CENTER);
        scoreBoard.setText("Blue: "+"\n"+" Red: ");
        scoreBoard.setVisible(true);
        root.getChildren().add(scoreBoard);
        
        //DEBUG
        debugBoard.relocate(10, 10);
        debugBoard.setGraphic(new ImageView(board));
        debugBoard.setTextFill(Color.web("#FF0000"));
        debugBoard.contentDisplayProperty().set(ContentDisplay.CENTER);
        debugBoard.setText("Debug");
        debugBoard.setVisible(true);
        root.getChildren().add(debugBoard);
        
        //INTOR
        //nothing done yet
        
        //AMER CODE: it seemt that there is no need for this
        throwInBoard.setGraphic(new ImageView(throwInPic));
        throwInBoard.relocate(420, 440);
        throwInBoard.setVisible(false);
        root.getChildren().add(throwInBoard);
        
        GoalBoard.setGraphic(new ImageView(GoalPic));
        GoalBoard.relocate(420, 440);
        GoalBoard.setVisible(false);
        root.getChildren().add(GoalBoard);
        
        GoalKickBoard.setGraphic(new ImageView(GoalKickPic));
        GoalKickBoard.relocate(420, 440);
        GoalKickBoard.setVisible(false);
        root.getChildren().add(GoalKickBoard);
        
        PauseBoard.setGraphic(new ImageView(PausePic));
        PauseBoard.relocate(420, 440);
        PauseBoard.setVisible(false);
        root.getChildren().add(PauseBoard);
        
        SecondHalfBoard.setGraphic(new ImageView(SecondHalfPic));
        SecondHalfBoard.relocate(420, 440);
        SecondHalfBoard.setVisible(false);
        root.getChildren().add(SecondHalfBoard);
        
        GameOverBoard.setGraphic(new ImageView(GameOverPic));
        GameOverBoard.relocate(420, 440);
        GameOverBoard.setVisible(false);
        root.getChildren().add(GameOverBoard);
        
        IntroBoard.setGraphic(new ImageView(IntroPic));
        IntroBoard.relocate(290, 80);
        IntroBoard.setVisible(false);
        root.getChildren().add(IntroBoard);
        IntroBoard.toFront();
        
        
        Game.createGameAndSetGui(this);
    }

    //AMER CODE: button code moved here so we can reach from hotkey
    public void ButtomClickHere(){
        if (isWaitingButtonResponse){
            System.out.println("The game was started...!");
            startWorldTime();
            startGameTime();
            menuButton.setVisible(false);
            hideIntrolBoard();
            isWaitingButtonResponse = false;
        }
    }
    public void startWorldTime(){
        worldTime = new Timeline(new KeyFrame(Duration.millis(30), (ActionEvent event) -> {
            Game.getInstance().timeTick();
        }));      
        worldTime.setCycleCount(Timeline.INDEFINITE);
        worldTime.play();        
    }
    
    public void stopWorldTime() {
        worldTime.stop();
    }
    
     public void startGameTime(){
        gameTime = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
            //Game.getInstance().timeTick();
            gameTimeCounter--;
            
        
        }));      
        
        
        gameTime.setCycleCount(Timeline.INDEFINITE);
        gameTime.play();        
    }
    
    public void stopGameTime() {
        gameTime.stop();
    }
    
    public void showMenuButtonWithText(String textOnButton){
        menuButton.setText(textOnButton);
        menuButton.setVisible(true);
        isWaitingButtonResponse = true;
    }
    
    
    public void showThrowInBoard(){
          throwInBoard.setVisible(true);
    }
    
    public void hideThrowInBoard(){
          throwInBoard.setVisible(false);
    }
    
    public void showGoalBoard(){
          GoalBoard.setVisible(true);
    }
    
    public void hideGoalBoard(){
          GoalBoard.setVisible(false);
    }
    
    public void showGoalKickBoard(){
          GoalKickBoard.setVisible(true);
    }
    
    public void hideGoalKickBoard(){
          GoalKickBoard.setVisible(false);
    }
    
    public void showPauseBoard(){
          PauseBoard.setVisible(true);
    }
    
    public void hidePauseBoard(){
          PauseBoard.setVisible(false);
    }
    
    public void showSecondHalfBoard(){
          SecondHalfBoard.setVisible(true);
    }
    
    public void hideSecondHalfBoard(){
          SecondHalfBoard.setVisible(false);
    }
    
    public void showGameOverBoard(){
          GameOverBoard.setVisible(true);
    }
    
    public void hideGameOverBoard(){
          GameOverBoard.setVisible(false);
    }
    
    public void showIntrolBoard(){
          IntroBoard.setVisible(true);
    }
    
    public void hideIntrolBoard(){
          IntroBoard.setVisible(false);
    }
    
    public void addImageView(ImageView imageView){
        root.getChildren().add(imageView);
    }

    public void removeImageView(ImageView imageView){
        root.getChildren().remove(imageView);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public static Gui getInstance(){
        return gui;
    }
    
    public int getHeight(){
        return (int)root.getHeight();
    }
    
    public int getWidth(){
        return (int)root.getWidth();
    }
    
    
}

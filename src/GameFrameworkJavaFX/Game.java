/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GameFrameworkJavaFX;

import Actors.Actor;
import Actors.Actor.ActorDirectionEnum;
import Actors.Ball;
import Actors.ComputerGoalKeeper;
import Actors.ComputerPlayer;
import Actors.HumanGoalKeeper;
import Actors.HumanPlayer;
import Actors.Player;
import Actors.Player.PlayerPositionEnum;
import Actors.Team;
import Actors.ComputerTeam;
import Actors.HumanTeam;
import Actors.Team.TeamKindEnum;
import java.awt.BorderLayout;
import java.util.ArrayList;        
import javafx.scene.input.KeyCode;        
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author anc
 */

//How to add me sound effect to the game search for word "SFX:" there is a step from 1 to 5 describe it
//SFX: Step 1: Download or record a file in mp3 format then put it inside this folder of the project [Project Folder]\src\mp3\example.mp3 
public class Game {
    private static Game game; //A reference to the singleton game instance.
    Gui gui;
    Ball ball;
    
    //SFX: Step 2: add enum here
    private enum GameSoundFXEnum //this list to hold all the options of Sound effect currently only has Goal
    {
        Intro
        ,GameOver
        ,Goal
    }

    //SFX: Step 3: add the mp3 file here simial to below
    Media SFXIntro = new Media(getClass().getResource("/mp3/intro.mp3").toString());  //Intro Sound
    Media SFXGameOver = new Media(getClass().getResource("/mp3/gameOver.mp3").toString());  //GameOver Sound
    Media SFXGoal = new Media(getClass().getResource("/mp3/goal.mp3").toString());  //Goal Sound
//    MediaPlayer SFXIntro = new MediaPlayer(new Media(getClass().getResource("/mp3/intro.mp3").toString()));  //Intro Sound
//    MediaPlayer SFXGameOver = new MediaPlayer(new Media(getClass().getResource("/mp3/gameOver.mp3").toString()));  //GameOver Sound
//    MediaPlayer SFXGoal = new MediaPlayer(new Media(getClass().getResource("/mp3/goal.mp3").toString()));  //Goal Sound
    

    
    public enum GameTimeEnum{
        TimeOver 
        ,FirstHalf
        ,SecondHalf
    }
    
    private Player controlledPlayer; //variable where we store player currently under user control
    private Player nearestTeamMate; //variable where we store closest team mate to controlled player, useful later for passing
    private Player nearestRedPlayerToBall; //not used at the moment, but maybe useful later
    private int blueGoals; //current tally of goals for blue team
    private int redGoals; //current tally of goals for red team
    private TeamKindEnum LastGoalTeam = TeamKindEnum.NoTeam;
    private TeamKindEnum whichTeamHaveBall = TeamKindEnum.NoTeam; //very useful: holds name of team currently in possession of ball, "blue" or "red"
    
    //AMER CODE:
    private GameTimeEnum CurrentHalf =GameTimeEnum.FirstHalf ; //1 means first half of time 2 means second got updated in Gameover method and switchSide method
    
    private final ArrayList<Actor> actors;
    //private final Player[] blueTeam = new Player[6]; //array that stores 6 blue team players
    private HumanTeam blueTeam;
    private HumanGoalKeeper blueKeeper ;
    
    //private final Player[] redTeam = new Player[6]; //array that stores 6 red team players
    private ComputerTeam redTeam;    
    private ComputerGoalKeeper redKeeper;

    //AMER CODE:
    private Player[] bothTeams; // to be used if we want to prefourm an actoin to all players one time
    
    private Game(Gui gui){
        this.gui = gui;        
        actors = new ArrayList();
        createActors();
        HideActors(); //so the Intro Screen will be appear
        playSFX(GameSoundFXEnum.Intro);
        gui.showIntrolBoard();
    }
    
    public static Game getInstance(){
        return game;
    }
    
    public static void createGameAndSetGui(Gui gui){
        game = new Game(gui);
    }
    
    private ArrayList<Actor> getActors() {
        return actors;
    }
    
    //public void addPlayers(Player[] blueTeam, Player[] redTeam) {
    private void addPlayers() {

        bothTeams = new Player[12];
        int x = 0;
        HumanPlayer NewBP;
        ComputerPlayer NewRP;
        for (int i=0; i<5; i++) { //add 5 player in each team
            NewBP = new HumanPlayer(blueTeam);
            blueTeam.Players[i] = NewBP;
            NewBP.setPlayerTeam(blueTeam);
            NewBP.setBall(ball); //define game ball
            addToWorld(NewBP);
            bothTeams[x] = NewBP;
            x++;
            
            NewRP = new ComputerPlayer(redTeam); 
            redTeam.Players[i] = NewRP; 
            NewRP.setPlayerTeam(redTeam);
            NewRP.setBall(ball); //define game ball
            addToWorld(NewRP);
            bothTeams[x] = NewRP;
            x++;
        }

        blueKeeper = new HumanGoalKeeper(blueTeam);
        blueKeeper.setPlayerTeam(blueTeam);
        blueKeeper.setBall(ball);
        blueTeam.Players[5] = blueKeeper;
        addToWorld(blueKeeper);
        bothTeams[x]= blueKeeper;
        x++;
        
        redKeeper = new ComputerGoalKeeper(blueTeam);
        redKeeper.setPlayerTeam(redTeam);
        redKeeper.setBall(ball);
        redTeam.Players[5] = redKeeper;
        addToWorld(redKeeper);
        bothTeams[x]= redKeeper;
        x++;
    }
    
    //AMER CODE: make a method to assign has ball in order to be sure that there is only one play have the ball not more
    private void setPlayerWithBall(Player playerWhoWillHasBall){
        
        for (Player P:bothTeams){
            P.setHasBall(false);
            P.setTeamWithBall(playerWhoWillHasBall.getPlayerTeamKind());
        }
        playerWhoWillHasBall.setHasBall(true);
        trapBall(playerWhoWillHasBall);

    }
    
    private void gameOver(){
        gui.stopWorldTime();
        gui.stopGameTime();
        
        gui.gameTimeCounter = gui.TotalGameTime;
        
        gui.showGameOverBoard();
        playSFX(GameSoundFXEnum.GameOver);
        gui.showMenuButtonWithText("Game Over...Restart");
        blueGoals = 0;
        redGoals = 0;
        CurrentHalf = GameTimeEnum.FirstHalf;
        for (Player P:bothTeams){
            P.setGameTimeFirstHalf();
        }
        setPlayerInStartPositions();    
    }
    
    private void setGameTimeSecondHalf(){ //start 2nd half
        gui.stopWorldTime();
        gui.stopGameTime();

        gui.showSecondHalfBoard();

        gui.showMenuButtonWithText("2nd Half");
        CurrentHalf = GameTimeEnum.SecondHalf;
        for (Player P:bothTeams){
            P.setGameTimeSecondHalf();
        }
        //setPlayerInStartPositions();
        secondHalfPositionsStartByRedTeam();
    }
    
    private void setPlayerInStartPositions() {
        
        if (checkTime() == GameTimeEnum.FirstHalf){
            if (LastGoalTeam != TeamKindEnum.BlueTeam){
                firstHalfPositionsStartByBlueTeam();
            }
            else {
                firstHalfPositionsStartByRedTeam();
            }
        }
        else if(checkTime() == GameTimeEnum.SecondHalf){
            if ((gui.gameTimeCounter == gui.TotalGameTime/2) || LastGoalTeam == TeamKindEnum.RedTeam){ //just start
                secondHalfPositionsStartByRedTeam();
            }
            else {
                secondHalfPositionsStartByBlueTeam();
            }
        }
        else {
            firstHalfPositionsStartByBlueTeam(); //in case of time over we will start as first Half with Blue
        }
    }
    
    private void firstHalfPositionsStartByBlueTeam(){

        ball.stop();
        ball.setLocation(665, 335);
        for (Player B: blueTeam.Players) {
            B.getImageView().setScaleX(1);
            B.stop();
            B.setHasBall(false);
        }
        blueTeam.Players[0].setLocation(200, 150);
        blueTeam.Players[0].setPosition(PlayerPositionEnum.LeftBack);
        blueTeam.Players[1].setLocation(200, 450);
        blueTeam.Players[1].setPosition(PlayerPositionEnum.RightBack);
        blueTeam.Players[2].setLocation(400, 335);
        blueTeam.Players[2].setPosition(PlayerPositionEnum.CentreMidfield);
        blueTeam.Players[3].setLocation(660, 340);
        blueTeam.Players[3].getImageView().setRotate(270);
        blueTeam.Players[3].setPosition(PlayerPositionEnum.InsideRight);
        setPlayerInControl(blueTeam.Players[3]);
        setPlayerWithBall(blueTeam.Players[3]);
        blueTeam.Players[4].setLocation(660, 290);
        blueTeam.Players[4].getImageView().setRotate(90);
        blueTeam.Players[4].setPosition(PlayerPositionEnum.InsideLeft);
        blueTeam.Players[5].setLocation(90, 320);
        blueTeam.Players[5].setPosition(PlayerPositionEnum.Keeper);

        for (Player R: redTeam.Players) {
            R.getImageView().setScaleX(-1);
            R.stop();
            R.setHasBall(false);
        }
        redTeam.Players[0].setLocation(800, 150);
        redTeam.Players[0].setPosition(PlayerPositionEnum.RightMidfield);
        redTeam.Players[1].setLocation(800, 500);
        redTeam.Players[1].setPosition(PlayerPositionEnum.LeftMidfield);
        redTeam.Players[2].setLocation(1050, 100);
        redTeam.Players[2].setPosition(PlayerPositionEnum.RightBack);
        redTeam.Players[3].setLocation(1050, 450);
        redTeam.Players[3].setPosition(PlayerPositionEnum.LeftBack);
        redTeam.Players[4].setLocation(800, 340);
        redTeam.Players[4].setPosition(PlayerPositionEnum.CentreMidfield);
        redTeam.Players[5].setLocation(1320, 330);
        redTeam.Players[5].setPosition(PlayerPositionEnum.Keeper);
    
    }
    
    private void firstHalfPositionsStartByRedTeam(){

        ball.stop();
        ball.setLocation(665, 335);
        for (Player B: blueTeam.Players) {
            B.stop(ActorDirectionEnum.East);
            B.setHasBall(false);
        }
        
        blueTeam.Players[0].setLocation(200, 150);
        blueTeam.Players[0].setPosition(PlayerPositionEnum.LeftBack);
        blueTeam.Players[1].setLocation(200, 450);
        blueTeam.Players[1].setPosition(PlayerPositionEnum.RightBack);
        blueTeam.Players[2].setLocation(400, 335);
        blueTeam.Players[2].setPosition(PlayerPositionEnum.CentreMidfield);
        blueTeam.Players[3].setLocation(600, 450);
        blueTeam.Players[3].setPosition(PlayerPositionEnum.InsideRight);
        setPlayerInControl(blueTeam.Players[3]);
        blueTeam.Players[4].setLocation(600, 150);
        blueTeam.Players[4].setPosition(PlayerPositionEnum.InsideLeft);
        blueTeam.Players[5].setLocation(90, 320);
        blueTeam.Players[5].setPosition(PlayerPositionEnum.Keeper);

        for (Player R: redTeam.Players) {
            R.stop(ActorDirectionEnum.West);
            R.setHasBall(false);
        }
        redTeam.Players[0].setLocation(800, 150);
        redTeam.Players[0].setPosition(PlayerPositionEnum.RightMidfield);
        redTeam.Players[1].setLocation(800, 500);
        redTeam.Players[1].setPosition(PlayerPositionEnum.LeftMidfield);
        redTeam.Players[2].setLocation(1050, 100);
        redTeam.Players[2].setPosition(PlayerPositionEnum.RightBack);
        redTeam.Players[3].stop(ActorDirectionEnum.North,660, 330);
        setPlayerWithBall(redTeam.Players[3]);
        redTeam.Players[3].setPosition(PlayerPositionEnum.LeftBack);
        redTeam.Players[4].stop(ActorDirectionEnum.South,660, 290);
        redTeam.Players[4].setPosition(PlayerPositionEnum.Striker);
        redTeam.Players[5].setLocation(1320, 330);
        redTeam.Players[5].setPosition(PlayerPositionEnum.Keeper);
    
    }
    
    private void secondHalfPositionsStartByRedTeam() {

        ball.stop();
        ball.setLocation(665, 335);
        for (Player R: redTeam.Players) {
            R.stop(ActorDirectionEnum.East);
            R.setHasBall(false);
        }
        redTeam.Players[0].setLocation(200, 150);
        redTeam.Players[1].setLocation(200, 450);
        redTeam.Players[2].setLocation(400, 335);
        redTeam.Players[3].setLocation(660, 340);
        redTeam.Players[3].stop(ActorDirectionEnum.North, 660, 340);
        setPlayerWithBall(redTeam.Players[3]);
        redTeam.Players[4].stop(ActorDirectionEnum.South,660, 290);
        redTeam.Players[5].setLocation(90, 320);
        
        for (Player B: blueTeam.Players) {
            B.stop(ActorDirectionEnum.West);
            B.setHasBall(false);
        }
        blueTeam.Players[0].setLocation(800, 150);
        blueTeam.Players[1].setLocation(800, 450);
        blueTeam.Players[2].setLocation(1050, 150);
        blueTeam.Players[3].setLocation(1050, 450);
        setPlayerInControl(blueTeam.Players[3]);
        blueTeam.Players[4].setLocation(1200, 340);
        blueTeam.Players[5].setLocation(1320, 330);
    }
    
    private void secondHalfPositionsStartByBlueTeam() {

        ball.stop();
        ball.setLocation(665, 335);
        for (Player R: redTeam.Players) {
            R.stop(ActorDirectionEnum.East);
            R.setHasBall(false);
        }
        redTeam.Players[0].setLocation(200, 150);
        redTeam.Players[1].setLocation(200, 450);
        redTeam.Players[2].setLocation(400, 335);
        redTeam.Players[3].setLocation(500, 340);
        redTeam.Players[4].setLocation(500, 290);
        redTeam.Players[5].setLocation(90, 320);
        
        for (Player B: blueTeam.Players) {
            B.stop(ActorDirectionEnum.West);
            B.setHasBall(false);
        }
        blueTeam.Players[0].setLocation(800, 150);
        blueTeam.Players[1].setLocation(800, 450);
        blueTeam.Players[2].setLocation(1050, 150);
        blueTeam.Players[3].stop(ActorDirectionEnum.North,660, 340 );
        blueTeam.Players[3].setPosition(PlayerPositionEnum.InsideRight);
        setPlayerInControl(blueTeam.Players[3]);
        setPlayerWithBall(blueTeam.Players[3]);
        blueTeam.Players[4].stop(ActorDirectionEnum.East, 660, 290);
        blueTeam.Players[4].setPosition(PlayerPositionEnum.InsideLeft);
        blueTeam.Players[5].setLocation(1320, 330);
    }
    
    private void throwInPositions() {
        int x = ball.getX(), y = ball.getY();
        
        if (checkTime() == GameTimeEnum.FirstHalf){
            blueTeam.Players[0].setLocation(200, 150);
            blueTeam.Players[1].setLocation(200, 450);
            blueTeam.Players[2].setLocation(400, 335);
            if (y < 10) {
                blueTeam.Players[3].setLocation(x-50, y+50);
                redTeam.Players[3].setLocation(x+50, y+50);
            }
            else if (y >= 10) {
                blueTeam.Players[3].setLocation(x-50, y-50);
                redTeam.Players[3].setLocation(x+50, y-50);
            }
            blueTeam.Players[4].setLocation(660, 290);    
            blueTeam.Players[5].setLocation(90, 320);
            redTeam.Players[0].setLocation(800, 150);
            redTeam.Players[1].setLocation(800, 500);
            redTeam.Players[2].setLocation(1050, 100);
            redTeam.Players[4].setLocation(800, 340);
            redTeam.Players[5].setLocation(1320, 330);
    
            for (int i=0; i<6; i++) {
                redTeam.Players[i].getImageView().setScaleX(-1);
            }
        }
        else if (checkTime() == GameTimeEnum.SecondHalf){
            redTeam.Players[0].setLocation(200, 150);
            redTeam.Players[1].setLocation(200, 450);
            redTeam.Players[2].setLocation(400, 335);
            if (y < 10) {
                redTeam.Players[3].setLocation(x-50, y+50);
                blueTeam.Players[3].setLocation(x+50, y+50);
            }
            else if (y >= 10) {
                redTeam.Players[3].setLocation(x-50, y-50);
                blueTeam.Players[3].setLocation(x+50, y-50);
            }
            redTeam.Players[4].setLocation(660, 290);    
            redTeam.Players[5].setLocation(90, 320);
            blueTeam.Players[0].setLocation(800, 150);
            blueTeam.Players[1].setLocation(800, 500);
            blueTeam.Players[2].setLocation(1050, 100);
            blueTeam.Players[4].setLocation(800, 340);
            blueTeam.Players[5].setLocation(1320, 330);
            
            for (int i=0; i<6; i++) {
                redTeam.Players[i].getImageView().setScaleX(1);
            }
            
        }
    }
    
    //TODO which team which side this goal kick is 
    private void goalKickPositions() {
        //positions to take at a goal kick
        if (checkTime() == GameTimeEnum.FirstHalf){
            blueTeam.Players[0].setLocation(200, 150);
            blueTeam.Players[1].setLocation(200, 450);
            blueTeam.Players[2].setLocation(400, 335);
            blueTeam.Players[3].setLocation(660, 500);
            blueTeam.Players[4].setLocation(660, 100);    
            redTeam.Players[0].setLocation(800, 150);
            redTeam.Players[1].setLocation(800, 500);
            redTeam.Players[2].setLocation(1050, 100);
            redTeam.Players[3].setLocation(1050, 450);
            redTeam.Players[4].setLocation(800, 340);
            for (int i=0; i<6; i++) {
                redTeam.Players[i].getImageView().setScaleX(-1);
            }    
        }else if (checkTime() == GameTimeEnum.SecondHalf) {
            redTeam.Players[0].setLocation(200, 150);
            redTeam.Players[1].setLocation(200, 450);
            redTeam.Players[2].setLocation(400, 335);
            redTeam.Players[3].setLocation(660, 500);
            redTeam.Players[4].setLocation(660, 100);    
            blueTeam.Players[0].setLocation(800, 150);
            blueTeam.Players[1].setLocation(800, 500);
            blueTeam.Players[2].setLocation(1050, 100);
            blueTeam.Players[3].setLocation(1050, 450);
            blueTeam.Players[4].setLocation(800, 340);
        }
    }
    
    //AMER CODE: rename it 
    //private String whichTeamHaveBall(Player[] blueTeam, Player[] redTeam) {
    private void updateWhichTeamHaveBall() {
        
        whichTeamHaveBall = TeamKindEnum.NoTeam;
        for(Player P:bothTeams) {
            if (P.getHasBall()) {
                if (P.getPlayerTeamKind() == TeamKindEnum.RedTeam){
                    whichTeamHaveBall = TeamKindEnum.RedTeam;
                    break;
                }
                else{
                    whichTeamHaveBall = TeamKindEnum.BlueTeam;
                    break;
                }
           }
        }
        
        for(Player P:bothTeams) { //update all Player objects with new info about team who have the ball
            P.setTeamWithBall(whichTeamHaveBall);
        }
        ball.setWhichTeamHadBallLast(whichTeamHaveBall);

   }
    
    private void addToWorld(Actor actor){
        actors.add(actor);
        gui.addImageView(actor.getImageView());
    }
    
    private void removeActorFromWorld(Actor actor){
        actors.remove(actor);
        gui.removeImageView(actor.getImageView());        
    }
    
    private void checkForCollisions() { 
        /* collision checking, called in timeTick(). Note: each player has a boolean value called UnCollidable, where
        they can be made uncollidable for a few miliseconds to prevent passes and shots sticking to them
        */
        
        //AMER CODE: re write it in simpler way
        for (Player P:bothTeams){
            if(!P.getHasBall() && P.collidesWith(ball) && !P.getUnCollidable() && !ball.getUnCollidable()){
                if (P.getPlayerTeamKind() == TeamKindEnum.BlueTeam){
                    setPlayerInControl(P);
                }
                setPlayerWithBall(P);
                break; // because only one collide can accure in one TimeTick
            }
            if (P.getHasBall() && !P.getUnCollidable()) {
                travelWithBall(P);
                break;  // because only one collide can accure in one TimeTick
            }
        }

        //goal keeper save mode override UnCollidablility
        if (ball.getUnCollidable() && blueKeeper.getKeeperSave() && blueKeeper.collidesWith(ball)) {
            if (checkTime() == GameTimeEnum.FirstHalf){
                ball.setSpeedAndDirection(4, ActorDirectionEnum.NorthEast);
            }
            else if (checkTime() == GameTimeEnum.FirstHalf){
                ball.setSpeedAndDirection(4, ActorDirectionEnum.NorthWest);
            }
            blueKeeper.setKeeperSave(false);
            blueKeeper.setHasBall(false);
        }
        
    }
    
    private GameTimeEnum checkTime(){ //this proc return 0 if time isi over, 1 for first half, and 2 for 2nd half
        if (gui.gameTimeCounter <= 0 ){
            return GameTimeEnum.TimeOver;
        }
        else if (gui.gameTimeCounter >= (gui.TotalGameTime/2)){
            return GameTimeEnum.FirstHalf;
        }
        else {
            return GameTimeEnum.SecondHalf;
        }
    }
    
    
    private boolean checkForThrowIns() {
        //called in timeTick(), checks for throw ins
        if(ball.getY() <= 3 && ball.getX() > 15 && ball.getX() < 1325) {
            if(ball.getWhoHadBallLast() == TeamKindEnum.RedTeam) {
                gui.stopWorldTime();
                gui.stopGameTime();
                gui.showMenuButtonWithText("THROW-IN to blue team");
                ball.setLocation(ball.getX(), 5);
                throwInPositions();
                ball.gotoPlayer(blueTeam.Players[3]);
            }
            if(ball.getWhoHadBallLast() == TeamKindEnum.BlueTeam) {
                gui.stopWorldTime();
                gui.stopGameTime();
                gui.showMenuButtonWithText("THROW-IN to red team");
                ball.setLocation(ball.getX(), 5);
                throwInPositions();
                ball.gotoPlayer(redTeam.Players[3]);
            }
            return true;
        }
        else if(ball.getY() >= 670 && ball.getX() > 15 && ball.getX() < 1325) {
            if(ball.getWhoHadBallLast() == TeamKindEnum.RedTeam) {
                gui.stopWorldTime();
                gui.stopGameTime();
                gui.showMenuButtonWithText("THROW-IN to blue team");
                ball.setLocation(ball.getX(), 665);
                throwInPositions();
                ball.gotoPlayer(blueTeam.Players[3]);
            }
            else if(ball.getWhoHadBallLast() == TeamKindEnum.BlueTeam) {
                gui.stopWorldTime();
                gui.stopGameTime();
                gui.showMenuButtonWithText("THROW-IN to red team");
                ball.setLocation(ball.getX(), 665);
                throwInPositions();
                ball.gotoPlayer(redTeam.Players[3]);
            }
            return true;
        }
        else {return false;}
    }
    
    private boolean checkForGoalKicks() {
        //called in timeTick(), checks for goal kicks
        if (ball.getX() <= 15 && ball.getY() < 300 && 
            ball.getWhoHadBallLast() == TeamKindEnum.RedTeam) {
            
            gui.stopWorldTime();
            gui.stopGameTime();
            gui.showMenuButtonWithText("GOAL KICK");
            ball.setLocation(90, 220);
            blueKeeper.setLocation(88, 220);
            goalKickPositions();
            return true;
        }
        else if (ball.getX() <= 15 && ball.getY() > 380 &&
                 ball.getWhoHadBallLast() == TeamKindEnum.RedTeam) {
            
            gui.stopWorldTime();
            gui.stopGameTime();
            gui.showMenuButtonWithText("GOAL KICK");
            ball.setLocation(90, 400);
            blueKeeper.setLocation(88, 400);
            goalKickPositions();
            return true;
        }
        else if (ball.getX() >= 1325 && ball.getY() > 380 && 
                 ball.getWhoHadBallLast() == TeamKindEnum.BlueTeam) {
            gui.stopWorldTime();
            gui.stopGameTime();
            gui.showMenuButtonWithText("GOAL KICK");
            ball.setLocation(1240, 400);
            redTeam.Players[5].setLocation(1242, 400);
            goalKickPositions();
            return true;
        }
        else if (ball.getX() >= 1325 && ball.getY() < 300 &&
                 ball.getWhoHadBallLast() == TeamKindEnum.BlueTeam) {
            gui.stopWorldTime();
            gui.stopGameTime();
            gui.showMenuButtonWithText("GOAL KICK");
            ball.setLocation(1240, 220);
            redTeam.Players[5].setLocation(1242, 220);
            goalKickPositions();
            return true;
        }
        else {return false;}
    }
    
    private boolean checkForRedGoals() {
        //called in timeTick(), checks for goals scored by red team (ie. in blue's goal)
        if ((checkTime() == GameTimeEnum.FirstHalf &&
            ball.getX()<15 && ball.getY()>=300 && ball.getY()<=380 ||
            (checkTime() == GameTimeEnum.SecondHalf &&
            ball.getX()>1325 && ball.getY()>=300 && ball.getY()<=380) ))
        {
            redGoals++;
            LastGoalTeam=TeamKindEnum.RedTeam;
            updateScoreBoard();
            
            gui.stopWorldTime();
            gui.stopGameTime();
            
            gui.showMenuButtonWithText("GOAL!!!");
            setPlayerInStartPositions();
            playSFX(GameSoundFXEnum.Goal);
            
            return true;
        }
        else {return false;}
    }
    
    private boolean checkForBlueGoals() {
        //called in timeTick(), checks for goals scored by blue team (ie. in red's goal)
        if ((checkTime() == GameTimeEnum.FirstHalf &&
            ball.getX()>1325 && ball.getY()>=300 && ball.getY()<=380) ||
            (checkTime() == GameTimeEnum.SecondHalf &&
            ball.getX()<15 && ball.getY()>=300 && ball.getY()<=380))
        {
                blueGoals++;
                LastGoalTeam=TeamKindEnum.BlueTeam;
                updateScoreBoard();
                gui.stopWorldTime();
                gui.stopGameTime();

                gui.showMenuButtonWithText("GOAL!!!");
                setPlayerInStartPositions();
                playSFX(GameSoundFXEnum.Goal);

                return true;
            }
        else {return false;}
    }
    
    private void stopAllPlayers(){
        for (Player P:bothTeams){
            P.stop();
        }
    }
    
    private void stopAllActors(){
        for (Actor A:actors){
            A.stop();
        }
    }
        
    private void updateNearestTeamMate() {
        /*VERY IMPORTANT! Checks for nearest team mate to controlledPlayer. Called in timeTick() so constantly updated.
        first loop finds nearest team mate to controlledPlayer and saves him in variable nearestTeamMate. second loop
        sends position of nearestTeamMate (eg. "CentreMidfield") as String to nearestTeamMate variable of each player,
        so effectively each player knows if he is the nearestTeamMate (if his position matches it) */
        double dist = 2000; //biggest posibble value

        for (Player B:blueTeam.Players){
                if (B.getUnderControl() == false && B.getDistanceFromActor(controlledPlayer) < dist) {
                    dist = B.getDistanceFromActor(controlledPlayer);
                    //System.out.println("Distance is: " + dist);
                    nearestTeamMate = B;
                }            
        }
        
        for (Player B:blueTeam.Players) {
            B.setNearestTeamMate(nearestTeamMate);
        }
    }
    
    
    private void updateNearestRedPlayerToBall() {
        /*This is something I was trying to get the computer controlled guys to approach the ball if they were
        the nearest to it when defending. Didn't work but a nearestToBall calculation can be useful for later, so it
        stays in the code
        */
        double dist = 2000;
        for (Player R:redTeam.Players) {
            
            if (R.getDistanceFromActor(ball) < dist) {
                dist = R.getDistanceFromActor(ball);
                nearestRedPlayerToBall = R;        
            }
        }
        //TODO update this, there is a comment from Christopher that is not in use anyway in side Player class
        //nearestRedPlayerToBall.setNearestDefender(nearestRedPlayerToBall.getPosition());
    }
    
    private void trapBall(Player player) {
        //AMER CODE: its fucken not working 
        //ball.setLocation(player.getPerfectBallX(), player.getPerfectBallY());
        //old code 
        ball.setLocation(player.getX(), player.getY());
        travelWithBall(player);
    }
    
    private void travelWithBall(Player player) {
        ball.TravelWithPlayer(player);
    }
    
    /*the above two methods are called in checkForCollisions() and are what each player does when they collide with ball.
    can probably be combined into one, but i had so much trouble trying to make the players keep the ball when they
    received it, i left it as two because it worked this way.*/
    
       
    private void computerPassingAndShooting() {
        /*at the moment, this controls how the computer controlled team attacks, by determining where they pass to and
        shoot at. each player has a "pass" boolean and a "shoot" boolean, functioning as a sort of switch. as you will
        see in the player class, a method there called autoPass() or autoShoot() sets the values to true at the right
        time and in this method the game interprets this into an effect on the ball. i tried to create passing to
        nearest team mate for the computer, but this actually works out shit because if Player 1 is closest to Player 2, 
        this of course also means that Player 2 is closest to Player 1, so they just passed the ball between the same
        two guys all the time. so instead i wrote this method to put in timeTick() so players pass to specific team mates.
        at the moment, full backs pass to wingers, wingers pass to the striker. 
        */
        for (ComputerPlayer R:redTeam.Players){
            
        }
        
        for (int i=0; i<5; i++) {
            if(redTeam.Players[0].getPass() == true) {                
                ball.Pass(redTeam.Players[4].getX(), redTeam.Players[4].getY());
                redTeam.Players[0].setPass(false);
            }
            
            if(redTeam.Players[1].getPass() == true) {           
                ball.Pass(redTeam.Players[4].getX(), redTeam.Players[4].getY());
                redTeam.Players[1].setPass(false);
            }
            if(redTeam.Players[2].getPass() == true) {               
                ball.Pass(redTeam.Players[0].getX(), redTeam.Players[0].getY());
                redTeam.Players[2].setPass(false);
            }
            if(redTeam.Players[3].getPass() == true) {                
                ball.Pass(redTeam.Players[1].getX(), redTeam.Players[1].getY());
                redTeam.Players[3].setPass(false);
            }
            if(redTeam.Players[4].getPass() == true) {
                ball.Pass(redTeam.Players[0].getX(), redTeam.Players[0].getY());
                redTeam.Players[4].setPass(false);
            }
            if(redTeam.Players[4].getShoot() == true) {   
                if (checkTime()== GameTimeEnum.FirstHalf){
                    ball.highShotFirstHalf();
                }
                else {
                    ball.highShotSecondHalf();
                }
                ball.setHighShot(true);
                redTeam.Players[4].setShoot(false);
            }
            if(redTeam.Players[5].getPass() == true) {
                ball.Pass(redTeam.Players[2].getX(), redTeam.Players[2].getY());
                redTeam.Players[5].setPass(false);
            }
        
        }
    }
    
//    AMER CODE: no need replaced with a new one down    
//    private Player controlledPlayer(Player[] blueTeam.Players) {
//        //this method finds which player is currently under user control and returns him
//        //as controlledPlayer, which we can use as a variable for the key inputs
//        for(int i=0; i<6; i++) {
//            if(blueTeam[i].getUnderControl() == true) {
//                controlledPlayer = blueTeam[i];
//            }
//        }
//        return controlledPlayer;
//    }
     
    //AMER CODE: this is a replacer for the above proc
    private void updateControlledPlayer() {
    
        for(Player P :blueTeam.Players) {
            if(P.getUnderControl()) {
                controlledPlayer = P;
                return;
            }
        }
        //if reach here then there is a mistake and no one under control, fix under
        blueTeam.Players[0].setUnderControl(true);
        controlledPlayer = blueTeam.Players[0];
    }
    
    private final void createActors(){
        System.out.println("Creating actors...");
        
        //init Teams
        redTeam = new ComputerTeam();
        blueTeam = new HumanTeam();
        
        ball = new Ball();
        ball.setLocation(665, 335);
        //ball.setCollisionRadius(1);
        ball.setCollisionRadius(0.5);
        addToWorld(ball);
        
        
        addPlayers();
        //setPlayerInStartPositions();
        firstHalfPositionsStartByBlueTeam();
                
    }
    
    private void stopBothTeamsHavingBall() {

        for (Player P:bothTeams) {
            if (P.collidesWith(ball) == false && P.getHasBall() == true) {
                P.setHasBall(false);
            } 
        }

    }    
    private void stopPeopleHavingBall(Player[] team) {
        /*I created this method to fix a bug I was getting where some players' hasBall() boolean value was stuck on true
        even after they had lost the ball, so the ball was sticking under their control even from a great distance, 
        Matrix/Jedi style. so putting this in timeTick() makes sure that everyone's hasBall() value is false if they're
        not actually near the ball.
        */
        //AMER CODE: UPDATE LOOP STYLE
        for (Player P:team) {
            if (P.collidesWith(ball) == false && P.getHasBall() == true) {
                P.setHasBall(false);
            } 
        }
//OLD CODE
//        int i = 0;
//        
//        while(i < team.length) {
//        if (team[i].collidesWith(ball) == false && team[i].getHasBall() == true) {
//            team[i].setHasBall(false);
//        } 
//        i++;
//        }
    }
    
    private int hideBoardCounter;
    public void timeTick(){
        
        if (checkTime() == GameTimeEnum.TimeOver){
            gameOver();
            return;
        }
        else if (checkTime() == GameTimeEnum.SecondHalf && CurrentHalf == GameTimeEnum.FirstHalf){
            setGameTimeSecondHalf();
            return;
        }
        
        
        //First all actors do their default actions.
        for (Actor actor : actors) {
            //If an actor has some speed, it will automatically move here...
            actor.timeTick();
        }
        /*all the methods included here are the stuff that needs to be done every time tick - ie. continuously, while
        the game is running. needs to keep controlledPlayer and nearestTeamMate up to date, keep scoreboard up to date
        always be checking for throw ins and such, and sending the values for where the ball is to each player.
        */
        //controlledPlayer(blueTeam);
        
        updateControlledPlayer();
        updateNearestTeamMate();
        updateNearestRedPlayerToBall();    
        //whichTeamHaveBall(blueTeam, redTeam);
        updateWhichTeamHaveBall();
        updateScoreBoard();
        checkForCollisions();
        giveControlToPlayerWithBall(); //this will give control to the player with ball but without control
        //AMER CODE: Because only one is posible then no need to check the other
        if (checkForThrowIns()){
            gui.showThrowInBoard();
            hideBoardCounter = 0;
        }
        else if (checkForRedGoals()){
            gui.showGoalBoard();
            hideBoardCounter = 0;
        }
        else if (checkForBlueGoals()){
            gui.showGoalBoard();
            hideBoardCounter = 0;
        }
        else if (checkForGoalKicks()){
            gui.showGoalKickBoard();
            hideBoardCounter = 0;
        }
        else {
            hideBoardCounter++;
            if (hideBoardCounter >= 20){ //you can adjast this number to be how long the board will dispaly after resuming the game
                hideBoardCounter = 0;
                gui.hideThrowInBoard();
                gui.hideGoalBoard();
                gui.hideGoalKickBoard();
                gui.hideSecondHalfBoard();
                gui.hideGameOverBoard();
            }
            
        }
        stopBothTeamsHavingBall();
        //stopPeopleHavingBall(blueTeam);
        //stopPeopleHavingBall(redTeam);
        
        if (whichTeamHaveBall == TeamKindEnum.BlueTeam){
            //do something for to move other human player to ball
        }
        else if (whichTeamHaveBall == TeamKindEnum.RedTeam){
            computerPassingAndShooting();           
        }
        //AMER CODE: NO NEED FOR THIS aproch if we just assign object ball to all player then all of them will know the current location
        /*the for loop before sends the current x and y coordinate of the ball through to the BallX and BallY variable
        of each player, so they know where the ball is - you can say it is the code version of the players' eyes!
        */
//        for (int i=0; i<6; i++) {
//            redTeam[i].setballX(ball.getX());
//            redTeam[i].setballY(ball.getY());
//            blueTeam[i].setballX(ball.getX());
//            blueTeam[i].setballY(ball.getY());
//        }
        
//        if(whichTeamHaveBall.contains("blue")) {
//            for(int i=0; i<6; i++) {
//                if(blueTeam[i].getHasBall() == false && blueTeam[i].getUnderControl() == true) {
//                    blueTeam[i].setUnderControl(false);
//                }
//            } //fixes the bug where more than one player shows as under control at same time
//            for(int i=0; i<6; i++) {
//                redTeam[i].setTeamWithBall(whichTeamHaveBall);
//            }
//            ball.setWhoHadBallLast("blue");
//        }
//        else if(whichTeamHaveBall.contains("red")) {
//            if(controlledPlayer.getUnderControl() == false){
//                controlledPlayer.setUnderControl(true); //fixes bug where you can't switch control while defending
//            }
//            
//            for(int i=0; i<6; i++) {
//                redTeam[i].setTeamWithBall(whichTeamHaveBall);
//            }
//            computerPassingAndShooting();           
//            ball.setWhoHadBallLast("red");
//        }
        
        /*the two loops above ensure that control is always with the guy who has the ball, if the blue team have the ball,
        and they send the information about which team has the ball to the whichTeamHaveBall String variable in each 
        player, so they know if their team has the ball and whether to attack or defend
        */
                
        //Then, you can add more code here...
    }//end of timeTick//end of timeTick
    
    private void updateScoreBoard(){
        gui.scoreBoard.setText("Blue: " + blueGoals + "\n" +
                               "Red: " + redGoals + "\n" + 
                               "Time: " + gui.gameTimeCounter + "/" + gui.TotalGameTime + " : " + getGameTimeName() );
    }
    
    private String getGameTimeName(){
        switch (checkTime()){
            case FirstHalf:
                return "First Half";
            case SecondHalf:
                return "Second Half";
            case TimeOver:
                return "Time Over";
        }
        return "";
    }
    //AMER CODE: START
//    public void keyClick(KeyCode id){
//        if (id == KeyCode.TAB){
//       
//            NextPlayerInControl();
//        }
//    }
    
    private void setPlayerInControl(Player P){

        for (Player B:blueTeam.Players){
            B.setUnderControl(false);
            B.updateImages();
        }

        P.setUnderControl(true);
        P.updateImages();
        controlledPlayer = P;
    }
    
    private int countForGCtPwB = 0; 
    private void giveControlToPlayerWithBall(){
        countForGCtPwB++;
        if (countForGCtPwB >= 15){ //run every 15 TimeTick , can be changed 
            countForGCtPwB = 0;
            for (Player B:blueTeam.Players){
                if (B.getHasBall() && !B.getUnderControl()) {
                    setPlayerInControl(B);
                    break;
                }
            }
                
        }
    }
    
    private void NextPlayerInControl(){

        
        if(blueTeam.Players[0].getUnderControl()){
            setPlayerInControl(blueTeam.Players[1]);
        }
        else if(blueTeam.Players[1].getUnderControl()){
            setPlayerInControl(blueTeam.Players[2]);
        }
        else if(blueTeam.Players[2].getUnderControl()){
            setPlayerInControl(blueTeam.Players[3]);
        }
        else if(blueTeam.Players[3].getUnderControl()){
            setPlayerInControl(blueTeam.Players[4]);
        }
        else if(blueTeam.Players[4].getUnderControl()){
            setPlayerInControl(blueTeam.Players[5]);
        }
        else if(blueTeam.Players[5].getUnderControl()){
            setPlayerInControl(blueTeam.Players[0]);
        }
        else { //there is must be a mistake then at least one must be under control so we will assigen player 0
            setPlayerInControl(blueTeam.Players[0]);
        }
    }
    //AMER CODE: END
    
    public void keyPressed(KeyCode id){
        System.out.println("You pressed key: "+id.toString());
        if (id == KeyCode.UP){
            //What should happen if user presses ARROW-UP?
            //AMER CODE: i commet this so the play er can go dignal 
            //controlledPlayer.setSpeedX(0);
            controlledPlayer.setSpeedY(-5);
        }
        else if (id == KeyCode.DOWN) {
            //AMER CODE: i commet this so the play er can go dignal 
            //controlledPlayer.setSpeedX(0);
            controlledPlayer.setSpeedY(5);
        }
        else if (id == KeyCode.LEFT) {
            controlledPlayer.setSpeedX(-5);
            //AMER CODE: i commet this so the play er can go dignal 
            //controlledPlayer.setSpeedY(0);
        }
        else if (id == KeyCode.RIGHT) {
            controlledPlayer.setSpeedX(5);
            //AMER CODE: i commet this so the play er can go dignal 
            //controlledPlayer.setSpeedY(0);
        }
        else if (id == KeyCode.A){
            if (controlledPlayer.getHasBall() == true) {
                controlledPlayer.setUnCollidable(true);
                if (checkTime()== GameTimeEnum.FirstHalf){
                    ball.lowShotFirstHalf();
                }
                else {
                    ball.lowShotSecondHalf();
                }
                controlledPlayer.setHasBall(false);
            }
        }
        else if (id == KeyCode.S) {
            if (controlledPlayer.getHasBall() == true) {
                controlledPlayer.setUnCollidable(true);
                ball.Pass(nearestTeamMate.getX(), nearestTeamMate.getY());
                controlledPlayer.stop();
                controlledPlayer.setHasBall(false);
            }
        }
        
        else if (id == KeyCode.D) {
            if (controlledPlayer.getHasBall() == true) {
                controlledPlayer.setUnCollidable(true);
                ball.ownGoal();
                controlledPlayer.setHasBall(false);
            }
        }
        
        else if (id == KeyCode.SPACE) {
            if (controlledPlayer.getHasBall() == true) {
                controlledPlayer.setUnCollidable(true);
                if (checkTime()== GameTimeEnum.FirstHalf){
                    ball.highShotFirstHalf();
                }
                else {
                    ball.highShotSecondHalf();
                }
                ball.setHighShot(true);
                controlledPlayer.setHasBall(false);
                
            }
        }
        
        else if (id == KeyCode.W) {
            //AMER CODE:
            setPlayerInControl(blueKeeper);
            blueKeeper.setKeeperSave(true);
        }
        
        //AMER CODE:
        else if (id == KeyCode.ENTER) {
            
            gui.ButtomClickHere();
            ShowActors(); // in case there are hidden from intro.... should be change later
        }
        
//        else if (id == KeyCode.TAB){
//            //TAB tabs through the players in the team, changing control
//            //AMER CODE: BLCOK
//            for (int i=0; i<6; i++) {
//                if(blueTeam[i].getUnderControl() == true){
//                    if (i != 5){
//                        blueTeam[i].setUnderControl(false);
//                        blueTeam[i+1].setUnderControl(true);
//                    }
//                    else{
//                        blueTeam[0].setUnderControl(true);
//                        blueTeam[1].setUnderControl(false);
//                        blueTeam[2].setUnderControl(false);
//                        blueTeam[3].setUnderControl(false);
//                        blueTeam[4].setUnderControl(false);
//                        blueTeam[5].setUnderControl(false);
//                    }
//                    break;
//                }
//            }

            // OLD CODE
            //            for (int i=0; i<5; i++) {
            //                if(blueTeam[i].getUnderControl() == true){
            //                    blueTeam[i].setUnderControl(false);
            //                    blueTeam[i+1].setUnderControl(true);
            //                    break;
            //                }
            //                else if(blueTeam[5].getUnderControl() == true) {
            //                    blueTeam[5].setUnderControl(false);
            //                    blueTeam[0].setUnderControl(true);
            //                }
            //            }

                        //}
        
        //AMER CODE: debug KEY not to be use by user 
        
        else if (id == KeyCode.F10){
            setPlayerInStartPositions();
        }
        else if (id == KeyCode.F11){
            gui.gameTimeCounter=gui.TotalGameTime;
        }
        else if (id == KeyCode.F12){
            gui.gameTimeCounter -= 30;
        }
        //AMER CODE: no need to draw the actor it will be draw automatictly when it assigen a new speed value
        //controlledPlayer.setRotationBasedOnSpeed();
    }
    
    public void keyReleased(KeyCode id) {

        System.out.println("You released key: "+id.toString());
        if (id == KeyCode.UP){
            if (controlledPlayer.getSpeedY()<0)
                controlledPlayer.setSpeedY(0);
        }
        else if (id == KeyCode.DOWN){
            if (controlledPlayer.getSpeedY()>0)
                controlledPlayer.setSpeedY(0);
        }
        else if (id == KeyCode.LEFT){
            if (controlledPlayer.getSpeedX()<0)
                controlledPlayer.setSpeedX(0);
        }
        else if (id == KeyCode.RIGHT){
            if (controlledPlayer.getSpeedX()>0)
                controlledPlayer.setSpeedX(0);
        }
        else if (id == KeyCode.SPACE){
        }
        else if (id == KeyCode.TAB){
            NextPlayerInControl();
        }
        else if (id == KeyCode.P) {
            PausResumeGame();
        }
   }

    public void mousePressed(boolean leftMouse, int x, int y) {
        System.out.printf("Mouse clicked %s, x = %d, y = %d\n", leftMouse?"LEFT BUTTON":"OTHER BUTTON", x, y);
    }
    
    private boolean Pause = false;
    private void PausResumeGame(){
        
        Pause = !Pause;
        if (Pause){
            gui.stopWorldTime();
            gui.stopGameTime();
            gui.showPauseBoard();
        }
        else {
            gui.startWorldTime();
            gui.startGameTime();
            gui.hidePauseBoard();
        }
    }
    
    private void playSFX(GameSoundFXEnum SoundFX)
    {
        try
        {
            MediaPlayer Player;
            //SFX: Step 4: add new case for the the switch 
            switch (SoundFX){
                case Intro:
                    Player = new MediaPlayer(SFXIntro);
                    Player.play();
                    break;
                case GameOver:
                    Player = new MediaPlayer(SFXGameOver);
                    Player.play();
                    break;
                case Goal:
                    Player = new MediaPlayer(SFXGoal);
                    Player.play();
                    break;
            } 

        }
        catch (Exception e)
        {
            System.out.println( e.getMessage() );
            System.exit(0);
        }
       
    }
    
    private void HideActors(){
        
        for (Actor A:actors){
            A.getImageView().setVisible(false);
        }
    }

    private void ShowActors(){
        
        for (Actor A:actors){
            A.getImageView().setVisible(true);
        }
    }

//SFX: Step 5: just use it where ever you want a sound see example below
//playSoundFX(<Enum of the sound kind>)

}//end of Game class

    //AMER CODE: there is now 4 diffreant start postion methodss
    //public void startPositions(Player[] blueTeam, Player[] redTeam) {
//    private void setPlayerInStartPositions(String teamToStart) {
//
//        ball.stop();
//        ball.setLocation(665, 335);
//        if (teamToStart == "blue"){
//            setPlayerInStartPositions();
//        }
//        else {//assume it will be red 
//            setPlayerInStartPositions();
//        }
//            
//    }

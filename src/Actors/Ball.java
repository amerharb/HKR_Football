/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Actors;
import javafx.scene.image.Image;
import Actors.Team.TeamKindEnum;

/**
 *
 * @author Chris
 */
public class Ball extends Actor{
    
    private int rightGoalX = 1330;
    private int rightGoalY = 380;
    private int leftGoalX = 15;
    private int leftGoalY = 380;
    
    boolean highShot;
    boolean unCollidable = false;
    int highShotCounter = 0;
    TeamKindEnum teamWithLastTouch = TeamKindEnum.NoTeam;
    
    //Image image = new Image(("/img/"+"football.png"));


    public Ball() {
        super("Football.png",20);
    }
    
//    public Ball(String name, String filename, int size) {
//        super(name, filename, size);
//    }
//    
    @Override
    public void timeTick(){
        
        super.timeTick(); 
        if (highShot == true) {
            highShotAnimation();
        }
    }


    public void lowShotFirstHalf() {
        setSpeedX((rightGoalX-getX())/20);
        setSpeedY((rightGoalY-getY())/20);
    }
    
    public void lowShotSecondHalf() {
        setSpeedX((leftGoalX-getX())/20);
        setSpeedY((leftGoalY-getY())/20);
    }
    
    public void highShotFirstHalf() {
        if(teamWithLastTouch == TeamKindEnum.RedTeam) {
            setSpeedX((getX()-300 -getX())/25);
            setSpeedY((330-getY())/25);
        }
        else if(teamWithLastTouch == TeamKindEnum.BlueTeam) {
            setSpeedX((getX()+300 -getX())/25);
            setSpeedY((330-getY())/25);
        }
        
    }
    
    public void highShotSecondHalf() {
        if(teamWithLastTouch == TeamKindEnum.RedTeam) {
            setSpeedX((getX()+300 -getX())/25);
            setSpeedY((330-getY())/25);
        }
        else if(teamWithLastTouch == TeamKindEnum.BlueTeam) {
            setSpeedX((getX()-300 -getX())/25);
            setSpeedY((330-getY())/25);
        }
        
    }
    
    public void highShotAnimation() {
        unCollidable = true;
        highShotCounter++;
        if (highShotCounter == 3) {
            getImageView().setScaleX(1.1);
            getImageView().setScaleY(1.1);
        }
        if (highShotCounter == 6) {
            getImageView().setScaleX(1.2);
            getImageView().setScaleY(1.2);
        }
        if (highShotCounter == 9) {
            getImageView().setScaleX(1.3);
            getImageView().setScaleY(1.3);
        }
        if (highShotCounter == 12) {
            getImageView().setScaleX(1.4);
            getImageView().setScaleY(1.4);
        }
        if (highShotCounter == 15) {
            getImageView().setScaleX(1.5);
            getImageView().setScaleY(1.5);
        }
        if (highShotCounter == 18) {
            getImageView().setScaleX(1.4);
            getImageView().setScaleY(1.4);
        }
        if (highShotCounter == 21) {
            getImageView().setScaleX(1.3);
            getImageView().setScaleY(1.3);
        }
        if (highShotCounter == 24) {
            getImageView().setScaleX(1.2);
            getImageView().setScaleY(1.2);
        }
        if (highShotCounter == 27) {
            getImageView().setScaleX(1.1);
            getImageView().setScaleY(1.1);
        }
        if (highShotCounter == 30) {
            getImageView().setScaleX(1.0);
            getImageView().setScaleY(1.0);
            highShotCounter = 0;
            highShot = false;
            unCollidable = false;
        }   
    }
    
    public void ownGoal() {
        setSpeedX((20-getX())/20);
        setSpeedY((330-getY())/20);
    }
    
    public void Pass(int x, int y) {
        setSpeedX((x-getX())/10);
        setSpeedY((y-getY())/10);
    }
    
   
    
    public void setWhichTeamHadBallLast (TeamKindEnum teamWithLastTouch) {
        this.teamWithLastTouch = teamWithLastTouch;
    }
    
    /*method above lets the ball know who had it last, sent through from
    timeTick() method in Game. Used to determine which team a throw in or
    goal kick is awarded to.
    */
    
    public TeamKindEnum getWhoHadBallLast () {
        return teamWithLastTouch;
    }
    
    public void setHighShot(boolean highShot) {
        this.highShot = highShot;
    }
    
    public boolean getHighShot() {
        return highShot;
    }
    
    public void setUnCollidable(boolean unCollidable) {
        this.unCollidable = unCollidable;
    }
        
    public boolean getUnCollidable() {
        return unCollidable;
    }

}

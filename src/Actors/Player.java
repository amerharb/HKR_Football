/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Actors;

import Actors.Team.TeamKindEnum;
import GameFrameworkJavaFX.Game;
///import javafx.scene.Parent;
import javafx.scene.image.Image;

/**
 *
 * @author anc
 */
public class Player extends Actor{

    /**
     * @return the PlayerDirection
     */
    
    //AMER CODE: these should be private, well no these should not be array at all
    //old code
    //Image[] blueTeamimages = new Image[4];
    //Image[] redTeamimages = new Image[6];
    //new code
    private Image bluePlayerImage;
    private Image blueUndercontrolPlayerImage;
    private Image blueNearestPlayerImage;
    
    private Image redPlayerImage;
    

    private boolean hasBall; //does this player have the ball or not
    private boolean underControl; //is this player under user control or not
    
    //AMER CODE: this should be object
    private Player nearestTeamMate;
    //private String nearestTeamMate = ""; 
    /* here we store the position name (eg. "RightBack") of the nearest team
    mate for the user controlled player. this way, each player knows if he is the current nearest team mate, and what
    his image should be (yellow highlight or not). information is sent through from getNearestMan() method in Game.
    */
    //TODO: change it to object Player
    private String nearestDefender = ""; //was planning to use this field for redTeam, not being used at the moment

    
    private boolean UnCollidable; /* if player is uncollidable the ball will not stick to them - they are not included
    in the condition for collision checking in the checkForCollisions() method in Game. this was necessary to create
    a few miliseconds of immunity from collision so passes and shots get away from players properly */
    //private boolean onRedTeam; //player needs to know which team he's on
    private boolean pass; //when true, Game will make this player pass, with computerPassingAndShooting() method
    private boolean shoot; //when true, Game will make this player shoot, with computerPassingAndShooting() method
    private TeamKindEnum teamWithBall = TeamKindEnum.NoTeam; //brought in from whichTeamHaveBall() method in Game, so players know
    private String position = ""; //position player is currently playing, defined in startPositions() method in Game
    //AMER CODE: NO NEED i user ball object instead
    //private int ballX; //the current x coordinate of the ball, sent through from Game's timeTick();
    //private int ballY; //the current x coordinate of the ball, sent through from Game's timeTick();
    private Ball ball;
    
    private int collidabilitycounter = 0; //a counter used in the restoreCollidability() method below
    private int passcounter = 0; //a counter used in the autoPass() method below
    private int shotcounter = 0; //a counter used in the autoShoot() method below
    private Team PlayerTeam;

    private Game.GameTimeEnum GameTime = Game.GameTimeEnum.FirstHalf;
    
    public Player(Team PlayerTeam){
        //I wish if this code work but java said constructor must be the first
        super("PlayerRed.png", 35);
        
//        ?(TeamKind == TeamKindEnum.BlueTeam):super("Player", "PlayerBlue.png", 32),super("Player", "PlayerRed.png", 32);
//        if (TeamKind == TeamKindEnum.BlueTeam){
//            super("Player", "PlayerBlue.png", 32);
//        }    
//        else{
//            super("Player", "PlayerRed.png", 32);
//        }
        
        //super("PlayerRed.png", 35);
                
        bluePlayerImage = new Image(("/img/"+"PlayerBlue.png"));
        blueUndercontrolPlayerImage = new Image(("/img/"+"PlayerUnderControl.png"));
        blueNearestPlayerImage = new Image(("/img/"+"PlayerNearest.png"));
        
        redPlayerImage = new Image(("/img/"+"PlayerRed.png"));
        
    }
    
    @Override
    public void timeTick(){
        /*
        Like the Game timeTick(), this contains the stuff that should be done continuously for each player. updateImages
        makes sure each player's image is the right one based on his status. redTeamAI controls red team movements,
        blueTeamAI is supposed to do the same for your blue team mates, but isn't working at the moment for some reason
        I haven't figured out yet.
        */
        super.timeTick(); 
        updateImages();
        if (UnCollidable == true) {
            restoreCollidability();
        }
        if (getPlayerTeamKind() == TeamKindEnum.RedTeam) {
            redTeamAI();
        }
        if (getPlayerTeamKind() == TeamKindEnum.BlueTeam) {
            blueTeamAI();
        }
//        if (keeperSave == true) {
//            switchOffSave();
//        }
        
        
    }

    /*
    Most of the methods below should be self-explanatory. There are methods for each position on each team, for defending
    and attacking, to describe what they should do in each case. For example, if the red team's RightBack and LeftBack 
    are attacking, they run with the ball to x=800 on the pitch, then after that they try to pass. You can adjust the
    X and Y parameters to change when they pass or when they shoot. At the moment, only the striker is capable of shooting.
    The autoPass() and autoShoot() method wait for a few miliseconds (nearly one second) and then switch the pass boolean
    to true, which the Game then interprets in computerPassingAndShooting(). The redTeamAI() method checks which team
    currently has the ball, then specifies either attack or defence methods for each position. The blueTeamAI() should 
    do the same for your blue team mates, but is currently not working due to some bug I haven't discovered yet.
    
    The key point here is that each player - especially on the redTeam - should now be fairly "smart" thanks to the 
    booleans and Strings sent through to them from the methods in Game's timeTick(). They know where the ball is at any
    time, thanks to BallX and BallY variables, they know what team they are on thanks to onRedTeam, they know which 
    team currently has the ball thanks to teamWithBall, they know what position they are playing thanks to position, and
    they know if they are the current nearestTeamMate by doing a comparison between the string value in nearestTeamMate
    and the string value in position - if they match, player knows HE is nearestTeamMate, and can "switch on" his yellow
    highlight image thanks to updateImages() method.
    */
    
    //AMER CODE: 1 line
    //@Override
    //public void setRotationBasedOnSpeed(){
        //AMER CODE: 1 line
        //super.setRotationBasedOnSpeed();
        
        //AMER CODE: BLOCK old code has been commited down
/*        if (getSpeedY()<0 && getSpeedX()==0){ //going North
            PlayerDirection = PlayerDirectionEnum.North;
            getImageView().setRotate(270);
            getImageView().setScaleX(1);
        }
        else if (getSpeedY()<0 && getSpeedX()>0){ //going NorthEast
            PlayerDirection = PlayerDirectionEnum.NorthEast;
            getImageView().setRotate(315);
            getImageView().setScaleX(1);
        }
        else if (getSpeedY()==0 && getSpeedX()>0){ //going East
            PlayerDirection = PlayerDirectionEnum.East;
            getImageView().setRotate(0);
            getImageView().setScaleX(1);
        }
        else if (getSpeedY()>0 && getSpeedX()>0){ //going South East
            PlayerDirection = PlayerDirectionEnum.SouthEast;
            getImageView().setRotate(45);
            getImageView().setScaleX(1);
        }
        else if (getSpeedY()>0 && getSpeedX()==0){ //going South
            PlayerDirection = PlayerDirectionEnum.South;
            getImageView().setRotate(90);
            getImageView().setScaleX(1);
        }
        else if (getSpeedY()>0 && getSpeedX()<0){ //going SouthWest
            PlayerDirection= PlayerDirectionEnum.SouthWest;
            getImageView().setRotate(315);
            getImageView().setScaleX(-1);
        }
        else if (getSpeedY()==0 && getSpeedX()<0){ //going West
            PlayerDirection= PlayerDirectionEnum.West;
            getImageView().setRotate(0);
            getImageView().setScaleX(-1);
        }
        else if (getSpeedY()<0 && getSpeedX()<0){ //going NorthWest
            PlayerDirection= PlayerDirectionEnum.NorthWest;
            getImageView().setRotate(45);
            getImageView().setScaleX(-1);
        }
*/
/* old code
        if (getSpeedY()>0){
            getImageView().setRotate(90);
            getImageView().setScaleX(1);
            
        }
        else if (getSpeedY()<0){
            getImageView().setRotate(270);
            getImageView().setScaleX(1);
        }
        else if (getSpeedX()>0){
            getImageView().setRotate(0);
            getImageView().setScaleX(1);
        }
        else if (getSpeedX()<0){
            getImageView().setRotate(0);
            getImageView().setScaleX(-1);
        }
        */
    //}
    
    //AMER CODE: START
    public int getPerfectBallX(){
        
        int PerfectX = 0;
        int Xadj = 20; // how far the ball will be located from the player
        
        switch(getActorDirection()){
            case North:
                PerfectX = getX();
                break;
            case NorthEast:
                PerfectX = getX() + Xadj;
                break;
            case East:
                PerfectX = getX() + Xadj;
                break;
            case SouthEast:
                PerfectX = getX() + Xadj;
                break;
            case South:
                PerfectX = getX();
                break;
            case SouthWest:
                PerfectX = getX() - Xadj;
                break;
            case West:
                PerfectX = getX() - Xadj;
                break;
            case NorthWest:
                PerfectX = getX() - Xadj;
                break;
        }
        
        return PerfectX;
    }
    
    public int getPerfectBallY(){
        
        int PerfectY = 0;
        int Yadj = 20; // how far the ball will be located from the player
        
        switch(getActorDirection()){
            case North:
                PerfectY = getY() - Yadj;
                break;
            case NorthEast:
                PerfectY = getY() - Yadj;
                break;
            case East:
                PerfectY = getY();
                break;
            case SouthEast:
                PerfectY = getY() + Yadj;
                break;
            case South:
                PerfectY = getY()+ Yadj;
                break;
            case SouthWest:
                PerfectY = getY() + Yadj;
                break;
            case West:
                PerfectY = getY();
                break;
            case NorthWest:
                PerfectY = getY() - Yadj;
                break;
        }
        return PerfectY;
    }
    //AMER CODE: END
    
    //AMER CODE: redocration and put the new images variable instead of array images
    public void updateImages() {
        if (getPlayerTeamKind() == TeamKindEnum.BlueTeam) { //will only affect blue team, red team images never need to change
            if(underControl) {
                if(!position.contains("Keeper")) {
                    updateImage(blueUndercontrolPlayerImage);
                }
            }
            else { //player not in Control
                if(!position.contains("Keeper")) {//not a keeper
                    if (isNearst() && isMyTeamHaveBall()){
                        updateImage(blueNearestPlayerImage);
                    }
                    else{ // not nearest team mate
                        updateImage(bluePlayerImage);
                    }
                }
            }
        }
    }
    
    public boolean isNearst(){ //return true in case this object is the neast to the player under control
        if (nearestTeamMate == this){
            return true;
        } else{return false;}
    }
    
    public boolean isMyTeamHaveBall(){
        
        if (teamWithBall == PlayerTeam.getTeamKind()){
            return true;
        } else{return false;}
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public String getPosition() {
        return position;
    }
    
    //AMER CODE: new object 
    public void setBall(Ball ball){
        this.ball = ball;
    }
    
//    public void setballX(int ballX) {
//        this.ballX = ballX;
//    }
//    
//    public void setballY(int ballY) {
//        this.ballY = ballY;
//    }
    
    public void rightBackDefending() {
        
        if (ball.getX() < 500 && ball.getY() < 335) {
            setSpeedX((ball.getX()+600 - getX())/20);
            setSpeedY((ball.getY() - getY())/20);
        }
        else if (ball.getX() < 500 && ball.getY() >= 335) {
            setSpeedX((ball.getX()+600 - getX())/20);
            setSpeedY((250 - getY())/20);
        }
        else if (ball.getX() >= 500 && ball.getX() < 900 && ball.getY() < 335) {
            setSpeedX((ball.getX()+250 - getX())/20);
            setSpeedY((ball.getY() - getY())/20);
        }
        else if (ball.getX() >= 500 && ball.getX() < 900 && ball.getY() >= 335) {
            setSpeedX((ball.getX()+250 - getX())/20);
            setSpeedY((250 - getY())/20);
        }
        else if (ball.getX() > 900 && ball.getY() < 335) {
            setSpeedX((ball.getX() - getX())/30);
            setSpeedY((ball.getY() - getY())/30);
        }
        else if (ball.getX() > 900 && ball.getY() >= 335) {
            setSpeedX((ball.getX() - getX())/30);
            setSpeedY((250 - getY())/20);
        }
    }
    
    public void rightBackAttacking() {
        if (hasBall == true) {
            if (getX() > 800) {
                setSpeedY((150 - getY())/20);
                setSpeedX(-3);
            }
            else if(getX() <= 800) {
                setSpeedX(-1);
                setSpeedY((150 - getY())/20);
                autoPass();
            }
        }
        else if (hasBall == false) {
            if (getX() > 665) {
                setSpeedX(-3);
                setSpeedY((150 - getY())/20);
            }
            else if (getX() <= 665) {
                setSpeedX(-1);
                setSpeedY(0);    
            }
        }
    }
    
    public void leftBackDefending() {
        if (ball.getX() < 500 && ball.getY() < 335) {
            setSpeedX((ball.getX()+600 - getX())/20);
            setSpeedY((410 - getY())/20);
        }
        else if (ball.getX() < 500 && ball.getY() >= 335) {
            setSpeedX((ball.getX()+600 - getX())/20);
            setSpeedY((ball.getY() - getY())/20);
        }
        else if (ball.getX() >= 500 && ball.getX() < 900 && ball.getY() < 335) {
            setSpeedX((ball.getX()+250 - getX())/20);
            setSpeedY((410 - getY())/20);
        }
        else if (ball.getX() >= 500 && ball.getX() < 900 && ball.getY() >= 335) {
            setSpeedX((ball.getX()+250 - getX())/20);
            setSpeedY((ball.getY() - getY())/20);
        }
        else if (ball.getX() > 900 && ball.getY() < 335) {
            setSpeedX((ball.getX() - getX())/30);
            setSpeedY((410 - getY())/30);
        }
        else if (ball.getX() > 900 && ball.getY() >= 335) {
            setSpeedX((ball.getX() - getX())/30);
            setSpeedY((ball.getY() - getY())/20);
        }
            
    }
    
    public void leftBackAttacking() {
        if (hasBall == true) {
            if (getX() > 800) {
                setSpeedY((450 - getY())/20);
                setSpeedX(-3);
            }
            else if(getX() <= 800) {
                setSpeedX(-1);
                setSpeedY((450 - getY())/20);
                autoPass();
            }
        }
        else if (hasBall == false) {
            if (getX() > 665) {
                setSpeedX(-3);
                setSpeedY((450 - getY())/20);
            }
            else if (getX() <= 665) {
                setSpeedX(-1);
                setSpeedY(0);    
            }
        }
    }
    
      public void rightMidfielderDefending() {
        if (ball.getX() < 500 && ball.getY() < 335) {
            setSpeedX((ball.getX()+250 - getX())/20);
            setSpeedY((ball.getY() - getY())/20);
        }
        else if (ball.getX() < 500 && ball.getY() >= 335) {
            setSpeedX((ball.getX()+250 - getX())/20);
            setSpeedY((250 - getY())/20);
        }
        else if (ball.getX() >= 500 && ball.getX() < 900 && ball.getY() < 335) {
            setSpeedX((ball.getX() - getX())/20);
            setSpeedY((ball.getY() - getY())/20);
        }
        else if (ball.getX() >= 500 && ball.getX() < 900 && ball.getY() >= 335) {
            setSpeedX((ball.getX()+50 - getX())/20);
            setSpeedY((250 - getY())/20);
        }
        else if (ball.getX() > 900 && ball.getY() < 335) {
            setSpeedX((ball.getX()-200 - getX())/30);
            setSpeedY((ball.getY() - getY())/30);
        }
        else if (ball.getX() > 900 && ball.getY() >= 335) {
            setSpeedX((ball.getX()-200 - getX())/30);
            setSpeedY((250 - getY())/20);
        }
    }
    
    public void rightMidfielderAttacking() {
        if (hasBall == true) {
            if (getX() > 350) {
                setSpeedY((100 - getY())/20);
                setSpeedX(-3);
            }
            else if(getX() <= 350) {
                autoPass();
            }
        }
        else if (hasBall == false) {
            if (getX() > 350) {
                setSpeedX(-3);
                setSpeedY((150 - getY())/20);
            }
            else if (getX() <= 350) {
                setSpeedX(-1);
                setSpeedY(0);    
            }
        }
    }
    
      public void leftMidfielderDefending() {
        if (ball.getX() < 500 && ball.getY() < 335) {
            setSpeedX((ball.getX()+250 - getX())/20);
            setSpeedY((410 - getY())/20);
        }
        else if (ball.getX() < 500 && ball.getY() >= 335) {
            setSpeedX((ball.getX()+250 - getX())/20);
            setSpeedY((ball.getY() - getY())/20);
        }
        else if (ball.getX() >= 500 && ball.getX() < 900 && ball.getY() < 335) {
            setSpeedX((ball.getX()+50 - getX())/20);
            setSpeedY((410 - getY())/20);
        }
        else if (ball.getX() >= 500 && ball.getX() < 900 && ball.getY() >= 335) {
            setSpeedX((ball.getX() - getX())/20);
            setSpeedY((ball.getY() - getY())/20);
        }
        else if (ball.getX() > 900 && ball.getY() < 335) {
            setSpeedX((ball.getX()-200 - getX())/30);
            setSpeedY((410 - getY())/30);
        }
        else if (ball.getX() > 900 && ball.getY() >= 335) {
            setSpeedX((ball.getX()-200 - getX())/30);
            setSpeedY((ball.getY() - getY())/20);
        }
    }
    
    public void leftMidfielderAttacking() {
        if (hasBall == true) {
            if (getX() > 350) {
                setSpeedY((550 - getY())/20);
                setSpeedX(-3);
            }
            else if(getX() <= 350) {
                autoPass();
            }
        }
        else if (hasBall == false) {
            if (getX() > 350) {
                setSpeedX(-3);
                setSpeedY((550 - getY())/20);
            }
            else if (getX() <= 350) {
                setSpeedX(0);
                setSpeedY(0);    
            }
        }
    }
    
    public void strikerDefending() {
        if (ball.getX() < 500 && ball.getY() < 335) {
            setSpeedX((ball.getX()+50 - getX())/20);
            setSpeedY((ball.getY() - getY())/30);
        }
        else if (ball.getX() < 500 && ball.getY() >= 335) {
            setSpeedX((ball.getX()+50 - getX())/20);
            setSpeedY((ball.getY() - getY())/30);
        }
        else if (ball.getX() >= 500 && ball.getX() < 900 && ball.getY() < 335) {
            setSpeedX((ball.getX()-50 - getX())/20);
            setSpeedY((ball.getY() - getY())/30);
        }
        else if (ball.getX() >= 500 && ball.getX() < 900 && ball.getY() >= 335) {
            setSpeedX((ball.getX()-50 - getX())/20);
            setSpeedY((ball.getY() - getY())/30);
        }
        else if (ball.getX() > 900 && ball.getY() < 335) {
            setSpeedX((ball.getX()-300 - getX())/30);
            setSpeedY((ball.getY() - getY())/30);
        }
        else if (ball.getX() > 900 && ball.getY() >= 335) {
            setSpeedX((ball.getX()-300 - getX())/30);
            setSpeedY((ball.getY() - getY())/30);
        }
    }
    
    public void strikerAttacking() {
        if (hasBall == true) {
            if (getX() > 250) {
                setSpeedY((335 - getY())/20);
                setSpeedX(-3);
                autoPass();
            }
            else if(getX() <= 250) {
                setSpeedY((450 - getY())/20);
                setSpeedX(-1);
                autoShoot();
            }
        }
        else if (hasBall == false) {
            if (getX() > 300) {
                setSpeedX(-3);
                setSpeedY(0);
            }
            else if (getX() <= 300) {
                setSpeedX(-1);
                setSpeedY(0);    
            }
        }
    }
    
    public void keeperDefending() {
        if (GameTime == Game.GameTimeEnum.FirstHalf){
            if (ball.getY() < 335) {
                setSpeedX(0);
                setSpeedY((280 - getY())/30);
            }
            else if (ball.getY() >= 335) {
                setSpeedX(0);
                setSpeedY((370 - getY())/30);
            }
        }
        else if (GameTime == Game.GameTimeEnum.FirstHalf) {
            if (ball.getY() >= 335) {
                setSpeedX(0);
                setSpeedY((280 - getY())/30);
            }
            else if (ball.getY() < 335) {
                setSpeedX(0);
                setSpeedY((370 - getY())/30);
            }
        }
    }
    
    public void keeperAttacking() {
        if (hasBall) {
            autoPass();
        }
        else {
            if (GameTime == Game.GameTimeEnum.FirstHalf){
                if (ball.getX() < 665) {
                    setSpeedY((330 - getY())/30);
                    setSpeedX((1250 - getX())/30);
                }
                else if (ball.getX() >= 665) {
                    setSpeedY((330 - getY())/30);
                    setSpeedX((1320 - getX())/30);
                }
            }
            else {
                if (ball.getX() < 665) {
                    setSpeedY((330 - getY())/30);
                    setSpeedX((25 - getX())/30);
                }
                else if (ball.getX() >= 665) {
                    setSpeedY((330 - getY())/30);
                    setSpeedX((0 - getX())/30);
                }

            }
        }
    }
    
    public void blueRightBackDefend() {
        if (ball.getX() > 800 && ball.getY() < 335) {
            setSpeedX((ball.getX()-600 - getX())/20);
            setSpeedY((410 - getY())/20);
        }
        else if (ball.getX() > 800 && ball.getY() >= 335) {
            setSpeedX((ball.getX()-600 - getX())/30);
            setSpeedY((ball.getY() - getY())/20);
            
        }
        else if (ball.getX() <= 800 && ball.getX() > 400 && ball.getY() < 335) {
            setSpeedX((ball.getX()-250 - getX())/20);
            setSpeedY((410 - getY())/20);
        }
        else if (ball.getX() <= 800 && ball.getX() > 400 && ball.getY() >= 335) {
            setSpeedX((ball.getX()-250 - getX())/20);
            setSpeedY((ball.getY() - getY())/20);
        }
        else if (ball.getX() <= 400 && ball.getY() < 335) {
            setSpeedX((ball.getX() - getX())/30);
            setSpeedY((410 - getY())/30);
        }
        else if (ball.getX() <= 400 && ball.getY() >= 335) {
            setSpeedX((ball.getX() - getX())/20);
            setSpeedY((ball.getY() - getY())/20);
        }
            
    }
   
    
    
    
    public void blueRightBackAttack() {
        if (ball.getX() < 665) {
            setSpeedX((ball.getX() - getX())/20);
            setSpeedY((450 - getY())/20);
        }
        else if (ball.getX() >= 665) {
            setSpeedX(1);
            setSpeedY((450 - getY())/20);
        }
    }
    
     public void blueLeftBackDefend() {
        if (ball.getX() > 800 && ball.getY() < 335) {
            setSpeedX((ball.getX()-600 - getX())/20);
            setSpeedY((ball.getY() - getY())/20);
        }
        else if (ball.getX() > 800 && ball.getY() >= 335) {
            setSpeedX((ball.getX()-600 - getX())/30);
            setSpeedY((150 - getY())/20);
            
        }
        else if (ball.getX() <= 800 && ball.getX() > 400 && ball.getY() < 335) {
            setSpeedX((ball.getX()-250 - getX())/20);
            setSpeedY((ball.getY() - getY())/20);
        }
        else if (ball.getX() <= 800 && ball.getX() > 400 && ball.getY() >= 335) {
            setSpeedX((ball.getX()-250 - getX())/20);
            setSpeedY((150 - getY())/20);
        }
        else if (ball.getX() <= 400 && ball.getY() < 335) {
            setSpeedX((ball.getX() - getX())/30);
            setSpeedY((ball.getY() - getY())/30);
        }
        else if (ball.getX() <= 400 && ball.getY() >= 335) {
            setSpeedX((ball.getX() - getX())/20);
            setSpeedY((150 - getY())/20);
        }
            
    }
        
    
    public void blueLeftBackAttack() {
        if (ball.getX() < 665) {
            setSpeedX((ball.getX() - getX())/20);
            setSpeedY((150 - getY())/20);
        }
        else if (ball.getX() >= 665) {
            setSpeedX(1);
            setSpeedY((150 - getY())/20);
        }
    }
    
     public void centreMidfieldDefend() {
        if (ball.getX() > 800 && ball.getY() < 335) {
            setSpeedX((ball.getX()-250 - getX())/20);
            setSpeedY((ball.getY()+50 - getY())/20);
        }
        else if (ball.getX() > 800 && ball.getY() >= 335) {
            setSpeedX((ball.getX()-250 - getX())/30);
            setSpeedY((ball.getY()-50 - getY())/20);
            
        }
        else if (ball.getX() <= 800 && ball.getX() > 400 && ball.getY() < 335) {
            setSpeedX((ball.getX()-50 - getX())/20);
            setSpeedY((ball.getY()+50 - getY())/20);
        }
        else if (ball.getX() <= 800 && ball.getX() > 400 && ball.getY() >= 335) {
            setSpeedX((ball.getX()-50 - getX())/20);
            setSpeedY((ball.getY()-50 - getY())/20);
        }
        else if (ball.getX() <= 400 && ball.getY() < 335) {
            setSpeedX((ball.getX()-25 - getX())/30);
            setSpeedY((ball.getY()+50 - getY())/30);
        }
        else if (ball.getX() <= 400 && ball.getY() >= 335) {
            setSpeedX((ball.getX() - getX())/20);
            setSpeedY((ball.getY()-50 - getY())/20);
        }
            
    }
    
    public void centreMidfieldAttack() {
        if (ball.getX() < 665) {
            setSpeedX((ball.getX()+150 - getX())/20);
            setSpeedY((335 - getY())/20);
        }
        else if (ball.getX() >= 665) {
            setSpeedX(2);
            setSpeedY((335 - getY())/20);
        }
    }
    
    public void insideRightDefend() {
        if (ball.getX() > 800 && ball.getY() < 335) {
            setSpeedX((ball.getX()-100 - getX())/20);
            setSpeedY((350 - getY())/20);
        }
        else if (ball.getX() > 800 && ball.getY() >= 335) {
            setSpeedX((ball.getX()-100 - getX())/30);
            setSpeedY((ball.getY()-50 - getY())/20);
            
        }
        else if (ball.getX() <= 800 && ball.getX() > 400 && ball.getY() < 335) {
            setSpeedX((ball.getX() - getX())/20);
            setSpeedY((350 - getY())/20);
        }
        else if (ball.getX() <= 800 && ball.getX() > 400 && ball.getY() >= 335) {
            setSpeedX((ball.getX() - getX())/30);
            setSpeedY((ball.getY() - getY())/30);
        }
        else if (ball.getX() <= 400 && ball.getY() < 335) {
            setSpeedX((ball.getX()+100 - getX())/30);
            setSpeedY((400 - getY())/30);
        }
        else if (ball.getX() <= 400 && ball.getY() >= 335) {
            setSpeedX((ball.getX()+100 - getX())/20);
            setSpeedY((ball.getY()+50 - getY())/20);
        }
            
    }
    
     public void insideRightAttack() {
        if (ball.getX() < 665) {
            setSpeedX((ball.getX()+150 - getX())/20);
            setSpeedY((500 - getY())/20);
        }
        else if (ball.getX() >= 665) {
            setSpeedX(2);
            setSpeedY((450 - getY())/20);
        }
    }
     
     public void insideLeftDefend() {
        if (ball.getX() > 800 && ball.getY() < 335) {
            setSpeedX((ball.getX()-100 - getX())/20);
            setSpeedY((ball.getY()-50 - getY())/20);
        }
        else if (ball.getX() > 800 && ball.getY() >= 335) {
            setSpeedX((ball.getX()-100 - getX())/30);
            setSpeedY((280 - getY())/20);
            
        }
        else if (ball.getX() <= 800 && ball.getX() > 400 && ball.getY() < 335) {
            setSpeedX((ball.getX() - getX())/20);
            setSpeedY((ball.getY() - getY())/20);
        }
        else if (ball.getX() <= 800 && ball.getX() > 400 && ball.getY() >= 335) {
            setSpeedX((ball.getX() - getX())/30);
            setSpeedY((280 - getY())/30);
        }
        else if (ball.getX() <= 400 && ball.getY() < 335) {
            setSpeedX((ball.getX()+100 - getX())/30);
            setSpeedY((ball.getY()-50 - getY())/30);
        }
        else if (ball.getX() <= 400 && ball.getY() >= 335) {
            setSpeedX((ball.getX()+100 - getX())/20);
            setSpeedY((280 - getY())/20);
        }
            
    }
     
     public void insideLeftAttack() {
        if (ball.getX() < 665) {
            setSpeedX((ball.getX()+150 - getX())/20);
            setSpeedY((100 - getY())/20);
        }
        else if (ball.getX() >= 665) {
            setSpeedX(2);
            setSpeedY((150 - getY())/20);
        }
    }
     
     
    
    public void redTeamAI() {
        if (getTeamWithBall() == TeamKindEnum.BlueTeam) {
                if (position.contains("RightBack")) {
                    rightBackDefending();
                }
                else if (position.contains("LeftBack")) {
                    leftBackDefending();
                }
                else if (position.contains("RightMidfield")) {
                    rightMidfielderDefending();
                }
                else if (position.contains("LeftMidfield")) {
                    leftMidfielderDefending();
                }
                else if (position.contains("Striker")) {
                    strikerDefending();
                }
                else if (position.contains("Keeper")) {
                    keeperDefending();
                }
            }
        
        else if (getTeamWithBall() == TeamKindEnum.RedTeam) {
                if (position.contains("RightBack")) {
                    rightBackAttacking();
                }
                else if (position.contains("LeftBack")) {
                    leftBackAttacking();
                }
                else if (position.contains("RightMidfield")) {
                    rightMidfielderAttacking();
                }
                else if (position.contains("LeftMidfield")) {
                    leftMidfielderAttacking();
                }
                else if (position.contains("Striker")) {
                    strikerAttacking();
                }
                else if (position.contains("Keeper")) {
                    keeperAttacking();
                }       
            }
        
        else if (getTeamWithBall() == TeamKindEnum.NoTeam) {
                if (position.contains("RightBack")) {
                    rightBackDefending();
                }
                else if (position.contains("LeftBack")) {
                    leftBackDefending();
                }
                else if (position.contains("RightMidfield")) {
                    rightMidfielderDefending();
                }
                else if (position.contains("LeftMidfield")) {
                    leftMidfielderDefending();
                }
                else if (position.contains("Striker")) {
                    strikerDefending();
                }
                else if (position.contains("Keeper")) {
                    keeperDefending();
                }
            }
    }
    
    public void blueTeamAI() {
        if (getTeamWithBall() == TeamKindEnum.BlueTeam) {
            if (underControl == false) {
                if (position.contains("RightBack")) {
                    blueRightBackAttack();
                }
                else if (position.contains("LeftBack")) {
                    blueLeftBackAttack();
                }
                else if (position.contains("CentreMidfield")) {
                    centreMidfieldAttack();
                }
                else if (position.contains("InsideRight")) {
                    insideRightAttack();
                }
                else if (position.contains("InsideLeft")) {
                    insideLeftAttack();
                }
                
            }
        }
        else if (getTeamWithBall() == TeamKindEnum.RedTeam) {
            if (underControl == false) {
                if (position.contains("RightBack")) {
                    blueRightBackDefend();
                }
                else if (position.contains("LeftBack")) {
                    blueLeftBackDefend();
                }
                else if (position.contains("CentreMidfield")) {
                    centreMidfieldDefend();
                }
                else if (position.contains("InsideRight")) {
                    insideRightDefend();
                }
                else if (position.contains("InsideLeft")) {
                    insideLeftDefend();
                }
                
            }
         }
    }
//    AMER CODE: no need for these     
//    public void setcontrolImage() {
//        updateImage(blueTeamimages[1]);
//    }
//    
//    public void setstandardImage() {
//        updateImage(blueTeamimages[0]);
//    }
//    
//    public void setKeepercontrolImage() {
//        updateImage(redTeamimages[2]);
//    }
//    
//    public void setKeeperstandardImage() {
//        updateImage(blueTeamimages[2]);
//    }
//    
//    public void setNearestTeamMateImage() {
//        updateImage(blueTeamimages[3]);
//    }
    
    public void setHasBall(boolean hasBall) {
        this.hasBall = hasBall;
    }
    
    public boolean getHasBall() {
        return hasBall;
    }
    
//    public void setOnRedTeam(boolean onRedTeam) {
//        this.onRedTeam = onRedTeam;
//    }
//    
//    public boolean getOnRedTeam() {
//        return onRedTeam;
//    }
//    
    public void setPass(boolean pass) {
        this.pass = pass;
    }
    
    public boolean getPass() {
        return pass;
    }
    
    public void setShoot(boolean shoot) {
        this.shoot = shoot;
    }
    
    public boolean getShoot() {
        return shoot;
    }
    
    public void setUnderControl(boolean underControl) {
    
        this.underControl = underControl;
        
    }
    
    public boolean getUnderControl() {
        return underControl;
    }
    
    
    //public void setNearestTeamMate(String nearestTeamMate) {
    public void setNearestTeamMate(Player nearestTeamMate) {
        this.nearestTeamMate = nearestTeamMate;
    }
    
    //public String getNearestTeamMate() {
    public Player getNearestTeamMate() {
        return nearestTeamMate;
    }
    
    public void setNearestDefender(String nearestDefender) {
        this.nearestDefender = nearestDefender;
    }
    
    public String getNearestDefender() {
        return nearestDefender;
    }
    
    public void setUnCollidable(boolean UnCollidable) {
        this.UnCollidable = UnCollidable;
    }
    
    public boolean getUnCollidable() {
        return UnCollidable;
    }
    
    public void restoreCollidability() {
        collidabilitycounter++;
        if (collidabilitycounter == 30) {
            UnCollidable = false;
            collidabilitycounter = 0;
        }
    }
    
    public void autoPass() {
        passcounter++;
        if (passcounter == 40) {
            UnCollidable = true;
            pass = true;
            passcounter = 0;
            hasBall = false;
        }
    }
    
    public void autoShoot() {
        shotcounter++;
        if (shotcounter == 60) {
            UnCollidable = true;
            shoot = true;
            shotcounter = 0;
            hasBall = false;
        }
    }
    
    //AMER CODE: change name 
    public double getDistanceFromActor(Actor A) {
        int h = A.getX() - getX();
        int v = A.getY() - getY();
        double distance = Math.sqrt(h*h + v*v);
        return distance;
    }
//    public double getNearestTeamMate(int underControlX, int underControlY) {
//        int h = underControlX - getX();
//        int v = underControlY - getY();
//        double distance = Math.sqrt(h*h + v*v);
//        return distance;
//    }
    
        
    public void followBall() {
       
            setSpeedX((ball.getX() - getX())/100);
            setSpeedY((ball.getY() - getY())/100);
        
    }

    /**
     * @return the teamWithBall
     */
    public TeamKindEnum getTeamWithBall()
    {
        return teamWithBall;
    }

    public void setTeamWithBall(TeamKindEnum teamWithBall) {
        this.teamWithBall = teamWithBall;
    }

    public Team getPlayerTeam()
    {
        return PlayerTeam;
    }

    public void setPlayerTeam(Team PlayerTeam)
    {
        this.PlayerTeam = PlayerTeam;
    }
    
    public TeamKindEnum getPlayerTeamKind(){
        if  (PlayerTeam != null){
            return PlayerTeam.getTeamKind();
        }
        else {return TeamKindEnum.NoTeam;}
    }
     
    public void setGameTimeSecondHalf(){
        GameTime = Game.GameTimeEnum.SecondHalf;
    }  
    
    public void setGameTimeFirstHalf(){
        GameTime = Game.GameTimeEnum.FirstHalf;
    }  

}

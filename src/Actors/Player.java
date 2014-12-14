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

    
    private Image PlayerImage;
    
    protected boolean hasBall; //does this player have the ball or not
    protected boolean underControl; //is this player under user control or not
    
    private Player nearestTeamMate;
    
    private boolean UnCollidable; /* if player is uncollidable the ball will not stick to them - they are not included
    in the condition for collision checking in the checkForCollisions() method in Game. this was necessary to create
    a few miliseconds of immunity from collision so passes and shots get away from players properly */

    private TeamKindEnum teamWithBall = TeamKindEnum.NoTeam; //brought in from whichTeamHaveBall() method in Game, so players know
    
    public enum PlayerPositionEnum{
        NoPosition
        ,RightBack
        ,LeftBack
        ,RightMidfield
        ,LeftMidfield
        ,Striker
        ,Keeper
        
        ,CentreMidfield
        ,InsideRight
        ,InsideLeft
    }
    
    private PlayerPositionEnum position = PlayerPositionEnum.NoPosition; //position player is currently playing, defined in startPositions() method in Game

    protected Ball ball;
    
    private int collidabilitycounter = 0; //a counter used in the restoreCollidability() method below
    private int passcounter = 0; //a counter used in the autoPass() method below
    private int shotcounter = 0; //a counter used in the autoShoot() method below
    private Team PlayerTeam;

    protected Game.GameTimeEnum GameTime = Game.GameTimeEnum.FirstHalf;
    
    public Player(Team PlayerTeam){
        //I wish if this code work but java said constructor must be the first
        super("PlayerBlack.png", 35);
                        
        PlayerImage = new Image(("/img/"+"PlayerBlack.png"));
        
    }
    
    public Player(){
        //I wish if this code work but java said constructor must be the first
        super("PlayerBlack.png", 35);
                        
        PlayerImage = new Image(("/img/"+"PlayerBlack.png"));
        
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
        if (UnCollidable == true) {
            restoreCollidability();
        }

        updateImages();
        
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
    
    public void updateImages() {

    }
    
    public boolean isNearst(){ //return true in case this object is the neast to the player under control
        if (nearestTeamMate == this)
            return true;
        else 
            return false;
    }
    
    public boolean isMyTeamHaveBall(){
        
        if (teamWithBall == PlayerTeam.getTeamKind())
            return true;
        else
            return false;
    }
    
    public void setPosition(PlayerPositionEnum position) {
        this.position = position;
    }
    
    public PlayerPositionEnum getPosition() {
        return position;
    }
    
    //AMER CODE: new object 
    public void setBall(Ball ball){
        this.ball = ball;
    }
    
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

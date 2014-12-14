/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Actors;

import GameFrameworkJavaFX.*;
import java.awt.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author anc
 */
public class Actor {
    
    public enum ActorDirectionEnum{
        None
        ,North
        ,NorthEast
        ,East
        ,SouthEast
        ,South
        ,SouthWest
        ,West
        ,NorthWest
    }    
    
    protected enum ActorRotationStyle{
        ManualRotation
        ,AutoNormalRotation
        ,AutoFlipVerticalyAndRotation //its mean to flip the actor if the rotation was more than 90 degree or less than -90
    }
    
    private ActorDirectionEnum actorDirection = ActorDirectionEnum.East;

    private int speedX = 0;
    private int speedY = 0;
    private int x;
    private int y;

    private ActorRotationStyle rotationStyle = ActorRotationStyle.AutoFlipVerticalyAndRotation; 
    private ActorDirectionEnum actorRotation = ActorDirectionEnum.East; // this will have meaning only if the autoRotation is Disable
    
    private double collisionRadius = 0.5; 

    private final ImageView imageView;
    private final int width;
    private final int height;
    
    public Actor(String ImageFileName, int ImageSize){
        imageView = new ImageView();
        imageView.setImage(new Image(getClass().getResourceAsStream("/img/"+ImageFileName)));
        imageView.setFitWidth(ImageSize);
        imageView.setPreserveRatio(true);
        this.width = ImageSize;
        this.height = ImageSize;

    }
    
    public ActorDirectionEnum getActorDirection()
    {
        return actorDirection;
    }
    
    protected void setActorRotation(int newAngel)
    {
        if (rotationStyle == ActorRotationStyle.ManualRotation)
        if (newAngel < 0 || newAngel > 360)
            return;
        
        imageView.setRotate(Math.abs(90 - newAngel));

    }
    
    protected void setActorRotaionBasedOnDirection(ActorDirectionEnum newDirection)
    {
        actorDirection = newDirection;
        switch (newDirection){
        case North:
            imageView.setRotate(270);
            imageView.setScaleX(1);
            break;
        case NorthEast:
            imageView.setRotate(315);
            imageView.setScaleX(1);
            break;
        case East:
            imageView.setRotate(0);
            imageView.setScaleX(1);
            break;
        case SouthEast:
            imageView.setRotate(45);
            imageView.setScaleX(1);
            break;
        case South:
            imageView.setRotate(90);
            imageView.setScaleX(1);
            break;
        case SouthWest:
            imageView.setRotate(315);
            imageView.setScaleX(-1);
            break;
        case West:
            imageView.setRotate(0);
            imageView.setScaleX(-1);
            break;
        case NorthWest:
            imageView.setRotate(45);
            imageView.setScaleX(-1);
            break;
        }
        
    }

    public void updateImage(Image image){
        imageView.setImage(image);
    }
    
    public void setLocation(int x, int y){
        this.setX(x); 
        this.setY(y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        imageView.setLayoutX(x);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        imageView.setLayoutY(y);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public void setCollisionRadius(double value){
        collisionRadius = value;
    }
    
    public double getCollisionRadius(){
        return collisionRadius;
    }
    
    public boolean collidesWith(Actor otherActor){
        int shrinkX = (int)(width * (1-collisionRadius))/2;
        int shrinkY = (int)(height * (1-collisionRadius))/2;
        Rectangle r1 = new Rectangle(x+shrinkX, y+shrinkY, width-(2*shrinkX), height-(2*shrinkY));
        int otherShrinkX = (int)(otherActor.getWidth()*(1-otherActor.getCollisionRadius()))/2;
        int otherShrinkY = (int)(otherActor.getHeight()*(1-otherActor.getCollisionRadius()))/2;
        Rectangle r2 = new Rectangle(otherActor.getX()+otherShrinkX, otherActor.getY()+otherShrinkY, otherActor.getWidth()-(2*otherShrinkX), otherActor.getHeight()-(2*otherShrinkY));
        return r1.intersects(r2);
    }

    private void updatePositionFromSpeed(){
        
        if (getSpeedX()==0 && getSpeedY()==0)
            return;
        
        setX(getX()+getSpeedX());
        setY(getY()+getSpeedY());
        
        if (getX()>Gui.getInstance().getWidth()-this.width)
            setX(Gui.getInstance().getWidth()-this.width);
        if (getX()<0)
            setX(0);
        if (getY()>Gui.getInstance().getHeight()-this.height)
            setY(Gui.getInstance().getHeight()-this.height);
        if (getY()<0)
            setY(0);
    }
    
    public boolean atHorizontalEdge(){
        return (x>=Gui.getInstance().getWidth()-this.width ||  x<=0);   
    }
    
    public boolean atVerticalEdge(){
        return (y>=Gui.getInstance().getHeight()-this.height ||  y<=0);   
    }
    
    public void timeTick(){
        updatePositionFromSpeed();
    }

    public int getSpeedX() {
        return speedX;
    }

    public void setSpeedX(int xSpeed) {
        if (xSpeed != this.speedX){
            this.speedX = xSpeed;
            updateDirectionBasedOnSpeed();
            
        }
    }

    public int getSpeedY() {
        return speedY;
    }

    public void setSpeedY(int ySpeed) {
        if (ySpeed != this.speedY){
            this.speedY = ySpeed;
            updateDirectionBasedOnSpeed();
        }
    }
    
    public void setSpeedAndDirection(int speed, ActorDirectionEnum direction){

        if (speed == 0 || direction == ActorDirectionEnum.None){
            speed = 0;
            actorDirection = ActorDirectionEnum.None;
        }
        
        switch (direction){
            case North:
                speedY = -speed;
                speedX = 0;
                break;
            case NorthEast:
                speedY = (int) - Math.sqrt((speed ^ 2) * 2); // x = sqr((speed ^ 2)/2) 
                speedX = (int) Math.sqrt((speed ^ 2) * 2);
                break;
            case East:
                speedY = 0;
                speedX = speed;
                break;
            case SouthEast:
                speedY = (int) Math.sqrt((speed ^ 2) * 2);
                speedX = (int) Math.sqrt((speed ^ 2) * 2);
                break;
            case South:
                speedY = speed;
                speedX = 0;
                break;
            case SouthWest:
                speedY = (int) Math.sqrt((speed ^ 2) * 2);
                speedX = (int) - Math.sqrt((speed ^ 2) * 2);
                break;
            case West:
                speedY = 0;
                speedX = -speed;
                break;
            case NorthWest:
                speedY = (int) - Math.sqrt((speed ^ 2) * 2);
                speedX = (int) - Math.sqrt((speed ^ 2) * 2);
                break;
                
        }
      
        if (rotationStyle == ActorRotationStyle.AutoFlipVerticalyAndRotation)
            FlipAndRotationBasedOnDirection();
        
    }
    
    public void ChangeDirection(ActorDirectionEnum newDirection){
        
    }
            
    public void stop()
    {
        //basicly to put both speed in zero
        speedX = 0;
        speedY = 0;
        updateDirectionBasedOnSpeed();
    }

    public void stop(ActorDirectionEnum Rotation){
        stop();
        setRotation(Rotation);
    }

    public void stop(ActorDirectionEnum RotationAfterStop, int LocationX, int LocationY){
        stop();
        setRotation(RotationAfterStop);
        setX(LocationX);
        setY(LocationY);
    }


    private void setRotation(ActorDirectionEnum Rotation)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void updateDirectionBasedOnSpeed(){
       if (speedY == 0 && speedX == 0){
           actorDirection = ActorDirectionEnum.None;
       } 
       else if (speedY < 0 && speedX ==0){ //going North
            actorDirection = ActorDirectionEnum.North;
        }
        else if (speedY <0 && speedX >0){ //going NorthEast
            actorDirection = ActorDirectionEnum.NorthEast;
        }
        else if (speedY ==0 && speedX >0){ //going East
            actorDirection = ActorDirectionEnum.East;
        }
        else if (speedY >0 && speedX >0){ //going South East
            actorDirection = ActorDirectionEnum.SouthEast;
        }
        else if (speedY >0 && speedX ==0){ //going South
            actorDirection = ActorDirectionEnum.South;
        }
        else if (speedY >0 && speedX <0){ //going SouthWest
            actorDirection= ActorDirectionEnum.SouthWest;
        }
        else if (speedY ==0 && speedX <0){ //going West
            actorDirection= ActorDirectionEnum.West;
        }
        else if (speedY <0 && speedX <0){ //going NorthWest
            actorDirection= ActorDirectionEnum.NorthWest;
        }
        if (rotationStyle == ActorRotationStyle.AutoFlipVerticalyAndRotation)
            FlipAndRotationBasedOnDirection();
        
    }
    
    private void FlipAndRotationBasedOnDirection(){
        
        switch (actorDirection){
            case None:
                //do nothing keep that last rotation still
                break;
            case North:
                imageView.setRotate(270);
                imageView.setScaleX(1);
                break;
            case NorthEast:
                imageView.setRotate(315);
                imageView.setScaleX(1);
                break;
            case East:
                imageView.setRotate(0);
                imageView.setScaleX(1);
                break;
            case SouthEast:
                imageView.setRotate(45);
                imageView.setScaleX(1);
                break;
            case South:
                imageView.setRotate(90);
                imageView.setScaleX(1);
                break;
            case SouthWest:
                if (rotationStyle == ActorRotationStyle.AutoFlipVerticalyAndRotation){
                    imageView.setRotate(315);
                    imageView.setScaleX(-1);
                }else if(rotationStyle == ActorRotationStyle.AutoNormalRotation) {
                    imageView.setRotate(315);
                    imageView.setScaleX(1);
                }
                break;
            case West:
                if (rotationStyle == ActorRotationStyle.AutoFlipVerticalyAndRotation){
                    imageView.setRotate(0);
                    imageView.setScaleX(-1);
                }else if(rotationStyle == ActorRotationStyle.AutoNormalRotation) {
                    imageView.setRotate(0);
                    imageView.setScaleX(1);
                }
                break;
            case NorthWest:
                if (rotationStyle == ActorRotationStyle.AutoFlipVerticalyAndRotation){
                    imageView.setRotate(45);
                    imageView.setScaleX(-1);
                }else if(rotationStyle == ActorRotationStyle.AutoNormalRotation) {
                    imageView.setRotate(45);
                    imageView.setScaleX(1);
                }
                break;
        }
    }        
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Actors;

import GameFrameworkJavaFX.Game;
import javafx.scene.image.Image;

/**
 *
 * @author Amer
 */
public class ComputerPlayer extends Player
{
    private Image playerImage;
    
    private boolean pass; //when true, Game will make this player pass, with computerPassingAndShooting() method
    private boolean shoot; //when true, Game will make this player shoot, with computerPassingAndShooting() method

    public ComputerPlayer(Team PlayerTeam){
        super(PlayerTeam);

        playerImage = new Image(("/img/"+"PlayerRed.png"));
        updateImage(playerImage);

    }

    public boolean getPass()
    {
        return pass;
    }

    public void setPass(boolean pass)
    {
        this.pass = pass;
    }

    public boolean getShoot()
    {
        return shoot;
    }

    public void setShoot(boolean shoot)
    {
        this.shoot = shoot;
    }

    @Override
    public void timeTick(){
        
        super.timeTick(); 

        redTeamAI();
        updateImages();
        
    }

    
    public void redTeamAI() {
        if (getTeamWithBall() == Team.TeamKindEnum.BlueTeam) {
            if (getPosition() == PlayerPositionEnum.RightBack) {
                rightBackDefending();
            }
            else if (getPosition() == PlayerPositionEnum.LeftBack) {
                leftBackDefending();
            }
            else if (getPosition() == PlayerPositionEnum.RightMidfield) {
                rightMidfielderDefending();
            }
            else if (getPosition() == PlayerPositionEnum.LeftMidfield) {
                leftMidfielderDefending();
            }
            else if (getPosition() == PlayerPositionEnum.Striker) {
                strikerDefending();
            }
            else if (getPosition() == PlayerPositionEnum.Keeper) {
                keeperDefending();
            }
        }
        else if (getTeamWithBall() == Team.TeamKindEnum.RedTeam) {
            if (getPosition() == PlayerPositionEnum.RightBack) {
                rightBackAttacking();
            }
            else if (getPosition() == PlayerPositionEnum.LeftBack) {
                leftBackAttacking();
            }
            else if (getPosition() == PlayerPositionEnum.RightMidfield) {
                rightMidfielderAttacking();
            }
            else if (getPosition() == PlayerPositionEnum.LeftMidfield) {
                leftMidfielderAttacking();
            }
            else if (getPosition() == PlayerPositionEnum.Striker) {
                strikerAttacking();
            }
            else if (getPosition() == PlayerPositionEnum.Keeper) {
                keeperAttacking();
            }       
        }
        else if (getTeamWithBall() == Team.TeamKindEnum.NoTeam) {
            if (getPosition() == PlayerPositionEnum.RightBack) {
                rightBackDefending();
            }
            else if (getPosition() == PlayerPositionEnum.LeftBack) {
                leftBackDefending();
            }
            else if (getPosition() == PlayerPositionEnum.RightMidfield) {
                rightMidfielderDefending();
            }
            else if (getPosition() == PlayerPositionEnum.LeftMidfield) {
                leftMidfielderDefending();
            }
            else if (getPosition() == PlayerPositionEnum.Striker) {
                strikerDefending();
            }
            else if (getPosition() == PlayerPositionEnum.Keeper) {
                keeperDefending();
            }
        }
        

    }
    
    private int passcounter;
    public void autoPass() {
        passcounter++;
        if (passcounter == 40) {
            setUnCollidable(true);
            setPass(true);
            passcounter = 0;
            setHasBall(false);
        }
    }

    private int shotcounter;
    public void autoShoot() {
        shotcounter++;
        if (shotcounter == 60) {
            setUnCollidable(true);
            setShoot(true);
            shotcounter = 0;
            setHasBall(false);
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
    

    

}

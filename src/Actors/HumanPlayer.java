/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Actors;

import javafx.scene.image.Image;

/**
 *
 * @author Amer
 */
public class HumanPlayer extends Player
{
    private Image playerImage;
    private Image playerUndercontrolImage;
    private Image playerNearestImage;
    

    public HumanPlayer(Team PlayerTeam){
        super(PlayerTeam);

        playerImage = new Image(("/img/"+"PlayerBlue.png"));
        playerUndercontrolImage = new Image(("/img/"+"PlayerUnderControl.png"));
        playerNearestImage = new Image(("/img/"+"PlayerNearest.png"));
    
    }
    @Override
    public void timeTick(){
        
        super.timeTick(); 

        blueTeamAI();
        updateImages();
        
    }
    
    @Override
    public void updateImages() {
        if(getUnderControl()) {
            updateImage(playerUndercontrolImage);
        }
        else { //player not in Control
            if (isNearst() && isMyTeamHaveBall()){
                updateImage(playerNearestImage);
            }
            else{ // not nearest team mate
                updateImage(playerImage);
            }
        }
    }
    
    public void blueTeamAI() {
        if (getTeamWithBall() == Team.TeamKindEnum.BlueTeam) {
            if (underControl == false) {
                if (getPosition() == PlayerPositionEnum.RightBack) {
                    blueRightBackAttack();
                }
                else if (getPosition() == PlayerPositionEnum.LeftBack) {
                    blueLeftBackAttack();
                }
                else if (getPosition() == PlayerPositionEnum.CentreMidfield) {
                    centreMidfieldAttack();
                }
                else if (getPosition() == PlayerPositionEnum.InsideRight) {
                    insideRightAttack();
                }
                else if (getPosition() == PlayerPositionEnum.InsideLeft) {
                    insideLeftAttack();
                }
                
            }
        }
        else if (getTeamWithBall() == Team.TeamKindEnum.RedTeam) {
            if (underControl == false) {
                if (getPosition() == PlayerPositionEnum.RightBack) {
                    blueRightBackDefend();
                }
                else if (getPosition() == PlayerPositionEnum.LeftBack) {
                    blueLeftBackDefend();
                }
                else if (getPosition() == PlayerPositionEnum.CentreMidfield) {
                    centreMidfieldDefend();
                }
                else if (getPosition() == PlayerPositionEnum.InsideRight) {
                    insideRightDefend();
                }
                else if (getPosition() == PlayerPositionEnum.InsideLeft) {
                    insideLeftDefend();
                }
                
            }
         }
    }

}

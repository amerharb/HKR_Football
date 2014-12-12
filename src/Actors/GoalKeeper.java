/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Actors;

import javafx.scene.Parent;
import javafx.scene.image.Image;

/**
 *
 * @author Amer
 */
public class GoalKeeper extends Player
{
    private Image blueKeeperImage;
    private Image blueKeeperUnderControlImage;
    private Image blueKeeperNearestImage;
    private Image blueKeeperSave1;
    private Image blueKeeperSave2;

    private Image redKeeperImage;

    private int keeperSaveImageCounter = 0;
    private boolean keeperSave;

    
    public GoalKeeper(Team PlayerTeam){
        super(PlayerTeam);
        this.redKeeperImage = new Image(("/img/"+"GK.png"));
        this.blueKeeperSave2 = new Image(("/img/"+"GKsave2.png"));
        this.blueKeeperSave1 = new Image(("/img/"+"GKsave1.png"));
        this.blueKeeperNearestImage = new Image(("/img/"+"GKnearest.png"));
        this.blueKeeperUnderControlImage = new Image(("/img/"+"GKcontrol.png"));
        this.blueKeeperImage = new Image(("/img/"+"GK.png"));
    }

    public void keeperMakeSave() {
                
    }
    
    public void setKeeperSave(boolean keeperSaveMode) {
        
        if (keeperSaveMode != this.keeperSave) {
            this.keeperSave = keeperSaveMode;
            keeperSaveImageCounter = 0;
            updateImages();
        }
    }
    
    public boolean getKeeperSave() {
        return keeperSave;
    }
    
    @Override
    public void timeTick(){
        super.timeTick(); 
        if (keeperSave == true) {
            keeperSaveImageCounter++;
            if (keeperSaveImageCounter >= 20) {
                keeperSave = false;
                keeperSaveImageCounter = 0; //it seem to me that this need to be reset
            }
        }
    }

    

    @Override
    public void updateImages() {
        if (super.getPlayerTeamKind()== Team.TeamKindEnum.BlueTeam) { //will only affect blue team, red team images never need to change
            if(super.getUnderControl()) {
                if (keeperSave) {
                    if (keeperSaveImageCounter < 10) {    
                        updateImage(blueKeeperSave1);
                    }
                    else {
                        updateImage(blueKeeperSave2);
                    }
                }
                else {
                    updateImage(blueKeeperUnderControlImage);
                }
            }
            else if (isNearst() && isMyTeamHaveBall()){
                updateImage(blueKeeperNearestImage);
            }
            else {
                updateImage(blueKeeperImage);
            }
        }
        else {
            updateImage(redKeeperImage);
        }
    }
}

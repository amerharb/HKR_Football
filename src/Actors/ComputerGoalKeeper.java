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
public class ComputerGoalKeeper extends ComputerPlayer
{
    private Image keeperImage;
 
    public ComputerGoalKeeper(Team PlayerTeam){
        super(PlayerTeam);
        keeperImage = new Image(("/img/"+"GK.png"));
        updateImage(keeperImage);
   }
    
}

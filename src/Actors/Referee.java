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
public class Referee extends Player
{
    private Image PlayerImage;
    
    public Referee()
    {
        super();

        PlayerImage = new Image(("/img/"+"PlayerBlack.png"));
        
   }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Actors;

/**
 *
 * @author Amer
 */
//not in use
public class HumanTeam extends Team
{

    public HumanPlayer[] Players = new HumanPlayer[6];

    public HumanTeam(){
        super(TeamKindEnum.BlueTeam);
    }
}

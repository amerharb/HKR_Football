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

public class ComputerTeam extends Team
{

    public ComputerPlayer[] Players = new ComputerPlayer[6];

    public ComputerTeam(){
        super(TeamKindEnum.RedTeam);
    }
}

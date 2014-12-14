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
public class Team
{

    public enum TeamKindEnum{
        RedTeam
        ,BlueTeam
        ,NoTeam
        ,BothTeam
    }
    
    private TeamKindEnum teamKind = TeamKindEnum.NoTeam;
    public Player[] Players = new Player[6];
    
    public Team(){
        this(TeamKindEnum.NoTeam);
    }
    
    public Team(TeamKindEnum team){
        teamKind = team;
    }

    public TeamKindEnum getTeamKind()
    {
        return teamKind;
    }
    
//    public HumanGoalKeeper getKeeper(){
//        return (HumanGoalKeeper)Players[5];
//    }
}

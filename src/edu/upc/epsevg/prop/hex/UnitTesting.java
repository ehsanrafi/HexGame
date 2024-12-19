/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.hex;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
import edu.upc.epsevg.prop.hex.players.Monaco.Dijkstra;
import edu.upc.epsevg.prop.hex.players.ProfeGameStatus2;
import edu.upc.epsevg.prop.hex.players.ProfeGameStatus3;
import edu.upc.epsevg.prop.hex.players.ProfeGameStatus3.Result;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
/**
 *
 * @author bernat
 */
public class UnitTesting {
    public static void main(String[] args) {
        
        byte[][] board = {
        //X   0  1  2  3  4  5  6  7  8
            { -1, 1, 1, 1, 1, 1, 1, 1, 1},                     // 0   Y
              { -1, 0, 0, 0, 0, 0, 0, 0, 0},                    // 1
                { -1, 0, 0, 0, 0, 0, 0, 0, 0},                  // 2
                  { -1, 0, 0, 0, 0, 0, 0, 0, 0},                // 3
                    { -1, 0, 0, 0,-1, 0, 0, 0, 0},              // 4
                      { -1, 0, 0, 0, 0, 1, 0, 0, 0},            // 5
                        { -1, 0, 0,-1,-1,-1, -1,-1, 1},          // 6
                          { -1, 1, 0, 1, 1, 1,-1, 1, 1},        // 7
                            { 0, 0, 0, 0, 0, 0,-1, 0, 1}       // 8    Y
        };
        
       
        /*
        byte[][] board = {
            { -1, 1, 0 },
              { -1, -1, 0 },
                { -1, 0, -1 }
        };
        */
        
        //System.out.println("ocupa: " + board[0][1]);
        /*
        for (int i = 0; i < board.length; ++i) {
            for (int j = 0; j < board[i].length; ++j) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        */
        
        //PLAYER1 = 1 : PLAYER2 : -1
        HexGameStatus gs = new HexGameStatus(board, PlayerType.PLAYER1);
        //PLAYER1 - LADOS VERTICALES (IZQUIERDA Y DERECHA)
        Dijkstra dJugador = new Dijkstra(gs, PlayerType.PLAYER1);
        int nJugador = dJugador.shortestPath();
        //PLAYER2 - LADOS HORIZONTALES - (ARRIBA Y ABAJO)
        Dijkstra dEnemic = new Dijkstra(gs, PlayerType.PLAYER2);
        int nEnemic = dEnemic.shortestPath();
        System.out.println("Distance Jugador: " + nJugador);
        System.err.println("Distance Enemic: " + nEnemic);
    }
}

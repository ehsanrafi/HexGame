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
        /*
        byte[][] board = {
        //X   0  1  2  3  4  5  6  7  8
            { 1, 1, 1, 1, 1, 1, 1, 1, 0},                     // 0   Y
              { 1, 0, 0, 0, 0, 0, 0, 0, 0},                    // 1
                { 1, 0, 0, 0, 0, 0, 0, 0, 0},                  // 2
                  { 1, 0, 0, 0, 0, 0, 0, 0, 0},                // 3
                    { 1, 0, 0, 0, 0, 0, 0, 0, 0},              // 4
                      { 1, 0, 0, 0, 0, 0, 1, 1, 1},            // 5
                        { 1, 0, 0, 0, 0, 0, 0, 0, -1},          // 6
                          { 1, 0, 0, 0, 0, 0, 0, 0, -1},        // 7
                            { 0, 0, 0, 0, 0, 0, 0, 0, -1}       // 8    Y
        };
        */
        
        
        byte[][] board = {
            { 1, 0, 0 , -1},
              { 0, 0, 0, 1},
                { 1, 1, 1, -1},
                  { 0, 1, -1, -1}
        };
        
        
        
        //System.out.println("ocupa: " + board[0][1]);
        /*
        System.out.println("Original:");
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
        //Dijkstra dGraf = new Dijkstra(gs);
        //System.out.println(gs.getMoves());
        
        /*
        byte[][] boardAux = new byte[gs.getSize()][gs.getSize()];
        
        for (int i = 0; i < gs.getSize(); ++i) {
            for (int j = 0; j < gs.getSize(); ++j) {
                boardAux[j][i] = (byte) gs.getPos(i, j);
            }
        }
        
        System.out.println("Copy:");
        for (int i = 0; i < boardAux.length; ++i) {
            for (int j = 0; j < boardAux[i].length; ++j) {
                System.out.print(boardAux[i][j] + " ");
            }
            System.out.println();
        }
        */
        
        //int nJugador = dGraf.getDistance(PlayerType.PLAYER1);
        //PLAYER2 - LADOS HORIZONTALES - (ARRIBA Y ABAJO)
        //int nEnemic = dGraf.getDistance(PlayerType.PLAYER2);
        
        //System.out.println(gs.getMoves());
        //System.out.println(gs.getNeigh(new Point(0, 0)));
        
        //System.out.println(gs.isGameOver());
        
        
        //int PlayerEvaluation = Math.max(1, 100 - Math.abs(nJugador));
        //int EnemicEvaluation = Math.max(1, 100 - Math.abs(nEnemic));
        
        //System.out.println("Heuristic: " + (PlayerEvaluation - EnemicEvaluation));
        //System.out.println("Distance Jugador: " + nJugador);
        //System.err.println("Distance Enemic: " + nEnemic);
        //if(gs.isGameOver() && gs.GetWinner() == PlayerType.PLAYER1) {
            //System.out.println("Guanyador PLAYER1");
        //}
        System.out.println(9 / 2);
    }
}

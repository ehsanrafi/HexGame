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
            { 0, 0, 0, 0, 0, 0, 0, 0, 0},                     // 0   Y
              { 1, 0, 0, 0, 0, 0, 0, 0, 0},                    // 1
                { 1, 0, 0, 0, 0, 0, 0, 0, 1},                  // 2
                  { 1, 0, 0, 0, 0, 0, 0, 0, 0},                // 3
                    { 1, 0, 0, 0,-1, 0, 0, 0, 0},              // 4
                      { 1, 0, 0, 0, 0, 1, 0, 0, 0},            // 5
                        { 1, 0, 0,-1,-1,-1, 1,-1, 0},          // 6
                          { 1, 0, 1, 1, 1, 1,-1, 1, 0},        // 7
                            { 1, 0, 0, 0, 0, 0,-1, 0, 1}       // 8    Y
        };

        //System.out.println("ocupa: " + board[4][4]);

        HexGameStatus gs = new HexGameStatus(board, PlayerType.PLAYER1);
        Dijkstra d = new Dijkstra(gs);
        int n = d.getDistance(gs, PlayerType.PLAYER2, new Point(6, 8), new Point(4, 4));
        System.out.println("Distància: " + n);
    }
}

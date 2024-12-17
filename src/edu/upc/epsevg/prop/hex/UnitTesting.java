/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.hex;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
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
            { 0, 0, 0, 0,  0, 0, 0, 0, 0},                     // 0   Y
              { 0, 0, 0, 0, 0, 0, 0, 0, 0},                    // 1
                { 0, 0, 0, 0, 0, 0, 0, 0, 0},                  // 2
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0},                // 3
                    { 0, 0, 0, 0,-1, 0, 0, 0, 0},              // 4  
                      { 0, 0, 0, 0, 0, 1, 0, 0, 0},            // 5    
                        { 0, 0, 0,-1,-1,-1, 1,-1, 0},          // 6      
                          { 0, 0, 1, 1, 1, 1,-1, 1, 0},        // 7       
                            { 0, 0, 0, 0, 0, 0,-1, 0, 1}       // 8    Y         
        };


        HexGameStatus gs = new HexGameStatus(board, PlayerType.PLAYER1);
        int n = getHeuristica(gs);
        System.out.println("Heuristic: " + n);
    }
        
    static public int getHeuristica(HexGameStatus s) {
        
        int distJugador = calcularDistancia(s, PlayerType.PLAYER1);
        int distOponent = calcularDistancia(s, PlayerType.PLAYER2);
        
        return distOponent - distJugador;
    }
    
    static public int calcularDistancia(HexGameStatus s, PlayerType p) {
        int mida = s.getSize();
        int[][] dist = new int[mida][mida];
        boolean[][] visitat = new boolean[mida][mida];
        Queue<Point> queue = new PriorityQueue<>((a, b) -> dist[a.x][a.y] - dist[b.x][b.y]);
        
        //inicializar distancias
        for (int i = 0; i < mida; ++i) {
            for (int j = 0; j < mida; ++j) {
                dist[i][j] = Integer.MAX_VALUE;
                if((p == PlayerType.PLAYER1 && i == 0) || (p == PlayerType.PLAYER2 && j == 0)) {
                    dist[i][j] = 0;
                    queue.add(new Point(i, j));
                }
            }
        }
        
        //algorismo más o menos dijkstra
        while(!queue.isEmpty()) {
            Point punt = queue.poll();
            if (visitat[punt.x][punt.y]) continue;
            visitat[punt.x][punt.y] = true;

            for (Point v : getVeins(punt, mida)) {
                if (s.getPos(v.x, v.y) == PlayerType.getColor(p) || s.getPos(v.x, v.y) == 0) {
                    int novaDist = dist[punt.x][punt.y] + 1;
                    if (novaDist < dist[v.x][v.y]) {
                        dist[v.x][v.y] = novaDist;
                        queue.add(v);
                    }
                }
            }
        }
        
        //buscant distancia minima al costat oposat
        int minDist = Integer.MAX_VALUE;
        for (int i = 0; i < mida; i++) {    
            for (int j = 0; j < mida; j++) {
                if ((p == PlayerType.PLAYER1 && i == s.getSize() - 1) || (p == PlayerType.PLAYER2 && j == s.getSize() - 1)) {
                    minDist = Math.min(minDist, dist[i][j]);
                }
            }
        }

        return minDist;
    }
    
    static public List<Point> getVeins(Point p, int m) {
        int[][] dir = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, 1}, {1, -1}};
        List<Point> v = new ArrayList<>();
        for (int[] d : dir) {
            int x = p.x + d[0];
            int y = p.y + d[1];
            
            if(x >= 0 && x < m && y >= 0 && y < m) {
                v.add(new Point(x, y));
            }
        }
        
        return v;
    }
}

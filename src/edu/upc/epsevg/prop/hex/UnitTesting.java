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
        
        static int getHeuristica(HexGameStatus s) {
            int playerShortestPath = getShortestPath(s, PlayerType.PLAYER1);
            int opponentShortestPath = getShortestPath(s, PlayerType.PLAYER2);
            return opponentShortestPath - playerShortestPath;
        }

        static int getShortestPath(HexGameStatus s, PlayerType player) {
            PriorityQueue<Node> queue = new PriorityQueue<>((a, b) -> Integer.compare(a.distance, b.distance));
            boolean[][] visited = new boolean[s.getSize()][s.getSize()];
            int[][] distances = new int[s.getSize()][s.getSize()];

            for (int i = 0; i < s.getSize(); i++) {
                for (int j = 0; j < s.getSize(); j++) {
                    distances[i][j] = Integer.MAX_VALUE;
                }
            }

            // Initialize start nodes (borders based on player)
            if (player == PlayerType.PLAYER1) {
                for (int i = 0; i < s.getSize(); i++) {
                    queue.add(new Node(0, i, 0));
                    distances[0][i] = 0;
                }
            } else {
                for (int i = 0; i < s.getSize(); i++) {
                    queue.add(new Node(i, 0, 0));
                    distances[i][0] = 0;
                }
            }

            while (!queue.isEmpty()) {
                Node current = queue.poll();

                if (visited[current.x][current.y]) continue;
                visited[current.x][current.y] = true;

                for (Point neighbor : getNeighbors(current.x, current.y, s)) {
                    int nx = neighbor.x;
                    int ny = neighbor.y;

                    if (!visited[nx][ny] && s.getPos(nx, ny) != PlayerType.opposite(player).ordinal()) {
                        int newDist = distances[current.x][current.y] + (s.getPos(nx, ny) == player.ordinal() ? 0 : 1);
                        if (newDist < distances[nx][ny]) {
                            distances[nx][ny] = newDist;
                            queue.add(new Node(nx, ny, newDist));
                        }
                    }
                }
            }

            // Find the shortest path to the opposite border
            int shortestPath = Integer.MAX_VALUE;
            if (player == PlayerType.PLAYER1) {
                for (int i = 0; i < s.getSize(); i++) {
                    shortestPath = Math.min(shortestPath, distances[s.getSize() - 1][i]);
                }
            } else {
                for (int i = 0; i < s.getSize(); i++) {
                    shortestPath = Math.min(shortestPath, distances[i][s.getSize() - 1]);
                }
            }

            return shortestPath;
        }

        static List<Point> getNeighbors(int x, int y, HexGameStatus s) {
            List<Point> neighbors = new ArrayList<>();
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, 1}, {1, -1}};

            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx >= 0 && ny >= 0 && nx < s.getSize() && ny < s.getSize()) {
                    neighbors.add(new Point(nx, ny));
                }
            }

            return neighbors;
        }
    
        private static class Node {
            int x, y, distance;

            Node(int x, int y, int distance) {
                this.x = x;
                this.y = y;
                this.distance = distance;
            }
        }
}

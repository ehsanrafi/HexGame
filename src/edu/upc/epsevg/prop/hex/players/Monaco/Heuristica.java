package edu.upc.epsevg.prop.hex.players.Monaco;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
import java.awt.Point;

/**
 * Classe que implementa la lògica heurística per avaluar l'estat d'un
 * joc de Hex. Aquesta classe calcula una puntuació heurística basada en la
 * distància del jugador a la meta i factors estratègics com els dos punts de pont.
 *
 * @author Ehsan i Iván
 */
public class Heuristica {
    /**
     * Estat actual del joc.
     */
    private final HexGameStatus s;
    
    /**
     * Tipus del jugador actual.
     */
    private final PlayerType Jugador;
    
    //private static final HashMap<Long, Integer> evaluationCache = new HashMap<>();
    
    /**
     * Constructor de la classe Heuristica.
     *
     * @param hgs Estat del joc en un moment donat.
     * @param p Tipus del jugador actual.
     */
    public Heuristica(HexGameStatus hgs, PlayerType p) {
        this.s = hgs;
        this.Jugador = p;
    }

    /**
     * Calcula l'avaluació heurística de l'estat actual del joc.
     *
     * @param s Estat actual del joc.
     * @return Un enter que representa la puntuació heurística del jugador.
     */
    public int getEvaluation(HexGameStatus s) {
        /*
        long hash = s.getZobristHash();
        
        if (evaluationCache.containsKey(hash)) {
            return evaluationCache.get(hash);
        }
        */
        Dijkstra dGraf = new Dijkstra(s);
        
        int PlayerScore = dGraf.getDistance(Jugador);
        int EnemicScore = dGraf.getDistance(PlayerType.opposite(Jugador));
        
        int twoBridgeEvaluation = evaluateTwoBridgeState(Jugador);
        
        int PlayerEvaluation = Math.max(1, 100 - PlayerScore);
        int EnemicEvaluation = Math.max(1, 100 - EnemicScore);
        
        //evaluationCache.put(hash, 16 * (PlayerEvaluation - EnemicEvaluation) + twoBridgeEvaluation);
        
        return 16 * (PlayerEvaluation - EnemicEvaluation) + twoBridgeEvaluation;
    }
    
    /**
     * Avalua l'estat dels "dos punts de pont" del jugador donat.
     * Aquesta heurística considera la connexió entre dues cel·les
     * del mateix color que poden connectar-se a través d'una estructura de pont.
     *
     * @param Player Tipus del jugador (jugador actual).
     * @return Un enter que representa la puntuació de l'estat dels dos punts de pont.
     */
    public int evaluateTwoBridgeState(PlayerType Player) {
        int v = 0;
        int[][] directions = {
            {-1, -1}, {1, -2}, {2, -1}, {1, 1}, {-1, 2}, {-2, 1}
        };

        for (int i = 0; i < s.getSize(); ++i) {
            for (int j = 0; j < s.getSize(); ++j) {
                Point pCurrent = new Point(i, j);
                if (s.getPos(pCurrent) == PlayerType.getColor(Player)) {
                    for (int[] dir : directions) {
                        int x = i + dir[0];
                        int y = j + dir[1];
                        if (x >= 0 && x < s.getSize() && y >= 0 && y < s.getSize() && s.getPos(x, y) == PlayerType.getColor(Player)) {
                            if (dir[0] == -1 && dir[1] == -1) {
                                v += (s.getPos(i, j - 1) == 0 && s.getPos(i - 1, j) == 0) ? 2 : 0;
                            }
                            else if (dir[0] == 1 && dir[1] == -2) {
                                v += (s.getPos(i, j - 1) == 0 && s.getPos(i + 1, j - 1) == 0) ? 2 : 0;
                            }
                            else if (dir[0] == 2 && dir[1] == -1) {
                                v += (s.getPos(i + 1, j) == 0 && s.getPos(i + 1, j - 1) == 0) ? 2 : 0;
                            }
                            else if (dir[0] == 1 && dir[1] == 1) {
                                v += (s.getPos(i + 1, j) == 0 && s.getPos(i, j + 1) == 0) ? 2 : 0;
                            }
                            else if (dir[0] == -1 && dir[1] == 2) {
                                v += (s.getPos(i, j + 1) == 0 && s.getPos(i - 1, j + 1) == 0) ? 2 : 0;
                            }
                            else if (dir[0] == -2 && dir[1] == 1) {
                                v += (s.getPos(i - 1, j + 1) == 0 && s.getPos(i - 1, j) == 0) ? 2 : 0;
                            }
                        }
                    }
                }
            }
        }

        return v;
    }
}

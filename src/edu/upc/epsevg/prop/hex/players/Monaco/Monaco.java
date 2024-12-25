package edu.upc.epsevg.prop.hex.players.Monaco;


import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.MoveNode;
import edu.upc.epsevg.prop.hex.PlayerMove;
import edu.upc.epsevg.prop.hex.PlayerType;
import edu.upc.epsevg.prop.hex.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

/**
 * @author Ehsan i Iván
 */
public class Monaco implements IPlayer, IAuto {
    private final String name;
    private final boolean mode;
    private final int profunditat;
    private boolean timeout;
    private PlayerType Jugador;
    private PlayerType JugadorEnemic;
    private long jugadesExplorades;
    private int profMax;
    
    public Monaco(String name, boolean m, int prof) {
        this.name = name;
        this.mode = m;
        this.profunditat = prof;
    }
    
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public void timeout() {
        timeout = true;  
    }
    
    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public PlayerMove move(HexGameStatus s) {
        profMax = 0;
        timeout = false;
        jugadesExplorades = 0;
        int valorMesAlt = Integer.MIN_VALUE;
        int valor = Integer.MIN_VALUE;
        Point puntOptim = null;
        Jugador = s.getCurrentPlayer();
        JugadorEnemic = PlayerType.opposite(Jugador);
        
        if (mode) {
            for (int pActual = 1; !s.isGameOver() && !timeout; ++pActual) {
                Point puntActual = null;
                valorMesAlt = Integer.MIN_VALUE;
                valor = Integer.MIN_VALUE;
                
                for (MoveNode p : s.getMoves()) {
                    if(timeout) {
                        break;
                    }
                    
                    HexGameStatus AuxBoard = new HexGameStatus(s);
                    AuxBoard.placeStone(p.getPoint());
                    
                    valor = minimaxAlfaBeta(AuxBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, pActual - 1, false);
                    
                    if (valor > valorMesAlt && !timeout) {
                        valorMesAlt = valor;
                        puntActual = p.getPoint();
                    }
                    
                }
                
                if (!timeout) {
                    puntOptim = puntActual;
                    ++profMax;
                }
            }
        } else {
            for (MoveNode p : s.getMoves()) {
                HexGameStatus AuxBoard = new HexGameStatus(s);
                AuxBoard.placeStone(p.getPoint());

                valor = minimaxAlfaBeta(AuxBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, profunditat - 1, false);

                if (valor > valorMesAlt) {
                    valorMesAlt = valor;
                    puntOptim = p.getPoint();
                }
            }
        }
        
        return new PlayerMove(puntOptim, jugadesExplorades, profMax, mode ? SearchType.MINIMAX_IDS : SearchType.MINIMAX);
    }

    public int minimaxAlfaBeta(HexGameStatus s, int alfa, int beta, int profunditat, boolean maxJugador) {
        if (timeout && mode) {
            return 0;
        }

        if(s.isGameOver() || profunditat == 0) {
            if(!mode) profMax = Math.max(profMax, this.profunditat - profunditat);
            
            if (s.isGameOver()) {
                return (s.GetWinner() == Jugador) ? 10000 : -10000;
            } else {
                ++jugadesExplorades;
                return getHeuristica(s);
            }
        }
        
        int valor = maxJugador ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        
        for (MoveNode p : s.getMoves()) {
            HexGameStatus AuxBoard = new HexGameStatus(s);
            AuxBoard.placeStone(p.getPoint());
            
            if(maxJugador) {
               valor = Math.max(valor, minimaxAlfaBeta(AuxBoard, alfa, beta, profunditat - 1, false));
               if (beta <= valor) {
                   return valor;
               }

               alfa = Math.max(alfa, valor); 
           } else {
               valor = Math.min(valor, minimaxAlfaBeta(AuxBoard, alfa, beta, profunditat - 1, true));
               if (valor <= alfa) {
                   return valor;
               }

               beta = Math.min(beta, valor);
           }   
        }
        
        return valor;
    }
    
    public int getHeuristica(HexGameStatus s) {
        Dijkstra dGrafJugador = new Dijkstra(s);
        Dijkstra dGrafEnemic = new Dijkstra(s);
        
        int PlayerScore = dGrafJugador.getDistance(Jugador);
        int EnemicScore = dGrafEnemic.getDistance(JugadorEnemic);
        
        int PlayerEvaluation = Math.max(1, 100 - PlayerScore /* + BonusCamino?? */);
        int EnemicEvaluation = Math.max(1, 100 - EnemicScore);
        
        int twoBridgeAdvantage = calculateTwoBridgeAdvantage(s, Jugador);
        int criticalPointAdvantage = calculateCriticalPoints(s, Jugador, JugadorEnemic);
        //int boardControl = calculateBoardControl(s, Jugador, JugadorEnemic);

        
        return PlayerEvaluation - EnemicEvaluation + twoBridgeAdvantage + criticalPointAdvantage /*+ boardControl*/;
    }
    
    /**
     * Retorna el nom del jugador que s'utlilitza per visualització a la UI
     *
     * @return Nom del jugador
     */
    @Override
    public String getName() {
        return name;
    }
    
    public int calculateTwoBridgeAdvantage(HexGameStatus s, PlayerType jugador) {
        int advantage = 0;
        
        for (MoveNode p : s.getMoves()) {
            if(s.getPos(p.getPoint()) == PlayerType.getColor(jugador)) {
                for (int[] offset : new int[][]{{2, 0}, {0, 2}, {2, -2}, {-2, 2}, {2, 2}, {-2, -2}}) {
                    Point candidate = new Point(p.getPoint().x + offset[0], p.getPoint().y + offset[1]);
                    if (isValidTwoBridge(s, p.getPoint(), candidate, jugador)) {
                        advantage += 15; // Beneficio por un dos-puentes
                    }
                }
            }
        }
        return advantage;
    }

    public boolean isValidTwoBridge(HexGameStatus s, Point p1, Point p2, PlayerType jugador) {
        if (!isWithinBounds(s, p2)) return false;
        Point midpoint = new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
        return s.getPos(p2) == 0 && s.getPos(midpoint) == 0;
    }

    public boolean isWithinBounds(HexGameStatus s, Point p) {
        return p.x >= 0 && p.x < s.getSize() && p.y >= 0 && p.y < s.getSize();
    }

    public int calculateCriticalPoints(HexGameStatus s, PlayerType jugador, PlayerType enemic) {
        int advantage = 0;
        
        byte[][] boardAux = new byte[s.getSize()][s.getSize()];
        
        for (int i = 0; i < s.getSize(); ++i) {
            for (int j = 0; j < s.getSize(); ++j) {
                boardAux[j][i] = (byte) s.getPos(i, j);
            }
        }
        
        for (MoveNode p : s.getMoves()) {
            // Evaluar si este punto es crítico para ambos jugadores
            HexGameStatus auxBoardJugador = new HexGameStatus(boardAux, jugador);
            auxBoardJugador.placeStone(p.getPoint());
            int jugadorDistance = new Dijkstra(auxBoardJugador).getDistance(jugador);

            HexGameStatus auxBoardEnemic = new HexGameStatus(boardAux, enemic);
            auxBoardEnemic.placeStone(p.getPoint());
            int enemicDistance = new Dijkstra(auxBoardEnemic).getDistance(enemic);

            if (jugadorDistance < enemicDistance) {
                advantage += 5; // Beneficio por un punto crítico favorable
            } else if (jugadorDistance > enemicDistance) {
                advantage -= 5; // Penalización si el punto es favorable al enemigo
            }
        }
        return advantage;
    }

    /*
    public int calculateBoardControl(HexGameStatus s, PlayerType jugador, PlayerType enemic) {
        int controlScore = 0;
        
        for (MoveNode p : s.getMoves()) {
            int playerProximity = calculateProximityScore(s, p.getPoint(), jugador);
            int enemicProximity = calculateProximityScore(s, p.getPoint(), enemic);
            controlScore += playerProximity - enemicProximity;
        }
        return controlScore;
    }

    public int calculateProximityScore(HexGameStatus s, Point p, PlayerType jugador) {
        int proximityScore = 0;
        for (int[] dir : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, 1}, {1, -1}}) {
            Point neighbor = new Point(p.x + dir[0], p.y + dir[1]);
            if (isWithinBounds(s, neighbor) && s.getPos(neighbor) == PlayerType.getColor(jugador)) {
                proximityScore += 2; // Más peso a celdas cercanas ocupadas por el jugador
            }
        }
        return proximityScore;
    }
    */
}
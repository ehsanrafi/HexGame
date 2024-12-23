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
        this.timeout = false;
        this.jugadesExplorades = 0;
        
        if(m) {
            this.profMax = 0;
        } else {
            this.profMax = prof;
        }
    }

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
        int valorMesAlt = Integer.MIN_VALUE;
        int valor = Integer.MIN_VALUE;
        Point puntOptim = new Point();
        Jugador = s.getCurrentPlayer();
        JugadorEnemic = PlayerType.opposite(Jugador);
        
        for (int i = 0; i < s.getSize(); ++i) {
            for (int j = 0; j < s.getSize(); ++j) {
                if (s.getPos(new Point(i, j)) == 0) {
                    HexGameStatus AuxBoard = new HexGameStatus(s);
                    AuxBoard.placeStone(new Point(i, j));
                    
                    
                    if(mode && !timeout) {
                        valor = minimaxIDS(s);
                    } else {
                        valor = minimaxAlfaBeta(AuxBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, profunditat - 1, false);
                    }
                    
                    //valor = minimaxAlfaBeta(AuxBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, profunditat - 1, false);
                    
                    if (valor > valorMesAlt) {
                        valorMesAlt = valor;
                        puntOptim = new Point(i, j) ;
                    }
                }
            }
        }
        
        System.out.println("Heuristic: " + valor);
        return new PlayerMove(puntOptim, jugadesExplorades, profMax, mode ? SearchType.MINIMAX_IDS : SearchType.MINIMAX);
        //return new PlayerMove(puntOptim, jugadesExplorades, profMax, SearchType.MINIMAX);
    }

    public int minimaxAlfaBeta(HexGameStatus s, int alfa, int beta, int profunditat, boolean maxJugador) {
        if(s.isGameOver() || profunditat == 0) {
            if(s.isGameOver()) {
                return (s.GetWinner()) == Jugador ? 1000 : -1000;
            } else {
                ++jugadesExplorades;
                return getHeuristica(s);
            }
        }
        
        int valor = maxJugador ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        if((mode && !timeout) || !mode) {
            if(mode) profMax = Math.max(profMax, this.profunditat - profunditat);
            
            for (int i = 0; i < s.getSize(); ++i) {
                for (int j = 0; j < s.getSize(); ++j) {
                    if(s.getPos(i, j) == 0) {
                        HexGameStatus AuxBoard = new HexGameStatus(s);
                        AuxBoard.placeStone(new Point(i, j));

                        if(maxJugador) {
                            valor = Math.max(valor, minimaxAlfaBeta(AuxBoard, alfa, beta, profunditat - 1, false));
                            alfa = Math.max(alfa, valor); 
                        } else {
                            valor = Math.min(valor, minimaxAlfaBeta(AuxBoard, alfa, beta, profunditat - 1, true));
                            beta = Math.min(beta, valor);
                        }

                        if (alfa >= beta) break;
                    }
                }
            }
        }
        
        return valor;
    }
    
    public int minimaxIDS(HexGameStatus s) {
        //falta guardar primer movimiento del bucle anterior
        int millorValor = Integer.MIN_VALUE;
        int millorValorAux = Integer.MIN_VALUE;
        
        for(int pActual = 1; !timeout; ++pActual) {
            millorValor = millorValorAux;
            millorValorAux = minimaxAlfaBeta(s, Integer.MIN_VALUE, Integer.MAX_VALUE, pActual, true);
        }
        
        return millorValor;
    }
    
    public int getHeuristica(HexGameStatus s) {
        Dijkstra dGrafJugador = new Dijkstra(s, Jugador);
        Dijkstra dGrafEnemic = new Dijkstra(s, JugadorEnemic);
        int PlayerScore = dGrafJugador.shortestPath();
        int EnemicScore = dGrafEnemic.shortestPath();
        
        if (PlayerScore == 0) {
            return 1000;
        }
        
        if (EnemicScore == 0) {
            return -1000;
        }
        
        if(PlayerScore == Integer.MAX_VALUE) {
            return -950;
        } 
        
        if(EnemicScore == Integer.MAX_VALUE) {
            return 950;
        } 
        
        int PlayerEvaluation = Math.max(1, 100 - Math.abs(PlayerScore));
        int EnemicEvaluation = Math.max(1, 100 - Math.abs(EnemicScore));
        
        return PlayerEvaluation - EnemicEvaluation;
        
        //double PlayerEvaluation = Math.pow(2, -PlayerScore);
        //double EnemicEvaluation = Math.pow(2, -EnemicScore);
        
        //return PlayerEvaluation - EnemicEvaluation;
        
        //return (EnemicScore - PlayerScore);
    }  
   /* 
    public int getHeuristica(HexGameStatus s) {
        Dijkstra dGrafJugador = new Dijkstra(s, Jugador);
        Dijkstra dGrafEnemic = new Dijkstra(s, JugadorEnemic);
        int PlayerScore = dGrafJugador.shortestPath();
        int EnemicScore = dGrafEnemic.shortestPath();
        
        if(PlayerScore == Integer.MAX_VALUE) {
            return -1000;
        } 
        
        if(EnemicScore == Integer.MAX_VALUE) {
            return 1000;
        } 
        
        return (EnemicScore - PlayerScore);
    }
    */
    
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return name;
    }

}
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
 * Jugador aleatori
 * @author bernat
 */
public class Monaco implements IPlayer, IAuto {
    private String name;
    private boolean mode;
    private int profunditat;
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
        this.profMax = 0;
    }

    @Override
    public void timeout() {
        timeout = true;
    }

    //OPTIMITZACIÓ IDS
    /*
    Si ves que con i = 1, el mejor nodo es el tercero, con i = 2, empieza por el tercero.
    Para saber si un nodo es una repeticción, usar hash table --> adaptar zobrist hashing
    */
    
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
        Point puntOptim = new Point();
        Jugador = s.getCurrentPlayer();
        JugadorEnemic = PlayerType.opposite(Jugador);
        
        for (int i = 0; i < s.getSize(); ++i) {
            for (int j = 0; j < s.getSize(); ++j) {
                if (s.getPos(i, j) == 0) {
                    HexGameStatus AuxBoard = new HexGameStatus(s);
                    AuxBoard.placeStone(new Point(i, j));

                    int valor;
                    
                    /*
                    if(mode && !timeout) {
                        valor = minimaxIDS(s);
                    } else {
                        valor = minimaxAlfaBeta(AuxBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, profunditat - 1, false);
                    }
                    */
                    
                    valor = minimaxAlfaBeta(AuxBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, profunditat - 1, false);
                    
                    if (valor > valorMesAlt) {
                        valorMesAlt = valor;
                        puntOptim = new Point(i, j);
                    }
                }
            }
        }        
        
        //return new PlayerMove(puntOptim, jugadesExplorades, profMax, mode ? SearchType.MINIMAX_IDS : SearchType.MINIMAX);
        return new PlayerMove(puntOptim, jugadesExplorades, profMax, SearchType.MINIMAX);
    }

    public int minimaxAlfaBeta(HexGameStatus s, int alfa, int beta, int profunditat, boolean maxJugador) {
        boolean fFinal = s.isGameOver();
        if(fFinal || profunditat == 0) {
            if(fFinal && s.GetWinner() == Jugador) {
                return Integer.MAX_VALUE;
            } else if(fFinal && s.GetWinner() == JugadorEnemic) {
                return Integer.MIN_VALUE;
            } else {
                ++jugadesExplorades;
                return getHeuristica(s);
            }
        }
        
        int valor = maxJugador ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        //if((mode && !timeout) || !mode) {
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
        //}
        
        return valor;
    }
    
    /*
    public int minimaxIDS(HexGameStatus s) {
        //falta guardar primer movimiento del bucle anterior
        int millorValor = Integer.MIN_VALUE;
        int millorValorAux = Integer.MIN_VALUE;
        
        for(int pActual = 1; !timeout; ++pActual) {
            ++profMax;
            millorValor = millorValorAux;
            millorValorAux = minimaxAlfaBeta(s, Integer.MIN_VALUE, Integer.MAX_VALUE, pActual, true);
        }
        
        --profMax;
        return millorValor;
    }
    */
    
    public int getHeuristica(HexGameStatus s) {
        Dijkstra dGrafJugador = new Dijkstra(s, Jugador);
        Dijkstra dGrafEnemic = new Dijkstra(s, JugadorEnemic);
        
        //habría que ver esto
        //se supone que sPoint es (-1, -1) y tPoint (-2, -2)
        //hay que modificar getDistance para que sea solo así --> dGrafJuagor.shortestPath();
        //int PlayerScore = dGrafJugador.getDistance(s, Jugador, sPoint, tPoint);
        //int OpponentScore = dGrafEnemic.getDistance(s, Jugador, sPoint, tPoint);
        //y luego aplicar la heuristica 2
        return 0;
    }
    
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return name;
    }

}
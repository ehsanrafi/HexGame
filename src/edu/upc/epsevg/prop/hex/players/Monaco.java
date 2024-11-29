package edu.upc.epsevg.prop.hex.players;


import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.MoveNode;
import edu.upc.epsevg.prop.hex.PlayerMove;
import edu.upc.epsevg.prop.hex.PlayerType;
import edu.upc.epsevg.prop.hex.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Jugador aleatori
 * @author bernat
 */
public class Monaco implements IPlayer, IAuto {

    private String name;
    private boolean mode;
    private int profunditat;
    private boolean timeout = false;
    private PlayerType Jugador;
    private PlayerType JugadorEnemic;
    private long jugadesExplorades = 0;
    private int profMax = 0;
    

    public Monaco(String name, boolean m, int prof) {
        this.name = name;
        this.mode = m;
        this.profunditat = prof;
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
        Point puntOptim = new Point();
        Point punt;
        Jugador = s.getCurrentPlayer();
        JugadorEnemic = Jugador == PlayerType.PLAYER1 ? PlayerType.PLAYER2 : PlayerType.PLAYER1;
        
        for(int i=0;i<s.getSize();i++) {
            for(int j=0;j<s.getSize();j++) {
                if (s.getPos(i,j) == 0) {
                    punt = new Point(i,j);
                    HexGameStatus AuxBoard = new HexGameStatus(s);
                    AuxBoard.placeStone(punt);
                  
                    if (AuxBoard.GetWinner() == Jugador){
                        return new PlayerMove(punt, jugadesExplorades, profMax, mode == true ? SearchType.MINIMAX_IDS : SearchType.MINIMAX);
                    }

                    int valor;
                    if(mode) {
                        //MINIMAX_IDS
                    }
                    else {
                        //MINIMAX
                        valor = MiniMax(s, Integer.MIN_VALUE, Integer.MAX_VALUE, profunditat - 1, false);


                    }

                    // No sé si meterlo dentro de los ifs o aquí fuera
                    if(valor > valorMesAlt) {
                        valorMesAlt = valor;
                        puntOptim = punt;
                    }
                }
            }  
        }
        
        return new PlayerMove(puntOptim, jugadesExplorades, profMax, mode == true ? SearchType.MINIMAX_IDS : SearchType.MINIMAX);
    }

    public int MiniMax(HexGameStatus s, int alfa, int beta, int profunditat, boolean maxJugador) {
        if(s.isGameOver() || profunditat == 0) {
            if(s.GetWinner() == Jugador) {
                return 1000; //ejemplo de heurística, no definitivo
            } else if(s.GetWinner() == JugadorEnemic) {
                return -1000;
            } else {
                return getHeuristica(s);
            }
        }
        
        int valor;
        
        if(mode) {
            if(maxJugador) {

            } else {
                
            }
        } else {
            if (maxJugador) {
                valor = Integer.MIN_VALUE;
                
                for(int i = 0; i < s.getSize(); ++i) {
                    for(int j = 0; j < s.getSize(); ++j) {
                        if (s.getPos(i, j) == 0) {
                            //punt = new Point(i,j);
                            HexGameStatus AuxBoard = new HexGameStatus(s);
                            AuxBoard.placeStone(new Point(i, j));
                            
                            valor = Math.max(valor, MiniMax(AuxBoard, alfa, beta, profunditat - 1, false));
                            
                            if (beta <= valor) return valor;
                            alfa = Math.max(valor, alfa);
                        }
                    }
                }
            } else {
                valor = Integer.MAX_VALUE;
                
                for(int i = 0; i < s.getSize(); ++i) {
                    for(int j = 0; j < s.getSize(); ++j) {
                        if (s.getPos(i, j) == 0) {
                            //punt = new Point(i,j);
                            HexGameStatus AuxBoard = new HexGameStatus(s);
                            AuxBoard.placeStone(new Point(i, j));
                            
                            valor = Math.min(valor, MiniMax(AuxBoard, alfa, beta, profunditat - 1, true));
                            
                            if (valor <= alfa) return valor;
                            beta = Math.min(valor, beta);
                        }
                    }
                }
            }
        }
        
        return valor;
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

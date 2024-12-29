package edu.upc.epsevg.prop.hex.players.Monaco;


import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.MoveNode;
import edu.upc.epsevg.prop.hex.PlayerMove;
import edu.upc.epsevg.prop.hex.PlayerType;
import edu.upc.epsevg.prop.hex.SearchType;
import java.awt.Point;

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
        
        // Si no hay ficha en el centro, poner.
        if (s.getSize() % 2 != 0) {
            if (s.getPos(new Point(s.getSize() / 2, s.getSize() / 2)) == 0) {
                return new PlayerMove(new Point(s.getSize() / 2, s.getSize() / 2), 1, 1, mode ? SearchType.MINIMAX_IDS : SearchType.MINIMAX);
            }
        } else {
            if (s.getPos(new Point((s.getSize() / 2) + 1, (s.getSize() / 2) + 1)) == 0) {
                return new PlayerMove(new Point((s.getSize() / 2), (s.getSize() / 2)), 1, 1, mode ? SearchType.MINIMAX_IDS : SearchType.MINIMAX);
            }
        }
        
        
        for (MoveNode n : s.getMoves()) {
            HexGameStatus boardAux = new HexGameStatus(s);
            boardAux.placeStone(n.getPoint());
            
            if(s.isGameOver() && s.GetWinner() == Jugador) {
                return new PlayerMove(n.getPoint(), jugadesExplorades, 1, mode ? SearchType.MINIMAX_IDS : SearchType.MINIMAX);
            }
            ++jugadesExplorades;
        }
        
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
                return (s.GetWinner() == Jugador) ? 100000 : -100000;
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
               /*
               if (beta <= valor) {
                   return valor;
               }
               */
               alfa = Math.max(alfa, valor); 
           } else {
               valor = Math.min(valor, minimaxAlfaBeta(AuxBoard, alfa, beta, profunditat - 1, true));
               /*
               if (valor <= alfa) {
                   return valor;
               }
               */
               beta = Math.min(beta, valor);
           }
            
            if (beta <= alfa) {
                break;
            }
        }
        
        return valor;
    }
    
    public int getHeuristica(HexGameStatus s) {
        Heuristica h = new Heuristica(s, Jugador);
        
        return h.getEvaluation(s);
        
        //Dijkstra dGraf = new Dijkstra(s);
        
        //int PlayerScore = dGraf.getDistance(Jugador);
        //int EnemicScore = dGraf.getDistance(JugadorEnemic);
        /*
        int bridgeF = bridgeFactor(s);
        int centerF = focusCenter(s);
        
        bridgeF = bridgeF * 2;
        centerF = centerF * 2;
        int value = bridgeF + centerF;

        countConnectedNodes(s, PlayerType.getColor(Jugador));
        int AIValue = counter;
        counter = 0;
        
        countConnectedNodes(s, PlayerType.getColor(JugadorEnemic));
        int humanValue = counter;
        counter = 0;
        
        int boardState = AIValue - humanValue;
        
        return boardState + value /*+ boardControl;
        */
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
}
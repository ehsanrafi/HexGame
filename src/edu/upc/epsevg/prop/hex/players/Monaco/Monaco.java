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
 * Classe que implementa un jugador automàtic per al joc de Hex utilitzant
 * estratègies basades en Minimax amb poda alfa-beta i cerca iterativa si està
 * habilitada.
 * 
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
    
    /**
     * Constructor de la classe Monaco.
     * 
     * @param name Nom del jugador.
     * @param m Mode de cerca (true per a cerca iterativa, false per a cerca fixa).
     * @param prof Profunditat màxima de la cerca fixa.
     */
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
        
        // Si no hi ha fitxa al centre, posar-ne una.
            if (s.getSize() % 2 != 0) {
                if (s.getPos(new Point(s.getSize() / 2, s.getSize() / 2)) == 0) {
                    return new PlayerMove(new Point(s.getSize() / 2, s.getSize() / 2), 1, 1, mode ? SearchType.MINIMAX_IDS : SearchType.MINIMAX);
                }
            } else {
                if (s.getPos(new Point((s.getSize() / 2 - 1), (s.getSize() / 2))) == 0) {
                    return new PlayerMove(new Point((s.getSize() / 2 - 1), (s.getSize() / 2)), 1, 1, mode ? SearchType.MINIMAX_IDS : SearchType.MINIMAX);
                }
            }
            
            for (MoveNode n : s.getMoves()) {
                HexGameStatus boardAux = new HexGameStatus(s);
                boardAux.placeStone(n.getPoint());

                ++jugadesExplorades;
                if (boardAux.isGameOver() && boardAux.GetWinner() == Jugador) {
                    return new PlayerMove(n.getPoint(), jugadesExplorades, 1, mode ? SearchType.MINIMAX_IDS : SearchType.MINIMAX);
                }
            }
            
        // Cerca del millor moviment.
        if (mode) {
            for (int pActual = 1; !s.isGameOver() && !timeout; ++pActual) {
                Point puntActual = null;
                valorMesAlt = Integer.MIN_VALUE;
                valor = Integer.MIN_VALUE;
                
                for (MoveNode p : s.getMoves()) {
                    if (timeout) {
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
    
    /**
     * Implementa l'algorisme Minimax amb poda alfa-beta.
     * 
     * @param s Estat actual del joc.
     * @param alfa Valor alfa per a la poda.
     * @param beta Valor beta per a la poda.
     * @param profunditat Profunditat restant per explorar.
     * @param maxJugador Indica si el jugador actual és el maximitzador.
     * @return Valor heurístic del moviment.
     */
    public int minimaxAlfaBeta(HexGameStatus s, int alfa, int beta, int profunditat, boolean maxJugador) {
        if (timeout && mode) {
            return 0;
        }

        if (s.isGameOver() || profunditat == 0) {
            if (!mode) profMax = Math.max(profMax, this.profunditat - profunditat);
            
            if (s.isGameOver()) {
                return (s.GetWinner() == Jugador) ? 1000000 : -1000000;
            } else {
                ++jugadesExplorades;
                return getHeuristica(s);
            }
        }
        
        int valor = maxJugador ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        
        for (MoveNode p : s.getMoves()) {
            HexGameStatus AuxBoard = new HexGameStatus(s);
            AuxBoard.placeStone(p.getPoint());
            
            if (maxJugador) {
               valor = Math.max(valor, minimaxAlfaBeta(AuxBoard, alfa, beta, profunditat - 1, false));
               alfa = Math.max(alfa, valor); 
           } else {
               valor = Math.min(valor, minimaxAlfaBeta(AuxBoard, alfa, beta, profunditat - 1, true));
               beta = Math.min(beta, valor);
           }
            
            if (beta <= alfa) {
                break;
            }
        }
        
        return valor;
    }
    
    /**
     * Calcula l'heurística d'un estat de joc donat.
     * 
     * @param s Estat actual del joc.
     * @return Valor heurístic calculat.
     */
    public int getHeuristica(HexGameStatus s) {
        Heuristica h = new Heuristica(s, Jugador);
        
        return h.getEvaluation(s);
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
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
        Jugador = s.getCurrentPlayer();
        JugadorEnemic = PlayerType.opposite(Jugador);
        
        for(Point p : getValidMoves(s)) {
            HexGameStatus AuxBoard = new HexGameStatus(s);
            AuxBoard.placeStone(p);
            
            int valor;
            if(mode) {
                valor = minimaxIDS(AuxBoard, profunditat);
            } else {
                valor = minimaxAlfaBeta(AuxBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, profunditat - 1, false);
            }
            
            if (valor > valorMesAlt) {
                valorMesAlt = valor;
                puntOptim = p;
            }
        }        
        
        return new PlayerMove(puntOptim, jugadesExplorades, profMax, mode ? SearchType.MINIMAX_IDS : SearchType.MINIMAX);
    }
    
    public List<Point> getValidMoves(HexGameStatus s) {
        List<Point> validMoves = new ArrayList<>();
        for (int i = 0; i < s.getSize(); i++) {
            for (int j = 0; j < s.getSize(); j++) {
                if (s.getPos(i, j) == 0) {
                    validMoves.add(new Point(i, j));
                }
            }
        }
        return validMoves;
    }

    public int minimaxAlfaBeta(HexGameStatus s, int alfa, int beta, int profunditat, boolean maxJugador) {
        if(s.isGameOver() || profunditat == 0) {
            if(s.GetWinner() == Jugador) {
                return 1000; //ejemplo de heurística, no definitivo
            } else if(s.GetWinner() == JugadorEnemic) {
                return -1000;
            } else {
                ++jugadesExplorades;
                return getHeuristica(s);
            }
        }
        
        int valor;
        if(maxJugador) {
            valor = Integer.MIN_VALUE;
            for(Point p : getValidMoves(s)) {
                HexGameStatus AuxBoard = new HexGameStatus(s);
                AuxBoard.placeStone(p);
                
                valor = Math.max(valor, minimaxAlfaBeta(AuxBoard, alfa, beta, profunditat - 1, false));
                
                alfa = Math.max(alfa, valor);
                if (alfa >= beta) break;
            }
        } else {
            valor = Integer.MAX_VALUE;
            for(Point p : getValidMoves(s)) {
                HexGameStatus AuxBoard = new HexGameStatus(s);
                AuxBoard.placeStone(p);
                
                valor = Math.min(valor, minimaxAlfaBeta(AuxBoard, alfa, beta, profunditat - 1, true));
                
                beta = Math.min(beta, valor);
                if (alfa >= beta) break;
            }
        }
        return valor;
    }
    
    public int minimaxIDS(HexGameStatus s, int profunditat) {
        //falta hacer que cuando se haga el timeout, si no ha acabado de hacer toda
        //la busqueda que se quede con el valor anterior
        int millorValor = 0;
        
        for(int pActual = 1; pActual <= profunditat; ++pActual) {
            if(!timeout) {
                millorValor = minimaxAlfaBeta(s, Integer.MIN_VALUE, Integer.MAX_VALUE, profunditat, mode);
                profMax = pActual;
            } else {
                break;
            }
        }
        
        return millorValor;
    }
    
    public int getHeuristica(HexGameStatus s) {
        
        int distJugador = calcularDistancia(s, Jugador);
        int distOponent = calcularDistancia(s, JugadorEnemic);
        
        return distJugador - distOponent;
    }
    
    public int calcularDistancia(HexGameStatus s, PlayerType p) {
        int mida = s.getSize();
        int[][] dist = new int[mida][mida];
        boolean[][] visited = new boolean[mida][mida];
        //final int INFINIT = Integer.MAX_VALUE;
        
        for (int i = 0; i < mida; ++i) {
            for (int j = 0; j < mida; ++j) {
                dist[i][j] = Integer.MAX_VALUE;
            }
        }
        
        List<Point> queue = new ArrayList<>();
        
        if(p == PlayerType.PLAYER1) {
           for (int i = 0; i < mida; ++i) {
               if (s.getPos(0, i) == 0 || s.getPos(0, i) == PlayerType.getColor(p)) {
                   dist[0][i] = 0;
                   queue.add(new Point(0, i));
               }
           }
        } else if (p == PlayerType.PLAYER2) {
           for (int i = 0; i < mida; ++i) {
               if (s.getPos(i, 0) == 0 || s.getPos(i, 0) == PlayerType.getColor(p)) {
                   dist[i][0] = 0;
                   queue.add(new Point(i, 0));
               }
           }
        }
        
        while(!queue.isEmpty()) {
            Point pCurrent = queue.remove(0);
            int x = pCurrent.x;
            int y = pCurrent.y;
            
            if (visited[x][y]) continue;
            visited[x][y] = true;
            
            for (Point pN  : getAdjacents(x, y, mida)) {
                int cost = (s.getPos(pN) == 0 || s.getPos(pN) == PlayerType.getColor(p) ? 1 : 10);
                int newDist = dist[x][y] + cost;
                
                if(newDist < dist[pN.x][pN.y]) {
                    dist[pN.x][pN.y] = newDist;
                    queue.add(pN);
                }
            }
        }
        
        int minimumDist = Integer.MAX_VALUE;
        
        if(p == PlayerType.PLAYER1) {
            for (int i = 0; i < mida; ++i) {
                minimumDist = Math.min(minimumDist, dist[mida - 1][i]);
            }
        } else if(p == PlayerType.PLAYER2) {
            for (int i = 0; i < mida; ++i) {
                minimumDist = Math.min(minimumDist, dist[i][mida - 1]);
            } 
        }
        return minimumDist == Integer.MAX_VALUE ? 1000 : minimumDist;
    }

    public List<Point> getAdjacents(int x, int y, int mida) {
        List<Point> Neighbours = new ArrayList<>();
        int[][] direccions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, 1}, {1, -1}};
        
        for(int[] dir : direccions) {
            int nx = x + dir[0], ny = y + dir[1];
            
            if(nx >= 0 && nx < mida && ny >= 0 && ny < mida) {
                Neighbours.add(new Point(nx, ny));
            }  
        }
        return Neighbours;
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

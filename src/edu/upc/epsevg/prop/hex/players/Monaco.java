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
        
        for(Point p : getValidMoves(s)) {
            HexGameStatus AuxBoard = new HexGameStatus(s);
            AuxBoard.placeStone(p);
            
            int valor;
            if(mode) {
                valor = minimaxIDS(s);
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
                return X; //falta numero
            } else if(s.GetWinner() == JugadorEnemic) {
                return X;// falta numero
            } else {
                ++jugadesExplorades;
                return getHeuristica(s);
            }
        }
        
        int valor = maxJugador ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        if((mode && !timeout) || !mode) {
            if(maxJugador) {
                for(Point p : getValidMoves(s)) {
                    HexGameStatus AuxBoard = new HexGameStatus(s);
                    AuxBoard.placeStone(p);

                    valor = Math.max(valor, minimaxAlfaBeta(AuxBoard, alfa, beta, profunditat - 1, false));

                    alfa = Math.max(alfa, valor);
                    if (alfa >= beta) break;
                }
            } else {
                for(Point p : getValidMoves(s)) {
                    HexGameStatus AuxBoard = new HexGameStatus(s);
                    AuxBoard.placeStone(p);

                    valor = Math.min(valor, minimaxAlfaBeta(AuxBoard, alfa, beta, profunditat - 1, true));

                    beta = Math.min(beta, valor);
                    if (alfa >= beta) break;
                }
            }
        }
        return valor;
        
        /*
        if((mode && !timeout) || !mode) {
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
        */
    }
    
    public int minimaxIDS(HexGameStatus s) {
        //falta guardar primer movimiento del bucle anterior
        int millorValor = 0;
        int millorValorAux = 0;
        
        for(int pActual = 1; pActual < Integer.MAX_VALUE; ++pActual) {
            if(!timeout) {
                millorValor = millorValorAux;
                millorValorAux = minimaxAlfaBeta(s, Integer.MIN_VALUE, Integer.MAX_VALUE, pActual, true);
            } else {
                profMax = pActual - 1;
                break;
            }
        }
        
        return millorValor;
    }
    
    public int getHeuristica(HexGameStatus s) {
        
        int distJugador = calcularDistancia(s, Jugador);
        int distOponent = calcularDistancia(s, JugadorEnemic);
        
        return distOponent - distJugador;
    }
    
    public int calcularDistancia(HexGameStatus s, PlayerType p) {
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
    
    public List<Point> getVeins(Point p, int m) {
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
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return name;
    }

}
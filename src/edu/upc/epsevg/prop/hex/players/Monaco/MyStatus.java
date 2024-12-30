/*
package edu.upc.epsevg.prop.hex.players.Monaco;
import edu.upc.epsevg.prop.hex.HexGameStatus;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Ehsan i Iván
 */
/*
public class MyStatus extends HexGameStatus {
    private int hash;
    private long[][][] zobristTable;
    private Map<Long, Integer> transpositionTable = new HashMap<>();
    private Random random = new Random();
    
    
    
    public MyStatus(HexGameStatus hgs) {
        super(hgs);
        //this.hash = hgs.hash; //puede que haya que hacer un private int hash en HexGameStatus
        
        for (int x = 0; x < super.getSize(); x++) {
            for (int y = 0; y < super.getSize(); y++) {
                for (int s = 0; s < 3; s++) {
                    zobristTable[x][y][s] = random.nextLong();
                }
            }
        }
    }
    
    /*
    @Override
    public void placeStone(Point p) {
        super.placeStone(p);
        hashActual ^= zobristTable[x][y][estadoAnterior];
        hashActual ^= zobristTable[x][y][estadoNuevo];
        return hashActual;
    }
}
*/
package edu.upc.epsevg.prop.hex.players.Monaco;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import java.awt.Point;
import java.util.Random;

/**
 *
 * @author Ehsan i Iván
 */

public class MyStatus extends HexGameStatus {

    private long zobristHash;
    private final long[][] zobristTable;

    /**
     * Constructor que inicializa un nuevo estado del juego basado en otro estado.
     * @param hgs Estado base del juego.
     */
    public MyStatus(HexGameStatus hgs) {
        super(hgs);
        int mida = hgs.getSize();
        zobristTable = generateZobristTable(mida);
        zobristHash = calculateInitialHash();
    }

    /**
     * Genera la tabla de números aleatorios para Zobrist hashing.
     * @param size Dimensiones del tablero.
     * @return Tabla de números aleatorios.
     */
    private long[][] generateZobristTable(int size) {
        Random rand = new Random();
        long[][] table = new long[size * size][3]; // 3 estados: vacío, jugador 1, jugador 2
        for (int i = 0; i < size * size; i++) {
            for (int j = 0; j < 3; j++) {
                table[i][j] = rand.nextLong();
            }
        }
        return table;
    }

    /**
     * Calcula el hash inicial basado en el estado actual del tablero.
     * @return Hash inicial del tablero.
     */
    private long calculateInitialHash() {
        long hash = 0;
        for (int x = 0; x < getSize(); x++) {
            for (int y = 0; y < getSize(); y++) {
                int state = getPos(new Point(x, y)); // 0 = vacío, 1 = jugador 1, 2 = jugador 2
                
                int mappedState = (state == -1) ? 2 : state;
                
                int index = x * getSize() + y;
                
                hash ^= zobristTable[index][mappedState];
            }
        }
        return hash;
    }

    /**
     * Actualiza el hash después de colocar una piedra.
     * @param point Punto donde se coloca la piedra.
     * @param player Jugador que realiza el movimiento (1 o 2).
     */
    public void updateHash(Point point, int player) {
        int index = point.x * getSize() + point.y;
        int previousState = getPos(point); // Estado previo
        
        int mappedPreviousState = (previousState == -1) ? 2 : previousState;
        int mappedPlayer = (player == -1) ? 2 : player;
        
        zobristHash ^= zobristTable[index][mappedPreviousState]; // Eliminar estado previo
        zobristHash ^= zobristTable[index][mappedPlayer];       // Añadir nuevo estado
    }

    /**
     * Obtiene el hash actual del tablero.
     * @return Hash del estado actual.
     */
    public long getZobristHash() {
        return zobristHash;
    }

    /**
     * Coloca una piedra en el tablero y actualiza el hash.
     * @param point Punto donde se coloca la piedra.
     * @param player Jugador que realiza el movimiento.
     */
    @Override
    public void placeStone(Point point) {
        int player = getCurrentPlayerColor();// Asumimos que 1 = J1, 2 = J2
        updateHash(point, player);
        super.placeStone(point);
    }
}

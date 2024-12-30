package edu.upc.epsevg.prop.hex.players.Monaco;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import java.awt.Point;
import java.util.Random;

/**
 * Classe que representa l'estat del joc de Hex amb una implementació personalitzada
 * de la taula de Zobrist i el càlcul del seu hash.
 * Aquesta classe hereta de `HexGameStatus` i permet gestionar l'estat del joc
 * amb un sistema d'indexació basat en hashing per tal d'optimitzar la detecció
 * d'estats repetits.
 * 
 * @author Ehsan i Iván
 */
public class MyStatus extends HexGameStatus {
    private long zobristHash;
    private final long[][] zobristTable;

    /**
     * Constructor que inicialitza un nou estat del joc a partir d'un altre estat existent.
     * 
     * @param hgs Estat base del joc a partir del qual es crea el nou estat.
     */
    public MyStatus(HexGameStatus hgs) {
        super(hgs);
        int mida = hgs.getSize();
        zobristTable = generateZobristTable(mida);
        zobristHash = calculateInitialHash();
    }

    /**
     * Genera la taula de números aleatoris per a la tècnica de Zobrist hashing.
     * La taula conté un conjunt de valors aleatoris associats a cada cel·la del tauler
     * i als estats possibles de cada cel·la (buida, jugador 1 o jugador 2).
     * 
     * @param size Dimensió del tauler de joc (tamany).
     * @return Taula de números aleatoris utilitzada per al càlcul de Zobrist hashing.
     */
    private long[][] generateZobristTable(int size) {
        Random rand = new Random();
        long[][] table = new long[size * size][3];
        for (int i = 0; i < size * size; i++) {
            for (int j = 0; j < 3; j++) {
                table[i][j] = rand.nextLong();
            }
        }
        
        return table;
    }

    /**
     * Calcula el valor inicial del hash utilitzant Zobrist hashing basat en l'estat
     * actual del tauler. Aquest hash és únic per cada disposició de fitxes.
     * 
     * @return El hash inicial del tauler de joc.
     */
    private long calculateInitialHash() {
        long hash = 0;
        for (int x = 0; x < getSize(); x++) {
            for (int y = 0; y < getSize(); y++) {
                int state = getPos(new Point(x, y)) + 1;
                int index = x * getSize() + y;
                hash ^= zobristTable[index][state];
            }
        }
        
        return hash;
    }

    /**
     * Actualitza el valor del hash després d'haver col·locat una nova fitxa en el tauler.
     * El hash es modifica en funció de la nova posició ocupada i del jugador que fa el moviment.
     * 
     * @param point Punt on es col·loca la fitxa (coordenades de la casella).
     * @param player Jugador que realitza el moviment (1 o 2).
     */
    public void updateHash(Point point, int player) {
        int index = point.x * getSize() + point.y;
        int previousState = getPos(point) + 1;
        zobristHash ^= zobristTable[index][previousState];
        zobristHash ^= zobristTable[index][player];
    }

    /**
     * Obté el valor actual del hash basat en Zobrist hashing, que representa l'estat actual
     * del tauler de joc.
     * 
     * @return El valor del hash actual del tauler.
     */
    public long getZobristHash() {
        return zobristHash;
    }

    /**
     * Col·loca una fitxa al tauler i actualitza el valor del hash associat.
     * Aquesta operació inclou l'actualització del hash i la col·locació efectiva de la fitxa
     * a la posició indicada.
     * 
     * @param point Punt (coordenades) on es vol col·locar la fitxa.
     */
    @Override
    public void placeStone(Point point) {
        int player = getCurrentPlayerColor() + 1;
        updateHash(point, player);
        super.placeStone(point);
    }
}

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
public class MyStatus extends HexGameStatus {
    private int hash;
    private long[][][] zobristTable;
    private Map<Long, Integer> transpositionTable = new HashMap<>();
    private Random random = new Random();
    
    
    
    public MyStatus(HexGameStatus hgs) {
        super(hgs);
        this.hash = hgs.hash; //puede que haya que hacer un private int hash en HexGameStatus
        
        for (int x = 0; x < super.getSize(); x++) {
            for (int y = 0; y < super.getSize(); y++) {
                for (int s = 0; s < 3; s++) {
                    zobristTable[x][y][s] = random.nextLong();
                }
            }
        }
    }
    
    @Override
    public void placeStone(Point p) {
        super.placeStone(p);
        hashActual ^= zobristTable[x][y][estadoAnterior];
        hashActual ^= zobristTable[x][y][estadoNuevo];
        return hashActual;
    }
}

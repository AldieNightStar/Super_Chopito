package haxidenti.schopito;

import haxidenti.chopito.ChopitoAPI;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class SuperChopitoAPI {
    private static final int RADIUS = 32;

    public static void superChopAt(Location location, Plugin executorPlugin) {
        Block interactBlock = location.getBlock();

        int y = interactBlock.getY();
        int subRad = RADIUS / 2;
        int xFrom = interactBlock.getX() - subRad;
        int zFrom = interactBlock.getZ() - subRad;
        int xTo = xFrom + RADIUS;
        int zTo = zFrom + RADIUS;

        for (int x = xFrom; x < xTo; x++) {
            for (int z = zFrom; z < zTo; z++) {
                ChopitoAPI.chopAt(new Location(interactBlock.getWorld(), x, y, z), location, executorPlugin);
            }
        }
    }
}

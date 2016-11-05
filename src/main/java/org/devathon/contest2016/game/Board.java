package org.devathon.contest2016.game;

import org.devathon.contest2016.stuff.Direction;
import org.devathon.contest2016.stuff.Point2I;
import org.devathon.contest2016.stuff.TileType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Location;

/**
 * The board for the game
 */
public class Board {
    
    private TileType[][] board;
    private Direction[][] directions;
    private Game game;
    
    /**
     * Initialises this board
     *
     * @param xSize the number of tiles in x direction
     * @param zSize the number of tiles in z direction
     * @param game  the game this board belongs to
     */
    public Board(int xSize, int zSize, Game game) {
        board = new TileType[xSize][zSize];
        directions = new Direction[xSize][zSize];
        this.game = game;
        
        for (int x = 0; x < xSize; x++) {
            for (int z = 0; z < zSize; z++) {
                board[x][z] = TileType.AIR;
                directions[x][z] = Direction.UNDEFINED;
            }
        }
    }
    
    /**
     * Sets a tile on this board
     *
     * @param x    the x coordinate
     * @param z    the z coordinate
     * @param tile the tile to set
     */
    public void setTile(int x, int z, TileType tile) {
        board[x][z] = tile;
    }
    
    /**
     * Returns the tile at a giving postion
     *
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the tile at (x,z)
     */
    public TileType getTile(int x, int z) {
        return board[x][z];
    }
    
    /**
     * Sets the direction the flow is flowing at the given location
     *
     * @param x         the x coordinate
     * @param z         the z coordinate
     * @param direction the direction the flow should be flowing at (x,z)
     */
    public void setDirections(int x, int z, Direction direction) {
        this.directions[x][z] = direction;
    }
    
    /**
     * Returns the direction the flow is flowing at the given location
     *
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the direction the flow should be flowing at (x,z)
     */
    public Direction getDirection(int x, int z) {
        return directions[x][z];
    }
    
    public boolean checkWin(Map<TileType, Point2I> startPoints, Map<TileType, Point2I> endPoints) {
        for (TileType key : startPoints.keySet()) {
            Point2I start = startPoints.get(key);
            Point2I stop = endPoints.get(key);
            
            int startX = start.getX();
            int startZ = start.getZ();
            
            int stopX = stop.getX();
            int stopZ = stop.getZ();
            
            List<Point2I> visited = new ArrayList<>();
            visited.add(new Point2I(startX, startZ));
            boolean result = checkFlow(key, startX, startZ, stopX, stopZ, visited);
            
            // spawn particles
            for (Point2I point : visited) {
                Location loc = game.xzToLocation(point.getX(), point.getZ());
                loc.setY(loc.getY() + 1);
                loc.getWorld().spigot().playEffect(loc, Effect.HEART);//TODO do effect based on TileType
            }
            
            if (!result) {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean checkFlow(TileType tile, int startX, int startZ, int stopX, int stopZ, List<Point2I> visited) {
        System.out.println("visited " + visited.size());
        //check if we need the direction
        Map<Direction, Point2I> possibleRoutes = new HashMap<>();
        for (Direction dir : Direction.values()) {
            switch (dir) {
                case NORTH:
                    // check win
                    if (startX == stopX && startZ - 1 == stopZ) {
                        return true;
                    }
                    // don't check again
                    if (visited.contains(new Point2I(startX, startZ - 1))) {
                        continue;
                    }
                    // is still in bound?
                    if (isInBounds(startX, startZ - 1)) {
                        TileType nextTile = getTile(startX, startZ - 1);
                        // is right tile?
                        if (nextTile.equals(tile)) {
                            // cool!
                            possibleRoutes.put(Direction.NORTH, new Point2I(startX, startZ - 1));
                        }
                    }
                    break;
                case EAST:
                    // check win
                    if (startX + 1 == stopX && startZ == stopZ) {
                        return true;
                    }
                    // don't check again
                    if (visited.contains(new Point2I(startX + 1, startZ))) {
                        continue;
                    }
                    // is still in bound?
                    if (isInBounds(startX + 1, startZ)) {
                        TileType nextTile = getTile(startX + 1, startZ);
                        // is right tile?
                        if (nextTile.equals(tile)) {
                            // cool!
                            possibleRoutes.put(Direction.EAST, new Point2I(startX + 1, startZ));
                        }
                    }
                    break;
                case SOUTH:
                    // check win
                    if (startX == stopX && startZ + 1 == stopZ) {
                        return true;
                    }
                    // don't check again
                    if (visited.contains(new Point2I(startX, startZ + 1))) {
                        continue;
                    }
                    // is still in bound?
                    if (isInBounds(startX, startZ + 1)) {
                        TileType nextTile = getTile(startX, startZ + 1);
                        // is right tile?
                        if (nextTile.equals(tile)) {
                            // cool!
                            possibleRoutes.put(Direction.SOUTH, new Point2I(startX, startZ + 1));
                        }
                    }
                    break;
                case WEST:
                    // check win
                    if (startX - 1 == stopX && startZ == stopZ) {
                        return true;
                    }
                    // don't check again
                    if (visited.contains(new Point2I(startX - 1, startZ))) {
                        continue;
                    }
                    // is still in bound?
                    if (isInBounds(startX - 1, startZ)) {
                        TileType nextTile = getTile(startX - 1, startZ);
                        // is right tile?
                        if (nextTile.equals(tile)) {
                            // cool!
                            possibleRoutes.put(Direction.WEST, new Point2I(startX - 1, startZ));
                        }
                    }
                    break;
                case UNDEFINED:
                    break;
            }
        }
        
        // no possible route found == puzzle not solved
        if (possibleRoutes.size() == 0) {
            return false;
        }
        
        // only one possible route found == just use that
        if (possibleRoutes.size() == 1) {
            Point2I pos = possibleRoutes.values().iterator().next();
            visited.add(pos);
            return checkFlow(tile, pos.getX(), pos.getZ(), stopX, stopZ, visited);
        }
        
        // multiple routes found, do we have a sane direction?
        Point2I point = possibleRoutes.get(getDirection(startX, startZ));
        if (point == null) {
            // direction does not help, lets just abort...
            return false;
        } else {
            // lets use it!
            visited.add(point);
            return checkFlow(tile, point.getX(), point.getZ(), stopX, stopZ, visited);
        }
    }
    
    public boolean isInBounds(int x, int z) {
        return x < board.length && z < board[0].length && x >= 0 && z >= 0;
    }
}
package com.angrytanks.util;

import com.angrytanks.entity.Decoration;
import com.angrytanks.terrain.elements.Decor;
import com.angrytanks.terrain.elements.Grass;
import com.angrytanks.terrain.elements.Sand;
import com.angrytanks.terrain.elements.Landscape;
import com.angrytanks.terrain.DestructibleTerrain;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class DecorationFactory {


    //ONLY FOR DESTRUCTIBLE TERRAIN WITH PHYSICS
    public static DestructibleTerrain createDestructible(String type, double x, double y,
                                                         java.util.List<Point2D> vertices, Color fill) {
        if ("grass".equalsIgnoreCase(type)) {
            return new Grass(x, y, vertices, fill);
        }
        if ("sand".equalsIgnoreCase(type)) {
            return new Sand(x, y, vertices, fill);
        }
        if ("ground".equalsIgnoreCase(type)) {
            return new Landscape(vertices, fill);
        }
        return null;
    }


    //ONLY FOR DECORATION NOT THINGS WITHOUT PHYISCS
    public static Decoration createDecoration(String type, double x, double y,
                                              java.util.List<Point2D> vertices, Color fill) {
        if ("decor".equalsIgnoreCase(type)) {
            return new Decor(x, y, vertices, fill);
        }
        return null;
    }
}

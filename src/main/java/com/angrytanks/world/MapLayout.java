package com.angrytanks.world;

import com.angrytanks.entity.Actor;
import com.angrytanks.entity.Decoration;
import com.angrytanks.terrain.elements.Landscape;
import com.angrytanks.terrain.DestructibleTerrain;
import com.angrytanks.util.DecorationFactory;
import com.angrytanks.util.MapElement;
import com.angrytanks.util.SVGMapLoader;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class MapLayout {
    private final List<Landscape> groundSegments;
    private final List<Decoration> staticDecorations;


    private final List<DestructibleTerrain> DestroyableElement;

    private String backgroundName;
    private ImageView backgroundImageView;

    public MapLayout() {
        groundSegments = new ArrayList<>();
        staticDecorations = new ArrayList<>();



        DestroyableElement = new ArrayList<>();
    }

    public void loadFromSVG(String resourcePath) {

        List<MapElement> elements = SVGMapLoader.loadMapElements(resourcePath);

        if (elements.isEmpty()) {
            System.out.println("svg corrupt " + resourcePath);
            return;
        }

        this.backgroundName = SVGMapLoader.loadBackgroundName(resourcePath);
        System.out.println("Background" + backgroundName);


        for (MapElement me : elements) {
            String type = me.getType();
            Color fill = me.getFillColor();
            java.util.List<Point2D> verts = me.getVertices();

            if (type != null && type.contains("ground")) {

                DestructibleTerrain ground = DecorationFactory.createDestructible("ground",
                        verts.getFirst().getX(), verts.getFirst().getY(), verts, fill);
                addGroundSegment((Landscape) ground);
            } else if (type != null && type.contains("grass")) {

                DestructibleTerrain grass = DecorationFactory.createDestructible("grass",
                        verts.getFirst().getX(), verts.getFirst().getY(), verts, fill);
                addDynamicDecoration(grass);
            } else if (type != null && type.contains("sand")) {

                DestructibleTerrain sand = DecorationFactory.createDestructible("sand",
                        verts.getFirst().getX(), verts.getFirst().getY(), verts, fill);
                addDynamicDecoration(sand);
            } else if (type != null && type.contains("decor")) {
                Decoration decor = DecorationFactory.createDecoration("decor",
                        verts.getFirst().getX(), verts.getFirst().getY(), verts, fill);
                addStaticDecoration(decor);
            } else {
                System.out.println("???: " + type);
            }
        }

    }


    public String getBackgroundName() {
        return backgroundName;
    }

    public ImageView getBackgroundImageView() {
        return backgroundImageView;
    }

    public List<Landscape> getGroundSegments() {
        return groundSegments;
    }

    public List<Decoration> getStaticDecorations() {
        return staticDecorations;
    }

    public List<DestructibleTerrain> getDynamicDecorations() {
        return DestroyableElement;
    }

    public void addGroundSegment(Landscape ground) {
        groundSegments.add(ground);
    }

    public void addDynamicDecoration(DestructibleTerrain decor) {
        DestroyableElement.add(decor);
    }

    public void addStaticDecoration(Decoration decor) {
        staticDecorations.add(decor);
    }

    public List<Actor> getAllMapActors() {
        List<Actor> actors = new ArrayList<>();
        actors.addAll(groundSegments);
        actors.addAll(staticDecorations);
        actors.addAll(DestroyableElement);
        return actors;
    }
}

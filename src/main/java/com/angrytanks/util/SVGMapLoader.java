package com.angrytanks.util;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;


public class SVGMapLoader {


    public static List<MapElement> loadMapElements(String resourcePath) {
        List<MapElement> elements = new ArrayList<>();
        try {
            InputStream is = SVGMapLoader.class.getResourceAsStream(resourcePath);
            if (is == null) {
                System.err.println("SVG resource not found: " + resourcePath);
                return elements;
            }





            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();


            Element root = doc.getDocumentElement();
            String bgFile = root.getAttribute("background");



            Map<String, String> cssFills = parseCssStyles(doc);

            NodeList pathNodes = doc.getElementsByTagName("path");
            for (int i = 0; i < pathNodes.getLength(); i++) {
                Element el = (Element) pathNodes.item(i);

                String shapeId = el.getAttribute("id");
                String classAttr = el.getAttribute("class");
                Color fillColor = findFillColor(classAttr, cssFills);

                // geometry
                String d = el.getAttribute("d");
                List<Point2D> verts = parsePathData(d);
                if (d.trim().toLowerCase().endsWith("z")) {
                    verts = ensureClosedPolygon(verts);
                }

                MapElement me = new MapElement(shapeId, verts);
                me.setFillColor(fillColor);
                elements.add(me);
            }

            NodeList polygonNodes = doc.getElementsByTagName("polygon");
            for (int i = 0; i < polygonNodes.getLength(); i++) {
                Element el = (Element) polygonNodes.item(i);

                String shapeId = el.getAttribute("id");
                String classAttr = el.getAttribute("class");
                Color fillColor = findFillColor(classAttr, cssFills);

                String pointsStr = el.getAttribute("points").trim();
                List<Point2D> verts = parsePoints(pointsStr);
                verts = ensureClosedPolygon(verts);

                MapElement me = new MapElement(shapeId, verts);
                me.setFillColor(fillColor);
                elements.add(me);
            }

            NodeList polylineNodes = doc.getElementsByTagName("polyline");
            for (int i = 0; i < polylineNodes.getLength(); i++) {
                Element el = (Element) polylineNodes.item(i);

                String shapeId = el.getAttribute("id");
                String classAttr = el.getAttribute("class");
                Color fillColor = findFillColor(classAttr, cssFills);

                String pointsStr = el.getAttribute("points").trim();
                List<Point2D> verts = parsePoints(pointsStr);
                verts = ensureClosedPolygon(verts);

                MapElement me = new MapElement(shapeId, verts);
                me.setFillColor(fillColor);
                elements.add(me);
            }

            NodeList rectNodes = doc.getElementsByTagName("rect");
            for (int i = 0; i < rectNodes.getLength(); i++) {
                Element el = (Element) rectNodes.item(i);

                String shapeId = el.getAttribute("id");
                String classAttr = el.getAttribute("class");
                Color fillColor = findFillColor(classAttr, cssFills);

                double x = Double.parseDouble(el.getAttribute("x"));
                double y = Double.parseDouble(el.getAttribute("y"));
                double w = Double.parseDouble(el.getAttribute("width"));
                double h = Double.parseDouble(el.getAttribute("height"));

                List<Point2D> verts = new ArrayList<>();
                verts.add(new Point2D(x, y));
                verts.add(new Point2D(x + w, y));
                verts.add(new Point2D(x + w, y + h));
                verts.add(new Point2D(x,     y + h));
                verts = ensureClosedPolygon(verts);

                MapElement me = new MapElement(shapeId, verts);
                me.setFillColor(fillColor);
                elements.add(me);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return elements;
    }


    private static Map<String, String> parseCssStyles(Document doc) {
        Map<String, String> cssMap = new HashMap<>();
        NodeList styleNodes = doc.getElementsByTagName("style");
        if (styleNodes.getLength() > 0) {
            Element styleEl = (Element) styleNodes.item(0);
            String cssText = styleEl.getTextContent();


            String[] rules = cssText.split("}");
            for (String rule : rules) {
                rule = rule.trim();
                if (!rule.isEmpty() && rule.startsWith(".")) {
                    int braceIndex = rule.indexOf('{');
                    if (braceIndex > 0) {
                        String className = rule.substring(1, braceIndex).trim();
                        String inside = rule.substring(braceIndex + 1).trim();
                        String[] props = inside.split(";");
                        for (String prop : props) {
                            prop = prop.trim();
                            if (prop.startsWith("fill:")) {
                                String colorVal = prop.substring("fill:".length()).trim();
                                cssMap.put(className, colorVal);
                            }
                        }
                    }
                }
            }
        }
        return cssMap;
    }

    private static Color findFillColor(String classAttr, Map<String, String> cssFills) {
        if (classAttr == null || classAttr.isEmpty()) {
            return null;
        }
        String[] tokens = classAttr.split("\\s+");
        for (String token : tokens) {
            if (cssFills.containsKey(token)) {
                String colorStr = cssFills.get(token);
                try {
                    return Color.web(colorStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    public static List<Point2D> ensureClosedPolygon(List<Point2D> points) {
        if (!points.isEmpty()) {
            Point2D first = points.get(0);
            Point2D last = points.get(points.size() - 1);
            if (first.distance(last) > 0.5) {
                points.add(first);
            }
        }
        return points;
    }

    public static String[] splitByCommand(String d) {
        return d.split("(?=[MLHVZCSmlhvzcs])");
    }

    public static List<Point2D> parsePathData(String d) {
        List<Point2D> points = new ArrayList<>();
        d = d.replaceAll("(?<=\\d)-", " -");

        String[] tokens = splitByCommand(d);

        Point2D currentPoint = new Point2D(0, 0);
        Point2D lastControlPoint = null;
        char lastCommand = 0;

        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) continue;

            char command = token.charAt(0);
            boolean isRelative = Character.isLowerCase(command);

            String data = token.substring(1).trim();
            data = data.replaceAll("[a-zA-Z]", " ");
            data = data.replaceAll("\\s+", " ").trim();
            String[] numTokens = data.isEmpty() ? new String[0] : data.split("[,\\s]+");

            switch (Character.toUpperCase(command)) {
                case 'M': {
                    for (int i = 0; i + 1 < numTokens.length; i += 2) {
                        double x = Double.parseDouble(numTokens[i]);
                        double y = Double.parseDouble(numTokens[i + 1]);
                        if (isRelative) {
                            x += currentPoint.getX();
                            y += currentPoint.getY();
                        }
                        currentPoint = new Point2D(x, y);
                        points.add(currentPoint);
                    }
                    lastCommand = 'M';
                    break;
                }
                case 'L': {
                    for (int i = 0; i + 1 < numTokens.length; i += 2) {
                        double x = Double.parseDouble(numTokens[i]);
                        double y = Double.parseDouble(numTokens[i + 1]);
                        if (isRelative) {
                            x += currentPoint.getX();
                            y += currentPoint.getY();
                        }
                        currentPoint = new Point2D(x, y);
                        points.add(currentPoint);
                    }
                    lastCommand = 'L';
                    break;
                }
                case 'H': {
                    for (String s : numTokens) {
                        double x = Double.parseDouble(s);
                        if (isRelative) {
                            x += currentPoint.getX();
                        }
                        currentPoint = new Point2D(x, currentPoint.getY());
                        points.add(currentPoint);
                    }
                    lastCommand = 'H';
                    break;
                }
                case 'V': {
                    for (String s : numTokens) {
                        double y = Double.parseDouble(s);
                        if (isRelative) {
                            y += currentPoint.getY();
                        }
                        currentPoint = new Point2D(currentPoint.getX(), y);
                        points.add(currentPoint);
                    }
                    lastCommand = 'V';
                    break;
                }
                case 'C': {
                    for (int i = 0; i + 5 < numTokens.length; i += 6) {
                        double c1x = Double.parseDouble(numTokens[i]);
                        double c1y = Double.parseDouble(numTokens[i + 1]);
                        double c2x = Double.parseDouble(numTokens[i + 2]);
                        double c2y = Double.parseDouble(numTokens[i + 3]);
                        double ex = Double.parseDouble(numTokens[i + 4]);
                        double ey = Double.parseDouble(numTokens[i + 5]);

                        if (isRelative) {
                            c1x += currentPoint.getX(); c1y += currentPoint.getY();
                            c2x += currentPoint.getX(); c2y += currentPoint.getY();
                            ex  += currentPoint.getX(); ey  += currentPoint.getY();
                        }

                        List<Point2D> curvePoints = flattenCubicBezier(
                                currentPoint,
                                new Point2D(c1x, c1y),
                                new Point2D(c2x, c2y),
                                new Point2D(ex, ey),
                                20
                        );
                        for (int j = 1; j < curvePoints.size(); j++) {
                            currentPoint = curvePoints.get(j);
                            points.add(currentPoint);
                        }
                        lastControlPoint = new Point2D(c2x, c2y);
                    }
                    lastCommand = 'C';
                    break;
                }
                case 'S': {
                    for (int i = 0; i + 3 < numTokens.length; i += 4) {
                        double c2x = Double.parseDouble(numTokens[i]);
                        double c2y = Double.parseDouble(numTokens[i + 1]);
                        double ex  = Double.parseDouble(numTokens[i + 2]);
                        double ey  = Double.parseDouble(numTokens[i + 3]);

                        if (isRelative) {
                            c2x += currentPoint.getX();
                            c2y += currentPoint.getY();
                            ex  += currentPoint.getX();
                            ey  += currentPoint.getY();
                        }

                        Point2D c1;
                        if (lastCommand == 'C' || lastCommand == 'S') {
                            double reflectedX = 2 * currentPoint.getX() - lastControlPoint.getX();
                            double reflectedY = 2 * currentPoint.getY() - lastControlPoint.getY();
                            c1 = new Point2D(reflectedX, reflectedY);
                        } else {
                            c1 = currentPoint;
                        }

                        List<Point2D> curvePoints = flattenCubicBezier(
                                currentPoint,
                                c1,
                                new Point2D(c2x, c2y),
                                new Point2D(ex, ey),
                                20
                        );
                        for (int j = 1; j < curvePoints.size(); j++) {
                            currentPoint = curvePoints.get(j);
                            points.add(currentPoint);
                        }
                        lastControlPoint = new Point2D(c2x, c2y);
                    }
                    lastCommand = 'S';
                    break;
                }
                case 'Z': {
                    if (!points.isEmpty()) {
                        points.add(points.get(0));
                    }
                    lastCommand = 'Z';
                    break;
                }
                default:

                    break;
            }
        }
        return points;
    }

    public static List<Point2D> flattenCubicBezier(Point2D p0, Point2D p1, Point2D p2, Point2D p3, int segments) {
        List<Point2D> pts = new ArrayList<>();
        for (int i = 0; i <= segments; i++) {
            double t = i / (double) segments;
            double mt = 1 - t;

            double x = (mt*mt*mt)*p0.getX()
                    + 3*(mt*mt)*t * p1.getX()
                    + 3*mt*(t*t) * p2.getX()
                    + (t*t*t)    * p3.getX();
            double y = (mt*mt*mt)*p0.getY()
                    + 3*(mt*mt)*t * p1.getY()
                    + 3*mt*(t*t) * p2.getY()
                    + (t*t*t)    * p3.getY();
            pts.add(new Point2D(x, y));
        }
        return pts;
    }

    private static List<Point2D> parsePoints(String pointsStr) {
        List<Point2D> verts = new ArrayList<>();
        String[] pairs = pointsStr.split("\\s+");
        for (String pair : pairs) {
            String[] coords = pair.split(",");
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);
            verts.add(new Point2D(x, y));
        }
        return verts;
    }
    public static String loadBackgroundName(String resourcePath) {
        try {
            Document doc = loadDocument(resourcePath);
            if (doc == null) {
                return null;
            }
            Element rootEl = doc.getDocumentElement();
            return rootEl.getAttribute("background");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static Document loadDocument(String resourcePath) {
        try {
            InputStream is = SVGMapLoader.class.getResourceAsStream(resourcePath);
            if (is == null) {
                System.err.println("svg nt fond " + resourcePath);
                return null;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static String getBackgroundFile(Document doc) {
        Element root = doc.getDocumentElement();

        return root.getAttribute("data-background");
    }
}

package com.angrytanks.infrastructure.assets;

import com.angrytanks.model.assets.MapElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class SvgAssetLoader {
  private static final Logger LOG = Logger.getLogger(SvgAssetLoader.class.getName());
  private static final List<String> SHAPE_ORDER = List.of("path", "polygon", "polyline", "rect");

  private SvgAssetLoader() {}

  public static SvgAsset load(String resourcePath) {
    List<MapElement> elements = new ArrayList<>();
    String background = null;
    String failureContext = "Cannot read SVG resource: " + resourcePath;
    try {
      Document document = SvgDocumentReader.read(resourcePath);
      background = document.getDocumentElement().getAttribute("background");
      SvgClassStyles styles = SvgClassStyles.read(document);
      for (String tag : SHAPE_ORDER) {
        NodeList shapes = document.getElementsByTagName(tag);
        for (int i = 0; i < shapes.getLength(); i++) {
          Element shape = (Element) shapes.item(i);
          failureContext =
              "Cannot parse SVG <%s> id='%s' (index %s) in %s"
                  .formatted(tag, shape.getAttribute("id"), i, resourcePath);
          elements.add(
              new MapElement(
                  shape.getAttribute("id"),
                  SvgShapeParser.vertices(shape),
                  styles.fillFor(shape.getAttribute("class"))));
        }
      }
    } catch (IOException | SAXException | ParserConfigurationException | RuntimeException error) {
      LOG.log(Level.WARNING, failureContext, error);
    }
    return new SvgAsset(background, elements);
  }
}

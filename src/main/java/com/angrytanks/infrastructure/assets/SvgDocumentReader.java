package com.angrytanks.infrastructure.assets;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

final class SvgDocumentReader {
  private SvgDocumentReader() {}

  static Document read(String path) throws IOException, SAXException, ParserConfigurationException {
    try (var stream = SvgDocumentReader.class.getResourceAsStream(path)) {
      if (stream == null) throw new IllegalArgumentException("SVG resource not found: " + path);
      var factory = DocumentBuilderFactory.newInstance();
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      var document = factory.newDocumentBuilder().parse(stream);
      document.getDocumentElement().normalize();
      return document;
    }
  }
}

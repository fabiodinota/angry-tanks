package com.angrytanks.infrastructure.assets;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

final class SvgClassStyles {
  private final Map<String, String> fills;

  private SvgClassStyles(Map<String, String> fills) {
    this.fills = Map.copyOf(fills);
  }

  static SvgClassStyles read(Document document) {
    Map<String, String> fills = new HashMap<>();
    NodeList styles = document.getElementsByTagName("style");
    if (styles.getLength() == 0) return new SvgClassStyles(fills);

    for (String rawRule : styles.item(0).getTextContent().split("}")) {
      String rule = rawRule.trim();
      int brace = rule.indexOf('{');
      if (!rule.startsWith(".") || brace <= 0) {
        continue;
      }
      String className = rule.substring(1, brace).trim();
      for (String rawProperty : rule.substring(brace + 1).trim().split(";")) {
        String property = rawProperty.trim();
        if (property.startsWith("fill:")) {
          fills.put(className, property.substring("fill:".length()).trim());
        }
      }
    }
    return new SvgClassStyles(fills);
  }

  String fillFor(String classes) {
    if (classes == null || classes.isEmpty()) return null;

    for (String className : classes.split("\\s+")) {
      String fill = fills.get(className);
      if (fill != null) {
        return fill;
      }
    }
    return null;
  }
}

package com.angrytanks.infrastructure.assets;

import com.angrytanks.model.geometry.Point2;
import java.util.ArrayList;
import java.util.List;

final class SvgPathParser {
  private static final int CURVE_SEGMENTS = 20;

  private SvgPathParser() {}

  private static String[] splitByCommand(String data) {
    return data.split("(?=[MLHVZCSmlhvzcs])");
  }

  static List<Point2> parsePathData(String data) {
    var state = new PathState();
    for (String rawToken : splitByCommand(data.replaceAll("(?<=\\d)-", " -"))) {
      String token = rawToken.trim();
      if (token.isEmpty()) continue;
      char command = token.charAt(0);
      state.accept(
          Character.toUpperCase(command),
          Character.isLowerCase(command),
          numberTokens(token.substring(1).trim()));
    }
    return state.points;
  }

  private static String[] numberTokens(String data) {
    String normalized = data.replaceAll("[a-zA-Z]", " ").replaceAll("\\s+", " ").trim();
    return normalized.isEmpty() ? new String[0] : normalized.split("[,\\s]+");
  }

  private static List<Point2> flattenCubicBezier(
      Point2 p0, Point2 p1, Point2 p2, Point2 p3) {
    List<Point2> points = new ArrayList<>();
    for (int i = 0; i <= CURVE_SEGMENTS; i++) {
      double t = i / (double) CURVE_SEGMENTS;
      double inverseT = 1 - t;
      double x =
          (inverseT * inverseT * inverseT) * p0.x()
              + 3 * (inverseT * inverseT) * t * p1.x()
              + 3 * inverseT * (t * t) * p2.x()
              + (t * t * t) * p3.x();
      double y =
          (inverseT * inverseT * inverseT) * p0.y()
              + 3 * (inverseT * inverseT) * t * p1.y()
              + 3 * inverseT * (t * t) * p2.y()
              + (t * t * t) * p3.y();
      points.add(new Point2(x, y));
    }
    return points;
  }

  private static final class PathState {
    private final List<Point2> points = new ArrayList<>();
    private Point2 current = Point2.ZERO;
    private Point2 lastControl;
    private char previousCommand;

    void accept(char command, boolean relative, String[] numbers) {
      switch (command) {
        case 'M', 'L' -> appendLines(numbers, relative);
        case 'H' -> appendHorizontal(numbers, relative);
        case 'V' -> appendVertical(numbers, relative);
        case 'C' -> appendCubicCurves(numbers, relative);
        case 'S' -> appendSmoothCurves(numbers, relative);
        case 'Z' -> closePath();
        default -> {
          return;
        }
      }

      previousCommand = command;
    }

    private void appendLines(String[] numbers, boolean relative) {
      for (int i = 0; i + 1 < numbers.length; i += 2) {
        appendLine(readPoint(numbers, i, relative));
      }
    }

    private void appendHorizontal(String[] numbers, boolean relative) {
      for (String number : numbers) {
        double x = Double.parseDouble(number);
        if (relative) {
          x += current.x();
        }
        appendLine(new Point2(x, current.y()));
      }
    }

    private void appendVertical(String[] numbers, boolean relative) {
      for (String number : numbers) {
        double y = Double.parseDouble(number);
        if (relative) {
          y += current.y();
        }
        appendLine(new Point2(current.x(), y));
      }
    }

    private void appendLine(Point2 point) {
      current = point;
      points.add(point);
    }

    private void appendCubicCurves(String[] numbers, boolean relative) {
      for (int i = 0; i + 5 < numbers.length; i += 6) {
        Point2 firstControl = readPoint(numbers, i, relative);
        Point2 secondControl = readPoint(numbers, i + 2, relative);
        Point2 end = readPoint(numbers, i + 4, relative);
        appendCurve(firstControl, secondControl, end);
      }
    }

    private void appendSmoothCurves(String[] numbers, boolean relative) {
      for (int i = 0; i + 3 < numbers.length; i += 4) {
        Point2 secondControl = readPoint(numbers, i, relative);
        Point2 end = readPoint(numbers, i + 2, relative);
        Point2 firstControl = current;
        if (previousCommand == 'C' || previousCommand == 'S') {
          firstControl =
              new Point2(2 * current.x() - lastControl.x(), 2 * current.y() - lastControl.y());
        }
        appendCurve(firstControl, secondControl, end);
      }
    }

    private Point2 readPoint(String[] numbers, int index, boolean relative) {
      double x = Double.parseDouble(numbers[index]);
      double y = Double.parseDouble(numbers[index + 1]);
      if (relative) {
        x += current.x();
        y += current.y();
      }
      return new Point2(x, y);
    }

    private void appendCurve(Point2 firstControl, Point2 secondControl, Point2 end) {
      List<Point2> samples =
          flattenCubicBezier(current, firstControl, secondControl, end);
      points.addAll(samples.subList(1, samples.size()));
      current = samples.getLast();
      lastControl = secondControl;
    }

    private void closePath() {
      if (!points.isEmpty()) {
        points.add(points.getFirst());
      }
    }
  }
}

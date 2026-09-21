package com.angrytanks.view.fx.game;

import com.angrytanks.model.geometry.Point2;
import com.angrytanks.model.snapshot.EntitySnapshot;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

final class TankRenderer implements EntityRenderer {
  private final Group root = new Group();
  private final Group tank = new Group();
  private final Group hull = new Group();
  private final Group turret = new Group();
  private final Node hullFill;
  private final Node hullOutline;
  private final Polygon cannon;
  private final Point2 cannonPivot;
  private boolean outlined;

  TankRenderer(EntitySnapshot state) {
    var appearance = state.tankAppearance();
    hullFill = Shapes.polygon(appearance.hull().outline(), appearance.hull().fill(), Color.GRAY);
    hullOutline = Shapes.outlines(appearance.hull().parts());
    hull.getChildren().add(hullFill);
    Group tracks =
        new Group(
            Shapes.polygon(
                appearance.tracks().outline(), appearance.tracks().fill(), Color.DARKGRAY));
    Group wheels = new Group();
    for (var wheel : appearance.wheels()) {
      wheels.getChildren().add(Shapes.polygon(wheel.outline(), wheel.fill(), Color.BLACK));
    }
    tracks.getChildren().add(wheels);
    var decor =
        Shapes.polygon(appearance.decor().outline(), appearance.decor().fill(), Color.WHITE);
    cannon = Shapes.polygon(appearance.cannon().outline(), appearance.cannon().fill(), Color.BLACK);
    cannonPivot = appearance.cannonPivot();
    turret
        .getChildren()
        .addAll(
            Shapes.polygon(
                appearance.turret().outline(), appearance.turret().fill(), Color.DARKSLATEGRAY),
            new Group(cannon));
    tank.getChildren().addAll(hull, tracks, decor, turret);
    root.getChildren().add(tank);
  }

  @Override
  public Node node() {
    return root;
  }

  @Override
  public void update(EntitySnapshot state, boolean outlines) {
    if (outlined != outlines) {
      outlined = outlines;
      hull.getChildren().setAll(outlines ? hullOutline : hullFill);
    }
    if (!state.turretPresent()) tank.getChildren().remove(turret);
    updatePose(state);
  }

  private void updatePose(EntitySnapshot state) {
    double cosine = Math.cos(state.angle());
    double sine = Math.sin(state.angle());
    double mirrorSign = state.mirrored() ? -1 : 1;
    double matrixXX = cosine * mirrorSign;
    double matrixXY = -sine;
    double matrixYX = sine * mirrorSign;
    double matrixYY = cosine;
    double pivotX = state.pivotX();
    double pivotY = state.pivotY();
    tank.getTransforms()
        .setAll(
            new Affine(
                matrixXX,
                matrixXY,
                state.x() + pivotX - matrixXX * pivotX - matrixXY * pivotY,
                matrixYX,
                matrixYY,
                state.y() + pivotY - matrixYX * pivotX - matrixYY * pivotY));
    cannon
        .getTransforms()
        .setAll(new Rotate(Math.toDegrees(state.cannonAngle()), cannonPivot.x(), cannonPivot.y()));
  }
}

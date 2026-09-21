package com.angrytanks.model.geometry;

public final class GeometryException extends IllegalArgumentException {
  private final String stage;

  public GeometryException(String stage, String reason) {
    super(stage + ": " + reason);
    this.stage = stage;
  }

  public GeometryException(String stage, String reason, Throwable cause) {
    super(stage + ": " + reason, cause);
    this.stage = stage;
  }

  public String stage() {
    return stage;
  }

  public GeometryException withContext(String context) {
    return new GeometryException(stage, context + ": " + getMessage(), this);
  }
}

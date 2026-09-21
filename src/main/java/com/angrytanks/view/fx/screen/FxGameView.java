package com.angrytanks.view.fx.screen;

import com.angrytanks.model.api.GameAction;
import com.angrytanks.model.api.Registration;
import com.angrytanks.view.contract.GameFrame;
import com.angrytanks.view.contract.GameView;
import com.angrytanks.view.fx.game.InGameHud;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public final class FxGameView implements GameView {
  @FXML private AnchorPane gameContainer;
  private InGameHud hud;
  private boolean fireKeyHeld;

  @Override
  public Registration bind(Actions actions) {
    Scene scene = gameContainer.getScene();
    if (scene == null) throw new IllegalStateException("Game view must be attached before binding");
    hud = new InGameHud(actions::decompositionChanged);
    fireKeyHeld = false;
    gameContainer.getChildren().setAll(hud.getHudPane());
    EventHandler<KeyEvent> input =
        event -> {
          GameAction action = translateInput(event);
          if (action != null) actions.gameAction(action);
          event.consume();
        };
    scene.addEventHandler(KeyEvent.ANY, input);
    return () -> scene.removeEventHandler(KeyEvent.ANY, input);
  }

  private GameAction translateInput(KeyEvent event) {
    if (event.getEventType() == KeyEvent.KEY_PRESSED) {
      return switch (event.getCode()) {
        case A -> GameAction.MOVE_LEFT;
        case D -> GameAction.MOVE_RIGHT;
        case W -> GameAction.AIM_UP;
        case S -> GameAction.AIM_DOWN;
        case G -> fireOncePerPress();
        default -> null;
      };
    }
    if (event.getEventType() != KeyEvent.KEY_RELEASED) {
      return null;
    }

    return switch (event.getCode()) {
      case G -> {
        fireKeyHeld = false;
        yield null;
      }
      case A, D -> GameAction.STOP;
      default -> null;
    };
  }

  private GameAction fireOncePerPress() {
    if (fireKeyHeld) return null;
    fireKeyHeld = true;
    return GameAction.FIRE;
  }

  @Override
  public void display(GameFrame frame) {
    hud.display(frame);
  }

  @Override
  public void clear() {
    fireKeyHeld = false;
    if (hud != null) hud.clear();
    hud = null;
    if (gameContainer != null) gameContainer.getChildren().clear();
  }
}

package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Tile extends Rectangle {

  private Piece piece;

  public boolean hasPiece() {
    return piece != null;
  }

  public Piece getPiece() {
    return piece;
  }

  public void setPiece(Piece piece) {
    this.piece = piece;
  }

  public Tile(boolean light, int x, int y) {
    setWidth(LineOfAction.TILE_SIZE);
    setHeight(LineOfAction.TILE_SIZE);

    relocate(x * LineOfAction.TILE_SIZE, y * LineOfAction.TILE_SIZE);

    setFill(light ? Color.valueOf("#E59866") : Color.valueOf("#EDBB99"));
  }
}
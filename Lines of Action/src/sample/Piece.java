package sample;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import java.util.ArrayList;
import java.util.LinkedList;

import static sample.LineOfAction.TILE_SIZE;

public class Piece extends StackPane {

  private Tile[][] board;
  private PieceType type;
  private Tile occupiedTile;
  ArrayList<Integer> highlightedPositions;
  boolean connected;

  private double mouseX, mouseY;
  private double oldX, oldY;


  public void setOccupiedTile(Tile occupiedTile) {
    this.occupiedTile = occupiedTile;
  }

  public Tile getOccupiedTile() {
    return occupiedTile;
  }

  public PieceType getType() {
    return type;
  }

  public double getOldX() {
    return oldX;
  }

  public double getOldY() {
    return oldY;
  }

  public Piece(PieceType type, Tile[][] board, Tile occupiedTile, int x, int y) {
    this.type = type;
    this.occupiedTile=occupiedTile;
    this.board=board;
    highlightedPositions=new ArrayList<>();
    connected=false;

    move(x, y);

    Ellipse bg = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
    bg.setFill(Color.BLACK);

    bg.setStroke(Color.BLACK);
    bg.setStrokeWidth(TILE_SIZE * 0.03);

    bg.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
    bg.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2 + TILE_SIZE * 0.07);

    Ellipse ellipse = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
    ellipse.setFill(type == PieceType.BLACK
            ? Color.valueOf("#444444") : Color.valueOf("#fff9f4"));

    ellipse.setStroke(Color.BLACK);
    ellipse.setStrokeWidth(TILE_SIZE * 0.03);

    ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
    ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2);

    getChildren().addAll(bg, ellipse);

    setOnMousePressed(e -> {
      if(!LineOfAction.TURN && type==PieceType.BLACK)
        findAvailableMoves(true);
      else if(LineOfAction.TURN && type==PieceType.WHITE && !LineOfAction.playAI)
        findAvailableMoves(true);
      mouseX = e.getSceneX();
      mouseY = e.getSceneY();
    });

    setOnMouseDragged(e -> {
      relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
    });
  }

  public void findAvailableMoves(boolean highlight) {
    int positionX = (int) oldX / 100;
    int positionY = (int) oldY / 100;
    //System.out.println(positionX + " , " + positionY);
    //horizontal highlight
    int count = 0;
    for (int i = 0; i < LineOfAction.HEIGHT; i++) {
      if (board[i][positionY].hasPiece()) count++;
    }
    if (positionX + count < LineOfAction.HEIGHT){
      int i;
      for (i = positionX + 1; i < positionX + count; i++)
        if (board[i][positionY].hasPiece() && board[i][positionY].getPiece().getType() != type)
          break;
      if (i == positionX + count && !board[positionX + count][positionY].hasPiece()) {
        if(highlight) board[positionX + count][positionY].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX + count);
        highlightedPositions.add(positionY);
      } else if (i == positionX + count && board[positionX + count][positionY].getPiece().getType() != type) {
        if(highlight) board[positionX + count][positionY].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX + count);
        highlightedPositions.add(positionY);
      }
    }
    if(positionX - count >= 0){
      int i;
      for(i=positionX-count+1;i<positionX;i++)
        if (board[i][positionY].hasPiece() && board[i][positionY].getPiece().getType() != type)
          break;
      if (i == positionX  && !board[positionX-count][positionY].hasPiece()) {
        if(highlight) board[positionX-count][positionY].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX-count);
        highlightedPositions.add(positionY);
      }
      else if (i == positionX  && board[positionX-count][positionY].getPiece().getType() != type) {
        if(highlight) board[positionX-count][positionY].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX-count);
        highlightedPositions.add(positionY);
      }
    }

    //vertical
    count = 0;
    for (int i = 0; i < LineOfAction.HEIGHT; i++) {
      if (board[positionX][i].hasPiece()) count++;
    }
    if (positionY + count < LineOfAction.HEIGHT){
      int i;
      for (i = positionY + 1; i < positionY + count; i++)
        if (board[positionX][i].hasPiece() && board[positionX][i].getPiece().getType() != type)
          break;
      if (i == positionY + count && !board[positionX][positionY+count].hasPiece()) {
        if(highlight) board[positionX][positionY+count].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX);
        highlightedPositions.add(positionY+count);
      } else if (i == positionY + count && board[positionX][positionY+count].getPiece().getType() != type) {
        if(highlight) board[positionX][positionY+count].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX);
        highlightedPositions.add(positionY+count);
      }
    }
    if(positionY - count >= 0){
      int i;
      for(i=positionY-count+1;i<positionY;i++)
        if (board[positionX][i].hasPiece() && board[positionX][i].getPiece().getType() != type)
          break;
      if (i == positionY  && !board[positionX][positionY-count].hasPiece()) {
        if(highlight) board[positionX][positionY-count].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX);
        highlightedPositions.add(positionY-count);
      }
      else if (i == positionY  && board[positionX][positionY-count].getPiece().getType() != type) {
        if(highlight) board[positionX][positionY-count].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX);
        highlightedPositions.add(positionY-count);
      }
    }

    //diagonal top-left to bottom-right
    count = 0;
    int[] arr=getDiagonalOneStartCoord(positionX,positionY);
    //System.out.println("diagonal top left start coord " + arr[0] + " , " + arr[1]);

    while(arr[0]<LineOfAction.WIDTH && arr[1]<LineOfAction.HEIGHT){
      if(board[arr[0]][arr[1]].hasPiece()) count++;
      arr[0]++;
      arr[1]++;
    }
    if(positionX+count<LineOfAction.HEIGHT && positionY+count<LineOfAction.HEIGHT){
      int i,j;
      for(i=positionX+1,j=positionY+1;i<positionX+count && j<positionY+count;i++,j++)
        if (board[i][j].hasPiece() && board[i][j].getPiece().getType() != type)
          break;
      if (i==positionX+count && !board[positionX+count][positionY+count].hasPiece()) {
        if(highlight) board[positionX+count][positionY+count].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX+count);
        highlightedPositions.add(positionY+count);
      } else if (i==positionX+count && board[positionX+count][positionY+count].getPiece().getType() != type) {
        if(highlight) board[positionX+count][positionY+count].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX+count);
        highlightedPositions.add(positionY+count);
      }
    }
    if(positionX-count>=0 && positionY-count>=0){
      int i,j;
      for(i=positionX-count+1,j=positionY-count+1;i<positionX && j<positionY;i++,j++)
        if (board[i][j].hasPiece() && board[i][j].getPiece().getType() != type)
          break;
      if (i==positionX && !board[positionX-count][positionY-count].hasPiece()) {
        if(highlight) board[positionX-count][positionY-count].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX-count);
        highlightedPositions.add(positionY-count);
      } else if (i==positionX && board[positionX-count][positionY-count].getPiece().getType() != type) {
        if(highlight) board[positionX-count][positionY-count].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX-count);
        highlightedPositions.add(positionY-count);
      }
    }

    //diagonal top-right to bottom-left

    count = 0;
    //System.out.println(positionX + " , " + positionY);
    arr=getDiagonalOtherStartCoord(positionX,positionY);

    while(arr[0]>=0 && arr[1]<LineOfAction.HEIGHT){
      if(board[arr[0]][arr[1]].hasPiece()) count++;
      arr[0]--;
      arr[1]++;
    }

    if(positionX+count<LineOfAction.HEIGHT && positionY-count>=0){
      int i,j;
      for(i=positionX+1,j=positionY-1;i<positionX+count && j>positionY-count;i++,j--)
        if (board[i][j].hasPiece() && board[i][j].getPiece().getType() != type)
          break;
      if (i==positionX+count && !board[positionX+count][positionY-count].hasPiece()) {
        if(highlight) board[positionX+count][positionY-count].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX+count);
        highlightedPositions.add(positionY-count);
      } else if (i==positionX+count && board[positionX+count][positionY-count].getPiece().getType() != type) {
        if(highlight) board[positionX+count][positionY-count].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX+count);
        highlightedPositions.add(positionY-count);
      }
    }
    if(positionX-count>=0 && positionY+count<LineOfAction.HEIGHT){
      int i,j;
      for(i=positionX-1,j=positionY+1;i>positionX-count && j<positionY+count;i--,j++)
        if (board[i][j].hasPiece() && board[i][j].getPiece().getType() != type)
          break;
      if (i==positionX-count && !board[positionX-count][positionY+count].hasPiece()) {
        if(highlight) board[positionX-count][positionY+count].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX-count);
        highlightedPositions.add(positionY+count);
      } else if (i==positionX-count && board[positionX-count][positionY+count].getPiece().getType() != type) {
        if(highlight) board[positionX-count][positionY+count].setFill(Color.valueOf("#2ECC71"));
        highlightedPositions.add(positionX-count);
        highlightedPositions.add(positionY+count);
      }
    }

  }

  private int[] getDiagonalOneStartCoord(int a,int b){
    int[] arr=new int[2];
    arr[0]=a;
    arr[1]=b;
    while (true){
      if(arr[0]==0 || arr[1]==0) break;
      else{
        arr[0]--;
        arr[1]--;
      }
    }

    return arr;
  }

  private int[] getDiagonalOtherStartCoord(int a,int b){
    int[] arr=new int[2];
    arr[0]=a;
    arr[1]=b;
    while (true){
      if(arr[1]==0 || arr[0]==LineOfAction.HEIGHT-1) break;
      else{
        arr[0]++;
        arr[1]--;
      }
    }
    return arr;
  }

  public boolean isAllConnected(){

    int count=getConnectedCount();
    //System.out.println("connected count : " + count);
    if(type==PieceType.BLACK && count==LineOfAction.BLACKCOUNT) return true;
    else if(type==PieceType.WHITE && count==LineOfAction.WHITECOUNT) return true;

    return false;
  }

  public int getConnectedCount(){
    int count=0;
    LinkedList<Piece> queue=new LinkedList<>();
    ArrayList<Piece> tmp=new ArrayList<>();
    queue.add(this);
    tmp.add(this);
    count++;
    connected=true;

    while(!queue.isEmpty()){
      Piece piece=queue.poll();
      ArrayList<Piece> np=getNeighbourPieces(piece);
      if(!np.isEmpty()){
        for(int i=0;i<np.size();i++){
          //System.out.println("neighbour piece : " + (int)np.get(i).getOldX()/100 + "," + (int)np.get(i).getOldY()/100);
          if(!np.get(i).connected){
            np.get(i).connected=true;
            count++;
            queue.add(np.get(i));
            tmp.add(np.get(i));
          }
        }
      }
    }
    for(int i=0;i<tmp.size();i++) tmp.get(i).connected=false;
    tmp.clear();

    return count;
  }

  private ArrayList<Piece> getNeighbourPieces(Piece piece) {
    int positionX = (int) piece.oldX / 100;
    int positionY = (int) piece.oldY / 100;
    //System.out.println("finding neighbour of : " + positionX + "," + positionY);
    ArrayList<Piece> arr=new ArrayList<>();
    if(positionX-1>=0 && board[positionX-1][positionY].hasPiece() && board[positionX-1][positionY].getPiece().getType()==piece.getType())
      arr.add(board[positionX - 1][positionY].getPiece());
    if(positionX+1<LineOfAction.WIDTH && board[positionX+1][positionY].hasPiece() && board[positionX+1][positionY].getPiece().getType()==piece.getType())
      arr.add(board[positionX + 1][positionY].getPiece());
    if(positionY-1>=0 && board[positionX][positionY-1].hasPiece() && board[positionX][positionY-1].getPiece().getType()==piece.getType())
      arr.add(board[positionX][positionY-1].getPiece());
    if(positionY+1<LineOfAction.WIDTH && board[positionX][positionY+1].hasPiece() && board[positionX][positionY+1].getPiece().getType()==piece.getType())
      arr.add(board[positionX][positionY+1].getPiece());
    if(positionX+1<LineOfAction.WIDTH && positionY+1<LineOfAction.WIDTH && board[positionX+1][positionY+1].hasPiece()
            && board[positionX+1][positionY+1].getPiece().getType()==piece.getType())
      arr.add(board[positionX+1][positionY+1].getPiece());
    if(positionX-1>=0 && positionY-1>=0 && board[positionX-1][positionY-1].hasPiece()
            && board[positionX-1][positionY-1].getPiece().getType()==piece.getType())
      arr.add(board[positionX-1][positionY-1].getPiece());
    if(positionX+1<LineOfAction.WIDTH && positionY-1>=0 && board[positionX+1][positionY-1].hasPiece()
            && board[positionX+1][positionY-1].getPiece().getType()==piece.getType())
      arr.add(board[positionX+1][positionY-1].getPiece());
    if(positionX-1>=0 && positionY+1<LineOfAction.WIDTH && board[positionX-1][positionY+1].hasPiece()
            && board[positionX-1][positionY+1].getPiece().getType()==piece.getType())
      arr.add(board[positionX-1][positionY+1].getPiece());

    return arr;
  }

  public void move(int x, int y) {
    oldX = x * TILE_SIZE;
    oldY = y * TILE_SIZE;
    relocate(oldX, oldY);
  }

  public void abortMove() {
    relocate(oldX, oldY);
  }
}
package sample;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

public class LineOfAction extends Application {

  public static final int TILE_SIZE = 100;
  public static int WHITECOUNT;
  public static int BLACKCOUNT;
  public static boolean TURN=false;
  public static boolean playAI=false;
  public static int WIDTH;
  public static int HEIGHT;
  public static int DEPTH=3;
  public static final int[][] pieceSquareTable = {
          { -80, -25, -20, -20, -20, -20, -25, -80 },
          { -25,  10,  10,  10,  10,  10,  10, -25 },
          { -20,  10,  25,  25,  25,  25,  10, -20 },
          { -20,  10,  25,  50,  50,  25,  10, -20 },
          { -20,  10,  25,  50,  50,  25,  10, -20 },
          { -20,  10,  25,  25,  25,  25,  10, -20 },
          { -25,  10,  10,  10,  10,  10,  10, -25 },
          { -80, -25, -20, -20, -20, -20, -25, -80 }
  };

  private ArrayList<Stage> stages=new ArrayList<>();
  private Tile[][] board = new Tile[WIDTH][HEIGHT];
  private ArrayList<Piece> whiteAiPieces=new ArrayList<>();

  private Group tileGroup = new Group();
  private Group pieceGroup = new Group();


  @Override
  public void start(Stage primaryStage) throws Exception {
    selectOption();
    Scene scene = new Scene(createContent());
    primaryStage.setTitle("Lines of Action");
    primaryStage.setScene(scene);
    primaryStage.show();
    stages.add(primaryStage);
  }

  private void selectOption(){
    ButtonType humanVsAI = new ButtonType("Human vs AI", ButtonBar.ButtonData.OK_DONE);
    ButtonType humanVshuman = new ButtonType("Human vs Human", ButtonBar.ButtonData.CANCEL_CLOSE);
    Alert alert=new Alert(Alert.AlertType.INFORMATION,"none",humanVsAI,humanVshuman);
    alert.setTitle("Lines of Action");
    alert.setHeaderText("Select Option :");
    alert.initModality(Modality.APPLICATION_MODAL);
    Optional<ButtonType> bd = alert.showAndWait();

    if (bd.orElse(humanVsAI) == humanVsAI) playAI=true;
    else if (bd.orElse(humanVshuman) == humanVshuman) playAI=false;
  }


  private Parent createContent() {
    WHITECOUNT=BLACKCOUNT=(WIDTH-2)*2;
    Pane root = new Pane();
    root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
    root.getChildren().addAll(tileGroup, pieceGroup);

    for (int y = 0; y < HEIGHT; y++) {
      for (int x = 0; x < WIDTH; x++) {
        Tile tile = new Tile((x + y) % 2 == 0, x, y);
        board[x][y] = tile;

        tileGroup.getChildren().add(tile);

        Piece piece = null;

        if ((y == 0 || y == HEIGHT-1) && ( x!=0 && x!=WIDTH-1 ) ) {
          piece = makePiece(PieceType.BLACK, tile, x, y);
          piece.setOccupiedTile(tile);
        }

        if ((x == 0 || x == WIDTH-1) && ( y!=0 && y!=WIDTH-1 ) ) {
          piece = makePiece(PieceType.WHITE, tile, x, y);
          piece.setOccupiedTile(tile);
          whiteAiPieces.add(piece);
        }

        if (piece != null) {
          tile.setPiece(piece);
          pieceGroup.getChildren().add(piece);
        }
      }
    }

    return root;
  }

  private boolean hasMove(Piece piece){
    piece.findAvailableMoves(false);
    if(piece.highlightedPositions.size()>0) return true;
    return false;

  }
  private void aiRandomMove(){

    Random random=new Random();
    Piece piece=null;
    for(int i=0;i<WHITECOUNT;i++){
      if(hasMove(whiteAiPieces.get(i))) {
        piece=whiteAiPieces.get(i);
        break;
      }
    }
    int x=Math.abs(random.nextInt(piece.highlightedPositions.size()/2));
    if(x%2!=0) x--;
    int newx=piece.highlightedPositions.get(x);
    int newy=piece.highlightedPositions.get(x+1);
    MoveResult result=tryMove(board,piece,newx,newy);
    movePieceToPosition(board,result,piece,newx,newy,true);
    for(int k=0;k<piece.highlightedPositions.size()-1;k+=2){
      System.out.println(piece.highlightedPositions.get(k) + "," + piece.highlightedPositions.get(k+1) + "  ");
    }
    System.out.println();
    piece.highlightedPositions.clear();
    Tile[][] newd=getNewBoard(board);

  }

  private Tile[][] getNewBoard(Tile[][] demo){
    Tile[][] tmpBoard=new Tile[WIDTH][HEIGHT];
    for(int i=0;i<WIDTH;i++){
      for(int j=0;j<HEIGHT;j++){
        Tile tile=new Tile((i+j)%2==0,i,j);
        tmpBoard[i][j]=tile;
        if(demo[i][j].hasPiece())
          tmpBoard[i][j].setPiece(new Piece(demo[i][j].getPiece().getType(),tmpBoard,tile,i,j));
        else tmpBoard[i][j].setPiece(null);
      }
    }
    //System.out.println("done");
    return tmpBoard;
  }

  private void aiHeuristicMove(){

    int score=0,bestScore=Integer.MIN_VALUE;
    int prevX=-1;
    int prevY=-1;
    int finalX=-1;
    int finalY=-1;

    Tile[][] tmpBoard=getNewBoard(board);

    for(int i=0;i<WIDTH;i++){
      for(int j=0;j<HEIGHT;j++){
        if(tmpBoard[i][j].hasPiece() && tmpBoard[i][j].getPiece().getType()==PieceType.WHITE){
          //System.out.println("white guti(i,j) = " + i + "," + j);
          Piece piece=tmpBoard[i][j].getPiece();

          if(hasMove(piece)){
            /*for(int k=0;k<piece.highlightedPositions.size()-1;k+=2){
              System.out.println(piece.highlightedPositions.get(k) + "," + piece.highlightedPositions.get(k+1) + "  ");
            }
            System.out.println();*/
            ArrayList<Integer> arr = (ArrayList<Integer>) piece.highlightedPositions.clone();
            piece.highlightedPositions.clear();
            for(int k=0;k<arr.size()-1;k+=2){
              int newx=arr.get(k);
              int newy=arr.get(k+1);
              //System.out.println("Tending move at (" + newx + "," + newy + ")");
              //System.out.println(newx + " " + newy);
              MoveResult result=aiTryMove(tmpBoard,newx,newy);
              //System.out.println(result.getType());
              movePieceToPosition(tmpBoard,result,piece,newx,newy,false);
              /*for(int x=0;x<WIDTH;x++) {
                for (int y = 0; y < HEIGHT; y++) {
                  if(tmpBoard[x][y].hasPiece() && tmpBoard[x][y].getPiece().getType()==PieceType.WHITE)
                    System.out.print(x + "," + y + "   ");
                }
                System.out.println();
              }*/
              //System.out.println("reachedasfffffffffffffffafa");
              score=miniMax(tmpBoard,false,Integer.MIN_VALUE,Integer.MAX_VALUE,DEPTH);
              //System.out.println("Score = " + score);

              //System.out.println("Score = " + score);
              movePieceToPosition(tmpBoard,new MoveResult(MoveType.NORMAL),piece,i,j,false);

              if(result.getType()==MoveType.KILL) tmpBoard[newx][newy].setPiece(result.getPiece());
              if(score>bestScore){
                bestScore=score;
                prevX=i;
                prevY=j;
                finalX=newx;
                finalY=newy;
              }
            }
          }else System.out.println("There is no move");
        }
      }
    }
    //System.out.println("Bestscore = " + bestScore);
    //System.out.println("reachedasfffffffffffffffafa");
    //System.out.println("(finalX,finalY) = " + " ( " + finalX + " , " + finalY + " ) ");
    Piece p=board[prevX][prevY].getPiece();
    //System.out.println(p.getOldX()/100 + " " + p.getOldY()/100);
    hasMove(p);
    movePieceToPosition(board,tryMove(board,p,finalX,finalY),p,finalX,finalY,true);
  }

  private boolean checkVirtualWin(Tile[][] demoBoard,PieceType pieceType){
    for(int i=0;i<WIDTH;i++) {
      for (int j = 0; j < HEIGHT; j++) {
        if (demoBoard[i][j].hasPiece() && demoBoard[i][j].getPiece().getType() == pieceType) {
          if (demoBoard[i][j].getPiece().isAllConnected())  return true;
          else return false;
        }
      }
    }
    return false;
  }

  private boolean chceckOpponentWin(PieceType pieceType){
    PieceType pt;
    if(pieceType==PieceType.BLACK) pt=PieceType.WHITE;
    else pt=PieceType.BLACK;

    return checkVirtualWin(board,pt);
  }

  private int miniMax(Tile[][] demoBoard,boolean isMax,int alpha,int beta,int depth) {

    int score = 0, bestScore;
    boolean flag = false;

    boolean blackWin=checkVirtualWin(demoBoard,PieceType.BLACK);
    boolean whiteWin=checkVirtualWin(demoBoard,PieceType.WHITE);

    if(blackWin && whiteWin){
      if (isMax) return Integer.MIN_VALUE + 1; // black moved last
      else return Integer.MAX_VALUE - 1;
    }else if(blackWin) return Integer.MIN_VALUE+1;
    else if(whiteWin) return Integer.MAX_VALUE-1;

    if (depth == 0) {

      score = getScore(demoBoard);
      //System.out.println(score);
      return score;
    }

    if (isMax) {
      bestScore = Integer.MIN_VALUE;
      //System.out.println("enter max func with depth = " + depth);
      for (int i = 0; i < WIDTH && !flag; i++) {
        for (int j = 0; j < HEIGHT && !flag; j++) {
          if (demoBoard[i][j].hasPiece() && demoBoard[i][j].getPiece().getType() == PieceType.WHITE) {
            //System.out.println("white guti(i,j) at depth(" + depth + ") = " + i + "," + j);
            Piece piece = demoBoard[i][j].getPiece();
            if (hasMove(piece)) {
              ArrayList<Integer> arr = (ArrayList<Integer>) piece.highlightedPositions.clone();
              piece.highlightedPositions.clear();
              for (int k = 0; k < arr.size() - 1; k += 2) {
                int newx = arr.get(k);
                int newy = arr.get(k + 1);
                //System.out.println("Tending move at (" + newx + "," + newy + ")");
                MoveResult result=aiTryMove(demoBoard,newx,newy);
                movePieceToPosition(demoBoard, result, piece, newx, newy, false);
                score = miniMax(demoBoard, false, alpha, beta, depth - 1);
                movePieceToPosition(demoBoard, new MoveResult(MoveType.NORMAL), piece, i, j, false);
                if (result.getType() == MoveType.KILL) demoBoard[newx][newy].setPiece(result.getPiece());
                if (score>bestScore) bestScore = score;
                if (bestScore>alpha) alpha = bestScore;
                if (beta<=alpha) flag = true;
              }
            }
          }
        }
      }
    }else{
      bestScore=Integer.MAX_VALUE;
      //System.out.println("enter min func");
      for (int i = 0; i < WIDTH && !flag; i++) {
        for (int j = 0; j < HEIGHT && !flag; j++) {
          if (demoBoard[i][j].hasPiece() && demoBoard[i][j].getPiece().getType() == PieceType.BLACK) {
            //System.out.println("black guti(i,j) at depth(" + depth + ") = " + i + "," + j);
            Piece piece = demoBoard[i][j].getPiece();
            if (hasMove(piece)) {
              ArrayList<Integer> arr = (ArrayList<Integer>) piece.highlightedPositions.clone();
              piece.highlightedPositions.clear();
              for (int k = 0; k < arr.size() - 1; k += 2) {
                int newx = arr.get(k);
                int newy = arr.get(k + 1);
                //System.out.println("movable option size = " + piece.highlightedPositions.size());
                //System.out.println(" still black Tending move at (" + newx + "," + newy + ")");
                MoveResult result=aiTryMove(demoBoard,newx,newy);
                movePieceToPosition(demoBoard, result, piece, newx, newy, false);
                //System.out.println("here");
                score = miniMax(demoBoard, true, alpha, beta, depth - 1);
                movePieceToPosition(demoBoard, new MoveResult(MoveType.NORMAL), piece, i, j, false);
                if (result.getType() == MoveType.KILL) demoBoard[newx][newy].setPiece(result.getPiece());
                if (score<bestScore) bestScore = score;
                if (score<alpha) alpha = score;
                if (beta<=alpha) flag = true;
              }
            }
          }
        }
      }
    }

    return bestScore;
  }

  private int getScore(Tile[][] demoBoard){
    int pieceTableWeightage=1;
    int areaWeightage=-2;
    int connectedWeightage=4;
    int quadCountWeightage=3;
    int densityWeightage=-3;
    int totalScore= getDensity(demoBoard)*densityWeightage +
                    //getArea(demoBoard)*areaWeightage +
                    getConnectednessCount(demoBoard)*connectedWeightage +
                    //getQuadCount(demoBoard)*quadCountWeightage +
                    pieceTableWeightage*pieceSquareScore(demoBoard);

    return totalScore;
  }

  private int pieceSquareScore(Tile[][] demoBoard)
  {
    int whiteScore =0, blackScore=0;
    for(int i=0;i<HEIGHT;i++){
      for(int j=0;j<WIDTH;j++){
        if(demoBoard[i][j].hasPiece()){
          int x=0;
          if(WIDTH==6) x=1;
          if(demoBoard[i][j].getPiece().getType()==PieceType.BLACK)
            blackScore+= pieceSquareTable[i+x][j+x];
          else whiteScore+=pieceSquareTable[i+x][j+x];
        }
      }
    }
    return whiteScore-blackScore;
  }

  private int getArea(Tile[][] tiles){
    int lx=100;
    int ux=-100;
    int ly=100;
    int uy=-100;
    for(int i=0;i<WIDTH;i++){
      for(int j=0;j<HEIGHT;j++){
        if(tiles[i][j].hasPiece() && tiles[i][j].getPiece().getType()==PieceType.WHITE){
          if(i<lx) lx=i;
          if(i>ux) ux=i;
          if(j<ly) ly=j;
          if(j>uy) uy=j;
        }
      }
    }
    return (ux-lx)*(uy-ly);
  }

  private int getConnectednessCount(Tile[][] tiles){
    int blackCount = Integer.MIN_VALUE,whiteCount = Integer.MIN_VALUE;
    for(int i=0;i<WIDTH;i++) {
      for (int j = 0; j < HEIGHT; j++) {
        if(tiles[i][j].hasPiece() && tiles[i][j].getPiece().getType()==PieceType.WHITE){
          int tmp=tiles[i][j].getPiece().getConnectedCount();
          if(tiles[i][j].getPiece().getType()==PieceType.BLACK) blackCount=Math.max(blackCount,tmp);
          else whiteCount=Math.max(whiteCount,tmp);
        }
      }
    }

    return whiteCount-blackCount;
  }

  private int getQuadCount(Tile[][] tiles){
    int finalCount=0;
    int count;
    for(int i=0;i<WIDTH-1;i++) {
      for (int j = 0; j < HEIGHT-1; j++) {
        count=0;
        for(int k=0;k<2;k++){
          for(int l=0;l<2;l++){
            if(tiles[i+k][j+l].hasPiece() && tiles[i+k][j+l].getPiece().getType()==PieceType.WHITE)
              count++;
          }
        }
        if(count>=3) finalCount++;
      }
    }
    return finalCount;
  }

  private int getDensity(Tile[][] tiles){
    int totalX=0,totalY=0,totalWhite=0;
    for(int i=0;i<WIDTH;i++) {
      for (int j = 0; j < HEIGHT; j++) {
        if(tiles[i][j].hasPiece() && tiles[i][j].getPiece().getType()==PieceType.WHITE){
          totalX+=i;
          totalY+=j;
          totalWhite++;
        }
      }
    }
    int cx=totalX/totalWhite;
    int cy=totalY/totalWhite;

    int totalWhiteDistance=0,totalBlackDistance=0;
    for(int i=0;i<WIDTH;i++) {
      for (int j = 0; j < HEIGHT; j++) {
        if(tiles[i][j].hasPiece()){
          int tmp=(int)Math.sqrt((cx-i)*(cx-i) + (cy-j)*(cy-j));
          if (tiles[i][j].getPiece().getType()==PieceType.BLACK) totalBlackDistance+=tmp;
          else totalWhiteDistance+=tmp;
        }
      }
    }
    return totalWhiteDistance-totalBlackDistance;
  }

  private MoveResult aiTryMove(Tile[][] tiles,int newX,int newY){
    if(tiles[newX][newY].hasPiece()) return new MoveResult(MoveType.KILL,tiles[newX][newY].getPiece());
    return new MoveResult(MoveType.NORMAL);
  }

  private MoveResult tryMove(Tile[][] demoBoard,Piece piece, int newX, int newY) {
    //System.out.println("newX newY " + newX + " " + newY);
    //if(piece.getType()==PieceType.WHITE) System.out.println("option size " + piece.highlightedPositions.size());
    for(int i=0;i<piece.highlightedPositions.size();i+=2){
      if(newX==piece.highlightedPositions.get(i) && newY==piece.highlightedPositions.get(i+1)){
        //System.out.println("here");
        if(demoBoard[newX][newY].hasPiece()) return new MoveResult(MoveType.KILL,demoBoard[newX][newY].getPiece());
        else return new MoveResult(MoveType.NORMAL);
      }
    }

    return new MoveResult(MoveType.NONE);
  }

  private void movePieceToPosition(Tile[][] demoBoard,MoveResult result,Piece piece,int newX,int newY,boolean real){
    //System.out.println(".//////////////////////////////");
    int x0 = toBoard(piece.getOldX());
    int y0 = toBoard(piece.getOldY());

    switch (result.getType()) {
      case NONE:
        piece.abortMove();
        //System.out.println("ohhhhhhhhhhh!  no move!!!!!!!!!!!!!!!");
        break;
      case NORMAL:
        //System.out.println("normal moooove");
        piece.move(newX, newY);
        piece.setOccupiedTile(demoBoard[newX][newY]);
        demoBoard[x0][y0].setPiece(null);
        demoBoard[newX][newY].setPiece(piece);

        if(real) TURN=!TURN;
        if(piece.isAllConnected() && real) {
          startOverAgain(piece.getType());
        }else if(chceckOpponentWin(piece.getType()) && real){
          if(piece.getType()==PieceType.BLACK)
            startOverAgain(PieceType.WHITE);
          else startOverAgain(PieceType.BLACK);
        }
        break;
      case KILL:
        Piece otherPiece = result.getPiece();
        if(otherPiece.getType()==PieceType.BLACK && real) BLACKCOUNT--;
        else if (otherPiece.getType()==PieceType.WHITE && real){
          WHITECOUNT--;
          whiteAiPieces.remove(otherPiece);
        }
        demoBoard[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
        pieceGroup.getChildren().remove(otherPiece);

        piece.move(newX, newY);
        piece.setOccupiedTile(demoBoard[newX][newY]);
        demoBoard[x0][y0].setPiece(null);
        demoBoard[newX][newY].setPiece(piece);
        if(real) TURN=!TURN;

        if(piece.isAllConnected() && real) {
          startOverAgain(piece.getType());
        }else if(chceckOpponentWin(piece.getType()) && real){
          if(piece.getType()==PieceType.BLACK)
            startOverAgain(PieceType.WHITE);
          else startOverAgain(PieceType.BLACK);
        }

        break;
    }
    if(real) {
      piece.highlightedPositions.clear();
    }

    //if(playAI && TURN) aiRandomMove();
    if(playAI && TURN && real) aiHeuristicMove();
  }

  private int toBoard(double pixel) {
    return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
  }

  private Piece makePiece(PieceType type, Tile tile, int x, int y) {
    Piece piece = new Piece(type, board, tile, x, y);

    piece.setOnMouseReleased(e -> {

      for(int i=0;i<piece.highlightedPositions.size()-1;i+=2){
        int xc=piece.highlightedPositions.get(i);
        int yc=piece.highlightedPositions.get(i+1);
        board[xc][yc].setFill(((xc + yc) % 2 == 0) ? Color.valueOf("#E59866") : Color.valueOf("#EDBB99"));
      }

      int newX = toBoard(piece.getLayoutX());
      int newY = toBoard(piece.getLayoutY());
      //System.out.println("newX " + newX + "  newY " + newY);

      MoveResult result;

      if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
        result = new MoveResult(MoveType.NONE);
      } else {
        result = tryMove(board,piece, newX, newY);
      }

      movePieceToPosition(board,result,piece,newX,newY,true);

    });

    return piece;
  }

  public void startOverAgain(PieceType pieceType){

    TURN=false;

    ButtonType play_again = new ButtonType("Play Again", ButtonBar.ButtonData.OK_DONE);
    Alert alert=new Alert(Alert.AlertType.INFORMATION,"none",play_again);
    alert.setTitle("Lines of Action");
    alert.setHeaderText("Game Finished");
    if(pieceType==PieceType.WHITE)
      alert.setContentText("WHITE has won");
    else alert.setContentText("BLACK has won");
    alert.initModality(Modality.APPLICATION_MODAL);
    Optional<ButtonType> bd = alert.showAndWait();

    if (bd.orElse(play_again) == play_again) {
      selectOption();
      stages.get(0).close();
      stages.clear();
      pieceGroup.getChildren().clear();
      tileGroup.getChildren().clear();
      whiteAiPieces.clear();
      Stage primaryStage=new Stage();
      Scene scene = new Scene(createContent());
      primaryStage.setTitle("Lines of Action");
      primaryStage.setScene(scene);
      primaryStage.show();
      stages.add(primaryStage);
    }
  }

  public static void main(String[] args) {
    Scanner scanner=new Scanner(System.in);
    System.out.println("Enter Board Size ( 6 / 8 ) :");
    int size=scanner.nextInt();
    //int size=8;
    WIDTH=HEIGHT=size;
    launch(args);
  }
}
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;


public class CSP {

    public static int N;
    public static String inputFileName="input.txt";
    public static int[][] latinSquare;
    public static LinkedList<Integer>[][] domainList;
    public static int [][] staticDegreeCount;
    public static long nodeCount=0;
    public static long backTrackCount=0;

    //public static String backTrackType="BT";
    public static String backTrackType="FC";

  //  public static String variableOrderingHeuristic="SDF";
 //  public static String variableOrderingHeuristic="DOMdeg";
  //  public static String variableOrderingHeuristic="DOMDdeg";
   // public static String variableOrderingHeuristic="BRELAZ";
    public static String variableOrderingHeuristic="BRELAZMin";

    public static void main(String[] args) throws FileNotFoundException {

        File inputFile=new File(inputFileName);
        Scanner scanner=new Scanner(inputFile);

        String line=scanner.nextLine();
        N = Integer.parseInt(line.substring(2, line.length() - 1));

        System.out.println(N);


        latinSquare=new int[N][N];
        domainList=new LinkedList[N][N];
        staticDegreeCount=new int[N][N];


        //int cnt=N;
        line=scanner.nextLine();
        if(!line.equalsIgnoreCase("start=")){
            System.out.println("File error");
            return;
        }
        scanner.nextLine();
        int readline=N;
        while (readline>0){
            line = scanner.nextLine().replaceAll("\\s", "");
            if (readline==1) {
                line=line.substring(0, line.length() - 3);
            } else {
                line=line.substring(0, line.length() - 1);
            }
            String [] tokens = line.split(",");
            for (int i = 0; i < tokens.length; i++){
                latinSquare[N-readline][i] = Integer.parseInt(tokens[i]);
            }
            readline--;
        }

        printLatinSquare();
        //test();
        calculateDomain();
        /*for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                System.out.print("domain[" + i + "," + j + "] : " );
                for(int k=0;k<domainList[i][j].size();k++){
                    System.out.print(domainList[i][j].get(k) + "  ");
                }
                System.out.println();
            }
        }*/
        calculateDegree();
        if(solveCSP()) {
            System.out.println("Solved");
            for(int i=0;i<N;i++){
                for(int j=0;j<N;j++){
                    System.out.print(latinSquare[i][j] + "  ");
                }
                System.out.println();
            }
        }
        else System.out.println("Not Solvable");
        System.out.println();
        System.out.println("NodeCount = " + nodeCount);
        System.out.println("BackTrackCount = " + backTrackCount);

    }

    private static void test() {
        LinkedList<Integer> test=new LinkedList<>();
        for(int i=0;i<100;i++) test.add(i);
        test.remove(5);
        for(int i=0;i<test.size();i++) System.out.print(test.get(i));

    }


    private static void printLatinSquare() {
        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                System.out.print(latinSquare[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static void calculateDomain() {
        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                domainList[i][j]=calcDomainAtPos(i,j);
            }
        }
    }

    private static LinkedList<Integer> calcDomainAtPos(int i, int j) {
        LinkedList<Integer> container=new LinkedList<>();
        LinkedList<Integer> returnList=new LinkedList<>();
        if(latinSquare[i][j]!=0){
            returnList.add(latinSquare[i][j]);
        }
        else{
            for(int r=0;r<N;r++){
                if(latinSquare[r][j]!=0) container.add(latinSquare[r][j]);
            }
            for(int c=0;c<N;c++){
                if(latinSquare[i][c]!=0) container.add(latinSquare[i][c]);
            }

            for(int x=1;x<N+1;x++){
                boolean exist=false;
                for(int y=0;y<container.size();y++){
                    if(container.get(y)==x){
                        exist=true;
                        break;
                    }
                }
                if(!exist) returnList.add(x);
            }
        }
        return returnList;
    }

    private static void calculateDegree(){
        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                staticDegreeCount[i][j]=degreeCountAtPos(i,j);
            }
        }
    }

    private static int degreeCountAtPos(int i, int j) {
        int degree=0;
        if(latinSquare[i][j]==0) degree++;
        for(int r=0;r<N && r!=i;r++){
            if(latinSquare[r][j]==0) degree++;
        }
        for(int c=0;c<N && c!=j;c++){
            if(latinSquare[i][c]==0) degree++;
        }
        return degree;
    }


    private static boolean solveCSP() {

        nodeCount++;
        int row,col;

        int[] pos=heurisTicOptimalPos();
        row= pos[0];
        col=pos[1];

        //System.out.println("row : " + row  + " ;   col : " + col);
        if(row==-1 || col==-1) return true;

        for(int i=0;i<domainList[row][col].size();i++){

            int value=domainList[row][col].get(i);
            //System.out.println("value = " + value);

            LinkedList<Integer>[][] backupDomain=new LinkedList[N][N];
            for(int j=0;j<N;j++){
                for(int k=0;k<N;k++){
                    backupDomain[j][k]=new LinkedList<>();
                    for(int l=0;l<domainList[j][k].size();l++){
                        backupDomain[j][k].add(domainList[j][k].get(l));
                    }
                }
            }

            boolean safe=isSafeAssign(row,col,value);
            if(safe){
                latinSquare[row][col]=value;
                domainList[row][col].clear();
                domainList[row][col].add(value);
                if(solveCSP()) return true;
                latinSquare[row][col]=0;
            }
            for(int j=0;j<N;j++){
                for(int k=0;k<N;k++){
                    domainList[j][k]=backupDomain[j][k];
                }
            }
        }
        backTrackCount++;
        return false;
    }


    private static int[] heurisTicOptimalPos() {

        if(variableOrderingHeuristic.equalsIgnoreCase("SDF")){
            LinkedList<Integer> positions=calcSDF();
            int[] pos=new int[2];
            pos[0]=positions.get(positions.size()-2);
            pos[1]=positions.get(positions.size()-1);
            return pos;
        }
        else if(variableOrderingHeuristic.equalsIgnoreCase("domdeg"))
            return calcDomDeg();
        else if(variableOrderingHeuristic.equalsIgnoreCase("domddeg"))
            return calcDomDDeg();
        else if(variableOrderingHeuristic.equalsIgnoreCase("brelaz"))
            return calcBrelaz(false);
        else if(variableOrderingHeuristic.equalsIgnoreCase("brelazmin"))
            return calcBrelaz(true);
        return new int[2];

    }

    private static int[] calcBrelaz(boolean isMin) {
        int[] pos = new int[2];
        int deg, maxDeg = Integer.MIN_VALUE, minDeg = Integer.MAX_VALUE;

        LinkedList<Integer> positions = calcSDF();

        pos[0] = -1;
        pos[1] = -1;

        if (!(positions.size() == 2 && positions.get(0) == -1)) {
            for (int i = 0; i < positions.size(); i += 2) {
                deg = degreeCountAtPos(positions.get(i), positions.get(i + 1));
                if (!isMin) {
                    if (deg > maxDeg) {
                        maxDeg = deg;
                        pos[0] = positions.get(i);
                        pos[1] = positions.get(i + 1);
                    }
                } else {
                    if (deg < minDeg) {
                        minDeg = deg;
                        pos[0] = positions.get(i);
                        pos[1] = positions.get(i + 1);
                    }

                }
            }
        }
        return pos;
    }

    private static int[] calcDomDDeg() {
        int [] pos=new int[2];
        int domainSize;
        float ratio,minRatio=Float.MAX_VALUE;

        pos[0]=-1;
        pos[1]=-1;

        for(int i=0;i<N;i++) {
            for (int j = 0; j < N; j++) {
                if(latinSquare[i][j]==0){
                    domainSize=calcDomainAtPos(i,j).size();
                    int deg=degreeCountAtPos(i,j);
                    ratio= (float) domainSize/deg;
                    if(ratio<minRatio){
                        minRatio=ratio;
                        pos[0]=i;
                        pos[1]=j;
                    }
                }
            }
        }
        return pos;
    }

    private static int[] calcDomDeg() {
        int [] pos=new int[2];
        int domainSize;
        float ratio,minRatio=Float.MAX_VALUE;

        pos[0]=-1;
        pos[1]=-1;

        for(int i=0;i<N;i++) {
            for (int j = 0; j < N; j++) {
                if(latinSquare[i][j]==0){
                    domainSize=calcDomainAtPos(i,j).size();
                    ratio= (float) domainSize/staticDegreeCount[i][j];
                    if(ratio<minRatio){
                        minRatio=ratio;
                        pos[0]=i;
                        pos[1]=j;
                    }
                }
            }
        }
        return pos;
    }

    private static LinkedList<Integer> calcSDF() {
        LinkedList<Integer> positions=new LinkedList<>();
        int domainSize,minSize=Integer.MAX_VALUE;

        positions.add(-1);
        positions.add(-1);

        for(int i=0;i<N;i++) {
            for (int j = 0; j < N; j++) {
                if (latinSquare[i][j] == 0) {
                    domainSize=calcDomainAtPos(i,j).size();
                    if(domainSize<minSize){
                        minSize=domainSize;
                        positions.clear();
                        positions.add(i);
                        positions.add(j);
                    }else if(domainSize==minSize){
                        positions.add(i);
                        positions.add(j);
                    }
                }
            }
        }
        //System.out.println("Linkedlist Size = " + positions.size());
        return positions;
    }


    private static boolean isSafeAssign(int row, int col, int value) {
        if(backTrackType.equalsIgnoreCase("FC")){
            for(int r=0;r<N;r++){
                if(latinSquare[r][col]==0 && r!=row){
                    domainList[r][col].remove(Integer.valueOf(value));
                }
                if(domainList[r][col].isEmpty()) return false;
                if(latinSquare[row][r]==0 && r!=col){
                    domainList[row][r].remove(Integer.valueOf(value));
                }
                if(domainList[row][r].isEmpty()) return false;
            }
        }
        return regularCheck(row,col,value);
    }

    private static boolean regularCheck(int row, int col, int value) {
        if(latinSquare[row][col]!=0) return false;
        for(int i=0;i<N;i++){
            if(latinSquare[i][col]==value) return false;
        }
        for(int j=0;j<N;j++){
            if(latinSquare[row][j]==value) return false;
        }
        return true;
    }


}

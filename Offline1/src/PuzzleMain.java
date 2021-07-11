import java.io.File;
import java.io.FileNotFoundException;
import java.util.PriorityQueue;
import java.util.Scanner;

public class PuzzleMain {

    public static int n=4;
    public static int[][] startState;
    public static int[][] finalState;


    public static int displacementCost(int[][] arr)
    {
        int cost=0;
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(arr[i][j]!=finalState[i][j]) cost++;
            }
        }
        return cost;
    }

    public static int manhattanCost(int[][] arr)
    {
        int cost=0;
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(arr[i][j]!=0){
                    for(int k=0;k<n;k++){
                        for(int l=0;l<n;l++){
                            if(arr[i][j]==finalState[k][l]){
                                cost+=Math.abs(i-k) + Math.abs(j-l);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return cost;
    }


    public static int[] blankPositionXY(int arr[][])
    {
        int[] tem=new int [2];
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(arr[i][j]==0)
                {
                    tem[0]=i;
                    tem[1]=j;
                    break;
                }
            }
        }
        return tem;
    }

    public static int inversionCount()
    {
        int count = 0;
        int[] tem=new int [n*n-1];

        int k=0;
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(startState[i][j]!=0) tem[k++]=startState[i][j];
            }
        }

        for(int i=0;i<n*n-2;i++){
            for(int j=i+1;j<n*n-1;j++){
                if(tem[i]>tem[j]) count++;
            }
        }

        return count;
    }

    public static boolean solvable()
    {
        if(n%2==1){
            if(inversionCount()%2==0) return true;
            else return false;
        }
        else{
            int blankPos=n-blankPositionXY(startState)[0];
            if(blankPos%2==0 && inversionCount()%2==1) return true;
            else if(blankPos%2==1 && inversionCount()%2==0) return true;
            else return false;
        }
    }

    public static boolean isMoveValid(int i)
    {
        if(i>=0 && i<n) return true;
        return false;
    }

    public static boolean isMatched(int arr[][])
    {
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(arr[i][j]!=finalState[i][j]) return false;
            }
        }
        return true;
    }


    public static void printPath(NodeState node)
    {
        if(node.parent!=null) printPath(node.parent);

        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                System.out.print(node.state[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void manhattanHeuristic()
    {
        int nodeCount=0;
        NodeState root=new NodeState(n,startState,0,"no move",null);
        root.cost=displacementCost(root.state);

        PriorityQueue<NodeState> pq=new PriorityQueue<>();
        pq.add(root);

        nodeCount++;

        while (!pq.isEmpty())
        {
            NodeState node=pq.remove();


            if(isMatched(node.state))
            {
                System.out.println("----------Manhattan Heuristics----------");
                System.out.println("Path cost = " + node.level);
                System.out.println("Expanded nodes = " + nodeCount);
                System.out.println("The full path is in below -");
                printPath(node);
                pq.clear();
                break;

            }
            else
            {
                int[] blankPos=blankPositionXY(node.state);

                if(isMoveValid(blankPos[0]+1) && !node.lastMove.equalsIgnoreCase("up"))
                {
                    //System.out.println("down");
                    int[][] temArr=new int [n][n];
                    for(int i=0;i<n;i++){
                        for(int j=0;j<n;j++){
                            temArr[i][j]=node.state[i][j];
                        }
                    }
                    NodeState newNode=new NodeState(n,temArr,node.level+1,"down",node);
                    newNode.state[blankPos[0]][blankPos[1]]=temArr[blankPos[0]+1][blankPos[1]];
                    newNode.state[blankPos[0]+1][blankPos[1]]=0;
                    newNode.cost=manhattanCost(newNode.state);
                    pq.add(newNode);
                    nodeCount++;


                    /*System.out.println("Child : ");
                    for(int i=0;i<n;i++){
                        for(int j=0;j<n;j++){
                            System.out.print(newNode.state[i][j] + "   ");
                        }
                        System.out.println();
                    }
                    System.out.println();*/
                }

                if(isMoveValid(blankPos[0]-1) && !node.lastMove.equalsIgnoreCase("down"))
                {
                    //System.out.println("up");
                    int[][] temArr=new int [n][n];
                    for(int i=0;i<n;i++){
                        for(int j=0;j<n;j++){
                            temArr[i][j]=node.state[i][j];
                        }
                    }
                    NodeState newNode=new NodeState(n,temArr,node.level+1,"up",node);
                    newNode.state[blankPos[0]][blankPos[1]]=temArr[blankPos[0]-1][blankPos[1]];
                    newNode.state[blankPos[0]-1][blankPos[1]]=0;
                    newNode.cost=manhattanCost(newNode.state);
                    pq.add(newNode);
                    nodeCount++;
                }

                if(isMoveValid(blankPos[1]+1) && !node.lastMove.equalsIgnoreCase("left"))
                {
                    //System.out.println("right");

                    int[][] temArr=new int [n][n];
                    for(int i=0;i<n;i++){
                        for(int j=0;j<n;j++){
                            temArr[i][j]=node.state[i][j];
                        }
                    }
                    NodeState newNode=new NodeState(n,temArr,node.level+1,"right",node);
                    newNode.state[blankPos[0]][blankPos[1]]=temArr[blankPos[0]][blankPos[1]+1];
                    newNode.state[blankPos[0]][blankPos[1]+1]=0;
                    newNode.cost=manhattanCost(newNode.state);
                    pq.add(newNode);
                    nodeCount++;
                }

                if(isMoveValid(blankPos[1]-1) && !node.lastMove.equalsIgnoreCase("right"))
                {
                    //System.out.println("left");
                    int[][] temArr=new int [n][n];
                    for(int i=0;i<n;i++){
                        for(int j=0;j<n;j++){
                            temArr[i][j]=node.state[i][j];
                        }
                    }
                    NodeState newNode=new NodeState(n,temArr,node.level+1,"left",node);
                    newNode.state[blankPos[0]][blankPos[1]]=temArr[blankPos[0]][blankPos[1]-1];
                    newNode.state[blankPos[0]][blankPos[1]-1]=0;
                    newNode.cost=manhattanCost(newNode.state);
                    pq.add(newNode);
                    nodeCount++;
                }
            }
        }
    }

    public static void displacementHeuristic()
    {
        int nodeCount=0;
        NodeState root=new NodeState(n,startState,0,"no move",null);
        root.cost=displacementCost(root.state);

        PriorityQueue<NodeState> pq=new PriorityQueue<>();
        pq.add(root);

        nodeCount++;

        while (!pq.isEmpty())
        {
            NodeState node=pq.remove();


            if(isMatched(node.state))
            {
                System.out.println("----------Displacement Heuristics----------");
                System.out.println("Path cost = " + node.level);
                System.out.println("Expanded nodes = " + nodeCount);
                System.out.println("The full path is in below -");
                printPath(node);
                pq.clear();
                break;

            }
            else
            {
                int[] blankPos=blankPositionXY(node.state);

                if(isMoveValid(blankPos[0]+1) && !node.lastMove.equalsIgnoreCase("up"))
                {
                    //System.out.println("down");
                    int[][] temArr=new int [n][n];
                    for(int i=0;i<n;i++){
                        for(int j=0;j<n;j++){
                            temArr[i][j]=node.state[i][j];
                        }
                    }
                    NodeState newNode=new NodeState(n,temArr,node.level+1,"down",node);
                    newNode.state[blankPos[0]][blankPos[1]]=temArr[blankPos[0]+1][blankPos[1]];
                    newNode.state[blankPos[0]+1][blankPos[1]]=0;
                    newNode.cost=displacementCost(newNode.state);
                    pq.add(newNode);
                    nodeCount++;


                    /*System.out.println("Child : ");
                    for(int i=0;i<n;i++){
                        for(int j=0;j<n;j++){
                            System.out.print(newNode.state[i][j] + "   ");
                        }
                        System.out.println();
                    }
                    System.out.println();*/
                }

                if(isMoveValid(blankPos[0]-1) && !node.lastMove.equalsIgnoreCase("down"))
                {
                    //System.out.println("up");
                    int[][] temArr=new int [n][n];
                    for(int i=0;i<n;i++){
                        for(int j=0;j<n;j++){
                            temArr[i][j]=node.state[i][j];
                        }
                    }
                    NodeState newNode=new NodeState(n,temArr,node.level+1,"up",node);
                    newNode.state[blankPos[0]][blankPos[1]]=temArr[blankPos[0]-1][blankPos[1]];
                    newNode.state[blankPos[0]-1][blankPos[1]]=0;
                    newNode.cost=displacementCost(newNode.state);
                    pq.add(newNode);
                    nodeCount++;
                }

                if(isMoveValid(blankPos[1]+1) && !node.lastMove.equalsIgnoreCase("left"))
                {
                    //System.out.println("right");

                    int[][] temArr=new int [n][n];
                    for(int i=0;i<n;i++){
                        for(int j=0;j<n;j++){
                            temArr[i][j]=node.state[i][j];
                        }
                    }
                    NodeState newNode=new NodeState(n,temArr,node.level+1,"right",node);
                    newNode.state[blankPos[0]][blankPos[1]]=temArr[blankPos[0]][blankPos[1]+1];
                    newNode.state[blankPos[0]][blankPos[1]+1]=0;
                    newNode.cost=displacementCost(newNode.state);
                    pq.add(newNode);
                    nodeCount++;
                }

                if(isMoveValid(blankPos[1]-1) && !node.lastMove.equalsIgnoreCase("right"))
                {
                    //System.out.println("left");
                    int[][] temArr=new int [n][n];
                    for(int i=0;i<n;i++){
                        for(int j=0;j<n;j++){
                            temArr[i][j]=node.state[i][j];
                        }
                    }
                    NodeState newNode=new NodeState(n,temArr,node.level+1,"left",node);
                    newNode.state[blankPos[0]][blankPos[1]]=temArr[blankPos[0]][blankPos[1]-1];
                    newNode.state[blankPos[0]][blankPos[1]-1]=0;
                    newNode.cost=displacementCost(newNode.state);
                    pq.add(newNode);
                    nodeCount++;
                }
            }
        }


    }

    public static void main(String[] args) throws FileNotFoundException {

        int prbNumber;
        File file=new File("input.txt");
        Scanner scn=new Scanner(file);

        startState=new int [n][n];
        finalState=new int [n][n];

        prbNumber= scn.nextInt();

        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                finalState[i][j]=scn.nextInt();
            }
        }

        for(int k=0;k<prbNumber-1;k++)
        {
            for(int i=0;i<n;i++){
                for(int j=0;j<n;j++){
                    startState[i][j]=scn.nextInt();
                }
            }

            System.out.println("--------------Solution to puzzle no : " + (k+1) + "  --------------------");

            if(!solvable()) System.out.println("The puzzle is not solvable");
            else {
                displacementHeuristic();
                manhattanHeuristic();
            }
        }



    }
}

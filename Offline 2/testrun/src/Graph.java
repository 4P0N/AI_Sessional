import java.util.ArrayList;

public class Graph {

    int vertices;
    int dup;
    ArrayList<Course> adj[];

    public Graph(int vertices) {
        this.vertices = vertices;
        adj=new ArrayList[vertices];
        for(int i=0;i<vertices;i++)
        {
            adj[i]=new ArrayList<>();
        }
        dup=0;
    }

    boolean isEdge(Course u,Course v){
        return adj[u.getID()].contains(v);
    }

    void addEdge(Course u,Course v)
    {
        if(!isEdge(u,v)) {
            adj[u.getID()].add(v);
            adj[v.getID()].add(u);
        } else dup++;
    }

    int getDegree(Course c){
        return adj[c.getID()].size();
    }


    void printGraph(){
        for(int i=0;i<vertices;i++){
            System.out.println("\nAdjacency list of vertex " + i);
            System.out.print("head (" + adj[i].size() + ")");
            for(int j=0;j<adj[i].size();j++){
                System.out.print(" ->" + adj[i].get(j).getID() + "(" + adj[i].get(j).color + ")");
            }
            System.out.println();
        }
    }
}

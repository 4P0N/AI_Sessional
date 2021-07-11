public class NodeState implements Comparable<NodeState>{

    int size;
    int[][] state;
    int level;
    int cost;
    String lastMove;
    NodeState parent;

    public NodeState(int size, int[][] state,int level,String lastMove,NodeState parent) {
        this.size = size;
        this.state = state;
        this.level=level;
        this.lastMove=lastMove;
        this.parent=parent;
    }

    @Override
    public int compareTo(NodeState o) {
        if(this.cost + this.level > o.cost + o.level) {
            return 1;
        } else if (this.cost + this.level < o.cost + o.level) {
            return -1;
        } else {
            return 0;
        }
    }
}

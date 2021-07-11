import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TimeTabling {

    public static String crsFileName="c.crs";
    public static String stuFileName="s.stu";

    public static FileWriter myWriter1;
    public static FileWriter myWriter2;

    public static int totalCourse;
    public static int totalStudent;
    public static ArrayList<Integer> studentPerCourse;
    public static ArrayList<Integer> coursesPerStudent[];
    public static Course[] courses;
    public static int numOfColors;
    public static int penalty;
    public static int tolerate=500;

    /////////hudai

    public static ArrayList<Course> hudai=new ArrayList<>();

    /////////////

    public static void checkColor(Graph g){
        for(int i=0;i<hudai.size();i+=2){
            if(hudai.get(i).color==hudai.get(i+1).color){
                System.out.println("XXXXXXXXXXXXXXX");
                break;
            }
        }
    }

    public static int checkPenalty(Graph g){
        int penal=0;
        for(int i=0;i<totalStudent;i++){
            for(int j=0;j<coursesPerStudent[i].size()-1;j++){
                for(int k=j+1;k<coursesPerStudent[i].size();k++){
                    Course c1=courses[coursesPerStudent[i].get(j)];
                    Course c2=courses[coursesPerStudent[i].get(k)];

                    switch (Math.abs(c1.color-c2.color)){
                        case 1:
                            penal+=16;
                            break;
                        case 2:
                            penal+=8;
                            break;
                        case 3:
                            penal+=4;
                            break;
                        case 4:
                            penal+=2;
                            break;
                        case 5:
                            penal+=1;
                            break;
                    }
                }
            }
        }
        return penal/totalStudent;
    }

    public static boolean kempeChain(Graph g){

        Random rand=new Random();
        Course u=courses[rand.nextInt(totalCourse)];
        int x= rand.nextInt(g.adj[u.getID()].size());
        Course v=g.adj[u.getID()].get(x);
        Course tmp;
        int c1=u.color;
        int c2=v.color;

        ArrayList<Course> kempeSubGraph=new ArrayList<>();
        kempeSubGraph.add(u);
        kempeSubGraph.add(v);
        LinkedList<Course> queue=new LinkedList<>();
        boolean []visited=new boolean[totalCourse];
        for (int i=0;i<totalCourse;i++) visited[i]=false;

        visited[u.getID()]=true;
        visited[v.getID()]=true;
        queue.add(u);
        queue.add(v);

        while (!queue.isEmpty()){
            tmp=queue.poll();
            Iterator<Course> m=g.adj[tmp.getID()].listIterator();
            while (m.hasNext()){
                Course n=m.next();
                if(!visited[n.getID()] && (n.color==c1 || n.color==c2)){
                    visited[n.getID()]=true;
                    queue.add(n);
                    kempeSubGraph.add(n);
                }
            }
        }
        //System.out.println("kepme sixe " + kempeSubGraph.size());
        for(int i=0;i<kempeSubGraph.size();i++){
            if(kempeSubGraph.get(i).color==c1) kempeSubGraph.get(i).color=c2;
            else if(kempeSubGraph.get(i).color==c2) kempeSubGraph.get(i).color=c1;
            //else System.out.println("xxxxxxxxxxxx");
        }

        int a=checkPenalty(g);
        if(a<penalty) {
            penalty=a;
            //System.out.println("kempePenalty -> " + penalty);
            return true;
        }
        else{
            for(int i=0;i<kempeSubGraph.size();i++){
                if(kempeSubGraph.get(i).color==c1) kempeSubGraph.get(i).color=c2;
                else if(kempeSubGraph.get(i).color==c2) kempeSubGraph.get(i).color=c1;
            }
            return false;
        }

    }



    public static void graphColouringDeg(Graph g) throws IOException {
        Comparator<Course> highDeg=new Comparator<Course>() {
            @Override
            public int compare(Course o1, Course o2) {
                if(o1.deg>o2.deg) return -1;
                else if(o1.deg<o2.deg) return 1;
                else return 0;
            }
        };
        for(int i=0;i<totalCourse;i++)  courses[i].color=-1;

        PriorityQueue<Course> pq=new PriorityQueue<>(highDeg);
        for(int i=0;i<totalCourse;i++)  pq.add(courses[i]);

        boolean []clr=new boolean[totalCourse];

        numOfColors=0;

        while (!pq.isEmpty()){
            Course c=pq.poll();

            for(int i=0;i<totalCourse;i++)  clr[i]=false;

            for (int i=0;i<g.adj[c.getID()].size();i++){
                int x=g.adj[c.getID()].get(i).color;
                if(x!=-1){
                    clr[x]=true;
                }
            }

            for(int i=0;i<totalCourse;i++){
                if(!clr[i]){
                    c.color=i;
                    if(i>numOfColors) numOfColors=i;
                    break;
                }
            }

        }

        System.out.println(">>>>>>>>>>> Scheme 2 <<<<<<<<<<");
        System.out.println();
        System.out.println("Timeslots --> " + (numOfColors+1));

        penalty=checkPenalty(g);
        //System.out.println("penalty highDeg --> " + penalty);

        int cnt=0;
        while (cnt<tolerate){
            boolean flag=kempeChain(g);
            if(flag) cnt=0;
            else cnt++;
        }
        System.out.println("Penalty --> " + penalty);
        System.out.println();

        for (int i=0;i<courses.length;i++){
            myWriter2.write((i+1) + " " +  (courses[i].color+1) + "\n");
        }
        //myWriter.write("xxxxxxxxxxxxxx");
        myWriter2.close();
    }


    public static void graphColouringSat(Graph g) throws IOException {
        Comparator<Course> satdeg=new Comparator<Course>() {
            @Override
            public int compare(Course o1, Course o2) {
                if(o1.satDeg>o2.satDeg) return -1;
                else if(o1.satDeg<o2.satDeg) return 1;
                else{
                    if(o1.deg>o2.deg) return -1;
                    else if(o1.deg<o2.deg) return 1;
                    else return 0;
                }
            }
        };
        PriorityQueue<Course> pq=new PriorityQueue<>(satdeg);
        for(int i=0;i<totalCourse;i++){
            pq.add(courses[i]);
        }

        numOfColors=0;

        boolean []clr=new boolean[totalCourse];

        while (!pq.isEmpty()){
            Course c=pq.poll();

            for(int i=0;i<totalCourse;i++)  clr[i]=false;

            for (int i=0;i<g.adj[c.getID()].size();i++){
                int x=g.adj[c.getID()].get(i).color;
                if(x!=-1){
                    clr[x]=true;
                }
                g.adj[c.getID()].get(i).satDeg++;
            }

            for(int i=0;i<totalCourse;i++){
                if(!clr[i]){
                    c.color=i;
                    if(i>numOfColors) numOfColors=i;
                    break;
                }
            }
        }

        System.out.println(">>>>>>>>>>> Scheme 1 <<<<<<<<<");
        System.out.println();

        System.out.println("Timeslots --> " + (numOfColors+1));
        penalty=checkPenalty(g);
        //System.out.println("penalty dSat --> " + (checkPenalty(g)));

        int cnt=0;
        while (cnt<tolerate){
            boolean flag=kempeChain(g);
            if(flag) cnt=0;
            else cnt++;
        }
        System.out.println("Penalty --> " + penalty);
        System.out.println();

        for (int i=0;i<courses.length;i++){
            myWriter1.write((i+1) + " " +  (courses[i].color+1) + "\n");
        }
        //myWriter.write("xxxxxxxxxxxxxx");
        myWriter1.close();
    }


    public static void main(String[] args) throws IOException {

        File crsFile=new File(crsFileName);
        File stuFile=new File(stuFileName);

        myWriter1 = new FileWriter("scheme1.txt");
        myWriter2 = new FileWriter("scheme2.txt");

        Scanner scn=new Scanner(crsFile);
        studentPerCourse=new ArrayList<>();
        while(scn.hasNextInt()){
            scn.nextInt();
            studentPerCourse.add(scn.nextInt());
        }
        totalCourse=studentPerCourse.size();
        //System.out.println("total course " + totalCourse);


        courses=new Course[totalCourse];
        for(int i=0;i<totalCourse;i++){
            courses[i]=new Course(i);
        }


        scn=new Scanner(stuFile);
        totalStudent=0;
        while(scn.hasNextLine()){
            totalStudent++;
            scn.nextLine();
        }
        //System.out.println("total student " + totalStudent);

        coursesPerStudent=new ArrayList[totalStudent];
        for(int i=0;i<totalStudent;i++){
            coursesPerStudent[i]=new ArrayList<>();
        }

        int index=0;
        scn=new Scanner(stuFile);
        while(scn.hasNextLine()){
            //System.out.println(scn.nextLine());
            String[] line = scn.nextLine().trim().split(" ");
            for(int i=0;i<line.length;i++){
                //System.out.println(line[i]);
                coursesPerStudent[index].add(Integer.parseInt(line[i])-1);
            }
            index++;
        }

        Graph graph=new Graph(totalCourse);

        for(int i=0;i<totalStudent;i++){
            for(int j=0;j<coursesPerStudent[i].size()-1;j++){
                for(int k=j+1;k<coursesPerStudent[i].size();k++){
                    graph.addEdge(courses[coursesPerStudent[i].get(j)],courses[coursesPerStudent[i].get(k)]);
                    hudai.add(courses[coursesPerStudent[i].get(j)]);
                    hudai.add(courses[coursesPerStudent[i].get(k)]);
                }
            }
        }

        for(int i=0;i<totalCourse;i++){
            courses[i].deg=graph.getDegree(courses[i]);
        }

        graphColouringSat(graph);

        graphColouringDeg(graph);

    }
}

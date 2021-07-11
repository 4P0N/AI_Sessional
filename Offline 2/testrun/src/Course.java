public class Course {

    int id;
    int color;
    int satDeg;
    int deg;

    public Course(int id) {
        this.id = id;
        this.color = -1;
        this.satDeg = 0;
    }

    int getID() {
        return id;
    }
}

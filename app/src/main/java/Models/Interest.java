package Models;

import java.util.ArrayList;

public class Interest {
    private String title;
    private int id;
    public static ArrayList<Interest> LIST = new ArrayList<>();

    static {
        LIST.add(new Interest(1, "Science"));
        LIST.add(new Interest(2, "IT"));
        LIST.add(new Interest(3, "Music"));
        LIST.add(new Interest(4, "Languages"));
        LIST.add(new Interest(5, "Sports"));
        LIST.add(new Interest(6, "Travelling"));
        LIST.add(new Interest(7, "Art"));
        LIST.add(new Interest(8, "Business"));
        LIST.add(new Interest(9, "Politics"));
        LIST.add(new Interest(10, "Gaming"));
    }

    public Interest(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Interest() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

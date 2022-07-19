package zyrosite.pocketexam;
import com.google.firebase.Timestamp;

import java.util.Date;

public class StudyMaterialModel {
    private int type;
    private String name;
    private String parent;
    private String link;
    private String path;
    private String categoryID;
    private Timestamp timeStamp;

    public StudyMaterialModel() {
    }

    public StudyMaterialModel(int type,
                              String name, String parent,
                              String link, String path,
                              String categoryID, Timestamp timeStamp
                              ) {
        this.type = type;
        this.name = name;
        this.parent = parent;
        this.link = link;
        this.path = path;
        this.categoryID = categoryID;
        this.timeStamp = timeStamp;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

    public String getLink() {
        return link;
    }

    public String getPath() {
        return path;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public String getCategoryID() {
        return categoryID;
    }
}

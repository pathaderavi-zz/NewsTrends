package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ravikiranpathade on 12/12/17.
 */

public class Source {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;

    public Source(String id, String name){
        this.setId(id);
        this.setName(name);
    }

}

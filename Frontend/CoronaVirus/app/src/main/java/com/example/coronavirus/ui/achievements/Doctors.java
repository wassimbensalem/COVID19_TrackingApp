
package com.example.coronavirus.ui.achievements;


import java.util.HashMap;
import java.util.Map;

public class Doctors {


    private String type;

    private Properties properties;

    private Geometry geometry;

    private String id;
    private String adress;
    private String _id;
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getAdress() {
        return this.adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String get_Id() {
        return _id;
    }

    public void set_Id(String _id) {
        this._id = _id;
    }

}

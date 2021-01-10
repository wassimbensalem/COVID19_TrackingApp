package com.example.coronavirus.ui.achievements;



    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;



public class Geometry {


    private String type;

    private List<List<List<Double>>> coordinates = null;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public List<List<List<Double>>> getCoordinates() {
        return coordinates;
    }


    public void setCoordinates(List<List<List<Double>>> coordinates) {
        this.coordinates = coordinates;
    }


    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }


    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

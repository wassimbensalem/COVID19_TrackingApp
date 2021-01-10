package com.example.coronavirus.ui.achievements;

    import java.util.HashMap;
    import java.util.Map;


public class Properties {

    private String id;
    private String addrCity;
    private String addrCountry;
    private String addrHousenumber;
    private String addrPostcode;
    private String addrStreet;
    private String healthcareSpeciality;
    private String name;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddrCity() {
        return addrCity;
    }

    public void setAddrCity(String addrCity) {
        this.addrCity = addrCity;
    }

    public String getAddrCountry() {
        return addrCountry;
    }

    public void setAddrCountry(String addrCountry) {
        this.addrCountry = addrCountry;
    }

    public String getAddrHousenumber() {
        return addrHousenumber;
    }

    public void setAddrHousenumber(String addrHousenumber) {
        this.addrHousenumber = addrHousenumber;
    }

    public String getAddrPostcode() {
        return addrPostcode;
    }

    public void setAddrPostcode(String addrPostcode) {
        this.addrPostcode = addrPostcode;
    }

    public String getAddrStreet() {
        return addrStreet;
    }

    public void setAddrStreet(String addrStreet) {
        this.addrStreet = addrStreet;
    }

    public String getHealthcareSpeciality() {
        return healthcareSpeciality;
    }

    public void setHealthcareSpeciality(String healthcareSpeciality) {
        this.healthcareSpeciality = healthcareSpeciality;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

package com.example.coronavirus.ui.map;
import java.util.List;

public class  EssentialsDTO {
    String name;
    String category;
    List<PropertiesScheme> essentialProperties;

    public class PropertiesScheme{
        String quantity;
                     String price;
                    String ownerName;
                    List<String> ownerLocation;
                    String email;
    }

}



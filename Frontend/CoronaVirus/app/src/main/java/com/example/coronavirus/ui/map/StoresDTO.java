package com.example.coronavirus.ui.map;

import java.util.List;

public class StoresDTO {
    public String name;
    public List<String> location;
    public List<EssentialsStore> essentials;

    public  class  EssentialsStore{
        public String name;
        public String price;
        public String quantity;
    }
}

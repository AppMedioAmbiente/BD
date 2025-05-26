package com.example.firelogin;

import java.util.HashMap;
import java.util.Map;

public class SpinnerOption {
    private Object id;
    private String name;
    private Map<String, Object> extraData;

    public SpinnerOption(Object id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getId() {
        return id.toString();
    }

    public Object getName() {
        return name;
    }
    public Boolean setExtraData(String key, Object value){
        if(extraData==null){
            extraData=new HashMap<>();
        }
        try {
            extraData.put(key, value);
            return true;
        }catch (Exception ex){
            return false;
        }
    }
    public Object getExtraData(String key){
        if(extraData==null){
            return null;
        }
        return extraData.get(key);
    }
}

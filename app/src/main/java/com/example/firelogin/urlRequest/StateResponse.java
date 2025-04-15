package com.example.firelogin.urlRequest;
import com.google.android.gms.common.api.Result;

import java.util.List;

public abstract class StateResponse implements Result {
    private boolean error;
    private String msg;
    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {
        private String name;
        private List<String> states;

        public List<String> getStates() {
            return states;
        }
    }
}


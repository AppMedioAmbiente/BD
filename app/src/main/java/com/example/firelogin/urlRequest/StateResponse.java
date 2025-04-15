package com.example.firelogin.urlRequest;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import java.util.List;

public class StateResponse implements Result {
    private boolean error;
    private String msg;
    private Data data;
    private Status status;

    public Status getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public class Data {
        private String name;
        private List<State> states;

        public List<State> getStates() {
            return states;
        }
    }

    public class State {
        private String name;
        private int id; // o el tipo correcto

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }
}



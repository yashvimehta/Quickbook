package com.example.quickbook.ApiHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchBookResult {
    final private boolean success;
    final private String error;
    private ArrayList<HashMap<String,String>> answer;

    public SearchBookResult(boolean success, ArrayList answer, String error) {
        this.success=success;
        this.answer=answer;
        this.error=error;
    }

    public boolean getSuccess() {
        return success;
    }
    public ArrayList getAnswer(){ return answer;}
    public String getError() {
        return error;
    }
}

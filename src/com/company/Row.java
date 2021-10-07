package com.company;

public class Row implements Comparable{
    double[] content;
    public Row(double[] input){
        content = input;
    }

    public int compareTo(Object o){
        if(o instanceof Row){
            Row obj = (Row)o;
            if(content.length == obj.content.length){
                for(int c = 0; c < content.length; c++){
                    if(content[c] != 0 && obj.content[c] == 0){
                        return 1;
                    }else if(content[c] == 1 && obj.content[c] != 1){
                        return 1;
                    }else if(obj.content[c] != 0 && content[c] == 0){
                        return -1;
                    }else if(obj.content[c] == 1 && content[c] != 1){
                        return -1;
                    }else{
                        //otherwise proceed
                    }
                }
                return 0;
            }
            return -99;
        }
        return -999;
    }
    public void multiplyBy(double x){
        for(int i = 0; i < content.length; i++){
            content[i] *= x;
        }
    }
    public void minus(Row r){
        if(content.length == r.content.length){
            for(int i = 0; i < content.length; i++){
                content[i] -= r.content[i];
            }
        }
    }
    public double[] output(){
        return content;
    }
}
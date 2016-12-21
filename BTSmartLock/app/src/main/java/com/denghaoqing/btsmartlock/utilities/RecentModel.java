package com.denghaoqing.btsmartlock.utilities;

/**
 *
 * @author Haoqing Deng
 */
public class RecentModel {
    /*
    * Model:name1,10000;name2,10001
    * */
    private String[] recent=new String[5];
    private String[] recentName=new String[5];
    private String[] recentCode=new String[5];


    public RecentModel(String raw){
        recent=raw.split(";",5);
        for(int i=0;i<(recent.length<=5?recent.length:5);i++){
            if(recent[i].split(",").length>1){
                recentName[i]=recent[i].split(",")[0];
                recentCode[i]=recent[i].split(",")[1];
            }

        }
    }


    public String[] getRecentName() {
        return recentName;
    }

    public String findCodeByID(int id){
        return recentCode[id];
    }

    public String[] getRecent() {
        return recent;
    }

    public void insert(String name,String code){
        for(int i=(recent.length-2<=3?recent.length-2:3);i>=0;i--){
            recent[i+1]=recent[i];
            recentCode[i+1]=recentCode[i];
            recentName[i+1]=recentName[i];
        }
        recent[0]=name+","+code;
        recentCode[0]=code;
        recentName[0]=name;
    }

    public String getRaw(){
        String raw="";
        for(int i=0;i<(recent.length<=5?recent.length:5);i++){
            if(!recent[i].equals("") && !recent[i].equals(",")){
                raw=raw+recent[i]+";";
            }

        }
        return raw;
    }
}

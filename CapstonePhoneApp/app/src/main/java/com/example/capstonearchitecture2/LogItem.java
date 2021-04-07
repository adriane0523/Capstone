package com.example.capstonearchitecture2;


import java.util.Date;

public class LogItem implements Comparable<LogItem>{

    String description = "";
    String name = "";
    String pictureURL = "";
    double probability = 0.0;
    String relation = "";
    String grade = "";
    Date timestamp = null;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGrade(){ return grade; }

    public void setGrade(String grade){ this.grade = grade; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public LogItem(String description, String name, String pictureURL, double probability,
                   String relation, String grade, Date timestamp){
        this.description = description;
        this.name = name;
        this.pictureURL =  pictureURL;
        this.probability = probability;
        this.relation = relation;
        this.grade = grade;
        this.timestamp = timestamp;
    }


    @Override
    public int compareTo(LogItem logItem) {
        if(getTimestamp() == null || logItem.getTimestamp() == null){
            return 0;
        }
        return getTimestamp().compareTo(logItem.getTimestamp());
    }
}

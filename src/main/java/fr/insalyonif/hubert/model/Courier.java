package fr.insalyonif.hubert.model;

public class Courier {
    private int id;

    public Courier(int id) {
        this.id=id;
    }

    public int getId(){
        return this.id;
    }

    public String toString(){
        return "Courier "+this.id;
    }
}


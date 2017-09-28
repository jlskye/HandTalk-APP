/**
 * 章节的实体类
 */
package model;

public class Chapter {
    private int id;
    private String tittle;
    private String describe;

    public Chapter(){
    }
    public Chapter(int id, String title, String describe){
        this.id = id;
        this.tittle = title;
        this.describe = describe;
    }

    public String getTittle() {
        return tittle;
    }

    public String getDescribe() {
        return describe;
    }
    public int getId() {
        return id;
    }

}

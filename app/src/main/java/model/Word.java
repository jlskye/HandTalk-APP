/**
 * 单词的实体类
 */
package model;

public class Word {
    private int id;
    private String title;
    private int chapterID;
    private boolean collected;//是否被收藏
    private int wrong;//错了几次
    private String filename;

    public Word(int id, String title, int chapterID, boolean collected, int wrong, String filename) {
        this.id = id;
        this.title = title;
        this.chapterID = chapterID;
        this.collected = collected;
        this.wrong = wrong;
        this.filename = filename;
    }

    public String getTitle() {
        return title;
    }

    public int getChapterID() {
        return chapterID;
    }

    public int getId() {
        return id;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public int getWrong() {
        return wrong;
    }

    public void setWrong(int wrong) {
        this.wrong = wrong;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Word)) return false;
        Word other = (Word) o;
        return this.getId()==other.getId();
    }

}

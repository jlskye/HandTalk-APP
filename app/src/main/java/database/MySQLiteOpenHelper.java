/**
 * 打开数据库时用的类
 * 因为懒，所以第一次打开应用时，直接将数据插进去（本来应该把数据库文件拷进去的）
 * 不要在其他包里面使用这个类！
 * 不要在其他包里面使用这个类！
 * 不要在其他包里面使用这个类！
 * */
package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper{

    public static int dbVersion = 1;

    public MySQLiteOpenHelper(Context context) {
        super(context, "HandTalkDataBase", null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE word("
                + "id INTEGER PRIMARY KEY,"
                + "name TEXT,"
                + "chapterId INTEGER,"
                + "collected INTEGER,"
                + "wrong INTEGER,"
                + "filename TEXT);");
        db.execSQL("CREATE TABLE chapter("
                + "id INTEGER PRIMARY KEY,"
                + "name TEXT,"
                + "desc TEXT);");
        insertData(db);
    }

    private SQLiteDatabase databaseToInsert;
    private void insertData(SQLiteDatabase db) {
        databaseToInsert = db;
        insertChapter("称谓", "本节主要涉及事物的称谓相关知识，例如：人 他 你 我");
        insertWord("人", "ren");
        insertWord("他", "ta");
        insertWord("她", "ta2");
        insertWord("你", "ni");
        insertWord("我", "wo");
        insertWord("大家", "dajia");
        insertWord("自己", "ziji");
        insertWord("妈妈", "mama");
        insertWord("母亲", "muqin");
        insertWord("父亲", "fuqin");
        insertWord("爸爸", "baba");
        insertWord("聋人", "longren");
        insertWord("朋友", "pengyou");

        insertChapter("空间", "本节主要涉及空间方位相关知识，例如：上 下 左 右");
        insertWord("上", "shang");
        insertWord("下", "xia");
        insertWord("左", "zuo");
        insertWord("右", "you");
        insertWord("前", "qian");
        insertWord("后", "hou");
        insertWord("东", "dong");
        insertWord("南", "nan");
        insertWord("西", "xi");
        insertWord("北", "bei");
        insertWord("位置", "weizhi");
        insertWord("周围", "zhouwei");
        insertWord("对面", "duimian");
        insertWord("方向", "fangxiang");
        insertWord("旁边", "pangbian");

        insertChapter("事物特征", "本节主要涉及事物的特征相关知识，例如：好 坏 大 小");
        insertWord("好", "hao");
        insertWord("坏", "huai");
        insertWord("大", "da");
        insertWord("小", "xiao");
        insertWord("宽", "kuan");
        insertWord("窄", "zhai");
        insertWord("快", "kuai");
        insertWord("慢", "man");
        insertWord("长", "chang");
        insertWord("短", "duan");

        insertChapter("常用动词", "本节主要涉及事物的称谓相关知识，例如：是 说 有 看");
        insertWord("是", "shi");
        insertWord("有", "you");
        insertWord("看", "kan");
        insertWord("要", "yao");
        insertWord("说", "shuo");
        insertWord("请", "qing");
        insertWord("遇见", "yujian");

        insertChapter("时间", "本节主要涉及时间相关知识，例如：今天 以前 以后 年");
        insertWord("今天", "jintian");
        insertWord("以前", "yiqian");
        insertWord("以后", "yihou");
        insertWord("年", "nian");
        insertWord("时间", "shijian");
        insertWord("明天", "mingtian");
        insertWord("昨天", "zuotian");
        insertWord("现在", "xianzai");

        insertChapter("生活用语", "本节主要涉及人们的生活用语相关知识，例如：对不起 再见 名字 哪");
        insertWord("再见", "zaijian");
        insertWord("叫", "jiao");
        insertWord("名字", "mingzi");
        insertWord("哪", "na");
        insertWord("家", "jia");
        insertWord("对不起", "duibuqi");
        insertWord("帮助", "bangzhu");
        insertWord("忙", "mang");
        insertWord("怎么样", "zenmeyang");
        insertWord("欢迎", "huanying");
        insertWord("理发", "lifa");
        insertWord("睡觉", "shuijiao");
        insertWord("祝", "zhu");
        insertWord("谢谢", "xiexieni");

        insertChapter("交通", "本节主要涉及人们的交通用语相关知识，例如：车 船 路 街");
        insertWord("桥", "qiao");
        insertWord("车", "qiche");
        insertWord("自行车", "zixingche");
        insertWord("船", "chuan");
        insertWord("街", "jie");
        insertWord("路", "lu");
        insertWord("道路", "daolu");
        insertWord("飞机", "feiji");
    }

    private int chapterID = -1;
    private int wordID = -1;
    private void insertChapter(String title, String desc){
        chapterID++;
        ContentValues values = new ContentValues();
        values.put("id", chapterID);
        values.put("name", title);
        values.put("desc", desc);
        databaseToInsert.insert("chapter", null, values);

    }
    private void insertWord(String name, String pinyin){
        wordID++;
        ContentValues values = new ContentValues();
        values.put("id", wordID);
        values.put("name", name);
        values.put("chapterId", chapterID);
        values.put("collected", 0);
        values.put("wrong", 0);
        values.put("filename", "c" + chapterID + "_" + pinyin);
        databaseToInsert.insert("word", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}

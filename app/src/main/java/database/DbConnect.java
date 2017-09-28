/**
 * 用之前要init！！
 * 用之前要init！！
 * 用之前要init！！
 * init一次就好
 * 可以通过这个类获取单词、章节的实体对象
 * */
package database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import model.*;

public class DbConnect {
	/**
	 * 初始化 用之前一定要初始化 初始化一次就好 初始化之后再也不用初始化了
	 */
	public static void initDbConnect(Context context) {
		initHepler(context);
	}
	
	/**
	 * 根据单词名字获取其单词实例
	 * */
	public static Word getWordByName(String name){
		SQLiteDatabase db = getReadableDatabase();
		Word word = null;
		// 读
		Cursor cursor = db.query("word", 
				new String[] { "id","name","chapterId", "collected","wrong", "filename" },
				"name='" + name + "'",
				null, null, null, null);
		if (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String title = cursor.getString(cursor.getColumnIndex("name"));
			int chapterid = cursor.getInt(cursor.getColumnIndex("chapterId"));
			int collected_int = cursor.getInt(cursor.getColumnIndex("collected"));
			int wrong = cursor.getInt(cursor.getColumnIndex("wrong"));
			String filename = cursor.getString(cursor.getColumnIndex("filename"));
			boolean collected = false;
			if(collected_int != 0) collected = true;
			
			word = new Word(id, title, chapterid, collected, wrong, filename);
		}
		// 读完了
		db.close();
		return word;
	}
	/**
	 * 检查以这个字符串开头的词有几个
	 * */
	public static int checkWordExist(String word){
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor cursor = db.query("word", new String[]{"name"}, "name LIKE '" + word + "%'", null, null, null, null);
		
		int count = cursor.getCount();
		db.close();
		
		return count;
	}

	/**
	 * 根据可以获取每一个章节的单词 如果chapterid不合法，返回空数组
	 */
	public static List<Word> getWordByChapter(int chapterID) {
		if(chapterID == -1) return getWrongWord();
		if(chapterID == -2) return getCollectedWord();
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<Word> words = new ArrayList<Word>();
		// 读
		Cursor cursor = db.query("word", 
				new String[] { "id","name","chapterId", "collected","wrong", "filename" },
				"chapterId = " + chapterID,
				null, null, null, null);
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String title = cursor.getString(cursor.getColumnIndex("name"));
			int chapterid = cursor.getInt(cursor.getColumnIndex("chapterId"));
			int collected_int = cursor.getInt(cursor.getColumnIndex("collected"));
			int wrong = cursor.getInt(cursor.getColumnIndex("wrong"));
			String filename = cursor.getString(cursor.getColumnIndex("filename"));
			boolean collected = false;
			if(collected_int != 0) collected = true;
			
			Word w = new Word(id, title, chapterid, collected, wrong, filename);
			words.add(w);
		}
		// 读完了
		db.close();
		return words;
	}
	
	/**
	 * 获取错题本单词
	 * */
	public static List<Word> getWrongWord(){
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<Word> words = new ArrayList<Word>();
		// 读
		Cursor cursor = db.query("word", 
				new String[] { "id","name","chapterId", "collected","wrong", "filename" },
				"wrong>0",
				null, null, null, null);
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String title = cursor.getString(cursor.getColumnIndex("name"));
			int chapterid = cursor.getInt(cursor.getColumnIndex("chapterId"));
			int collected_int = cursor.getInt(cursor.getColumnIndex("collected"));
			int wrong = cursor.getInt(cursor.getColumnIndex("wrong"));
			String filename = cursor.getString(cursor.getColumnIndex("filename"));
			boolean collected = false;
			if(collected_int != 0) collected = true;
			
			Word w = new Word(id, title, chapterid, collected, wrong, filename);
			words.add(w);
		}
		// 读完了
		db.close();
		return words;
	}
	
	/**
	 * 获取生词本单词
	 * */
	public static List<Word> getCollectedWord(){
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<Word> words = new ArrayList<Word>();
		// 读
		Cursor cursor = db.query("word", 
				new String[] { "id","name","chapterId", "collected","wrong", "filename" },
				"collected>0",
				null, null, null, null);
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String title = cursor.getString(cursor.getColumnIndex("name"));
			int chapterid = cursor.getInt(cursor.getColumnIndex("chapterId"));
			int collected_int = cursor.getInt(cursor.getColumnIndex("collected"));
			int wrong = cursor.getInt(cursor.getColumnIndex("wrong"));
			String filename = cursor.getString(cursor.getColumnIndex("filename"));
			boolean collected = false;
			if(collected_int != 0) collected = true;
			
			Word w = new Word(id, title, chapterid, collected, wrong, filename);
			words.add(w);
		}
		// 读完了
		db.close();
		return words;
	}

	/**
	 * 根据单词ID获取单词
	 */
	public static Word getWordByID(int wordID) {
		SQLiteDatabase db = getReadableDatabase();
		Word word = null;
		// 读
		Cursor cursor = db.query("word", 
				new String[] { "id","name","chapterId", "collected","wrong", "filename" },
				"id = " + wordID,
				null, null, null, null);
		if (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String title = cursor.getString(cursor.getColumnIndex("name"));
			int chapterid = cursor.getInt(cursor.getColumnIndex("chapterId"));
			int collected_int = cursor.getInt(cursor.getColumnIndex("collected"));
			int wrong = cursor.getInt(cursor.getColumnIndex("wrong"));
			String filename = cursor.getString(cursor.getColumnIndex("filename"));
			boolean collected = false;
			if(collected_int != 0) collected = true;
			
			word = new Word(id, title, chapterid, collected, wrong, filename);
		}
		// 读完了
		db.close();
		return word;
	}

	/**
	 * 根据章节ID获取章节
	 */
	public static Chapter getChapterByID(int chapterID) {
		if(chapterID == -1) return getWrongChapter();//id为-1就代表错题本
		if(chapterID == -2) return getCollectedChapter();//id为-2就代表生词本
		SQLiteDatabase db = getReadableDatabase();
		Chapter chapter = null;
		// 读
		Cursor cursor = db.query("chapter", 
				new String[] { "id","name","desc"},
				"id = " + chapterID,
				null, null, null, null);
		if (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String title = cursor.getString(cursor.getColumnIndex("name"));
			String descript = cursor.getString(cursor.getColumnIndex("desc"));

			chapter = new Chapter(id, title ,descript);
		}
		// 读完了
		db.close();
		return chapter;
	}

	/**
	 * 获取所有的章节
	 */
	public static List<Chapter> getChapters() {
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<Chapter> chapters = new ArrayList<Chapter>();
		// 读
		Cursor cursor = db.query("chapter", 
				new String[] { "id","name","desc"},
				null, null, null, null, null);
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String title = cursor.getString(cursor.getColumnIndex("name"));
			String descript = cursor.getString(cursor.getColumnIndex("desc"));

			Chapter chapter = new Chapter(id, title ,descript);
			chapters.add(chapter);
		}
		// 读完了
		db.close();
		
		chapters.add(getWrongChapter());//添加错题本
		chapters.add(getCollectedChapter());//添加生词本
		
		return chapters;
	}
	
	/**
	 * 更新单词，会更新它的被收藏、错的次数
	 * */
	public static void updateWord(Word word){
		SQLiteDatabase db = getWriteableDatabase();
		ContentValues values = new ContentValues();
		int collected = 0;
		if(word.isCollected()) collected = 1;
		values.put("collected", collected);
		values.put("wrong", word.getWrong());
		db.update("word", values, "id=?", new String[] { ""+word.getId() });
		db.close();
	}

	/**
	 * 获取一个错题本章节对象
	 * */
	public static Chapter getWrongChapter(){
		return new Chapter(-1, "错题本", "做错过的题目，共 " + getWrongWordNumber() + "个");
	}
	
	/**
	 * 获取一个生词本对象
	 * */
	public static Chapter getCollectedChapter(){
		return new Chapter(-2, "生词本", "共" + getCollectedWordNumber() + "个");
	}
	
	/**
	 * 获取错题本里面错题数量
	 * */
	public static int getWrongWordNumber(){
		return getWordByChapter(-1).size();
	}
	
	/**
	 * 获取生词本里面的数量
	 * */
	public static int getCollectedWordNumber(){
		return getWordByChapter(-2).size();
	}
	
	private static MySQLiteOpenHelper helper;

	private static void initHepler(Context context) {
		helper = new MySQLiteOpenHelper(context);
	}

	private static SQLiteDatabase getReadableDatabase() {
		return helper.getReadableDatabase();
	}

	private static SQLiteDatabase getWriteableDatabase() {
		return helper.getWritableDatabase();
	}
}

package wordspliter;

import java.util.ArrayList;
import java.util.List;
import database.DbConnect;
import model.Word;

public class WordSpliter {
    /**
     * 传入句子和上下文，可以给根据词库分词
     * 词库存在数据库中
     * 该函数涉及IO操作（访问数据库）
     * */
    public static List<String> splitWordToString(String sentence){
        String str = sentence;
        if(str == null) return null;
        if(str.length() == 0) return null;

        ArrayList<String> words = new ArrayList<String>();
        String word = "";
        String newWord = "";
        for(int i = 0; i<str.length(); i++){
            if(word.length() == 0){
                word = str.charAt(i) + "";
                newWord = word;
            }else{
                word = newWord;
                newWord = word + str.charAt(i);//尝试拼出一个更长的词
            }
            if(DbConnect.checkWordExist(newWord) == 0){
                words.add(word);
                if(newWord.length() > 1){
                    i--;//吸了一个多余的字，把它吐出来
                }
                word = "";
            }
        }
        if(DbConnect.checkWordExist(newWord) > 0){
            words.add(newWord);
        }

        return (words);
    }
    public static List<Word> splitWord(String sentence){
        List<String> stringList = splitWordToString(sentence);
        if(stringList == null || stringList.size()==0) return null;
        List<Word> wordList = new ArrayList<Word>();
        for(String s:stringList){
            Word w = DbConnect.getWordByName(s);
            if(w != null)
                wordList.add(w);
        }
        return wordList;
    }
}

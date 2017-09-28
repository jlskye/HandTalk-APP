package animation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.content.Context;

public class MotionReader{
    private static Motion middleMotion = null;
    /**站立姿势*/
    private static Motion standMotion = null;
    /**
     * 获取两个动作之间的过渡动作
     * m1:从这个动作开始
     * m2:要变成这个动作
     * transform:变换率，取值从0到1，意思是从m1到m2要变换多少
     * */
    public static Motion getMiddleMotionBetween(Motion m1, Motion m2, float transform){
        if(middleMotion == null){
            middleMotion = getNewMotion();
        }
        for(int i=0; i<BoneManager.getBoneCount(); i++){
            float f1 = m1.getBonesRotate001()[i];
            float f2 = m2.getBonesRotate001()[i];
            float f3 = calculateMiddleFloat(f1, f2, transform);
            middleMotion.getBonesRotate001()[i] = f3;

            f1 = m1.getBonesRotate010()[i];
            f2 = m2.getBonesRotate010()[i];
            f3 = calculateMiddleFloat(f1, f2, transform);
            middleMotion.getBonesRotate010()[i] = f3;

            f1 = m1.getBonesRotate100()[i];
            f2 = m2.getBonesRotate100()[i];
            f3 = calculateMiddleFloat(f1, f2, transform);
            middleMotion.getBonesRotate100()[i] = f3;
        }
        return middleMotion;
    }
    /**
     * 计算两个float中间值
     * f1：从这个float开始
     * f2：要变成这个float
     * transform:要变多少
     * 注意：f1和f2都是角度（单位为度），比方说从 0 到361的中间值(transform=0.5)应该为360.5或者是-0.5
     * */
    private static float calculateMiddleFloat(float f1, float f2, float transform){
        float ret = 0;
        f1 = f1%360;
        f2 = f2%360;
        if(f1 < 0) f1 += 360;
        if(f2 < 0) f2 += 360;
        float df = f2 - f1;
        if(df > 180) df -= 360;
        else if(df < -180) df += 360;
        ret = f1 + df*transform;
        return ret;
    }
    /**
     * 读取动作文件，后缀名应该为.mot
     * 在此之前请务必先执行BoneManager中的readBone方法
     * 注意，这个方法会new出一个Motion，会占用些许内存，可以试着用readMotionInAssetsIntoMotion这个方法，填一个没用的motion进去
     * */
    public static Motion readMotionInAssets(Context context, String fileName) throws Exception{
        InputStream is = context.getAssets().open(fileName);
        Motion motion = getNewMotion();
        parserMotionFile(is, motion);
        return motion;
    }
    /**
     * 读取动作文件，后缀名应该为.mot
     * 在此之前请务必先执行BoneManager中的readBone方法
     * */
    public static void readMotionInAssetsIntoMotion(Context context, String fileName,Motion motion) throws Exception{
        InputStream is = context.getAssets().open(fileName);
        parserMotionFile(is, motion);
    }

    public static void readMotionInSDcardIntoMotion(File file, Motion motion){
        try {
            FileInputStream fis = new FileInputStream(file);
            parserMotionFile(fis, motion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取一个新的Motion，里面没数据
     * 要慎重调用，因为要占挺多内存
     * */
    public static Motion getNewMotion() {
        float[] rotate001 = new float[BoneManager.getBoneCount()];
        float[] rotate010 = new float[BoneManager.getBoneCount()];
        float[] rotate100 = new float[BoneManager.getBoneCount()];
        Motion newMotion = new Motion(rotate001, rotate010, rotate100);
        return newMotion;
    }

    private static void parserMotionFile(InputStream is, Motion motion) throws Exception{
        BufferedInputStream bis = new BufferedInputStream(is);
        for(int i=0; i<BoneManager.getBoneCount(); i++){
            int boneID = Util.readInt(bis);
            if(boneID<0 || boneID>=BoneManager.getBoneCount())
                throw new Exception("boneIDs in motionFile are wrong!");

            float rot001 = Util.readFloat(bis);
            float rot010 = Util.readFloat(bis);
            float rot100 = Util.readFloat(bis);

            motion.getBonesRotate001()[boneID] = rot001;
            motion.getBonesRotate010()[boneID] = rot010;
            motion.getBonesRotate100()[boneID] = rot100;
        }
        bis.close();
    }
    /**
     * 站立姿势
     * 使用前一定要setStandMotion()!!!
     * 使用前一定要setStandMotion()!!!
     * 使用前一定要setStandMotion()!!!
     */
    public static Motion getStandMotion() {
        return standMotion;
    }
    public static void setStandMotion(Context context) throws Exception {
        if(standMotion == null){
            standMotion = readMotionInAssets(context, "motion/standmotion.mot");
        }
    }
}

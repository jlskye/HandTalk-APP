package animation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import android.content.Context;

public class Motion {
    private float[] boneRotate001;//位置索引即骨头ID
    private float[] boneRotate010;//位置索引即骨头ID
    private float[] boneRotate100;//位置索引即骨头ID
    private boolean isKeyMotion = true;

    protected Motion(float[] rotate001, float[] rotate010, float[] rotate100) {
        this.boneRotate001 = rotate001;
        this.boneRotate010 = rotate010;
        this.boneRotate100 = rotate100;
    }
    public float[] getBonesRotate001() {
        return boneRotate001;
    }
    public float[] getBonesRotate010() {
        return boneRotate010;
    }
    public float[] getBonesRotate100() {
        return boneRotate100;
    }
    public float getBoneRotate001(int boneID){
        if(boneID < 0 || boneID >= BoneManager.getBoneCount()){
            return 0;
        }
        return getBonesRotate001()[boneID];
    }
    public float getBoneRotate010(int boneID){
        if(boneID < 0 || boneID >= BoneManager.getBoneCount()){
            return 0;
        }
        return getBonesRotate010()[boneID];
    }
    public float getBoneRotate100(int boneID){
        if(boneID < 0 || boneID >= BoneManager.getBoneCount()){
            return 0;
        }
        return getBonesRotate100()[boneID];
    }
    public void setBoneRotate001(int boneID, float rotation){
        boneRotate001[boneID] = rotation;
    }
    public void setBoneRotate010(int boneID, float rotation){
        boneRotate010[boneID] = rotation;
    }
    public void setBoneRotate100(int boneID, float rotation){
        boneRotate100[boneID] = rotation;
    }
    public boolean isKeyMotion() {
        return isKeyMotion;
    }
    public void setKeyMotion(boolean isKeyMotion) {
        this.isKeyMotion = isKeyMotion;
    }
}





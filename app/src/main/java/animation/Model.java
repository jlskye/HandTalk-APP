package animation;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class Model {
    private int faceCount;
    private float[] verts;// 点坐标
    private float[] vnorms;// 法向量
    private short[] remarks;// 三角面属性
    private float[] cols;// 颜色
    private float[] textures;
    private Bitmap bitmap;
    private boolean isGreen = false;

    private FloatBuffer vertBuffer;
    private FloatBuffer vnormBuffer;
    private FloatBuffer ambBuffer;
    private FloatBuffer diffBuffer;
    private FloatBuffer specBuffer;
    private FloatBuffer texturesBuffer;

    private int boneID;

    public static float maxX, minX, maxY, minY, maxZ, minZ;

    public static Point getCenterPoint() {
        float cx = minX + (maxX - minX) / 2;
        float cy = minY + (maxY - minY) / 2;
        float cz = minZ + (maxZ - minZ) / 2;
        return new Point(cx, cy, cz);
    }

    /**
     * 获取最大半径，获取的是整个大模型的
     */
    public static float getR() {
        float dx = (maxX - minX);
        float dy = (maxY - minY);
        float dz = (maxZ - minZ);
        float max = dx;
        if (dy > max)
            max = dy;
        if (dz > max)
            max = dz;
        return max;
    }

    /**
     * 返回这个模型有没有贴图
     * */
    public boolean haveTexture(){
        return getTexturesBuffer()!=null;
    }

    public void setColToGreen() {
        isGreen = true;
    }

    public void setColToNormal() {
        isGreen = false;
    }

    public boolean isGreen() {
        return isGreen;
    }

    public int getFaceCount() {
        return faceCount;
    }

    public void setFaceCount(int faceCount) {
        this.faceCount = faceCount;
    }

    public float[] getVerts() {
        return verts;
    }

    public void setVerts(float[] verts) {
        this.verts = verts;
        vertBuffer = Util.floatToBuffer(verts);
    }

    public float[] getVnorms() {
        return vnorms;
    }

    public void setVnorms(float[] vnorms) {
        this.vnorms = vnorms;
        vnormBuffer = Util.floatToBuffer(vnorms);
    }

    public void setCols(float[] cols) {
        this.cols = cols;
        setAmbBuffer(cols);
        setDiffBuffer(cols);
        setSpecBuffer(cols);
    }

    public short[] getRemarks() {
        return remarks;
    }

    public void setRemarks(short[] remarks) {
        this.remarks = remarks;
    }

    public FloatBuffer getVertBuffer() {
        return vertBuffer;
    }

    public void setVertBuffer(FloatBuffer vertBuffer) {
        this.vertBuffer = vertBuffer;
    }

    public FloatBuffer getVnormBuffer() {
        return vnormBuffer;
    }

    public void setVnormBuffer(FloatBuffer vnormBuffer) {
        this.vnormBuffer = vnormBuffer;
    }

    public float[] getCols() {
        return cols;
    }

    public int getBoneID() {
        return boneID;
    }

    public void setBoneID(int boneID) {
        this.boneID = boneID;
    }

    public FloatBuffer getAmbBuffer() {
        return ambBuffer;
    }

    public void setAmbBuffer(float[] cols) {
        // 二阶阴影
        float[] ambArr = new float[4];
        for (int i = 0; i < 4; i++) {
            ambArr[i] = cols[i] * 0.3f;
        }
        ambArr[3] = 1.0f;
        ambBuffer = Util.floatToBuffer(ambArr);
    }

    public FloatBuffer getDiffBuffer() {
        return diffBuffer;
    }

    public void setDiffBuffer(float[] cols) {
        // 一阶阴影
        float[] difArr = new float[4];
        for (int i = 0; i < 4; i++) {
            difArr[i] = cols[i] * 0.7f;
        }
        difArr[3] = 1.0f;
        diffBuffer = Util.floatToBuffer(difArr);
    }

    public FloatBuffer getSpecBuffer() {
        return specBuffer;
    }

    public void setSpecBuffer(float[] cols) {
        // 本色
        float[] specArr = new float[4];
        for (int i = 0; i < 4; i++) {
            specArr[i] = cols[i] * 0.8f;
        }
        specArr[3] = 1.0f;
        specBuffer = Util.floatToBuffer(specArr);
    }

    public float[] getTextures() {
        return textures;
    }

    /**
     * 传入点数据、贴图文件名
     * */
    public void setTextures(float[] textures) {
        this.textures = textures;
        setTexturesBuffer(Util.floatToBuffer(textures));
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public FloatBuffer getTexturesBuffer() {
        return texturesBuffer;
    }

    public void setTexturesBuffer(FloatBuffer texturesBuffer) {
        this.texturesBuffer = texturesBuffer;
    }

}


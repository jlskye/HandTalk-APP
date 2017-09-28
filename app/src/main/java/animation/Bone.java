package animation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class Bone {
    private int id;
    private int parentId;
    private float x;
    private float y;
    private float z;
    private ArrayList<Bone> childBones;
    public Bone(int id, int parentId, float x, float y, float z) {
        this.id = id;
        this.parentId = parentId;
        this.x = x;
        this.y = y;
        this.z = z;
        childBones = new ArrayList<Bone>();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getParentId() {
        return parentId;
    }
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }
    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }
    public float getZ() {
        return z;
    }
    public void setZ(float z) {
        this.z = z;
    }
    public ArrayList<Bone> getChildBones() {
        return childBones;
    }
}



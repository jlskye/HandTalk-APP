package animation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;

public class BoneManager {
    private static Bone[] bones;
    private static int boneCount = 0;
    private static Bone rootBone;
    private static boolean isBoneReaded = false;
    /**
     * 读取骨头文件，读到的数据会有BoneManager进行保管
     * 使用骨骼管理器之前先执行这个函数
     * 读取的文件后缀名应该为.bon
     * */
    public static void readBoneInAssets(Context context, String fileName) throws Exception{
        if(isBoneReaded) return;//如果已经读过了，就不读了
        InputStream is = context.getAssets().open(fileName);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        parserBoneFile(br);
        br.close();
        isBoneReaded = true;
    }
    /**
     * 解析骨头文件
     * */
    private static void parserBoneFile(BufferedReader br) throws Exception{
        int count = 0;
        String line = br.readLine();
        ArrayList<Bone> temp_bones = new ArrayList<Bone>();

        while(line != null){
            if(line.startsWith("//"))continue;//跳过注释

            int boneID;
            int boneParentID;
            float boneX, boneY, boneZ;

            count++;

            String[] datas = line.split(";");

            if(datas[1].toLowerCase().equals("mainbone")){
                //是主骨头，父骨头ID为-1
                boneParentID = -1;
                boneX = 0;
                boneY = 0;
                boneZ = 0;
            }else{
                //不是主骨头
				/*
				 * 这里本应该是X Y Z的排序。但是因为3DMAX和OPENGL坐标系不同，应改成XZY
				 * */
                boneParentID = Integer.parseInt(datas[1]);
                boneX = Float.parseFloat(datas[2]);
                boneZ = Float.parseFloat(datas[3]);
                boneY = Float.parseFloat(datas[4]);
            }
            boneID = Integer.parseInt(datas[0]);

            Bone abone = new Bone(boneID, boneParentID, boneX, boneY, boneZ);
            temp_bones.add(abone);
            if(abone.getParentId() == -1){
                //如果是主骨头
                rootBone = abone;
            }

            line = br.readLine();
        }

        boneCount = count;
        bones = new Bone[count];
        for(Bone b:temp_bones){
            int id = b.getId();
            if(id < 0 || id >= count){
                throw new Exception("IDs in boneFile are wrong");
            }
            bones[id] = b;//id即位置
        }

        //设置孩子
        for(Bone b:bones){
            if(b.getParentId() == -1) continue;
            int pid = b.getParentId();
            Bone pBone = getBoneByID(pid);
            pBone.getChildBones().add(b);
        }
    }
    public static int getBoneCount() {
        return boneCount;
    }
    public static Bone[] getBones() {
        return bones;
    }
    public static Bone getBoneByID(int id){
        if(id < 0 || id >= getBoneCount()){
            return null;
        }
        return getBones()[id];
    }
    public static Bone getRootBone() {
        return rootBone;
    }
}

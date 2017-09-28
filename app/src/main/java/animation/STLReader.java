package animation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class STLReader {

    private LoadListener stlLoadListener;

    public Model parserBinStlInSDCard(String path) throws IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        return parserBinStl(fis);
    }

    public Model parserBinStlInAssets(Context context, String fileName) throws IOException {
        InputStream is = context.getAssets().open(fileName);
        if(fileName.contains("eyes.mstl")){
            readTexture(context, "testmodel/eyes.mpxy", "testmodel/eyes.jpg");
        }
        return parserBinStl(is);
    }
    private float[] texture;
    private Bitmap bitmap;
    private void readTexture(Context context, String textureFileName, String bitmapFileName) throws IOException {
        InputStream is = context.getAssets().open(textureFileName);
        BufferedInputStream in = new BufferedInputStream(is);
        int faceCount = Util.readInt(in);
        texture = new float[faceCount * 3 * 2];
        for(int i=0; i<texture.length; i++){
            texture[i] = Util.readFloat(in);
        }
        bitmap = BitmapFactory.decodeStream(context.getAssets().open(bitmapFileName));
    }

    // 解析二进制的Stl文件
    public Model parserBinStl(InputStream in) throws IOException {
        if (stlLoadListener != null)
            stlLoadListener.onstart();
        Model model = new Model();

        // 读取颜色
        byte[] colBytes = new byte[16];
        in.read(colBytes);

        // 读取绑定骨头ID
        byte[] boneBytes = new byte[4];
        in.read(boneBytes);

        // 下面是读取正常STL的代码

        // 前面80字节是文件头，用于存贮文件名；
        in.skip(80);

        // 紧接着用 4 个字节的整数来描述模型的三角面片个数
        byte[] bytes = new byte[4];
        in.read(bytes);// 读取三角面片个数
        int facetCount = Util.byte4ToInt(bytes, 0);
        model.setFaceCount(facetCount);
        if (facetCount == 0) {
            in.close();
            return model;
        }

        // 每个三角面片占用固定的50个字节
        byte[] facetBytes = new byte[50 * facetCount];
        // 将所有的三角面片读取到字节数组
        in.read(facetBytes);
        // 数据读取完毕后，可以把输入流关闭
        in.close();

        readColor(model, colBytes, facetCount);// 放颜色
        readBone(model, boneBytes);// 放骨头
        parseModel(model, facetBytes);// 放点坐标、向量
        if(texture != null){
            model.setTextures(texture);
            model.setBitmap(bitmap);
            texture = null;
            bitmap = null;
        }

        if (stlLoadListener != null)
            stlLoadListener.onFinished();
        return model;
    }

    /**
     * 读取骨头信息
     */
    private void readBone(Model model, byte[] boneBytes) {
        int boneID = Util.byte4ToInt(boneBytes, 0);
        model.setBoneID(boneID);
    }

    /**
     * 读取颜色信息
     */
    private void readColor(Model model, byte[] cols, int facetCount) {
        float[] cols_f = new float[4];
        float r = Util.byte4ToFloat(cols, 0);
        float g = Util.byte4ToFloat(cols, 4);
        float b = Util.byte4ToFloat(cols, 8);
        float a = Util.byte4ToFloat(cols, 12);
        cols_f[0] = r;
        cols_f[1] = g;
        cols_f[2] = b;
        cols_f[3] = a;
        model.setCols(cols_f);
    }

    /**
     * 解析模型数据，包括顶点数据、法向量数据、所占空间范围等
     */
    private void parseModel(Model model, byte[] facetBytes) {
        int facetCount = model.getFaceCount();
        /**
         * 每个三角面片占用固定的50个字节,50字节当中： 三角片的法向量：（1个向量相当于一个点）*（3维/点）*（4字节浮点数/维）=12字节
         * 三角片的三个点坐标：（3个点）*（3维/点）*（4字节浮点数/维）=36字节 最后2个字节用来描述三角面片的属性信息
         **/
        // 保存所有顶点坐标信息,一个三角形3个顶点，一个顶点3个坐标轴
        float[] verts = new float[facetCount * 3 * 3];
        // 保存所有三角面对应的法向量位置，
        // 一个三角面对应一个法向量，一个法向量有3个点
        // 而绘制模型时，是针对需要每个顶点对应的法向量，因此存储长度需要*3
        // 又同一个三角面的三个顶点的法向量是相同的，
        // 因此后面写入法向量数据的时候，只需连续写入3个相同的法向量即可
        float[] vnorms = new float[facetCount * 3 * 3];
        // 保存所有三角面的属性信息
        short[] remarks = new short[facetCount];

        int stlOffset = 0;
        try {
            for (int i = 0; i < facetCount; i++) {
                if (stlLoadListener != null) {
                    stlLoadListener.onLoading(i, facetCount);
                }
                for (int j = 0; j < 4; j++) {
                    /**
                     * 本来下面三行应该按照xyz排序 可是我测试的时候用的康娜模型，其中应该按XZY排序才能正确显示
                     * 正式版的时候可能还会改成其他顺序，看哪种正确就用哪种吧
                     */
                    float x = Util.byte4ToFloat(facetBytes, stlOffset);
                    float z = Util.byte4ToFloat(facetBytes, stlOffset + 4);
                    float y = Util.byte4ToFloat(facetBytes, stlOffset + 8);
                    stlOffset += 12;

                    if (j == 0) {// 法向量
                        vnorms[i * 9] = x;
                        vnorms[i * 9 + 1] = y;
                        vnorms[i * 9 + 2] = z;
                        vnorms[i * 9 + 3] = x;
                        vnorms[i * 9 + 4] = y;
                        vnorms[i * 9 + 5] = z;
                        vnorms[i * 9 + 6] = x;
                        vnorms[i * 9 + 7] = y;
                        vnorms[i * 9 + 8] = z;
                    } else {// 三个顶点
                        verts[i * 9 + (j - 1) * 3] = x;
                        verts[i * 9 + (j - 1) * 3 + 1] = y;
                        verts[i * 9 + (j - 1) * 3 + 2] = z;

                        // 记录模型中三个坐标轴方向的最大最小值
                        if (i == 0 && j == 1) {
                            Model.minX = Model.maxX = x;
                            Model.minY = Model.maxY = y;
                            Model.minZ = Model.maxZ = z;
                        } else {
                            Model.minX = Math.min(Model.minX, x);
                            Model.minY = Math.min(Model.minY, y);
                            Model.minZ = Math.min(Model.minZ, z);
                            Model.maxX = Math.max(Model.maxX, x);
                            Model.maxY = Math.max(Model.maxY, y);
                            Model.maxZ = Math.max(Model.maxZ, z);
                        }
                    }
                }
                short r = Util.byte2ToShort(facetBytes, stlOffset);
                stlOffset = stlOffset + 2;
                remarks[i] = r;
            }
        } catch (Exception e) {
            if (stlLoadListener != null) {
                stlLoadListener.onFailure(e);
            } else {
                e.printStackTrace();
            }
        }
        // 将读取的数据设置到Model对象中
        model.setVerts(verts);
        model.setVnorms(vnorms);
        model.setRemarks(remarks);

    }
}

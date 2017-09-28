package animation;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;

public class MyRenderer implements Renderer {

    private Point mCenterPoint;
    private Point eye = new Point(0, 0, -3);
    private Point up = new Point(0, 1, 0);
    private Point center = new Point(0, 0, 0);
    private float mScalef = 1;
    private float mr = 0;
    private float bodyRotation = 0;//全身的转动（骨头0的010转动）

    private Bone rootBone;
    private Motion showMotion;

    public MyRenderer(Context context) {
    }

    public void addRotation(float rotation){
        bodyRotation += rotation;
        bodyRotation = bodyRotation%360;
        if(bodyRotation < 0) bodyRotation += 360;
    }

    /**
     * 设置下次刷新显示的内容。传入主骨头和要显示的动作。调用完这个之后要调用invalidate();才能显示
     * */
    public void setFrame(Bone rootBone, Motion motion){
        this.rootBone = rootBone;
        this.showMotion = motion;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清除屏幕和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        //设置背景颜色
        gl.glClearColor(0.9f, 0.9f, 0.9f, 1f);
        //允许给每个顶点设置法向量
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        // 允许设置顶点
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // 允许设置颜色
        //gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glLoadIdentity();// 重置当前的模型观察矩阵
        //眼睛对着原点看 
        GLU.gluLookAt(gl, eye.x, eye.y, eye.z, eye.x,
                eye.y, mCenterPoint.z, up.x, up.y, up.z);

        /*=========开始画================*/
        if(showMotion != null) showMotion.setBoneRotate010(0, bodyRotation);//转动全身
        drawModelsByMotion(gl, rootBone, showMotion);
        /*=========画好了================*/


        //取消颜色设置
        //gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        //取消顶点设置
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        //取消法向量设置
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
    }

    /**
     * 画出以这根骨头，以及以这根骨头为老大的所有孩子骨头。根据这个动作
     * */
    private void drawModelsByMotion(GL10 gl, Bone rootBone, Motion motion){
        if(rootBone == null) return;
        if(motion == null) return;
        gl.glPushMatrix();//储存矩阵入栈

        translateByBone(gl, rootBone, motion);//变换矩阵
        drawModelsByABone(gl, rootBone, motion);//先画自己

        ArrayList<Bone> childBones = rootBone.getChildBones();
        if(childBones != null){
            for(Bone childBone:childBones){
                drawModelsByMotion(gl, childBone, motion);//递归画孩子
            }
        }

        gl.glPopMatrix();//画完矩阵出栈
    }
    /**
     * 调用这个函数之前，先做好矩阵变换。这个函数直接画
     * */
    private void drawModelsByABone(GL10 gl, Bone bone, Motion motion){
        ArrayList<Model> models = ModelManager.getModels();
        for(Model m:models){
            if(m.getBoneID() == bone.getId()){
                //如果是这根骨头上的 就按照这样渲染
                drawModel(gl, m);
            }
        }
    }
    /**
     * 根据这根骨头的朝向做矩阵变换
     * */
    private void translateByBone(GL10 gl, Bone bone, Motion motion){
        float x = bone.getX();
        float y = bone.getY();
        float z = bone.getZ();
        int id = bone.getId();
        rotateAroundIn001(gl, motion.getBoneRotate001(id), x, y);
        rotateAroundIn010(gl, motion.getBoneRotate010(id), x, z);
        rotateAroundIn100(gl, motion.getBoneRotate100(id), y, z);
    }

    /**
     * 绕着向量(0,0,1)这个方向（右手原则），绕着(around_x,around_y)这个坐标，旋转angle角度
     * 注意：坐标是经过世界坐标变换前的
     * 例：传入参数时是(1,0)，但是世界矩阵变换transform(5,0)，实际上就是绕着(6,0)转
     * */
    private void rotateAroundIn001(GL10 gl, float angle, float around_x, float around_y){
        gl.glTranslatef(around_x, around_y, 0);
        gl.glRotatef(angle, 0, 0, 1);
        gl.glTranslatef(-around_x, -around_y, 0);
    }
    /**
     * 绕着向量(0,1,0)这个方向（右手原则），绕着这个坐标，旋转angle角度
     * 注意：坐标是经过世界坐标变换前的
     * 例：传入参数时是(1,0)，但是世界矩阵变换transform(5,0)，实际上就是绕着(6,0)转
     * */
    private void rotateAroundIn010(GL10 gl, float angle, float around_x, float around_z){
        gl.glTranslatef(around_x, 0, around_z);
        gl.glRotatef(angle, 0, 1, 0);
        gl.glTranslatef(-around_x, 0, -around_z);
    }
    /**
     * 绕着向量(1,0,0)这个方向（右手原则），绕着这个坐标，旋转angle角度
     * 注意：坐标是经过世界坐标变换前的
     * 例：传入参数时是(1,0)，但是世界矩阵变换transform(5,0)，实际上就是绕着(6,0)转
     * */
    private void rotateAroundIn100(GL10 gl, float angle, float around_y, float around_z){
        gl.glTranslatef(0, around_y, around_z);
        gl.glRotatef(angle, 1, 0, 0);
        gl.glTranslatef(0, -around_y, -around_z);
    }

    private void drawModel(GL10 gl, Model m){
        //设置法向量数据源
        gl.glNormalPointer(GL10.GL_FLOAT, 0, m.getVnormBuffer());
        // 设置三角形顶点数据源
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m.getVertBuffer());
        // 设置顶点颜色数据，直接设置材料就好了
        //gl.glColorPointer(4, GL10.GL_FLOAT, 0, m.getColBuffer());
        if(m.isGreen()){
            setMaterialToGreen(gl);
        }else{
            setMaterial(gl, m);
        }
        if(m.haveTexture())loadTexture(gl, m);
        else gl.glDrawArrays(GL10.GL_TRIANGLES, 0, m.getFaceCount() * 3);// 绘制三角形
    }
    private int[] textures = {0};
    private boolean seted = false;
    /**加载贴图*/
    private void loadTexture(GL10 gl, Model m){
        //允许使用贴图
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        if(!seted){
            // 生成一个纹理对象，并将其ID保存到成员变量 texture 中
            gl.glGenTextures(1, textures, 0);
            // 设置2D纹理通道当前绑定的纹理的属性
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                    GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                    GL10.GL_LINEAR);

            // 将bitmap应用到2D纹理通道当前绑定的纹理中
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, m.getBitmap(), 0);
            seted = true;
        }

        gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, m.getTexturesBuffer());

        //绘制三角形
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, m.getFaceCount() * 3);

        //取消使用贴图
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
    }
    //眼睛远离模型多远，单位是模型半径
    private int dis = 4;
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        // 设置OpenGL场景的大小,(0,0)表示窗口内部视口的左下角，(width, height)指定了视口的大小
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_PROJECTION); // 设置投影矩阵
        gl.glLoadIdentity(); // 设置矩阵为单位矩阵，相当于重置矩阵
        GLU.gluPerspective(gl, 45.0f, ((float) width) / height, (dis-3)*mr, (dis+3)*mr);// 设置透视范围

        //以下两句声明，以后所有的变换都是针对模型(即我们绘制的图形)
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glEnable(GL10.GL_DEPTH_TEST); // 启用深度缓存
        gl.glClearDepthf(1.0f); // 设置深度缓存值
        gl.glDepthFunc(GL10.GL_LEQUAL); // 设置深度缓存比较函数
        gl.glShadeModel(GL10.GL_SMOOTH);// 设置阴影模式GL_SMOOTH
        mr = Model.getR();
        mScalef = 0.5f / mr;
        openLight(gl);
        mCenterPoint = Model.getCenterPoint();
        eye.x = 0;
        eye.y = mCenterPoint.y + 200;
        eye.z = mCenterPoint.z - dis*mr;
    }


    float[] ambient = {0.7f, 0.7f, 0.7f, 1.0f};
    float[] diffuse = {0.8f, 0.8f, 0.8f, 1.0f};
    float[] specular = {0.5f, 0.5f, 0.5f, 0.5f};
    float[] lightPosition = {1.0f, 1.0f, 0.5f, 0.0f};
    public void openLight(GL10 gl) {
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, Util.floatToBuffer(ambient));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, Util.floatToBuffer(diffuse));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, Util.floatToBuffer(specular));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, Util.floatToBuffer(lightPosition));
    }

    float[] materialAmb = {0.0f, 0.5f, 0.0f, 1.0f};
    float[] materialDiff = {0.0f, 0.75f, 0.0f, 1.0f};
    float[] materialSpec = {0.0f, 1.0f, 0.0f, 1.0f};
    FloatBuffer amdBuffer = Util.floatToBuffer(materialAmb);
    FloatBuffer diffBuffer = Util.floatToBuffer(materialDiff);
    FloatBuffer specBuffer = Util.floatToBuffer(materialSpec);
    private void setMaterialToGreen(GL10 gl){
        //材料对环境光的反射情况
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, amdBuffer);
        //散射光的反射情况
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, diffBuffer);
        //镜面光的反射情况
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, specBuffer);
    }
    private void setMaterial(GL10 gl, Model model){
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, model.getAmbBuffer());
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, model.getDiffBuffer());
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, model.getSpecBuffer());
    }
}
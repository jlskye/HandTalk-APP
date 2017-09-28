package animation;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

public class ModelManager {

	/*private static String[] modelFiles = { "hair", "head", "clothes", "trousers", "shoes", "eyes", "leftUpperArm",
			"leftLowerArm", "leftWrist", "leftHand", "leftThumbBottom", "leftThumbTop", "leftForefingerBottom",
			"leftForefingerMiddle", "leftForefingerTop", "leftMiddlefingerBottom", "leftMiddlefingerMiddle",
			"leftMiddlefingerTop", "leftRingfingerBottom", "leftRingfingerMiddle", "leftRingfingerTop",
			"leftLittlefingerBottom", "leftLittlefingerMiddle", "leftLittlefingerTop", "rightUpperArm", "rightLowerArm",
			"rightWrist", "rightHand", "rightThumbBottom", "rightThumbTop", "rightForefingerBottom",
			"rightForefingerMiddle", "rightForefingerTop", "rightMiddlefingerBottom", "rightMiddlefingerMiddle",
			"rightMiddlefingerTop", "rightRingfingerBottom", "rightRingfingerMiddle", "rightRingfingerTop",
			"rightLittefingerBottom", "rightLittlefingerMiddle", "rightLittlefingerTop" };*/

    private static String[] modelFiles = { "body", "clothes", "eyebrows", "eyes", "hair", "head",
            "leftForefingerBottom", "leftForefingerMiddle", "leftForefingerTop", "leftHand", "leftLittlefingerBottom",
            "leftLittlefingerMiddle", "leftLittlefingerTop", "leftLowerArm", "leftMiddlefingerBottom",
            "leftMiddlefingerMiddle", "leftMiddlefingerTop", "leftRingfingerBottom", "leftRingfingerMiddle",
            "leftRingfingerTop", "leftThumbBottom", "leftThumbTop", "leftUpperArm", "rightForefingerBottom",
            "rightForefingerMiddle", "rightForefingerTop", "rightHand", "rightLittefingerBottom",
            "rightLittlefingerMiddle", "rightLittlefingerTop", "rightLowerArm", "rightMiddlefingerBottom",
            "rightMiddlefingerMiddle", "rightMiddlefingerTop", "rightRingfingerBottom", "rightRingfingerMiddle",
            "rightRingfingerTop", "rightThumbBottom", "rightThumbTop", "rightUpperArm", "shoes", "skirt", "socks", };

    private static ArrayList<Model> models = null;

    private static boolean modelReaded = false;

    /**
     * 读取整个模型的所有部件，读进这个类里面的models里面
     *
     * @throws IOException
     */
    public static void readModels(Context context) throws IOException {
        if(modelReaded) return;//如果已经读过了，就不用再读了

        if (models == null)
            models = new ArrayList<Model>();
        STLReader reader = new STLReader();
        for (String fileName : modelFiles) {
            fileName = "testmodel/" + fileName + ".mstl";
            Model m = reader.parserBinStlInAssets(context, fileName);
            models.add(m);
        }
        modelReaded = true;
    }

    public static ArrayList<Model> getModels() {
        return models;
    }
}

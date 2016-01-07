package cn.yesway.cimg.util;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;

public class ImageUtils {
	/** * ImageMagick的路径 */
	public static String imageMagickPath = PropertiesHolderUtils.getPropertity("imageMagickPath");

	public static void zoomImage(Integer width, Integer height, String srcPath, String newPath) throws Exception {
		IMOperation op = new IMOperation();
		op.addImage(srcPath);
		if (width == null) {// 根据高度缩放图片
			op.resize(null, height);
		} else if (height == null) {// 根据宽度缩放图片
			op.resize(width, null);
		} else {
			op.resize(width, height);
		}
		op.addImage(newPath);
		// 这里不加参数或者参数为false是使用ImageMagick，true是使用GraphicsMagick
		ConvertCmd convert = new ConvertCmd();
		convert.setSearchPath(imageMagickPath);
		convert.run(op);
	}
}
package cn.yesway.cimg.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.yesway.cimg.util.ImageUtils;
import cn.yesway.cimg.util.Md5;
import cn.yesway.cimg.util.PropertiesHolderUtils;

@Controller
@RequestMapping("/j/**")
public class JImageController {
	private static final Logger log = LoggerFactory.getLogger(JImageController.class);

	/**
	 * 获取url中的图片进行处理
	 * 测试路径：http://localhost:8080/cimg/j/a.hiphotos.baidu.com/zhidao/pic/item/4034970a304e251fddcebf70a686c9177f3e5378.jpg.200x500.png
	 * 原图片本地保存路径为：imageRoot/a.hiphotos.baidu.com/zhidao/pic/item/4034970a304e251fddcebf70a686c9177f3e5378.jpg
	 * 放缩后的图片本地保存路径为：imageRoot/a.hiphotos.baidu.com/zhidao/pic/item/MD5(4034970a304e251fddcebf70a686c9177f3e5378.jpg)/200x500.png
	 * @createDate 2015年11月24日 下午3:54:19
	 * @param request
	 * @param response
	 */
	@RequestMapping(method = RequestMethod.GET)
	public void handleImage(HttpServletRequest request, HttpServletResponse response) {
		String pathInfo = request.getRequestURI();
		String contextPath = request.getContextPath();
		pathInfo = pathInfo.replaceAll(contextPath, "");
		if (pathInfo != null && pathInfo.length() > 0) {
			OutputStream out = null;
			// 解析pathInfo,获取imgUrl和width height
			pathInfo = pathInfo.substring(3);
			String[] arr = pathInfo.split("\\.");
			if (arr.length >= 3) {
				String imageRoot = PropertiesHolderUtils.getPropertity("imageRoot");
				String format = arr[arr.length - 1];
				int width = 0;
				int height = 0;
				String widthheight = arr[arr.length - 2];
				String originalPath = pathInfo.substring(0, pathInfo.lastIndexOf(widthheight) - 1);// 图片原始路径
				String savePath = imageRoot + "/" + originalPath.substring(0, originalPath.lastIndexOf("/"));// 本地保存路径
				String originalImageName = originalPath.substring(originalPath.lastIndexOf("/") + 1);// 原始文件名
				String fileName = widthheight + pathInfo.substring(pathInfo.lastIndexOf("."));// 放缩后保存文件名
				// 计算md5
				Md5 md5 = new Md5();
				String md5path = md5.digest(originalImageName);
				try {
					out = response.getOutputStream();
					// 如果本地已经存在，直接访问本地
					if (new File(savePath + "/" + md5path + "/" + fileName).isFile()) {
						BufferedImage image = ImageIO.read(new File(savePath + "/" + md5path + "/" + fileName));
						ImageIO.write(image, format, out);
						return;
					}
					String[] arr1 = widthheight.split("x");
					if (arr1.length == 2) {
						width = Integer.parseInt(arr1[0]);
						height = Integer.parseInt(arr1[1]);
						String imageUrl = "http://" + originalPath;
						// 如果本地无原图片文件，下载图片
						if (!new File(savePath + "/" + originalImageName).isFile()) {
							download(imageUrl, originalImageName, savePath);
						}
						// 创建生成图片保存目录
						File file = new File(savePath + "/" + md5path + "/");
						if (!file.isDirectory())
							file.mkdir();
						ImageUtils.zoomImage(width, height, savePath + "/" + originalImageName, savePath + "/" + md5path + "/" + fileName);
						BufferedImage image = ImageIO.read(new File(savePath + "/" + md5path + "/" + fileName));
						ImageIO.write(image, format, out);
					} else {
						response.setStatus(404);
					}
				} catch (Exception e) {
					response.setStatus(404);
					log.error("error:", e);
				}
			} else {
				response.setStatus(404);
			}
		} else {
			response.setStatus(404);
		}
	}
	/**
	 * 下载图片文件
	 * @createDate 2015年11月24日 下午4:11:18
	 * @param strUrl
	 * @param fileName
	 * @param savePath
	 * @throws Exception
	 */
	public static void download(String strUrl, String fileName, String savePath) throws Exception {
		URL url = new URL(strUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		InputStream inStream = conn.getInputStream();

		File sf = new File(savePath);
		if (!sf.exists()) {
			sf.mkdirs();
		}
		OutputStream outStream = new FileOutputStream(sf.getPath() + "\\" + fileName);

		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		outStream.close();
	}
}

package cn.yesway.cimg.controller;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.yesway.cimg.util.ImageScale;

@Controller
@RequestMapping("/i/**")
public class ImageController {
	private static final Logger log = LoggerFactory
			.getLogger(ImageController.class);

	// 获取url中的图片进行处理
	// 小图：http://220.181.190.212:8080/cimg/i/shopimg.kongfz.com.cn/20111221/1449467/12773vMwdv0_b.jpg.520x393.jpg
	// 原图：http://shopimg.kongfz.com.cn/20111221/1449467/12773vMwdv0_b.jpg
	@RequestMapping(method = RequestMethod.GET)
	public void handleImage(HttpServletRequest req, HttpServletResponse res) {
		String pathInfo = req.getPathInfo();
		if (pathInfo != null && pathInfo.length() > 0
				&& pathInfo.startsWith("/i/")) {
			log.debug("start - " + pathInfo);
			// 解析pathInfo,获取imgUrl和width height
			pathInfo = pathInfo.substring(3);
			String[] arr = pathInfo.split("\\.");
			if (arr.length >= 3) {
				String format = arr[arr.length - 1];
				int width = 0;
				int height = 0;
				String widthheight = arr[arr.length - 2];
				String[] arr1 = widthheight.split("x");
				if (arr1.length == 2) {
					try {
						width = Integer.parseInt(arr1[0]);
						height = Integer.parseInt(arr1[1]);
						String imageUrl = "http://"
								+ pathInfo.substring(0,
										pathInfo.lastIndexOf(widthheight) - 1);
						log.debug("img start- " + imageUrl);
						BufferedImage srcImage = ImageIO
								.read(new URL(imageUrl));
						log.debug("img end- " + pathInfo);
						float srcHeight = srcImage.getHeight();
						float srcWidth = srcImage.getWidth();

						float newHeight = srcHeight;
						float newWidth = srcWidth;

						// 图片不进行放大
						if (width > srcWidth) {
							width = (int) srcWidth;
						}
						if (height > srcHeight) {
							height = (int) srcHeight;
						}

						// 等比例缩放
						newWidth = width;
						newHeight = newWidth * (srcHeight / srcWidth);
						if (newHeight > height) {
							newHeight = height;
							newWidth = newHeight * (srcWidth / srcHeight);
						}

						ImageScale imageScale = new ImageScale();
						BufferedImage newImage = imageScale.imageZoomOut(
								srcImage, (int) newWidth, (int) newHeight);
						log.debug("scale end - " + pathInfo);
						ImageIO.write(newImage, format, res.getOutputStream());
					} catch (Exception e) {
						res.setStatus(404);
					}
				} else {
					res.setStatus(404);
				}
			} else {
				res.setStatus(404);
			}
		} else {
			res.setStatus(404);
		}
		log.debug("end");
	}
}

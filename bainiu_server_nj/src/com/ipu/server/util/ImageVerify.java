package com.ipu.server.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

/**
 * @author huangbo
 * 图片验证码类库
 */
public class ImageVerify {
	private static Random random = new Random();

	/**得到图片背景色*/
	private static Color getBack() {
		return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
	}

	/**生成颜色的反色*/
	private static Color getFront(Color c) {
		return new Color(255 - c.getRed(), 255 - c.getGreen(),
				255 - c.getBlue());
	}
	
	/**
	 * 图片转化成Base64编码
	 * @param filePath 文件过大可能内存溢出
	 */
	public static String getImageBase64(BufferedImage image) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", baos);
		byte[] jpegData = baos.toByteArray();
		return new String(Base64.encodeBase64(jpegData), "utf-8");
	}
	
	/**
	 * 生成校验图片
	 */
	public static BufferedImage getImageVerify(String code) {
		int width = 100;
		int height = 45; // 验证图片的宽度，高度
		Color back = getBack();
		Color front = getFront(back);
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		Graphics2D g = bi.createGraphics(); // 得到画布
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20)); // 设置字体
		g.setColor(back);
		g.fillRect(0, 0, width, height); // 画背景
		g.setColor(front);
		g.drawString(code, 18, 20); // 画字符
		for (int i = 0, n = random.nextInt(20); i < n; i++) {
			g.fillRect(random.nextInt(width), random.nextInt(height), 1, 1);
		} // 产生至多20个噪点
		return bi;
	}

	/**
	 * 作用：获取六位随机码
	 */
	public static String getVerifyCode(int length, int verifyCodeType) throws Exception {
		String num = "0123456789";
		String ch = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ";
		String vc = "";

		if (verifyCodeType == 0)
			vc = num;
		else if (verifyCodeType == 1)
			vc = ch;
		else
			vc = num + ch;

		char[] chs = vc.toCharArray();
		String code = "";
		for (int i = 0; i < length; i++) {
			code += chs[(int) (Math.random() * vc.length())];
		}
		
		return code;
	}
}

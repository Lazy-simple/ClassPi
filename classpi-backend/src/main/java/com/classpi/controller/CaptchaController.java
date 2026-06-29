package com.classpi.controller;

import com.classpi.common.Result;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/captcha")
@CrossOrigin
public class CaptchaController {

    // 存储验证码结果（key: 随机ID, value: 验证码答案）
    private static final Map<String, String> captchaStore = new HashMap<>();
    private static final Random random = new Random();

    @GetMapping
    public Result getCaptcha() {
        try {
            // 生成随机算术表达式
            int num1 = random.nextInt(10) + 1;
            int num2 = random.nextInt(10) + 1;
            int operator = random.nextInt(2); // 0: 加法, 1: 乘法

            String expression;
            int answer;

            if (operator == 0) {
                expression = num1 + " + " + num2 + " = ?";
                answer = num1 + num2;
            } else {
                expression = num1 + " × " + num2 + " = ?";
                answer = num1 * num2;
            }

            // 生成唯一ID作为验证码的key
            String captchaId = String.valueOf(System.currentTimeMillis() + random.nextInt(1000));
            captchaStore.put(captchaId, String.valueOf(answer));

            // 生成图片
            BufferedImage image = generateCaptchaImage(expression);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            // 返回base64编码的图片和验证码ID
            String base64Image = "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(imageBytes);

            Map<String, String> result = new HashMap<>();
            result.put("captchaId", captchaId);
            result.put("image", base64Image);

            return Result.success("获取验证码成功", result);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("生成验证码失败");
        }
    }

    @PostMapping("/verify")
    public Result verifyCaptcha(@RequestParam String captchaId, @RequestParam String answer) {
        String correctAnswer = captchaStore.get(captchaId);
        if (correctAnswer == null) {
            return Result.error("验证码已过期");
        }
        if (correctAnswer.equals(answer)) {
            captchaStore.remove(captchaId); // 验证成功后删除
            return Result.success("验证码验证成功", true);
        }
        return Result.error("验证码错误");
    }

    private BufferedImage generateCaptchaImage(String text) {
        int width = 150;
        int height = 50;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 设置干扰线
        g.setColor(new Color(200, 200, 200));
        for (int i = 0; i < 5; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }

        // 设置验证码文字
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(new Color(50, 50, 50));

        // 文字居中
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (width - textWidth) / 2;
        int y = (height + fm.getAscent()) / 2 - 5;

        g.drawString(text, x, y);

        // 添加一些噪点
        g.setColor(new Color(180, 180, 180));
        for (int i = 0; i < 30; i++) {
            int px = random.nextInt(width);
            int py = random.nextInt(height);
            image.setRGB(px, py, Color.GRAY.getRGB());
        }

        g.dispose();
        return image;
    }
}

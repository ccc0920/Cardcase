package com.cardcase.server.service;

import com.cardcase.server.repository.User;
import com.cardcase.server.repository.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JavaMailSenderImpl mailSender;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public boolean sendEmail(String to, String subject, String text, byte[] imageBytes) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("1972356958@qq.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            helper.addInline("imageId", new ByteArrayDataSource(imageBytes, "image/png"));
        } catch (MessagingException e) {
            return false;
        }
        try {
            mailSender.send(mimeMessage);
        } catch (MailException e) {
            return false;
        }
        return true;
    }

    private static String generateVerificationCode(int length) {
        String characters = "01234567890123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    private boolean isLowContrast(int r, int g, int b) {
        int luminance = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
        return luminance < 64 || luminance > 192;
    }

    @Override
    public byte[] vcodeToImage(String vcode) {
        int width = 240;
        int height = 80;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graph = image.getGraphics();
        graph.setColor(Color.WHITE);
        graph.fillRect(0, 0, width, height);
        Random random = new Random();
        for (int i = 0; i < vcode.length(); i++) {
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            while (isLowContrast(r, g, b)) {
                r = random.nextInt(256);
                g = random.nextInt(256);
                b = random.nextInt(256);
            }
            graph.setColor(new Color(r, g, b));
            graph.setFont(new Font("Arial", Font.BOLD, 36));
            graph.drawString(String.valueOf(vcode.charAt(i)), 30 + i * 30, 50);
        }
        for (int i = 0; i < vcode.length(); i++) {
            graph.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            graph.drawLine(x1, y1, x2, y2);
        }
        graph.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            return null;
        }
        return baos.toByteArray();
    }

    @Override
    public String insertUser(String email, String password) {
        User user = userRepo.findByEmail(email);
        if (user != null && user.getUsertype() != 0) {
            return null;
        } else {
            if (user == null) user = new User();
            user.setEmail(email);
            user.setPwd(passwordEncoder.encode(password));
            user.setUsertype(1);
            userRepo.save(user);
            return "";
        }
    }

    @Override
    public int verifyPassword(String email, String password) {
        User user = userRepo.findByEmail(email);
        if (user != null && user.getUsertype() != 0) {
            if (passwordEncoder.matches(password, user.getPwd())) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 2;
        }
    }

    @Override
    public int getIdByEmail(String email) {
        User user = userRepo.findByEmail(email);
        return user.getId();
    }

    @Override
    public void setPwdByEmail(String email, String password) {
        User user = userRepo.findByEmail(email);
        user.setPwd(passwordEncoder.encode(password));
        userRepo.save(user);
    }

    @Override
    public String getCodeByEmail(String email) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            return user.getVcode();
        } else {
            return null;
        }
    }

    @Override
    public String setCodeByEmail(String email) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            return null;
        } else {
            String verificationCode = generateVerificationCode(6);
            user.setVcode(verificationCode);
            userRepo.save(user);
            return verificationCode;
        }
    }

    @Override
    public int setTypeByEmail(String email) {
        User user = userRepo.findByEmail(email);
        user.setUsertype(1);
        userRepo.save(user);
        return user.getId();
    }
}

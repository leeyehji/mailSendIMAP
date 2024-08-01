package com.example.mailsend.service;

import com.example.config.MailConfig;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = false)
public class MailSendService {
    private final MailConfig mailConfig;
    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, String> redisTemplate;

    private static final long AUTH_CODE_EXPIRATION_HOURS = 1;
    private static final String EMAIL_AUTH_HASH = "email_auth_hash";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);


    /*==================================================================================*/
    @Autowired
    MailSendService(MailConfig mailConfig, JavaMailSender javaMailSender, RedisTemplate<String, String> redisTemplate){
        this.mailConfig = mailConfig;
        this.javaMailSender = javaMailSender;
        this.redisTemplate = redisTemplate;
    }


    public Boolean mailSend(String mailAddress){
        try{
            String code = String.valueOf(UUID.randomUUID());

            //1시간 후 만료, 해시맵 형태의 redis. email - code 관계
            HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
            hashOps.put(EMAIL_AUTH_HASH, mailAddress, code);
            redisTemplate.expire(EMAIL_AUTH_HASH, AUTH_CODE_EXPIRATION_HOURS, TimeUnit.HOURS);

            String title ="[웹사이트이름] 이메일 인증 번호 안내";
            String content = mailAuthcode(code);

            return sendEmailForm(mailAddress, title, content);
        }catch(Exception e){
            return false;
        }
    }
    // 이메일 인증 코드 content
    // 이메일은 javascript 지원을 하지 않음. css를 header에 두면 인식하지 않음.
    private String mailAuthcode(String code) {
        try {
            return "<html>" +
                    "<head>" +
                    "<meta charset=\"UTF-8\">" +
                    "<title>이메일 인증</title>" +
                    "</head>" +
                    "<body style=\"font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4;\">" +
                    "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 20px auto; padding: 0; width: 100%; max-width: 600px; background-color: #ffffff; border: 2px solid #009FBD; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);\">" +
                    "<tr><td style=\"background-color: #009FBD; height: 10px;\">&nbsp;</td></tr>" +
                    "<tr><td style=\"padding: 20px; text-align: center;\">" +
                    "<h1 style=\"color: #009FBD; margin: 0;\">이메일 인증</h1>" +
                    "</td></tr>" +
                    "<tr><td style=\"padding: 20px; text-align: center;\">" +
                    "<p style=\"color: #333333; margin: 0;\">아래의 인증 코드를 복사하여 사용하세요:</p>" +
                    "<div style=\"font-size: 1.2em; font-weight: bold; margin: 20px 0; padding: 15px; border: 1px solid #cccccc; border-radius: 4px; background-color: #e8f4f8; display: inline-block;\">" + code + "</div>" +
                    "</td></tr>" +
                    "<tr><td style=\"padding: 20px; text-align: center;\">" +
                    "<p style=\"color: #777777; font-size: 0.9em; margin: 0;\">텍스트 상자를 클릭하여 선택한 후, Ctrl+C를 눌러 복사하세요.</p>" +
                    "</td></tr>" +
                    "<tr><td style=\"background-color: #f4f4f4; padding: 20px; text-align: center; color: #999999; font-size: 0.8em;\">" +
                    "<p>감사합니다,<br>[웹사이트 이름]</p>" +
                    "</td></tr>" +
                    "</table>" +
                    "</body>" +
                    "</html>";
        } catch (Exception e) {
            return "false";
        }
    }

    //유효한 주소인지 확인
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty() || !EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            return false;
        }
        return true;
    }

    //이메일 전송 형식.
    //버튼을 누르면 복사도록 하고 전체적으로 메일을 깔끔하게 꾸밀 것.
    private Boolean sendEmailForm(String email, String title, String content) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("이메일 주소 형식이 올바르지 않습니다.");
        } else if (content.equals("false")) {
            throw new IllegalArgumentException("이메일 내용이 올바르지 않습니다.");
        }

        String emailFrom = mailConfig.getEmailAddress();

        //이메일 내용 작성
        MimeMessage message = javaMailSender.createMimeMessage();
        try{
            //이메일 내용 작성: MessagingException
//            message.addRecipients(Message.RecipientType.TO, email);
//            message.setSubject(title);
//            message.setText(content);
//            message.setFrom(new InternetAddress(emailFrom));

            // html 적용을 위함.
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject(title);
            helper.setText(content, true); // true를 설정하여 HTML 콘텐츠로 처리
            helper.setFrom(new InternetAddress(emailFrom));

            //이메일 전송: MailException
            javaMailSender.send(message);
        }catch(MailException | MessagingException mailException){
            throw new IllegalArgumentException("이메일 내용 작성 중, 혹은 이메일 전송 중 문제가 발생했습니다.");
        }
        return true;
    }

    public Boolean mailcheck(@Nonnull String mailAdress, @Nonnull String authcode) {
        // 인증 코드 DB에서 꺼내기
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        @Nullable String authDB = hashOps.get(EMAIL_AUTH_HASH, mailAdress); // HGET email_auth_hash "lamp0525@naver.com"

        // authDB가 null인지 확인한 후 유저가 입력한 인증 코드와 비교하여 T/F
        return authDB != null && authDB.equals(authcode);
    }
}

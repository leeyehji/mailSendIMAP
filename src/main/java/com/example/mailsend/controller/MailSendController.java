package com.example.mailsend.controller;

import com.example.mailsend.service.MailSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class MailSendController {
    private final MailSendService mailSendService;


    /*==================================================================================*/
    @Autowired
    MailSendController(MailSendService mailSendService){
        this.mailSendService = mailSendService;
    }


    //메일 주소로 인증번호를 보내고 이를 Redis 서버에 저장함. 성공 시 3000으로 true를 보냄.
    @PostMapping(path = "mailSend")
    public Boolean mailSend(@RequestBody String mailAddress){
        mailAddress = mailAddress.replaceAll("\"", "");

        return mailSendService.mailSend(mailAddress);
    }

    //해당 메일 주소가 입력받은 인증번호와 일치하는 지 redis 서버에서 확인. 일치 시 3000으로 true, 불일치 시 false 전송.
    @PostMapping(path="mailCheck")
    public Boolean mailCheck(@RequestParam String mailAddress, @RequestParam String authCode){
        mailAddress = mailAddress.replaceAll("\"", "");
        return mailSendService.mailcheck(mailAddress, authCode);
    }
}

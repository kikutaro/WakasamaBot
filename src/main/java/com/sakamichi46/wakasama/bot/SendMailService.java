package com.sakamichi46.wakasama.bot;

import com.sendgrid.ASM;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import java.io.IOException;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author kikuta
 */
@Service
public class SendMailService {
    
    @Value("${mail.sendgrid.key}")
    private String sendGridKey;
    
    @Value("${mail.to}")
    private String mailTo;
    
    @Value("${mail.from}")
    private String mailFrom;
    
    private SendGrid sg;
    
    private Email from;
    
    private Email to;
    
    @PostConstruct
    public void init() {
        sg = new SendGrid(sendGridKey);
        from = new Email(mailFrom);
        to = new Email(mailTo);
    }
    
    public boolean sendMail(String message) {
        Content content = new Content("text/plain", message);
        Mail mail = new Mail(from, "[若様Bot]友だちが追加されました", to, content);
        mail.categories = Arrays.asList("wakasamabot");
        ASM asm = new ASM();
        asm.setGroupId(1983);
        mail.setASM(asm);
        Request req = new Request();
        try {
            req.method = Method.POST;
            req.endpoint = "mail/send";
            req.body = mail.build();
            Response ret = sg.api(req);
            System.out.println("メール送信完了" + ret.statusCode);
        } catch (IOException ex) {
            System.out.println("メール送信失敗");
            return false;
        }
        return true;
    }
}

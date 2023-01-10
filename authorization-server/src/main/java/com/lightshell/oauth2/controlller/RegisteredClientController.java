package com.lightshell.oauth2.controlller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import com.lightshell.oauth2.common.ResponseMessage;
import com.lightshell.oauth2.service.RegisteredClientServiceImpl;

@RestController
@RequestMapping({"/register-client"})
public class RegisteredClientController {

    @Value("${oauth2.register-client.secret-code}")
    private String secret;

    @Autowired
    private RegisteredClientServiceImpl registeredClientService;

    @GetMapping("/hello")
    public @ResponseBody String hello() {
        return "Authorization Register";
    }

    @PostMapping("/register")
    public @ResponseBody ResponseMessage add(@RequestBody Map<String, String> data) {
        String clientId = data.get("clientId");
        String pwd = data.get("pwd");
        String code = data.get("code");
        if (clientId == null || pwd == null || "".equals(clientId) || "".equals(pwd) || !secret.equals(code)) {
            return new ResponseMessage("400", "Bad Request");
        }
        if (registeredClientService.isExists(clientId)) {
            return new ResponseMessage("409", "ClientId已存在");
        }
        try {
            registeredClientService.save(clientId, pwd);
            return new ResponseMessage("200", "success");
        } catch (Exception ex) {
            return new ResponseMessage("500", ex.getMessage());
        }
    }

}

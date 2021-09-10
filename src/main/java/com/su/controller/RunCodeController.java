package com.su.controller;

import com.su.service.ExecuteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RunCodeController {
    private Logger logger = LoggerFactory.getLogger(RunCodeController.class);

    @Autowired
    private ExecuteService executeService;

    private volatile int visitNum=0;

    private static final String defaultSource = "public class Run {\n"
            + "    public static void main(String[] args) {\n"
            + "        \n"
            + "    }\n"
            + "}";

    @RequestMapping(path = {"/"}, method = RequestMethod.GET)
    public String entry(Model model) {
        visitNum++;
        model.addAttribute("lastSource", defaultSource);
        model.addAttribute("visitNum",visitNum);
        return "ide";
    }

    @RequestMapping(path = {"/run"}, method = RequestMethod.POST)
    public String runCode(@RequestParam("source") String source,
                          @RequestParam("systemIn") String systemIn, Model model) {
        String runResult = executeService.execute(source, systemIn);
        // 处理html中换行的问题
        runResult = runResult.replaceAll(System.lineSeparator(), "<br/>");

        model.addAttribute("lastSource", source);
        model.addAttribute("lastSystemIn", systemIn);
        model.addAttribute("runResult", runResult);
        model.addAttribute("visitNum",visitNum);
        return "ide";
    }
}


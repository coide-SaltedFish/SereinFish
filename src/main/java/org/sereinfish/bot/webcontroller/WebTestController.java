package org.sereinfish.bot.webcontroller;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.YuWeb.annotation.WebController;

@WebController
public class WebTestController {


    @Action("test")
    public String test(){
        return "123";
    }
}

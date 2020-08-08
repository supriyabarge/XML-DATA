package com.xml.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller

public class LoginController {


	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView openPage() {
		ModelAndView page = new ModelAndView();
		page.setViewName("login");
		return page;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginPage(@RequestParam(value = "error", required = false) String error, 
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        String errorMessge = null;
        if(error != null) {
            errorMessge = "Username or Password is incorrect !!";
        }
        if(logout != null) {
            errorMessge = "You have been successfully logged out !!";
        }
        model.addAttribute("errorMessge", errorMessge);
        return "login";
    }
	
	 @RequestMapping(value="/logout", method = RequestMethod.GET)
	    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        if (auth != null){    
	            new SecurityContextLogoutHandler().logout(request, response, auth);
	        }
	        return "redirect:/login?logout=true";
	    }
	 
	 @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	    public String dashBoard() { 
	        return "dashboard";
	    }
	 
	 @RequestMapping(value = "/uploadXML", method = RequestMethod.GET)
	    public String upload() { 
	        return "upload";
	    }
	 
	 @RequestMapping(value = "/generateXML", method = RequestMethod.GET)
	    public String generate() { 
	        return "generate";
	    }
	 
}

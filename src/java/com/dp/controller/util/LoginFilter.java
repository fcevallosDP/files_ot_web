/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dp.controller.util;

import com.dp.util.TblUsers;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author rlama
 */
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
         
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String reqURI = req.getRequestURI();
        try{
            
            HttpSession ses = req.getSession();
            TblUsers user = (ses != null) ? (TblUsers) ses.getAttribute("SESVAR_Usuario") : null;
            if ( (ses != null && user != null) ){
                /*if(reqURI.indexOf("/index.xhtml") >=0){
                    res.sendRedirect(req.getContextPath()+"/tktTickets/CrearTicketList.xhtml");
                }*/

                chain.doFilter(request, response);

            }else if(reqURI.contains("javax.faces.resource") || reqURI.indexOf("/login.xhtml") >= 0 || reqURI.indexOf("/public/") >= 0){
                chain.doFilter(request, response);
                
            /*}else if(validarLinkEncuesta(reqURI)){
                chain.doFilter(request, response);*/
            }else{
                res.sendRedirect(req.getContextPath()+"/login.xhtml");
            }
                
                 
        }catch(Exception e){
             res.sendRedirect(req.getContextPath()+"/login.xhtml");
        }
    }

    @Override
    public void destroy() {
         
    }

}

package com.dp.controller.util;

/**
 *
 * @author ZAMBRED
 */
import java.io.Serializable;
import com.dp.util.DAOFile;
import com.dp.util.TblProfiles;
import com.dp.util.TblUsers;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author zambred
 */
@ManagedBean(name = "LoginBean")
@SessionScoped
public class LoginBean implements Serializable {
    public String username;
    public String password;
    public TblUsers LoggedInUser;
    public BigInteger currentView = null;
    public String autorizacion = "";
    public String hostname;
    public Boolean notificatorOpened = false;
    public String notificationTitle;
    public boolean lbIfAdmin = false;
    
    private List<String> notifications = new ArrayList<String>();
    private List<String> rutas = new ArrayList<String>();

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public Boolean setOpenCloseNotificator(){
        return notificatorOpened = !notificatorOpened;
    }
    
    public Boolean getNotificatorOpened() {
        return notificatorOpened;
    }

    public void setNotificatorOpened(Boolean notificatorOpened) {
        this.notificatorOpened = notificatorOpened;
    }

    public List<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
    }

    public boolean isAuthorized(String url)
    {
        for (String rt:rutas){
            if (url.contains(rt)) return true;
        }
        return false;
    }
    
    public List<String> getRutas()
    {
        return rutas;
    }

    public void setRutas(List<String> rutas) {
        this.rutas = rutas;
    }
    
    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public void setCurrentView(BigInteger op)
    {
        currentView=op;
    }
    public BigInteger getCurrentView()
    {
        return currentView;
    }
    public void setLoggedInUser(TblUsers User)
    {
        LoggedInUser=User;
    }

    public TblUsers getLoggedInUser()
    {
        return LoggedInUser;
    }

    public void setUsername(String Username)
    {
        username=Username;//.toLowerCase();
    }
    public String getUsername()
    {
        return username;
    }

    public boolean isLbIfAdmin() {
        return lbIfAdmin;
    }

    public void setLbIfAdmin(boolean lbIfAdmin) {
        this.lbIfAdmin = lbIfAdmin;
    }
    
    public void setPassword(String Password)
    {
        password=Password;
    }
    public String getPassword()
    {
        return password;
    }
    public LoginBean() {
    }
    
    public void resetPassword()
    {
        try{
            DAOFile dbCon = new DAOFile();       
            LoggedInUser = dbCon.getItemUserById(LoggedInUser.getIdUser());
            if (LoggedInUser != null){
                LoggedInUser.setvPassword(JsfUtil.generateHash(password));
                dbCon.setUpdateUser(LoggedInUser);
                this.setPassword("");
                JsfUtil.addSuccessMessage("Password changed successfully!");                    
            }
        }catch (Exception e) {        
            e.printStackTrace();
            JsfUtil.addErrorMessage("Something went wrong");
        }
    }
    public String login()
    {
        try
        {
           DAOFile dbCon = new DAOFile(); 
           this.setLoggedInUser(dbCon.getItemUserByUserAndPass(username,JsfUtil.generateHash(password)));

           if(this.LoggedInUser==null)
           {
               this.setPassword("");
               JsfUtil.addErrorMessage("Login failed!");          
               
           }else
           {
               if (this.LoggedInUser.getiStatus() > 0) 
               {
                lbIfAdmin = false;   
                TblProfiles perfil = this.LoggedInUser.getIdProfile();
                HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                request.getSession().setAttribute("SESVAR_Usuario", this.LoggedInUser);                
                ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
                if(perfil != null){
                    if (perfil.getVDescription().toUpperCase().contains("ADMIN")){
                        lbIfAdmin = true;                        
                    }
                    //externalContext.redirect(externalContext.getRequestContextPath() + "/tblYtvideos/List.xhtml");
                    externalContext.redirect(externalContext.getRequestContextPath() + "/index.xhtml");
                    return "index";
                }         
               }else{
                    this.setLoggedInUser(null);
                    this.setPassword("");
                   JsfUtil.addErrorMessage("The user is inactive");                   
               }
               return "login";               
           }
        } catch (Exception e)
        {
            JsfUtil.addErrorMessage("Something went wrong!");
            this.setPassword(""); 
            return "login";
        }finally{            
        }
        return "login";
    }
    
    public void logout() throws IOException {
        HttpSession session = JsfUtil.getSession();
        if(session != null){
            session.invalidate();
            this.setLoggedInUser(null);
            this.setPassword("");        
        }
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect(externalContext.getRequestContextPath() + "/");
   }
    
    public void keepUserSessionAlive() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.getSession();
    }
}

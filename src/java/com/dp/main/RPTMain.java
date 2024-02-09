/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dp.main;
import com.dp.util.DAOFile;
import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author ZAMBRED
 */
public class RPTMain {
    protected DAOFile dbCon = new DAOFile();    
    /**
     * @param args the command line arguments
     */    
        
    public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException{
        RPTMain programm = new RPTMain();
        String lsRootPath_O = "C:\\RPTFiles\\Inbox\\";
        String lsRootPath_D = "C:\\RPTFiles\\Done\\";
        programm.start(lsRootPath_O, lsRootPath_D);                 
        /*if (args.length >= 0){
            lsFileName = args[0];// en esta sección debemos realizar validaciones al archivo
            lsFileName = "C:\\Users\\ZAMBRED\\Downloads\\DV360_Spend_Pacing_Daily_20240123_062444_925471573_4444983233.xlsx";            
        }else{
            System.out.println("No params got");        
        }*/                                        
    }
    
    public void start(String lsRootPath_O, String lsRootPath_D) throws IOException, ClassNotFoundException {
        System.out.println("Process Start!");
        dbCon.ScanFiles(lsRootPath_O, lsRootPath_D);         
        System.out.println("Process End!");
    }    
}

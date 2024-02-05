package com.dp.util;

import com.dp.facade.TblCatalogFacade;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static jdk.nashorn.internal.objects.NativeMath.round;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ZAMBRED
 */
//@ManagedBean
//@ViewScoped

public class DAOFile implements Serializable  {
    public Connection connect = null;
    private String url = "jdbc:mysql://localhost:3306/rptdata?serverTimezone=UTC";
    private String username = "scraper";
    private String password = "qernel24";
    private List<TblDV360SPD> itemsDV360 = null;
    private TblCatalogFacade jpaCatalog;
    
    /**
     * 
     * Get a connection to database and Scrap files
     * 
     */
    
    public DAOFile() {
        jpaCatalog = new TblCatalogFacade();
    }        
    
    protected void getConnection() throws ClassNotFoundException
    {
      try {
          connect = (Connection) DriverManager.getConnection(url, username, password);
      } catch (SQLException ex) {
          ex.printStackTrace();
          throw new RuntimeException("Error connecting to the database", ex);
      }
    }        
        
    protected void closeConnection()
    {
      try {
          if (connect != null)
            connect.close(); 
      } catch (SQLException ex) {
          ex.printStackTrace();
          throw new RuntimeException("Error closing connection to database", ex);
      }
    }      
      
    protected void scrap_DV360_Format(String lsFileName) throws FileNotFoundException, IOException{
        System.out.println("scrap_DV360_Format: "+lsFileName);
        itemsDV360 = null;
        if (!lsFileName.isEmpty()){
            
            String filePath= lsFileName;
            File file = new File(filePath);
            filePath= file.getAbsolutePath(); 
            File xlFile = new File(filePath);            

            if (xlFile.exists() && !xlFile.isDirectory() && xlFile.toString().endsWith(".xlsx")){                               
                
                InputStream is = new FileInputStream(xlFile);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                //Get first sheet from the workbook
                XSSFSheet sheet = workbook.getSheetAt(0);                                
                Sheet firstSheet = workbook.getSheetAt(0);  
                Iterator<Row> rowIterator = firstSheet.iterator();
                 // skip the header row
                 if (rowIterator.hasNext()) {
                     rowIterator.next(); // 1
                 }       
                 Boolean lbEndFile = false, lbEndCol = false;
                 int iColBlank;
                 TblDV360SPD item = null;
                 itemsDV360 =  new ArrayList();
                 while (rowIterator.hasNext() && !lbEndFile) {
                    // aqui empiezo a iterar filas
                    Row nextRow = rowIterator.next();
                    Iterator<Cell> cellIterator = nextRow.cellIterator(); 
                    lbEndCol = false;
                    iColBlank = 0;   
                    item = new TblDV360SPD();
                    item.setdMediaCosts(0.00);
                    item.setiImpressions(0);
                    item.setdTotalMediaCosts(0.00);
                    while (cellIterator.hasNext() && !lbEndCol) {
                        // aqui empiezo a iterar las columnas
                        Cell nextCell = cellIterator.next();         

                        int columnIndex = nextCell.getColumnIndex();     

                        if(nextCell.getCellType() == CellType.BLANK){
                            iColBlank++;
                        }                                                
                        switch (columnIndex) {                     
                            case 1://Date
                                try{
                                    if (nextCell.getCellType() == CellType.STRING){                                        
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvDate(nextCell.getStringCellValue());

                                            String string = item.getvDate();
                                            String[] parts = string.split("/");
                                            if (parts.length > 0){
                                                item.setiAnio(Integer.valueOf(parts[0]));
                                                item.setiMes(Integer.valueOf(parts[1]));
                                                item.setiDia(Integer.valueOf(parts[2]));                                            
                                            }                                          
                                        }else{
                                            iColBlank++;    
                                        }
                                    }
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }
                                break;                            
                            case 2://Partner
                                try{
                                    if(nextCell.getCellType() == CellType.STRING){
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvPartner(nextCell.getStringCellValue());                                        
                                        }else{
                                            iColBlank++;    
                                        }                                                                                  
                                    }                                                                 
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }
                                break;
                            case 3://CAMPAIGN
                                try{                                    
                                    if (nextCell.getCellType() == CellType.STRING){
                                        
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvCampaign(nextCell.getStringCellValue());
                                        }else{
                                            iColBlank++;    
                                        }                                              
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }                                
                                break;
                            case 4://Insertion Order
                                try{                                    
                                    if (nextCell.getCellType() == CellType.STRING){
                                        
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvInsertionOrder(nextCell.getStringCellValue());
                                        }else{
                                            iColBlank++;    
                                        }                                              
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }                                
                                break;
                            case 5://Line Item
                                try{
                                    if (nextCell.getCellType() == CellType.STRING){

                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvLineItem(nextCell.getStringCellValue());
                                        }else{
                                            iColBlank++;    
                                        }                                              
                                        
                                    }
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }
                                break;                            
                            case 6://Exchange
                                try{
                                    if (nextCell.getCellType() == CellType.STRING){
                                        
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvExchange(nextCell.getStringCellValue());
                                        }else{
                                            iColBlank++;    
                                        }                                              
                                        
                                    }
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }
                                break;  
                            case 7://Inventory Source
                                try{
                                    if (nextCell.getCellType() == CellType.STRING){
                                        
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvDealName(nextCell.getStringCellValue());
                                        }else{
                                            iColBlank++;    
                                        }                                                                                      
                                    }
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }
                                break;                                  
                            case 8://Impressions
                                try{                                    
                                    if(nextCell.getCellType() == CellType.NUMERIC){                                        
                                        item.setiImpressions((int) nextCell.getNumericCellValue());
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }                                
                                break;                
                            case 9://Clicks
                                try{                                    
                                    if(nextCell.getCellType() == CellType.NUMERIC){                                        
                                        item.setiClicks((int) nextCell.getNumericCellValue());
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }                                
                                break;                
                            case 10://Media Costs
                                try{                                    
                                    if(nextCell.getCellType() == CellType.NUMERIC){                                        
                                        item.setdMediaCosts(nextCell.getNumericCellValue());
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }                                
                                break;                
                            case 11://Total Media Costs
                                try{                                    
                                    if(nextCell.getCellType() == CellType.NUMERIC){                                        
                                        item.setdTotalMediaCosts(nextCell.getNumericCellValue());
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }              
                                lbEndCol = true;  
                                break;  
                        }// END SWITCH                                        
                    }//END Col
                    if(iColBlank > 3){
                        item = null;
                        lbEndFile = true;                                                   
                    }else{
                        try {
                            item.setvDSP((item.getvPartner() !=null && !item.getvPartner().isEmpty()) ? jpaCatalog.getValueByTypePattern("DSP", item.getvPartner()):"");
                            item.setvClient((item.getvCampaign() !=null && !item.getvCampaign().isEmpty()) ? jpaCatalog.getValueByTypePattern("CLIENT", item.getvCampaign()):"");
                            item.setvAgency((item.getvClient() !=null && !item.getvClient().isEmpty()) ? jpaCatalog.getValueByTypePattern("AGENCY", item.getvClient()): "");
                            item.setvChannel((item.getvLineItem() !=null && !item.getvLineItem().isEmpty()) ? jpaCatalog.getValueByTypePattern("CHANNEL", item.getvLineItem()) : "");
                            item.setvVendor((item.getvDealName() !=null && !item.getvDealName().isEmpty()) ? jpaCatalog.getValueByTypePattern("VENDOR", item.getvDealName()): "");
                            item.setvAlias((item.getvDealName() !=null && !item.getvDealName().isEmpty() && item.getvDealName().length() > 2) ? item.getvDealName().substring(0, 2) : "");
                            item.setvVendorSource((item.getvVendor().length() > 2) ? "INTERNAL" : "EXTERNAL");  
                            item.setdCPM((item.getiImpressions() > 0) ? (item.getdMediaCosts() * 1000.00) / item.getiImpressions() : 0.00);
                            item.setdCTR((item.getiImpressions() > 0) ? item.getiClicks() / item.getiImpressions() : 0.000);
                            item.setdCPC((item.getiClicks() > 0) ? item.getdMediaCosts() / item.getiClicks() : 0.00);                          } catch (Exception exe) {
                            System.out.println(exe.getMessage());  
                            exe.printStackTrace();
                        }
                    }                    
                    // Append to list
                    if (item != null){
                        itemsDV360.add(item);                     
                    }        

                 }// END ROWS
                workbook.close();
                is.close();                   
        }
       }
    }

    protected void scrap_PPOINT_Format(String lsFileName) throws FileNotFoundException, IOException{
        System.out.println("scrap_PPOINT_Format: "+lsFileName);
        itemsDV360 = null;
        if (!lsFileName.isEmpty()){
            
            String filePath= lsFileName;
            File file = new File(filePath);
            filePath= file.getAbsolutePath(); 
            File xlFile = new File(filePath);            

            if (xlFile.exists() && !xlFile.isDirectory() && xlFile.toString().endsWith(".xlsx")){                               
                
                InputStream is = new FileInputStream(xlFile);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                //Get first sheet from the workbook
                XSSFSheet sheet = workbook.getSheetAt(0);                                
                Sheet firstSheet = workbook.getSheetAt(0);  
                Iterator<Row> rowIterator = firstSheet.iterator();
                 // skip the header row
                 if (rowIterator.hasNext()) {
                     rowIterator.next(); // 1
                 }       
                 Boolean lbEndFile = false, lbEndCol = false;
                 int iColBlank;
                 TblDV360SPD item = null;
                 itemsDV360 =  new ArrayList();
                 while (rowIterator.hasNext() && !lbEndFile) {
                    // aqui empiezo a iterar filas
                    Row nextRow = rowIterator.next();
                    Iterator<Cell> cellIterator = nextRow.cellIterator(); 
                    lbEndCol = false;
                    iColBlank = 0;   
                    item = new TblDV360SPD();
                    while (cellIterator.hasNext() && !lbEndCol) {
                        // aqui empiezo a iterar las columnas
                        Cell nextCell = cellIterator.next();         

                        int columnIndex = nextCell.getColumnIndex();     

                        if(nextCell.getCellType() == CellType.BLANK){
                            iColBlank++;
                        }                                                
                        switch (columnIndex) {                        
                            case 0://Date
                                try{
                                    if (nextCell.getCellType() == CellType.STRING){                                        
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvDate(nextCell.getStringCellValue());

                                            String string = item.getvDate();
                                            String[] parts = string.split("-");
                                            if (parts.length > 0){
                                                item.setiAnio(Integer.valueOf(parts[0]));
                                                item.setiMes(Integer.valueOf(parts[1]));
                                                item.setiDia(Integer.valueOf(parts[2]));                                            
                                            }                                          
                                        }else{
                                            iColBlank++;    
                                        }
                                    }
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }
                                break;                            
                            case 2://CAMPAIGN
                                try{                                    
                                    if (nextCell.getCellType() == CellType.STRING){
                                        
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvCampaign(nextCell.getStringCellValue());
                                            item.setvPartner("ATAYLOR");
                                        }else{
                                            iColBlank++;    
                                        }                                              
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }                                
                                break;
                            case 3://Insertion Order
                                try{
                                    if (nextCell.getCellType() == CellType.STRING){

                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvInsertionOrder(nextCell.getStringCellValue());
                                        }else{
                                            iColBlank++;    
                                        }                                              
                                        
                                    }
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }
                                break;                            
                            case 4://Line Item
                                try{
                                    if (nextCell.getCellType() == CellType.STRING){
                                        
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvLineItem(nextCell.getStringCellValue());
                                        }else{
                                            iColBlank++;    
                                        }                                              
                                        
                                    }
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }
                                break;  
                            case 5://Exchange
                                try{
                                    if (nextCell.getCellType() == CellType.STRING){
                                        
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvExchange(nextCell.getStringCellValue());
                                        }else{
                                            iColBlank++;    
                                        }                                                                                      
                                    }
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }
                                break;                                  
                            case 6://Deal Name
                                try{
                                    if (nextCell.getCellType() == CellType.STRING){
                                        
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvDealName(nextCell.getStringCellValue());
                                        }else{
                                            iColBlank++;    
                                        }                                                                                      
                                    }
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }
                                break;                               
                            case 7://Impressions
                                try{                                    
                                    if(nextCell.getCellType() == CellType.NUMERIC){                                        
                                        item.setiImpressions((int) nextCell.getNumericCellValue());
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }                                
                                break;                
                            case 8://Clicks
                                try{                                    
                                    if(nextCell.getCellType() == CellType.NUMERIC){                                        
                                        item.setiClicks((int) nextCell.getNumericCellValue());
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }                                
                                break;                
                            case 9://Media Costs
                                try{                                    
                                    if(nextCell.getCellType() == CellType.NUMERIC){                                        
                                        item.setdMediaCosts(nextCell.getNumericCellValue());
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }                                
                                break;                
                            case 10://Total Media Costs
                                try{                                    
                                    if(nextCell.getCellType() == CellType.NUMERIC){                                        
                                        item.setdTotalMediaCosts(nextCell.getNumericCellValue());
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }              
                                lbEndCol = true;  
                                break;  
                        }// END SWITCH                                        
                    }//END Col
                    if(iColBlank > 4){
                        item = null;
                        lbEndFile = true;                                                   
                    }else{
                        try {
                            item.setvDSP((item.getvPartner() !=null && !item.getvPartner().isEmpty()) ? jpaCatalog.getValueByTypePattern("DSP", item.getvPartner()):"");
                            item.setvClient((item.getvCampaign() !=null && !item.getvCampaign().isEmpty()) ? jpaCatalog.getValueByTypePattern("CLIENT", item.getvCampaign()):"");
                            item.setvAgency((item.getvClient() !=null && !item.getvClient().isEmpty()) ? jpaCatalog.getValueByTypePattern("AGENCY", item.getvClient()): "");
                            item.setvChannel((item.getvLineItem() !=null && !item.getvLineItem().isEmpty()) ? jpaCatalog.getValueByTypePattern("CHANNEL", item.getvLineItem()) : "");
                            item.setvVendor((item.getvDealName() !=null && !item.getvDealName().isEmpty()) ? jpaCatalog.getValueByTypePattern("VENDOR", item.getvDealName()): "");
                            item.setvAlias((item.getvDealName() !=null && !item.getvDealName().isEmpty() && item.getvDealName().length() > 2) ? item.getvDealName().substring(0, 2) : "");
                            item.setvVendorSource((item.getvVendor().length() > 2) ? "INTERNAL" : "EXTERNAL");    
                            item.setdCPM((item.getiImpressions() > 0) ? (item.getdMediaCosts() * 1000.00) / item.getiImpressions() : 0.00);
                            item.setdCTR((item.getiImpressions() > 0) ? item.getiClicks() / item.getiImpressions() : 0.000);
                            item.setdCPC((item.getiClicks() > 0) ? item.getdMediaCosts() / item.getiClicks() : 0.00);                          } catch (Exception exe) {
                            System.out.println(exe.getMessage());  
                            exe.printStackTrace();
                        }
                    }                           

                    // Append to list
                    if (item != null){
                        itemsDV360.add(item);                     
                    }        

                 }// END ROWS
                workbook.close();
                is.close();                 
        }
       }
    }
        
    protected void scrap_BASIS_Format(String lsFileName) throws FileNotFoundException, IOException{
        System.out.println("scrap_BASIS_Format: "+lsFileName);
        itemsDV360 = null;
        if (!lsFileName.isEmpty()){
            
            String filePath= lsFileName;
            File file = new File(filePath);
            filePath= file.getAbsolutePath(); 
            File xlFile = new File(filePath);            

            if (xlFile.exists() && !xlFile.isDirectory() && xlFile.toString().endsWith(".xlsx")){                               
                
                InputStream is = new FileInputStream(xlFile);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                //Get first sheet from the workbook
                XSSFSheet sheet = workbook.getSheetAt(0);                                
                Sheet firstSheet = workbook.getSheetAt(0);  
                Iterator<Row> rowIterator = firstSheet.iterator();
                 // skip the header row
                 if (rowIterator.hasNext()) {
                     rowIterator.next(); // 1
                     rowIterator.next(); // 2
                     rowIterator.next(); // 3
                     rowIterator.next(); // 4
                     rowIterator.next(); // 5
                     rowIterator.next(); // 6
                     rowIterator.next(); // 7
                     rowIterator.next(); // 8
                     rowIterator.next(); // 9
                     rowIterator.next(); // 10
                     rowIterator.next(); // 11
                     rowIterator.next(); // 12
                 }       
                 Boolean lbEndFile = false, lbEndCol = false;
                 int iColBlank;
                 TblDV360SPD item = null;
                 itemsDV360 =  new ArrayList();
                 while (rowIterator.hasNext() && !lbEndFile) {
                    // aqui empiezo a iterar filas
                    Row nextRow = rowIterator.next();
                    Iterator<Cell> cellIterator = nextRow.cellIterator(); 
                    lbEndCol = false;
                    iColBlank = 0;   
                    item = new TblDV360SPD();
                    while (cellIterator.hasNext() && !lbEndCol) {
                        // aqui empiezo a iterar las columnas
                        Cell nextCell = cellIterator.next();         

                        int columnIndex = nextCell.getColumnIndex();     

                        if(nextCell.getCellType() == CellType.BLANK){
                            iColBlank++;
                        }                                                
                        switch (columnIndex) {                        
                            case 0://Date
                                try{
                                    if (nextCell.getCellType() == CellType.STRING){                                        
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvDate(nextCell.getStringCellValue());

                                            String string = item.getvDate();
                                            String[] parts = string.split("-");
                                            if (parts.length > 0){
                                                item.setiAnio(Integer.valueOf(parts[0]));
                                                item.setiMes(Integer.valueOf(parts[1]));
                                                item.setiDia(Integer.valueOf(parts[2]));                                            
                                            }                                          
                                        }else{
                                            iColBlank++;    
                                        }
                                    }
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }
                                break;                            
                            case 3://CAMPAIGN
                                try{                                    
                                    if (nextCell.getCellType() == CellType.STRING){
                                        
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvCampaign(nextCell.getStringCellValue());
                                            item.setvPartner("Basis");
                                        }else{
                                            iColBlank++;    
                                        }                                              
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }                                
                                break;
                            case 5://Insertion Order
                                try{
                                    if (nextCell.getCellType() == CellType.STRING){

                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvInsertionOrder(nextCell.getStringCellValue());
                                        }else{
                                            iColBlank++;    
                                        }                                              
                                        
                                    }
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }
                                break;                            
                            case 9://Line Item
                                try{
                                    if (nextCell.getCellType() == CellType.STRING){
                                        
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvLineItem(nextCell.getStringCellValue());
                                        }else{
                                            iColBlank++;    
                                        }                                              
                                        
                                    }
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }
                                break;  
                            case 13://Exchange
                                try{
                                    if (nextCell.getCellType() == CellType.STRING){
                                        
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvExchange(nextCell.getStringCellValue());
                                        }else{
                                            iColBlank++;    
                                        }                                                                                      
                                    }
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }
                                break;                                  
                            case 15://Deal Name
                                try{
                                    if (nextCell.getCellType() == CellType.STRING){
                                        
                                        if (!nextCell.getStringCellValue().isEmpty()){
                                            item.setvDealName(nextCell.getStringCellValue());
                                        }else{
                                            iColBlank++;    
                                        }                                                                                      
                                    }
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }
                                break;                               
                            case 16://Impressions
                                try{                                    
                                    if(nextCell.getCellType() == CellType.NUMERIC){                                        
                                        item.setiImpressions((int) nextCell.getNumericCellValue());
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }                                
                                break;                
                            case 17://Clicks
                                try{                                    
                                    if(nextCell.getCellType() == CellType.NUMERIC){                                        
                                        item.setiClicks((int) nextCell.getNumericCellValue());
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }                                
                                break;                
                            case 31://Media Costs
                                try{                                    
                                    if(nextCell.getCellType() == CellType.NUMERIC){                                        
                                        item.setdMediaCosts(nextCell.getNumericCellValue());
                                        item.setdMediaCosts(item.getdMediaCosts() * 85 / 100);
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }                                
                                break;                
                            case 33://Total Media Costs
                                try{                                    
                                    if(nextCell.getCellType() == CellType.NUMERIC){                                        
                                        item.setdTotalMediaCosts(nextCell.getNumericCellValue());
                                    }                                                                   
                                }catch (IllegalStateException e) {
                                   e.printStackTrace();
                                }catch (Exception ex){
                                   ex.printStackTrace();
                                }              
                                lbEndCol = true;  
                                break;  
                        }// END SWITCH                                           
                    }//END Col
                    if(iColBlank > 3){
                        item = null;
                        lbEndFile = true;                                                   
                    }else{
                        try {
                            item.setvDSP((item.getvPartner() !=null && !item.getvPartner().isEmpty()) ? jpaCatalog.getValueByTypePattern("DSP", item.getvPartner()):"");
                            item.setvClient((item.getvCampaign() !=null && !item.getvCampaign().isEmpty()) ? jpaCatalog.getValueByTypePattern("CLIENT", item.getvCampaign()):"");
                            item.setvAgency((item.getvClient() !=null && !item.getvClient().isEmpty()) ? jpaCatalog.getValueByTypePattern("AGENCY", item.getvClient()): "");
                            item.setvChannel((item.getvLineItem() !=null && !item.getvLineItem().isEmpty()) ? jpaCatalog.getValueByTypePattern("CHANNEL", item.getvLineItem()) : "");
                            item.setvVendor((item.getvDealName() !=null && !item.getvDealName().isEmpty()) ? jpaCatalog.getValueByTypePattern("VENDOR", item.getvDealName()): "");
                            item.setvAlias((item.getvDealName() !=null && !item.getvDealName().isEmpty() && item.getvDealName().length() > 2) ? item.getvDealName().substring(0, 2) : "");
                            item.setvVendorSource((item.getvVendor().length() > 2) ? "INTERNAL" : "EXTERNAL");                                
                            item.setdCPM((item.getiImpressions() > 0) ? (item.getdMediaCosts() * 1000.00) / item.getiImpressions() : 0.00);
                            item.setdCTR((item.getiImpressions() > 0) ? item.getiClicks() / item.getiImpressions() : 0.000);
                            item.setdCPC((item.getiClicks() > 0) ? item.getdMediaCosts() / item.getiClicks() : 0.00);                            
                        } catch (Exception exe) {
                            System.out.println(exe.getMessage());  
                            exe.printStackTrace();
                        }
                    }                                               
                    // Append to list
                    if (item != null){
                        itemsDV360.add(item);                     
                    }        

                 }// END ROWS
                workbook.close();
                is.close();                   
        }
       }
    }    
  
    protected boolean save_Items(String lsFileName){
        System.out.println("saveFile");
        if (itemsDV360 != null && !itemsDV360.isEmpty() && !lsFileName.isEmpty()){
            try { 

                PreparedStatement pstmt = connect.prepareStatement("INSERT into `tbl_raw_data` "
                                        + "(`dDate`,`iDia`,`iMes`,`iAnio`,`vPartner`,`vCampaign`,`vInsertionOrder`,`vLineItem`,`vExchange`,`vDealName`,`iImpressions`,`iClicks`,`dMediaCost`,`dTotalMediaCost`,`dSystemDate`,`vFileName`,`vDSP`,`vClient`,`vAgency`,`vChannel`,`vAlias`,`vVendor` ,`vVendorSource`, `dCPM`, `dCTR`, `dCPC`)"
                                        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,?,?,?,?,?,?,?);");

                for (TblDV360SPD item : itemsDV360) {                                    
                    pstmt.setString(1, item.getvDate());
                    pstmt.setLong(2, item.getiDia());
                    pstmt.setLong(3, item.getiMes());
                    pstmt.setLong(4, item.getiAnio());
                    pstmt.setString(5, item.getvPartner());
                    pstmt.setString(6, item.getvCampaign());
                    pstmt.setString(7, item.getvInsertionOrder());
                    pstmt.setString(8, item.getvLineItem());
                    pstmt.setString(9, item.getvExchange());
                    pstmt.setString(10, item.getvDealName());
                    pstmt.setInt(11, item.getiImpressions());                
                    pstmt.setInt(12, item.getiClicks());                    
                    pstmt.setDouble(13, item.getdMediaCosts());
                    pstmt.setDouble(14, item.getdTotalMediaCosts());
                    pstmt.setString(15, lsFileName.trim());
                    pstmt.setString(16, item.getvDSP());
                    pstmt.setString(17, item.getvClient());
                    pstmt.setString(18, item.getvAgency());
                    pstmt.setString(19, item.getvChannel());
                    pstmt.setString(20, item.getvAlias());
                    pstmt.setString(21, item.getvVendor());
                    pstmt.setString(22, item.getvVendorSource());

                    double num = item.getdCPM();
                    BigDecimal bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                      
                    pstmt.setDouble(23, bd.doubleValue());

                    num = item.getdCTR();
                    bd = new BigDecimal(num).setScale(3, RoundingMode.HALF_UP);                                                              
                    pstmt.setDouble(24, bd.doubleValue());

                    num = item.getdCPC();
                    bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                                                                                  
                    pstmt.setDouble(25, bd.doubleValue());

                    pstmt.executeUpdate();
                }                
                pstmt.close(); 
                System.out.println("items saved: " + String.valueOf(itemsDV360.size()));
                return true;
            } catch (Exception ex) {
            
                System.out.println("in save_Items");
                System.out.println(ex.getMessage());
                ex.printStackTrace();                
            }        
        }
        return false;
    }      
    
    protected Set<String> getFileNames(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
              .filter(file -> !Files.isDirectory(file))
              .map(Path::getFileName)
              .map(Path::toString)
              .collect(Collectors.toSet());
        }
    }        
        
    protected void moveFile(String lsPath, String lsFileName, String lsDestinationPath) throws FileNotFoundException, IOException{        
        try {
            if (!lsFileName.isEmpty() && !lsDestinationPath.isEmpty() && !lsPath.isEmpty()){     
                Path source = Paths.get(lsPath+lsFileName);
                Path dest = Paths.get(lsDestinationPath+lsFileName);
                Files.move(source, dest);
                System.out.println("File moved");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }            
    }
    
    protected boolean clean_RawItems(){        
        System.out.println("clean_RawItems");
        try {                
            PreparedStatement pstmt = connect.prepareStatement("delete from `tbl_raw_data` where Id_raw > 0;");
            pstmt.executeUpdate();
            pstmt = connect.prepareStatement("ALTER TABLE `tbl_raw_data` AUTO_INCREMENT = 1;");
            pstmt.executeUpdate();            
            pstmt.close(); 
            System.out.println("items cleanned successfully");
            return true;            
        } catch (Exception ex) {
            System.out.println("clean_RawItems");
            System.out.println(ex.getMessage());
            ex.printStackTrace();                
        }        
        return false;
    }

    protected boolean copy_RawItems_to_History(){        
        System.out.println("copy_RawItems_to_History");
        try {                
            PreparedStatement pstmt = connect.prepareStatement("insert into `tbl_data_history` (`dDate`, `vPartner`, `vCampaign`, `vInsertionOrder`, `vLineItem`, `vExchange`, `vDealName`, `iImpressions`, `iClicks`, `dMediaCost`, `dTotalMediaCost`, `vDSP`, `vClient`, `vAgency`, `vChannel`, `vAlias`, `vVendor`, `vVendorSource`,	`dCPM`,	`dCTR`,	`dCPC`,	`iAnio`, `iMes`, `iDia`, `dSystemDate`,	`vFileName`, `tEstado`)\n" +
                                                                "select `dDate`, `vPartner`, `vCampaign`, `vInsertionOrder`, `vLineItem`, `vExchange`, `vDealName`, `iImpressions`, `iClicks`, `dMediaCost`, `dTotalMediaCost`, `vDSP`, `vClient`, `vAgency`, `vChannel`, `vAlias`, `vVendor`, `vVendorSource`, `dCPM`, `dCTR`, `dCPC`, `iAnio`, `iMes`, `iDia`, `dSystemDate`, `vFileName`, `tEstado`\n" +
                                                                "From `tbl_raw_data`\n" +
                                                                "where tEstado = 1;");
            pstmt.executeUpdate();
            pstmt.close(); 
            System.out.println("items copied successfully");
            return true;            
        } catch (Exception ex) {
            System.out.println("copy_RawItems_to_History");
            System.out.println(ex.getMessage());
            ex.printStackTrace();                
        }        
        return false;
    }    
    
    public void ScanFiles(String lsPath, String lsPathMov) throws IOException, ClassNotFoundException{
        if (!lsPath.isEmpty()){
            Set<String> lsFileNames = getFileNames(lsPath);
            if (lsFileNames!=null && !lsFileNames.isEmpty()){
                getConnection();                        
                
                if(clean_RawItems()){
                
                    for (String lsFileName : lsFileNames) {// iterate files got
                        if (lsFileName.contains("DV360")){
                            scrap_DV360_Format(lsPath+lsFileName);
                            if (save_Items(lsFileName)) {
                               moveFile(lsPath,lsFileName,lsPathMov) ;
                            }
                        }else if (lsFileName.contains("Basis")){
                            scrap_BASIS_Format(lsPath+lsFileName);
                            if (save_Items(lsFileName)) {
                               moveFile(lsPath,lsFileName,lsPathMov) ;
                            }                                                     
                        }else if (lsFileName.contains("Domain-Detailed")){
                            scrap_PPOINT_Format(lsPath+lsFileName);
                           if (save_Items(lsFileName)){                    
                               moveFile(lsPath,lsFileName,lsPathMov) ;
                           }
                        }//else if (lsFileName.contains("DSP 3")){
                         //else if (lsFileName.contains("DSP 4")){
                         //else if (lsFileName.contains("DSP N")){
                    }
                }else{
                    System.out.println("RawItems could not be moved");                    
                }
                
                closeConnection();
            }
        }                
    }    
}

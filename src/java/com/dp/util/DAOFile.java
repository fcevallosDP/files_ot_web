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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.file.UploadedFile;

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
    private List<TblDVXANDRSPD> itemsXANDR = null;
    private List<TblDailyProcess> itemsDaily = null;
    private List<TblProcessStatus> itemsStatusProcess = null;
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
      
    protected List<TblDV360SPD> scrap_DV360_Format(UploadedFile itemFile, TblDailyProcess idDaily) throws FileNotFoundException, IOException{        
        System.out.println("scrap_DV360_Format");
        List<TblDV360SPD> localitemsDV360 = new ArrayList();
        if (itemFile != null){
            String lsFileName = itemFile.getFileName();            
            if (lsFileName.endsWith(".xlsx")){                                               
                //Get first sheet from the workbook
                try (XSSFWorkbook workbook = new XSSFWorkbook(itemFile.getInputStream())) {
                    //Get first sheet from the workbook
                    Sheet firstSheet = workbook.getSheetAt(0);
                    Iterator<Row> rowIterator = firstSheet.iterator();
                    // skip the header row
                    if (rowIterator.hasNext()) {
                        rowIterator.next(); // 1
                    }  Boolean lbEndFile = false, lbEndCol = false;
                    int iColBlank;
                    TblDV360SPD item;                    
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
                        item.setIdDaily(idDaily);
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
                                item.setvDSP((item.getvPartner() !=null && !item.getvPartner().isEmpty()) ? jpaCatalog.getValueByTypePattern("D","DSP", item.getvPartner()):"");
                                item.setvClient((item.getvCampaign() !=null && !item.getvCampaign().isEmpty()) ? jpaCatalog.getValueByTypePattern("D","CLIENT", item.getvCampaign()):"");
                                item.setvAgency((item.getvClient() !=null && !item.getvClient().isEmpty()) ? jpaCatalog.getValueByTypePattern("D","AGENCY", item.getvClient()): "");
                                item.setvChannel((item.getvLineItem() !=null && !item.getvLineItem().isEmpty()) ? jpaCatalog.getValueByTypePattern("D","CHANNEL", item.getvLineItem()) : "");
                                item.setvVendor((item.getvDealName() !=null && !item.getvDealName().isEmpty()) ? jpaCatalog.getValueByTypePattern("D","VENDOR", item.getvDealName()): "");
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
                            localitemsDV360.add(item);
                        }
                        
                    }// END ROWS
                    workbook.close(); 
                }              
        }
       }
        return localitemsDV360;
    }

    protected List<TblDV360SPD> scrap_PPOINT_Format(UploadedFile itemFile, TblDailyProcess idDaily) throws FileNotFoundException, IOException{
        System.out.println("scrap_PPOINT_Format");
        List<TblDV360SPD> localitemsDV360 = new ArrayList();
        if (itemFile != null){            
            String lsFileName = itemFile.getFileName();
            if (lsFileName.endsWith(".xlsx")){                                               
                //Get first sheet from the workbook
                try (XSSFWorkbook workbook = new XSSFWorkbook(itemFile.getInputStream())) {
                    //Get first sheet from the workbook
                    Sheet firstSheet = workbook.getSheetAt(0);
                    Iterator<Row> rowIterator = firstSheet.iterator();
                    // skip the header row
                    if (rowIterator.hasNext()) {
                        rowIterator.next(); // 1
                    }  Boolean lbEndFile = false, lbEndCol = false;
                    int iColBlank;
                    TblDV360SPD item = null;                  
                    while (rowIterator.hasNext() && !lbEndFile) {
                        // aqui empiezo a iterar filas
                        Row nextRow = rowIterator.next();
                        Iterator<Cell> cellIterator = nextRow.cellIterator();
                        lbEndCol = false;
                        iColBlank = 0;
                        item = new TblDV360SPD();
                        item.setIdDaily(idDaily);
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
                                item.setvDSP((item.getvPartner() !=null && !item.getvPartner().isEmpty()) ? jpaCatalog.getValueByTypePattern("D","DSP", item.getvPartner()):"");
                                item.setvClient((item.getvCampaign() !=null && !item.getvCampaign().isEmpty()) ? jpaCatalog.getValueByTypePattern("D","CLIENT", item.getvCampaign()):"");
                                item.setvAgency((item.getvClient() !=null && !item.getvClient().isEmpty()) ? jpaCatalog.getValueByTypePattern("D","AGENCY", item.getvClient()): "");
                                item.setvChannel((item.getvLineItem() !=null && !item.getvLineItem().isEmpty()) ? jpaCatalog.getValueByTypePattern("D","CHANNEL", item.getvLineItem()) : "");
                                item.setvVendor((item.getvDealName() !=null && !item.getvDealName().isEmpty()) ? jpaCatalog.getValueByTypePattern("D","VENDOR", item.getvDealName()): "");
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
                            localitemsDV360.add(item);
                        }
                        
                    }// END ROWS
                    workbook.close(); 
                }               
        }
       }
        return localitemsDV360;
    }
        
    protected List<TblDV360SPD> scrap_BASIS_Format(UploadedFile itemFile, TblDailyProcess idDaily) throws FileNotFoundException, IOException{
        System.out.println("scrap_BASIS_Format");
        List<TblDV360SPD> localitemsDV360 = new ArrayList();
        if (itemFile != null){            
            String lsFileName = itemFile.getFileName();
            if (lsFileName.endsWith(".xlsx")){                                               
                //Get first sheet from the workbook
                try (XSSFWorkbook workbook = new XSSFWorkbook(itemFile.getInputStream())) {
                    //Get first sheet from the workbook
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
                    }  Boolean lbEndFile = false, lbEndCol = false;
                    int iColBlank;
                    TblDV360SPD item = null;                     
                    while (rowIterator.hasNext() && !lbEndFile) {
                        // aqui empiezo a iterar filas
                        Row nextRow = rowIterator.next();
                        Iterator<Cell> cellIterator = nextRow.cellIterator();
                        lbEndCol = false;
                        iColBlank = 0;
                        item = new TblDV360SPD();
                        item.setIdDaily(idDaily);
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
                                item.setvDSP((item.getvPartner() !=null && !item.getvPartner().isEmpty()) ? jpaCatalog.getValueByTypePattern("D","DSP", item.getvPartner()):"");
                                item.setvClient((item.getvCampaign() !=null && !item.getvCampaign().isEmpty()) ? jpaCatalog.getValueByTypePattern("D","CLIENT", item.getvCampaign()):"");
                                item.setvAgency((item.getvClient() !=null && !item.getvClient().isEmpty()) ? jpaCatalog.getValueByTypePattern("D","AGENCY", item.getvClient()): "");
                                item.setvChannel((item.getvLineItem() !=null && !item.getvLineItem().isEmpty()) ? jpaCatalog.getValueByTypePattern("D","CHANNEL", item.getvLineItem()) : "");
                                item.setvVendor((item.getvDealName() !=null && !item.getvDealName().isEmpty()) ? jpaCatalog.getValueByTypePattern("D","VENDOR", item.getvDealName()): "");
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
                            localitemsDV360.add(item);
                        }
                        
                    }// END ROWS
                    workbook.close(); 
                }                 
        }
       }
        return localitemsDV360;
    }    

    protected List<TblDVXANDRSPD> scrap_SSP_Equative_Format(UploadedFile itemFile, TblDailyProcess idDaily) throws FileNotFoundException, IOException, Exception{
        System.out.println("scrap_SSP_Equative_Format");
        List<TblDVXANDRSPD> localitemsXANDR = new ArrayList();
        if (itemFile != null){            
            String lsFileName = itemFile.getFileName();                   
            if (lsFileName.endsWith(".csv")){                
                //Get first sheet from the workbook
                try (SXSSFWorkbook workbook = convertCsvToXlsx(itemFile)) {
                    //Get first sheet from the workbook
                    Sheet firstSheet = workbook.getSheetAt(0);
                    Iterator<Row> rowIterator = firstSheet.iterator();
                    // skip the header row
                    if (rowIterator.hasNext()) {
                        rowIterator.next(); // 1
                    }  
                    Boolean lbEndFile = false, lbEndCol = false;
                    int iColBlank;
                    TblDVXANDRSPD item = null;                  
                    while (rowIterator.hasNext() && !lbEndFile) {
                        // aqui empiezo a iterar filas
                        Row nextRow = rowIterator.next();
                        Iterator<Cell> cellIterator = nextRow.cellIterator();
                        lbEndCol = false;
                        iColBlank = 0;
                        item = new TblDVXANDRSPD();
                        item.setdMediaCost(0.00);
                        item.setiImpressions(0);
                        item.setdTotalCost(0.00);
                        item.setdCPM(0.00);                        
                        item.setdDspFee(0.00);
                        item.setdGrossMargin(0.00);
                        item.setdNetRevenue(0.00);
                        item.setdGrossRevenue(0.00);
                        item.setdMargin(0.00);
                        item.setdMlFee(0.00);
                        item.setdPlatformFee(0.00);
                        item.setdTechFee(0.00);
                        item.setdSalesRevenue(0.00);
                        item.setvDevice("NA");
                        item.setIdDaily(idDaily);
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
                                                String[] parts = string.split(" ");
                                                if (parts.length > 0){
                                                    String lsDate = parts[0].replace("\"", "");
                                                    item.setvDate(lsDate);
                                                    parts = null;
                                                    parts = lsDate.split("-");
                                                    if (parts.length > 2){
                                                        
                                                        item.setiYear(Integer.valueOf(parts[0]));
                                                        item.setiMonth(Integer.valueOf(parts[1]));
                                                        item.setiDay(Integer.valueOf(parts[2]));
                                                    }                                                    
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
                                case 3://DealName
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){
                                                item.setvDeal(nextCell.getStringCellValue());    
                                                item.setvDeal(item.getvDeal().replace("\"", ""));
                                                item.setvBrand((item.getvDeal()!=null && !item.getvDeal().isEmpty()) ? jpaCatalog.getValueByTypePattern("S","BRAND", item.getvDeal()):"");
                                                item.setvAdvertiser((item.getvBrand() !=null && !item.getvBrand().isEmpty()) ? jpaCatalog.getValueByTypePattern("S","ADVERTISER", item.getvBrand()):"");                                                
                                                item.setvClient(item.getvBrand());
                                                item.setvAgency((item.getvBrand() !=null && !item.getvBrand().isEmpty()) ? jpaCatalog.getValueByTypePattern("S","AGENCY", item.getvBrand()):"");
                                                item.setvDsp((item.getvDeal()!=null && !item.getvDeal().isEmpty()) ? jpaCatalog.getValueByTypePattern("S","DSP", item.getvDeal()):"");
                                                item.setvChannel((item.getvDeal()!=null && !item.getvDeal().isEmpty()) ? jpaCatalog.getValueByTypePattern("S","CHANNEL", item.getvDeal()):"");
                                                item.setvSeat((item.getvDeal()!=null && !item.getvDeal().isEmpty()) ? jpaCatalog.getValueByTypePattern("S","SEAT", item.getvDeal()):"");
                                                item.setvExchange((item.getvSeat()!=null && !item.getvSeat().isEmpty()) ? jpaCatalog.getValueByTypePattern("S","EXCHANGE", item.getvSeat()):"");                                                
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
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){                                                
                                                item.setiImpressions(Integer.valueOf(nextCell.getStringCellValue().replace("\"", "")));
                                            }
                                        }
                                    }catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                    break;
                                case 10://SalesRevenue (SpendUSD)
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){  
                                                item.setdSalesRevenue(Double.valueOf(nextCell.getStringCellValue().replace("\"", "")));
                                                if(item.getdSalesRevenue() != null){
                                                    item.setdTechFee((item.getdSalesRevenue() * 10.00) / 100.00);
                                                    item.setdCPM((item.getiImpressions() > 0) ? (1000.00 * (item.getdSalesRevenue() / item.getiImpressions())) : 0.00);
                                                    item.setdDspFee((item.getdSalesRevenue() * 19.00) / 100.00);
                                                    if ((item.getvSeat()!=null && item.getvSeat().contains("DATAP-ML"))){
                                                        item.setdMlFee((item.getdSalesRevenue() * 10.00) / 100.00);
                                                    }                                                                                                
                                                }     
                                            }
                                        }
                                    }catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                    break;
                                case 11://curationMargin/GrossMargin
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){ 
                                                item.setdGrossMargin(Double.valueOf(nextCell.getStringCellValue().replace("\"", "")));
                                                if(item.getdGrossMargin() != null && item.getdSalesRevenue() != null){
                                                    item.setdMediaCost(item.getdSalesRevenue() - item.getdGrossMargin() - item.getdTechFee());
                                                    if (item.getdSalesRevenue() > 0){
                                                        item.setdMargin(item.getdGrossMargin() / item.getdSalesRevenue());
                                                    }
                                                }
                                                item.setdGrossRevenue(item.getdGrossMargin() - item.getdMlFee());
                                                item.setdTotalCost(item.getdMediaCost() + item.getdTechFee());
                                                item.setdNetRevenue(item.getdSalesRevenue() - item.getdTechFee() - item.getdMediaCost() - item.getdMlFee() - item.getdPlatformFee() - item.getdDspFee());
                                            }                                                        
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
                        }
                        // Append to list
                        if (item != null && item.getiImpressions() > 0 && item.getdSalesRevenue() > 0){
                            localitemsXANDR.add(item);
                        }
                        
                    }// END ROWS
                    workbook.close(); 
                }               
        }
       }
        return localitemsXANDR;
    }
    
    protected List<TblDVXANDRSPD> scrap_SSP_PubMatic_Format(UploadedFile itemFile, TblDailyProcess idDaily) throws FileNotFoundException, IOException, Exception{
        System.out.println("scrap_SSP_PubMatic_Format");
        List<TblDVXANDRSPD> localitemsXANDR = new ArrayList();      
        if (itemFile != null){            
            String lsFileName = itemFile.getFileName();                    
            if (lsFileName.endsWith(".csv")){                
                //Get first sheet from the workbook
                try (SXSSFWorkbook workbook = convertCsvToXlsx(itemFile)) {
                    //Get first sheet from the workbook
                    Sheet firstSheet = workbook.getSheetAt(0);
                    Iterator<Row> rowIterator = firstSheet.iterator();
                    // skip the header row
                    if (rowIterator.hasNext()) {
                        rowIterator.next(); // 1
                    }  
                    Boolean lbEndFile = false, lbEndCol = false;
                    int iColBlank;
                    TblDVXANDRSPD item = null;                        
                    while (rowIterator.hasNext() && !lbEndFile) {
                        // aqui empiezo a iterar filas
                        Row nextRow = rowIterator.next();
                        Iterator<Cell> cellIterator = nextRow.cellIterator();
                        lbEndCol = false;
                        iColBlank = 0;
                        item = new TblDVXANDRSPD();
                        item.setdMediaCost(0.00);
                        item.setiImpressions(0);
                        item.setdTotalCost(0.00);
                        item.setdCPM(0.00);                        
                        item.setdDspFee(0.00);
                        item.setdGrossMargin(0.00);
                        item.setdNetRevenue(0.00);
                        item.setdGrossRevenue(0.00);
                        item.setdMargin(0.00);
                        item.setdMlFee(0.00);
                        item.setdPlatformFee(0.00);
                        item.setdTechFee(0.00);
                        item.setdSalesRevenue(0.00);
                        item.setvDevice("NA");
                        item.setIdDaily(idDaily);
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
                                                String[] parts = string.split(" ");
                                                if (parts.length > 0){
                                                    String lsDate = parts[0].replace("\"", "");
                                                    item.setvDate(lsDate);
                                                    parts = null;
                                                    parts = lsDate.split("-");
                                                    if (parts.length > 2){
                                                        
                                                        item.setiYear(Integer.valueOf(parts[0]));
                                                        item.setiMonth(Integer.valueOf(parts[1]));
                                                        item.setiDay(Integer.valueOf(parts[2]));
                                                    }                                                    
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
                                case 2://DealName
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){
                                                item.setvDeal(nextCell.getStringCellValue());    
                                                item.setvDeal(item.getvDeal().replace("\"", ""));
                                                item.setvBrand((item.getvDeal()!=null && !item.getvDeal().isEmpty()) ? jpaCatalog.getValueByTypePattern("S","BRAND", item.getvDeal()):"");
                                                item.setvAdvertiser((item.getvBrand() !=null && !item.getvBrand().isEmpty()) ? jpaCatalog.getValueByTypePattern("S","ADVERTISER", item.getvBrand()):"");                                                
                                                item.setvClient(item.getvBrand());
                                                item.setvAgency((item.getvBrand() !=null && !item.getvBrand().isEmpty()) ? jpaCatalog.getValueByTypePattern("S","AGENCY", item.getvBrand()):"");
                                                item.setvDsp((item.getvDeal()!=null && !item.getvDeal().isEmpty()) ? jpaCatalog.getValueByTypePattern("S","DSP", item.getvDeal()):"");
                                                item.setvChannel((item.getvDeal()!=null && !item.getvDeal().isEmpty()) ? jpaCatalog.getValueByTypePattern("S","CHANNEL", item.getvDeal()):"");
                                                item.setvSeat((item.getvDeal()!=null && !item.getvDeal().isEmpty()) ? jpaCatalog.getValueByTypePattern("S","SEAT", item.getvDeal()):"");
                                                item.setvExchange((item.getvSeat()!=null && !item.getvSeat().isEmpty()) ? jpaCatalog.getValueByTypePattern("S","EXCHANGE", item.getvSeat()):"");                                                
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
                                case 3://Impressions
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){                                                
                                                item.setiImpressions(Integer.valueOf(nextCell.getStringCellValue().replace("\"", "")));
                                            }
                                        }
                                    }catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                    break;
                                case 4://SalesRevenue (SpendUSD)
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){  
                                                item.setdSalesRevenue(Double.valueOf(nextCell.getStringCellValue().replace("\"", "")));
                                                if(item.getdSalesRevenue() != null){
                                                    item.setdTechFee((item.getdSalesRevenue() * 10.00) / 100.00);
                                                    item.setdCPM((item.getiImpressions() > 0) ? (1000.00 * (item.getdSalesRevenue() / item.getiImpressions())) : 0.00);
                                                    item.setdDspFee((item.getdSalesRevenue() * 19.00) / 100.00);
                                                    if ((item.getvSeat()!=null && item.getvSeat().contains("DATAP-ML"))){
                                                        item.setdMlFee((item.getdSalesRevenue() * 10.00) / 100.00);
                                                    }                                                                                                
                                                }     
                                            }
                                        }
                                    }catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                    break;
                                case 6://curationMargin/GrossMargin
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){ 
                                                item.setdGrossMargin(Double.valueOf(nextCell.getStringCellValue().replace("\"", "")));
                                                if(item.getdGrossMargin() != null && item.getdSalesRevenue() != null){
                                                    item.setdMediaCost(item.getdSalesRevenue() - item.getdGrossMargin() - item.getdTechFee());
                                                    if (item.getdSalesRevenue() > 0){
                                                        item.setdMargin(item.getdGrossMargin() / item.getdSalesRevenue());
                                                    }
                                                }
                                                item.setdGrossRevenue(item.getdGrossMargin() - item.getdMlFee());
                                                item.setdTotalCost(item.getdMediaCost() + item.getdTechFee());
                                                item.setdNetRevenue(item.getdSalesRevenue() - item.getdTechFee() - item.getdMediaCost() - item.getdMlFee() - item.getdPlatformFee() - item.getdDspFee());
                                            }                                                        
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
                        }
                        // Append to list
                        if (item != null && item.getiImpressions() > 0 && item.getdSalesRevenue() > 0){
                            localitemsXANDR.add(item);
                        }
                        
                    }// END ROWS
                    workbook.close(); 
                }               
        }
       }
       return localitemsXANDR;
    }

    protected void scrap_SSP_Xandr_Format(UploadedFile itemFile, TblDailyProcess idDaily) throws FileNotFoundException, IOException{
        System.out.println("scrap_SSP_Xandr_Format");
        itemsDV360 = null;
        if (itemFile != null){            
            String lsFileName = itemFile.getFileName();
            if (lsFileName.endsWith(".xlsx")){                               
                XSSFWorkbook workbook = new XSSFWorkbook(itemFile.getInputStream());                              
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
                    item.setIdDaily(idDaily);
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
        }
       }
    }
    
    protected void scrap_SSP_OpenX_Format(UploadedFile itemFile, TblDailyProcess idDaily) throws FileNotFoundException, IOException{
        System.out.println("scrap_SSP_OpenX_Format");
        itemsDV360 = null;
        if (itemFile != null){            
            String lsFileName = itemFile.getFileName();
            if (lsFileName.endsWith(".xlsx")){                                               
                XSSFWorkbook workbook = new XSSFWorkbook(itemFile.getInputStream());
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
                    item.setIdDaily(idDaily);
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
        }
       }
    }
    
    protected boolean save_ItemsSSP(String lsFileName){
        System.out.println("save_ItemsSSP");
        if (itemsXANDR != null && !itemsXANDR.isEmpty() && !lsFileName.isEmpty()){
            try { 

                PreparedStatement pstmt = connect.prepareStatement("INSERT into `tbl_raw_ssp_data` "
                                        + "(`dDate`,`vAdvertiser`,`vBrand`,`vDeal`,`vDevice`,`dGrossMargin`,`iImpressions`,`dSalesRevenue`,`dTechFee`,`dMediaCost`,`dTotalCost`,`dCPM`,`dMlFee`,`dPlatformFee`,`dDspFee`,`dGrossRevenue`,`dNetRevenue`,`vClient`,`vChannel`,`vDsp`,`vAgency`,`iYear`,`iMonth`,`iDay`,`vSeat`,`vExchange`,`dMargin`,`dSystemDate`,`vFileName`, `id_daily`)"
                                        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?);");

                for (TblDVXANDRSPD item : itemsXANDR) {                                    
                    pstmt.setDate(1, item.getIdDaily().getdDate());
                    pstmt.setString(2, item.getvAdvertiser());
                    pstmt.setString(3, item.getvBrand());
                    pstmt.setString(4, item.getvDeal());
                    pstmt.setString(5, item.getvDevice());
                    
                    double num = item.getdGrossMargin();
                    BigDecimal bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                      
                    pstmt.setDouble(6, bd.doubleValue());                    
                    
                    pstmt.setInt(7, item.getiImpressions());
                    
                    num = item.getdSalesRevenue();
                    bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                      
                    pstmt.setDouble(8, bd.doubleValue());                     
                    
                    num = item.getdTechFee();
                    bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                                        
                    pstmt.setDouble(9, bd.doubleValue());
                    
                    num = item.getdMediaCost();
                    bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                    
                    pstmt.setDouble(10, bd.doubleValue());
                    
                    num = item.getdTotalCost();
                    bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                    
                    pstmt.setDouble(11, bd.doubleValue());                
                    
                    num = item.getdCPM();
                    bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                     
                    pstmt.setDouble(12, bd.doubleValue());                    
                    
                    num = item.getdMlFee();
                    bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                    
                    pstmt.setDouble(13, bd.doubleValue());
                    
                    num = item.getdPlatformFee();
                    bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                     
                    pstmt.setDouble(14, bd.doubleValue());
                    
                    num = item.getdDspFee();
                    bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                    
                    pstmt.setDouble(15, bd.doubleValue());
                    
                    num = item.getdGrossRevenue();
                    bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                     
                    pstmt.setDouble(16, bd.doubleValue());
                    
                    num = item.getdNetRevenue();
                    bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                    
                    pstmt.setDouble(17, bd.doubleValue());
                                        
                    pstmt.setString(18, item.getvClient());
                    pstmt.setString(19, item.getvChannel());
                    pstmt.setString(20, item.getvDsp());
                    pstmt.setString(21, item.getvAgency());
                    pstmt.setInt(22, item.getIdDaily().getiYear());
                    pstmt.setInt(23, item.getIdDaily().getiMonth());
                    pstmt.setInt(24, item.getIdDaily().getiDay());
                    pstmt.setString(25, item.getvSeat());
                    pstmt.setString(26, item.getvExchange());
                    pstmt.setInt(27, item.getIdDaily().getId_daily());

                    num = item.getdMargin();
                    bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);
                    pstmt.setDouble(27, bd.doubleValue());                    
                    
                    pstmt.setString(28, lsFileName.trim());                    
                    pstmt.executeUpdate();
                }                
                pstmt.close(); 
                System.out.println("items saved: " + String.valueOf(itemsXANDR.size()));
                return true;
            } catch (Exception ex) {
            
                System.out.println("in save_ItemsSSP");
                System.out.println(ex.getMessage());
                ex.printStackTrace();                
            }        
        }
        return false;
    }          
    
    public List<TblDailyProcess> getCalendarUpToYesterday(){
        try {
                getConnection();
                PreparedStatement pstmt = connect.prepareStatement("select `id_daily`, `iYear`, `iMonth`, `iDay`, `dDate`, `iStatusProcess`, `vDescription`, `iQuarter`, `iWeek`, `vDayName`, `vMonthName`, `iSHoliday`, `iSWeekend` from tbl_daily_process, tbl_process_status where idStatus = iStatusProcess and dDate <= (CURDATE() - INTERVAL 1 DAY) and tbl_process_status.`idStatus` = 1 order by iYear, iMonth, iDay");            
                ResultSet rs = pstmt.executeQuery();  
                itemsDaily = new ArrayList();
                while (rs.next()) {             
                    TblDailyProcess item = new TblDailyProcess();
                    item.setId_daily(rs.getInt("id_daily"));
                    item.setiYear(rs.getInt("iYear"));
                    item.setiMonth(rs.getInt("iMonth"));
                    item.setiDay(rs.getInt("iDay"));
                    item.setdDate(rs.getDate("dDate"));

                    TblProcessStatus itemStatus = new TblProcessStatus();
                    itemStatus.setIdStatus(rs.getInt("iStatusProcess"));
                    itemStatus.setvDescription(rs.getString("vDescription"));

                    item.setiStatusProcess(itemStatus);
                    item.setiQuarter(rs.getInt("iQuarter"));
                    item.setiWeek(rs.getInt("iWeek"));
                    item.setvDayName(rs.getString("vDayName"));
                    item.setvMonthName(rs.getString("vMonthName"));

                    itemsDaily.add(item);
                }
                rs.close();
                pstmt.close();      
                closeConnection();
            } catch (Exception ex) {
                itemsDaily = null;
                System.out.println("getCalendarFromDatabase");
                System.out.println(ex.getMessage());
                ex.printStackTrace();                
            }    
        return itemsDaily;
    }

    public List<Date> getDatesMerged(){
        try {
                getConnection();
                PreparedStatement pstmt = connect.prepareStatement("select `dDate` from tbl_daily_process where `iStatusProcess` = 3 and `dDate` < (now()-1) and `iStatus` = 1");            
                ResultSet rs = pstmt.executeQuery();  
                List<Date> itemsDate = new ArrayList();
                while (rs.next()) {             
                    itemsDate.add(rs.getDate("dDate"));                    
                }
                rs.close();
                pstmt.close();   
                closeConnection();
                return itemsDate;                
            } catch (Exception ex) {                
                System.out.println("getCalendarFromDatabase");
                System.out.println(ex.getMessage());
                ex.printStackTrace();   
                return null;
            }    
    }    
    
    public List<TblDailyProcess> getCalendarFromDatabase(){
        try {
                getConnection();
                PreparedStatement pstmt = connect.prepareStatement("select `id_daily`, `iYear`, `iMonth`, `iDay`, `dDate`, `iStatusProcess`, `vDescription`, `iQuarter`, `iWeek`, `vDayName`, `vMonthName`, `iSHoliday`, `iSWeekend` from tbl_daily_process, tbl_process_status where idStatus = iStatusProcess and year(now()) = `iYear` and tbl_daily_process.`iStatus` = 1");            
                ResultSet rs = pstmt.executeQuery();  
                itemsDaily = new ArrayList();
                while (rs.next()) {             
                    TblDailyProcess item = new TblDailyProcess();
                    item.setId_daily(rs.getInt("id_daily"));
                    item.setiYear(rs.getInt("iYear"));
                    item.setiMonth(rs.getInt("iMonth"));
                    item.setiDay(rs.getInt("iDay"));
                    item.setdDate(rs.getDate("dDate"));
                    
                    TblProcessStatus itemStatus = new TblProcessStatus();
                    itemStatus.setIdStatus(rs.getInt("iStatusProcess"));
                    itemStatus.setvDescription(rs.getString("vDescription"));
                    
                    item.setiStatusProcess(itemStatus);
                    item.setiQuarter(rs.getInt("iQuarter"));
                    item.setiWeek(rs.getInt("iWeek"));
                    item.setvDayName(rs.getString("vDayName"));
                    item.setvMonthName(rs.getString("vMonthName"));
                    
                    itemsDaily.add(item);
                }
                rs.close();
                pstmt.close();   
                closeConnection();
            } catch (Exception ex) {
                itemsDaily = null;
                System.out.println("getCalendarFromDatabase");
                System.out.println(ex.getMessage());
                ex.printStackTrace();                
            }    
        return itemsDaily;
    }
    
    public List<TblDV360SPD> getRawDatabyDate(Date ldSelectedDate){
        try {
            getConnection(); 
            PreparedStatement pstmt = connect.prepareStatement("select `Id_raw`, `tbl_raw_data`.`dDate`, `vPartner`, `vCampaign`, `vInsertionOrder`, `vLineItem`, `vExchange`, `vDealName`,	`iImpressions`, `iClicks`, `dMediaCost`, `dTotalMediaCost`, `vDSP`,\n" +
                                                            "	`vClient`, `vAgency`, `vChannel`, `vAlias`, `vVendor`, `vVendorSource`,	`dCPM`, `dCTR`, `dCPC`, `tbl_daily_process`.`iYear`, `tbl_daily_process`.`iMonth`, `tbl_daily_process`.`iDay`, `vFileName`, `tbl_raw_data`.`id_daily`, `tbl_daily_process`.`dDate` as dateProcess\n" +
                                                            "from `tbl_raw_data`, `tbl_daily_process`\n" +
                                                            "where `tbl_raw_data`.`id_daily` = `tbl_daily_process`.`id_daily` and\n" +
                                                            "	`tbl_raw_data`.`tStatus` = 1 and `tbl_daily_process`.`dDate` =  ?"); 
            pstmt.setDate(1, (new java.sql.Date(ldSelectedDate.getTime())));
            ResultSet rs = pstmt.executeQuery();  
            itemsDV360 = new ArrayList();
            while (rs.next()) {             
                TblDailyProcess itemDaily = new TblDailyProcess();
                itemDaily.setId_daily(rs.getInt("id_daily"));
                itemDaily.setdDate(rs.getDate("dateProcess"));                    
                TblDV360SPD item = new TblDV360SPD();

                item.setIdDaily(itemDaily);
                item.setId(rs.getInt("Id_raw"));
                item.setdDate(rs.getDate("dDate"));
                item.setdCPC(rs.getDouble("dCPC"));
                item.setdCPM(rs.getDouble("dCPM"));
                item.setdCTR(rs.getDouble("dCTR"));
                item.setdMediaCosts(rs.getDouble("dMediaCost"));
                item.setdTotalMediaCosts(rs.getDouble("dTotalMediaCost"));
                item.setiAnio(rs.getInt("iYear"));
                item.setiClicks(rs.getInt("iClicks"));
                item.setiDia(rs.getInt("iDay"));
                item.setiImpressions(rs.getInt("iImpressions"));
                item.setiMes(rs.getInt("iMonth"));
                item.setvAgency(rs.getString("vAgency"));
                item.setvAlias(rs.getString("vAlias"));
                item.setvCampaign(rs.getString("vCampaign"));
                item.setvChannel(rs.getString("vChannel"));
                item.setvClient(rs.getString("vClient"));
                item.setvDSP(rs.getString("vDSP"));
                item.setvDealName(rs.getString("vDealName"));
                item.setvExchange(rs.getString("vExchange"));
                item.setvInsertionOrder(rs.getString("vInsertionOrder"));
                item.setvLineItem(rs.getString("vLineItem"));
                item.setvPartner(rs.getString("vPartner"));
                item.setvVendor(rs.getString("vVendor"));
                item.setvVendorSource(rs.getString("vVendorSource"));
                
                itemsDV360.add(item);
            }
            rs.close();
            pstmt.close();                                 
            closeConnection();            
        } catch (Exception ex) {
            itemsDV360 = null;
            System.out.println("getRawDatabyDate");
            System.out.println(ex.getMessage());
            ex.printStackTrace();                
        }
        return itemsDV360;
    }    
    
    protected boolean save_Items(String lsFileName, List<TblDV360SPD> localitemsDV360){
        System.out.println("saveFile");
        if (localitemsDV360 != null && !localitemsDV360.isEmpty() && !lsFileName.isEmpty()){
            try { 
                getConnection(); 
                PreparedStatement pstmt = connect.prepareStatement("INSERT into `tbl_raw_data` "
                                        + "(`dDate`,`iDia`,`iMes`,`iAnio`,`vPartner`,`vCampaign`,`vInsertionOrder`,`vLineItem`,`vExchange`,`vDealName`,`iImpressions`,`iClicks`,`dMediaCost`,`dTotalMediaCost`,`dSystemDate`,`vFileName`,`vDSP`,`vClient`,`vAgency`,`vChannel`,`vAlias`,`vVendor` ,`vVendorSource`, `dCPM`, `dCTR`, `dCPC`, `id_daily`)"
                                        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,?,?,?,?,?,?,?,?);");

                for (TblDV360SPD item : localitemsDV360) {                                    
                    pstmt.setDate(1, item.getIdDaily().getdDate());
                    pstmt.setLong(2, item.getIdDaily().getiDay());
                    pstmt.setLong(3, item.getIdDaily().getiMonth());
                    pstmt.setLong(4, item.getIdDaily().getiYear());
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
                    pstmt.setInt(26, item.getIdDaily().getId_daily());
                    pstmt.executeUpdate();
                }                
                pstmt.close(); 
                closeConnection(); 
                System.out.println("items saved: " + String.valueOf(localitemsDV360.size()));
                return true;
            } catch (Exception ex) {
            
                System.out.println("in save_Items");
                System.out.println(ex.getMessage());
                ex.printStackTrace();                
            }        
        }
        return false;
    }      
    
    protected static SXSSFWorkbook convertCsvToXlsx(UploadedFile itemFile) throws Exception {
        try {
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            SXSSFSheet sheet = workbook.createSheet("Sheet");
            AtomicReference<Integer> row = new AtomicReference<>(0);
            /*Files.readAllLines(itemFile.getInputStream()).forEach(line -> {
                Row currentRow = sheet.createRow(row.getAndSet(row.get() + 1));
                String[] nextLine = line.split(",");
                Stream.iterate(0, i -> i + 1).limit(nextLine.length).forEach(i -> {
                    currentRow.createCell(i).setCellValue(nextLine[i]);
                });
            });*/
            return workbook;
            //FileOutputStream fos = new FileOutputStream(new File(xlsLocation));
            //workbook.write(fos);
            //fos.flush();            
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        return null;
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
    
    protected boolean clean_RawItems(String lsSource){        
        System.out.println("clean_RawItems");
        try {                
            PreparedStatement pstmt;
            if(lsSource.contains("DSP")){
                pstmt = connect.prepareStatement("delete from `tbl_raw_data` where Id_raw > 0;");
            }
            else{
                pstmt = connect.prepareStatement("delete from `tbl_raw_ssp_data` where Id_raw > 0;");
            }
            pstmt.executeUpdate();
            
            if(lsSource.contains("DSP")){
                pstmt = connect.prepareStatement("ALTER TABLE `tbl_raw_data` AUTO_INCREMENT = 1;");
            }
            else{
                pstmt = connect.prepareStatement("ALTER TABLE `tbl_raw_ssp_data` AUTO_INCREMENT = 1;");
            }            
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

    protected boolean copy_RawItems_to_History(String lsSource){        
        System.out.println("copy_RawItems_to_History");
        try {                
            PreparedStatement pstmt;
            if (lsSource.contains("DSP")){
                pstmt = connect.prepareStatement("insert into `tbl_historical_data` (`dDate`, `vPartner`, `vCampaign`, `vInsertionOrder`, `vLineItem`, `vExchange`, `vDealName`, `iImpressions`, `iClicks`, `dMediaCost`, `dTotalMediaCost`, `vDSP`, `vClient`, `vAgency`, `vChannel`, `vAlias`, `vVendor`, `vVendorSource`,	`dCPM`,	`dCTR`,	`dCPC`,	`iAnio`, `iMes`, `iDia`, `dSystemDate`,	`vFileName`, `tEstado`)\n" +
                                                                    "select `dDate`, `vPartner`, `vCampaign`, `vInsertionOrder`, `vLineItem`, `vExchange`, `vDealName`, `iImpressions`, `iClicks`, `dMediaCost`, `dTotalMediaCost`, `vDSP`, `vClient`, `vAgency`, `vChannel`, `vAlias`, `vVendor`, `vVendorSource`, `dCPM`, `dCTR`, `dCPC`, `iAnio`, `iMes`, `iDia`, `dSystemDate`, `vFileName`, `tEstado`\n" +
                                                                    "From `tbl_raw_data`\n" +
                                                                    "where tEstado = 1;");                
            }else{
                pstmt = connect.prepareStatement("insert into `tbl_historical_ssp_data` (`dDate`, `vPartner`, `vCampaign`, `vInsertionOrder`, `vLineItem`, `vExchange`, `vDealName`, `iImpressions`, `iClicks`, `dMediaCost`, `dTotalMediaCost`, `vDSP`, `vClient`, `vAgency`, `vChannel`, `vAlias`, `vVendor`, `vVendorSource`,	`dCPM`,	`dCTR`,	`dCPC`,	`iAnio`, `iMes`, `iDia`, `dSystemDate`,	`vFileName`, `tEstado`)\n" +
                                                                "select `dDate`, `vPartner`, `vCampaign`, `vInsertionOrder`, `vLineItem`, `vExchange`, `vDealName`, `iImpressions`, `iClicks`, `dMediaCost`, `dTotalMediaCost`, `vDSP`, `vClient`, `vAgency`, `vChannel`, `vAlias`, `vVendor`, `vVendorSource`, `dCPM`, `dCTR`, `dCPC`, `iAnio`, `iMes`, `iDia`, `dSystemDate`, `vFileName`, `tEstado`\n" +
                                                                "From `tbl_raw_ssp_data`\n" +
                                                                "where tEstado = 1;");
            }
            
            
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
    
    public void ScanFiles(String lsSource, UploadedFile itemFile, TblDailyProcess idDaily) throws IOException, ClassNotFoundException, Exception{                 
        String lsFileName="";
        if (itemFile != null){  
            if (lsSource.contains("DSP")){
                lsFileName = itemFile.getFileName();
                if (lsFileName.contains("DV360")){
                    save_Items(lsFileName, scrap_DV360_Format(itemFile, idDaily));
                }else if (lsFileName.contains("Basis")){
                    save_Items(lsFileName, scrap_BASIS_Format(itemFile, idDaily));                          
                }else if (lsFileName.contains("Domain-Detailed")){
                    save_Items(lsFileName, scrap_PPOINT_Format(itemFile, idDaily));                  
                }                        
            }else{//SSP
                lsFileName = itemFile.getFileName();
                if (lsFileName.contains("Equativ")){//CSV
                    scrap_SSP_Equative_Format(itemFile, idDaily);
                    save_ItemsSSP(lsFileName);
                }else if (lsFileName.contains("PubMatic")){//CSV
                    scrap_SSP_PubMatic_Format(itemFile, idDaily);
                    save_ItemsSSP(lsFileName);
                }else if (lsFileName.contains("Xandr")){
                    scrap_SSP_Xandr_Format(itemFile, idDaily);
                    save_ItemsSSP(lsFileName);                   
                }else if (lsFileName.contains("DPX")){
                    scrap_PPOINT_Format(itemFile, idDaily);
                    save_ItemsSSP(lsFileName);                    
                }                        
            }                
        }
    }
}

package com.dp.util;

import com.dp.entity.TblCatalog;
import com.dp.entity.TblCatalogColumn;
import com.dp.entity.TblTypeSources;
import com.dp.facade.TblCatalogColumnFacade;
import com.dp.facade.TblCatalogFacade;
import com.dp.facade.util.JsfUtil;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.omnifaces.util.Components;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author ZAMBRED
 */
@ManagedBean
@ViewScoped

public class DAOFile implements Serializable  {
    private Connection connect = null;
    private String url = "jdbc:mysql://localhost:3306/rptdata?serverTimezone=UTC";
    private String username = "scraper";
    private String password = "qernel24";
    private List<TblDV360SPD> itemsDV360Refactor = null;
    private List<TblDVXANDRSPD> itemsXANDRRefactor = null;
    private List<TblDailyProcess> itemsDaily = null;
    private List<TblProcessStatus> itemsStatusProcess = null;
    private List<TblCatalogo> itemsCatalogo = null;
    //private List<TblCatalogoColumn> itemsCatalogoColumn = null;
    
    public DAOFile() {
        //jpaCatalog = new TblCatalogFacade();
    }        
    
    protected void getConnection() throws ClassNotFoundException
    {
      try {
          Class.forName("com.mysql.jdbc.Driver");
          connect = (Connection) DriverManager.getConnection(url, username, password);
      } catch (SQLException ex) {
          ex.printStackTrace();
          throw new RuntimeException("Error connecting to the database", ex);
      }
    }        

    public List<TblCatalogo> getItemsCatalogo() {
        return itemsCatalogo;
    }

    public List<TblDV360SPD> getItemsDV360Refactor() {
        return itemsDV360Refactor;
    }

    public void setItemsDV360Refactor(List<TblDV360SPD> itemsDV360Refactor) {
        this.itemsDV360Refactor = itemsDV360Refactor;
    }

    public List<TblDVXANDRSPD> getItemsXANDRRefactor() {
        return itemsXANDRRefactor;
    }

    public void setItemsXANDRRefactor(List<TblDVXANDRSPD> itemsXANDRRefactor) {
        this.itemsXANDRRefactor = itemsXANDRRefactor;
    }

    public void setItemsCatalogo(List<TblCatalogo> itemsCatalogo) {
        this.itemsCatalogo = itemsCatalogo;
    }
/*
    public List<TblCatalogoColumn> getItemsCatalogoColumn() {
        return itemsCatalogoColumn;
    }

    public void setItemsCatalogoColumn(List<TblCatalogoColumn> itemsCatalogoColumn) {
        this.itemsCatalogoColumn = itemsCatalogoColumn;
    }
*/        
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
      
    protected String getValueBetweenColumnsPredefined(TblDV360SPD item, String lsCategory){
        String lsRet="OTROS";        
        List<TblCatalogo> itemsCatalogoFiltered = new ArrayList<>();
        itemsCatalogo.stream().filter((cat) -> (cat.getvType().equals(lsCategory))).forEachOrdered((cat) -> {
                itemsCatalogoFiltered.add(cat);
        });        
        
        /*
        List<TblCatalogoColumn> itemsColumnFiltered = new ArrayList<>();
        itemsCatalogoColumn.stream().filter((a) -> (a.getvCategory().equals(lsCategory))).forEachOrdered((a) -> {
                itemsColumnFiltered.add(a);
        });

        
        TblCatalogoColumn catFound = itemsCatalogoFiltered.stream()
            .flatMap(a -> a.getTblCatalogColumnList().stream())
            .filter(c -> c.getvColumnName().equalsIgnoreCase("vPartner"))
            .findFirst()
            .orElse(null);
        
        if (catFound != null){
            
        }
        */
        
        for (TblCatalogo catFound : itemsCatalogoFiltered) {
            TblCatalogo itemFound = null;
            for (TblCatalogoColumn itemColum : catFound.getTblCatalogColumnList()) {            
                switch(itemColum.getvColumnName()){                    
                    case "vPartner":
                        itemFound = (item.getvPartner().toUpperCase().contains(catFound.getvPattern().toUpperCase())) ? catFound : null;
                        break;
                    case "vCampaign":
                        itemFound = (item.getvCampaign().toUpperCase().contains(catFound.getvPattern().toUpperCase())) ? catFound : null;
                        break;
                    case "vInsertionOrder":
                        itemFound = (item.getvInsertionOrder().toUpperCase().contains(catFound.getvPattern().toUpperCase())) ? catFound : null;
                        break;
                    case "vLineItem":
                        itemFound = (item.getvLineItem().toUpperCase().contains(catFound.getvPattern().toUpperCase())) ? catFound : null;
                        break;
                    case "vExchange":
                        itemFound = (item.getvExchange().toUpperCase().contains(catFound.getvPattern().toUpperCase())) ? catFound : null;
                        break;
                    case "vDealName":
                        itemFound = (item.getvDealName().toUpperCase().contains(catFound.getvPattern().toUpperCase())) ? catFound : null;
                        break;
                    case "vClient":
                        itemFound = (item.getvClient().toUpperCase().contains(catFound.getvPattern().toUpperCase())) ? catFound : null;
                        break;
                }
                if(itemFound != null) {
                    lsRet = itemFound.getvValue();
                    break;
                }                  
            }
            if (itemFound != null) break;            
        }
        
        /*
        for (TblCatalogoColumn itemColum : itemsColumnFiltered) {
                TblCatalogo catFound = null;
                switch(itemColum.getvColumnName()){                    
                    case "vPartner":
                        catFound = IterableUtils.find(itemsCatalogoFiltered, itemCat -> item.getvPartner().toUpperCase().contains(itemCat.getvPattern().toUpperCase()));                        
                        break;
                    case "vCampaign":
                        catFound = IterableUtils.find(itemsCatalogoFiltered, itemCat -> item.getvCampaign().toUpperCase().contains(itemCat.getvPattern().toUpperCase()));  
                        break;
                    case "vInsertionOrder":
                        catFound = IterableUtils.find(itemsCatalogoFiltered, itemCat -> item.getvInsertionOrder().toUpperCase().contains(itemCat.getvPattern().toUpperCase()));  
                        break;
                    case "vLineItem":
                        catFound = IterableUtils.find(itemsCatalogoFiltered, itemCat -> item.getvLineItem().toUpperCase().contains(itemCat.getvPattern().toUpperCase()));
                        break;
                    case "vExchange":
                        catFound = IterableUtils.find(itemsCatalogoFiltered, itemCat -> item.getvExchange().toUpperCase().contains(itemCat.getvPattern().toUpperCase()));
                        break;
                    case "vDealName":
                        catFound = IterableUtils.find(itemsCatalogoFiltered, itemCat -> item.getvDealName().toUpperCase().contains(itemCat.getvPattern().toUpperCase()));                        
                        break;
                    case "vClient":
                        catFound = IterableUtils.find(itemsCatalogoFiltered, itemCat -> item.getvClient().toUpperCase().contains(itemCat.getvPattern().toUpperCase()));
                        break;
                }
                if(catFound != null) {
                    lsRet = catFound.getvValue();
                    break;
                }
        }*/
        
        return lsRet;
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
                        item.setvPartner("");
                        item.setvCampaign("");
                        item.setvInsertionOrder("");
                        item.setvLineItem("");
                        item.setvExchange("");
                        item.setvDealName("");
                        item.setvClient("");                     
                        while (cellIterator.hasNext() && !lbEndCol) {
                            // aqui empiezo a iterar las columnas
                            Cell nextCell = cellIterator.next();
                            
                            int columnIndex = nextCell.getColumnIndex();
                            
                            if(nextCell.getCellType() == CellType.BLANK){
                                iColBlank++;
                            }
                            switch (columnIndex) {
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
                                item.setvDSP(getValueBetweenColumnsPredefined(item,"DSP"));                                
                                item.setvClient(getValueBetweenColumnsPredefined(item,"CLIENT"));
                                item.setvAgency(getValueBetweenColumnsPredefined(item,"AGENCY"));
                                item.setvChannel(getValueBetweenColumnsPredefined(item,"CHANNEL"));
                                item.setvVendor(getValueBetweenColumnsPredefined(item,"VENDOR"));
                                
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
                        item.setvPartner("");
                        item.setvCampaign("");
                        item.setvInsertionOrder("");
                        item.setvLineItem("");
                        item.setvExchange("");
                        item.setvDealName("");
                        item.setvClient("");                        
                        while (cellIterator.hasNext() && !lbEndCol) {
                            // aqui empiezo a iterar las columnas
                            Cell nextCell = cellIterator.next();
                            
                            int columnIndex = nextCell.getColumnIndex();
                            
                            if(nextCell.getCellType() == CellType.BLANK){
                                iColBlank++;
                            }
                            switch (columnIndex) {
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

                                item.setvDSP(getValueBetweenColumnsPredefined(item,"DSP"));                                
                                item.setvClient(getValueBetweenColumnsPredefined(item,"CLIENT"));
                                item.setvAgency(getValueBetweenColumnsPredefined(item,"AGENCY"));
                                item.setvChannel(getValueBetweenColumnsPredefined(item,"CHANNEL"));
                                item.setvVendor(getValueBetweenColumnsPredefined(item,"VENDOR"));

                                
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
                        item.setvPartner("");
                        item.setvCampaign("");
                        item.setvInsertionOrder("");
                        item.setvLineItem("");
                        item.setvExchange("");
                        item.setvDealName("");
                        item.setvClient("");                        
                        while (cellIterator.hasNext() && !lbEndCol) {
                            // aqui empiezo a iterar las columnas
                            Cell nextCell = cellIterator.next();
                            
                            int columnIndex = nextCell.getColumnIndex();
                            
                            if(nextCell.getCellType() == CellType.BLANK){
                                iColBlank++;
                            }
                            switch (columnIndex) {
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
                              
                                item.setvDSP(getValueBetweenColumnsPredefined(item,"DSP"));                                
                                item.setvClient(getValueBetweenColumnsPredefined(item,"CLIENT"));
                                item.setvAgency(getValueBetweenColumnsPredefined(item,"AGENCY"));
                                item.setvChannel(getValueBetweenColumnsPredefined(item,"CHANNEL"));
                                item.setvVendor(getValueBetweenColumnsPredefined(item,"VENDOR"));                                
                                
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

    protected String getValueBetweenColumnsPredefined(TblDVXANDRSPD item, String lsCategory){
        String lsRet="OTROS";        
        List<TblCatalogo> itemsCatalogoFiltered = new ArrayList<>();
        itemsCatalogo.stream().filter((cat) -> (cat.getvType().equals(lsCategory))).forEachOrdered((cat) -> {
                itemsCatalogoFiltered.add(cat);
        });          
        /*
        List<TblCatalogoColumn> itemsColumnFiltered = new ArrayList<>();        
        itemsCatalogoColumn.stream().filter((a) -> (a.getvCategory().equals(lsCategory))).forEachOrdered((a) -> {
                itemsColumnFiltered.add(a);
        });        
        */
        
        for (TblCatalogo catFound : itemsCatalogoFiltered) {
            TblCatalogo itemFound = null;
            for (TblCatalogoColumn itemColum : catFound.getTblCatalogColumnList()) {            
                switch(itemColum.getvColumnName()){                    
                    case "vAdvertiser":
                        itemFound = (item.getvAdvertiser().toUpperCase().contains(catFound.getvPattern().toUpperCase())) ? catFound : null;
                        break;
                    case "vBrand":
                        itemFound = (item.getvBrand().toUpperCase().contains(catFound.getvPattern().toUpperCase())) ? catFound : null;
                        break;
                    case "vDeal":
                        itemFound = (item.getvDeal().toUpperCase().contains(catFound.getvPattern().toUpperCase())) ? catFound : null;
                        break;
                    case "vDevice":
                        itemFound = (item.getvDevice().toUpperCase().contains(catFound.getvPattern().toUpperCase())) ? catFound : null;
                        break;
                    case "vSeat":
                        itemFound = (item.getvSeat().toUpperCase().contains(catFound.getvPattern().toUpperCase())) ? catFound : null;
                        break;
                }
                if(itemFound != null) {
                    lsRet = itemFound.getvValue();
                    break;
                }                  
            }
            if (itemFound != null) break;            
        }        
        
        /*
        for (TblCatalogoColumn itemColum : itemsColumnFiltered) {
                TblCatalogo catFound = null;
                switch(itemColum.getvColumnName()){                    
                    case "vAdvertiser":
                        catFound = IterableUtils.find(itemsCatalogoFiltered, itemCat -> item.getvAdvertiser().toUpperCase().contains(itemCat.getvPattern().toUpperCase()));                        
                        break;
                    case "vBrand":
                        catFound = IterableUtils.find(itemsCatalogoFiltered, itemCat -> item.getvBrand().toUpperCase().contains(itemCat.getvPattern().toUpperCase()));  
                        break;
                    case "vDeal":
                        catFound = IterableUtils.find(itemsCatalogoFiltered, itemCat -> item.getvDeal().toUpperCase().contains(itemCat.getvPattern().toUpperCase()));  
                        break;
                    case "vDevice":
                        catFound = IterableUtils.find(itemsCatalogoFiltered, itemCat -> item.getvDevice().toUpperCase().contains(itemCat.getvPattern().toUpperCase()));
                        break;
                    case "vSeat":
                        catFound = IterableUtils.find(itemsCatalogoFiltered, itemCat -> item.getvSeat().toUpperCase().contains(itemCat.getvPattern().toUpperCase()));
                        break;
                }
                if(catFound != null) {
                    lsRet = catFound.getvValue();
                    break;
                }
        }*/        

        return lsRet;
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
                        item.setdMarginFee(0.00);
                        item.setdTechFee(0.00);
                        item.setdSalesRevenue(0.00);
                        item.setdNetMargin(0.00);
                        item.setvDevice("NA");
                        item.setIdDaily(idDaily);
                        item.setvDeal("");                                                    
                        item.setvBrand("");
                        item.setvAdvertiser("");                                                
                        item.setvClient("");
                        item.setvAgency("");
                        item.setvDsp("");
                        item.setvChannel("");
                        item.setvSeat("");
                        item.setvExchange("");                                                
                        while (cellIterator.hasNext() && !lbEndCol) {
                            // aqui empiezo a iterar las columnas
                            Cell nextCell = cellIterator.next();
                            
                            int columnIndex = nextCell.getColumnIndex();
                            
                            if(nextCell.getCellType() == CellType.BLANK){
                                iColBlank++;
                            }
                            switch (columnIndex) {
                                case 3://DealName
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){
                                                item.setvDeal(nextCell.getStringCellValue());    
                                                item.setvDeal(item.getvDeal().replace("\"", ""));
                                                item.setvBrand(getValueBetweenColumnsPredefined(item,"BRAND"));
                                                item.setvAdvertiser(getValueBetweenColumnsPredefined(item,"ADVERTISER"));                                                
                                                item.setvClient(item.getvBrand());
                                                item.setvAgency(getValueBetweenColumnsPredefined(item,"AGENCY"));
                                                item.setvDsp(getValueBetweenColumnsPredefined(item,"DSP"));
                                                item.setvChannel(getValueBetweenColumnsPredefined(item,"CHANNEL"));
                                                item.setvSeat(getValueBetweenColumnsPredefined(item,"SEAT"));
                                                item.setvExchange(getValueBetweenColumnsPredefined(item,"EXCHANGE"));                                                
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
                                                    if (item.getvSeat() != null){
                                                        if(item.getvSeat().contains("DATAP-EQT")){
                                                            item.setdMarginFee((item.getdGrossMargin() * 7.00) / 100.00);
                                                        }else if(item.getvSeat().contains("DATAP-PUB")){
                                                            item.setdMarginFee((item.getdGrossMargin() * 5.00) / 100.00);
                                                        }
                                                    }                                                                                                                                                    
                                                }
                                                item.setdGrossRevenue(item.getdGrossMargin() - item.getdMlFee());
                                                item.setdTotalCost(item.getdMediaCost() + item.getdTechFee());
                                                item.setdNetRevenue(item.getdSalesRevenue() - item.getdTechFee() - item.getdMediaCost() - item.getdMlFee() - item.getdMarginFee()- item.getdDspFee());
                                                if (item.getdSalesRevenue() > 0){
                                                    item.setdMargin(item.getdGrossMargin() / item.getdSalesRevenue());
                                                    item.setdNetMargin(item.getdNetRevenue() / item.getdSalesRevenue());
                                                }                                                
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
                        item.setdNetMargin(0.00);
                        item.setdMlFee(0.00);
                        item.setdMarginFee(0.00);
                        item.setdTechFee(0.00);
                        item.setdSalesRevenue(0.00);
                        item.setvDevice("NA");
                        item.setIdDaily(idDaily);
                        item.setvDeal("");                                                    
                        item.setvBrand("");
                        item.setvAdvertiser("");                                                
                        item.setvClient("");
                        item.setvAgency("");
                        item.setvDsp("");
                        item.setvChannel("");
                        item.setvSeat("");
                        item.setvExchange("");                                                
                        while (cellIterator.hasNext() && !lbEndCol) {
                            // aqui empiezo a iterar las columnas
                            Cell nextCell = cellIterator.next();
                            
                            int columnIndex = nextCell.getColumnIndex();
                            
                            if(nextCell.getCellType() == CellType.BLANK){
                                iColBlank++;
                            }
                            switch (columnIndex) {
                                case 2://DealName
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){
                                                item.setvDeal(nextCell.getStringCellValue());    
                                                item.setvDeal(item.getvDeal().replace("\"", ""));                                                
                                                item.setvBrand(getValueBetweenColumnsPredefined(item,"BRAND"));
                                                item.setvAdvertiser(getValueBetweenColumnsPredefined(item,"ADVERTISER"));                                                
                                                item.setvClient(item.getvBrand());
                                                item.setvAgency(getValueBetweenColumnsPredefined(item,"AGENCY"));
                                                item.setvDsp(getValueBetweenColumnsPredefined(item,"DSP"));
                                                item.setvChannel(getValueBetweenColumnsPredefined(item,"CHANNEL"));
                                                item.setvSeat(getValueBetweenColumnsPredefined(item,"SEAT"));
                                                item.setvExchange(getValueBetweenColumnsPredefined(item,"EXCHANGE"));                                                                                                
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
                                                }
                                                if (item.getvSeat() != null){
                                                    if(item.getvSeat().contains("DATAP-EQT")){
                                                        item.setdMarginFee((item.getdGrossMargin() * 7.00) / 100.00);
                                                    }else if(item.getvSeat().contains("DATAP-PUB")){
                                                        item.setdMarginFee((item.getdGrossMargin() * 5.00) / 100.00);
                                                    }
                                                }                                                                                                                                                                                                    
                                                item.setdGrossRevenue(item.getdGrossMargin() - item.getdMlFee());
                                                item.setdTotalCost(item.getdMediaCost() + item.getdTechFee());
                                                item.setdNetRevenue(item.getdSalesRevenue() - item.getdTechFee() - item.getdMediaCost() - item.getdMlFee() - item.getdMarginFee()- item.getdDspFee());
                                                if (item.getdSalesRevenue() > 0){
                                                    item.setdMargin(item.getdGrossMargin() / item.getdSalesRevenue());
                                                    item.setdNetMargin(item.getdNetRevenue() / item.getdSalesRevenue());
                                                }                                              
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

    protected List<TblDVXANDRSPD> scrap_SSP_Xandr_Data_Format(UploadedFile itemFile, TblDailyProcess idDaily) throws FileNotFoundException, IOException{
        System.out.println("scrap_SSP_Xandr_Data_Format");
        List<TblDVXANDRSPD> localitemsXANDR = new ArrayList();      
        if (itemFile != null){            
            String lsFileName = itemFile.getFileName();       

            if (lsFileName.endsWith(".xlsx")){                
                //Get first sheet from the workbook
                try (XSSFWorkbook workbook = new XSSFWorkbook(itemFile.getInputStream())) {
                     TblCatalogFacade jpaCatalog = new TblCatalogFacade();
                    //Get first sheet from the workbook
                    Sheet firstSheet = workbook.getSheetAt(0);
                    Iterator<Row> rowIterator = firstSheet.iterator();
                    // skip the header row
                    if (rowIterator.hasNext()) {
                        rowIterator.next(); // 1
                        rowIterator.next(); // 2
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
                        item.setdNetRevenue(0.00);
                        item.setdMlFee(0.00);
                        item.setdMarginFee(0.00);
                        item.setdTechFee(0.00);
                        item.setdSalesRevenue(0.00);
                        item.setvDevice("NA");
                        item.setIdDaily(idDaily);
                        item.setvDeal("");                                                    
                        item.setvBrand("");
                        item.setvAdvertiser("");                                                
                        item.setvClient("");
                        item.setvAgency("");
                        item.setvDsp("");
                        item.setvChannel("");
                        item.setvSeat("");
                        item.setvExchange("");                        
                        while (cellIterator.hasNext() && !lbEndCol) {
                            // aqui empiezo a iterar las columnas
                            Cell nextCell = cellIterator.next();
                            
                            int columnIndex = nextCell.getColumnIndex();
                            
                            if(nextCell.getCellType() == CellType.BLANK){
                                iColBlank++;
                            }
                            switch (columnIndex) {
                                case 2://DealName
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){
                                                item.setvDeal(nextCell.getStringCellValue());                                                    
                                                item.setvBrand(getValueBetweenColumnsPredefined(item,"BRAND"));
                                                item.setvAdvertiser(getValueBetweenColumnsPredefined(item,"ADVERTISER"));                                                
                                                item.setvClient(item.getvBrand());
                                                item.setvAgency(getValueBetweenColumnsPredefined(item,"AGENCY"));
                                                item.setvDsp(getValueBetweenColumnsPredefined(item,"DSP"));
                                                item.setvChannel(getValueBetweenColumnsPredefined(item,"CHANNEL"));
                                                item.setvSeat(getValueBetweenColumnsPredefined(item,"SEAT"));
                                                item.setvExchange(getValueBetweenColumnsPredefined(item,"EXCHANGE"));                                                                                                   
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
                                case 3://SalesRevenue (SpendUSD)
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){  
                                                item.setdSalesRevenue(Double.valueOf(nextCell.getStringCellValue()));
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
                                case 4://curationMargin/GrossMargin
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){ 
                                                item.setdGrossMargin(Double.valueOf(nextCell.getStringCellValue().replace("\"", "")));
                                                if(item.getdGrossMargin() != null && item.getdSalesRevenue() != null){
                                                    item.setdMediaCost(item.getdSalesRevenue() - item.getdGrossMargin() - item.getdTechFee());
                                                }
                                                if (item.getvSeat() != null){
                                                    if(item.getvSeat().contains("DATAP-EQT")){
                                                        item.setdMarginFee((item.getdGrossMargin() * 7.00) / 100.00);
                                                    }else if(item.getvSeat().contains("DATAP-PUB")){
                                                        item.setdMarginFee((item.getdGrossMargin() * 5.00) / 100.00);
                                                    }
                                                }                                                                                                                                                                                                    
                                                item.setdGrossRevenue(item.getdGrossMargin() - item.getdMlFee());
                                                item.setdTotalCost(item.getdMediaCost() + item.getdTechFee());
                                                item.setdNetRevenue(item.getdSalesRevenue() - item.getdTechFee() - item.getdMediaCost() - item.getdMlFee() - item.getdMarginFee()- item.getdDspFee());
                                                if (item.getdSalesRevenue() > 0){
                                                    item.setdMargin(item.getdGrossMargin() / item.getdSalesRevenue());
                                                    item.setdNetMargin(item.getdNetRevenue() / item.getdSalesRevenue());
                                                }                                              
                                            }                                                        
                                        }
                                    }catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                    break;
                                case 6://Impressions
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){                                                
                                                item.setiImpressions(Integer.valueOf(nextCell.getStringCellValue()));
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

    protected List<TblDVXANDRSPD> scrap_SSP_Xandr_MLM_Format(UploadedFile itemFile, TblDailyProcess idDaily) throws FileNotFoundException, IOException{
        System.out.println("scrap_SSP_Xandr_MLM_Format");
        List<TblDVXANDRSPD> localitemsXANDR = new ArrayList();      
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
                        item.setdNetMargin(0.00);
                        item.setdMlFee(0.00);
                        item.setdMarginFee(0.00);
                        item.setdTechFee(0.00);
                        item.setdSalesRevenue(0.00);
                        item.setvDevice("NA");
                        item.setIdDaily(idDaily);
                        item.setvDeal("");                                                    
                        item.setvBrand("");
                        item.setvAdvertiser("");                                                
                        item.setvClient("");
                        item.setvAgency("");
                        item.setvDsp("");
                        item.setvChannel("");
                        item.setvSeat("");
                        item.setvExchange("");                                                
                        while (cellIterator.hasNext() && !lbEndCol) {
                            // aqui empiezo a iterar las columnas
                            Cell nextCell = cellIterator.next();
                            
                            int columnIndex = nextCell.getColumnIndex();
                            
                            if(nextCell.getCellType() == CellType.BLANK){
                                iColBlank++;
                            }
                            switch (columnIndex) {
                                case 1://Advertiser
                                    try{
                                        if (nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){
                                                item.setvAdvertiser(nextCell.getStringCellValue());
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
                                case 2://Brand
                                    try{
                                        if (nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){
                                                item.setvBrand(nextCell.getStringCellValue());
                                                item.setvClient(item.getvBrand());                                               
                                                item.setvAgency(getValueBetweenColumnsPredefined(item,"AGENCY"));
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
                                                item.setvDsp(getValueBetweenColumnsPredefined(item,"DSP"));
                                                item.setvChannel(getValueBetweenColumnsPredefined(item,"CHANNEL"));
                                                item.setvSeat(getValueBetweenColumnsPredefined(item,"SEAT"));
                                                item.setvExchange(getValueBetweenColumnsPredefined(item,"EXCHANGE"));   
                                                
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
                                case 4://Device
                                    try{
                                        if (nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){
                                                item.setvDevice(nextCell.getStringCellValue());
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
                                case 5://curationMargin/GrossMargin
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){ 
                                                item.setdGrossMargin(Double.valueOf(nextCell.getStringCellValue()));
                                            }
                                        }else if(nextCell.getCellType() == CellType.NUMERIC){
                                            item.setdGrossMargin(nextCell.getNumericCellValue());
                                        }                                               
                                        if (item.getvSeat() != null){
                                            if(item.getvSeat().contains("DATAP-EQT")){
                                                item.setdMarginFee((item.getdGrossMargin() * 7.00) / 100.00);
                                            }else if(item.getvSeat().contains("DATAP-PUB")){
                                                item.setdMarginFee((item.getdGrossMargin() * 5.00) / 100.00);
                                            }
                                        }                                                                                                                                                    
                                    }catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                    break;                                                                    
                                case 6://Impressions
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){                                                
                                                item.setiImpressions(Integer.valueOf(nextCell.getStringCellValue()));
                                            }
                                        }else if(nextCell.getCellType() == CellType.NUMERIC){
                                            Double ldvalue = nextCell.getNumericCellValue();
                                            item.setiImpressions(ldvalue.intValue());
                                        }
                                    }catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                    break;
                                case 7://curationRevenue/SalesRevenue
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){ 
                                                item.setdSalesRevenue(Double.valueOf(nextCell.getStringCellValue()));
                                            }
                                        }else if(nextCell.getCellType() == CellType.NUMERIC){
                                            item.setdSalesRevenue(nextCell.getNumericCellValue());
                                        }                                                                                                                                                   
                                        item.setdDspFee((item.getdSalesRevenue() * 19.00) / 100.00);
                                        if ((item.getvSeat()!=null && item.getvSeat().contains("DATAP-ML"))){
                                            item.setdMlFee((item.getdSalesRevenue() * 10.00) / 100.00);
                                        }                                                                    
                                        item.setdGrossRevenue(item.getdGrossMargin() - item.getdMlFee());       
                                        if (item.getdSalesRevenue() > 0){
                                            item.setdMargin(item.getdGrossMargin() / item.getdSalesRevenue());
                                        }                                                                  
                                    }catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                    break;                                                                    
                                case 8://techFees
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){  
                                                item.setdTechFee(Double.valueOf(nextCell.getStringCellValue()));                                                   
                                            }
                                        }else if(nextCell.getCellType() == CellType.NUMERIC){
                                            item.setdTechFee(nextCell.getNumericCellValue());
                                        }                                                                                                                                                   
                                    }catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                    break;
                                case 9://mediaCost
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){ 
                                                item.setdMediaCost(Double.valueOf(nextCell.getStringCellValue()));
                                            }
                                        }else if(nextCell.getCellType() == CellType.NUMERIC){
                                            item.setdMediaCost(nextCell.getNumericCellValue());
                                        }                                                                                                                                                   
                                            
                                        item.setdNetRevenue(item.getdSalesRevenue() - item.getdTechFee() - item.getdMediaCost() - item.getdMlFee() - item.getdMarginFee()- item.getdDspFee());                                                                                                                                                                                             
                                        if (item.getdSalesRevenue() > 0){
                                            item.setdMargin(item.getdGrossMargin() / item.getdSalesRevenue());
                                            item.setdNetMargin(item.getdNetRevenue() / item.getdSalesRevenue());
                                        }  
                                    }catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                    break;
                                case 10://TotalCost
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){ 
                                                item.setdTotalCost(Double.valueOf(nextCell.getStringCellValue()));                                                
                                            }                                                        
                                        }else if(nextCell.getCellType() == CellType.NUMERIC){
                                            item.setdTotalCost(nextCell.getNumericCellValue());
                                        }                                                                                                                                                   
                                    }catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                    break;
                                case 11://CPM
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){ 
                                                item.setdCPM(Double.valueOf(nextCell.getStringCellValue()));
                                            }                                                        
                                        }else if(nextCell.getCellType() == CellType.NUMERIC){
                                            item.setdCPM(nextCell.getNumericCellValue());
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
    
    protected List<TblDVXANDRSPD> scrap_SSP_OpenX_Format(UploadedFile itemFile, TblDailyProcess idDaily) throws FileNotFoundException, IOException{
        System.out.println("scrap_SSP_OpenX_Format");
        List<TblDVXANDRSPD> localitemsXANDR = new ArrayList();      
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
                        rowIterator.next(); // Period: 01/26/2024 00:
                        rowIterator.next(); // OrderBy: Day DESC
                        rowIterator.next(); // Day DealID DealName
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
                        item.setdMarginFee(0.00);
                        item.setdNetMargin(0.00);
                        item.setdTechFee(0.00);
                        item.setdSalesRevenue(0.00);
                        item.setvDevice("NA");
                        item.setIdDaily(idDaily);
                        item.setvDeal("");                                                    
                        item.setvBrand("");
                        item.setvAdvertiser("");                                                
                        item.setvClient("");
                        item.setvAgency("");
                        item.setvDsp("");
                        item.setvChannel("");
                        item.setvSeat("");
                        item.setvExchange("");                                                
                        while (cellIterator.hasNext() && !lbEndCol) {
                            // aqui empiezo a iterar las columnas
                            Cell nextCell = cellIterator.next();
                            
                            int columnIndex = nextCell.getColumnIndex();
                            
                            if(nextCell.getCellType() == CellType.BLANK){
                                iColBlank++;
                            }
                            switch (columnIndex) {
                                case 2://DealName
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){
                                                item.setvDeal(nextCell.getStringCellValue());   
                                                item.setvBrand(getValueBetweenColumnsPredefined(item,"BRAND"));
                                                item.setvAdvertiser(getValueBetweenColumnsPredefined(item,"ADVERTISER"));                                                
                                                item.setvClient(item.getvBrand());
                                                item.setvAgency(getValueBetweenColumnsPredefined(item,"AGENCY"));
                                                item.setvDsp(getValueBetweenColumnsPredefined(item,"DSP"));
                                                item.setvChannel(getValueBetweenColumnsPredefined(item,"CHANNEL"));
                                                item.setvSeat(getValueBetweenColumnsPredefined(item,"SEAT"));
                                                item.setvExchange(getValueBetweenColumnsPredefined(item,"EXCHANGE"));                                                  
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
                                case 3://SalesRevenue (SpendUSD)
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){  
                                                item.setdSalesRevenue(Double.valueOf(nextCell.getStringCellValue()));
                                            }
                                        }else if(nextCell.getCellType() == CellType.NUMERIC){    
                                                item.setdSalesRevenue(nextCell.getNumericCellValue());
                                        }                                                        
                                        if(item.getdSalesRevenue() != null){
                                            item.setdTechFee((item.getdSalesRevenue() * 10.00) / 100.00);
                                            item.setdCPM((item.getiImpressions() > 0) ? (1000.00 * (item.getdSalesRevenue() / item.getiImpressions())) : 0.00);
                                            item.setdDspFee((item.getdSalesRevenue() * 19.00) / 100.00);
                                            if ((item.getvSeat()!=null && item.getvSeat().contains("DATAP-ML"))){
                                                item.setdMlFee((item.getdSalesRevenue() * 10.00) / 100.00);
                                            }                                                                                                
                                        }     
                                    }catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                    break;
                                case 4://curationMargin/GrossMargin
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){ 
                                                item.setdGrossMargin(Double.valueOf(nextCell.getStringCellValue()));
                                            }
                                        }else if(nextCell.getCellType() == CellType.NUMERIC){
                                            item.setdGrossMargin(nextCell.getNumericCellValue());
                                        }        
                                                
                                                
                                                
                                        if(item.getdGrossMargin() != null && item.getdSalesRevenue() != null){
                                            item.setdMediaCost(item.getdSalesRevenue() - item.getdGrossMargin() - item.getdTechFee());
                                        }
                                        if (item.getvSeat() != null){
                                            if(item.getvSeat().contains("DATAP-EQT")){
                                                item.setdMarginFee((item.getdGrossMargin() * 7.00) / 100.00);
                                            }else if(item.getvSeat().contains("DATAP-PUB")){
                                                item.setdMarginFee((item.getdGrossMargin() * 5.00) / 100.00);
                                            }
                                        }                                                                                                                                                                                                    
                                        item.setdGrossRevenue(item.getdGrossMargin() - item.getdMlFee());
                                        item.setdTotalCost(item.getdMediaCost() + item.getdTechFee());
                                        item.setdNetRevenue(item.getdSalesRevenue() - item.getdTechFee() - item.getdMediaCost() - item.getdMlFee() - item.getdMarginFee()- item.getdDspFee());
                                        if (item.getdSalesRevenue() > 0){
                                            item.setdMargin(item.getdGrossMargin() / item.getdSalesRevenue());
                                            item.setdNetMargin(item.getdNetRevenue() / item.getdSalesRevenue());
                                        }                                              
                                    }catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                    break;
                                case 6://Impressions
                                    try{
                                        if(nextCell.getCellType() == CellType.STRING){
                                            if (!nextCell.getStringCellValue().isEmpty()){                                                
                                                item.setiImpressions(Integer.valueOf(nextCell.getStringCellValue()));
                                            }
                                        }else if(nextCell.getCellType() == CellType.NUMERIC){
                                            Double ldval = nextCell.getNumericCellValue();
                                            item.setiImpressions(ldval.intValue());
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
    
    protected boolean save_ItemsSSP(String lsFileName, List<TblDVXANDRSPD> localitemsXANDR){
        System.out.println("save_ItemsSSP "+lsFileName);
        if (localitemsXANDR != null && !localitemsXANDR.isEmpty() && !lsFileName.isEmpty()){
            try { 
                getConnection();
                PreparedStatement pstmt = connect.prepareStatement("INSERT into `tbl_raw_ssp_data` "
                                        + "(`dDate`,`vAdvertiser`,`vBrand`,`vDeal`,`vDevice`,`dGrossMargin`,`iImpressions`,`dSalesRevenue`,`dTechFee`,`dMediaCost`,`dTotalCost`,`dCPM`,`dMlFee`,`dMarginFee`,`dDspFee`,`dGrossRevenue`,`dNetRevenue`,`vClient`,`vChannel`,`vDsp`,`vAgency`,`iYear`,`iMonth`,`iDay`,`vSeat`,`vExchange`,`id_daily`,`dSystemDate`, `dMargin`, `vFileName`, `dNetMargin`)"
                                        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?);");

                for (TblDVXANDRSPD item : localitemsXANDR) {                                    
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
                    
                    num = item.getdMarginFee();
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
                    pstmt.setDouble(28, bd.doubleValue());                    
                    
                    pstmt.setString(29, lsFileName.trim());                    

                    num = item.getdNetMargin();
                    bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);
                    pstmt.setDouble(30, bd.doubleValue());                    

                    pstmt.executeUpdate();
                }                
                pstmt.close(); 
                closeConnection();
                System.out.println("items saved: " + String.valueOf(localitemsXANDR.size()));
                return true;
            } catch (Exception ex) {
            
                System.out.println("in save_ItemsSSP");
                System.out.println(ex.getMessage());
                ex.printStackTrace();                
            }        
        }
        return false;
    }          
    
    public List<TblCatalogo> getCatalogoItems(String lsSource){
        try {
                List<TblCatalogo> itemsCatalogo = new ArrayList();
                getConnection();
                PreparedStatement pstmt = connect.prepareStatement("select `id`, `vType`, `vValue`, `vPattern` from tbl_catalog where vSource = ? and `iEstado` = 1 order by `id`");            
                pstmt.setString(1, lsSource);
                ResultSet rs = pstmt.executeQuery();  
                while (rs.next()) {             
                    TblCatalogo item = new TblCatalogo();
                    item.setId(rs.getInt("id"));
                    item.setvPattern(rs.getString("vPattern"));
                    item.setvSource(lsSource);
                    item.setvType(rs.getString("vType"));
                    item.setvValue(rs.getString("vValue"));
                    item.setTblCatalogColumnList(getItemsCatalogColumnByCatalogid(item.getId()));
                    itemsCatalogo.add(item);
                }
                rs.close();
                pstmt.close();      
                closeConnection();
                return itemsCatalogo;
            } catch (Exception ex) {
                System.out.println("getCatalogItems");                                
                System.out.println(ex.getMessage());
                ex.printStackTrace();                
                return null;
            }    
    }

    public List<TblTypeSources> getCatalogoItemsTypes(String lsSource){
        try {
                List<TblTypeSources> itemsCatalogo = new ArrayList();
                getConnection();
                PreparedStatement pstmt = connect.prepareStatement("select `vType` from tbl_type_sources where `vSource` = ? and `iEstado` = 1");            
                pstmt.setString(1, lsSource);
                ResultSet rs = pstmt.executeQuery();  
                while (rs.next()) {             
                    TblTypeSources item = new TblTypeSources();
                    item.setVType(rs.getString("vType"));
                    item.setVSource(lsSource);
                    itemsCatalogo.add(item);
                }
                rs.close();
                pstmt.close();      
                closeConnection();
                return itemsCatalogo;
            } catch (Exception ex) {
                System.out.println("getCatalogoItemsTypes");                                
                System.out.println(ex.getMessage());
                ex.printStackTrace();                
                return null;
            }    
    }
        
    public List<TblCatalogoColumn> getCatalogoColumnItems(String lsSource){
        try {
                List<TblCatalogoColumn> itemsCatalogo = new ArrayList();
                getConnection();
                PreparedStatement pstmt = connect.prepareStatement("select distinct `vCategory`, `vColumnName` from tbl_catalog_column where vSource = ? and `iEstado` = 1 order by vCategory, iOrder");            
                pstmt.setString(1, lsSource);
                ResultSet rs = pstmt.executeQuery();  
                while (rs.next()) {             
                    TblCatalogoColumn item = new TblCatalogoColumn();
                    item.setvColumnName(rs.getString("vColumnName"));
                    item.setvSource(lsSource);
                    item.setvCategory(rs.getString("vCategory"));
                    itemsCatalogo.add(item);
                }
                rs.close();
                pstmt.close();      
                closeConnection();
                return itemsCatalogo;
            } catch (Exception ex) {
                System.out.println("getCatalogoColumnItems");                                
                System.out.println(ex.getMessage());
                ex.printStackTrace();                
                return null;
            }    
    }
    
    protected List<TblCatalogoColumn> getItemsCatalogColumnByCatalogid(Integer idCatalog){
        try {
            List<TblCatalogoColumn> itemsCatalogColum = new ArrayList();
            PreparedStatement pstmt = connect.prepareStatement("select `id`, `vColumnName`, `iOrder` from tbl_catalog_column where id_catalog = ? and `iEstado` = 1 order by `iOrder`");
                    pstmt.setInt(1, idCatalog);
                    ResultSet rs = pstmt.executeQuery();   
                    while (rs.next()) {             
                        TblCatalogoColumn itemColumn = new TblCatalogoColumn();
                        itemColumn.setId(rs.getInt("id"));
                        itemColumn.setvColumnName(rs.getString("vColumnName"));
                        itemColumn.setiOrder(rs.getShort("iOrder")); 
                        
                        itemsCatalogColum.add(itemColumn);
                    }
                    return itemsCatalogColum;
        } catch (Exception ex) {
            System.out.println("getItemsCatalogColumnByCatalogid");                                
            System.out.println(ex.getMessage());
            ex.printStackTrace();                
            return null;
        }
        
    }
    
    public List<TblCatalogo> getCatalogItemsActive(){
        try {
                List<TblCatalogo> itemsCatalog = new ArrayList();
                getConnection();
                PreparedStatement pstmt = connect.prepareStatement("select `id`, `vSource`, `vType`, `vValue`, `vPattern` from tbl_catalog where `iEstado` = 1");            
                ResultSet rs = pstmt.executeQuery();  
                while (rs.next()) {             
                    TblCatalogo item = new TblCatalogo();
                    item.setId(rs.getInt("id"));
                    item.setvSource(rs.getString("vSource"));
                    item.setvType(rs.getString("vType"));
                    item.setvValue(rs.getString("vValue"));
                    item.setvPattern(rs.getString("vPattern"));    
                    item.setTblCatalogColumnList(getItemsCatalogColumnByCatalogid(item.getId()));
                    itemsCatalog.add(item);
                }                
                rs.close();
                pstmt.close();      
                closeConnection();
                return itemsCatalog;
            } catch (Exception ex) {
                System.out.println("getCatalogItemsActive");                                
                System.out.println(ex.getMessage());
                ex.printStackTrace();                
                return null;
            }    
    }    
        
    public Integer getItemDailybyDate(TblDailyProcess itemDaily){
        try {
                Integer iDaily = 0;
                getConnection();
                PreparedStatement pstmt = connect.prepareStatement("select `id_daily` from tbl_daily_process where iYear = ? and iMonth = ? and iDay = ? limit 1");            
                pstmt.setInt(1, itemDaily.getiYear());
                pstmt.setInt(2, itemDaily.getiMonth());
                pstmt.setInt(3, itemDaily.getiDay());
                ResultSet rs = pstmt.executeQuery();  
                if (rs.next()) {             
                    iDaily = rs.getInt("id_daily");                    
                }
                rs.close();
                pstmt.close();   
                closeConnection();
                return iDaily;                
            } catch (Exception ex) {                
                System.out.println("getCalendarFromDatabase");
                System.out.println(ex.getMessage());
                ex.printStackTrace();   
                return 0;
            }    
    }    

    public List<String> getItemsCategories(){
        try {
                List<String> itemsCategories = new ArrayList();
                getConnection();
                PreparedStatement pstmt = connect.prepareStatement("select distinct (vType) from tbl_type_sources where iEstado = 1;");            
                ResultSet rs = pstmt.executeQuery();  
                while (rs.next()) {             
                    itemsCategories.add(rs.getString("vType"));                    
                }
                rs.close();
                pstmt.close();   
                closeConnection();
                return itemsCategories;                
            } catch (Exception ex) {                
                System.out.println("getItemsCategories");
                System.out.println(ex.getMessage());
                ex.printStackTrace();   
                return null;
            }    
    }    
    
    public List<String> getItemsColumnNames(String lsSource){
        try {
                List<String> itemsColumns = new ArrayList();
                getConnection();
                PreparedStatement pstmt = connect.prepareStatement("select vColumName from tbl_raw_columns where vSource = ? and iStatus = 1;");            
                pstmt.setString(1, lsSource);
                ResultSet rs = pstmt.executeQuery();  
                while (rs.next()) {             
                    itemsColumns.add(rs.getString("vColumName"));                    
                }
                rs.close();
                pstmt.close();   
                closeConnection();
                return itemsColumns;                
            } catch (Exception ex) {                
                System.out.println("getItemsColumnNames");
                System.out.println(ex.getMessage());
                ex.printStackTrace();   
                return null;
            }    
    } 
    
    public List<String> getColumnNamesBySourceCategory(String lsSource, String lsCategory){
        try {
                List<String> itemsColumns = new ArrayList();
                getConnection();
                PreparedStatement pstmt = connect.prepareStatement("select distinct vColumnName from tbl_catalog_column where vSource = ? and vCategory = ? and iEstado = 1 order by iOrder;");            
                pstmt.setString(1, lsSource);
                pstmt.setString(2, lsCategory);
                ResultSet rs = pstmt.executeQuery();  
                while (rs.next()) {             
                    itemsColumns.add(rs.getString("vColumnName"));                    
                }
                rs.close();
                pstmt.close();   
                closeConnection();
                return itemsColumns;                
            } catch (Exception ex) {                
                System.out.println("getColumnNamesBySourceCategory");
                System.out.println(ex.getMessage());
                ex.printStackTrace();   
                return null;
            }    
    }     
    
    public boolean cleanRawDataByDaily(Integer idDaily, String lsSource){
        try {
                getConnection();
                
                PreparedStatement pstmt_i = null, pstmt_d = null;
                
                if(lsSource.contains("DSP")){
                    pstmt_i = connect.prepareStatement("insert into tbl_raw_data_moved (`Id_raw`,`dDate`,`vPartner`,`vCampaign`,`vInsertionOrder`,`vLineItem`,`vExchange`,`vDealName`,`iImpressions`,`iClicks`,`dMediaCost`,`dTotalMediaCost`,`vDSP`,`vClient`,`vAgency`,`vChannel`,`vAlias`,`vVendor`,`vVendorSource`,`dCPM`,	`dCTR`,`dCPC`,`iAnio`,`iMes`,`iDia`,`dSystemDate`,`vFileName`,`id_daily`,`tStatus`,`vDescription`,`vUser`)\n" +
                                                                         "select `Id_raw`,`dDate`,`vPartner`,`vCampaign`,`vInsertionOrder`,`vLineItem`,`vExchange`,`vDealName`,`iImpressions`,`iClicks`,`dMediaCost`,`dTotalMediaCost`,`vDSP`,`vClient`,`vAgency`,`vChannel`,`vAlias`,`vVendor`,`vVendorSource`,`dCPM`,	`dCTR`,`dCPC`,`iAnio`,`iMes`,`iDia`,now(),`vFileName`,`id_daily`,`tStatus`,'Reprocess Data','Default' from tbl_raw_data where id_daily = ?");                

                    pstmt_d = connect.prepareStatement("delete from tbl_raw_data where `id_daily` = ?");                            
                }else{
                    pstmt_i = connect.prepareStatement("insert into tbl_raw_ssp_data_moved\n" +
                                                        "	(`Id_raw`,`dDate`,`vAdvertiser`,`vBrand`,`vDeal`,`vDevice`,`dGrossMargin`,`iImpressions`,`dSalesRevenue`,`dTechFee`,`dMediaCost`,`dTotalCost`,`dCPM`,`dMlFee`,`dMarginFee`,`dDspFee`,`dGrossRevenue`,`dNetRevenue`,`vClient`,`vChannel`,`vDsp`,`vAgency`,`iYear`,`iMonth`,`iDay`,`vSeat`,`vExchange`,`dMargin`,`dNetMargin`,`vUser`,`dSystemDate`,`dModifiedDate`,`vFileName`,`id_daily`,`tEstado`,`vDescription`)\n" +
                                                        "select `Id_raw`,`dDate`,`vAdvertiser`,`vBrand`,`vDeal`,`vDevice`,`dGrossMargin`,`iImpressions`,`dSalesRevenue`,`dTechFee`,`dMediaCost`,`dTotalCost`,`dCPM`,`dMlFee`,`dMarginFee`,`dDspFee`,`dGrossRevenue`,`dNetRevenue`,`vClient`,`vChannel`,`vDsp`,`vAgency`,`iYear`,`iMonth`,`iDay`,`vSeat`,`vExchange`,`dMargin`,`dNetMargin`,'Default',now(),`dModifiedDate`,`vFileName`,`id_daily`,`tEstado`,'Reprocess Data' from tbl_raw_ssp_data where id_daily = ?");                

                    pstmt_d = connect.prepareStatement("delete from tbl_raw_ssp_data where `id_daily` = ?");                  
                }
                
                pstmt_i.setInt(1, idDaily);
                pstmt_i.executeUpdate();  
                pstmt_d.setInt(1, idDaily);
                pstmt_d.executeUpdate();                 
                pstmt_i.close();   
                pstmt_d.close(); 
                
                closeConnection();
                System.out.println("cleanned RawData");
                return true;
            } catch (Exception ex) {
                System.out.println("cleanRawDataByDaily");
                System.out.println(ex.getMessage());
                ex.printStackTrace();                
            }   
        return false;
    }

    public boolean cleanMonthlyRawData(List<TblDV360SPD> itemsToClean){
        try {
                getConnection();                  
                PreparedStatement pstmt_i = connect.prepareStatement("insert into tbl_raw_data_moved (`Id_raw`,`dDate`,`vPartner`,`vCampaign`,`vInsertionOrder`,`vLineItem`,`vExchange`,`vDealName`,`iImpressions`,`iClicks`,`dMediaCost`,`dTotalMediaCost`,`vDSP`,`vClient`,`vAgency`,`vChannel`,`vAlias`,`vVendor`,`vVendorSource`,`dCPM`,	`dCTR`,`dCPC`,`iAnio`,`iMes`,`iDia`,`dSystemDate`,`vFileName`,`id_daily`,`tStatus`,`vDescription`,`vUser`)\n" +
                                                                     "select `Id_raw`,`dDate`,`vPartner`,`vCampaign`,`vInsertionOrder`,`vLineItem`,`vExchange`,`vDealName`,`iImpressions`,`iClicks`,`dMediaCost`,`dTotalMediaCost`,`vDSP`,`vClient`,`vAgency`,`vChannel`,`vAlias`,`vVendor`,`vVendorSource`,`dCPM`,	`dCTR`,`dCPC`,`iAnio`,`iMes`,`iDia`,now(),`vFileName`,`id_daily`,`tStatus`,'Monthly Replacement','Default' from tbl_raw_data where id_raw = ?");
                PreparedStatement pstmt_d = connect.prepareStatement("delete from tbl_raw_data where `Id_raw` = ?"); 
                
                for (TblDV360SPD item : itemsToClean) {

                    pstmt_i.setInt(1, item.getId());
                    pstmt_i.executeUpdate();   
                    
                    pstmt_d.setInt(1, item.getId());
                    pstmt_d.executeUpdate();                       

                }
                                
                pstmt_i.close();   
                pstmt_d.close(); 
                closeConnection();
                System.out.println("cleanned RawData");
                return true;
            } catch (Exception ex) {
                System.out.println("cleanMonthlyRawData");
                System.out.println(ex.getMessage());
                ex.printStackTrace();                
            }   
        return false;
    }    

    public boolean cleanMonthlyRawSSPData(List<TblDVXANDRSPD> itemsToClean){
        try {
                getConnection();

                PreparedStatement pstmt = connect.prepareStatement("delete from tbl_raw_ssp_data where `Id_raw` = ?");                        
                
                for (TblDVXANDRSPD item : itemsToClean) {
                    pstmt.setInt(1, item.getId());
                    pstmt.executeUpdate();                                  
                }
                                
                pstmt.close();   
                closeConnection();
                System.out.println("cleanned RawData");
                return true;
            } catch (Exception ex) {
                System.out.println("cleanMonthlyRawSSPData");
                System.out.println(ex.getMessage());
                ex.printStackTrace();                
            }   
        return false;
    }  
    
    public List<TblDV360SPD> getRawDataPattern(Integer iDaily, String lsPattern){
        try {
            getConnection(); 
            lsPattern = lsPattern.toLowerCase();
            PreparedStatement pstmt = connect.prepareStatement("select `Id_raw`, `tbl_raw_data`.`dDate`, `vPartner`, `vCampaign`, `vInsertionOrder`, `vLineItem`, `vExchange`, `vDealName`, `iImpressions`, `iClicks`, `dMediaCost`, `dTotalMediaCost`, `vDSP`,\n" +
                                                            "	`vClient`, `vAgency`, `vChannel`, `vAlias`, `vVendor`, `vVendorSource`,	`dCPM`, `dCTR`, `dCPC`, `tbl_daily_process`.`iYear`, `tbl_daily_process`.`iMonth`, `tbl_daily_process`.`iDay`, `vFileName`, `tbl_raw_data`.`id_daily`, `tbl_daily_process`.`dDate` as dateProcess\n" +
                                                            "from `tbl_raw_data`, `tbl_daily_process`\n" +
                                                            "where `tbl_raw_data`.`id_daily` = `tbl_daily_process`.`id_daily` and\n" +
                                                            "	`tbl_raw_data`.`tStatus` = 1 and `tbl_daily_process`.`id_daily` = ? and\n" +
                                                            "(lower(vPartner) like '%" + lsPattern + "%' or lower(vCampaign) like '%" + lsPattern + "%' or lower(vInsertionOrder) like '%" + lsPattern + "%' or lower(vLineItem) like '%" + lsPattern + "%' or lower(vExchange) like '%" + lsPattern + "%' or lower(vDealName) like '%" + lsPattern + "%' or lower(vDSP) like '%" + lsPattern + "%' or lower(vClient) like '%" + lsPattern + "%' or lower(vAgency) like '%" + lsPattern + "%' or lower(vChannel) like '%" + lsPattern + "%' or lower(vAlias) like '%" + lsPattern + "%' or lower(vVendor) like '%" + lsPattern + "%')"); 
            pstmt.setInt(1, iDaily);
            ResultSet rs = pstmt.executeQuery();  
            List<TblDV360SPD> itemsLocalDV360 = new ArrayList();
            while (rs.next()) {             
                TblDailyProcess itemDaily = new TblDailyProcess();
                itemDaily.setId_daily(rs.getInt("id_daily"));
                itemDaily.setdDate(rs.getDate("dateProcess"));                    
                TblDV360SPD item = new TblDV360SPD();

                item.setIdDaily(itemDaily);
                item.setId(rs.getInt("Id_raw"));
                item.setdDate(rs.getDate("dateProcess"));
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
                
                itemsLocalDV360.add(item);
            }
            rs.close();
            pstmt.close();                                 
            closeConnection();    
            return itemsLocalDV360;
        } catch (Exception ex) {            
            System.out.println("getRawDatabyDate");
            System.out.println(ex.getMessage());
            ex.printStackTrace();                
        }
        return null;
    }    
    
    public List<TblDV360SPD> getRawDatabyMonth(Integer iYear, Integer iMonth){
        try {
            getConnection(); 
            PreparedStatement pstmt = connect.prepareStatement("select `Id_raw`, `tbl_raw_data`.`dDate`, `vPartner`, `vCampaign`, `vInsertionOrder`, `vLineItem`, `vExchange`, `vDealName`,	`iImpressions`, `iClicks`, `dMediaCost`, `dTotalMediaCost`, `vDSP`,\n" +
                                                            "	`vClient`, `vAgency`, `vChannel`, `vAlias`, `vVendor`, `vVendorSource`,	`dCPM`, `dCTR`, `dCPC`, `tbl_daily_process`.`iYear`, `tbl_daily_process`.`iMonth`, `tbl_daily_process`.`iDay`, `vFileName`, `tbl_raw_data`.`id_daily`, `tbl_daily_process`.`dDate` as dateProcess\n" +
                                                            "from `tbl_raw_data`, `tbl_daily_process`\n" +
                                                            "where `tbl_raw_data`.`id_daily` = `tbl_daily_process`.`id_daily` and\n" +
                                                            "	`tbl_raw_data`.`tStatus` = 1 and `tbl_daily_process`.`iYear` =  ? and `tbl_daily_process`.`iMonth` = ?"); 
            pstmt.setInt(1, iYear);
            pstmt.setInt(2, iMonth);
            
            ResultSet rs = pstmt.executeQuery();  
            List<TblDV360SPD> itemsLocalDV360 = new ArrayList();
            while (rs.next()) {             
                TblDailyProcess itemDaily = new TblDailyProcess();
                itemDaily.setId_daily(rs.getInt("id_daily"));
                itemDaily.setdDate(rs.getDate("dateProcess"));                    
                TblDV360SPD item = new TblDV360SPD();

                item.setIdDaily(itemDaily);
                item.setId(rs.getInt("Id_raw"));
                item.setdDate(rs.getDate("dateProcess"));
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
                item.setvAgency((rs.getString("vAgency") != null) ? rs.getString("vAgency") :"");
                item.setvAlias((rs.getString("vAlias") != null) ? rs.getString("vAlias") :"");
                item.setvCampaign((rs.getString("vCampaign") != null) ? rs.getString("vCampaign") :"");
                item.setvChannel((rs.getString("vChannel") != null) ? rs.getString("vChannel") :"");
                item.setvClient((rs.getString("vClient") != null) ? rs.getString("vClient") :"");
                item.setvDSP((rs.getString("vDsp") != null) ? rs.getString("vDsp") :"");
                item.setvDealName((rs.getString("vDealName") != null) ? rs.getString("vDealName") :"");
                item.setvExchange((rs.getString("vExchange") != null) ? rs.getString("vExchange") :"");
                item.setvInsertionOrder((rs.getString("vInsertionOrder") != null) ? rs.getString("vInsertionOrder") :"");
                item.setvLineItem((rs.getString("vLineItem") != null) ? rs.getString("vLineItem") :"");
                item.setvPartner((rs.getString("vPartner") != null) ? rs.getString("vPartner") :"");
                item.setvVendor((rs.getString("vVendor") != null) ? rs.getString("vVendor") :"");
                item.setvVendorSource((rs.getString("vVendorSource") != null) ? rs.getString("vVendorSource") :"");
                
                itemsLocalDV360.add(item);
            }
            rs.close();
            pstmt.close();                                 
            closeConnection();    
            return itemsLocalDV360;
        } catch (Exception ex) {            
            System.out.println("getRawDatabyDate");
            System.out.println(ex.getMessage());
            ex.printStackTrace();                
        }
        return null;
    }  

    public List<TblDV360SPD> getRawDatabyDate(Integer iDaily){
        try {
            getConnection(); 
            PreparedStatement pstmt = connect.prepareStatement("select `Id_raw`, `tbl_raw_data`.`dDate`, `vPartner`, `vCampaign`, `vInsertionOrder`, `vLineItem`, `vExchange`, `vDealName`,	`iImpressions`, `iClicks`, `dMediaCost`, `dTotalMediaCost`, `vDSP`,\n" +
                                                            "	`vClient`, `vAgency`, `vChannel`, `vAlias`, `vVendor`, `vVendorSource`,	`dCPM`, `dCTR`, `dCPC`, `tbl_daily_process`.`iYear`, `tbl_daily_process`.`iMonth`, `tbl_daily_process`.`iDay`, `vFileName`, `tbl_raw_data`.`id_daily`, `tbl_daily_process`.`dDate` as dateProcess\n" +
                                                            "from `tbl_raw_data`, `tbl_daily_process`\n" +
                                                            "where `tbl_raw_data`.`id_daily` = `tbl_daily_process`.`id_daily` and\n" +
                                                            "	`tbl_raw_data`.`tStatus` = 1 and `tbl_daily_process`.`id_daily` =  ?"); 
            pstmt.setInt(1, iDaily);
            ResultSet rs = pstmt.executeQuery();  
            List<TblDV360SPD> itemsLocalDV360 = new ArrayList();
            while (rs.next()) {             
                TblDailyProcess itemDaily = new TblDailyProcess();
                itemDaily.setId_daily(rs.getInt("id_daily"));
                itemDaily.setdDate(rs.getDate("dateProcess"));                    
                TblDV360SPD item = new TblDV360SPD();

                item.setIdDaily(itemDaily);
                item.setId(rs.getInt("Id_raw"));
                item.setdDate(rs.getDate("dateProcess"));
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
                item.setvAgency((rs.getString("vAgency") != null) ? rs.getString("vAgency") :"");
                item.setvAlias((rs.getString("vAlias") != null) ? rs.getString("vAlias") :"");
                item.setvCampaign((rs.getString("vCampaign") != null) ? rs.getString("vCampaign") :"");
                item.setvChannel((rs.getString("vChannel") != null) ? rs.getString("vChannel") :"");
                item.setvClient((rs.getString("vClient") != null) ? rs.getString("vClient") :"");
                item.setvDSP((rs.getString("vDsp") != null) ? rs.getString("vDsp") :"");
                item.setvDealName((rs.getString("vDealName") != null) ? rs.getString("vDealName") :"");
                item.setvExchange((rs.getString("vExchange") != null) ? rs.getString("vExchange") :"");
                item.setvInsertionOrder((rs.getString("vInsertionOrder") != null) ? rs.getString("vInsertionOrder") :"");
                item.setvLineItem((rs.getString("vLineItem") != null) ? rs.getString("vLineItem") :"");
                item.setvPartner((rs.getString("vPartner") != null) ? rs.getString("vPartner") :"");
                item.setvVendor((rs.getString("vVendor") != null) ? rs.getString("vVendor") :"");
                item.setvVendorSource((rs.getString("vVendorSource") != null) ? rs.getString("vVendorSource") :"");
                
                itemsLocalDV360.add(item);
            }
            rs.close();
            pstmt.close();                                 
            closeConnection();    
            return itemsLocalDV360;
        } catch (Exception ex) {            
            System.out.println("getRawDatabyDate");
            System.out.println(ex.getMessage());
            ex.printStackTrace();                
        }
        return null;
    }   
    
    public List<TblDVXANDRSPD> getRawSSPDatabyDate(Integer iDaily){
        try {
            getConnection(); 
            PreparedStatement pstmt = connect.prepareStatement("select `Id_raw`,  `tbl_raw_ssp_data`.`dDate`, `vAdvertiser`, `vBrand`, `vDeal`, `vDevice`, `dGrossMargin`, `iImpressions`, `dSalesRevenue`, `dTechFee`, `dMediaCost`, `dTotalCost`, `dCPM`, `dMlFee`, `dMarginFee`, `dDspFee`, `dGrossRevenue`, `dNetRevenue`,	`vClient`, `vChannel`, `vDsp`, `vAgency`, `tbl_daily_process`.`iYear`, `tbl_daily_process`.`iMonth`, `tbl_daily_process`.`iDay`, `vSeat`, `vExchange`, `dMargin`, `dNetMargin`, `vUser`, `dSystemDate`, `dModifiedDate`, `vFileName`, `tbl_raw_ssp_data`.`id_daily`, `tbl_daily_process`.`dDate` as dateProcess, `tEstado`\n" +
                                                                "from `tbl_raw_ssp_data` , `tbl_daily_process`\n" +
                                                                "where `tbl_raw_ssp_data`.`id_daily` = `tbl_daily_process`.`id_daily` and\n" +
                                                                "`tbl_raw_ssp_data`.`tEstado` = 1 and `tbl_daily_process`.`id_daily` = ?"); 
            pstmt.setInt(1, iDaily);
            ResultSet rs = pstmt.executeQuery();  
            List<TblDVXANDRSPD> itemsXandr = new ArrayList();
            while (rs.next()) {             
                TblDailyProcess itemDaily = new TblDailyProcess();
                itemDaily.setId_daily(rs.getInt("id_daily"));
                itemDaily.setdDate(rs.getDate("dateProcess"));                    
                TblDVXANDRSPD item = new TblDVXANDRSPD();

                item.setIdDaily(itemDaily);
                item.setId(rs.getInt("Id_raw"));
                item.setdDate(rs.getDate("dateProcess"));
                item.setdCPM(rs.getDouble("dCPM"));                
                item.setdMediaCost(rs.getDouble("dMediaCost"));
                item.setdTotalCost(rs.getDouble("dTotalCost"));
                item.setdGrossMargin(rs.getDouble("dGrossMargin"));
                item.setdSalesRevenue(rs.getDouble("dSalesRevenue"));
                item.setdTechFee(rs.getDouble("dTechFee"));
                item.setdMarginFee(rs.getDouble("dMarginFee"));                
                item.setdMlFee(rs.getDouble("dMlFee"));
                item.setdMargin(rs.getDouble("dMargin"));
                item.setdNetMargin(rs.getDouble("dNetMargin"));
                item.setdDspFee(rs.getDouble("dDspFee"));
                item.setdGrossRevenue(rs.getDouble("dGrossRevenue"));
                item.setdNetRevenue(rs.getDouble("dNetRevenue"));
                item.setiYear(rs.getInt("iYear"));
                item.setiDay(rs.getInt("iDay"));
                item.setiImpressions(rs.getInt("iImpressions"));
                item.setiMonth(rs.getInt("iMonth"));
                item.setvAgency((rs.getString("vAgency") != null) ? rs.getString("vAgency") :"");
                item.setvSeat((rs.getString("vSeat") != null) ? rs.getString("vSeat") :"");
                item.setvChannel((rs.getString("vChannel") != null) ? rs.getString("vChannel") :"");
                item.setvClient((rs.getString("vClient") != null) ? rs.getString("vClient") :"");
                item.setvDsp((rs.getString("vDsp") != null) ? rs.getString("vDsp") :"");
                item.setvDeal((rs.getString("vDeal") != null) ? rs.getString("vDeal") :"");
                item.setvExchange((rs.getString("vExchange") != null) ? rs.getString("vExchange") :"");
                item.setvAdvertiser((rs.getString("vAdvertiser") != null) ? rs.getString("vAdvertiser") :"");
                item.setvBrand((rs.getString("vBrand") != null) ? rs.getString("vBrand") :"");
                item.setvDevice((rs.getString("vDevice") != null) ? rs.getString("vDevice") :"");
                
                itemsXandr.add(item);
            }
            rs.close();
            pstmt.close();                                 
            closeConnection();     
            return itemsXandr;
        } catch (Exception ex) {
            System.out.println("getRawDatabyDate");
            System.out.println(ex.getMessage());
            ex.printStackTrace();                
        }
        return null;
    }        
    
    public Integer createItemDaily(TblDailyProcess itemDaily){
        try {
                Integer iDaily = 0;
                getConnection();
                PreparedStatement pstmt = connect.prepareStatement("INSERT INTO tbl_daily_process (`iYear`, `iMonth`, `iDay`, `dDate`, `iStatusProcess`, `iQuarter`, `iWeek`, `vDayName`, `vMonthName`, `iSHoliday`, `iSWeekend`, `iStatus`)\n" +
                                                                    "VALUES (?,?,?,?,1,QUARTER(?),WEEKOFYEAR(?),DATE_FORMAT(?,'%W'),DATE_FORMAT(?,'%M'),0,CASE DAYOFWEEK(?) WHEN 1 THEN 1 WHEN 7 then 1 ELSE 0 END,1);", Statement.RETURN_GENERATED_KEYS);            
                pstmt.setInt(1, itemDaily.getiYear());
                pstmt.setInt(2, itemDaily.getiMonth());
                pstmt.setInt(3, itemDaily.getiDay());
                pstmt.setDate(4, itemDaily.getdDate());
                pstmt.setDate(5, itemDaily.getdDate());
                pstmt.setDate(6, itemDaily.getdDate());
                pstmt.setDate(7, itemDaily.getdDate());
                pstmt.setDate(8, itemDaily.getdDate());
                pstmt.setDate(9, itemDaily.getdDate());         
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {             
                    iDaily = rs.getInt("GENERATED_KEY");
                }
                rs.close();
                pstmt.close();   
                closeConnection();
                return iDaily;                
            } catch (Exception ex) {                
                System.out.println("createItemDaily");
                System.out.println(ex.getMessage());
                ex.printStackTrace();   
                return 0;
            }    
    }      

    public boolean createItemCatalog(TblCatalog itemCatalog){
        try {
                getConnection();
                PreparedStatement pstmt = connect.prepareStatement("insert into `tbl_catalog` (`vSource`, `vType`, `vValue`, `vPattern`, `dSystemDate`, `iEstado`) VALUES (?,?,?,?,now(),1);");            
                pstmt.setString(1, itemCatalog.getVSource());
                pstmt.setString(2, itemCatalog.getVType());
                pstmt.setString(3, itemCatalog.getVValue());
                pstmt.setString(4, itemCatalog.getVPattern());
                pstmt.executeUpdate();
                pstmt.close();   
                closeConnection();
                return true;                
            } catch (Exception ex) {                
                System.out.println("createItemCatalog");
                System.out.println(ex.getMessage());
                ex.printStackTrace();   
            }    
        return false;
    }          

    public boolean createItemCatalogColumnsRelated(TblCatalogo itemCatalog, String[] selectedrawColumns){
        try {
                getConnection();
                itemCatalog.setId(0);
                PreparedStatement pstmt = connect.prepareStatement("insert into `tbl_catalog` (`vSource`, `vType`, `vValue`, `vPattern`, `dSystemDate`, `iEstado`) VALUES (?,?,?,?,now(),1);", Statement.RETURN_GENERATED_KEYS);            
                pstmt.setString(1, itemCatalog.getvSource());
                pstmt.setString(2, itemCatalog.getvType());
                pstmt.setString(3, itemCatalog.getvValue());
                pstmt.setString(4, itemCatalog.getvPattern());
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {             
                    itemCatalog.setId(rs.getInt("GENERATED_KEY"));                
                    int i = 1;
                    pstmt = connect.prepareStatement("insert into `tbl_catalog_column` (`id_catalog`, `vColumnName`, `iOrder`, `dSystemDate`, `iEstado`) VALUES (?,?,?,now(),1);");                            
                    for(String itemColumn: selectedrawColumns){
                        pstmt.setInt(1, itemCatalog.getId());
                        pstmt.setString(2, itemColumn);
                        pstmt.setInt(3, i++);
                        pstmt.executeUpdate();                                        
                    }                
                }
                pstmt.close();   
                closeConnection();
                return true;                
            } catch (Exception ex) {                
                System.out.println("createItemCatalogColumnsRelated");
                System.out.println(ex.getMessage());
                ex.printStackTrace();   
            }    
        return false;
    }          

    public boolean updateItemCatalogColumnsRelated(TblCatalogo itemCatalog, String[] selectedrawColumns){
        try {
                getConnection();
                PreparedStatement pstmt = connect.prepareStatement("update `tbl_catalog`\n"
                                                                + "set `vSource` = ?, `vType` = ?, `vValue` = ?, `vPattern` = ?, `dSystemDate` = now()\n"
                                                                + "where id = ?");            
                pstmt.setString(1, itemCatalog.getvSource());
                pstmt.setString(2, itemCatalog.getvType());
                pstmt.setString(3, itemCatalog.getvValue());
                pstmt.setString(4, itemCatalog.getvPattern());
                pstmt.setInt(5, itemCatalog.getId());
                pstmt.executeUpdate();

                /*delete currently rows*/
                pstmt = connect.prepareStatement("delete from `tbl_catalog_column` where `id_catalog` = ?;");   
                pstmt.setInt(1, itemCatalog.getId());
                pstmt.executeUpdate();
                /*add new ones instead*/
                int i = 1;
                pstmt = connect.prepareStatement("insert into `tbl_catalog_column` (`id_catalog`, `vColumnName`, `iOrder`, `dSystemDate`, `iEstado`) VALUES (?,?,?,now(),1);");                            
                for(String itemColumn: selectedrawColumns){
                    pstmt.setInt(1, itemCatalog.getId());
                    pstmt.setString(2, itemColumn);
                    pstmt.setInt(3, i++);
                    pstmt.executeUpdate();                                        
                }                
                
                pstmt.close();   
                closeConnection();
                return true;                
            } catch (Exception ex) {                
                System.out.println("updateItemCatalogColumnsRelated");
                System.out.println(ex.getMessage());
                ex.printStackTrace();   
            }    
        return false;
    }        

    protected String setRefactorValueBetweenColumns(TblDV360SPD item, String[] selectedrawColumns, TblCatalogo editCatalog){
        String lsRet="";            
        for (String itemString : selectedrawColumns) {
                switch(itemString){                    
                    case "vPartner":
                        if (item.getvPartner().toUpperCase().contains(editCatalog.getvPattern().toUpperCase())) lsRet = editCatalog.getvValue();
                        break;
                    case "vCampaign":
                        if (item.getvCampaign().toUpperCase().contains(editCatalog.getvPattern().toUpperCase())) lsRet = editCatalog.getvValue();
                        break;
                    case "vInsertionOrder":
                        if (item.getvInsertionOrder().toUpperCase().contains(editCatalog.getvPattern().toUpperCase())) lsRet = editCatalog.getvValue();
                        break;
                    case "vLineItem":
                        if (item.getvLineItem().toUpperCase().contains(editCatalog.getvPattern().toUpperCase())) lsRet = editCatalog.getvValue();                        
                        break;
                    case "vExchange":
                        if (item.getvExchange().toUpperCase().contains(editCatalog.getvPattern().toUpperCase())) lsRet = editCatalog.getvValue();                                                
                        break;
                    case "vDealName":
                        if (item.getvDealName().toUpperCase().contains(editCatalog.getvPattern().toUpperCase())) lsRet = editCatalog.getvValue();                        
                        break;
                    case "vClient":
                        if (item.getvClient().toUpperCase().contains(editCatalog.getvPattern().toUpperCase())) lsRet = editCatalog.getvValue();                                                
                        break;
                }

                if(!lsRet.isEmpty()) break;
        }        
        return lsRet;
    }    

    protected String setRefactorValueBetweenColumns(TblDVXANDRSPD item, String[] selectedrawColumns, TblCatalogo editCatalog){
        String lsRet="";            
        for (String itemString : selectedrawColumns) {
                switch(itemString){                    
                    case "vAdvertiser":
                        if (item.getvAdvertiser().toUpperCase().contains(editCatalog.getvPattern().toUpperCase())) lsRet = editCatalog.getvValue();
                        break;
                    case "vBrand":
                        if (item.getvBrand().toUpperCase().contains(editCatalog.getvPattern().toUpperCase())) lsRet = editCatalog.getvValue();
                        break;
                    case "vDeal":
                        if (item.getvDeal().toUpperCase().contains(editCatalog.getvPattern().toUpperCase())) lsRet = editCatalog.getvValue();
                        break;
                    case "vDevice":
                        if (item.getvDevice().toUpperCase().contains(editCatalog.getvPattern().toUpperCase())) lsRet = editCatalog.getvValue();                        
                        break;
                    case "vSeat":
                        if (item.getvSeat().toUpperCase().contains(editCatalog.getvPattern().toUpperCase())) lsRet = editCatalog.getvValue();                                                
                        break;
                }

                if(!lsRet.isEmpty()) break;
        }        
        return lsRet;
    }
    
    public boolean refactorRawSSPData(Integer idDaily, TblCatalogo editCatalog, String[] selectedrawColumns){
        try { 
            getConnection();
            PreparedStatement pstmt;
            switch(editCatalog.getvType()){
                case "BRAND":
                    pstmt = connect.prepareStatement("update tbl_raw_ssp_data \n" +
                                                    "set vBrand = ? \n" +
                                                    "where id_raw = ?;");                      
                    for (TblDVXANDRSPD item : itemsXANDRRefactor) {                
                        item.setvBrand(setRefactorValueBetweenColumns(item,selectedrawColumns,editCatalog));              
                        if(!item.getvBrand().isEmpty()){
                            pstmt.setString(1, item.getvBrand());  
                            pstmt.setInt(2, item.getId());  
                            pstmt.executeUpdate();                                                                          
                        }                                                                            
                    }  
                    pstmt.close();                    
                    break;
                case "ADVERTISER":
                    pstmt = connect.prepareStatement("update tbl_raw_ssp_data \n" +
                                                    "set vAdvertiser = ? \n" +
                                                    "where id_raw = ?;");                      
                    for (TblDVXANDRSPD item : itemsXANDRRefactor) {                
                        item.setvAdvertiser(setRefactorValueBetweenColumns(item, selectedrawColumns,editCatalog));
                        if(!item.getvAdvertiser().isEmpty()){
                            pstmt.setString(1, item.getvAdvertiser());  
                            pstmt.setInt(2, item.getId());  
                            pstmt.executeUpdate();                                                                              
                        }                        
                    }      
                    pstmt.close();                    
                    break;
                case "AGENCY":
                    pstmt = connect.prepareStatement("update tbl_raw_ssp_data \n" +
                                                    "set vAgency = ? \n" +
                                                    "where id_raw = ?;");                      
                    for (TblDVXANDRSPD item : itemsXANDRRefactor) {                
                        item.setvAgency(setRefactorValueBetweenColumns(item, selectedrawColumns,editCatalog));
                        if(!item.getvAgency().isEmpty()){
                            pstmt.setString(1, item.getvAgency());  
                            pstmt.setInt(2, item.getId());  
                            pstmt.executeUpdate();                                                                         
                        }                                                            
                    }         
                    pstmt.close();                    
                    break;
                case "DSP":
                    pstmt = connect.prepareStatement("update tbl_raw_ssp_data \n" +
                                                    "set vDsp = ? \n" +
                                                    "where id_raw = ?;");                     
                    for (TblDVXANDRSPD item : itemsXANDRRefactor) {                
                        item.setvDsp(setRefactorValueBetweenColumns(item, selectedrawColumns,editCatalog));
                        if(!item.getvDsp().isEmpty()){
                            pstmt.setString(1, item.getvDsp());  
                            pstmt.setInt(2, item.getId());  
                            pstmt.executeUpdate();                                                                         
                        }                                                              
                    }     
                    pstmt.close();                    
                    break;
                case "CHANNEL":
                    pstmt = connect.prepareStatement("update tbl_raw_ssp_data \n" +
                                                    "set vChannel = ? \n" +
                                                    "where id_raw = ?;");                                         
                    for (TblDVXANDRSPD item : itemsXANDRRefactor) {                
                        item.setvChannel(setRefactorValueBetweenColumns(item, selectedrawColumns,editCatalog));
                        if(!item.getvChannel().isEmpty()){
                            pstmt.setString(1, item.getvChannel());  
                            pstmt.setInt(2, item.getId());  
                            pstmt.executeUpdate();                                                                         
                        }                                                                                     
                    }            
                    pstmt.close();                    
                    break;
                case "SEAT":
                    pstmt = connect.prepareStatement("update tbl_raw_ssp_data \n" +
                                                    "set vSeat = ? \n" +
                                                    "where id_raw = ?;");                           
                    for (TblDVXANDRSPD item : itemsXANDRRefactor) {                
                        item.setvSeat(setRefactorValueBetweenColumns(item, selectedrawColumns,editCatalog));
                        if(!item.getvSeat().isEmpty()){
                            pstmt.setString(1, item.getvSeat());  
                            pstmt.setInt(2, item.getId());  
                            pstmt.executeUpdate();                                                                         
                        }                                                                                    
                    }                  
                    pstmt.close();                    
                    break;
                case "EXCHANGE":                                           
                    pstmt = connect.prepareStatement("update tbl_raw_ssp_data \n" +
                                                    "set vExchange = ? \n" +
                                                    "where id_raw = ?;");                     
                    for (TblDVXANDRSPD item : itemsXANDRRefactor) {                
                        item.setvExchange(setRefactorValueBetweenColumns(item,selectedrawColumns,editCatalog)); 
                        if(!item.getvExchange().isEmpty()){
                            pstmt.setString(1, item.getvExchange());  
                            pstmt.setInt(2, item.getId());  
                            pstmt.executeUpdate();                                                                         
                        }                                                                                        
                    }                    
                    pstmt.close();
                    break;
                case "DEAL":                                           
                    pstmt = connect.prepareStatement("update tbl_raw_ssp_data \n" +
                                                    "set vDeal = ? \n" +
                                                    "where id_raw = ?;");                     
                    for (TblDVXANDRSPD item : itemsXANDRRefactor) {                
                        item.setvDeal(setRefactorValueBetweenColumns(item,selectedrawColumns,editCatalog)); 
                        if(!item.getvDeal().isEmpty()){
                            pstmt.setString(1, item.getvDeal());  
                            pstmt.setInt(2, item.getId());  
                            pstmt.executeUpdate();                                                                         
                        }                                                                                        
                    }                    
                    pstmt.close();
                    break;
            }                                                                                                                                              
            closeConnection(); 
            System.out.println("items SSP refactored");
            return true;
        } catch (Exception ex) {            
            System.out.println("refactorRawSSPData");
            System.out.println(ex.getMessage());
            ex.printStackTrace();                
        }                                 
        return false;
    }    
    
    public boolean refactorRawData(Integer idDaily, TblCatalogo editCatalog, String[] selectedrawColumns){
        try { 
            getConnection();
            PreparedStatement pstmt;
            switch(editCatalog.getvType()){
                case "DSP":
                    pstmt = connect.prepareStatement("update tbl_raw_data \n" +
                                                    "set vDSP = ? \n" +
                                                    "where id_raw = ?;");                      
                    for (TblDV360SPD item : itemsDV360Refactor) {                
                        item.setvDSP(setRefactorValueBetweenColumns(item,selectedrawColumns,editCatalog));              
                        if(!item.getvDSP().isEmpty()){
                            pstmt.setString(1, item.getvDSP());  
                            pstmt.setInt(2, item.getId());  
                            pstmt.executeUpdate();                                                                          
                        }                                                                            
                    }  
                    pstmt.close();                    
                    break;
                case "CLIENT":
                    pstmt = connect.prepareStatement("update tbl_raw_data \n" +
                                                    "set vClient = ? \n" +
                                                    "where id_raw = ?;");                      
                    for (TblDV360SPD item : itemsDV360Refactor) {                
                        item.setvClient(setRefactorValueBetweenColumns(item, selectedrawColumns,editCatalog));
                        if(!item.getvClient().isEmpty()){
                            pstmt.setString(1, item.getvClient());  
                            pstmt.setInt(2, item.getId());  
                            pstmt.executeUpdate();                                                                              
                        }                        
                    }      
                    pstmt.close();                    
                    break;
                case "AGENCY":
                    pstmt = connect.prepareStatement("update tbl_raw_data \n" +
                                                    "set vAgency = ? \n" +
                                                    "where id_raw = ?;");                      
                    for (TblDV360SPD item : itemsDV360Refactor) {                
                        item.setvAgency(setRefactorValueBetweenColumns(item, selectedrawColumns,editCatalog));
                        if(!item.getvAgency().isEmpty()){
                            pstmt.setString(1, item.getvAgency());  
                            pstmt.setInt(2, item.getId());  
                            pstmt.executeUpdate();                                                                         
                        }                                                            
                    }         
                    pstmt.close();                    
                    break;
                case "CHANNEL":
                    pstmt = connect.prepareStatement("update tbl_raw_data \n" +
                                                    "set vChannel = ? \n" +
                                                    "where id_raw = ?;");                     
                    for (TblDV360SPD item : itemsDV360Refactor) {                
                        item.setvChannel(setRefactorValueBetweenColumns(item, selectedrawColumns,editCatalog));
                        if(!item.getvChannel().isEmpty()){
                            pstmt.setString(1, item.getvChannel());  
                            pstmt.setInt(2, item.getId());  
                            pstmt.executeUpdate();                                                                         
                        }                                                              
                    }     
                    pstmt.close();                    
                    break;
                case "VENDOR":
                    pstmt = connect.prepareStatement("update tbl_raw_data \n" +
                                                    "set vVendor = ? \n" +
                                                    "where id_raw = ?;");                                         
                    for (TblDV360SPD item : itemsDV360Refactor) {                
                        item.setvChannel(setRefactorValueBetweenColumns(item, selectedrawColumns,editCatalog));
                        if(!item.getvVendor().isEmpty()){
                            pstmt.setString(1, item.getvVendor());  
                            pstmt.setInt(2, item.getId());  
                            pstmt.executeUpdate();                                                                         
                        }                                                                                     
                    }            
                    pstmt.close();                    
                    break;
                case "DEALNAME":
                    pstmt = connect.prepareStatement("update tbl_raw_data \n" +
                                                    "set vDealName = ? \n" +
                                                    "where id_raw = ?;");                                         
                    for (TblDV360SPD item : itemsDV360Refactor) {                
                        item.setvDealName(setRefactorValueBetweenColumns(item, selectedrawColumns,editCatalog));
                        if(!item.getvDealName().isEmpty()){
                            pstmt.setString(1, item.getvDealName());  
                            pstmt.setInt(2, item.getId());  
                            pstmt.executeUpdate();                                                                         
                        }                                                                                     
                    }            
                    pstmt.close();                    
                    break;
            }                                                                                                                                              
            closeConnection(); 
            System.out.println("items refactored");
            return true;
        } catch (Exception ex) {            
            System.out.println("refactorRawData");
            System.out.println(ex.getMessage());
            ex.printStackTrace();                
        }                                 
        return false;
    }    
    
    protected boolean save_Items(String lsFileName, List<TblDV360SPD> localitemsDV360){
        System.out.println("saveFile "+lsFileName);
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

                    double num = item.getdMediaCosts();
                    BigDecimal bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                      
                    pstmt.setDouble(13, bd.doubleValue());
                                        
                    num = item.getdTotalMediaCosts();
                    bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                                          
                    pstmt.setDouble(14, bd.doubleValue());
                                        
                    pstmt.setString(15, lsFileName.trim());
                    pstmt.setString(16, item.getvDSP());
                    pstmt.setString(17, item.getvClient());
                    pstmt.setString(18, item.getvAgency());
                    pstmt.setString(19, item.getvChannel());
                    pstmt.setString(20, item.getvAlias());
                    pstmt.setString(21, item.getvVendor());
                    pstmt.setString(22, item.getvVendorSource());                  

                    num = item.getdCPM();
                    bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);                      
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
                        
            List<String> readAllLines =
            new BufferedReader(new InputStreamReader(itemFile.getInputStream(),StandardCharsets.UTF_8)).lines().collect(Collectors.toList());            
            
            if(readAllLines != null && !readAllLines.isEmpty()){
                AtomicReference<Integer> row = new AtomicReference<>(0);
                readAllLines.forEach(line -> {
                
                    Row currentRow = sheet.createRow(row.getAndSet(row.get() + 1));
                    String[] nextLine = line.split(",");

                    Stream.iterate(0, i -> i + 1).limit(nextLine.length).forEach(i -> {
                        currentRow.createCell(i).setCellValue(nextLine[i]);
                    });
                });
            }

            return workbook;          
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
        if (itemFile != null && idDaily != null){  

            if(idDaily.getId_daily() == 0){
                idDaily.setId_daily(getItemDailybyDate(idDaily));
                if(idDaily.getId_daily() == 0){
                    idDaily.setId_daily(createItemDaily(idDaily));
                }
            }

            lsFileName = itemFile.getFileName();
            if (lsSource.contains("DSP")){                
                if (lsFileName.contains("DV360")){
                    save_Items(lsFileName, scrap_DV360_Format(itemFile, idDaily));
                }else if (lsFileName.contains("Basis")){
                    save_Items(lsFileName, scrap_BASIS_Format(itemFile, idDaily));                          
                }else if (lsFileName.contains("Domain-Detailed")){
                    save_Items(lsFileName, scrap_PPOINT_Format(itemFile, idDaily));                  
                }                        
            }else{//SSP
                if (lsFileName.contains("Equativ")){//CSV                    
                    save_ItemsSSP(lsFileName, scrap_SSP_Equative_Format(itemFile, idDaily));
                }else if (lsFileName.contains("PubMatic")){//CSV
                    save_ItemsSSP(lsFileName, scrap_SSP_PubMatic_Format(itemFile, idDaily));
                }else if (lsFileName.contains("Xandr_Data")){                    
                    save_ItemsSSP(lsFileName, scrap_SSP_Xandr_Data_Format(itemFile, idDaily));                   
                }else if (lsFileName.contains("Xandr_MLM")){                    
                    save_ItemsSSP(lsFileName, scrap_SSP_Xandr_MLM_Format(itemFile, idDaily));                   
                }else if (lsFileName.contains("DPX")){
                    save_ItemsSSP(lsFileName, scrap_SSP_OpenX_Format(itemFile, idDaily));                    
                }                        
            }                
        }else{
            JsfUtil.addErrorMessage("No Date seleted");
        }
        
    }
}

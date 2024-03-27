package com.dp.controller;

import com.dp.entity.TblCatalog;
import com.dp.entity.TblTypeSources;
import com.dp.facade.TblTypeSourcesFacade;
import com.dp.facade.util.JsfUtil;
import com.dp.util.DAOFile;
import com.dp.util.TblCatalogo;
import com.dp.util.TblCatalogoColumn;
import com.dp.util.TblDVXANDRSPD;
import com.dp.util.TblDailyProcess;
import com.dp.util.TblHistoricalSSP;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.util.LangUtils;

@Named("tblRawSSPDataController")
@ViewScoped
public class TblRawSSPDataController implements Serializable {
    private List<TblDVXANDRSPD> items = null;
    private List<TblDVXANDRSPD> monthlyItems = null;
    private List<TblDVXANDRSPD> filteredItems = null;
    private List<TblHistoricalSSP> historicalItems = null;
    private TblDVXANDRSPD selected;
    private Date dDateSelected;
    private Date dMonthSelected; 
    private Date maxDate;    
    private TblDailyProcess idDailySelected;
    private Boolean lbDataFound;
    private Boolean lbDataTransfer;
    private TblCatalogo editCatalog;
    private List<TblTypeSources> itemsTypes = null;      
    private boolean globalFilterOnly = true;
    private String todayAsString;
    private List<TblCatalogo> itemsCatalogo = null;
    //private List<TblCatalogoColumn> itemsCatalogoColumn = null;    
    private List<String> rawColumns;
    private String[] selectedrawColumns;    
    private Integer iYear;
    private Integer iMonth;
    
    public TblRawSSPDataController() {
        internalLimpiar();
        getDateBounds();
        getItemCalendarByDate();
        getItemsCatalogo();
        getRawColumnsBySource();        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        todayAsString = df.format(JsfUtil.getFechaSistema().getTime());        
    }

    protected void getRawColumnsBySource() {
        rawColumns = null;      
        DAOFile dbCon = new DAOFile();
        rawColumns = dbCon.getItemsColumnNames("S");
    }

    public List<String> getRawColumns() {
        return rawColumns;
    }

    public Integer getIYear() {
        return iYear;
    }

    public void setIYear(Integer iYear) {
        this.iYear = iYear;
    }

    public Integer getIMonth() {
        return iMonth;
    }

    public void setIMonth(Integer iMonth) {
        this.iMonth = iMonth;
    }

    public Boolean getLbDataTransfer() {
        return lbDataTransfer;
    }

    public void setLbDataTransfer(Boolean lbDataTransfer) {
        this.lbDataTransfer = lbDataTransfer;
    }

    public Date getDMonthSelected() {
        return dMonthSelected;
    }

    public void setDMonthSelected(Date dMonthSelected) {
        this.dMonthSelected = dMonthSelected;
    }

    public List<TblDVXANDRSPD> getMonthlyItems() {
        if ((monthlyItems == null || monthlyItems.isEmpty()) && dMonthSelected != null/* && idDailySelected != null && idDailySelected.getId_daily() > 0*/) {
            LocalDate localDate = LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(dMonthSelected));
            DAOFile dbCon = new DAOFile();
            monthlyItems = dbCon.getRawSSPDatabyMonth(localDate.getYear(), localDate.getMonthValue());
            if (monthlyItems != null && !monthlyItems.isEmpty()){
                setLbDataFound(true);
            }else{
                 setLbDataFound(false);
            }
        }        
        return monthlyItems;
    }

    public void setMonthlyItems(List<TblDVXANDRSPD> monthlyItems) {
        this.monthlyItems = monthlyItems;
    }

    public void getHistoricalByParams(){
        historicalItems = null;
        setLbDataFound(false);
        DAOFile dbCon = new DAOFile();
        historicalItems = dbCon.getHistoricalSSPbyMonth(iYear, iMonth);
        if (historicalItems != null && !historicalItems.isEmpty()){
            setLbDataFound(true);
        }
    }  
    
    public List<TblHistoricalSSP> getHistoricalItems() {              
        return historicalItems;
    }

    public void setHistoricalItems(List<TblHistoricalSSP> historicalItems) {
        this.historicalItems = historicalItems;
    }

    
    public void setRawColumns(List<String> rawColumns) {
        this.rawColumns = rawColumns;
    }

    public String[] getSelectedrawColumns() {
        return selectedrawColumns;
    }

    public void setSelectedrawColumns(String[] selectedrawColumns) {
        this.selectedrawColumns = selectedrawColumns;
    }
    
    protected void getItemsCatalogo() {
        if (itemsCatalogo == null /*&& itemsCatalogoColumn == null*/){
            DAOFile dbCon = new DAOFile();
            itemsCatalogo = dbCon.getCatalogoItems("S");
            //itemsCatalogoColumn = dbCon.getCatalogoColumnItems("S");
        }
    }        
    
    public String getTodayAsString() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        todayAsString = df.format(JsfUtil.getFechaSistema().getTime());  
        return todayAsString;
    }

    public void setTodayAsString(String todayAsString) {
        this.todayAsString = todayAsString;
    }
        
    public TblDVXANDRSPD getSelected() {
        return selected;
    }

    public Boolean getLbDataFound() {
        return lbDataFound;
    }

    public List<TblDVXANDRSPD> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<TblDVXANDRSPD> filteredItems) {
        this.filteredItems = filteredItems;
    }

    public boolean isGlobalFilterOnly() {
        return globalFilterOnly;
    }

    public void setGlobalFilterOnly(boolean globalFilterOnly) {
        this.globalFilterOnly = globalFilterOnly;
    }

    public void setLbDataFound(Boolean lbDataFound) {
        this.lbDataFound = lbDataFound;
    }
    public TblCatalogo getEditCatalog() {
        return editCatalog;
    }
    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();

        if (LangUtils.isBlank(filterText)) {
            return true;
        }
        //int filterInt = getInteger(filterText);
        TblDVXANDRSPD item = (TblDVXANDRSPD) value;
        
        return item.getvAgency().toLowerCase().contains(filterText)
                || item.getvAdvertiser().toLowerCase().contains(filterText)
                || item.getIdDaily().getdDate().toString().toLowerCase().contains(filterText)
                || item.getvAgency().toLowerCase().contains(filterText)
                || item.getvChannel().toLowerCase().contains(filterText)
                || item.getvClient().toLowerCase().contains(filterText)
                || item.getvBrand().toLowerCase().contains(filterText)
                || item.getvDeal().toLowerCase().contains(filterText)
                || item.getvExchange().toLowerCase().contains(filterText)
                || item.getvDevice().toLowerCase().contains(filterText)
                || item.getvDsp().toLowerCase().contains(filterText)
                || item.getvSeat().toLowerCase().contains(filterText);
    }    
    public TblCatalogo prepareEdit() {        
        editCatalog = new TblCatalogo();
        editCatalog.setvSource("S");
        editCatalog.setvPattern("");
        editCatalog.setvType("");
        editCatalog.setvValue("");
        return editCatalog;
    }    


    protected boolean isValid(){
        if (editCatalog == null) {
            JsfUtil.addErrorMessage("Something went wrong! Try again");
            return false;
        }else if(editCatalog.getvPattern().isEmpty()){
            JsfUtil.addErrorMessage("Pattern is empty");
            return false;
        }else if(editCatalog.getvValue().isEmpty()){
            JsfUtil.addErrorMessage("Value is empty");
            return false;            
        }else if(editCatalog.getvType().isEmpty()){
            JsfUtil.addErrorMessage("Category is not selected");
            return false;            
        }else if(selectedrawColumns == null){
            JsfUtil.addErrorMessage("Something went wrong! Try again");
            return false;
        }else if(selectedrawColumns.length == 0){
            JsfUtil.addErrorMessage("You have to selected any Search file");
            return false;
        }
        return true;        
    }
    
    public void updateEdit(){
        if(isValid()){
            DAOFile dbCon = new DAOFile();           
            if (dbCon.createItemCatalogColumnsRelated(editCatalog,selectedrawColumns)){
                dbCon.setItemsCatalogo(itemsCatalogo);           
                dbCon.setItemsXANDRRefactor(items);                  
                if (dbCon.refactorRawSSPData(idDailySelected.getId_daily(), editCatalog, selectedrawColumns)){
                    selected = null;
                    items =  null;
                    editCatalog =  null;
                    selectedrawColumns = null;
                    JsfUtil.addSuccessMessage("Refactor completes successfully");                    
                }
            }else{
                JsfUtil.addErrorMessage("Can´t add new item catalog");
            }
        }else{
            JsfUtil.addErrorMessage("Check all fields for Edit catalog Form");
        }     
    }
    
    public void setEditCatalog(TblCatalogo editCatalog) {
        this.editCatalog = editCatalog;
    }
    
    public void setConfirm(){
        setLbDataFound(false);
    }
    public List<TblTypeSources> getItemsTypes() {
        if (itemsTypes == null && editCatalog != null){
            DAOFile dbCon = new DAOFile();
            itemsTypes = dbCon.getCatalogoItemsTypes(editCatalog.getvSource());         
        }
        return itemsTypes;   
    }

    public void getItemsTypesBySource() {
        if(editCatalog != null){
            itemsTypes = null;
            TblTypeSourcesFacade itemsEjb = new TblTypeSourcesFacade();
            itemsTypes = itemsEjb.getItemsBySource(editCatalog.getvSource());
        }        
    }    
    
    public void setItemsTypes(List<TblTypeSources> itemsTypes) {
        this.itemsTypes = itemsTypes;
    }
    
    public void setSelected(TblDVXANDRSPD selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    protected void initializeEmbeddableKey() {
    }

    public Date getDDateSelected() {
        return dDateSelected;
    }

    public TblDailyProcess getIdDailySelected() {
        return idDailySelected;
    }
    
    public void transferToHistorical(){
        if (idDailySelected != null){
            DAOFile dbCon = new DAOFile();
            dbCon.setUpdateUser(JsfUtil.getUsuarioSesion());
            if (dbCon.transferToHistorical("SSP", idDailySelected.getiYear(), idDailySelected.getiMonth() )){
                items = null;
                monthlyItems = null;
                filteredItems = null;
                selected = null;      
                JsfUtil.addSuccessMessage("Data transfered successfully");
            }
        }
    }    
    
    public void setIdDailySelected(TblDailyProcess idDailySelected) {
        this.idDailySelected = idDailySelected;
    }

    public void montlyClean(){
        if (filteredItems != null && !filteredItems.isEmpty()){
            DAOFile dbCon = new DAOFile();
            if (dbCon.cleanMonthlyRawSSPData(filteredItems)){
                itemsCatalogo = dbCon.getCatalogoItems("S");
                rawColumns = dbCon.getItemsColumnNames("S");
                items = null;
                monthlyItems = null;
                filteredItems = null;
                selected = null;                
                JsfUtil.addSuccessMessage("Items deleted successfully");
            }
        }else{
            JsfUtil.addErrorMessage("No items filtered to clear");
        }
    }
    
    
    public void getItemCalendarByMonth() {              
        try { 
            internalClear();
            if (dMonthSelected != null){ 
                DAOFile dbCon = new DAOFile();
                LocalDate localDate = LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(dMonthSelected));
                idDailySelected = new TblDailyProcess();
                idDailySelected.setiDay(localDate.lengthOfMonth());
                idDailySelected.setiMonth(localDate.getMonthValue());
                idDailySelected.setiYear(localDate.getYear());
                idDailySelected.setdDate(new java.sql.Date(dMonthSelected.getTime()));
                idDailySelected.setdDate(java.sql.Date.valueOf(LocalDate.of(localDate.getYear(), localDate.getMonthValue(), localDate.lengthOfMonth())));
                idDailySelected.setId_daily(dbCon.getItemDailybyDate(idDailySelected));
            }   
        } catch (Exception ex) {
            System.out.println("getItemCalendarByMonth");
            System.out.println(ex.getMessage());
            ex.printStackTrace();            
        }              
    } 
    public void getItemCalendarByDate() {
        try {            
            internalClear();
            if (dDateSelected != null){            
                LocalDate localDate = LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(dDateSelected) );
                DAOFile dbCon = new DAOFile();
                idDailySelected = new TblDailyProcess();
                idDailySelected.setiDay(localDate.getDayOfMonth());
                idDailySelected.setiMonth(localDate.getMonthValue());
                idDailySelected.setiYear(localDate.getYear());
                idDailySelected.setdDate(new java.sql.Date(dDateSelected.getTime()));
                idDailySelected.setId_daily(dbCon.getItemDailybyDate(idDailySelected));
                setLbDataFound(false);
                lbDataTransfer = false;
                if(localDate.lengthOfMonth() == localDate.getDayOfMonth()) lbDataTransfer = true;                
            }
        } catch (Exception ex) {
            System.out.println("getItemCalendarByDate");
            System.out.println(ex.getMessage());
            ex.printStackTrace();            
        }            
    }   
    
    protected void getDateBounds(){
        Calendar cal = JsfUtil.getFechaSistema();
        cal.add(Calendar.DATE, -1);
        setMaxDate(cal.getTime());
        setDDateSelected(cal.getTime());
    }    
    
    public void handleFileUpload(FileUploadEvent event) throws ClassNotFoundException, Exception {            
        if( dDateSelected != null){
            if (event != null && event.getFile() != null){
                DAOFile dbCon = new DAOFile();
                dbCon.setItemsCatalogo(itemsCatalogo);
                //dbCon.setItemsCatalogoColumn(itemsCatalogoColumn);                
                dbCon.ScanFiles("SSP", event.getFile(), idDailySelected);
                JsfUtil.addSuccessMessage(event.getFile().getFileName() + " uploaded successfully");
                items = null;
                monthlyItems = null;
                filteredItems = null;
            }            
        }else{
            JsfUtil.addErrorMessage("No date selected");
        }
    }    
    
    public void handleFileUploadMonthly(FileUploadEvent event) throws ClassNotFoundException, Exception {            
        if( dMonthSelected != null){
            if (event != null && event.getFile() != null){
                DAOFile dbCon = new DAOFile();
                dbCon.setItemsCatalogo(itemsCatalogo);
                dbCon.ScanFiles("SSP", event.getFile(), idDailySelected);
                JsfUtil.addSuccessMessage(event.getFile().getFileName() + " uploaded successfully");
                monthlyItems = null;
                filteredItems = null;
            }            
        }else{
            JsfUtil.addErrorMessage("No date selected");
        }
    } 
    
    protected void internalClear(){      
        setLbDataFound(true);
        lbDataTransfer = null;
        items = null;
        monthlyItems = null;
        filteredItems = null;
        selected = null;
        idDailySelected = null;
        //PrimeFaces.current().executeScript("$('#TblRawDataListForm\\:datalist\\:globalFilter').val('').keyup(); return false;");
    }
    
    public void internalLimpiar(){      
        setLbDataFound(true);
        lbDataTransfer = null;
        items = null;
        monthlyItems = null;
        filteredItems = null;
        selected = null;
        dDateSelected = null;
        idDailySelected = null;
    }    
         
    public void simpleLimpiar(){      
        setLbDataFound(true);
        lbDataTransfer = null;
        items = null;
        monthlyItems = null;
        filteredItems = null;
        selected = null;
        dDateSelected = null;
        idDailySelected = null;
        //PrimeFaces.current().executeScript("$('#TblRawSSPDataListForm\\:datalist\\:globalFilter').val('').keyup(); return false;");
    }

    public void complexLimpiar(){
        if (idDailySelected != null && idDailySelected.getId_daily() > 0){
            DAOFile dbCon = new DAOFile();
            if (dbCon.cleanRawDataByDaily(idDailySelected.getId_daily(), "SSP")){
                itemsCatalogo = dbCon.getCatalogoItems("S");
                //itemsCatalogoColumn = dbCon.getCatalogoColumnItems("S");
                rawColumns = dbCon.getItemsColumnNames("S");                
                items = null;
                monthlyItems = null;
                filteredItems = null;
                selected = null;                
            }
        }
    }
        
    public void setDDateSelected(Date dDateSelected) {
        this.dDateSelected = dDateSelected;
    }

    public List<TblDVXANDRSPD> getItems() {
        if ((items == null || items.isEmpty()) && idDailySelected != null && idDailySelected.getId_daily() > 0) {
            DAOFile dbCon = new DAOFile();
            items = dbCon.getRawSSPDatabyDate(idDailySelected.getId_daily());
            if (items != null && !items.isEmpty()){
                setLbDataFound(true);
            }else{
                 setLbDataFound(false);
            }
        }
        return items;
    }

}

package com.dp.controller;

import com.dp.entity.TblCatalog;
import com.dp.entity.TblTypeSources;
import com.dp.facade.TblTypeSourcesFacade;
import com.dp.facade.util.JsfUtil;
import com.dp.util.DAOFile;
import com.dp.util.TblDV360SPD;
import com.dp.util.TblDailyProcess;
import com.dp.util.TblCatalogo;
import com.dp.util.TblCatalogoColumn;
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
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.util.LangUtils;


@Named("tblRawDataController")
@ViewScoped
public class TblRawDataController implements Serializable {
    private List<TblDV360SPD> items = null;
    private List<TblDV360SPD> filteredItems = null;    
    private TblDV360SPD selected;
    private Date dDateSelected;
    private Date maxDate;    
    private TblDailyProcess idDailySelected;
    private Boolean lbDataFound;
    private TblCatalog editCatalog;
    private List<TblTypeSources> itemsTypes = null;    
    private boolean globalFilterOnly;
    private String todayAsString;
    private List<TblCatalogo> itemsCatalogo = null;
    private List<TblCatalogoColumn> itemsCatalogoColumn = null;
    private List<String> rawColumns;
    private String[] selectedrawColumns;    
    
    public TblRawDataController() {
        simpleLimpiar();
        getDateBounds();
        getItemsCatalogo();
        getRawColumnsBySource();
        globalFilterOnly = true;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        todayAsString = df.format(JsfUtil.getFechaSistema().getTime());               
    }

    protected void getRawColumnsBySource() {
        rawColumns = null;
        DAOFile dbCon = new DAOFile();
        rawColumns = dbCon.getItemsColumnNames("D");
    }

    public List<String> getRawColumns() {
        return rawColumns;
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
        if (itemsCatalogo == null && itemsCatalogoColumn == null){
            DAOFile dbCon = new DAOFile();
            itemsCatalogo = dbCon.getCatalogoItems("D");
            itemsCatalogoColumn = dbCon.getCatalogoColumnItems("D");
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

    public List<TblDV360SPD> getItems() {
        if ((items == null || items.isEmpty()) && idDailySelected != null && idDailySelected.getId_daily() > 0) {
            DAOFile dbCon = new DAOFile();
            items = dbCon.getRawDatabyDate(idDailySelected.getId_daily());
            if (items != null && !items.isEmpty()){
                PrimeFaces.current().executeScript("PF('bui').show()");
                setLbDataFound(true);
            }else{
                 setLbDataFound(false);
            }
        }
        return items;
    }

    public TblDV360SPD getSelected() {
        return selected;
    }

    public boolean isGlobalFilterOnly() {
        return globalFilterOnly;
    }

    public void setGlobalFilterOnly(boolean globalFilterOnly) {
        this.globalFilterOnly = globalFilterOnly;
    }

    public Boolean getLbDataFound() {
        return lbDataFound;
    }

    public TblCatalog getEditCatalog() {
        return editCatalog;
    }

    public List<TblDV360SPD> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<TblDV360SPD> filteredItems) {
        this.filteredItems = filteredItems;
    }

    public void toggleGlobalFilter() {
        setGlobalFilterOnly(!isGlobalFilterOnly());
    }

    private int getInteger(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException ex) {
            return 0;
        }
    }
    
    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();

        if (LangUtils.isBlank(filterText)) {
            return true;
        }
        //int filterInt = getInteger(filterText);
        TblDV360SPD item = (TblDV360SPD) value;
        
        return item.getvAgency().toLowerCase().contains(filterText)
                || item.getvAlias().toLowerCase().contains(filterText)
                || item.getIdDaily().getdDate().toString().toLowerCase().contains(filterText)
                || item.getvCampaign().toLowerCase().contains(filterText)
                || item.getvChannel().toLowerCase().contains(filterText)
                || item.getvClient().toLowerCase().contains(filterText)
                || item.getvDSP().toLowerCase().contains(filterText)
                || item.getvDealName().toLowerCase().contains(filterText)
                || item.getvExchange().toLowerCase().contains(filterText)
                || item.getvInsertionOrder().toLowerCase().contains(filterText)
                || item.getvLineItem().toLowerCase().contains(filterText)
                || item.getvPartner().toLowerCase().contains(filterText)
                || item.getvVendor().toLowerCase().contains(filterText)
                || item.getvVendorSource().toLowerCase().contains(filterText);
    }    
    
    public TblCatalog prepareEdit() {        
        editCatalog = new TblCatalog();
        editCatalog.setVSource("D");
        editCatalog.setVPattern("");
        editCatalog.setVType("");
        editCatalog.setVValue("");
        editCatalog.setIEstado(Boolean.TRUE);
        return editCatalog;
    }    
    protected boolean isValid(){
        if (editCatalog == null) {
            JsfUtil.addErrorMessage("Something went wrong! Try again");
            return false;
        }else if(editCatalog.getVPattern().isEmpty()){
            JsfUtil.addErrorMessage("Pattern is empty");
            return false;
        }else if(editCatalog.getVValue().isEmpty()){
            JsfUtil.addErrorMessage("Value is empty");
            return false;            
        }else if(editCatalog.getVType().isEmpty()){
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
                dbCon.setItemsCatalogoColumn(itemsCatalogoColumn);                  
                dbCon.setItemsDV360Refactor(items);                   
                if (dbCon.refactorRawData(idDailySelected.getId_daily(), editCatalog, selectedrawColumns)){
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
    
    public void setEditCatalog(TblCatalog editCatalog) {
        this.editCatalog = editCatalog;
    }

    public void setLbDataFound(Boolean lbDataFound) {
        this.lbDataFound = lbDataFound;
    }
    
    public void setConfirm(){
        setLbDataFound(false);
    }
    
    public void setSelected(TblDV360SPD selected) {
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

    public List<TblTypeSources> getItemsTypes() {
        if (itemsTypes == null && editCatalog != null){
            TblTypeSourcesFacade itemsEjb = new TblTypeSourcesFacade();
            itemsTypes = itemsEjb.getItemsBySource(editCatalog.getVSource());
            if (itemsTypes != null && !itemsTypes.isEmpty()) editCatalog.setVType((itemsTypes.get(0)).getVType());
        }
        return itemsTypes;
    }

    public void getItemsTypesBySource() {
        if(editCatalog != null){
            itemsTypes = null;
            TblTypeSourcesFacade itemsEjb = new TblTypeSourcesFacade();
            itemsTypes = itemsEjb.getItemsBySource(editCatalog.getVSource());
        }        
    }    
    
    public void setItemsTypes(List<TblTypeSources> itemsTypes) {
        this.itemsTypes = itemsTypes;
    }

    protected void initializeEmbeddableKey() {
    }

    public Date getDDateSelected() {
        return dDateSelected;
    }

    public TblDailyProcess getIdDailySelected() {
        return idDailySelected;
    }

    public void setIdDailySelected(TblDailyProcess idDailySelected) {
        this.idDailySelected = idDailySelected;
    }

    public void complexLimpiar(){
        if (idDailySelected != null && idDailySelected.getId_daily() > 0){
            DAOFile dbCon = new DAOFile();
            if (dbCon.cleanRawDataByDaily(idDailySelected.getId_daily(), "DSP")){
                PrimeFaces.current().executeScript("PF('bui').hide()");
                items = null;
                selected = null;                
            }
        }
    }
    
    public void getItemCalendarByDate() {
        try {           
            items = null;
            selected = null;
            idDailySelected = null;
            if (dDateSelected != null){ 
                DAOFile dbCon = new DAOFile();
                LocalDate localDate = LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(dDateSelected) );
                idDailySelected = new TblDailyProcess();
                idDailySelected.setiDay(localDate.getDayOfMonth());
                idDailySelected.setiMonth(localDate.getMonthValue());
                idDailySelected.setiYear(localDate.getYear());
                idDailySelected.setdDate(new java.sql.Date(dDateSelected.getTime()));
                idDailySelected.setId_daily(dbCon.getItemDailybyDate(idDailySelected));
                //if (idDailySelected.getId_daily() > 0) PrimeFaces.current().executeScript("PF('bui').show()");
                setLbDataFound(false);
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
    }    
    
    public void handleFileUpload(FileUploadEvent event) throws ClassNotFoundException, Exception {            
        if( dDateSelected != null){
            if (event != null && event.getFile() != null){
                DAOFile dbCon = new DAOFile();
                dbCon.setItemsCatalogo(itemsCatalogo);
                dbCon.setItemsCatalogoColumn(itemsCatalogoColumn);
                dbCon.ScanFiles("DSP", event.getFile(), idDailySelected);
                JsfUtil.addSuccessMessage(event.getFile().getFileName() + " uploaded successfully");
                items = null;
            }            
        }else{
            JsfUtil.addErrorMessage("No date selected");
        }
    }    
    
    public void simpleLimpiar(){      
        setLbDataFound(true);
        items = null;
        selected = null;
        dDateSelected = null;
        idDailySelected = null;
    }

    public void setDDateSelected(Date dDateSelected) {
        this.dDateSelected = dDateSelected;
    }
}

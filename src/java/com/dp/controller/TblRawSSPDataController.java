package com.dp.controller;

import com.dp.entity.TblCatalog;
import com.dp.entity.TblTypeSources;
import com.dp.facade.TblTypeSourcesFacade;
import com.dp.facade.util.JsfUtil;
import com.dp.util.DAOFile;
import com.dp.util.TblDV360SPD;
import com.dp.util.TblDVXANDRSPD;
import com.dp.util.TblDailyProcess;
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

@Named("tblRawSSPDataController")
@ViewScoped
public class TblRawSSPDataController implements Serializable {
    private List<TblDVXANDRSPD> items = null;
    private List<TblDVXANDRSPD> filteredItems = null;
    private TblDVXANDRSPD selected;
    private Date dDateSelected;
    private Date maxDate;    
    private TblDailyProcess idDailySelected;
    private Boolean lbDataFound;
    private TblCatalog editCatalog;
    private List<TblTypeSources> itemsTypes = null;      
    private boolean globalFilterOnly;
    private String todayAsString;
    
    public TblRawSSPDataController() {
        simpleLimpiar();
        getDateBounds();
        globalFilterOnly = true;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        todayAsString = df.format(JsfUtil.getFechaSistema().getTime());        
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
    public TblCatalog getEditCatalog() {
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
    public TblCatalog prepareEdit() {        
        editCatalog = new TblCatalog();
        editCatalog.setVSource("S");
        editCatalog.setVPattern("");
        editCatalog.setVType("");
        editCatalog.setVValue("");
        editCatalog.setIEstado(Boolean.TRUE);
        return editCatalog;
    }    
    
    public void updateEdit(){
        if(editCatalog != null && !editCatalog.getVType().isEmpty() && !editCatalog.getVPattern().isEmpty() && !editCatalog.getVValue().isEmpty()){
            DAOFile dbCon = new DAOFile();
            if (dbCon.createItemCatalog(editCatalog)){
                if (dbCon.refactorRawSSPData(idDailySelected.getId_daily(), editCatalog)){
                    selected = null;
                    items =  null;
                    editCatalog =  null;
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
    
    public void setConfirm(){
        setLbDataFound(false);
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

    public void setIdDailySelected(TblDailyProcess idDailySelected) {
        this.idDailySelected = idDailySelected;
    }

    public void getItemCalendarByDate() {
        try {            
            items = null;
            selected = null;
            idDailySelected = null;
            if (dDateSelected != null){            
                LocalDate localDate = LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(dDateSelected) );
                DAOFile dbCon = new DAOFile();
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
                dbCon.ScanFiles("SSP", event.getFile(), idDailySelected);
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

    public void complexLimpiar(){
        if (idDailySelected != null && idDailySelected.getId_daily() > 0){
            DAOFile dbCon = new DAOFile();
            if (dbCon.cleanRawDataByDaily(idDailySelected.getId_daily(), "SSP")){
                PrimeFaces.current().executeScript("PF('bui').hide()");
                items = null;
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
                PrimeFaces.current().executeScript("PF('bui').show()");
                setLbDataFound(true);
            }else{
                 setLbDataFound(false);
            }
        }
        return items;
    }

}

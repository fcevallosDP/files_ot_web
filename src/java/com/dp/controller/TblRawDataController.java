package com.dp.controller;

import com.dp.entity.TblCatalog;
import com.dp.entity.TblTypeSources;
import com.dp.facade.TblTypeSourcesFacade;
import com.dp.facade.util.JsfUtil;
import com.dp.util.DAOFile;
import com.dp.util.TblDV360SPD;
import com.dp.util.TblDailyProcess;
import java.awt.Event;
import java.io.IOException;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;


@Named("tblRawDataController")
@ViewScoped
public class TblRawDataController implements Serializable {

    private List<TblDV360SPD> items = null;
    private TblDV360SPD selected;
    private Date dDateSelected;
    private Date maxDate;    
    private TblDailyProcess idDailySelected;
    private Boolean lbDataFound;
    private TblCatalog editCatalog;
    private List<TblTypeSources> itemsTypes = null;    
     
    public TblRawDataController() {
        simpleLimpiar();
        getDateBounds();
    }

    public TblDV360SPD getSelected() {
        return selected;
    }

    public Boolean getLbDataFound() {
        return lbDataFound;
    }

    public TblCatalog getEditCatalog() {
        return editCatalog;
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
    
    public void updateEdit(){
        if(editCatalog != null && !editCatalog.getVType().isEmpty() && !editCatalog.getVPattern().isEmpty() && !editCatalog.getVValue().isEmpty()){
            DAOFile dbCon = new DAOFile();
            if (dbCon.createItemCatalog(editCatalog)){
                if (dbCon.refactorRawData(idDailySelected.getId_daily(), editCatalog)){
                    selected = null;
                    items =  null;
                    editCatalog =  null;
                    PrimeFaces.current().executeScript("PF('WdataList').clearFilters();");
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

    public List<TblDV360SPD> getItems() {
        if ((items == null || items.isEmpty()) && idDailySelected != null && idDailySelected.getId_daily() > 0) {
            DAOFile dbCon = new DAOFile();
            items = dbCon.getRawDatabyDate(idDailySelected.getId_daily());
            //itemsFiltered = items;
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

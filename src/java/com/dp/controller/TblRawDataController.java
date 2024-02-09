package com.dp.controller;

import com.dp.facade.util.JsfUtil;
import com.dp.util.DAOFile;
import com.dp.util.TblDV360SPD;
import com.dp.util.TblDailyProcess;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FilesUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;

@Named("tblRawDataController")
@ViewScoped
public class TblRawDataController implements Serializable {

    private DAOFile dbCon = new DAOFile(); 
    private List<TblDV360SPD> items = null;
    private TblDV360SPD selected;
    private Date dDateSelected;// = JsfUtil.getFechaSistema().getTime();
    private List<TblDailyProcess> itemsCalendar = null;
    private List<Date> invalidDates;
    private Date minDate;
    private Date maxDate;    
    private TblDailyProcess idDailySelected;
    
    public TblRawDataController() {
        getItemsCalendar();
        getDateBounds();
    }

    public TblDV360SPD getSelected() {
        return selected;
    }

    public void setSelected(TblDV360SPD selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }


    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
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

    public List<TblDailyProcess> getItemsCalendar() {
        if (itemsCalendar == null) {
            itemsCalendar = getDbCon().getCalendarUpToYesterday();
        }
        return itemsCalendar;        
    }


    public void getItemCalendarByDate() {
        items = null;
        selected = null;
        idDailySelected = null;
        if (dDateSelected != null) {
            for (TblDailyProcess itemDaily : itemsCalendar) {
                if (itemDaily.getdDate().compareTo(dDateSelected) == 0){
                    idDailySelected = itemDaily;
                    break;
                }     
            }
        }
//        getItems();
    }
    
    protected void getDateBounds(){
        if (itemsCalendar != null && itemsCalendar.size() > 0) {
            TblDailyProcess itemDaily = itemsCalendar.get(itemsCalendar.size() - 1);
            setMaxDate(itemDaily.getdDate());
            itemDaily = itemsCalendar.get(0);
            setMinDate(itemDaily.getdDate());
            setDDateSelected(itemDaily.getdDate());
        } 
    }    
    
    public void handleFilesUpload(FileUploadEvent event)  {
        try {
            if (event != null && event.getFile() != null){
                dbCon.ScanFiles("DSP", event.getFile(), idDailySelected);
                JsfUtil.addSuccessMessage(event.getFile().getFileName() + " uploaded successfully");
            }                            
        } catch (Exception ex) {
            System.out.println("handleFilesUpload");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }    
    
    public void simpleLimpiar(){             
        items = null;
        selected = null;
        getItemsCalendar();
        getDateBounds();        
    }
    
    public List<Date> getInvalidDates() {
        if (invalidDates == null){
            invalidDates = getDbCon().getDatesMerged();     
        }
        return invalidDates;
    }

    public void setInvalidDates(List<Date> invalidDates) {
        this.invalidDates = invalidDates;
    }

    
    public void setItemsCalendar(List<TblDailyProcess> itemsCalendar) {
        this.itemsCalendar = itemsCalendar;
    }

    public void setDDateSelected(Date dDateSelected) {
        this.dDateSelected = dDateSelected;
    }

    protected DAOFile getDbCon() {
        return dbCon;
    }

    protected void setDbCon(DAOFile dbCon) {
        this.dbCon = dbCon;
    }

    public List<TblDV360SPD> getItems() {
        if (items == null || items.isEmpty()) {
            items = getDbCon().getRawDatabyDate(dDateSelected);
        }
        return items;
    }

}

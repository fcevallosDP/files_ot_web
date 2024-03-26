package com.dp.controller;

import com.dp.entity.TblCatalog;
import com.dp.entity.TblTypeSources;
import com.dp.facade.util.JsfUtil;
import com.dp.facade.util.JsfUtil.PersistAction;
import com.dp.facade.TblCatalogFacade;
import com.dp.facade.TblTypeSourcesFacade;
import com.dp.util.DAOFile;
import com.dp.util.TblCatalogo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.view.ViewScoped;

@Named("tblCatalogController")
@ViewScoped
public class TblCatalogController implements Serializable {

    //@EJB
    //private TblCatalogFacade ejbFacade = new TblCatalogFacade();
    private List<TblCatalogo> items = null;
    private TblCatalogo selected;
    private List<TblTypeSources> itemsTypes = null;
    private List<String> itemsCategories = null;
    private List<String> rawColumns;
    private String[] selectedrawColumns;

    public TblCatalogController() {
        DAOFile dbCon = new DAOFile();
        itemsCategories = dbCon.getItemsCategories();        
    }

    public TblCatalogo getSelected() {
        return selected;
    }

    public List<String> getItemsCategories() {
        return itemsCategories;
    }

    public String[] getSelectedrawColumns() {
        return selectedrawColumns;
    }

    public void setSelectedrawColumns(String[] selectedrawColumns) {
        this.selectedrawColumns = selectedrawColumns;
    }

    public List<String> getRawColumns() {
        return rawColumns;
    }

    public void getRawColumnsBySource() {
        rawColumns = null;
        if(selected != null){            
            DAOFile dbCon = new DAOFile();
            rawColumns = dbCon.getItemsColumnNames(selected.getvSource());
        }        
    }    
    
    public void setRawColumns(List<String> rawColumns) {
        this.rawColumns = rawColumns;
    }

    public void setItemsCategories(List<String> itemsCategories) {
        this.itemsCategories = itemsCategories;
    }

    public void setSelected(TblCatalogo selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    public List<TblTypeSources> getItemsTypes() {
        if (itemsTypes == null && selected != null){
            DAOFile dbCon = new DAOFile();
            itemsTypes = dbCon.getCatalogoItemsTypes(selected.getvSource());         
        }
        return itemsTypes;
    }

    public void setItemsTypes(List<TblTypeSources> itemsTypes) {
        this.itemsTypes = itemsTypes;
    }

    public void getItemsTypesBySource() {
        if(selected != null){
            itemsTypes = null;
            selectedrawColumns = null;
            DAOFile dbCon = new DAOFile();
            itemsTypes = dbCon.getCatalogoItemsTypes(selected.getvSource());
            /*************************************************************/            
            rawColumns = dbCon.getItemsColumnNames(selected.getvSource());
            /*************************************************************/  
            List<String> lsRet =  new ArrayList();
            selected.getTblCatalogColumnList().stream().forEachOrdered((a) -> {
                    lsRet.add(a.getvColumnName());
            });
            if(lsRet != null && !lsRet.isEmpty()) selectedrawColumns = lsRet.toArray(new String[0]);             
        }        
    }

    /*private TblCatalogFacade getFacade() {
        return ejbFacade;
    }
    
    public void getColumNamesBySourceType() {
        selectedrawColumns = null;
        if(selected != null){
            DAOFile dbCon = new DAOFile();
            List<String> lsRet = dbCon.getColumnNamesBySourceCategory(selected.getVSource(), selected.getVType());
            if(lsRet != null && !lsRet.isEmpty()) selectedrawColumns = lsRet.toArray(new String[0]);      
        }        
    }    
    */
    public TblCatalogo prepareCreate() {
        selected = new TblCatalogo();
        selected.setvSource("");
        selected.setvPattern("");
        selected.setvType("");
        selected.setvValue("");
        selected.setTblCatalogColumnList(new ArrayList());
        selectedrawColumns = null;
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (isValid()){
            DAOFile dbCon = new DAOFile();
            dbCon.createItemCatalogColumnsRelated(selected,selectedrawColumns);
            JsfUtil.addSuccessMessage("Item added successfully");
            selected = null;
            items = null;            
            selectedrawColumns = null;
            rawColumns = null;
        }
    }

    protected boolean isValid(){
        if (selected == null) {
            JsfUtil.addErrorMessage("Something went wrong! Try again");
            return false;
        }else if(selected.getvPattern().isEmpty()){
            JsfUtil.addErrorMessage("Pattern is empty");
            return false;
        }else if(selected.getvValue().isEmpty()){
            JsfUtil.addErrorMessage("Value is empty");
            return false;            
        }else if(selected.getvType().isEmpty()){
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
    
    public void update() {
        if (isValid()){
            DAOFile dbCon = new DAOFile();
            dbCon.updateItemCatalogColumnsRelated(selected,selectedrawColumns);
            selected = null;            
            items = null;    // Invalidate list of items to trigger re-query.  
            selectedrawColumns = null;
            rawColumns = null;
            JsfUtil.addSuccessMessage("Updated successfully");        
        }
    }

    public List<TblCatalogo> getItems() {
        if (items == null) {
            DAOFile dbCon = new DAOFile();
            items = dbCon.getCatalogItemsActive();
        }
        return items;
    }

    /*
    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
        
    }
    
    @FacesConverter(forClass = TblCatalog.class)
    public static class TblCatalogControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TblCatalogController controller = (TblCatalogController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "tblCatalogController");
            return controller.getTblCatalog(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof TblCatalog) {
                TblCatalog o = (TblCatalog) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TblCatalog.class.getName()});
                return null;
            }
        }

    }
    */
}

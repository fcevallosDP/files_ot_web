package com.dp.controller;

import com.dp.entity.TblCatalog;
import com.dp.entity.TblTypeSources;
import com.dp.facade.util.JsfUtil;
import com.dp.facade.util.JsfUtil.PersistAction;
import com.dp.facade.TblCatalogFacade;
import com.dp.facade.TblTypeSourcesFacade;
import com.dp.util.DAOFile;

import java.io.Serializable;
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
    private TblCatalogFacade ejbFacade = new TblCatalogFacade();
    private List<TblCatalog> items = null;
    private TblCatalog selected;
    private List<TblTypeSources> itemsTypes = null;
    private List<String> itemsCategories = null;
    private List<String> rawColumns;
    private String[] selectedrawColumns;

    public TblCatalogController() {
        DAOFile dbCon = new DAOFile();
        itemsCategories = dbCon.getItemsCategories();        
    }

    public TblCatalog getSelected() {
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
            rawColumns = dbCon.getItemsColumnNames(selected.getVSource());
        }        
    }    
    
    public void setRawColumns(List<String> rawColumns) {
        this.rawColumns = rawColumns;
    }

    public void setItemsCategories(List<String> itemsCategories) {
        this.itemsCategories = itemsCategories;
    }

    public void setSelected(TblCatalog selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    public List<TblTypeSources> getItemsTypes() {
        if (itemsTypes == null && selected != null){
            TblTypeSourcesFacade itemsEjb = new TblTypeSourcesFacade();
            itemsTypes = itemsEjb.getItemsBySource(selected.getVSource());            
        }
        return itemsTypes;
    }

    public void setItemsTypes(List<TblTypeSources> itemsTypes) {
        this.itemsTypes = itemsTypes;
    }

    public void getItemsTypesBySource() {
        if(selected != null){
            itemsTypes = null;
            TblTypeSourcesFacade itemsEjb = new TblTypeSourcesFacade();
            itemsTypes = itemsEjb.getItemsBySource(selected.getVSource());
            /*************************************************************/
            DAOFile dbCon = new DAOFile();
            rawColumns = dbCon.getItemsColumnNames(selected.getVSource());
            /*************************************************************/
        }        
    }

    private TblCatalogFacade getFacade() {
        return ejbFacade;
    }

    public void getColumNamesBySourceType() {
        selectedrawColumns = null;
        if(selected != null){
            DAOFile dbCon = new DAOFile();
            List<String> lsRet = dbCon.getColumnNamesBySourceCategory(selected.getVSource(), selected.getVType());
            if(lsRet != null && !lsRet.isEmpty()) selectedrawColumns = lsRet.toArray(new String[0]);
            /*************************************************************/
        }        
    }    
    
    public TblCatalog prepareCreate() {
        selected = new TblCatalog();
        selected.setVSource("");
        selected.setVPattern("");
        selected.setVType("");
        selected.setVValue("");
        selected.setIEstado(Boolean.TRUE);
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
        }else if(selected.getVPattern().isEmpty()){
            JsfUtil.addErrorMessage("Pattern is empty");
            return false;
        }else if(selected.getVValue().isEmpty()){
            JsfUtil.addErrorMessage("Value is empty");
            return false;            
        }else if(selected.getVType().isEmpty()){
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

    public void destroy() {
        //persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TblCatalogDeleted"));
        if (isValid()) {
            selected.setIEstado(Boolean.FALSE);
            ejbFacade.getEM();
            ejbFacade.beginTransaction();
            ejbFacade.edit(selected);
            ejbFacade.commitTransaction();            
            JsfUtil.addSuccessMessage("Item removed");
            items.remove(selected);
            selected = null; // Remove selection
            /*DESCOMENTAR items = null;*/    // Invalidate list of items to trigger re-query.
        }else{
            JsfUtil.addErrorMessage("Something went wrong");
        }
    }

    public List<TblCatalog> getItems() {
        if (items == null) {
            items = getFacade().getItemsEnabled();//findAll();
        }
        return items;
    }

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

    public TblCatalog getTblCatalog(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<TblCatalog> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TblCatalog> getItemsAvailableSelectOne() {
        return getFacade().findAll();
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

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dp.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ZAMBRED
 */
@Entity
@Table(name = "tbl_catalog", catalog = "rptdata", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TblCatalog.findAll", query = "SELECT t FROM TblCatalog t")
    , @NamedQuery(name = "TblCatalog.findById", query = "SELECT t FROM TblCatalog t WHERE t.id = :id")
    , @NamedQuery(name = "TblCatalog.findByVType", query = "SELECT t FROM TblCatalog t WHERE t.vType = :vType and t.iEstado = 1")       
    , @NamedQuery(name = "TblCatalog.findByVSource", query = "SELECT t FROM TblCatalog t WHERE t.vSource = :vSource and t.iEstado = 1")           
    , @NamedQuery(name = "TblCatalog.findByVValue", query = "SELECT t FROM TblCatalog t WHERE t.vValue = :vValue")
    , @NamedQuery(name = "TblCatalog.findByVPattern", query = "SELECT t FROM TblCatalog t WHERE t.vPattern = :vPattern")
    , @NamedQuery(name = "TblCatalog.findByDSystemDate", query = "SELECT t FROM TblCatalog t WHERE t.dSystemDate = :dSystemDate")
    , @NamedQuery(name = "TblCatalog.findByIEstado", query = "SELECT t FROM TblCatalog t WHERE t.iEstado = :iEstado")
    , @NamedQuery(name = "TblCatalog.findByVSourceVType", query = "SELECT t FROM TblCatalog t WHERE t.vSource = :vSource and t.vType = :vType and t.iEstado = 1")           
})
public class TblCatalog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "vSource")
    private String vSource;  
    @Column(name = "vType")
    private String vType;
    @Column(name = "vValue")
    private String vValue;
    @Column(name = "vPattern")
    private String vPattern;
    @Column(name = "dSystemDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dSystemDate;
    @Column(name = "iEstado")
    private Boolean iEstado;
    @OneToMany(mappedBy = "id")
    private List<TblCatalogColumn> tblCatalogColumnList;
        
    public TblCatalog() {
    }

    public TblCatalog(Integer id) {
        this.id = id;
    }

    public List<TblCatalogColumn> getTblCatalogColumnList() {
        return tblCatalogColumnList;
    }

    public void setTblCatalogColumnList(List<TblCatalogColumn> tblCatalogColumnList) {
        this.tblCatalogColumnList = tblCatalogColumnList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVSource() {
        return vSource;
    }

    public void setVSource(String vSource) {
        this.vSource = vSource;
    }

    public String getVType() {
        return vType;
    }

    public void setVType(String vType) {
        this.vType = vType;
    }

    public String getVValue() {
        return vValue;
    }

    public void setVValue(String vValue) {
        this.vValue = vValue;
    }

    public String getVPattern() {
        return vPattern;
    }

    public void setVPattern(String vPattern) {
        this.vPattern = vPattern;
    }

    public Date getDSystemDate() {
        return dSystemDate;
    }

    public void setDSystemDate(Date dSystemDate) {
        this.dSystemDate = dSystemDate;
    }

    public Boolean getIEstado() {
        return iEstado;
    }

    public void setIEstado(Boolean iEstado) {
        this.iEstado = iEstado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TblCatalog)) {
            return false;
        }
        TblCatalog other = (TblCatalog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dp.controller.TblCatalog[ id=" + id + " ]";
    }
    
}

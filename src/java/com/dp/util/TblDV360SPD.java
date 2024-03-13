/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dp.util;
import java.util.Date;

/**
 *
 * @author ZAMBRED
 */
public class TblDV360SPD {
    private Integer id;

    private String vDate;
    private Date dDate;    
    private int iDia;
    private int iMes;
    private int iAnio;
    private String vPartner;
    private String vCampaign;
    private String vInsertionOrder;
    private String vLineItem;
    private String vExchange;
    private String vDealName;
    private Integer iImpressions;
    private Integer iClicks;
    private Double dMediaCosts;
    private Double dTotalMediaCosts;   
    private Double dCPM;
    private Double dCTR;
    private Double dCPC;
    private String vDSP;
    private String vClient;
    private String vAgency;
    private String vChannel;
    private String vAlias;
    private String vVendor;
    private String vVendorSource;
    private TblDailyProcess idDaily;
    
    public TblDV360SPD() {
    }    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getdDate() {
        return dDate;
    }

    public void setdDate(Date dDate) {
        this.dDate = dDate;
    }

    public String getvDate() {
        return vDate;
    }

    public void setvDate(String vDate) {
        this.vDate = vDate;
    }

    public int getiDia() {
        return iDia;
    }

    public void setiDia(int iDia) {
        this.iDia = iDia;
    }

    public int getiMes() {
        return iMes;
    }

    public void setiMes(int iMes) {
        this.iMes = iMes;
    }

    public int getiAnio() {
        return iAnio;
    }

    public TblDailyProcess getIdDaily() {
        return idDaily;
    }

    public void setIdDaily(TblDailyProcess idDaily) {
        this.idDaily = idDaily;
    }

    public void setiAnio(int iAnio) {
        this.iAnio = iAnio;
    }

    public String getvPartner() {
        return vPartner;
    }

    public void setvPartner(String vPartner) {
        this.vPartner = vPartner;
    }

    public String getvCampaign() {
        return vCampaign;
    }

    public void setvCampaign(String vCampaign) {
        this.vCampaign = vCampaign;
    }

    public String getvInsertionOrder() {
        return vInsertionOrder;
    }

    public void setvInsertionOrder(String vInsertionOrder) {
        this.vInsertionOrder = vInsertionOrder;
    }

    public String getvLineItem() {
        return vLineItem;
    }

    public void setvLineItem(String vLineItem) {
        this.vLineItem = vLineItem;
    }

    public String getvExchange() {
        return vExchange;
    }

    public void setvExchange(String vExchange) {
        this.vExchange = vExchange;
    }

    public String getvDealName() {
        return vDealName;
    }

    public void setvDealName(String vDealName) {
        this.vDealName = vDealName;
    }

    public Integer getiImpressions() {
        return iImpressions;
    }

    public void setiImpressions(Integer iImpressions) {
        this.iImpressions = iImpressions;
    }

    public Integer getiClicks() {
        return iClicks;
    }

    public void setiClicks(Integer iClicks) {
        this.iClicks = iClicks;
    }

    public Double getdMediaCosts() {
        return dMediaCosts;
    }

    public void setdMediaCosts(Double dMediaCosts) {
        this.dMediaCosts = dMediaCosts;
    }

    public Double getdTotalMediaCosts() {
        return dTotalMediaCosts;
    }

    public void setdTotalMediaCosts(Double dTotalMediaCosts) {
        this.dTotalMediaCosts = dTotalMediaCosts;
    }

    public Double getdCPM() {
        return dCPM;
    }

    public void setdCPM(Double dCPM) {
        this.dCPM = dCPM;
    }

    public Double getdCTR() {
        return dCTR;
    }

    public void setdCTR(Double dCTR) {
        this.dCTR = dCTR;
    }

    public Double getdCPC() {
        return dCPC;
    }

    public void setdCPC(Double dCPC) {
        this.dCPC = dCPC;
    }

    public String getvDSP() {
        return vDSP;
    }

    public void setvDSP(String vDSP) {
        this.vDSP = vDSP;
    }

    public String getvClient() {
        return vClient;
    }

    public void setvClient(String vClient) {
        this.vClient = vClient;
    }

    public String getvAgency() {
        return vAgency;
    }

    public void setvAgency(String vAgency) {
        this.vAgency = vAgency;
    }

    public String getvChannel() {
        return vChannel;
    }

    public void setvChannel(String vChannel) {
        this.vChannel = vChannel;
    }

    public String getvAlias() {
        return vAlias;
    }

    public void setvAlias(String vAlias) {
        this.vAlias = vAlias;
    }

    public String getvVendor() {
        return vVendor;
    }

    public void setvVendor(String vVendor) {
        this.vVendor = vVendor;
    }

    public String getvVendorSource() {
        return vVendorSource;
    }

    public void setvVendorSource(String vVendorSource) {
        this.vVendorSource = vVendorSource;
    }


}

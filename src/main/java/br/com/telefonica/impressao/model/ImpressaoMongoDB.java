package br.com.telefonica.impressao.model;

public class ImpressaoMongoDB {

    private Long billingId;
    private Long costumerId;
    private Long billingVencimento;
    private String bellingStatus;
    private Long billingDataPagamento;
    private Double billingValorFatura;
    private Boolean __deleted;
    private Boolean processed;

    public void setBillingId(Long billingId) {
        this.billingId = billingId;
    }
    public void setCostumerId(Long costumerId) {
        this.costumerId = costumerId;
    }
    public void setBillingVencimento(Long billingVencimento) {
        this.billingVencimento = billingVencimento;
    }
    public void setBellingStatus(String bellingStatus) {
        this.bellingStatus = bellingStatus;
    }
    public void setBillingDataPagamento(Long billingDataPagamento) {
        this.billingDataPagamento = billingDataPagamento;
    }
    public void setBillingValorFatura(Double billingValorFatura) {
        this.billingValorFatura = billingValorFatura;
    }
    public void set__deleted(Boolean __deleted) {
        this.__deleted = __deleted;
    }
    public Long getBillingId() {
        return billingId;
    }
    public Long getCostumerId() {
        return costumerId;
    }
    public Long getBillingVencimento() {
        return billingVencimento;
    }
    public String getBellingStatus() {
        return bellingStatus;
    }
    public Long getBillingDataPagamento() {
        return billingDataPagamento;
    }
    public Double getBillingValorFatura() {
        return billingValorFatura;
    }
    public Boolean get__deleted() {
        return __deleted;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }


}

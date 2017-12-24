package com.spg.invoiceportal.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spg.invoiceportal.db.Invoice;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceResponseDTO {
	private List<Invoice> invoices;
	private String error;
	
	public List<Invoice> getInvoices() {
		return invoices;
	}
	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
}

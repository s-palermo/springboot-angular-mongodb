package com.spg.invoiceportal.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.codecs.pojo.annotations.BsonId;

public class Invoice {
	@BsonId
	private String id;
	private String po_number;
	private String invoice_date;
	private String due_date;
	private long amount_cents;
	private String created_at;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPo_number() {
		return po_number;
	}
	public void setPo_number(String po_number) {
		this.po_number = po_number;
	}
	public String getInvoice_date() {
		return invoice_date;
	}
	public void setInvoice_date(String invoice_date) {
		this.invoice_date = invoice_date;
	}
	public String getDue_date() {
		return due_date;
	}
	public void setDue_date(String due_date) {
		this.due_date = due_date;
	}
	public long getAmount_cents() {
		return amount_cents;
	}
	public void setAmount_cents(long amount_cents) {
		this.amount_cents = amount_cents;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}	
	
	public void validate() throws ParseException {
		String dateRegex = "^(0[1-9]|1[0-2])[\\/](0[1-9]|[12]\\d|3[01])[\\/](19|20)\\d{2}$";
		String poRegex = "Z[A-F0-9]{10}";
		if(id == null || po_number == null || invoice_date == null || due_date == null )
		{
			throw new IllegalArgumentException("All values must be present.  "
					+ "Example request:\n{ \r" + 
					"“id”:​ ​123, “po_number”:​ ​“Z12345ABCDE”, “invoice_date”:​ ​“03/01/2017”, “due_date”:​ ​“03/15/2017”, “amount_cents”:​ ​15000 \r\n" + 
					"} \r\n" + 
					" ");			
		}
		
		if(!po_number.matches(poRegex))
		{
			throw new IllegalArgumentException("PO Number must start with 'Z' followed by ten characters of any combination of numbers and the letters A through F.  Example:Z12345ABCDE");
		}
		else if(amount_cents < 1)
		{
			throw new IllegalArgumentException("Invoice amount must be greater than 0");
		}
		else if(!invoice_date.matches(dateRegex) || !due_date.matches(dateRegex) )
		{
			throw new IllegalArgumentException("Dates must be VALID dates in mm/dd/yyyy format, between 01/01/1900 and 12/31/2099.");
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date invoiceDate = dateFormat.parse(invoice_date);
		Date dueDate = dateFormat.parse(due_date);
		if(!dueDate.after(invoiceDate))
		{
			throw new IllegalArgumentException("Due date must be later than the invoice date.");
		}
	}
}

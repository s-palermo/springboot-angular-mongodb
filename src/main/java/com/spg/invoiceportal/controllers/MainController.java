package com.spg.invoiceportal.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spg.invoiceportal.db.DatabaseManagerAsync;
import com.spg.invoiceportal.db.Invoice;
import com.spg.invoiceportal.dto.InvoiceResponseDTO;

@Controller
@CrossOrigin(origins = "*")
public class MainController {

    @RequestMapping(value="/",method = RequestMethod.GET)
    public String index(){
    	return "forward:index.html";
    }
    
    @RequestMapping(value="/api/invoice/create",method = RequestMethod.POST)
    public ResponseEntity<InvoiceResponseDTO> createInvoice(@RequestBody Invoice invoice){
    	InvoiceResponseDTO response = new InvoiceResponseDTO();
    	try 
    	{
    		invoice.validate();
    		if(DatabaseManagerAsync.getInstance().getInvoice(invoice.getId()) != null)
    		{
    			response.setError("Invoice with id " + invoice.getId() + " already exists.");
    			return new ResponseEntity<InvoiceResponseDTO>(response,HttpStatus.CONFLICT);
    		}
    		invoice.setCreated_at(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").format(new Date()));
			DatabaseManagerAsync.getInstance().saveInvoice(invoice);
		} 
    	catch (IllegalArgumentException ex) 
    	{			
    		response.setError(ex.getMessage());
			return new ResponseEntity<InvoiceResponseDTO>(response,HttpStatus.BAD_REQUEST);
		} 
    	catch (Exception ex)
    	{
    		response.setError(ex.getMessage());
    		return new ResponseEntity<InvoiceResponseDTO>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	
    	response.setInvoices(new ArrayList<Invoice>(Arrays.asList(invoice)));
    	return new ResponseEntity<InvoiceResponseDTO>(response,HttpStatus.CREATED);
    }
    
    @RequestMapping(value="/api/invoice/getall",method = RequestMethod.GET)
    public ResponseEntity<InvoiceResponseDTO> getInvoices()
    {
    	InvoiceResponseDTO response = new InvoiceResponseDTO();
    	try
    	{
    		response.setInvoices(DatabaseManagerAsync.getInstance().getInvoices());
    		return new ResponseEntity<InvoiceResponseDTO>(response,HttpStatus.OK);
    	}
    	catch(Exception e)
    	{
    		response.setError(e.getMessage());
    		return new ResponseEntity<InvoiceResponseDTO>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    @RequestMapping(value="/api/invoice/update",method = RequestMethod.PUT)
    public ResponseEntity<InvoiceResponseDTO> updateInvoice(@RequestBody Invoice invoice)
    {
    	InvoiceResponseDTO response = new InvoiceResponseDTO();
    	try
    	{
    		invoice.validate();
    		Invoice foundInvoice = DatabaseManagerAsync.getInstance().getInvoice(invoice.getId());
    		if(foundInvoice == null)
    		{
    			response.setError("Invoice with id " + invoice.getId() + " not found.");
    			return new ResponseEntity<InvoiceResponseDTO>(response,HttpStatus.NOT_FOUND);
    		}
    		invoice.setCreated_at(foundInvoice.getCreated_at());
    		DatabaseManagerAsync.getInstance().saveInvoice(invoice);
    	}
    	catch (IllegalArgumentException ex) 
    	{			
    		response.setError(ex.getMessage());
			return new ResponseEntity<InvoiceResponseDTO>(response,HttpStatus.BAD_REQUEST);
		} 
    	catch(Exception ex)
    	{
    		response.setError(ex.getMessage());
    		return new ResponseEntity<InvoiceResponseDTO>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	
    	response.setInvoices(new ArrayList<Invoice>(Arrays.asList(invoice)));
    	return new ResponseEntity<InvoiceResponseDTO>(response,HttpStatus.OK);
    }
    
    @RequestMapping(value="/api/invoice/delete",method = RequestMethod.DELETE)
    public ResponseEntity<InvoiceResponseDTO> deleteInvoice(@RequestParam(name = "invoiceId", required=false) String invoiceId)
    {
    	InvoiceResponseDTO response = new InvoiceResponseDTO();
    	try
    	{
    		if(invoiceId==null)
    		{
    			response.setError("Request parameter 'invoiceId' is required.");
    			return new ResponseEntity<InvoiceResponseDTO>(response,HttpStatus.BAD_REQUEST);
    		}
    		DatabaseManagerAsync.getInstance().deleteInvoice(invoiceId);
    	}
    	catch(NoSuchElementException ex)
    	{
    		response.setError("Invoice with id " + invoiceId + " not found.");
    		return new ResponseEntity<InvoiceResponseDTO>(response,HttpStatus.BAD_REQUEST);
    		
    	}
    	catch(Exception e)
    	{
    		response.setError(e.getMessage());
    		return new ResponseEntity<InvoiceResponseDTO>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	
    	return new ResponseEntity<InvoiceResponseDTO>(HttpStatus.NO_CONTENT);
    }
}

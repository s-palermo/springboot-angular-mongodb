package com.spg.invoiceportal.db;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;


public class DatabaseManagerAsync
{
	private static volatile DatabaseManagerAsync instance;//lazy initialization
	private static MongoClient mongoClient;
	private static CodecRegistry pojoCodecRegistry;
	private static MongoDatabase database;
	private static MongoCollection<Invoice> invoiceCollection;
	
	
	private DatabaseManagerAsync() throws InterruptedException
	{
		
		mongoClient = MongoClients.create("mongodb://localhost");
       
        pojoCodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        database = mongoClient.getDatabase("Zola-Invoices").withCodecRegistry(pojoCodecRegistry);

        invoiceCollection = database.getCollection("Invoices", Invoice.class);
	}

	public static DatabaseManagerAsync getInstance() throws InterruptedException
	{
		if(instance == null)
		{
			synchronized(DatabaseManagerAsync.class)
			{
				if(instance == null)
				{
					instance = new DatabaseManagerAsync();
				}
			}			
		}
		
		return instance;
	}		
	
	@SuppressWarnings("unchecked")
	public void saveInvoiceAsync(Invoice invoice,final CountDownLatch countLatch)
	{
		DefaultCallback saveCallback = new DefaultCallback(countLatch);
		invoiceCollection.replaceOne(eq("_id", invoice.getId()), invoice, new UpdateOptions().upsert(true),saveCallback);
	}	
	
	@SuppressWarnings("unchecked")
	public void saveInvoice(Invoice invoice) throws Exception
	{

		CountDownLatch saveLatch = new CountDownLatch(1);
		DefaultCallback saveCallback = new DefaultCallback(saveLatch);
		invoiceCollection.replaceOne(eq("_id", invoice.getId()), invoice, new UpdateOptions().upsert(true),saveCallback);
		saveLatch.await();
		
		if(saveCallback.exception != null)
		{
			throw saveCallback.exception;
		}
	}	
	
	public Invoice getInvoice(String id) throws Exception
	{
		final CountDownLatch countLatch = new CountDownLatch(1);
		FindInvoiceCallback findCallBack = new FindInvoiceCallback(countLatch);
		invoiceCollection.find(eq("_id", id)).first(findCallBack);
		countLatch.await();
		
		if(findCallBack.exception!=null)
		{
			throw findCallBack.exception;
		}
		
		return findCallBack.invoice;
	}
	
	@SuppressWarnings("unchecked")
	public List<Invoice> getInvoices() throws Exception
	{
		CountDownLatch findLatch = new CountDownLatch(1);
		DefaultCallback findAllCallback = new DefaultCallback(findLatch);
		List<Invoice> invoices = new ArrayList<Invoice>();
		
		Block<Invoice> printBlock = new Block<Invoice>() {
		    @Override
		    public void apply(final Invoice invoice) {
		    	invoices.add(invoice);
		    }
		};

		invoiceCollection.find().forEach(printBlock, findAllCallback);
		findLatch.await();
		if(findAllCallback.exception != null)
		{
			throw findAllCallback.exception;
		}
		return invoices;
	}
			
	public void deleteInvoice(String invoiceId) throws Exception
	{       
		CountDownLatch deleteLatch = new CountDownLatch(1);
		DeleteCallback deleteCallback = new DeleteCallback(deleteLatch);
		
		invoiceCollection.deleteOne(eq("_id", invoiceId), deleteCallback);
		
		deleteLatch.await();
		
		if(deleteCallback.exception != null)
		{
			throw deleteCallback.exception;
		}
	}	
	
	@SuppressWarnings("rawtypes")
	private static class DefaultCallback implements SingleResultCallback
	{
		private CountDownLatch countLatch;
		private Exception exception = null;
		public DefaultCallback(CountDownLatch countLatch)
		{
			this.countLatch = countLatch;
		}

		@Override
		public void onResult(Object result, Throwable thr)
		{			
			if(thr!=null)
			{
				thr.printStackTrace();
				this.exception = (Exception)thr;
			}		
            if(countLatch!=null)
			{
            	countLatch.countDown();			
			}
		}		
	}	
	
	private static class DeleteCallback implements SingleResultCallback<DeleteResult>
	{
		private CountDownLatch countLatch;
		private Exception exception = null;
		
		public DeleteCallback(CountDownLatch countLatch)
		{
			this.countLatch = countLatch;
		}
		
		@Override
		public void onResult(DeleteResult result, Throwable thr) {
			if(thr!=null)
			{
				thr.printStackTrace();
				this.exception = (Exception)thr;
			}		
			else if(result.getDeletedCount() < 1)
			{
				this.exception = new NoSuchElementException("Document not found.");
			}
            if(countLatch!=null)
			{
            	countLatch.countDown();			
			}
		}		
	}
	
	private static class FindInvoiceCallback implements SingleResultCallback<Invoice>
	{
		private static CountDownLatch countLatch;
		private Invoice invoice;
		private Exception exception = null;
		
		public FindInvoiceCallback(CountDownLatch countLatch)
		{
			FindInvoiceCallback.countLatch = countLatch;
		}

		@Override
		public void onResult(Invoice invoice, Throwable thr)
		{
			this.invoice = invoice;
			if(thr!=null)
			{
				exception.printStackTrace();
				this.exception = (Exception)thr;
			}			
			countLatch.countDown();
		}		
	}	
}

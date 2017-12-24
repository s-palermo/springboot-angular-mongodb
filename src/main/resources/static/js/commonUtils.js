function formatDate(date) {
	var splitString = date.split("-");     
     
	var formattedDate = splitString[1] + "/" + splitString[2] + "/" + splitString[0];

	return formattedDate;
}

function unFormatDate(date) {
	var splitString = date.split("/");     
     
	var formattedDate = splitString[2] + "-" + splitString[0] + "-" + splitString[1];
	
    return formattedDate;
}

function cleanInvoices(invoices) {
	invoices.forEach(function(element) {
		element.invoice_date = unFormatDate(element.invoice_date);
		element.due_date = unFormatDate(element.due_date);
	});
	
	return invoices;
}
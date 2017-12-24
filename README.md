# Spring-Boot AngularJS MongoDB Example Project

This project creates an example Invoice Portal using Spring-Boot, AngularJS, and MongoDB

### Prerequisites


| Prerequisite | Installation Instructions |
| ------ | ------ |
| Java JDK 1.8 | [Oracle JDK 8 Install] [PlDb] |
| Maven 3.3 or later | [Apache Maven Install] [PlGh] |
| MongoDB 3.5 or later | [MongoDB Install] [PlGd] |
Make sure these prerequisites are correctly installed and added to the System Path before proceding.
### Configuration

* To host this site for access to remote users update the src\main\resources\static\config.json file.
Edit the "baseUrl" value to the hostname and port of the web application.
* To set the server port the web application will start on edit the src\main\resources\application.yml file.
Change the "port" value to your desired port.
* To edit the MongoDB connection (localhost by default) change the connection string to point to the desired MongoDB server in the com.spg.invoiceportal.db.DatabaseManagerAsync class.  (TODO allow this to be configured through application.yml)

### Building

From the root directory where the pom.xml file is located, open a command prompt and run the following command:
```sh
mvn clean package
```

This will create a runnable jar in the /target directory.

### Running

From the root directory where the pom.xml file is located, open a command prompt and run the following command:
```sh
mvn spring-boot:run
```

Optionally, you can also run it as a service.  For information on installing a spring-boot app as a service visit [Spring Boot Deployment Instructions] [PlOd]

### API Endpoints

In addition to the portal, the application hosts a number of API endpoints that can be utilized.
| Action | CRUD Operation | Endpoint |
| ------ | ------ | ------ |
| Create Invoice | POST | /api/invoice/create
| Get Invoices | GET | /api/invoice/getall
| Update Invoice | PUT | /api/invoice/update
| Delete Invoice | DELETE | /api/invoice/delete

##### Create/Update Invoice
Accepts: application/json
Example:
```json
{ 
    “id”:123, 
    “po_number”:“Z12345ABCDE”, 
    “invoice_date”:“01/03/2017”, 
    “due_date”:“01/15/2017”,
    “amount_cents”:15000
}
```
Constraints
* Cannot create an invoice with an id that currently exists.  Use update method.
* po_number must match the regex Z[A-F0-9]{10}
* dates must be in mm/dd/yyyy format
* due_date must come after the invoice_date
* amount_cents must be greater than 0

Returns: application/json
Response will contain the updated/created invoice or an error if occured.
Example Success:
```json
{
  "invoices": [
    {
      "id": "123",
      "po_number": "Z12345ABCDE",
      "invoice_date": "12/10/2017",
      "due_date": "12/15/2017",
      "amount_cents": 90000,
      "created_at": "12/23/2017 14:49:29 EST"
    }
  ]
}
```
Example Failure:
```json
{
  "error": "Due date must be later than the invoice date."
}
```
##### Get Invoices
Returns the complete list of invoices.
Example Response:
```json
{  
   "invoices":[  
      {  
         "id":"123",
         "po_number":"Z12345ABCDE",
         "invoice_date":"12/10/2017",
         "due_date":"12/15/2017",
         "amount_cents":90000,
         "created_at":"12/23/2017 14:49:29 EST"
      },
      {  
         "id":"124",
         "po_number":"Z943EBFA865",
         "invoice_date":"12/11/2017",
         "due_date":"12/18/2017",
         "amount_cents":500,
         "created_at":"12/23/2017 14:50:48 EST"
      },
      {  
         "id":"125",
         "po_number":"ZB43EBFA865",
         "invoice_date":"12/11/2017",
         "due_date":"12/18/2017",
         "amount_cents":20000,
         "created_at":"12/23/2017 14:51:01 EST"
      },
      {  
         "id":"052",
         "po_number":"ZB43EBFA875",
         "invoice_date":"01/14/2017",
         "due_date":"01/22/2017",
         "amount_cents":50000,
         "created_at":"12/23/2017 14:52:59 EST"
      },
      {  
         "id":"054",
         "po_number":"Z443FBFA875",
         "invoice_date":"01/14/2017",
         "due_date":"01/22/2017",
         "amount_cents":30000,
         "created_at":"12/23/2017 14:53:10 EST"
      }
   ]
}
```

##### Delete Invoice
Request Parameter:invoiceId (the id of the invoice to be deleted)
Example Request: /api/invoice/delete?invoiceId=050

Returns: application/json
Response will contain the invoice that has just been deleted

   [PlDb]: <https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html>
   [PlGh]: <https://maven.apache.org/install.html>
   [PlGd]: <https://docs.mongodb.com/getting-started/shell/installation/>
   [PlOd]: <https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html>

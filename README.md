![IronHack Logo](https://s3-eu-west-1.amazonaws.com/ih-materials/uploads/upload_d5c5793015fec3be28a63c4fa3dd4d55.png)

# Midterm - Bank system 
<p align="center"><strong> Cristian Saavedra </strong></p>

The goal of this project is to build a banking system.

## Setup

To run this project locally do the following after cloning the project:

1. **Create two databases:** `midterm` and `midterm_test`

2. **Run** `mvn spring-boot:run` to launch the application

3. **Swagger**: The documentation for all requests is available in [Swagger](http://localhost:8080/swagger-ui.html) by entering the following link (http://localhost:8080/swagger-ui.html) while having the program rolling in the terminal.

4. Open the **Postman** in your terminal and then you can start making requests. There is a collection `midtetrm.json` to upload.


First, create this schemas:

```
CREATE SCHEMA midterm;
CREATE SCHEMA midterm_test;
USE midterm;
```

- **Important** : an accountholder only can be created when an account is created. Only can have one username. One accountholder can have diferent account types, but can acces to all of them with its unique username and pasword.  

## Endpoints Documentation

| Method | Endpoint        |                    Response                     |
| ------ | --------------- | :---------------------------------------------: |
| GET    | /accountholders |        retrieved data with all accountholders   |
| GET    | /account/user/balance |      Find all balances by accountuser     |
| GET   | /account/user/balance/{id}|  Owner can find the balance of its account giving the account id, username and password                |
| POST   | /transaction   |          Make a transaction                      |
| GET    | /users         | retrieved data with all users |
| GET    | /account/admin/{id}  |     Admin find an account by his id        |
| POST   | /debit/admin/  |     Admin debit an account        |
| POST   | /credit/admin/  |     Admin credit an account        |
| POST   | /user/thirdparty  |     Admin create a thirdparty user      |
| PATCH  | /admin/remove/frozen/{id}  |     Admin remove status frozen        |
| GET    | /checkings  |     Find all checking accounts        |
| POST    | /checking/ |     Create a checking/student account        |
| GET    | /creditcards  |     Find all creditcard accounts        |
| POST    | /creditcard  |     Create a creditcard account        |
| GET    | /savings  |     Find all savings accounts        |
| POST    | /saving/  |     Create a saving account        |
| GET    | /students  |     Find all students accounts        |
| POST    | /debit/thirdparty  |     Thirdparty debitt an account        |
| POST    | /credit/thirdparty  |     Thirdparty credit an account        |


## All routes are protected: 

1. **Admin** role ("ROLE_ADMIN") is required for POST requests (create all accounts by type and thirdparty, to **credit** and **debit** to an account), GET requests (find all accounts by type) and PATCH request (remove frozen status).

2. **Third Party** role ("ROLE_THIRDPARTY") is required for POST requests, to **credit** and **debit** to another account, giving the properly secretKey.

3. **Account Holder** role ("ROLE_ACCOUNTHOLDER") is requiered for GET requests (to find the balance of all the accounts by accountholder or only the balance or one of them giving the account id) and POST requests (to **make a transaction**)

## Add this within your database midterm to use security

Once you have run the application, introduce these data into that tables to start to using it.

```
INSERT INTO USER (dtype,username, password) values
("ThirdParty","user", "$2a$10$/uA3Dn9tZq69CTXsMuL31uN5PXRm7j0BrbmDRD./pLxSDtNUvKx2O"),
("ThirdParty","pepe", "$2a$10$2ajpVTRFnkYYGeiOQIWuzOJE47zRGr.mD7LeI3wj313QtMeF9co.S"),
("Admin","admin","$2a$10$Xbhdb3dGh.AhmSZyGsnZC.Gr.E6LDN2o6cykyMG04wO7H7UPmEsNq");

INSERT INTO role (role, user_id) values
("ROLE_THIRDPARTY", 1),
("ROLE_ADMIN", 3),
("ROLE_THIRDPARTY", 2);
```
- User: admin
- Password: admin

- User: pepe
- Pasword: pepe

- User: user
- Password: user

## Buisiness Logic

1. The system have 4 types of accounts: StudentChecking, Checking, Savings, and CreditCard, they are created by an admin.

2. The system  have 3 types of Users: Admins, AccountHolders and ThirdParty. ThirdParty and admin can credit and debit into any account, thirparty must know the secretkey of the account where it's pretending to credit and debit. AccountHolders can make a transaction if they have enough funds and who is making it isn't frozen.

3.  Accounts logic:

### Saving accounts:

- Default interest rate of 0.0025

- Can be instantiated with an interest rate other than the default, with a maximum interest rate of 0.5.

- Default minimumBalance of 1000. Can be instantiated with other than the default but no lower than 100.

- Interest on savings accounts is added to the account annually at the rate of specified interestRate per year. The method check, that controls if the interest has been applyed, is called when the user acces into its account given its account id and when it makes a transaction. Even if you haven't accessed into your account for a long time, the first time you access the interest will be added properly.

## CreditCard accounts:

-  Default creditLimit of 100. Can be instantiated with other than the default higher than 100 but not higher than 100000.

-  Default interestRate of 0.2. Can be instantiated with other than the default less than 0.2 but not lower than 0.1.

- Interest on credit cards is added to the balance monthly. The method check, that controls if the interest has been applyed, is called when the user acces into its account given its account id and when it makes a transaction. Even if you haven't accessed into your account for a long time, the first time you access the interest will be added properly.


## Checking accounts:

- If the primaryOwner is less than 24, a **StudentChecking** account is created, otherwise, a regular **Checking** Account.

- MinimumBalance of 250 and a monthlyMaintenanceFee of 12 by default.

## Penalty fee:

- The penaltyFee for all accounts is 40 and if any account drops below the minimumBalance, the penaltyFee is deducted from the balance automatically. The minumum balance that an account can stand is -40.

## Transaction:

- There is a table that holds all transactions made it.

- The transfer should only be processed if the account has sufficient funds. The user must provide the Primary or Secondary owner name and the id of the account and type that should receive the transfer.

## Fraud Detection 

- Transactions made in 24 hours that total amount to more than 150% of the customers highest daily total transactions amount in any other 24 hour period.

- More than 2 transactions occuring on a single account within a 1 second period.

- Admin is the only one who can remove Frozen status.


## Interpretation: 

- ThirdParty creation: hashkey is taken as password, like the creations of other users. 

- ThirdParty credit an debit methods: `must provide their hashed key in the header of the HTTP request`, as a user with username and pasword, when it is giving those data in the authentification, the hashedkey (password) is being given by the header of the HTTP request.

- Fraud detection: the first type of fraud is interpretated as "total amount of money transferred".

- CreditCard doesn't have status, so, it can't be frozen. Also, it hasn't got secret key, as a result, thirparty cannot  debit or credit creditcard accounts.

- The creation of accounts can be make giving a existent accountHolder id. 

- In the transaction endpoint, the user, who is making it, should give the account id and its type (Student, CreditCard, Checking, Saving) and account id and type of the receptor either.


## Tools

- Java as the foundation to build the app's backend
- Spring JPA for data persistence
- MySQL to handle platform models and database
- Swagger as a UI design to test controllers



<table>
<td align="center"><a href="https://github.com/ccsi923"><img src="https://avatars2.githubusercontent.com/u/65124499?s=400&v=4" width="100px;" alt="Cristian avatar"/><br/><sub><b>Cristian Saavedra</b></sub></a><br/><a href="https://github.com/ccsi923"></a>
</table>

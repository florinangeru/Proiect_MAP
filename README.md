# Proiect_MAP


Topic:
banking application (accounts, bank statements, transactions, cards,services) 


 Project Requirements – MAP Sem 2 / 2023 

Each student will work on an individual or in a team. In order for a project to be graded, it must:  
• not have compilation errors  
• implement all requirements  
• provide .zip file with the complete solution  

Timeline:  
• Submit project (zip and documentation) by May - end of day  

Project will be submitted via Teams.  

Assignment definition  
Select a system to be implemented that allows at least 10 actions/queries to be performed on at least 8 types of objects.  

Implementation  
Implement a project using the Java language.  

The application will:  
• include simple classes with private/protected attributes and methods  
• include abstract classes and interfaces with default behaviour  
• follow OOP principles while defining the classes and their interaction  
• include at least 2 different collection interfaces, each with multiple implementations (e.g. at least 2 List implementations, at list 2 Map implementations etc.), capable of administering the objects in the application  
• use inheritance and polymorphism for some of the classes used within the collections  
• at least one service class that exposes the system's operations  
• a main class that calls the service's methods  
• define custom Exceptions and use them to make decisions  
• use Enums  

Storage  
• CSV files will be used to store at least 4 types of objects. Each column in the file is separated with a comma.  
Example: name,surname,age or use data bases  
• Generic singleton services will be created for reading and writing from/to files  
• At system startup, the data will be automatically loaded from the files.  

Auditing  
An auditing service will need to be created that will log to a CSV file each time an action defined in the service is performed.  
Structure of the file: name_of_action,timestamp.

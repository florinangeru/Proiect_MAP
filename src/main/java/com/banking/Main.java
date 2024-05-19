package com.banking;

import com.banking.enums.AccountType;
import com.banking.exceptions.InsufficientFundsException;
import com.banking.exceptions.InvalidAccountException;
import com.banking.models.*;
import com.banking.services.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final Scanner scanner = new Scanner(System.in);
    private static final BankService bankService = new BankService();
    private static final Random random = new Random();


    public static void main(String[] args) {
        boolean exit = false;
        while (!exit) {
            displayMainMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    createCustomer();
                    break;
                case 2:
                    createAccount();
                    break;
                case 3:
                    makeTransaction();
                    break;
                case 4:
                    generateBankStatement();
                    break;
                case 5:
                    listCustomers();
                    break;
                case 6:
                    listAccounts();
                    break;
                case 7:
                    editCustomer();
                    break;
                case 8:
                    editAccounts();
                    break;
                case 9:
                    blockUnblockCard();
                    break;
                case 10:
                    getBalance();
                    break;
                case 11:
                    listCards();
                    break;
                case 12:
                    exit = true;
                    System.out.println("Exiting the application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\nWelcome to the Banking Application!");
        System.out.println("1. Create Customer");
        System.out.println("2. Create Account");
        System.out.println("3. Make Transaction");
        System.out.println("4. Generate Bank Statement");
        System.out.println("5. List Customers");
        System.out.println("6. List Accounts");
        System.out.println("7. Edit Customer");
        System.out.println("8. Edit Accounts");
        System.out.println("9. Block/Unblock Card");
        System.out.println("10. Get Balance");
        System.out.println("11. List Cards");
        System.out.println("12. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void createCustomer() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter surname: ");
        String surname = scanner.nextLine();
        System.out.print("Enter age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        bankService.createCustomer(name, surname, age);
        System.out.println("Customer created successfully.");
    }

    private static void createAccount() {
        listCustomers();
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();
        String type = null;

        System.out.println("Select Account type:");
        System.out.println("1. PRIMARY");
        System.out.println("2. SAVINGS");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        switch (choice) {
            case 1:
                type = "PRIMARY";
                break;
            case 2:
                type = "SAVINGS";
                break;
            default:
                System.out.println("Invalid choice.");
                logger.error("Invalid choice account type");
        }

        if (type != null) {
            try {
                bankService.createAccount(customerId, AccountType.valueOf(type));
                System.out.println("Account created successfully.");
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid account type.");
                logger.error("Invalid account type: {}", type, e);
            } catch (InvalidAccountException e) {
                System.out.println(e.getMessage());
                logger.error("Error creating account: {}", e.getMessage(), e);
            }
        }
    }

    private static void makeTransaction() {
        listCustomers();
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();

        try {
            Customer customer = bankService.getCustomerById(customerId);
            if (customer == null) {
                System.out.println("Customer not found.");
                return;
            }

            List<Account> accounts = customer.getAccounts();
            if (accounts.isEmpty()) {
                System.out.println("No accounts found for this customer.");
                return;
            }

            System.out.println("Accounts:");
            for (int i = 0; i < accounts.size(); i++) {
                System.out.println((i + 1) + ". " + accounts.get(i));
            }
            System.out.print("Select account: ");
            int accountChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (accountChoice < 1 || accountChoice > accounts.size()) {
                System.out.println("Invalid choice.");
                return;
            }

            Account account = accounts.get(accountChoice - 1);

            System.out.println("Select transaction type:");
            System.out.println("1. Transfer");
            System.out.println("2. Withdrawal");
            System.out.println("3. Deposit");
            System.out.print("Enter your choice: ");
            int transactionChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            double amount;
            switch (transactionChoice) {
                case 1:
                    // Transfer
                    System.out.print("Enter destination account ID: ");
                    String destinationAccountId = scanner.nextLine();
                    Account destinationAccount = bankService.getAccountById(destinationAccountId);
                    if (destinationAccount == null) {
                        System.out.println("Destination account not found.");
                        return;
                    }
                    System.out.print("Enter amount to transfer: ");
                    amount = scanner.nextDouble();
                    scanner.nextLine(); // Consume newline
                    if (amount <= 0) {
                        System.out.println("Amount must be positive.");
                        return;
                    }
                    bankService.transfer(account.getAccountId(), destinationAccount.getAccountId(), amount);
                    System.out.println("Transfer completed successfully.");
                    break;
                case 2:
                    // Withdrawal
                    System.out.print("Enter amount to withdraw: ");
                    amount = scanner.nextDouble();
                    scanner.nextLine(); // Consume newline
                    if (amount <= 0) {
                        System.out.println("Amount must be positive.");
                        return;
                    }
                    bankService.withdraw(account.getAccountId(), amount);
                    System.out.println("Withdrawal completed successfully.");
                    break;
                case 3:
                    // Deposit
                    System.out.print("Enter amount to deposit: ");
                    amount = scanner.nextDouble();
                    scanner.nextLine(); // Consume newline
                    if (amount <= 0) {
                        System.out.println("Amount must be positive.");
                        return;
                    }
                    bankService.deposit(account.getAccountId(), amount);
                    System.out.println("Deposit completed successfully.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (InvalidAccountException | InsufficientFundsException e) {
            System.out.println(e.getMessage());
            logger.error("Error making transaction: {}", e.getMessage(), e);
        }
    }


    private static void generateBankStatement() {
        listAccounts();
        System.out.print("Enter account ID: ");
        String accountId = scanner.nextLine();
        System.out.print("Enter start date (yyyy-MM-dd): ");
        String startDateStr = scanner.nextLine();
        System.out.print("Enter end date (yyyy-MM-dd): ");
        String endDateStr = scanner.nextLine();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            BankStatement statement = bankService.generateBankStatement(accountId, startDate, endDate);
            displayBankStatement(statement);
        } catch (InvalidAccountException e) {
            System.out.println(e.getMessage());
            logger.error("Error generating bank statement: {}", e.getMessage(), e);
        } catch (Exception e) {
            System.out.println("Error generating bank statement: " + e.getMessage());
            logger.error("Error generating bank statement: {}", e.getMessage(), e);
        }
    }

    private static void listCustomers() {
        List<Customer> customers = bankService.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
        } else {
            System.out.println("Customers:");
            for (Customer customer : customers) {
                System.out.println(customer);
            }
        }
    }

    private static void listAccounts() {
        List<Account> accounts = bankService.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
        } else {
            System.out.println("Accounts:");
            for (Account account : accounts) {
                System.out.println(account);
            }
        }
    }

    private static void editCustomer() {
        listCustomers();
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();
        try {
            Customer customer = bankService.getCustomerById(customerId);
            if (customer == null) {
                System.out.println("Customer not found.");
                return;
            }

            System.out.println("Select field to edit:");
            System.out.println("1. Name");
            System.out.println("2. Surname");
            System.out.println("3. Age");
            System.out.println("Or 99. To DELETE Customer");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter new name: ");
                    String name = scanner.nextLine();
                    customer.setName(name);
                    bankService.updateCustomer(customer);
                    System.out.println("Customer updated successfully.");
                    break;
                case 2:
                    System.out.print("Enter new surname: ");
                    String surname = scanner.nextLine();
                    customer.setSurname(surname);
                    bankService.updateCustomer(customer);
                    System.out.println("Customer updated successfully.");
                    break;
                case 3:
                    System.out.print("Enter new age: ");
                    int age = scanner.nextInt();
                    scanner.nextLine();
                    customer.setAge(age);
                    bankService.updateCustomer(customer);
                    System.out.println("Customer updated successfully.");
                    break;
                case 99:
                    System.out.println("Do you want to delete the customer? (yes/no): ");
                    String deleteChoice = scanner.nextLine().toLowerCase();
                    if (deleteChoice.equals("yes")) {
                        if (customer.getAccounts().isEmpty()) {
                            bankService.deleteCustomer(customerId);
                            System.out.println("Customer deleted successfully.");
                        } else {
                            System.out.println("Customer cannot be deleted. They have associated accounts.");
                        }
                    }
                default:
                    System.out.println("Invalid choice.");
            }


        } catch (InvalidAccountException e) {
            System.out.println(e.getMessage());
            logger.error("Error editing customer: {}", e.getMessage(), e);
        }
    }

    private static void editAccounts() {
        listCustomers();
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();
        try {
            Customer customer = bankService.getCustomerById(customerId);
            if (customer == null) {
                System.out.println("Customer not found.");
                return;
            }

            List<Account> accounts = customer.getAccounts();
            if (accounts.isEmpty()) {
                System.out.println("No accounts found for this customer.");
                return;
            }

            System.out.println("Accounts:");
            for (int i = 0; i < accounts.size(); i++) {
                System.out.println((i + 1) + ". " + accounts.get(i));
            }
            System.out.print("Select account: ");
            int accountChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (accountChoice < 1 || accountChoice > accounts.size()) {
                System.out.println("Invalid choice.");
                return;
            }

            Account account = accounts.get(accountChoice - 1);

            if (account instanceof SavingsAccount) {
                System.out.println("1. Change interest rate");
                System.out.println("Or 99. To DELETE Account");
                System.out.print("Enter your choice: ");
                int SavingsAccountChoice = scanner.nextInt();
                scanner.nextLine();
                switch (SavingsAccountChoice) {
                    case 1:
                        System.out.print("Enter new interest rate: ");
                        double interestRate = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline
                        ((SavingsAccount) account).setInterestRate(interestRate);
                        System.out.println("Interest rate updated successfully.");
                        break;
                    case 99:
                        System.out.println("Do you want to delete the account? (yes/no): ");
                        String deleteChoice = scanner.nextLine().toLowerCase();
                        if (deleteChoice.equals("yes")) {
                            if (account.getBalance() == 0 && account.getCards().isEmpty()) {
                                bankService.deleteAccount(account.getAccountId());
                                System.out.println("Account deleted successfully.");
                            } else {
                                System.out.println("Account cannot be deleted. It has non-zero balance or associated cards.");
                            }
                        }
                        break;
                    default:
                        System.out.println("Invalid choice.");

                }


            } else if (account instanceof PrimaryAccount) {
                System.out.println("1. Add Card");
                System.out.println("2. Delete Card");
                System.out.println("Or 99. To DELETE Account");
                System.out.print("Enter your choice: ");
                int PrimaryAccountChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (PrimaryAccountChoice) {
                    case 1:
                        StringBuilder cardNumber = new StringBuilder(String.format("%04d", 1000 + random.nextInt(9000)));
                        for (int i=0; i<3; i++){
                            cardNumber.append(String.format("%04d", random.nextInt(9000)));
                        }
                        Card newCard = new Card(cardNumber.toString(), new Date(), account);
                        ((PrimaryAccount) account).addCard(newCard);
                        bankService.addCard(newCard);
                        System.out.println("Card added successfully.");
                        break;
                    case 2:
                        List<Card> cards = account.getCards();
                        if (cards.isEmpty()) {
                            System.out.println("No cards found for this account.");
                            return;
                        }
                        System.out.println("Cards:");
                        for (int i = 0; i < cards.size(); i++) {
                            System.out.println((i + 1) + ". " + cards.get(i));
                        }
                        System.out.print("Select card to delete: ");
                        int cardDeleteChoice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        if (cardDeleteChoice < 1 || cardDeleteChoice > cards.size()) {
                            System.out.println("Invalid choice.");
                            return;
                        }

                        Card cardToDelete = cards.get(cardDeleteChoice - 1);
                        ((PrimaryAccount) account).removeCard(cardToDelete);
                        bankService.removeCard(cardToDelete); // Add this line to ensure the card is removed from the service
                        System.out.println("Card deleted successfully.");
                        break;
                    case 99:
                        System.out.println("Do you want to delete the account? (yes/no): ");
                        String deleteChoice = scanner.nextLine().toLowerCase();
                        if (deleteChoice.equals("yes")) {
                            if (account.getBalance() == 0 && account.getCards().isEmpty()) {
                                bankService.deleteAccount(account.getAccountId());
                                System.out.println("Account deleted successfully.");
                            } else {
                                System.out.println("Account cannot be deleted. It has non-zero balance or associated cards.");
                            }
                        }
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            }

        } catch (InvalidAccountException e) {
            System.out.println(e.getMessage());
            logger.error("Error editing account: {}", e.getMessage(), e);
        }
    }

    private static void blockUnblockCard() {
        listCustomers();
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();
        try {
            Customer customer = bankService.getCustomerById(customerId);
            if (customer == null) {
                System.out.println("Customer not found.");
                return;
            }

            List<Account> accounts = customer.getAccounts();
            if (accounts.isEmpty()) {
                System.out.println("No accounts found for this customer.");
                return;
            }

            System.out.println("Accounts:");
            for (int i = 0; i < accounts.size(); i++) {
                System.out.println((i + 1) + ". " + accounts.get(i));
            }
            System.out.print("Select account: ");
            int accountChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (accountChoice < 1 || accountChoice > accounts.size()) {
                System.out.println("Invalid choice.");
                return;
            }

            Account account = accounts.get(accountChoice - 1);

            List<Card> cards = account.getCards();
            if (cards.isEmpty()) {
                System.out.println("No cards found for this account.");
                return;
            }
            System.out.println("Cards:");
            for (int i = 0; i < cards.size(); i++) {
                System.out.println((i + 1) + ". " + cards.get(i));
            }
            System.out.print("Select card: ");
            int cardChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (cardChoice < 1 || cardChoice > cards.size()) {
                System.out.println("Invalid choice.");
                return;
            }

            Card card = cards.get(cardChoice - 1);
            System.out.println("1. Block Card");
            System.out.println("2. Unblock Card");
            System.out.print("Enter your choice: ");
            int blockUnblockChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (blockUnblockChoice == 1) {
                card.block();
                System.out.println("Card blocked successfully.");
            } else if (blockUnblockChoice == 2) {
                card.unblock();
                System.out.println("Card unblocked successfully.");
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (InvalidAccountException e) {
            System.out.println(e.getMessage());
            logger.error("Error blocking/unblocking card: {}", e.getMessage(), e);
        }
    }

    private static void getBalance() {
        listCustomers();
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();
        try {
            Customer customer = bankService.getCustomerById(customerId);
            if (customer == null) {
                System.out.println("Customer not found.");
                return;
            }

            List<Account> accounts = customer.getAccounts();
            if (accounts.isEmpty()) {
                System.out.println("No accounts found for this customer.");
                return;
            }

            System.out.println("Accounts with balances:");
            for (Account account : accounts) {
                System.out.println(account + " - Balance: " + account.getBalance());
            }
        } catch (InvalidAccountException e) {
            System.out.println(e.getMessage());
            logger.error("Error getting balance: {}", e.getMessage(), e);
        }
    }

    private static void listCards() {
        try {
            List<Account> accounts = bankService.getAllAccounts();
            if (accounts.isEmpty()) {
                System.out.println("No accounts found.");
                return;
            }

            boolean cardsFound = false;
            for (Account account : accounts) {
                List<Card> cards = account.getCards();
                if (cards.isEmpty()) {
                    System.out.println("No cards found for account: " + account.getAccountId());
                } else {
                    cardsFound = true;
                    System.out.println("Cards for account: " + account.getAccountId());
                    for (Card card : cards) {
                        System.out.println(card + " - Status: " + (card.isBlocked() ? "Blocked" : "Active"));
                    }
                }
            }
            if (!cardsFound) {
                System.out.println("No cards found for any account.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("Error listing cards: {}", e.getMessage(), e);
        }
    }


    private static void displayBankStatement(BankStatement statement) {
        System.out.println("Bank Statement ID: " + statement.getStatementId());
        System.out.println("Account ID: " + statement.getAccount().getAccountId());
        System.out.println("Start Date: " + statement.getStartDate());
        System.out.println("End Date: " + statement.getEndDate());
        System.out.println("Transactions:");
        for (Transaction transaction : statement.getTransactions()) {
            System.out.println(transaction);
        }
        System.out.println("Closing Balance: " + statement.getClosingBalance());
    }
}

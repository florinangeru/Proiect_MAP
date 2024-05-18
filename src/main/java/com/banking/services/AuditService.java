package com.banking.services;

import java.io.*;
import java.util.Date;

public class AuditService {
    private static AuditService instance = null;
    private static final String AUDIT_FILE_PATH = "database/audit.csv";

    private AuditService() { }

    public static synchronized AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    public void logAction(String actionName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(AUDIT_FILE_PATH, true))) {
            String timestamp = new Date().toString();
            writer.println(actionName + "," + timestamp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.monframework.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUploadUtil {
    
    private static String uploadDir = "/uploads"; // Répertoire par défaut
    
    public static void setUploadDirectory(String directory) {
        uploadDir = directory;
    }
    
    public static String saveFile(byte[] fileBytes, String originalFilename) 
            throws IOException {
        
        // Créer le répertoire s'il n'existe pas
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // Générer un nom de fichier unique
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;
        
        // Chemin complet
        String filePath = uploadDir + File.separator + filename;
        
        // Écrire le fichier
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(fileBytes);
        }
        
        return filename;
    }
    
    public static String saveFile(byte[] fileBytes) throws IOException {
        return saveFile(fileBytes, "uploaded_file.dat");
    }
    
    public static byte[] readFile(String filename) throws IOException {
        Path path = Paths.get(uploadDir + File.separator + filename);
        return Files.readAllBytes(path);
    }
    
    public static boolean deleteFile(String filename) {
        File file = new File(uploadDir + File.separator + filename);
        return file.delete();
    }
}
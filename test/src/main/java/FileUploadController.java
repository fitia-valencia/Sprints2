package testapp;

import com.monframework.JsonResponse;
import com.monframework.annotation.Controller;
import com.monframework.annotation.PostMapping;
import com.monframework.annotation.UploadedFile;
import com.monframework.annotation.RequestParam;
import com.monframework.util.FileUploadUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class FileUploadController {

    @PostMapping("/upload/single")
    public JsonResponse uploadSingleFile(
            @UploadedFile("file") byte[] fileBytes,
            @RequestParam("description") String description) {

        System.out.println("DEBUG - fileBytes: " + (fileBytes != null ? fileBytes.length + " bytes" : "null"));
        System.out.println("DEBUG - description: " + description);

        if (fileBytes == null) {
            return JsonResponse.error("Le fichier n'a pas été reçu correctement");
        }

        try {
            String filename = FileUploadUtil.saveFile(fileBytes, "uploaded_file");
            return JsonResponse.success("Fichier uploadé avec succès",
                    Map.of("filename", filename, "description", description, "size", fileBytes.length));
        } catch (IOException e) {
            return JsonResponse.error("Erreur lors de l'upload: " + e.getMessage());
        }
    }

    @PostMapping("/upload/multiple")
    public String uploadMultipleFiles(
            @UploadedFile("files") List<byte[]> files,
            @RequestParam("category") String category) {

        List<String> savedFiles = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            try {
                String filename = FileUploadUtil.saveFile(
                        files.get(i),
                        "file_" + i + ".dat");
                savedFiles.add(filename);
            } catch (IOException e) {
                savedFiles.add("ERROR: " + e.getMessage());
            }
        }

        return "Fichiers uploadés (" + category + "): " + savedFiles;
    }

    @PostMapping("/upload/map")
    public String uploadWithMap(@UploadedFile("document") Map<String, Object> fileInfo) {

        String filename = (String) fileInfo.get("filename");
        byte[] bytes = (byte[]) fileInfo.get("bytes");
        String contentType = (String) fileInfo.get("contentType");
        long size = (Long) fileInfo.get("size");

        try {
            String savedName = FileUploadUtil.saveFile(bytes, filename);
            return String.format(
                    "Fichier '%s' (%s, %d bytes) sauvegardé sous le nom: %s",
                    filename, contentType, size, savedName);
        } catch (IOException e) {
            return "Erreur: " + e.getMessage();
        }
    }

    @PostMapping("/upload/mixed")
    public String uploadMixed(
            @UploadedFile("avatar") byte[] avatar,
            @UploadedFile("documents") List<byte[]> documents,
            @RequestParam("username") String username) {

        try {
            String avatarName = FileUploadUtil.saveFile(avatar, "avatar_" + username + ".jpg");
            int docCount = documents.size();

            return String.format(
                    "Utilisateur: %s<br>Avatar: %s<br>Documents: %d fichier(s)",
                    username, avatarName, docCount);
        } catch (IOException e) {
            return "Erreur: " + e.getMessage();
        }
    }
}
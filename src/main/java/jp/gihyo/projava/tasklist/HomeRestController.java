package jp.gihyo.projava.tasklist;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class HomeRestController {

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".java")) {
            return "javaファイルを選択してください！";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String content = reader.lines().collect(Collectors.joining("\n"));
            return "受け取ったファイルの内容:\n" + content;
        } catch (IOException e) {
            return "ファイルの読み取り中にエラーが発生しました: " + e.getMessage();
        }
    }
}



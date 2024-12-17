package jp.gihyo.projava.tasklist;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import difflib.DiffUtils;
import difflib.Patch;


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

    @PostMapping("/compareFiles")
    public String compareFiles(@RequestParam("file1") MultipartFile file1,
                               @RequestParam("file2") MultipartFile file2) {
        // 文件验证
        if (file1.isEmpty() || !Objects.requireNonNull(file1.getOriginalFilename()).endsWith(".java") ||
                file2.isEmpty() || !Objects.requireNonNull(file2.getOriginalFilename()).endsWith(".java")) {
            return "比べたいファイルをアップロードしてください";
        }

        try {
            // ファイル内容を読み込む
            List<String> file1Lines = readFileLines(file1);
            List<String> file2Lines = readFileLines(file2);

            // 違うところを表示
            Patch<String> patch = DiffUtils.diff(file1Lines, file2Lines);

            //
            StringBuilder diffResult = new StringBuilder("ファイルの違いは以下の通りです:\n");
            patch.getDeltas().forEach(delta -> {
                diffResult.append("====\n")
                        .append("差分タイプ: ").append(delta.getType()).append("\n")
                        .append("差分内容: ").append(delta.getOriginal().getLines()).append("\n")
                        .append("修正した内容: ").append(delta.getRevised().getLines()).append("\n");
            });

            return diffResult.toString();
        } catch (IOException e) {
            return "エラー: " + e.getMessage();
        }
    }


    private List<String> readFileLines(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            return reader.lines().collect(Collectors.toList());
        }
    }
}






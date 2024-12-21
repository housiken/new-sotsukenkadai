package jp.gihyo.projava.tasklist;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
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

            StringBuilder diffResult = new StringBuilder("ファイルの違いは以下の通りです:\n");
            patch.getDeltas().forEach(delta -> {
                diffResult.append("====\n")
                        .append("差分タイプ: ").append(delta.getType()).append("\n")
                        .append("差分内容: ").append(delta.getSource().getLines()).append("\n")
                        .append("修正した内容: ").append(delta.getTarget().getLines()).append("\n");
            });

            return diffResult.toString();
        } catch (IOException e) {
            return "エラー: " + e.getMessage();
        }
    }

    @PostMapping("/convertFile")
    public String convertFile(@RequestParam("file") MultipartFile file, @RequestParam("format") String format) {
        if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".java")) {
            return "javaファイルを選択してください！";
        }

        if (!format.equals("txt") && !format.equals("pdf")) {
            return "対応している形式はTXTまたはPDFのみです。";
        }

        try {
            String content = new BufferedReader(new InputStreamReader(file.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));

            if (format.equals("txt")) {
                return convertToTxt(content, file.getOriginalFilename());
            } else if (format.equals("pdf")) {
                return convertToPdf(content, file.getOriginalFilename());
            }

        } catch (IOException e) {
            return "ファイルの変換中にエラーが発生しました: " + e.getMessage();
        }

        return "未知のエラーが発生しました。";
    }

    @PostMapping("/generateFlowchart")
    public String generateFlowchart(@RequestParam("flowchartCode") String flowchartCode) {
        if (flowchartCode == null || flowchartCode.isEmpty()) {
            return "プロセス図コードを入力してください。";
        }

        // 在这里你可以使用Mermaid生成流程图的逻辑
        // 比如，返回代码并让前端利用Mermaid.js渲染图表
        return "生成されたフロー図コード:\n" + flowchartCode;
    }

    private String convertToTxt(String content, String originalFilename) throws IOException {
        String newFilename = originalFilename.replace(".java", ".txt");
        File txtFile = new File(newFilename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(txtFile))) {
            writer.write(content);
        }

        return "TXT形式に変換しました。ファイル名: " + txtFile.getAbsolutePath();
    }

    private String convertToPdf(String content, String originalFilename) throws IOException {
        String newFilename = originalFilename.replace(".java", ".pdf");
        File pdfFile = new File(newFilename);

        // PDF変換ロジック（仮実装）
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pdfFile))) {
            writer.write("PDF変換は現在仮実装されています:\n\n");
            writer.write(content);
        }

        return "PDF形式に変換しました。ファイル名: " + pdfFile.getAbsolutePath();
    }

    private List<String> readFileLines(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            return reader.lines().collect(Collectors.toList());
        }
    }
}








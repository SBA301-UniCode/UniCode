package com.example.unicode.ultils;

import com.example.unicode.configuration.JavaConfig;
import com.example.unicode.configuration.LanguageConfig;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Component
public class CodeRunnerService {
    public String run(String fullCode, String inputData, LanguageConfig config) {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("judge_");
            Files.writeString(tempDir.resolve(config.getFileName()), fullCode);

            boolean gsonFromHost = false;
            if (config instanceof JavaConfig) {
                gsonFromHost = copyJudgeGsonJar(tempDir);
            }

            String innerCmd = config.getCompileCmd() + " && " + config.getRunCmd();
            if (config instanceof JavaConfig && !gsonFromHost) {
                String wget = "wget -q -O gson.jar https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar";
                innerCmd = wget + " && " + innerCmd;
            }

            String dockerCmd = String.format(
                    "docker run -i --rm --memory=256m --cpus=1 -v %s:/app -w /app %s sh -c \"%s\"",
                    tempDir.toAbsolutePath().toString().replace("\\", "/"),
                    config.getDockerImage(),
                    innerCmd
            );

            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            ProcessBuilder pb = isWindows
                    ? new ProcessBuilder("cmd", "/c", dockerCmd)
                    : new ProcessBuilder("sh", "-c", dockerCmd);

            Process process = pb.start();

            // Gửi input (đảm bảo có dấu xuống dòng)
            byte[] stdin = (inputData == null ? "" : inputData).getBytes(StandardCharsets.UTF_8);
            try (var os = process.getOutputStream()) {
                os.write(stdin);
                os.write("\n".getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            // Đọc output bằng cách dùng readAllBytes để lấy TRỌN BỘ dữ liệu
            String output;
            String error;
            try (var stdout = process.getInputStream(); var stderr = process.getErrorStream()) {
                output = new String(stdout.readAllBytes(), StandardCharsets.UTF_8).trim();
                error = new String(stderr.readAllBytes(), StandardCharsets.UTF_8).trim();
            }

            if (!process.waitFor(15, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new AppException(ErrorCode.PRACTICAL_EXAM_TIMLE_LIMIT);
            }

            int exit = process.exitValue();
            if (exit != 0) {
                String msg = (error == null || error.isBlank())
                        ? "Process exited with code " + exit
                        : error;
                throw new RuntimeException(msg);
            }

            return output;

        } catch (AppException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("System Error: " + e.getMessage(), e);
        } finally {
            // Xóa file tạm
            if (tempDir != null) {
                try {
                    org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(tempDir.toFile());
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Copy gson.jar từ classpath (maven-dependency-plugin) vào temp để tránh wget trong container.
     * @return true nếu copy thành công, false nếu không có file (fallback: wget trong container).
     */
    private static boolean copyJudgeGsonJar(Path tempDir) {
        try (InputStream in = CodeRunnerService.class.getResourceAsStream("/judge/gson.jar")) {
            if (in == null) return false;
            Files.copy(in, tempDir.resolve("gson.jar"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
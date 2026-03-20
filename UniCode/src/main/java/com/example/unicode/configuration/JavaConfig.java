package com.example.unicode.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class JavaConfig implements LanguageConfig {

    @Override
    public String getDockerImage() {
        return "eclipse-temurin:17-alpine";
    }

    @Override
    public String getFileName() {
        return "Main.java";
    }

    @Override
    public String getCompileCmd() {
        // Thêm -O gson.jar để lưu đúng tên file
        String wgetCmd = "wget -q -O gson.jar https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar";
        return wgetCmd + " && javac -cp gson.jar Main.java";
    }

    @Override
    public String getRunCmd() {
        return "java -cp .:gson.jar Main";
    }

    @Override
    public String wrapCode(String userCode, List<String> paramTypes, String functionName) {
        StringBuilder parseCode = new StringBuilder();
        StringBuilder callParams = new StringBuilder();

        for (int i = 0; i < paramTypes.size(); i++) {
            String type = paramTypes.get(i);
            String var = "p" + i;
            switch (type) {
                case "int", "Integer" ->
                        parseCode.append("            int ").append(var).append(" = toInt(inputs.get(").append(i).append("));\n");
                case "long", "Long" ->
                        parseCode.append("            long ").append(var).append(" = toLong(inputs.get(").append(i).append("));\n");
                case "double", "Double" ->
                        parseCode.append("            double ").append(var).append(" = toDouble(inputs.get(").append(i).append("));\n");
                case "String" ->
                        parseCode.append("            String ").append(var).append(" = inputs.get(").append(i).append(").getAsString();\n");
                case "int[]" ->
                        parseCode.append("            int[] ").append(var).append(" = toIntArray(inputs.get(").append(i).append("));\n");
                case "int[][]" ->
                        parseCode.append("            int[][] ").append(var).append(" = toInt2DArray(inputs.get(").append(i).append("));\n");
                case "boolean" ->
                        parseCode.append("            boolean ").append(var).append(" = inputs.get(").append(i).append(").getAsBoolean();\n");
                case "String[]" ->
                        parseCode.append("            String[] ").append(var).append(" = gson.fromJson(inputs.get(").append(i).append("), String[].class);\n");
                default ->
                        parseCode.append("            Object ").append(var).append(" = gson.fromJson(inputs.get(").append(i).append("), Object.class);\n");
            }
            callParams.append(var);
            if (i < paramTypes.size() - 1) callParams.append(", ");
        }

        return "import java.util.*;\n" +
                "import java.lang.reflect.*;\n" +
                "import com.google.gson.*;\n\n" +
                userCode + "\n\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        Scanner sc = new Scanner(System.in);\n" +
                "        Gson gson = new Gson();\n" +
                "        try {\n" +
                "            StringBuilder sb = new StringBuilder();\n" +
                "            while (sc.hasNextLine()) sb.append(sc.nextLine()).append(\" \");\n" +
                "            String fullInput = sb.toString().trim();\n" +
                "            JsonArray inputs;\n" +
                "            try {\n" +
                "                inputs = JsonParser.parseString(fullInput).getAsJsonArray();\n" +
                "            } catch (Exception e) {\n" +
                "                inputs = new JsonArray();\n" +
                "                for (String part : fullInput.split(\"\\\\\\\\s+\")) {\n" +
                "                    if (!part.isEmpty()) inputs.add(part);\n" +
                "                }\n" +
                "            }\n" +
                "            Solution sol = new Solution();\n" +
                parseCode +
                "            Method m = null;\n" +
                "            for (Method method : Solution.class.getMethods()) {\n" +
                "                if (method.getName().equals(\"" + functionName + "\") && method.getParameterCount() == inputs.size()) {\n" +
                "                    m = method;\n" +
                "                    break;\n" +
                "                }\n" +
                "            }\n" +
                "            if (m == null) {\n" +
                "                for (Method method : Solution.class.getMethods()) {\n" +
                "                    if (!method.getDeclaringClass().equals(Object.class) && method.getParameterCount() == inputs.size()) {\n" +
                "                        m = method;\n" +
                "                        break;\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "            if (m == null) throw new RuntimeException(\"No suitable method to run\");\n" +
                "            Object result = m.invoke(sol, " + callParams + ");\n" +
                "            if (result instanceof int[]) {\n" +
                "                System.out.print(formatIntArray((int[]) result));\n" +
                "            } else if (result instanceof int[][]) {\n" +
                "                System.out.print(formatInt2DArray((int[][]) result));\n" +
                "            } else if (result != null) {\n" +
                "                System.out.print(result.toString());\n" +
                "            }\n" +
                "        } catch (Throwable e) {\n" +
                "            e.printStackTrace(System.err);\n" +
                "            System.exit(1);\n" +
                "        }\n" +
                "    }\n\n" +
                "    private static int toInt(JsonElement e) {\n" +
                "        if (e == null || e.isJsonNull()) return 0;\n" +
                "        if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()) return e.getAsInt();\n" +
                "        return Integer.parseInt(e.getAsString().trim());\n" +
                "    }\n\n" +
                "    private static long toLong(JsonElement e) {\n" +
                "        if (e == null || e.isJsonNull()) return 0L;\n" +
                "        if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()) return e.getAsLong();\n" +
                "        return Long.parseLong(e.getAsString().trim());\n" +
                "    }\n\n" +
                "    private static double toDouble(JsonElement e) {\n" +
                "        if (e == null || e.isJsonNull()) return 0D;\n" +
                "        if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()) return e.getAsDouble();\n" +
                "        return Double.parseDouble(e.getAsString().trim());\n" +
                "    }\n\n" +
                "    /** Giống LeetCode: [1,2,3] — không khoảng trắng sau dấu phẩy (khác Arrays.toString). */\n" +
                "    private static String formatIntArray(int[] a) {\n" +
                "        if (a == null) return \"null\";\n" +
                "        StringBuilder sb = new StringBuilder();\n" +
                "        sb.append('[');\n" +
                "        for (int i = 0; i < a.length; i++) {\n" +
                "            if (i > 0) sb.append(',');\n" +
                "            sb.append(a[i]);\n" +
                "        }\n" +
                "        sb.append(']');\n" +
                "        return sb.toString();\n" +
                "    }\n\n" +
                "    private static String formatInt2DArray(int[][] a) {\n" +
                "        if (a == null) return \"null\";\n" +
                "        StringBuilder sb = new StringBuilder();\n" +
                "        sb.append('[');\n" +
                "        for (int i = 0; i < a.length; i++) {\n" +
                "            if (i > 0) sb.append(',');\n" +
                "            sb.append(formatIntArray(a[i]));\n" +
                "        }\n" +
                "        sb.append(']');\n" +
                "        return sb.toString();\n" +
                "    }\n\n" +
                "    private static int[] toIntArray(JsonElement e) {\n" +
                "        if (e == null || e.isJsonNull()) return new int[0];\n" +
                "        if (e.isJsonArray()) {\n" +
                "            JsonArray arr = e.getAsJsonArray();\n" +
                "            int[] out = new int[arr.size()];\n" +
                "            for (int i = 0; i < arr.size(); i++) out[i] = toInt(arr.get(i));\n" +
                "            return out;\n" +
                "        }\n" +
                "        String raw = unwrapArrayLikeString(e.getAsString());\n" +
                "        if (raw.isEmpty()) return new int[0];\n" +
                "        String[] parts = raw.split(\",\");\n" +
                "        int[] out = new int[parts.length];\n" +
                "        for (int i = 0; i < parts.length; i++) out[i] = Integer.parseInt(parts[i].trim());\n" +
                "        return out;\n" +
                "    }\n\n" +
                "    private static int[][] toInt2DArray(JsonElement e) {\n" +
                "        if (e == null || e.isJsonNull()) return new int[0][0];\n" +
                "        if (e.isJsonArray()) {\n" +
                "            JsonArray outer = e.getAsJsonArray();\n" +
                "            int[][] out = new int[outer.size()][];\n" +
                "            for (int i = 0; i < outer.size(); i++) out[i] = toIntArray(outer.get(i));\n" +
                "            return out;\n" +
                "        }\n" +
                "        return new int[][] { toIntArray(e) };\n" +
                "    }\n\n" +
                "    /** Bỏ lớp ngoài (...), [...] lặp lại — hỗ trợ ví dụ \\\"([1,2,3])\\\". */\n" +
                "    private static String unwrapArrayLikeString(String s) {\n" +
                "        if (s == null) return \"\";\n" +
                "        String raw = s.trim();\n" +
                "        while (raw.length() >= 2) {\n" +
                "            boolean changed = false;\n" +
                "            if (raw.charAt(0) == '(' && raw.charAt(raw.length() - 1) == ')') {\n" +
                "                raw = raw.substring(1, raw.length() - 1).trim();\n" +
                "                changed = true;\n" +
                "            }\n" +
                "            if (raw.length() >= 2 && raw.charAt(0) == '[' && raw.charAt(raw.length() - 1) == ']') {\n" +
                "                raw = raw.substring(1, raw.length() - 1).trim();\n" +
                "                changed = true;\n" +
                "            }\n" +
                "            if (!changed) break;\n" +
                "        }\n" +
                "        return raw;\n" +
                "    }\n" +
                "}\n";
    }
}


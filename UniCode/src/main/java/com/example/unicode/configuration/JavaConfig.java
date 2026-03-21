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
        // gson.jar được copy từ host vào /app trước khi docker run (xem CodeRunnerService)
        return "javac -cp gson.jar Main.java";
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
            String type = paramTypes.get(i) == null ? "Object" : paramTypes.get(i).trim();
            String var = "p" + i;
            appendParamParse(parseCode, type, i, var);
            callParams.append(var);
            if (i < paramTypes.size() - 1) callParams.append(", ");
        }

        return "import java.util.*;\n" +
                "import java.lang.reflect.*;\n" +
                "import com.google.gson.*;\n" +
                "import com.google.gson.reflect.TypeToken;\n\n" +
                userCode + "\n\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        Scanner sc = new Scanner(System.in);\n" +
                "        Gson gson = new GsonBuilder()\n" +
                "                .disableHtmlEscaping()\n" +
                "                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)\n" +
                "                .create();\n" +
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
                "            } else if (result != null && result.getClass().isArray()) {\n" +
                "                System.out.print(gson.toJson(result));\n" +
                "            } else if (result instanceof Map || result instanceof Collection) {\n" +
                "                System.out.print(gson.toJson(result));\n" +
                "            } else if (result instanceof JsonElement) {\n" +
                "                System.out.print(gson.toJson((JsonElement) result));\n" +
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
                "    private static float toFloat(JsonElement e) {\n" +
                "        if (e == null || e.isJsonNull()) return 0f;\n" +
                "        if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()) return e.getAsFloat();\n" +
                "        return Float.parseFloat(e.getAsString().trim());\n" +
                "    }\n\n" +
                "    private static char[] toCharArray(JsonElement e) {\n" +
                "        if (e == null || e.isJsonNull()) return new char[0];\n" +
                "        if (e.isJsonArray()) {\n" +
                "            StringBuilder sb = new StringBuilder();\n" +
                "            for (JsonElement x : e.getAsJsonArray()) sb.append(toChar(x));\n" +
                "            return sb.toString().toCharArray();\n" +
                "        }\n" +
                "        return e.getAsString().toCharArray();\n" +
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
                "    }\n\n" +
                "    private static char toChar(JsonElement e) {\n" +
                "        if (e == null || e.isJsonNull()) return '\\0';\n" +
                "        if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()) {\n" +
                "            String s = e.getAsString();\n" +
                "            return s.isEmpty() ? '\\0' : s.charAt(0);\n" +
                "        }\n" +
                "        return (char) toInt(e);\n" +
                "    }\n" +
                "}\n";
    }

    /**
     * Mỗi phần tử trong inputType (JSON array string) — ví dụ: {@code ["int[]","Map<String,Integer>"]}.
     */
    private void appendParamParse(StringBuilder parseCode, String type, int i, String var) {
        String g = "inputs.get(" + i + ")";
        switch (type) {
            case "int", "Integer" ->
                    parseCode.append("            int ").append(var).append(" = toInt(").append(g).append(");\n");
            case "long", "Long" ->
                    parseCode.append("            long ").append(var).append(" = toLong(").append(g).append(");\n");
            case "double", "Double" ->
                    parseCode.append("            double ").append(var).append(" = toDouble(").append(g).append(");\n");
            case "float", "Float" ->
                    parseCode.append("            float ").append(var).append(" = toFloat(").append(g).append(");\n");
            case "boolean", "Boolean" ->
                    parseCode.append("            boolean ").append(var).append(" = ").append(g).append(".getAsBoolean();\n");
            case "char", "Character" ->
                    parseCode.append("            char ").append(var).append(" = toChar(").append(g).append(");\n");
            case "String" ->
                    parseCode.append("            String ").append(var).append(" = ").append(g).append(".getAsString();\n");
            case "int[]" ->
                    parseCode.append("            int[] ").append(var).append(" = toIntArray(").append(g).append(");\n");
            case "int[][]" ->
                    parseCode.append("            int[][] ").append(var).append(" = toInt2DArray(").append(g).append(");\n");
            case "long[]" ->
                    parseCode.append("            long[] ").append(var).append(" = gson.fromJson(").append(g).append(", long[].class);\n");
            case "double[]" ->
                    parseCode.append("            double[] ").append(var).append(" = gson.fromJson(").append(g).append(", double[].class);\n");
            case "String[]" ->
                    parseCode.append("            String[] ").append(var).append(" = gson.fromJson(").append(g).append(", String[].class);\n");
            case "char[]" ->
                    parseCode.append("            char[] ").append(var).append(" = toCharArray(").append(g).append(");\n");
            case "JsonElement" ->
                    parseCode.append("            JsonElement ").append(var).append(" = ").append(g).append(";\n");
            case "JsonObject" ->
                    parseCode.append("            JsonObject ").append(var).append(" = ").append(g).append(".getAsJsonObject();\n");
            case "Map", "Map<String,Object>" -> {
                String t = "__mapT" + i;
                parseCode.append("            java.lang.reflect.Type ").append(t).append(" = TypeToken.getParameterized(Map.class, String.class, Object.class).getType();\n");
                parseCode.append("            @SuppressWarnings(\"unchecked\")\n");
                parseCode.append("            Map<String, Object> ").append(var).append(" = (Map<String, Object>) gson.fromJson(").append(g).append(", ").append(t).append(");\n");
            }
            case "Map<String,Integer>", "Map<String,Int>" ->
                    appendParameterizedMap(parseCode, var, g, i, "Integer.class", "Map<String, Integer>");
            case "Map<String,String>" ->
                    appendParameterizedMap(parseCode, var, g, i, "String.class", "Map<String, String>");
            case "Map<String,Long>" ->
                    appendParameterizedMap(parseCode, var, g, i, "Long.class", "Map<String, Long>");
            case "Map<String,Double>" ->
                    appendParameterizedMap(parseCode, var, g, i, "Double.class", "Map<String, Double>");
            case "Map<String,Boolean>" ->
                    appendParameterizedMap(parseCode, var, g, i, "Boolean.class", "Map<String, Boolean>");
            case "List<Integer>", "ArrayList<Integer>" ->
                    appendParameterizedList(parseCode, var, g, i, "Integer.class", "List<Integer>");
            case "List<Long>" ->
                    appendParameterizedList(parseCode, var, g, i, "Long.class", "List<Long>");
            case "List<String>" ->
                    appendParameterizedList(parseCode, var, g, i, "String.class", "List<String>");
            case "List<Double>" ->
                    appendParameterizedList(parseCode, var, g, i, "Double.class", "List<Double>");
            case "List<Boolean>" ->
                    appendParameterizedList(parseCode, var, g, i, "Boolean.class", "List<Boolean>");
            case "List<List<Integer>>" ->
                    appendListList(parseCode, var, g, i, "Integer.class", "List<List<Integer>>");
            case "List<List<String>>" ->
                    appendListList(parseCode, var, g, i, "String.class", "List<List<String>>");
            case "List<List<Long>>" ->
                    appendListList(parseCode, var, g, i, "Long.class", "List<List<Long>>");
            case "List<int[]>" -> {
                String t = "__li" + i;
                parseCode.append("            java.lang.reflect.Type ").append(t).append(" = TypeToken.getParameterized(List.class, int[].class).getType();\n");
                parseCode.append("            List<int[]> ").append(var).append(" = gson.fromJson(").append(g).append(", ").append(t).append(");\n");
            }
            default -> parseCode.append("            Object ").append(var).append(" = gson.fromJson(").append(g).append(", Object.class);\n");
        }
    }

    private void appendParameterizedMap(StringBuilder sb, String var, String g, int i,
                                        String valueClassDotClass, String javaDeclaredType) {
        String t = "__t" + i;
        sb.append("            java.lang.reflect.Type ").append(t).append(" = TypeToken.getParameterized(Map.class, String.class, ").append(valueClassDotClass).append(").getType();\n");
        sb.append("            ").append(javaDeclaredType).append(" ").append(var).append(" = gson.fromJson(").append(g).append(", ").append(t).append(");\n");
    }

    private void appendParameterizedList(StringBuilder sb, String var, String g, int i,
                                           String elemClassDotClass, String javaDeclaredType) {
        String t = "__t" + i;
        sb.append("            java.lang.reflect.Type ").append(t).append(" = TypeToken.getParameterized(List.class, ").append(elemClassDotClass).append(").getType();\n");
        sb.append("            ").append(javaDeclaredType).append(" ").append(var).append(" = gson.fromJson(").append(g).append(", ").append(t).append(");\n");
    }

    private void appendListList(StringBuilder sb, String var, String g, int i,
                                String innerElemClassDotClass, String javaDeclaredType) {
        String inner = "__inner" + i;
        String outer = "__outer" + i;
        sb.append("            java.lang.reflect.Type ").append(inner).append(" = TypeToken.getParameterized(List.class, ").append(innerElemClassDotClass).append(").getType();\n");
        sb.append("            java.lang.reflect.Type ").append(outer).append(" = TypeToken.getParameterized(List.class, ").append(inner).append(").getType();\n");
        sb.append("            ").append(javaDeclaredType).append(" ").append(var).append(" = gson.fromJson(").append(g).append(", ").append(outer).append(");\n");
    }
}


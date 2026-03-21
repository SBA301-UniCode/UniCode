//package com.example.unicode.configuration;
//
//import org.springframework.context.annotation.Configuration;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//
//@Configuration
//public class PythonConfig implements LanguageConfig {
//    public String getDockerImage() { return "python:3.9-slim"; }
//    public String getFileName() { return "solution.py"; }
//    public String getCompileCmd() { return "true"; } // Python không cần biên dịch
//
//    public String getRunCmd(String inputData) {
//        String rawInput = inputData == null ? "[]" : inputData;
//        String encoded = Base64.getEncoder().encodeToString(rawInput.getBytes(StandardCharsets.UTF_8));
//        return "python3 solution.py '" + encoded + "'";
//    }
//
//    public String wrapCode(String learnerCode) {
//        if (learnerCode == null) learnerCode = "";
//        return "import sys\n" +
//                "import json\n" +
//                "import base64\n" +
//                learnerCode + "\n" +
//                "if __name__ == \"__main__\":\n" +
//                "    try:\n" +
//                "        encoded = sys.argv[1] if len(sys.argv) > 1 else ''\n" +
//                "        raw = base64.b64decode(encoded).decode('utf-8') if encoded else '[]'\n" +
//                "        args = json.loads(raw) if raw else []\n" +
//                "        if not isinstance(args, list):\n" +
//                "            args = [args]\n" +
//                "        sol = Solution()\n" +
//                "        target = getattr(sol, 'solve', None)\n" +
//                "        if target is None:\n" +
//                "            candidates = [name for name in dir(sol) if callable(getattr(sol, name)) and not name.startswith('_')]\n" +
//                "            if not candidates:\n" +
//                "                raise Exception('Solution must provide solve(...) or another public method')\n" +
//                "            target = getattr(sol, candidates[0])\n" +
//                "        result = target(*args)\n" +
//                "        if isinstance(result, (dict, list, tuple, bool, int, float)) or result is None:\n" +
//                "            print(json.dumps(result, separators=(',', ':')), end='')\n" +
//                "        else:\n" +
//                "            print(str(result), end='')\n" +
//                "    except Exception as e: print(e, file=sys.stderr)";
//    }
//}

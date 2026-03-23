package com.example.unicode.configuration;

import java.util.List;

public interface LanguageConfig {

    String getDockerImage();

    String getFileName();

    String getCompileCmd();

    String getRunCmd();

    String wrapCode(String userCode, List<String> paramTypes, String functionName);
}
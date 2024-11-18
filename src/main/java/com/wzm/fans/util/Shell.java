package com.wzm.fans.util;

import expect4j.Expect4j;
import expect4j.IOPair;
import expect4j.matches.Match;
import expect4j.matches.RegExpMatch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.oro.text.regex.MalformedPatternException;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Shell {

    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
    private static final boolean IN_CHINA = Objects.equals(Locale.getDefault().getCountry(), "CN");
    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String chineseWindowDefaultCharset = "GBK";
    private static final List<String> winCommandPrefix = List.of("cmd.exe", "/c");
    private static final List<String> linuxCommandPrefix = List.of("/bin/sh", "-c");
    private static final String INPUT_PREFIX = "<<<";
    private static final String NEW_LINES = "\n";

    static {
        //禁止expect4j输出日志，仅在springboot环境中，且不启动springboot时使用
        LoggingSystem loggingSystem = LoggingSystem.get(Shell.class.getClassLoader());
        loggingSystem.setLogLevel("expect4j", LogLevel.OFF);
    }


    public static void exec(Consumer<String> outputConsumer, String command) {
        exec(outputConsumer, Collections.emptyList(), USER_DIR, command);
    }

    public static void exec(Consumer<String> outputConsumer, String directory, String command) {
        exec(outputConsumer, Collections.emptyList(), directory, command);
    }

    public static void exec(Consumer<String> outputConsumer, List<MatchEntry> matches, String command) {
        exec(outputConsumer, matches, USER_DIR, command);
    }

    public static void exec(Consumer<String> outputConsumer, List<MatchEntry> matches, String directory, String command) {
        Assert.notNull(matches, "matches不能为空");
        List<String> strings = perProcess(command);
        ProcessBuilder pb = new ProcessBuilder(strings);
        pb.directory(new File(directory));
        pb.redirectErrorStream(true);
        try {
            Process start = pb.start();
            try (OutputStream outputStream = start.getOutputStream();
                 InputStream inputStream = start.getInputStream()) {
                dealInteractive(inputStream, outputStream, matches, outputConsumer);
            }
        } catch (IOException e) {
            throw new RuntimeException("执行shell脚本失败", e);
        }
    }


    public static String execStr(List<MatchEntry> matches, String directory, String command) {
        StringBuilder sb = new StringBuilder();
        exec(s -> sb.append(s).append(NEW_LINES), matches, directory, command);
        return sb.toString();
    }

    public static String execStr(String directory, String command) {
        return execStr(Collections.emptyList(), directory, command);
    }

    public static String execStr(List<MatchEntry> matches, String command) {
        return execStr(matches, USER_DIR, command);
    }

    public static String execStr(String command) {
        return execStr(USER_DIR, command);
    }


    private static IOPair getIoPair(InputStream inputStream, OutputStream outputStream) {
        try {
            InputStreamReader reader = new InputStreamReader(inputStream, charset());
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, charset());
            return new MyIOPair(reader, writer);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }


    }

    //处理交互命令
    private static void dealInteractive(InputStream inputStream, OutputStream outputStream, List<MatchEntry> matches, Consumer<String> outputConsumer) throws UnsupportedEncodingException {
        IOPair ioPair = getIoPair(inputStream, outputStream);
        Expect4j expect = new Expect4j(ioPair);
        expect.setDefaultTimeout(5 * 60 * 1000); //设置超时时间

        expect.registerBufferChangeLogger((chars, len) -> {
                    String s = new String(chars, 0, len).trim();
                    if (StringUtils.hasText(s))
                        outputConsumer.accept(s);
                }
        );

        List<Match> collect = matches.stream().map(entry -> {
            try {
                String pattern = entry.getPattern();
                String inputStr = entry.getInput();
                return new RegExpMatch(pattern, state -> {
                    expect.send(inputStr + NEW_LINES);
                    if (StringUtils.hasText(inputStr))
                        outputConsumer.accept(INPUT_PREFIX + inputStr.trim());
                    state.exp_continue();
                });
            } catch (MalformedPatternException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        try {
            expect.expect(collect);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> perProcess(String command) {
        List<String> comm = List.of(command);
        if (IS_WINDOWS)
            return Stream.concat(winCommandPrefix.stream(), comm.stream()).toList();
        return Stream.concat(linuxCommandPrefix.stream(), comm.stream()).toList();
    }

    private static String charset() {
        return IS_WINDOWS && IN_CHINA ? chineseWindowDefaultCharset : Charset.defaultCharset().toString();
    }

    @Getter
    @AllArgsConstructor
    public static class MatchEntry {
        private String pattern; //匹配的模式
        private String input; //如果匹配到pattern，将与shell交互，输入input
    }

    public static class MatchListBuilder {
        private final ArrayList<MatchEntry> arrayList = new ArrayList<>();


        public List<MatchEntry> build() {
            return arrayList;
        }

        public MatchListBuilder add(String pattern, String input) {
            arrayList.add(new MatchEntry(pattern, input));
            return this;
        }


    }

    public static MatchListBuilder matchesBuilder() {
        return new MatchListBuilder();
    }


    private static class MyIOPair implements IOPair {
        final Reader is;
        final Writer os;

        public MyIOPair(Reader is, Writer os) {
            this.is = is;
            this.os = os;
        }

        public Reader getReader() {
            return is;
        }

        public Writer getWriter() {
            return os;
        }


        public void reset() {
            try {
                is.reset();
            } catch (IOException ignored) {
            }
        }

        public void close() {
            try {
                is.close();
            } catch (Exception ignored) {
            }
            try {
                os.close();
            } catch (Exception ignored) {
            }
        }
    }

}
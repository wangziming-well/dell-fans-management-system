package com.wzm.fans.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class IpmiTool {

    private static final Log logger = LogFactory.getLog(IpmiTool.class);

    private static final String workingDictionary;

    static {
        checkRemoteInfo();
        logger.info("初始化IPMI，检查impitool环境");
        if (Shell.IS_WINDOWS) {
            logger.info("当前操作系统为windows，自动加载ipmitool");
            workingDictionary = System.getProperty("user.dir") + File.separator + "ipmitool-win";
            unzipWinIpmitool();
        } else {
            logger.info("当前操作系统为linux");
            workingDictionary = System.getProperty("user.dir");
        }

        if (isIpmitoolAvailable()) {
            logger.info("初始化IPMI成功");
            setFansManualMode();
            logger.info("将风扇控制调整到手动模式");
        } else {
            logger.warn("ipmitool不可用，请先安装");
        }
    }

    private static void checkRemoteInfo(){
        Assert.hasText(ConfigUtils.get("idrac.host"),"ipmi访问域名不能为空");
        Assert.hasText(ConfigUtils.get("idrac.username"),"ipmi访问用户名不能为空");
        Assert.hasText(ConfigUtils.get("idrac.password"),"ipmi访问密码不能为空");
    }

    private static void unzipWinIpmitool() {
        if (hasIpmitoolExe()) {
            logger.info(workingDictionary + " 文件夹中已经存在ipmitool.exe,无需重复复制");
            return;
        }
        logger.info("解压缩impitool-win.zip到" + workingDictionary);
        try {
            ClassPathResource resource = new ClassPathResource("ipmitool-win.zip");
            InputStream inputStream = resource.getInputStream();
            FileUtils.unzip(inputStream, workingDictionary);
            logger.info("解压缩impitool-win.zip到" + workingDictionary + "完成");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean hasIpmitoolExe() {
        File workingDic = new File(workingDictionary);
        if (!workingDic.exists())
            return false;
        File[] files = workingDic.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.getName().contains("ipmitool.exe"))
                return true;
        }
        return false;
    }
    //将服务器风扇调整到手动模式，只有在手动模式下才能调用setFansPWM()
    public static void setFansManualMode(){
        String command = "raw 0x30 0x30 0x01 0x00";
        exec(command);
    }
    //将服务器风扇调整到自动模式
    public static void setFansAutoMode(){
        String command = "raw 0x30 0x30 0x01 0x01";
        exec(command);
    }

    /**
     * 调整服务器PWM风扇转速
     * @param percentage 风扇转速的百分比，传10则调整风扇转速到10%
     */

    public static void setFansPWM(int percentage){
        String command =String.format("raw 0x30 0x30 0x02 0xff 0x%s",Integer.toHexString(percentage)) ;
        exec(command);
    }

    public static boolean isIpmitoolAvailable() {
        try {
            String s = Shell.execStr(workingDictionary, "ipmitool -V");
            return s.startsWith("ipmitool version");
        } catch (Exception e) {
            logger.warn("执行ipmitool -V命令出错", e);
            return false;
        }

    }

    /**
     * 用于检测远程访问是否可用
     */
    public static boolean checkRemoteAccess(){
        String command = "bmc info";
        String result = exec(command);
        return result.contains("IPMI");
    }

    /**
     * 用于执行impitool远程命令
     * @param command 执行的命令
     * @return 命令的结果
     */

    private static String exec(String command) {
        checkRemoteInfo();
        command = String.format("ipmitool -I lanplus -H %s -U %s -P %s %s",
                ConfigUtils.get("idrac.host"), ConfigUtils.get("idrac.username"), ConfigUtils.get("idrac.password"), command);
        return Shell.execStr(workingDictionary, command);
    }

}

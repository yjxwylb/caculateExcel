package com.caculate.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: xiayuejie
 * @date: 2019/8/5 13:13
 * @description:
 */
@Controller
public class CaculateController {

    @GetMapping("/cal")
    public String multiUpload() {
        return "multiUpload";
    }

    @PostMapping("/cal")
    @ResponseBody
    public String multiUpload(@RequestParam("file") MultipartFile[] files, @RequestParam("step") Integer step, @RequestParam("dirName") String dirName) {

        if (files.length == 0) {
            return "文件不能为空";
        }
        if (StringUtils.isBlank(dirName)) {
            dirName = String.valueOf(RandomUtils.nextInt());
        }
        if (null == step || step <= 0) {
            return "输入行数不能小于0";
        }
        File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
        String writePath = desktopDir.getAbsolutePath();
        String path = writePath + "\\" + dirName + "_result\\";
        createDir(path);
        List<MultipartFile> fileList = Arrays.asList(files);
        fileList.forEach(e -> {
            String fileName = e.getOriginalFilename();
            String filePath = path + fileName.substring(0, fileName.lastIndexOf(".")) + "_result" + fileName.substring(fileName.lastIndexOf("."), fileName.length());
            caculate(e, step, filePath);
        });
        try {
            Runtime.getRuntime().exec("cmd /c start explorer " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "计算成功！！！";
    }

    private void caculate(MultipartFile file, Integer step, String filePath) {
        BigDecimal num = BigDecimal.valueOf(step);
        try {
            InputStream inputStream = file.getInputStream();
            List<List<String>> lists = EasyExcelUtil.readExcelWithStringList(inputStream, ExcelTypeEnum.XLSX);
            List<String> list0 = lists.get(0);
            List<String> list1 = lists.get(1);
            lists.remove(0);
            lists.remove(0);
            List<List<String>> convertList = convert(lists);
            List<List<String>> resultList = new ArrayList<>();

            for (List<String> list : convertList) {
                int size = list.size();
                List<String> decimals = new ArrayList<>();
                BigDecimal value = BigDecimal.ZERO;
                for (int i = 0; i < size; i++) {
                    if (i == 0) {
                        decimals.add(list.get(0));
                    } else {
                        value = value.add(new BigDecimal(list.get(i)));
                    }
                    if (i > 0 && i % num.longValue() == 0) {
                        BigDecimal avg = value.divide(num, 5, RoundingMode.HALF_EVEN);
                        decimals.add(avg.toString());
                        value = BigDecimal.ZERO;
                    }
                    if (size - 1 - i < num.longValue() && i == size - 2) {
                        BigDecimal avg = value.divide(num, 5, RoundingMode.HALF_EVEN);
                        decimals.add(avg.toString());
                    }
                    if (i == size - 1) {
                        decimals.add(list.get(size - 1));
                    }
                }
                resultList.add(decimals);
            }
            List<List<String>> convertResult = convert(resultList);
            convertResult.add(0, list0);
            convertResult.add(1, list1);
            writeNoModel(filePath, convertResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
            return;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            System.out.println("创建目录" + destDirName + "成功！");
        } else {
            System.out.println("创建目录" + destDirName + "失败！");
        }
    }

    private List<List<String>> convert(List<List<String>> list) {
        List<List<String>> newlist = new ArrayList<>();
        List list2List;
        int index = list.get(0).size();
        for (int i = 0; i < index; i++) {
            list2List = new ArrayList<List>();
            for (List list2 : list) {
                list2List.add(list2.get(i));
            }
            newlist.add(list2List);
        }
        return newlist;
    }

    private void writeNoModel(String writePath, List<List<String>> list) throws FileNotFoundException {
        long beginTime = System.currentTimeMillis();
        OutputStream out = new FileOutputStream(writePath);
        EasyExcelUtil.writeExcelWithStringListAndNoHead(out, list, ExcelTypeEnum.XLSX);
        long endTime = System.currentTimeMillis();
        System.out.println(String.format("总共耗时 %s 毫秒", (endTime - beginTime)));

    }

}

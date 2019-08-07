<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <title>单文件上传</title>
</head>
<body align="center">
<h2>Excel数值计算</h2>
<form action="/cal" method="post"  enctype="multipart/form-data">
    <table align="center">
        <tr align="right">
            <td><input type="file" name="file" multiple><br></td>
        </tr>
        <tr align="right">
            <td>请输入行数: </td>
            <td><input type="number" name="step"><br></td>
        </tr>
        <tr align="right">
            <td>请输入文件夹名(保存在桌面): </td>
                <td><input type="text" name="dirName"><br></td>
        </tr>
    </table>
    <input type="submit"  value="提交" >
    <input type="reset" name=refill value="重填" >
</form>
</body>
</html>

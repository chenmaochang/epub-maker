<?xml version="1.0" encoding="utf-8"?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
        "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>${title!''}</title>
    <style rel="stylesheet" type="text/css">
        div.div-tu {
            width: 100%;
            text-align: center;
            margin-bottom: 1.5em;
        }
    </style>
</head>
<body xmlns:xml="http://www.w3.org/XML/1998/namespace" xml:lang="zh-CN">
<#list images as image>
    <div class="div-tu">
        <img src="../Images/${image.fullName}" width="100%"/>
    </div>
</#list>
</body>
</html>
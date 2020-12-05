<?xml version='1.0' encoding='utf-8'?>
<ncx xmlns="http://www.daisy.org/z3986/2005/ncx/" version="2005-1" xml:lang="zh">
<head>
<meta content="${eBook.identifier}" name="dtb:uid"/>
<meta content="1" name="dtb:depth"/>
<meta content="0" name="dtb:totalPageCount"/>
<meta content="0" name="dtb:maxPageNumber"/>
</head>
<docTitle>
<text>${eBook.title}</text>
</docTitle>
<navMap>
    <#list chapters as data>
    <navPoint id="np_${data_index+1}" playOrder="${data_index+1}">
        <navLabel>
            <text>${data.title}</text>
        </navLabel>
        <content src="Text/${data.fullName}"/>
    </navPoint>
    </#list>
  </navMap>
</ncx>

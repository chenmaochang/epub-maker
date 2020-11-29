<?xml version='1.0' encoding='utf-8'?>
<package xmlns="http://www.idpf.org/2007/opf" version="2.0" unique-identifier="uid">
<metadata xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:opf="http://www.idpf.org/2007/opf">
    <dc:title>${ebook.title}</dc:title>
    <dc:language>${ebook.language!'zh'}</dc:language>
    <dc:identifier id="uid">${ebook.identifier}</dc:identifier>
    <dc:creator>${ebook.creator}</dc:creator>
    <dc:publisher>${ebook.publisher!'暂无出版社'}</</dc:publisher>
    <dc:date opf:event="publication">${ebook.date}</</dc:date>
    <meta name="cover" content="${cover.fullName}"/>
    <meta name="output encoding" content="utf-8"/>
    <meta name="primary-writing-mode" content="horizontal-lr"/>
  </metadata>
<manifest>
<item id="bookCover" media-type="application/xhtml+xml" href="Text/cover_page.xhtml"/>
<#list htmlPages as page>
<item id="${page}" media-type="application/xhtml+xml" href="Text/${page}"/>
</#list>
<#list images as image>
<item id="${image.fullName}" media-type="application/xhtml+xml" href="Text/${image.suffix}"/>
</#list>
<item id="${cover.fullName}" media-type="image/jpeg" href="Images/${cover.fullName}"/>
<item id="ncx" media-type="application/x-dtbncx+xml" href="toc.ncx"/>
</manifest>
<spine toc="ncx">
<itemref idref="bookCover" linear="yes"/>
<#list htmlPages as page>
<itemref idref="${page}"/>
</#list>
</spine>
<tours>
</tours>
<guide>
<reference type="toc" title="Table of Contents" href="Text/part0003.xhtml"/>
<reference href="Text/cover_page.xhtml" type="cover"/>
</guide>
</package>
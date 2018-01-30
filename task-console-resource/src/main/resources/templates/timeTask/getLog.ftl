<#ftl encoding="utf-8">

<HTML>
<#include "constants.ftl">
<head>
    <title>日志</title>
    <link rel="shortcut icon" href="${ctx}/res/img/snoopy2.png" type="image/png">
</head>
 
<body>
 <script src="${ctx}/res/timeTask/js/Common.js"></script>
 <table border="0" width="100%" height="85%">
			<tr><td>日志：${taskName }</td></tr>
			<tr>
				<td height="30px"> 
					每页${pageSize} K， 共${totalSize}K &nbsp; 
					<font color="red">${pn}</font>/${pageCount}页&nbsp;&nbsp;  

					<a
						href="getLog.htm?id=${RequestParameters.id}&pn=1">首页</a>
					<a
						href="getLog.htm?id=${RequestParameters.id}&pn=${pn-1}">上页</a>
					<a
						href="getLog.htm?id=${RequestParameters.id}&pn=${pn + 1}">下页</a>
					<a
						href="getLog.htm?id=${RequestParameters.id}&pn=-2">尾页</a>
					&nbsp;&nbsp;
					<input type="text" id="jump" size="1" style="width:40px"  ><input type="button" value="Go" onClick="doJump()">
					&nbsp;&nbsp;&nbsp;&nbsp;
					
					页大小（K）：<input type="text" id="pageSize"  style="width:40px"  ><input type="button" value="设置" onClick="doSetPageSize()">


				</td>
			</tr>

	<tr>
		<td  height="90%"><textarea id="talog" style="width: 99%; height: 98%">${log}</textarea></td>
	</tr>
	
	<script type="text/javascript">
		 
		 function doJump()
		 {
		 	location.href="getLog.htm?id=${RequestParameters.id}&pn="+document.getElementById("jump").value;
		 }
		 
		 function doSetPageSize()
		 {
			 Common.setCookie("pageSize", document.getElementById("pageSize").value);
		 }

         document.getElementById("talog").style.height=( document.body.scrollHeight-80)+"px";
		
		</script>
</table>
</body>
</HTML>

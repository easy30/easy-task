<#ftl encoding="utf-8">
 <font color="red">${pn}</font>/${pageCount}页
		 &nbsp; &nbsp; 每页${ps}条， 共${totalCount}条
		  &nbsp;&nbsp;
		<a href="${basePageUrl}&pn=1" class="btn btn-info btn-xs">首页</a>
		<a href="${basePageUrl}&pn=${pn-1}" class="btn btn-info btn-xs">上页</a>
		<a href="${basePageUrl}&pn=${pn + 1}" class="btn btn-info btn-xs">下页</a>
		<a href="${basePageUrl}&pn=-2"  class="btn btn-info btn-xs">尾页</a>
			&nbsp;&nbsp; 
		<input type="text" id="jump" size="1" style="width: 40px">
		<input type="button" value="Go" onClick="doJump(this)"> 
		<script type="text/javascript">
		function doJump(sender) {
			var p=$("#jump", $(sender).parent()).val();
			location = "${basePageUrl}&pn="+p;
		}
		</script> 
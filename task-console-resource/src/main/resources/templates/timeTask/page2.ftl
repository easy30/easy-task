<#ftl encoding="utf-8">
 <font color="red">${pn}</font>/${pageCount} <span lang-key="page">page</span>
&nbsp; &nbsp; <span lang-key="total"></span> ${totalCount}
		  &nbsp;&nbsp;
		<a href="${basePageUrl}&pn=1" class="btn btn-info btn-xs" lang-key="firstPage"></a>
		<a href="${basePageUrl}&pn=${pn-1}" class="btn btn-info btn-xs" lang-key="prevPage"></a>
		<a href="${basePageUrl}&pn=${pn + 1}" class="btn btn-info btn-xs" lang-key="nextPage"></a>
		<a href="${basePageUrl}&pn=-2"  class="btn btn-info btn-xs" lang-key="lastPage"></a>
			&nbsp;&nbsp; 
		<input type="text" id="jump" size="1" style="width: 40px">
		<input type="button" value="Go" onClick="doJump(this)"> 
		<script type="text/javascript">
		function doJump(sender) {
			var p=$("#jump", $(sender).parent()).val();
			location = "${basePageUrl}&pn="+p;
		}
		</script> 
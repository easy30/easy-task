<#ftl encoding="utf-8">
<#include "constants.ftl">
${timeTask.name}
<div>
当前页：${pn}
<a href="getTaskResults.htm?id=${id}&pn=${pn-1}&ps=${ps}">上页</a>
<a href="getTaskResults.htm?id=${id}&pn=${pn+1}&ps=${ps}">下页</a>
</div>
<#list taskResponses as response>
<table class="table">
    <tr>
        <td>
         ${response.id?c} - ${(response.success)?string("成功","<font color='red'>失败</font>")}
        </td>
    </tr>
    <tr>
        <td>${response.summary!}</td>
    </tr>
    <tr>
        <td>${response.message!}</td>
    </tr>
</table>
</#list>


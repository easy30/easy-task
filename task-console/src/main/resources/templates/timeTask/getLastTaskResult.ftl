<#ftl encoding="utf-8">
<#include "constants.ftl">
<table class="table"><tr><td>${timeTask.name} - ${taskResult}
    <a style="margin-left: 30px" href="getTaskResults.htm?id=${timeTask.id}" target="_blank">查看历史监控记录</a>
</td></tr>
    <tr><td>${summary!}</td></tr>
    <tr><td>${message!}</td></tr>
</table>


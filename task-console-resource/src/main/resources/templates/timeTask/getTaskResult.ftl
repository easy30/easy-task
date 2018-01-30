<#ftl encoding="utf-8">
<#include "constants.ftl">
<table class="table"><tr><td><b>${timeTask.name}</b></td></tr></table>
<table class="table"><tr><td>最新监控结果：<a href="getLastTaskResult.htm?id=${timeTaskId}">${lastTaskResult}</a></td></tr></table>
<table class="table"><tr><td>当前查看的监控结果 - ${taskResult}
    <a style="margin-left: 30px" href="getTaskResults.htm?id=${timeTaskId}">历史监控记录</a>
    <a style="margin-left: 30px" href="${ctx}/timeTask/list.htm">监控任务列表</a>
</td></tr>
    <tr><td>${summary!}</td></tr>
    <tr><td>${message!}</td></tr>
</table>


<#ftl encoding="utf-8">
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="${ctx}/res/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<#if customerJs?? && (customerJs?size >0)>
    <#list customerJs as jsFiles>
    <script src="${ctx}/res/js/app/${jsFiles}?${updateDate}"></script>
    </#list>
</#if>

<script src="${ctx}/res/js/lib/WdatePicker.js" type="text/javascript" ></script>


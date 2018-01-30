<#ftl encoding="utf-8">
<#include "constants.ftl">
<#macro page title="" menu="" customerCss=[] customerJs=[] specificLib=[]>
<!DOCTYPE html>
<html lang="zh-CN" ng-app="rootApp">
    <#include "head-meta.ftl">
<body>
    <#include "head-master.ftl">
    <#nested>
    <#include "foot.ftl">
    <#include "foot-meta.ftl">
</body>
</html>
</#macro>
<#ftl encoding="utf-8">
<#include "constants.ftl">
<form id="searchForm" name="searchForm" class="form-inline" method="get" action="list.htm">
    <input type="hidden" name="taskType" value="${taskType}">

    <input type="hidden" name="cat4" value="${cat4}">
    <table class="table table-condensed" style="margin-bottom: 0px;">
        <#--<#if  admin==0>
            <tr>
                <td style="width:80px">${areaCustom.categoryNames[0]}：</td>
                <td style="padding: 2px">
                    <input type="hidden" name="cat1" value="${cat1}">
                    <input type="hidden" name="cat2" value="${cat2}">
                    <input type="hidden" name="cat3" value="${cat3}">
                    <c:forEach items="${propsMap.cat1}" var="item">
                        <a href="#" style="margin-bottom:2px"
                           class="btn ${(item==cat1)?string('btn-danger','btn-warning')} btn-xs"
                           onclick="searchForm.cat1.value=this.innerText;searchForm.submit();">${item}</a>
                    </c:forEach>
                    &nbsp;&nbsp;&nbsp;<a href="#" class="btn btn-success btn-xs"
                                         onclick="searchForm.cat1.value='';searchForm.submit();">取消</a>
                </td>

            </tr>
            <tr>
                <td>${areaCustom.categoryNames[1]}：</td>
                <td style="padding: 2px">
                    <c:forEach items="${propsMap.cat2}" var="item" varStatus="status">
                        <#if  status.index<10>
                            <a href="#" style="margin-bottom:2px"
                               class="btn ${item==cat2?string('btn-danger','btn-warning')} btn-xs"
                               onclick="searchForm.cat2.value=this.innerText;searchForm.submit();">${item}</a>
                        </#if>
                    </c:forEach>
                    &nbsp;&nbsp;&nbsp;<a href="#" class="btn btn-success btn-xs"
                                         onclick="searchForm.cat2.value='';searchForm.submit();">取消</a>
                </td>

            </tr>
            <#if    propsMap.cat3??>
                <tr>
                    <td>${areaCustom.categoryNames[2]}：</td>
                    <td style="padding: 2px">
                        <c:forEach items="${propsMap.cat3}" var="item">
                            <a href="#" style="margin-bottom:2px"
                               class="btn ${item==cat3?string('btn-danger','btn-warning')} btn-xs"
                               onclick="searchForm.cat3.value=this.innerText;searchForm.submit();">${item}</a>
                        </c:forEach>
                        &nbsp;&nbsp;&nbsp;<a href="#" class="btn btn-success btn-xs"
                                             onclick="searchForm.cat3.value='';searchForm.submit();">取消</a>
                    </td>
                </tr>
            </#if>

            <#if  propsMap.cat4??>
                <#if  param.taskType!=1 && param.taskType!=2>
                    <tr>
                        <td>${areaCustom.categoryNames[3]}：</td>
                        <td style="padding: 2px">
                            <c:forEach items="${propsMap.cat4}" var="item">
                                <a href="#" style="margin-bottom:2px"
                                   class="btn ${item==cat4?string('btn-danger','btn-warning')} btn-xs"
                                   onclick="searchForm.cat4.value=this.innerText;searchForm.submit();">${item}</a>
                            </c:forEach>
                            &nbsp;&nbsp;&nbsp;<a href="#" class="btn btn-success btn-xs"
                                                 onclick="searchForm.cat4.value='';searchForm.submit();">取消</a>
                        </td>
                    </tr>
                </#if>
            </#if>
        </#if>-->

            <tr>
              <#--  <td>
                    <div style="margin-top:8px">查询项：</div>
                </td>-->
                <td>
                    <span style="margin-left:5px">分&nbsp;&nbsp; 组</span>
                    <input type="text" class="form-control input-xs" style="height:25px;width: 100px" name="cat1" value="${cat1}"/>

                    <span style="margin-left:5px">任务ID</span>
                    <input type="text" class="form-control input-xs" style="height:25px;width:80px" name="timeTaskId"
                           value="${timeTaskId!}"/>

                    <span style="margin-left:5px">应用</span>
                    <select id="appName" name="appName" class="form-control input-xs" style="height:25px"
                    onchange="onSearchAppChange(this)">
                        <option></option>
                        <#list  appNames as item>
                            <option value="${item}">${item}</option>
                        </#list>
                    </select>

                    <span style="margin-left:5px">服务器</span>
                    <select id="searchTargetIp" name="targetIp" class="form-control input-xs" style="height:25px">
                        <option></option>
                        <#list  machines as item>
                            <option value="${item}">${item}</option>
                        </#list>
                    </select>

                </td>
            </tr>


        <tr>

            <td>
                <span style="margin-left:5px">关键词</span>
                <input type="text" class="form-control input-xs" style="height:25px;width: 100px" name="words" value="${words}"/>

                <span style="margin-left:5px">创建者</span>
                <input type="text" class="form-control input-xs" style="height:25px;width:80px" name="createUser"
                       value="${createUser!}"/>

                <span style="margin-left:5px">状态</span>
                <select id="status" name="status" class="form-control input-xs" style="height:25px">
                    <option value="-1"></option>
                    <option value="1">运行</option>
                    <option value="0">停止</option>
                    <option value="2">删除</option>
                </select>


                <input style="margin-left:5px;width:60px" type="submit" value="查询" class="btn btn-primary btn-xs"
                       style="height:25px">
                <a href="list.htm?taskType=${RequestParameters.taskType!"0"}" style="margin-left:25px" class="btn btn-default btn-xs"
                   style="height:25px">重置</a>


            </td>
        </tr>
    </table>
</form>
<script>
    $(document).ready(function () {
        $("#status").val("${status}");
        $("#searchTargetIp").val("${targetIp}");
        $("#appName").val("${appName}");
        //$("#searchForm").ajaxForm({
        //	target : "#list"
        //});

    });

    function onSearchAppChange(sender) {
        var v=sender.value;
        Common.ajaxPost("getIpList.htm",{"appName":v},function (success,text) {
            var list=JSON.parse(text);
            var sel=$("#searchTargetIp");
            sel.empty();
            list.splice(0, 0, "");
            for(var i=0;i<list.length;i++) {
                var option = $("<option>").val(list[i]).text(list[i]);

                sel.append(option);

            }
            $("#searchTargetIp").html(h);
        });
    }
</script>

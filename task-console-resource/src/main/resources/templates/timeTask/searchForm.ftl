<#ftl encoding="utf-8">
<#include "../include/constants.ftl">
<form id="searchForm" name="searchForm" class="form-inline" method="get" action="">
    <input type="hidden" name="taskType" value="${taskType}">

    <input type="hidden" name="cat4" value="${cat4}">
    <#--<table class="table table-condensed" style="margin-bottom: 0px;">-->

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


              <#--  <td>
                    <div style="margin-top:8px">查询项：</div>
                </td>-->

                    <span id="fGroup" style="display: inline-block">
                    <span style="margin-left:5px" lang-key="group">group</span>
                    <input type="text" class="form-control input-xs" style="height:25px;width: 100px" name="cat1" value="${cat1}"/>
                    </span>

                    <span id="fTimeTaskId" style="display: inline-block">
                    <span style="margin-left:5px"  lang-key="taskId">taskId</span>
                    <input type="text" class="form-control input-xs" style="height:25px;width:80px" name="timeTaskId"
                           value="${timeTaskId!}"/>
                     </span>

                    <span id="fApplication" style="display: inline-block">
                    <span style="margin-left:5px"  lang-key="application">application</span>
                    <select id="appName" name="appName" class="form-control input-xs" style="height:25px"
                    onchange="onSearchAppChange(this)">
                        <option></option>
                        <#list  appNames as item>
                            <option value="${item}">${item}</option>
                        </#list>
                    </select>
                    </span>

                    <span id="fServer" style="display: inline-block">
                    <span style="margin-left:5px"  lang-key="server">server</span>
                    <select id="searchTargetIp" name="targetIp" class="form-control input-xs" style="height:25px">
                        <option></option>
                        <#list  machines as item>
                            <option value="${item}">${item}</option>
                        </#list>
                    </select>
                     </span>


                <span id="fKeyword" style="display: inline-block">
                <span style="margin-left:5px" lang-key="keyword">keyword</span>
                <input type="text" class="form-control input-xs" style="height:25px;width: 100px" name="words" value="${words}"/>
                </span>

                <span id="fCreator" style="display: inline-block">
                <span style="margin-left:5px" lang-key="creator">creator</span>
                <input type="text" class="form-control input-xs" style="height:25px;width:80px" name="createUser"
                       value="${createUser!}"/>
                 </span>

                <span id="fStatus" style="display: inline-block">
                <span style="margin-left:5px" lang-key="status">status</span>
                <select id="status" name="status" class="form-control input-xs" style="height:25px">
                    <option value="-1" ></option>
                    <option value="1"  lang-key="run">run</option>
                    <option value="0" lang-key="stop">stop</option>
                    <option value="2" lang-key="delete">delete</option>
                </select>
                </span>

                <span id="fStatus" style="display: inline-block">
                <button style="margin-left:5px;width:60px" type="submit"  class="btn btn-primary btn-xs"
                        style="height:25px" lang-key="search">search</button>
                <a href="list.htm?taskType=${RequestParameters.taskType!"0"}" style="margin-left:25px" class="btn btn-default btn-xs"
                   style="height:25px" lang-key="reset">reset</a>
                 </span>

    
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

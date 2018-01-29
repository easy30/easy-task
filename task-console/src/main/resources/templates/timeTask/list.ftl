<#ftl encoding="utf-8">
<#include "../include/constants.ftl">
<#include "constants.ftl">
<#import "../include/master.ftl" as frame>
<@frame.page title="${areaCustom.desc}" menu="timeTask_${taskType}" customerCss=[] customerJs=[] specificLib=[]>
<script src="${ctx}/res/timeTask/js/ajaxfileupload.js"></script>
<script type="text/javascript">
    function ajaxFileUpload() {
        $.ajaxFileUpload({
            url: 'importTasks.htm',
            secureuri: false,
            fileElementId: 'file1',
            dataType: 'html',
            data: {},
            success: function (data, status) {
                refreshList();
            },
            error: function (data, status, e) {
                alert('上传出错');
            }
        })

        return false;

    }
</script>
<style>
    .fileinput-button {
        position: relative;
        display: inline-block;
        overflow: hidden;
    }

    .fileinput-button input {
        position: absolute;
        right: 0px;
        top: 0px;
        opacity: 0;
        -ms-filter: 'alpha(opacity=0)';
        font-size: 200px;
    }
</style>
</head>

<body>

<script>
    function edit(page, n) {

        $("#myModalTitle").text(n < 0 ? "复制" : n == 0 ? "添加" : "修改");
        $("#myModalClose").click(function () {
            refreshList();
        });
        $("#myModalOK").show();
        $("#myModalBody").load(page + "?id=" + n + "&taskType=${taskType}");
        $("#myModal").modal({keyboard: false, backdrop: false, show: true});
    }

    function showResult(id) {

        $("#myModalTitle").text("监控结果");
        $("#myModalOK").hide();
        $("#myModalBody").html("正在加载......");
        $("#myModalBody").load("getLastTaskResult.htm?id=" + id + "&r=" + Math.random());
        $("#myModal").modal({keyboard: false, backdrop: false, show: true});
    }


    function doBatch(url, id, open, quiet, callback) {
        if (!quiet && !confirm("真的要继续吗？"))
            return;

        //var url = "delete.htm?";
        if (id) {
            url += "&ids=" + id;
        } else {
            var checkIds = jQuery("input:checked[name='ids']");
            var length = checkIds.length;
            if (length == 0) {
                alert("请至少选择一项！");
                return;
            }
            for (var i = 0; i < checkIds.length; i++)
                url += "&ids=" + checkIds[i].value;
        }

        if (open) {
            window.open(url);
        } else {
            Common.ajaxPost(url, null,

                    function (success, data) {
                        if (success)
                            if (callback) callback(); else refreshList();
                        else
                            alert("错误：" + data);
                    });
        }

    }

    function doStatus(sender, status, id) {
        doBatch('status.htm?status=' + (status == 1 ? 0 : 1), id, false, true, function () {
            if (status == 1) {
                $(sender).attr('disabled', "true");
                setTimeout(refreshList, 3000);
            } else {
                refreshList();
            }
        });

    }

    function doSwitchIp(targetIp) {
        var ip = targetIp.val();
        if (ip == "") {
            alert("请选择服务器");
            return;
        }
        var url = 'switchIp.htm?ip='+ encodeURIComponent(ip);
        doBatch(url);
    }
    function doPost(url) {
        Common.ajaxPost(url, null,
                function (success, data) {
                    if (success)
                        refreshList();
                    else
                        alert("错误：" + data);
                });

    }

    function doOperate(url, id, operate) {
        url += "&id=" + id;
        Common.ajaxPost(url, null,
                function (success, data) {
                    if (success) {
                        if (JSON.parse(data).success) {
                            alert(operate + "成功");
                        } else {
                            alert(operate + "失败：" + JSON.parse(data).resultMsg);
                        }
                    }
                    else
                        alert("错误：" + data);
                });

    }

    function doRetiveOnce(url, id) {
        url += "&id=" + id;
        Common.ajaxPost(url, null,
                function (success, data) {
                    console.log(data);
                    if (success) {
                        $("#myModalTitle").text("执行结果");
                        $("#myModalClose").click(function () {
                            refreshList();
                        });
                        $("#myModalBody").html(data)
                        $("#myModal").modal({keyboard: false, backdrop: false, show: true});
                    }
                    else
                        alert("错误：" + data);
                });

    }

    function refreshList() {
        window.location = "${url}&scroll=" + $(document).scrollTop();
    }
    $(document).ready(function () {
        $(document).scrollTop(${scroll});
    });
</script>
<div class="container-fluid">
    <!-- <a href="#"><strong><i class="glyphicon glyphicon-tasks"></i> 时间程序</strong></a>
    <hr style="margin-top: 3px; margin-bottom: 3px"> -->
    <div class="alert" style="background-color:#f0f0f0;">
        <#include "searchForm.ftl">

    </div>
    <#include "page.ftl">

    <table class="table table-striped table-hover" style="word-break: break-all; word-wrap: break-word;">
        <tr>
            <th align="left" width="20"><input type="checkbox" name="checkbox_check_all" id="checkbox_check_all"
                                               onClick="Common.checkAllClick(this,'ids')"></th>
            <th class="canHide">任务ID</th>
            <th>任务名称</th>

            <th class="canHide">服务器</th>
            <th class="canHide">计划</th>

            <th>状态</th>
            <th class="canHide">修改时间</th>
            <th class="canHide">操作者</th>

            <th>操作</th>

        </tr>
        <#list list as item>
            <tr>
                <td><input type="checkbox" name="ids" id="ids" value="${item.id}"/></td>
                <td>${item.id}  <#if  item.priority==2 > <span class="label label-danger">高</span></#if></td>
                <td>${item.name}</td>

                <td><#if (appName!)=="" >${item.appName}|</#if>${item.targetIp}</td>
                <td>${item.cron}</td>
                <td>${(item.status==0)?string("<font color='red'>停止</font>",(item.status==1)?string("<font color='green'>运行</font>","删除"))}</td>
                <td> ${item.operTime?string("yyyy-MM-dd HH:mm:ss")}</td>
                <td>${item.operUser}</td>
                <td><a href="#" class="btn btn-primary btn-xs" onclick="edit('edit.htm',${item.id})">修改</a>


                    <#if areaCustom.taskConfig??>
                        <a href="${ctx}/search/index.htm?admin=${admin}&timeTaskId=${item.id}&errorFlag=-1"
                           class="btn btn-info btn-xs" target="_blank">数据浏览</a>
                    </#if>
                    <span style="width:20px">&nbsp;</span>

                    <a href="#" class="btn btn-primary btn-xs" onclick="edit('edit.htm',-${item.id})">复制</a>
                    <span style="width:40px">&nbsp;</span>
                    <button class="btn btn-xs ${(item.status==1)?string("btn-warning","btn-success")}"
                            onclick="doStatus(this,${item.status},${item.id })">${(item.status==1)?string("停止","启动")}</button>

                    <a href="getLog.htm?id=${item.id}"  class="btn btn-primary btn-xs" target="_blank">查看日志</a>

                </td>

            </tr>
        </#list>
    </table>
    <#include "page.ftl">

</div>

    <#include "dialog.ftl">
    <#include "res.ftl">
</@frame.page>
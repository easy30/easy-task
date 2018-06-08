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
            alert($lg('uploadError'));
        }
    })

    return false;

}

function edit(n) {
    if(typeof(editNewWin) == "undefined" || !editNewWin) {
        $("#myModalTitle").text(n < 0 ? $lg("copy") : n == 0 ? $lg("add") : $lg("modify"));
        $("#myModalClose").click(function () {
            refreshList();
        });
        $("#myModalOK").show();
        $("#myModalBody").load(editPage + "?id=" + n + "&taskType=" + taskType);
        $("#myModal").modal({keyboard: false, backdrop: false, show: true});
    }else {
        window.open(editPage + "?id=" + n + "&taskType=" + taskType);
    }
}

function showResult(id) {

    $("#myModalTitle").text("监控结果");
    $("#myModalOK").hide();
    $("#myModalBody").html("正在加载......");
    $("#myModalBody").load("getLastTaskResult.htm?id=" + id + "&r=" + Math.random());
    $("#myModal").modal({keyboard: false, backdrop: false, show: true});
}


function doBatch(url, id, open, quiet, callback) {
    if (!quiet && !confirm($lg("continueConfirm")))
        return;

    //var url = "delete.htm?";
    if (id) {
        url += "&ids=" + id;
    } else {
        var checkIds = jQuery("input:checked[name='ids']");
        var length = checkIds.length;
        if (length == 0) {
            alert($lg("selectOne"));
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
                    alert($lg("error")+"：" + data);
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
        alert($lg("chooseServer"));
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
                alert($lg("error")+"：" + data);
        });

}

function doOperate(url, id, operate) {
    url += "&id=" + id;
    Common.ajaxPost(url, null,
        function (success, data) {
            if (success) {
                if (JSON.parse(data).success) {
                    alert(operate + $lg("success"));
                } else {
                    alert(operate + $lg("fail")+"：" + JSON.parse(data).resultMsg);
                }
            }
            else
                alert($lg("error")+"：" + data);
        });

}

function doRetiveOnce(url, id) {
    url += "&id=" + id;
    Common.ajaxPost(url, null,
        function (success, data) {
            console.log(data);
            if (success) {
                $("#myModalTitle").text($lg("runResult"));
                $("#myModalClose").click(function () {
                    refreshList();
                });
                $("#myModalBody").html(data)
                $("#myModal").modal({keyboard: false, backdrop: false, show: true});
            }
            else
                alert($lg("error")+"：" + data);
        });

}
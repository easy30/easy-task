/**
 * 自定义工具类
 *
 * */

//var baseUrl = "${mvn.jishou.baseUrl}";
//var imgTmpBaseUrl = "${mvn.jishou.imgTmpBaseUrl}";
//var imgBaseUrl = "${mvn.jishou.uploadImageApiBaseUrl}";
//var webBaseUrl = "${mvn.jishou.webBaseUrl}";
/**
 * 加载界面动画时序全局变量，控制setTimeout执行
 * */
var loadingPageVariable;

var Namespace = {};
Namespace.create = function (namespacePath) {
    // 以window为根
    var rootObject = window;
    // 对命名空间路径拆分成数组
    var namespaceParts = namespacePath.split('.');
    for (var i = 0; i < namespaceParts.length; i++) {
        var currentPart = namespaceParts[i];
        // 如果当前命名空间下不存在，则新建一个Object对象，等效于一个关联数组。
        if (!rootObject[currentPart]) {
            rootObject[currentPart] = {};
        }
        rootObject = rootObject[currentPart];
    }
};

UI = {
    /**
     * pageContainer(必) : 分页条所在div
     * totalResultSize(必) : 总数据量
     * curPagination(必): 当前页（从1开始）
     * fn(必) : 翻页函数
     * pageSize: 每页数据量
     * noDataNote: 无数据的时候，显示的文案
     * @param json
     */
    setPage: function (json) {
        json.pageContainer.html('');
        var totalResultSize = parseInt(json.totalResultSize);
        //1、如果没数据，就不显示分页信息
        if (!totalResultSize || totalResultSize <= 0) {
            json.pageContainer.addClass('bg-danger');
            var noDataNote = json.noDataNote;
            if (!noDataNote) {
                noDataNote = "无数据"
            }
            json.pageContainer.html('<div id="noDataDiv" class="alert alert-danger" role="alert" style="text-align: center">' + noDataNote + '</div>');
            return;
        }
        var curPagination = parseInt(json.curPagination);
        var pageSize = parseInt(json.pageSize);
        //2、设置每页数据量pageSize
        if (!pageSize || pageSize <= 0) {
            pageSize = 20;
        }
        //3、设置总页数totalPage
        var totalPage = Math.ceil(totalResultSize / pageSize);
        //4、构造
        //4.1、构造最外层div （position: relative）
        var relativeDiv = $('<div style="position: relative"></div>');
        json.pageContainer.append(relativeDiv);
        //4.2、构造页码部分div
        var centerDiv = $('<div style="text-align: right; margin-right: 20px;"></div>');
        relativeDiv.append(centerDiv);
        //4.3、构造页码ul
        var ul = $('<ul class="pagination "></ul>');
        centerDiv.append(ul);
        //4.4、添加页签等
        //4.5.1、添加【上一页】
        var pre = $('<li><a href="javascript:void(0)" aria-label="Previous"><span aria-hidden="true"><strong>&lt; </strong>上一页</span></a></li>');
        if (1 == curPagination) {
            pre.addClass("disabled");
        }
        ul.append(pre);
        //4.5.2、添加【页码】
        //少于5页，全部显示页码
        if (totalPage <= 5) {
            for (var i = 1; i <= totalPage; i++) {
                UI._appendLi(i, curPagination, ul);
            }
        } else {
            //1、当前页和其左右两页
            if (1 == curPagination) { //1.1、当前页为起始页，左边不用 设置
                UI._appendLi(1, curPagination, ul);
                UI._appendLi(2, curPagination, ul);
                UI._appendLi(3, curPagination, ul);
                UI._appendLi(4, curPagination, ul);
                UI._appendLiOmit(ul);
                UI._appendLi(totalPage, curPagination, ul);
            } else if (totalPage == curPagination) {  //1.2、当前页为最终页，右边不用设置
                UI._appendLi(1, curPagination, ul);
                UI._appendLiOmit(ul);
                UI._appendLi(totalPage - 3, curPagination, ul);
                UI._appendLi(totalPage - 2, curPagination, ul);
                UI._appendLi(totalPage - 1, curPagination, ul);
                UI._appendLi(totalPage, curPagination, ul);
            } else if (2 == curPagination) {     //1.3、当前页为第2页，其实页不用设置
                UI._appendLi(curPagination - 1, curPagination, ul);
                UI._appendLi(curPagination, curPagination, ul);
                UI._appendLi(curPagination + 1, curPagination, ul);
                UI._appendLi(curPagination + 2, curPagination, ul);
                UI._appendLiOmit(ul);
                UI._appendLi(totalPage, curPagination, ul);
            } else if (totalPage - 1 == curPagination) {     //1.4、当前页为倒数第2页，最终页不用设置
                UI._appendLi(1, curPagination, ul);
                UI._appendLiOmit(ul);
                UI._appendLi(curPagination - 2, curPagination, ul);
                UI._appendLi(curPagination - 1, curPagination, ul);
                UI._appendLi(curPagination, curPagination, ul);
                UI._appendLi(curPagination + 1, curPagination, ul);
            } else if (3 == curPagination) {   //1.5、当前页为第3页，
                UI._appendLi(1, curPagination, ul);
                UI._appendLi(curPagination - 1, curPagination, ul);
                UI._appendLi(curPagination, curPagination, ul);
                UI._appendLi(curPagination + 1, curPagination, ul);
                UI._appendLiOmit(ul);
                UI._appendLi(totalPage, curPagination, ul);
            } else if (totalPage - 2 == curPagination) {   //1.5、当前页为倒数第3页，
                UI._appendLi(1, curPagination, ul);
                UI._appendLiOmit(ul);
                UI._appendLi(curPagination - 1, curPagination, ul);
                UI._appendLi(curPagination, curPagination, ul);
                UI._appendLi(curPagination + 1, curPagination, ul);
                UI._appendLi(totalPage, curPagination, ul);
            } else {
                UI._appendLi(1, curPagination, ul);
                UI._appendLiOmit(ul);
                UI._appendLi(curPagination - 1, curPagination, ul);
                UI._appendLi(curPagination, curPagination, ul);
                UI._appendLi(curPagination + 1, curPagination, ul);
                UI._appendLiOmit(ul);
                UI._appendLi(totalPage, curPagination, ul);
            }
        }
        //4.5.3、添加【下一页】
        var next = $('<li><a href="javascript:void(0)" aria-label="Next"><span aria-hidden="true">下一页<strong> &gt;</strong></span></a></li>');
        if (totalPage == curPagination) {
            next.addClass("disabled");
        }
        ul.append(next);
        //4.6、添加【指定页跳转】
        var go = $('<li><input id="paginationGoIpt"/><button id="paginationGoBtn">GO</button></li>');
        ul.append(go);
        $('#paginationGoIpt').keypress(function (event) {
            if (event.charCode && (event.charCode < 48 || event.charCode > 57)) {
                event.preventDefault();
            }
        });
        $('#paginationGoIpt').keyup(function (event) {
            $(this).val($(this).val().replace(/[^\d]/g, ''));
        });
        //4.6、添加数据总量span
        var totalResultSizeSpan = $('<span class="text-primary" style="position: absolute; top:27px; left: 180px; "></span>');
        var selectPageDiv = $('<div class="text-primary" style="position: absolute; top:27px; margin-left: 20px;">每页显示数量：</div>');
        var selectPageSize = $('<select id="pageSizeSelect"></select>');
//        var page10 = $("<option value='10'>10</option>");
//        if (pageSize == 10) {
//            page10.attr("selected", "selected");
//        }
        var page20 = $("<option value='20'>20</option>");
        if (pageSize == 20) {
            page20.attr("selected", "selected");
        }
        var page40 = $("<option value='40'>40</option>");
        if (pageSize == 40) {
            page40.attr("selected", "selected");
        }
        var page80 = $("<option value='80'>80</option>");
        if (pageSize == 80) {
            page80.attr("selected", "selected");
        }
        var page100 = $("<option value='100'>100</option>");
        if (pageSize == 100) {
            page100.attr("selected", "selected");
        }
        var page150 = $("<option value='150'>150</option>");
        if (pageSize == 150) {
            page150.attr("selected", "selected");
        }
        var page200 = $("<option value='200'>200</option>");
        if (pageSize == 200) {
            page200.attr("selected", "selected");
        }
//        selectPageSize.append(page10);
        selectPageSize.append(page20);
        selectPageSize.append(page40);
        selectPageSize.append(page80);
        selectPageSize.append(page100);
        selectPageSize.append(page150);
        selectPageSize.append(page200);
        selectPageDiv.append(selectPageSize);
        relativeDiv.append(selectPageDiv);
        totalResultSizeSpan.text(" 总共 " + totalResultSize + " 条");
        relativeDiv.append(totalResultSizeSpan);
        //4.7、绑定事件
        $("#pageSizeSelect").change(function () {
            var pageSize = $("#pageSizeSelect").val();
            json.fn(1, pageSize);
        });
        $('#paginationGoIpt').blur(function () {
            var pn = $('#paginationGoIpt').val();
            if (isNaN(pn)) {
                $('#paginationGoIpt').val('1');
            } else {
                if (pn > totalPage) {
                    $('#paginationGoIpt').val(totalPage);
                }
            }
        });
        $('#paginationGoBtn').click(function () {
            var pageSize = $("#pageSizeSelect").val();
            var pn = $('#paginationGoIpt').val();
            if (!isNaN(pn)) {
                if (pn <= 0) {
                    pn = 1;
                    $('#paginationGoIpt').val(pn);
                }
                if (pn > totalPage) {
                    pn = totalPage;
                    $('#paginationGoBtn').val(totalPage);
                }
            }
            json.fn(pn, pageSize);
        });
        var as = centerDiv.find('a');
        as.each(function (index) {
            if (0 == index) { //上一页
                if ($(this.parentElement).attr('class') != 'disabled') {
                    $(this.parentElement).click(function () {
                        json.fn(curPagination - 1, pageSize);
                    });
                }
            } else if (as.length - 1 == index) { //下一页
                if ($(this.parentElement).attr('class') != 'disabled') {
                    $(this.parentElement).click(function () {
                        json.fn(curPagination + 1, pageSize);
                    });
                }
            } else {    //页码
                if ($(this.parentElement).attr('class') != 'disabled') {
                    $(this.parentElement).click(function () {
                        var as = $(this).find('a');
                        if (null == as || as.length == 0) {
                            //没找到a
                        } else {
                            var pageNo = parseInt(as[0].text);
                            if (!pageNo || pageNo <= 0) {
                                //pageNo 异常
                            } else {
                                json.fn(pageNo, pageSize);
                            }
                        }
                    });
                }
            }
        });
        if (totalPage <= 5) {
            $('#paginationGoIpt').hide();
            $('#paginationGoBtn').hide();
        }
    },
    _appendLi: function (i, curPagination, ul) {
        var li = $('<li></li>');
        if (i == curPagination) {
            li.addClass("active");
        }
        var a = $('<a href="javascript:void(0)">' + i + '</a>');
        li.append(a);
        ul.append(li);
    },
    _appendLiOmit: function (ul) {
        var li = $('<li></li>');
        li.addClass("disabled");
        var a = $('<a href="javascript:void(0)">...</a>');
        li.append(a);
        ul.append(li);
    },
    /**
     * 警告框
     */
    alert: function (msg, cbFn) {
        var old = $('#alert');
        if (!!old) {
            old.remove();
        }
        if (ValueUtil.isBlank(msg)) {
            msg = "失败";
        }
        //1、最外层div，占据整个屏幕
        var alert = $('<div  id="alert" class="modal fade label-default"></div>');
        $('body').append(alert);
        //2、内层对话框
        var dialog = $('<div class="modal-dialog"></div>');
        alert.append(dialog);
        //3、内容部分
        var content = $('<div class="modal-content"></div>');
        dialog.append(content);
        //4、标题部分
        var header = $('<div class="modal-header alert alert-danger"></div>');
        content.append(header);
        var h4 = $('<h4 class="modal-title"></h4>');
        var alertTitle = "警告";
        h4.text(alertTitle);
        header.append(h4);
        //5、内容
        var body = $('<div class="modal-body" ></div>');
        content.append(body);
        var p = $('<p></p>');
        body.append(p);
        p.html(msg);
        //6、按钮
        var footer = $('<div class="modal-footer"></div>');
        content.append(footer);
        var btn = $('<button id="alertCancelBtn" type="button" class="btn btn-danger btn-lg" data-dismiss="modal">关闭</button>');
        footer.append(btn);
        $('#alert').modal({
            backdrop: false,
            keyboard: false,
            show: false
        });
        $('#alertCancelBtn').click(function () {
            if (!ValueUtil.isBlank(cbFn)) {
                cbFn();
            }
            alert.remove();
        });
        $('#alert').modal('show');
    },

    /**
     * 确认框
     */
    confirm: function (msg, cbFun) {
        var old = $('#confirm');
        if (!!old) {
            old.remove();
        }
        //1、最外层div，占据整个屏幕
        var confirm = $('<div  id="confirm" class="modal fade label-default"></div>');
        $('body').append(confirm);
        //2、内层对话框
        var dialog = $('<div class="modal-dialog"></div>');
        confirm.append(dialog);
        //3、内容部分
        var content = $('<div class="modal-content"></div>');
        dialog.append(content);
        //4、标题部分
        var header = $('<div class="modal-header alert alert-warning"></div>');
        content.append(header);
        var h4 = $('<h4 class="modal-title"></h4>');
        var alertTitle = "确认";
        h4.text(alertTitle);
        header.append(h4);
        //5、内容
        var body = $('<div class="modal-body" ></div>');
        content.append(body);
        var p = $('<p></p>');
        body.append(p);
        p.html(msg);
        //6、按钮
        var footer = $('<div class="modal-footer"></div>');
        content.append(footer);
//        var center = $('<div class="center-block" style="width: 150px;"></div>');
//        footer.append(center);
        var sure = $('<button type="button" class="btn btn-primary btn-lg" >确认</button>');
        footer.append(sure);
        sure.click(function () {
            cbFun();
            $('#confirm').modal('hide');
        });
        var cancel = $('<button id="confirmCancelBtn" type="button" class="btn btn-default btn-lg" data-dismiss="modal">取消</button>');
        footer.append(cancel);
        $('#confirm').modal({
            backdrop: false,
            keyboard: false,
            show: false
        });
        $('#confirmCancelBtn').click(function () {
            confirm.remove();
        });
        $('#confirm').modal('show');

    },
    /**
     * 操作提示
     * */
    successTips: function (content) {
        var old = $('#tip');
        if (!!old) {
            old.remove();
        }
        if (!content) {
            content = "操作成功！"
        }
        var tip = $('<div id="tip" class="alert alert-success alert-dismissable fade in" role="alert"></div>');
        var span = $('<span class="glyphicon glyphicon-ok-sign"></span>');
        tip.append(span);
        tip.append('&nbsp;&nbsp;&nbsp' + content);
//        $(".nest-section").append(tip);
        $("#main").append(tip);
        setTimeout(function () {
            $("#tip").alert('close')
        }, 1000);
    },
    successToast: function (content, cbFun) {
        var old = $('#successToast');
        if (!!old) {
            old.remove();
        }
        if (!content) {
            content = "操作成功！"
        }
        //1、最外层div，占据整个屏幕
        var successToast = $('<div  id="successToast" class="modal fade label-default"></div>');
        $('body').append(successToast);
        //2、内层对话框
        var dialog = $('<div class="modal-dialog"></div>');
        successToast.append(dialog);
        //3、内容部分
        var contentDiv = $('<div class="modal-content"></div>');
        dialog.append(contentDiv);
        //4、标题部分
        var header = $('<div class="modal-header alert alert-success"></div>');
        contentDiv.append(header);
        var h4 = $('<h4 class="modal-title"></h4>');
        var alertTitle = "提示";
        h4.text(alertTitle);
        header.append(h4);
        //5、内容
        var body = $('<div class="modal-body" ></div>');
        contentDiv.append(body);
        body.html(content);
        //6、按钮
        var footer = $('<div class="modal-footer"></div>');
        contentDiv.append(footer);
        var btn = $('<button id="successToastCancelBtn" type="button" class="btn btn-danger btn-lg" data-dismiss="modal">关闭</button>');
        footer.append(btn);
        $('#successToastCancelBtn').click(function () {
            successToast.remove();
        });
        $('#successToast').modal({
            backdrop: false,
            keyboard: false,
            show: false
        });
        $('#successToast').modal('show');
        $('#successToast').delay(1000).fadeOut("slow", function () {
            $('#successToast').remove();
            if (cbFun) {
                cbFun();
            }
        });
    },

    loading: function (msg) {
        var old = $('#loading');
        if (!(null == old || undefined == old || 0 == old.length)) {
            old.remove();
            return;
        }
        if (!msg) {
            msg = "正在准备数据";
        }
        //1、最外层div，占据整个屏幕
        var loading = $('<div  id="loading" class="modal fade"></div>');
        $('body').append(loading);
        //2、内层对话框
        var dialog = $('<div class="modal-dialog"></div>');
        loading.append(dialog);
        //3、内容部分
        var content = $('<div class="modal-content"></div>');
        dialog.append(content);
        //5、内容
        var body = $('<div class="modal-body" ></div>');
//        var body = $('<span class="glyphicon glyphicon-refresh">正在加载数据</span>');
        content.append(body);
        var loadingImg = $('<img src="' + baseUrl + '/res/img/loading.gif" />');
        var p = $('<p></p>');
        p.append(loadingImg);
        var span = $('<span>' + msg + '</span>')
        p.append(span);
        body.append(p);
        $('#loading').modal({
            backdrop: false,
            keyboard: false,
            show: false
        });
        $('#loading').modal('show');
        $('body').css('padding', '0');
    },

    /**
     * 页面加载顶部动画 0.3秒加载20%，0.8秒加载60%
     * */
    startLoadingPage: function () {
        //初始化进度条
        $("#progress-bar-loadingPage").show();
        $("#progress-bar-loadingPage-width").width('0%');
        setTimeout(function () {
            $("#progress-bar-loadingPage-width").width('20%')
        }, 300);
        loadingPageVariable = setTimeout(function () {
            $("#progress-bar-loadingPage-width").width('60%')
        }, 800);
    },
    /**
     * 页面加载完成,0.3秒后隐藏
     * */
    finishLoadingPage: function () {
        //清除队列
        clearTimeout(loadingPageVariable);
        $("#progress-bar-loadingPage-width").width('100%');
        setTimeout(function () {
            $("#progress-bar-loadingPage").hide()
        }, 500);
    }

}


JsonHandler = {
    dealJson: function (json, successFn, failFn) {
        if (null == json || undefined == json) {
            console.error("jsonBack json parameter error. json: " + json);
            return;
        }
        if (json.ret == "0") {
            if (!successFn) {
                UI.successTips("成功");
            } else {
                successFn(json);
            }
        } else if (json.ret == "-7") {
            UI.alert(json.msg, function () {
                window.location = baseUrl + "/login/";
            });
        } else if (json.ret == "-71") {
            UI.alert("请重新登录", function () {
                window.location = baseUrl + "/login/?reUrl=" + json.msg;
            });
        } else {
            if (!failFn) {
                UI.alert(json.msg ? json.msg : "未知原因失败");
            } else {
                failFn(json);
            }
        }
    },
    /**
     * 处理返回page类型的json
     * json: 返回的json数据
     * pageContainer: 分页容器
     * pageFn: 分页方法
     * dataContainer: 接收数据的容器，table的body
     * rowFn: 返回table row 的方法
     * noDataNote: 无数据的时候，显示的文案
     */
    dealPageJson: function (jsonParameters) {
        JsonHandler.dealJson(jsonParameters.json, function () {
            JsonHandler._dealPage(jsonParameters)
        }, function () {
            UI.alert(jsonParameters.json.msg);
            UI.setPage({
                pageContainer: jsonParameters.pageContainer,
                totalResultSize: 0,
                noDataNote: jsonParameters.noDataNote
            });
        });
    },
    /**
     * 处理返回page
     * json: 返回的json数据
     * pageContainer: 分页容器
     * pageFn: 分页方法
     * dataContainer: 接收数据的容器，table的body
     * rowFn: 返回table row 的方法
     */
    _dealPage: function (jsonParameters) {
//        UI.successTips("成功");
        JsonHandler._dealPageResult(jsonParameters.pageContainer, jsonParameters.dataContainer, jsonParameters.json.page.datas, jsonParameters.rowFn);
        UI.setPage({
            pageContainer: jsonParameters.pageContainer,
            totalResultSize: jsonParameters.json.page.totalRecord,
            curPagination: jsonParameters.json.page.pageIndex,
            fn: function (pageNo, pageSize) {
                jsonParameters.pageFn(pageNo, pageSize);
            },
            pageSize: jsonParameters.json.page.pageSize,
            noDataNote: jsonParameters.noDataNote
        });
    },
    /**
     * @param container 接收数据的容器，table的body
     * @param pageResult list
     * @param rowFn 返回table row 的方法
     */
    _dealPageResult: function (pageContainer, dataContainer, pageResult, rowFn) {
        JsonHandler._clearLocalPageData(pageContainer, dataContainer);
        for (var i = 0; i < pageResult.length; i++) {
            var row = rowFn(pageResult[i]);
            dataContainer.append(row);
        }
    },
    /**
     * 清空原有数据
     * @param container 接收数据的容器，table的body
     */
    _clearLocalPageData: function (pageContainer, dataContainer) {
        var noDataDiv = $('#noDataDiv');
        if (null != noDataDiv) {
            noDataDiv.hide();
        }
        pageContainer.removeClass("bg-danger");
        dataContainer.empty();// 清空原有数据
    }
}

var ValueUtil = {
    isBlank: function (val) {
        return val == null || val == undefined || $.trim(val) == "";
    },
    isArrayEmpty: function (array) {
        return array == null || array == undefined || array.length == 0;
    },
    dealValue: function (ori, length) {
        if (ValueUtil.isBlank(ori)) {
            return "";
        }
        if (!length) {
            return ori;
        }
        return ori.length > length ? ori.substring(0, length) : ori;
    },
    formatDouble : function(data,length){
    	if (ValueUtil.isBlank(data)) {
            return "";
        }
    	if (!length) {
            return data;
        }
    	var data = data.toString();
    	if(data.indexOf(".")!=-1){
    		var dataArray = data.split(".");
        	if(dataArray[1].lenth<length){
        		var ca = length-dataArray[1].length;
        		var end = "";
        		for(var i=0;i<ca;i++){
        			end = end+"0";
        		}
        		return dataArray[0]+"."+dataArray[1]+end;
        	}else{
        		return dataArray[0]+"."+dataArray[1].substring(0,length);
        	}
    	}else{
    		var end = "";
    		for(var i=0;i<length;i++){
    			end = end+"0";
    		}
    		return data+"."+end;
    	}
    },
    dealParameter: function (val) {
        if (ValueUtil.isBlank(val)) {
            return undefined;
        }
        return $.trim(val);
    },
    dealUpdateParameter: function (val) {
        if (ValueUtil.isBlank(val)) {
            return "";
        }
        return $.trim(val);
    },
    occupySpaceLeft: function (ori, len) {
        ori += "";
        if (ori.length < len) {
            return ValueUtil._buildSpace(ori.length, len) + ori;
        }
        return ori;
    },
    _buildSpace: function (oriLen, needLen) {
        var size = needLen - oriLen;
        var space = "";
        for (var i = 0; i < size * 2; i++) {
            space += "&nbsp;"
        }
        return space;
    },
    dealMetaId: function (id) {
        return ValueUtil.isBlank(id) ? -1 : id;
    }
}

DateTimeUtil = {

    formatDateTime: function (date) {
        if (null == date || undefined == date || 0 == date) {
            return "";
        }
        date = new Date(date * 1000);
        var year = date.getFullYear();
        var month = date.getMonth() + 1; // js从0开始取
        var day = date.getDate();
        var hour = date.getHours();
        if (hour.toString().length == 1) {
            hour = "0" + hour;
        }
        var minutes = date.getMinutes();
        if (minutes.toString().length == 1) {
            minutes = "0" + minutes;
        }
        var seconds = date.getSeconds()
        if (seconds.toString().length == 1) {
            seconds = "0" + seconds;
        }
        var newDate = year + "-" + month + "-" + day + " " + hour + ":"
            + minutes + ":" + seconds;
        return newDate;
    },

    formatDateTimeSecond: function (date) {
        if (null == date || undefined == date || 0 == date) {
            return "不详";
        }
        date = new Date(date * 1000);
        var year = date.getFullYear();
        var month = date.getMonth() + 1; // js从0开始取
        var day = date.getDate();
        var hour = date.getHours();
        if (hour.toString().length == 1) {
            hour = "0" + hour;
        }
        var minutes = date.getMinutes();
        if (minutes.toString().length == 1) {
            minutes = "0" + minutes;
        }
        var seconds = date.getSeconds();
        if (seconds.toString().length == 1) {
            seconds = "0" + seconds;
        }
        var newDate = year + "-" + month + "-" + day + " " + hour + ":"
            + minutes + ":" + seconds + "";
        return newDate;
    },
    formatDate: function (date) {
        if (null == date || undefined == date || 0 == date) {
            return "";
        }
        date = new Date(date * 1000);
        var year = date.getFullYear();
        var month = date.getMonth() + 1; // js从0开始取
        var day = date.getDate();
        if (day < 10) {
            day = "0" + day;
        }
        var hour = date.getHours();
        if (month.toString().length == 1) {
            month = "0" + month;
        }
        var newDate = year + "/" + month + "/" + day;
        return newDate;
    },
    formatDateOther: function (date) {
        if (null == date || undefined == date || 0 == date) {
            return "";
        }
        date = new Date(date * 1000);
        var year = date.getFullYear();
        var month = date.getMonth() + 1; // js从0开始取
        var day = date.getDate();
        if (day < 10) {
            day = "0" + day;
        }
        var hour = date.getHours();
        if (month.toString().length == 1) {
            month = "0" + month;
        }
        var newDate = year + "-" + month + "-" + day;
        return newDate;
    },
    extractCreateDateStart: function (dateVal) {
        if (ValueUtil.isBlank(dateVal)) {
            return undefined;
        }
        return dateVal.split(' -- ')[0];
    },
    extractCreateDateEnd: function (dateVal) {
        if (ValueUtil.isBlank(dateVal)) {
            return undefined;
        }
        return dateVal.split(' -- ')[1];
    }
};


EXPORT_UTIL = {
    ajaxDownload: function (url, data) {
        var $iframe,
            iframe_doc,
            iframe_html;

        if (($iframe = $('#download_iframe')).length === 0) {
            $iframe = $("<iframe id='download_iframe'" +
                    " style='display: none' src='about:blank'></iframe>"
            ).appendTo("body");
        }

        iframe_doc = $iframe[0].contentWindow || $iframe[0].contentDocument;
        if (iframe_doc.document) {
            iframe_doc = iframe_doc.document;
        }

        iframe_html = "<html><head></head><body><form method='POST' action='" +
            url + "'>"

        Object.keys(data).forEach(function (key) {
            if (data[key] != undefined) {
                iframe_html += "<input type='hidden' name='" + key + "' value='" + data[key] + "'>";
            }
        });

        iframe_html += "</form></body></html>";

        iframe_doc.open();
        iframe_doc.write(iframe_html);
        $(iframe_doc).find('form').submit();
    }
};

QueryUtil={
    getParams:function () {
    var url = location.search; //获取url中"?"符后的字串
    var theRequest = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.substr(1);
        strs = str.split("&");
        for(var i = 0; i < strs.length; i ++) {
            theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
    return theRequest;
    },
    getParam:function (name,def){
        var p= this.getParams()[name];
        if(p==null) return def;
        return p;
    },
}

/**
 * Created by ruixiang.mrx on 2016/11/10.
 */
function submitIds(url,id,newWindow) {
    if (!confirm("真的要继续吗？"))
        return;
    if (id) {
        url += "&ids=" + id;
    } else {
        var checkIds = jQuery("input:checked[name='ids']");
        var length = checkIds.length;
        if (length == 0) {
            alert("请至少选择一项！");
            return;
        }
        for ( var i = 0; i < checkIds.length; i++)
            url += "&ids=" + checkIds[i].value;
    }

    if(newWindow){
        window.open(url);
    }else {

        Common.ajaxPost(url, null,

            function (success, data) {
                if (success)
                    refreshList();
                else
                    alert("错误：" + data);
            });
    }
}
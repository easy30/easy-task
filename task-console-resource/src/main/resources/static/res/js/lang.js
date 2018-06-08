function lang_trim(s){
    return s.replace(/(^\s*)|(\s*$)/g, "");
}
function lang_parse() {
    var text= lang_text.toString().replace(/^[^\/]+\/\*!?\s?/, '').replace(/\*\/[^\/]+$/, '');
    if(lang_text2){
        text+="\r\n"+lang_text2.toString().replace(/^[^\/]+\/\*!?\s?/, '').replace(/\*\/[^\/]+$/, '');
    }
    var list=text.split(/[\r\n]+/);
    var map={};
    for(var i in list){
        var line=list[i];
        var n=line.indexOf("=");
        if(n==-1){
            map[line]="";
        }else{
            map[lang_trim(line.substring(0,n))]=lang_trim(line.substring(n+1));
        }
    }
    return map;
}
var langMap=lang_parse?lang_parse():null;
var iii=0;
var langDomChangeTime=0;
var langTimeoutId=0;

function lang_apply() {
    if(langMap==null) return;
    var missKeys="";
    $("[lang-key]").each(function(){
        var key=$(this).attr("lang-key");
        if(key!=null && key!=""){
            var v=langMap[key];
            if(v!=null)  { $(this).html(v);}
            else{ missKeys+=key+"="+key+"\n";}
        }
        $(this).removeAttr("lang-key");
    })
    console.log(missKeys);
}

function lang_dom_change(){
    //DOMSubtreeModified
    document.addEventListener('DOMNodeInserted',function(e){

        if(langTimeoutId!=0 && new Date().getTime()-langDomChangeTime<50){
            clearTimeout(langTimeoutId);
        }
        langDomChangeTime = new Date().getTime();
        langTimeoutId=setTimeout(function lang_timeout() {
            console.log(iii++);
            lang_apply();
        },100);
    });
}



function $lg(key,def) {
    var v=null;
    if(langMap!=null){
        v=langMap[key];
    }
    if(v!=null) return v;
    if(def) return def;
    return null;


}

$(document).ready(function () {
    lang_apply();
    lang_dom_change();
})


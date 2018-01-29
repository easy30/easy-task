String.prototype.trim = function()
{
return this.replace(/(^\s*)|(\s*$)/g, "");
}

String.prototype.trimLeft = function()
{
return this.replace(/(^\s*)/g, "");
}

String.prototype.trimRight = function()
{
return this.replace(/(\s*$)/g, "");
}



function endsWith(s,ext,needCase)
{
	if(!needCase)
	{
	  s=s.toLowerCase();
	  ext=ext.toLowerCase();
	}

	var n1=s.length;
	var n2=ext.length;
	if(n1<n2) return false;
	if(s.substring(n1-n2)==ext) return true;
	return false;
	
}

function sameText(s1,s2)
{
	
	return s1.toLowerCase()==s2.toLowerCase();
}
////////////////////////////iframe  auto resize/////////////////////
function g_iframeResize(win,iframeName,level,lowerlimit)
{
	if(!win) win=window;
	if(!win.parent || win==window.top) return;
	if(!level) level=1;
	var iframe=null;
	
	if(!iframeName)
	{
		iframeName=win.name;
	}
	
	if(iframeName && iframeName!="") 
	{  
		var iframes=win.parent.document.getElementsByName(iframeName);
		if(iframes && iframes.length>0)
		 	for(var i=0;i<iframes.length;i++)
		 	  if(iframes[i].tagName== "IFRAME") { iframe=iframes[i]; break;}
	}
	else 
    {  
    	iframe= win.parent.frames[win.name];
    	if(iframe==null)
    
    	{
    		var iframes=win.parent.document.getElementsByTagName("IFRAME");
    		if(iframes && iframes.length>0) iframe=iframes[0];
    	}
    	
    }
    
    if(iframe==null) return;
    if(iframe.style.display=="none") return;
   
    iframe.height = win.document.body.scrollHeight;//+level;
    if(lowerlimit){
    	iframe.height = (iframe.height > lowerlimit)? iframe.height : lowerlimit;
    }
//    alert(iframe.name +";"+ iframe.height +";"+ lowerlimit);
    
   
    //iframe.style.width= document.body.scrollWidth;
    
    
    if(win.parent && win!=window.top && win.parent.frames.length>0 ) g_iframeResize (win.parent,win.parent.name,level+1);
    //else iframe.height=iframe.heigh+100;
    /*
    if (iframe.contentDocument && contentDocument.body.offsetHeight) //NetScape
    {		 
			iframe.height = contentDocument.body.offsetHeight; 
	}
	else if (iframe.document && document.body.scrollHeight) //IE
	{
			 
			iframe.height = document.body.scrollHeight;
	}
	
   */
			
	
}

function g_getCookie(sName,def)
{
  var value=null;
  // cookies are separated by semicolons	
  var aCookie = document.cookie.split("; ");
  for (var i=0; i < aCookie.length; i++)
  {
    // a name/value pair (a crumb) is separated by an equal sign
    var aCrumb = aCookie[i].split("=");
    if (sName == aCrumb[0]) 
      value= unescape(aCrumb[1]);
  }

  if(value==null && def) value=def;
  // a cookie with the requested name does not exist
  return value;
}
 // use: setCookie("name", value);
function g_setCookie(sName,sValue,isSession)
{
    var today = new Date();
    var expiry = new Date(today.getTime() + 3*365 * 24 * 60 * 60 * 1000); // plus 3 year
    if (sValue != null && sValue != "")
    {
       var ck=sName + "=" + escape(sValue);
       
       if(!isSession)
       {
           var today = new Date();
          var expiry = new Date(today.getTime() + 3*365 * 24 * 60 * 60 * 1000); // plus 3 year
    
           ck+= "; expires=" + expiry.toGMTString();
       }
       document.cookie=ck;
     }
}

// format("{0}*{1}={2};",[2,3,6]);
function format(formatText,params)
   {
   		for(var i=0;i<params.length;i++)
   		{
   		    var regexp = new RegExp("\\{"+i+"\\}", "g");
   		
   			formatText=formatText.replace(regexp, params[i]);
   			
   		}
   		return formatText;
   }
   
 
 


//根据所给的值选择下拉菜单的某一项
function selectItem(selectMenu, value) {
	var o = selectMenu;
	for (var i = 0; i < o.length; i++) {
		if (o[i].value == value) {
			o[i].selected = true;
			break;
		}
	}
}
//移动下拉菜单的项  flag:0 上移  1:下移
function  moveSelectMenu(selectMenu,flag)
{
    var o = selectMenu;
  
	for (var i = 0; i < o.length; i++) 
	{
	  
	  if(i==o.selectedIndex)
	  {
	     
	     var strText=o[i].text;
	     var strValue=o[i].value;
	     if (flag==0&& i>0) 
	     {
	     
	       o[i].text=o[i-1].text;
	       o[i].value=o[i-1].value;
	       o[i-1].text=strText;
	       o[i-1].value=strValue; 
	       o.selectedIndex =i-1;
	
	       
	     }
	     if (flag==1&& i<o.length-1) 
	     {
	       
	        o[i].text=o[i+1].text;
	        o[i].value=o[i+1].value;
	        o[i+1].text=strText;
	        o[i+1].value=strValue; 
	        o.selectedIndex =i+1;
	       
	     }
	     break;
	  }
	}
}

//把下拉1中的已选的值移动到下拉2
function moveToSelectMenu(select1, select2) {
	for (var i = select1.length - 1; i >= 0; i--) {
	
		if (select1[i].selected) {
		   
			select2.add( new Option(select1[i].text, select1[i].value));
			select1.remove(i);
		}
	}
}


function initArray(array,attr,initValue)
{
   if(array.length)
   {
      for(var i=0;i<array.length;i++)
      {
      	if(attr!=null && attr!="")
        	array[i].setAttribute(attr,initValue);
        else array[i]=initValue;
       }
        
   }
   else {
      if(attr!=null && attr!="")
        	array.setAttribute(attr,initValue);
        else array=initValue;
   }
}

// if value==null then ignore value .
function getChildNode(obj,attribute,value,recursive)
{
	   var  node=null;
	   //tag=tag.toUpperCase();
	   if(obj==null) return null;
	   for (var i=0;i<obj.childNodes.length;i++)
	   { 
	   		
	   		//alert(obj.childNodes[i].tagName);
	   		var child=obj.childNodes[i];
	   		if(typeof(child.getAttribute)!="undefined")
	   		{
	   			if(value==null || child.getAttribute(attribute)==value) return child;
	   		 }
			 //if( eval("obj.childNodes["+i+"]."+attribute)==value) return obj.childNodes[i];
		      if (recursive) 
			{
					var  childNode=getChildNode(child,attribute,value,recursive)
					if(childNode!=null) return childNode;
			}
	   }
	   return node;
	  
}

// JavaScript Document
function g_getChildNodeByTag(obj,tag,recursive)
{
	   var  node=null;
	   //tag=tag.toUpperCase();
	  
	   if(obj==null) return null;
	   for (var i=0;i<obj.childNodes.length;i++)
	   {
	   		if(obj.childNodes[i].tagName && sameText(obj.childNodes[i].tagName,tag)) return obj.childNodes[i];
		  	else if (recursive) 
			{
					var  childNode=g_getChildNodeByTag(obj.childNodes[i],tag,recursive);
					if(childNode!=null) return childNode;
			}
	   }
	   return node;
	  
}
function getParentNode(obj,attribute,value)
{
		var obj=obj.parentNode;
		while(obj!=null&& !obj.getAttribute(attribute)==value) obj=obj.parentNode;
		return obj;
}

function getParentNodeByTag(obj,tag)
{
		var obj=obj.parentNode;
		while(obj!=null&& !sameText(obj.tagName,tag))  obj=obj.parentNode;
		return obj;
}
  
 
    
function checkAll(sender,form,checkboxname)//obj为表单对象
{ 
    
   if(sender.type!="checkbox")
   {
   		if(sender.checked==null) sender.checked=false;
   		sender.checked=! sender.checked;
   	}
   if(form)
   {	
   	
	   for(var j=0;j<form.length;j++)
	    {
	    var e = form.elements[j];
	   
	    if( e && e.type=="checkbox" && e.name==checkboxname)
	     {
	       e.checked=sender.checked;
	     }
	    }
    }
    else 
    {
         var cbs=document.getElementsByName(checkboxname);
	 
		for (var i = 0; i < cbs.length; i++) {
			cbs[i].checked = sender.checked;
		}
	 
    }
}
function g_$(id)
{
	return document.getElementById(id);
}
//-- Check all checkboxs
function g_checkAllClick(sender,name)
{
	var checked=sender.checked;
	if(!name) name="checkbox_row";
	var arrCheckBox=document.getElementsByName(name);
	if(arrCheckBox!=null)
		for(var i=0;i<arrCheckBox.length;i++) arrCheckBox[i].checked=checked;
}

//--补充单元格
function g_fixCell(table,maxcol,width,color)
	{
		 
		var rowCount=table.rows.length; 
		var lastRow=table.rows[rowCount-1];
		
		var colCount=lastRow.cells.length; 
		 
		for(var i=colCount;i<maxcol;i++)
		{
		   var cell=lastRow.insertCell(i);
		 
		 	if(rowCount==1 && width!="")
		 	{
		 		  cell.style.width= width+"px";
		 	}
		  
		   cell.style.backgroundColor =color;
	     
		 }
	}

 // callback(success,data)
  function g_ajax(type,url,params,callback) {
	
	  var n=url.indexOf("?");
	  if(n==-1)url+="?";
	  url+="&ajaxRandom="+Math.random();
	  var options = {
		type: type,
		async: false,   
		url: url,  
		data : params,
		error: function(XMLHttpRequest, textStatus, errorThrown) {	
			var data=XMLHttpRequest.responseText;
			if(errorThrown)data+=" "+errorThrown;
		   if(callback) callback(false,data); else alert("发生错误："+data);
			 
		},
		success: function(data) {
			 
			 if(callback) callback(true,data);else alert("操作成功");
			 
		}
	};
	 
	jQuery.ajax(options);
}
  
function g_ajaxPost(url,data,callback) 
{
	g_ajax("POST",url,data,callback);
}
function g_ajaxGet(url,data,callback) 
{
	g_ajax("GET",url,data,callback);
}
// callback is a function or  id
function g_ajaxSubmit(formId,callback) 
{
	
	var options = {
		async: false, 
		data : {ajaxRandom:Math.random()},  // and random param
		beforeSubmit: function() 
		{ 
		},
		success: function(data) 
		{
		   if(callback) 
			   {
			      if(typeof(callback)=="function") 
			   		callback(true,data);
			      else if( document.getElementById(callback)!=null ) 
			    	  jQuery("#"+callback).html(data);
			    	 
			   }
		   else alert("操作成功");
		   	
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {	
			var data=XMLHttpRequest.responseText;
			if(errorThrown)data+=" "+errorThrown;
		   if(callback) {
			      if(typeof(callback)=="function") 
				   		callback(false,data);
				      else if( document.getElementById(callback)!=null ) 
				    	  jQuery("#"+callback).html(data);
				    	 
				   }
		   else alert("发生错误："+data);
			 
		} 
	};
	
	jQuery(formId).ajaxSubmit(options); 
}

function g_trimInputText(f)
{
	for(var i=0;i<f.elements.length;i++)
	{
		var e=f.elements[i];
		if(e.type=="text") e.value=e.value.trim();
		
	}
}

function g_getGreet()
{
	var s;
	now = new Date();
	hour = now.getHours();
	if(hour < 6){s=("凌晨好");}
	else if (hour < 9){s=("早上好");}
	else if (hour < 12){s=("上午好");}
	else if (hour < 14){s=("中午好");}
	else if (hour < 17){s=("下午好");}
	else if (hour < 19){s=("傍晚好");}
	else if (hour < 22){s=("晚上好");}
	else {s=("夜里好");}
	return s;

}
//--  "a{id}bc", {id:1}
function g_formatByNames(formatText,params)
{
	for(var x in params)
	{
		var regexp = new RegExp("\\{"+x+"\\}", "g");

		formatText=formatText.replace(regexp, params[x]);

	}
	return formatText;
}





var Common=new Object();
Common.iframeResize=g_iframeResize;
Common.setCookie=g_setCookie;
Common.getCookie=g_getCookie;
Common.getChildNodeByTag=g_getChildNodeByTag;
Common.$=g_$;
Common.checkAllClick=g_checkAllClick;
Common.fixCell=g_fixCell;
Common.ajaxPost=g_ajaxPost;
Common.ajaxGet=g_ajaxGet;
Common.ajaxSubmit=g_ajaxSubmit;
Common.trimInputText=g_trimInputText;
Common.getGreet=g_getGreet;
Common.formatByNames=g_formatByNames


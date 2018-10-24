(function($){
   
    $.fn.load2 = function(url,data,func){
    	$("#loading").center();
    	return this.each(function(){
    		$(this).load(url,data,function(){ 
    			func;
    			$('.ajaxlink').ajaxlink(); 
    			$("#loading").hide();
    		} );
    	});
    	
    };
    
    jQuery.fn.center = function () {  
    	return this.each(function(){
    		$(this).css('position','absolute');  
    		$(this).css('top', ( $(window).height() - $(this).height() ) /2 +$(window).scrollTop() + 'px');  
    		$(this).css('left', ( $(window).width() - $(this).width() ) / 2+$(window).scrollLeft() + 'px');  
    		$(this).show();
    	} );  
    }  ;
})(jQuery);


function  toggleTip(){
	var c=$.cookie("showTip");
	if(c==null|| c=="1"){
		$("#showTip").hide();
		$.cookie("showTip","2");
	}
	else { 
		$("#showTip").show();
		$.cookie("showTip","1");
	}
}

function  delSpm(s){
    var n= s.indexOf("?spm=");
    if(n>=0){
        var n2= s.indexOf("&",n);
        if(n2>=0) return s.substring(0,n+1)+ s.substring(n2+1);
        else return   s.substring(0,n);
    }
    return s;
}

function hideTableCols(width) {
	if(window.innerWidth>=width) return;
	$("table").each(function () {
		var tb=$(this);
		$(".canHide",tb).each(function () {
			var n=$(this).index();
			console.log(this);
			console.log(n);
			$("tr",tb).find('th:eq('+n+')').hide();
			$("tr",tb).find('td:eq('+n+')').hide();
		})
	})

}

function popover(sender,content,autoHide) {
    //$(sender).popover('destroy');
    var options = {"trigger": "manual", "placement": "auto", "content": content};
    if (!autoHide) {
        var func = " $('" + sender + "').popover('hide')";
        var template = '<div class="popover" role="tooltip"><div class="popover-content"></div><div style="text-align: center"><button onclick="' + func + '">Close</button></div></div>';
        options.template=template;

    }
    $(sender).popover(options);
    $(sender).popover('show');
    if(autoHide) {
        setTimeout(function () {
            $(sender).popover('hide');
        }, 3000);
    }
}

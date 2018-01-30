
$(document).ready(function(){
	$('.ajaxlink').ajaxlink(); 
});

//
//  This is a simple plugin which converts normal links into
// AJAX GET requests
//
// Steve Kemp
// --
// http://www.steve.org.uk/jquery/
// < a rel="" href="dddd"  target=""></a>

//
// create closure
//
(function($){
    //
    // plugin definition
    //
    $.fn.ajaxlink = function(options){

        // build main options before element iteration
        var opts = $.extend({}, $.fn.ajaxlink.defaults, options);

        // iterate and add the click handler to each matched element
        return this.each(function(){
            $this = $(this);
            if(jQuery(this).data("done")=="1") return;
            jQuery(this).data("done","1");
            jQuery(this).click( function() {
            	$("#loading").center();
            	$("#loading").show();
            	  var url=this.href;
            	  var n=url.indexOf("?");
            	  if(n==-1)url+="?";
            	  url+="&ajaxRandom="+Math.random();
                 opts["url"] = url;
                 div="#"+this.target;
                  
                    if ( ! opts["success"] )
                    {
                       opts["success"] = function(data) { 
                    	   if(data!=null&& data.indexOf("user_no_login")>=0){ 
                       		if(window.location.href) window.location.href ="/";
                       		else window.location ="/";
                       		}
                    	   $( div ).html( data );
                    	   $('.ajaxlink').ajaxlink(); //redo again;
                       }
                    }
                    
                    opts["complete"] = function(xhr,data) { 
                    	$("#loading").hide();
                 	   if ( opts["oncomplete"] ) { opts["oncomplete"](); }
                    }
                    
                    $.ajax( opts );
                
                return false;
            })
        });
    };

    //
    // plugin defaults: None by default.
    //
    $.fn.ajaxlink.defaults = {};

    //
    // end of closure
    //
})(jQuery);
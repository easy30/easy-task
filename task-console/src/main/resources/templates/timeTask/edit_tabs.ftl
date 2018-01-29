<#ftl encoding="utf-8">
<#include "constants.ftl">
<form id="edit_form" name="edit_form" method="post" action="save.htm">
 	<input type="hidden" name="csrfToken" value="${csrfToken}">
	<div role="tabpanel">
		<!-- Nav tabs -->
		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active"><a href="#tab1" aria-controls="tab1" role="tab" data-toggle="tab">基本配置</a></li>
			<li role="presentation"><a href="#tab2" aria-controls="tab2" role="tab" data-toggle="tab">调用参数</a></li>
		</ul>
		<!-- Tab panes -->
		<div class="tab-content">
			<%------- tab1 ---------%>
			<div role="tabpanel" class="tab-pane active" id="tab1">
			  <%@ include file="edit1.jsp"%> 
			</div>
			<%------- tab1 ---------%>
			<div role="tabpanel" class="tab-pane" id="tab2">
			<%@ include file="edit2.jsp"%> 
			</div>
		</div>
	</div>
</form>
<div class="alert" id="task_save_result" style="display: none"></div>
<script>
	$(document).ready(
			function() {

				$("#myModalOK").unbind("click").click(function() {
					$("#edit_form").submit();

				});
				$("#edit_form").validate(
						{
							submitHandler : function(form) {
								Common.ajaxSubmit("#edit_form", function(
										success, text) {
									if (success)
										$("#task_save_result").removeClass(
												"alert-danger").addClass(
												"alert-success");
									else
										$("#task_save_result").removeClass(
												"alert-success").addClass(
												"alert-danger");
									$("#task_save_result").html(
											success ? "保存成功!" : text);
									$("#task_save_result").show();

								});

							}
						});

			});
	
	
	
	{"beanName":"typeIssue15x5","issues":{"state":1,"kind":0,"years":1,"weekDays":"","openTime":"19:00:00","closeTime":"18:30:00","dateFormat":"yyyy","serialLength":3},"luckyNumbers":{kind:0,"state":1,"days":300}}
</script>

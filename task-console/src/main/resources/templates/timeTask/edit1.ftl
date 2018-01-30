<#ftl encoding="utf-8">
<#include "constants.ftl">
<#setting classic_compatible=true >
<input type="hidden" name="id" value="${entity.id }"/>
<input type="hidden" name="taskType" value="${entity.taskType}"/>
<input type="hidden" name="status" value="${entity.status}"/>
<table class="" width="100%" border="0" align="center">

        <tr>
            <td>任务ID：</td>
            <td>
                    ${entity.id}
            </td>
            <td>*应用：</td>
            <td>
                <select name="appName" id="appName" class="form-control input-xs" onchange="onAppChange(this)" data-rule-required="true">
                    <option value=""></option>
                     <#list  appNames as item>
                        <option value="${item}" ${(entity.appName==item)?string('selected','')} >${item}</option>
                     </#list>
                </select>
               <#-- <script> document.getElementById("appName").value=${appName}</script>-->

            </td>
        </tr>

    <tr>
        <td><span color="red">*</span>任务名称：</td>
        <td><input type="text" class="form-control input-xs" style="margin: 3px" id="name" name="name" value="${entity.name!}"
                   data-rule-required="true"/></td>
        <td>*服务器：</td>
        <td>

            <div class="btn-group">
                <input type="text" class="form-control  input-xs" style="margin: 3px" id="targetIp" name="targetIp"
                       autocomplete="off"
                       data-toggle="dropdown" aria-expanded="false" value="${entity.targetIp!}"
                       data-rule-required="true"/>
                <ul class="dropdown-menu" id="targetIpList" style="overflow: auto; max-height: 300px" role="menu">
                    <#list machines as item>
                        <li><a href="#" onclick="edit_form.targetIp.value=this.innerText">${item}</a></li>
                    </#list>
                </ul>
            </div>

            <div class="btn-group" style=" display: none">
                <span color="red">*</span>所属计划：
                <input type="text" class="form-control input-xs" style="margin: 3px;" id="scheduler" name="scheduler"
                       data-toggle="dropdown" aria-expanded="false" value="default" data-rule-required="true"/>
                <ul class="dropdown-menu" style="overflow: auto; max-height: 300px" role="menu">
                    <#list propsMap.scheduler! as item>
                        <li><a href="#" onclick="edit_form.scheduler.value=this.innerText">${item}</a></li>
                    </#list>
                </ul>
            </div>
        </td>
    </tr>

    <tr>


        <td><span color="red">*</span>计划时间：</td>
        <td><input type="text" class="form-control input-xs" style="margin: 3px" id="cron" name="cron"
                   value="${entity.cron}" data-rule-required="true"/></td>
        <td><span color="red">*</span>${areaCustom.categoryNames[0]}：</td>
        <td>
            <div class="btn-group">
                <input type="text" class="form-control input-xs" style="margin: 3px" id="cat1" name="cat1" autocomplete="off"
                       data-toggle="dropdown" aria-expanded="false" value="${cats[0]!}" data-rule-required="true"/>
                <ul class="dropdown-menu" style="overflow: auto; max-height: 300px" role="menu">
                    <#list  propsMap.cat1 as item>
                        <li><a href="#" onclick="edit_form.cat1.value=this.innerText">${item}</a></li>
                    </#list>
                </ul>
            </div>
        </td>

    </tr>


    <#--    <td>${areaCustom.categoryNames[3]}：</td>
        <td>

            <div class="btn-group">
            <input type="text" class="form-control input-xs" style="margin: 3px" id="cat4" name="cat4" autocomplete="off"
            data-toggle="dropdown" aria-expanded="false" value="${cats[3]}"   />
                <ul class="dropdown-menu" style="overflow: auto; max-height: 300px" role="menu">
                    <#list propsMap.cat4  as item>
                    <li><a href="#" onclick="edit_form.cat4.value=this.innerText">${item}</a></li>
                    </#list>
                </ul>
            </div>
        </td>-->

  <#if (fixed==0)>
        <tr>
            <td>Bean名称：</td>
            <td>
                <div class="btn-group">
                    <input type="text" class="form-control input-xs" style="margin: 3px" id="bean" name="bean"
                           data-toggle="dropdown" aria-expanded="false" value="${invoker.bean}"
                           data-rule-required="true"/>
                    <ul class="dropdown-menu" style="overflow: auto; max-height: 300px" role="menu">
                        <#list propsMap.bean as  item >
                            <li><a href="#" onclick="edit_form.bean.value=this.innerText">${item}</a></li>
                        </#list>
                    </ul>
                </div>

            </td>
            <td>Bean方法：</td>
            <td><input type="text" class="form-control input-xs" style="margin: 3px" id="method" name="method"
                       value="${invoker.method}" /></td>
        </tr>
        <tr>
            <td>Bean参数：</td>
            <td colspan="3"><textarea name="args" class="form-control input-xs" style="width: 97%"
                                      rows="5">${invoker.args}</textarea></td>
        </tr>
        <tr>
            <td>中断方法：</td>
            <td><input type="text" class="form-control input-xs" style="margin: 3px" id="stopMethod" name="stopMethod"
                       value="${invoker.stopMethod}"/></td>
        </tr>
    </#if>

      <tr>
          <td>自定义配置：</td>
          <td colspan="3"><textarea name="taskConfig" class="form-control input-xs" style="width: 97%"
                                    rows="6">${entity.taskConfig}</textarea>
          </td>
      </tr>



</table>
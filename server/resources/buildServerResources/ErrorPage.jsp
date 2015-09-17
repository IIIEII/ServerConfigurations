<%@ include file="/include.jsp"%>

<c:url value="/admin/admin.html?item=${pluginName}" var="pluginUrl"/>

<bs:messages key="serverConfigurationMessage"/>

<div class="saveButtonsBlock">
    <input type="button" value="Cancel" class="btn cancel"
           onclick="BS.openUrl(event, '${pluginUrl}'); return false;">
</div>
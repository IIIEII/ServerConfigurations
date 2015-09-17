<%@ include file="/include.jsp" %>

<c:url value="/admin/admin.html?item=${pluginName}" var="pluginUrl"/>
<c:url value="${controllerPath}" var="controllerPath"/>

<div class="serverConfigurations">

    <bs:refreshable containerId="templateMessageComponent" pageUrl="${pageUrl}">

        <bs:messages key="serverConfigurationMessage"/>

    </bs:refreshable>

    <bs:refreshable containerId="templateComponent" pageUrl="${pageUrl}">

        <h2 class="noBorder">
            <c:choose>
                <c:when test="${fn:length(name)>0}">
                    Edit server configuration template
                </c:when>
                <c:otherwise>
                    New server configuration template
                </c:otherwise>
            </c:choose>
        </h2>

        <form id="templateForm" action="<c:out value='${controllerPath}'/>?action=saveTemplate" method="post">
            <table class="runnerFormTable">
                <tr>
                    <th><label for="name">Template Name:</label></th>
                    <td colspan="2">
                        <c:choose>
                            <c:when test="${fn:length(name)==0}">
                                <input type="text" name="name" id="name" value="<c:out value='${name}'/>"
                                       class="textField">
                            </c:when>
                            <c:otherwise>
                                <c:out value='${name}'/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>

            <h2 class="noBorder">Template parameters</h2>

            <table class="runnerFormTable">
                <tr class="groupingTitle">
                    <td>Key</td>
                    <td colspan="2">Type</td>
                </tr>
                <c:forEach var="property" items="${properties}">
                    <tr>
                        <td>
                            <c:out value='${property.name}'/>
                        </td>
                        <td>
                            <input type="hidden" name="key" value="<c:out value='${property.name}'/>">
                            <input type="hidden" name="type" value="<c:out value='${property.type}'/>">
                            <c:out value='${property.type}'/>
                        </td>
                        <td class="actions edit last">
                            <a href="#"
                               onclick="if(confirm('Are you sure you want to delete template parameter?')) return ServerConfigurations.deleteTemplateParameter(event);">Delete</a>
                        </td>
                    </tr>
                </c:forEach>
                <tr>
                    <td colspan="2"></td>
                    <td class="actions edit last">
                        <a href="#" onclick="return ServerConfigurations.addTemplateParameter(event);">Add</a>
                    </td>
                </tr>
            </table>
            <div class="saveButtonsBlock">
                <c:set var="onclick">
                    <c:choose>
                        <c:when test="${fn:length(name)==0}">
                            return ServerConfigurations.saveTemplate(event, function success() {
                                BS.openUrl(event, '${pluginUrl}&action=editTemplate&name=' + encodeURIComponent($('name').value));
                            });
                        </c:when>
                        <c:otherwise>
                            return ServerConfigurations.saveTemplate(event);
                        </c:otherwise>
                    </c:choose>
                </c:set>
                <input id="templateSaveButton" type="submit" value="Save" class="btn btn_primary submitButton"
                       onclick="${onclick}">
                <input type="button" value="Cancel" class="btn cancel "
                       onclick="BS.openUrl(event, '${pluginUrl}'); return false;">
                <input type="hidden" id="initialName" name="initialName" value="<c:out value='${name}'/>"/>
                <i id="saving" style="display: none; " class="icon-refresh icon-spin progressRing progressRingDefault"
                   title="Please wait..."></i>
            </div>
            <select id="typesSelect" style="display:none;">
                <c:forEach var="type" items="${types}">
                    <option value="<c:out value='${type}'/>"><c:out value='${type}'/></option>
                </c:forEach>
            </select>
        </form>
    </bs:refreshable>

</div>
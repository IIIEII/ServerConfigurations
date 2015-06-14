<%@ include file="/include.jsp"%>

<c:set var="pluginUrl">/admin/editProject.html?projectId=${projectId}&tab=${pluginName}</c:set>

<div class="serverConfigurations">

    <bs:refreshable containerId="serverConfigurationMessageComponent" pageUrl="${pageUrl}">

        <bs:messages key="serverConfigurationMessage"/>

    </bs:refreshable>

    <bs:refreshable containerId="serverConfigurationComponent" pageUrl="${pageUrl}">

        <form id="serverConfigurationForm"  action="<c:out value='${controllerPath}'/>?action=save&projectId=${projectId}" method="post">
            <table class="runnerFormTable">
                <tbody>
                    <tr>
                        <th><label for="prefix">Parameters Prefix:</label></th>
                        <td>
                            <input type="text" name="prefix" id="prefix" value="<c:out value='${prefix}'/>" class="textField" placeholder="{some_prefix}">
                            <div class="grayNote">All server configuration parameters names will be prefixed with this string (e.g. %{some_prefix}.domain.name%).</div>
                        </td>
                    </tr>
                    <tr>
                        <th><label for="name">Configuration Name:</label></th>
                        <td>
                            <forms:select id="name" name="name" enableFilter="true">
                                <forms:option value="">-- Select server configuration --</forms:option>
                                <c:forEach var="configuration" items="${configurations}">
                                    <forms:option value="${configuration.name}" selected="${configuration.name == name}"><c:out value='${configuration.name}'/></forms:option>
                                </c:forEach>
                            </forms:select>
                            <div class="grayNote">Choose server configuration. All parameters will be available in build configurations.</div>
                        </td>
                    </tr>
                </tbody>
            </table>
            <div class="saveButtonsBlock">
                <c:set var="onclick">
                    <c:choose>
                        <c:when test="${fn:length(prefix)==0}">
                            return ServerConfigurations.saveProjectServerConfiguration(event, function success() {
                                BS.openUrl(event, '${pluginUrl}&action=edit&prefix=' + encodeURIComponent($('prefix').value));
                            });
                        </c:when>
                        <c:otherwise>
                            return ServerConfigurations.saveProjectServerConfiguration(event);
                        </c:otherwise>
                    </c:choose>
                </c:set>
                <input id="serverConfigurationSaveButton"  type="submit" value="Save"
                       class="btn btn_primary submitButton"
                       onclick="${onclick}">
                <input type="button" value="Cancel" class="btn cancel"
                       onclick="BS.openUrl(event, '${pluginUrl}'); return false;">
                <input type="hidden" id="initialPrefix" name="initialPrefix" value="<c:out value='${prefix}'/>" />
                <i id="saving" style="display: none; " class="icon-refresh icon-spin progressRing progressRingDefault" title="Please wait..."></i>
            </div>
        </form>

    </bs:refreshable>

</div>
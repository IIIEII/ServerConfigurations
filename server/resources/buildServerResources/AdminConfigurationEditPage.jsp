<%@ include file="/include.jsp" %>

<c:set var="pluginUrl">/admin/admin.html?item=${pluginName}</c:set>

<div class="serverConfigurations">

    <bs:refreshable containerId="serverConfigurationMessageComponent" pageUrl="${pageUrl}">

        <bs:messages key="serverConfigurationMessage"/>

    </bs:refreshable>

    <bs:refreshable containerId="serverConfigurationComponent" pageUrl="${pageUrl}">

        <h2 class="noBorder">
            <c:choose>
                <c:when test="${fn:length(name)>0}">
                    Edit server configuration
                </c:when>
                <c:otherwise>
                    New server configuration
                </c:otherwise>
            </c:choose>
        </h2>

        <form id="serverConfigurationForm" action="<c:out value='${controllerPath}'/>?action=save" method="post">
            <table class="runnerFormTable">
                <tr>
                    <th><label for="name">Configuration name:</label></th>
                    <td>
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
                <tr>
                    <c:set var="onchange">
                        <c:choose>
                            <c:when test="${fn:length(name)==0}">
                                ServerConfigurations.saveServerConfiguration(event, function success() {
                                    BS.openUrl(event, '${pluginUrl}&action=edit&name=' + encodeURIComponent($('name').value));
                                }, function failure() {
                                    $('templateName').setSelectValue(0);
                                });
                            </c:when>
                            <c:otherwise>
                                var index = $('templateName').selectedIndex;
                                if (confirm('Are you sure you want to change template?')) {
                                    ServerConfigurations.saveServerConfiguration(event, null, function failure() {
                                        $('templateName').setSelectValue(0);
                                    });
                                } else {
                                    $('templateName').setSelectValue(index);
                                }
                            </c:otherwise>
                        </c:choose>
                    </c:set>
                    <th><label for="templateName">Configuration template:</label></th>
                    <td>
                        <forms:select id="templateName" name="templateName" enableFilter="true" onchange="${onchange}">
                            <forms:option value="">-- Select template --</forms:option>
                            <c:forEach var="template" items="${templates}">
                                <forms:option value="${template.name}"
                                              selected="${template.name == templateName}"><c:out
                                        value='${template.name}'/></forms:option>
                            </c:forEach>
                        </forms:select>

                        <div class="grayNote">Choose server configuration template. Parameters of configuration will be
                            changed to parameters from template.
                        </div>
                    </td>
                </tr>
            </table>

            <h2 class="noBorder">Configuration parameters</h2>

            <table class="runnerFormTable">
                <tr class="groupingTitle">
                    <td>Key</td>
                    <td>Value</td>
                </tr>
                <c:forEach var="property" items="${properties}" varStatus="counter">
                    <tr>
                        <td>
                            <c:out value='${property.name}'/>
                            <div class="grayNote">Value will be available in build configuration as <span style="display:block;">%{some_prefix}.${property.name}%</span></div>
                        </td>
                        <td>
                            <input type="hidden" name="key" value="<c:out value='${property.name}'/>">
                            <input type="hidden" name="changed" value="false">
                            <c:choose>
                                <c:when test="${property.type == 'PASSWORD'}">
                                    <input type="password" name="value" value="" placeholder="******" class="textField"
                                           style="width: 370px;"
                                           onchange="ServerConfigurations.changeConfigurationParameter(event);">
                                </c:when>
                                <c:otherwise>
                                    <input type="text" name="value" value="<c:out value='${property.value}'/>"
                                           class="textField" style="width: 370px;"
                                           onchange="ServerConfigurations.changeConfigurationParameter(event);">
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </table>
            <div class="saveButtonsBlock">
                <input id="serverConfigurationSaveButton" type="submit" value="Save"
                       class="btn btn_primary submitButton "
                       onclick="return ServerConfigurations.saveServerConfiguration(event);">
                <input type="button" value="Cancel" class="btn cancel"
                       onclick="BS.openUrl(event, '${pluginUrl}'); return false;">
                <input type="hidden" id="initialName" name="initialName" value="<c:out value='${name}'/>"/>
                <i id="saving" style="display: none; " class="icon-refresh icon-spin progressRing progressRingDefault"
                   title="Please wait..."></i>
            </div>
        </form>
    </bs:refreshable>

</div>
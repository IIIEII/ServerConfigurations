<%@ include file="/include.jsp" %>

<c:set var="pluginUrl">/admin/admin.html?item=${pluginName}</c:set>

<div class="serverConfigurations">

    <bs:messages key="serverConfigurationMessage"/>

    <h2 class="noBorder">Server Configurations</h2>

    <div class="topButtons">
        <a class="btn" href="${pluginUrl}&action=new"><span class="addNew">Create new configuration</span></a>
    </div>
    <div>
        <c:choose>
            <c:when test="${fn:length(configurations) == 0}">
                There are no server configurations.
            </c:when>
            <c:otherwise>
                <l:tableWithHighlighting className="parametersTable" id="configurations">
                    <tr>
                        <th style="width:372px;">Configuration Name</th>
                        <th colspan="3">Template</th>
                    </tr>
                    <c:forEach var="configuration" items="${configurations}">
                        <c:set var="confEditUrl">${pluginUrl}&action=edit&name=<c:out value='${configuration.name}'/></c:set>
                        <c:set var="confDeleteUrl"><c:out value='${controllerPath}'/>?action=delete&name=<c:out value='${configuration.name}'/></c:set>
                        <c:set var="onclick">BS.openUrl(event, '${confEditUrl}'); return false;</c:set>
                        <tr>
                            <td class="name highlight" onclick="${onclick}"><c:out value="${configuration.name}"/></td>
                            <td class="highlight" onclick="${onclick}"><c:out value="${configuration.templateName}"/></td>
                            <td class="actions edit highlight">
                                <a href="${confEditUrl}">Edit</a>
                            </td>
                            <td class="actions edit highlight last">
                                <a href="${confDeleteUrl}" onclick="return confirm('Are you sure you want to delete server configuration?')">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </l:tableWithHighlighting>
            </c:otherwise>
        </c:choose>
    </div>

    <h2 class="noBorder">Templates</h2>

    <div class="topButtons">
        <a class="btn" href="/admin/admin.html?item=${pluginName}&action=newTemplate"><span class="addNew">Create new template</span></a>
    </div>
    <div>
        <c:choose>
            <c:when test="${fn:length(templates) == 0}">
                There are no server configuration templates.
            </c:when>
            <c:otherwise>
                <l:tableWithHighlighting className="parametersTable" id="templates">
                    <tr>
                        <th style="width:372px;">Template Name</th>
                        <th colspan="3">Properties Count</th>
                    </tr>
                    <c:forEach var="template" items="${templates}">
                        <c:set var="templEditUrl">${pluginUrl}&action=editTemplate&name=<c:out value='${template.name}'/></c:set>
                        <c:set var="templDeleteUrl"><c:out value='${controllerPath}'/>?action=deleteTemplate&name=<c:out value='${template.name}'/></c:set>
                        <c:set var="onclick">BS.openUrl(event, '${templEditUrl}'); return false;</c:set>
                        <tr>
                            <td class="name highlight" onclick="${onclick}"><c:out value="${template.name}"/></td>
                            <td class="highlight" onclick="${onclick}"><c:out value='${fn:length(template.properties)}'/></td>
                            <td class="actions edit highlight">
                                <a href="${templEditUrl}">Edit</a>
                            </td>
                            <td class="actions edit highlight last">
                                <a href="${templDeleteUrl}" onclick="return confirm('Are you sure you want to delete server configuration template?')">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </l:tableWithHighlighting>
            </c:otherwise>
        </c:choose>
    </div>

</div>
<%@ include file="/include.jsp"%>

<%--
  ~ Copyright 2000-2015 JetBrains s.r.o.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<c:url value="/admin/editProject.html?projectId=${projectId}&tab=${pluginName}" var="pluginUrl"/>
<c:url value="${controllerPath}" var="controllerPath"/>

<div id="serverConfigurations" class="section noMargin">

    <bs:messages key="serverConfigurationMessage"/>

    <h2 class="noBorder">Server configurations</h2>
    <div class="grayNote">This page contains server configurations defined in the current project. Parameters from server configurations could be used in any build configuration with desired prefix (e.g. %{some_prefix}.domain.name%).</div>
    <div class="topButtons">
        <a class="btn" href="${pluginUrl}&action=new"><span class="addNew">Add new configuration</span></a>
    </div>
    <div>
        <c:choose>
            <c:when test="${fn:length(configurations) == 0}">
                There are no server configurations in this project.
            </c:when>
            <c:otherwise>
                <l:tableWithHighlighting className="parametersTable" id="configurations">
                    <tr>
                        <th>Parameters Prefix</th>
                        <th>Branch Filter</th>
                        <th colspan="3">Configuration Name</th>
                    </tr>
                    <c:forEach var="configuration" items="${configurations}">
                        <c:url value="${pluginUrl}" var="confEditUrl">
                            <c:param name="action" value="edit" />
                            <c:param name="prefix" value="${configuration.prefix}" />
                            <c:param name="branchFilter" value="${configuration.branchFilter}" />
                        </c:url>
                        <c:url value="${controllerPath}" var="confDeleteUrl">
                            <c:param name="action" value="delete" />
                            <c:param name="projectId" value="${projectId}" />
                            <c:param name="prefix" value="${configuration.prefix}" />
                            <c:param name="branchFilter" value="${configuration.branchFilter}" />
                        </c:url>
                        <c:set var="onclick">BS.openUrl(event, '${confEditUrl}'); return false;</c:set>
                        <tr>
                            <td class="name highlight" onclick="${onclick}"><c:out value="${configuration.prefix}"/></td>
                            <td class="highlight" onclick="${onclick}"><c:out value="${configuration.branchFilter}"/></td>
                            <td class="highlight" onclick="${onclick}"><c:out value="${configuration.name}"/></td>
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
</div>
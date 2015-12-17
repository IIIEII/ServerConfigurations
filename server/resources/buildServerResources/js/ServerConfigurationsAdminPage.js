var ServerConfigurations = {
    getFunction: function (buttonName, formName, paramsList, messageComponentName, componentName) {
        return function (event, success, failure) {
            $(buttonName).disabled = 'true';
            BS.Util.show($('saving'));
            BS.ajaxRequest($(formName).action, {
                    parameters: ServerConfigurations.getParams(paramsList),
                    onComplete: function (transport) {
                        if (transport.responseXML) {
                            BS.XMLResponse.processErrors(transport.responseXML, {
                                onProfilerProblemError: function (elem) {
                                    alert(elem.firstChild.nodeValue);
                                }
                            });
                        }
                        $j('.successMessage').remove();
                        $(messageComponentName).refresh();
                        if (transport.status == 500) {
                            $(buttonName).disabled = '';
                            BS.Util.hide($('saving'));
                            if (failure && typeof failure == 'function') {
                                failure(event);
                            }
                        } else {
                            if (success && typeof success == 'function') {
                                success(event);
                            } else {
                                $(componentName).refresh();
                            }
                        }
                    }
                }
            );
            return false;
        }
    },
    getParams: function (list) {
        var names = list.split(",");
        var params = {};
        for (var i = 0; i < names.length; i++) {
            var name = names[i].trim();
            if (name.substr(-2) == "[]") {
                name = name.substr(0, name.length - 2);
                params[name] = $j("[name='" + name + "']").map(function (i, el) {
                    return el.value
                }).toArray();
            } else {
                if ($(name)) {
                    params[name] = $(name).value;
                }
            }
        }
        return params;
    }
};
ServerConfigurations.saveTemplate = ServerConfigurations.getFunction(
    'templateSaveButton',
    'templateForm',
    'name,initialName,key[],type[]',
    'templateMessageComponent',
    'templateComponent'
);
ServerConfigurations.saveServerConfiguration = ServerConfigurations.getFunction(
    'serverConfigurationSaveButton',
    'serverConfigurationForm',
    'initialName,name,templateName,key[],value[],changed[]',
    'serverConfigurationMessageComponent',
    'serverConfigurationComponent'
);
ServerConfigurations.saveProjectServerConfiguration = ServerConfigurations.getFunction(
    'serverConfigurationSaveButton',
    'serverConfigurationForm',
    'initialPrefix,initialBranchFilter,prefix,name,branchFilter',
    'serverConfigurationMessageComponent',
    'serverConfigurationComponent'
);
ServerConfigurations.changeConfigurationParameter = function(event) {
    $j('.successMessage').remove();
    $j(event.target).closest('tr').find('[name=changed]').val('true');
    return false;
};
ServerConfigurations.deleteTemplateParameter = function(event) {
    $j(event.target).closest('tr').remove();
    return false;
};
ServerConfigurations.addTemplateParameter = function(event) {
    $j('<tr>').append(
        $j('<td><input type="text" name="key" value="" class="textField" style="width: 370px;"></td>'),
        $j('<td>').append($j('#typesSelect').clone().removeAttr('id').attr('name', 'type').show()),
        $j('<td class="actions edit last"><a href="#" onclick="if(confirm(\'Are you sure you want to delete template parameter?\')) return ServerConfigurations.deleteTemplateParameter(event);">Delete</a></td>')
    ).insertBefore($j(event.target).closest('tr'));
    return false;
};
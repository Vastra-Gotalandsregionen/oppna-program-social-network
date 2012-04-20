AUI().add('social-config', function(A) {
    console.log('hejsan');

    var Lang = A.Lang,
//        isArray = Lang.isArray,
//        isDate = Lang.isDate,
//        isFunction = Lang.isFunction,
//        isNull = Lang.isNull,
//        isObject = Lang.isObject,
//        isString = Lang.isString,
//        isUndefined = Lang.isUndefined,
        getClassName = A.ClassNameManager.getClassName,
        concat = function() {
            return Array.prototype.slice.call(arguments).join(SPACE);
        },

//        BOUNDING_BOX = 'boundingBox',
//        CONTENT_BOX = 'contentBox',
        CSS_CLASS_EDIT_TRIGGER = 'profile-edit-trigger',
        CSS_CLASS_EDIT_TRIGGER_JOB_TITLE = 'profile-edit-trigger-job-title',
        CSS_CLASS_EDIT_TRIGGER_USER_ABOUT = 'profile-edit-trigger-user-about',
//        CSS_CLASS_EDIT_TRIGGER_DESCRIPTION = 'ifeed-edit-trigger-description',
//        CSS_CLASS_TREE_NODE_TOOLTIP = 'tree-node-tooltip',
//        CSS_CLASS_METADATA_NODE_TOOLTIP = 'metadata-icon-tooltip',
//        DESCRIPTION_NODE = 'descriptionNode',
//        DESCRIPTION_INPUT = 'descriptionInput',
//        EXISTING_FILTERS_TREE_BOUNDING_BOX = 'existingFiltersTreeBoundingBox',
//        EXISTING_FILTERS_TREE_CONTENT_BOX = 'existingFiltersTreeContentBox',
//        META_DATA_FORM = 'metaDataForm',
        JOB_TITLE_CHECK = 'jobTitleCheck',
        JOB_TITLE_FORM = 'jobTitleForm',
//        HEADING_NODE = 'headingNode',
        JOB_TITLE_NODE = 'jobTitleNode',
        JOB_TITLE_INPUT = 'jobTitleInput',
//        HEADING_INPUT = 'headingInput',
//        HREF = 'href',
//        ID = 'id',
        NAME = 'social-config',
        NS = 'social-config',
        PARENT_NODE = 'parentNode',
        PORTLET_NAMESPACE = 'portletNamespace',
        PORTLET_WRAP = 'portletWrap',
        SUBMIT_URL = 'submitUrl',
        USER_ABOUT_CHECK = 'userAboutCheck',
        USER_ABOUT_NODE = 'userAboutNode'
//        USED_FILTERS_TREE_BOUNDING_BOX = 'usedFiltersTreeBoundingBox',
//        USED_FILTERS_TREE_CONTENT_BOX = 'usedFiltersTreeContentBox',
//        METADATA_TOOLTIP_URL =  'metadataTooltipURL'

        ;


    var SocialConfig = A.Component.create(
        {
            ATTRS: {
                jobTitleCheck: {
                    setter: A.one
                },
                jobTitleNode: {
                    setter: A.one
                },
                /*jobTitleInput: {
                    setter: A.one
                },
                jobTitleForm: {
                    setter: A.one
                },*/
                portletWrap: {
                    setter: A.one
                },
                userAboutCheck: {
                    setter: A.one
                },
                userAboutNode: {
                    setter: A.one
                }
                /*,
                submitUrl: {
                    setter: A.one
                }*/
                /*existingFiltersTreeBoundingBox: {
                    setter: A.one
                },
                existingFiltersTreeContentBox: {
                    setter: A.one
                },
                descriptionInput: {
                    setter: A.one
                },
                descriptionNode: {
                    setter: A.one
                },
                headingInput: {
                    setter: A.one
                },
                headingNode: {
                    setter: A.one
                },
                metaDataForm: {
                    setter: A.one
                },
                portletNamespace: {
                    value: ''
                },
                portletWrap: {
                    setter: A.one
                },
                usedFiltersTreeBoundingBox: {
                    setter: A.one
                },
                usedFiltersTreeContentBox: {
                    setter: A.one
                },
                metadataTooltipURL: {
                    value: ''
                }*/
            },
            EXTENDS: A.Component,
            NAME: NAME,
            NS: NS,
//            editInlineTooltip: null,
//            existingFiltersTree: null,
            jobTitleEditable: null,
            userAboutEditable: null,
//            treeNodeTooltip: null,
//            metadataTooltip: null,
//            usedFiltersTree: null,
            prototype: {
                initializer: function(config) {
                    var instance = this;

                    // Init debugger console (if console is activated, console must be added to the module dependency list)
                    //instance._initConsole();
                },

                renderUI: function() {
                    console.log("renderUI");

                    var instance = this;

//                    var contentBox = instance.get(CONTENT_BOX);

                    // Init editable for jobtitle node
                    instance.jobTitleEditable = new A.Editable({
                        node: instance.get(JOB_TITLE_NODE)
                    });

                    instance.userAboutEditable = new A.Editable({
                        node: instance.get(USER_ABOUT_NODE),
                        inputType: 'textBox'
                    })

/*
                    instance.jobTitleEditable.after('save', function(e){
                        var instance = this;

                        var node = instance.jobTitleEditable.get('node');
                        var nodeValue = node.html();
//                        var nodeInput = instance.get(JOB_TITLE_INPUT);
//                        var form = instance.get(JOB_TITLE_FORM);
//
//                        nodeInput.set('value', nodeValue);
//                        form.submit(); //todo ajax istället?
                        A.io.request(instance._originalConfig.submitUrl + '&key=jobTitle&value=' + nodeValue, {
                            method: 'POST',
                            dataType: 'json',
                            on: {
                                success: function (event, id, xhr) {
                                    var res = this.get('responseData');
                                    //todo if res...
                                    var jobTitleCheck = instance.get(JOB_TITLE_CHECK);
                                    jobTitleCheck.setStyle('opacity', 1);
                                    var anim = new A.Anim({
                                        node: '#' + jobTitleCheck.get('id'),
                                        to: {opacity: 0}
                                    });
                                    A.later(5000, instance, function() {
                                        anim.run();
                                    }, [], false);
                                }
                            }
                        })

                    }, instance);
*/
//                    const submitUrl = instance._originalConfig.submitUrl;
                    instance.jobTitleEditable.after('save', function () {
                        submitData(instance, instance.jobTitleEditable, 'jobTitle', JOB_TITLE_CHECK);
                    }, instance);

                    instance.userAboutEditable.after('save', function () {
                        submitData(instance, instance.userAboutEditable, 'userAbout', USER_ABOUT_CHECK);
                    }, instance);

                    function submitData(instance, theEditable, key, confirmElement){
//                        var instance = this;

//                        var jobTitleEditable = instance.jobTitleEditable;
                        var node = theEditable.get('node');
                        var nodeValue = node.html();
//                        var nodeInput = instance.get(JOB_TITLE_INPUT);
//                        var form = instance.get(JOB_TITLE_FORM);
//
//                        nodeInput.set('value', nodeValue);
//                        form.submit(); //todo ajax istället?

//                        var key = 'jobTitle';
                        A.io.request(instance._originalConfig.submitUrl + '&key=' + key + '&value=' + nodeValue, {
                            method: 'POST',
                            dataType: 'json',
                            on: {
                                success: function (event, id, xhr) {
                                    var res = this.get('responseData');
                                    //todo if res...
//                                    var JOB_TITLE_CHECK2 = JOB_TITLE_CHECK;
                                    var confirmNode = instance.get(confirmElement);
                                    confirmNode.setStyle('opacity', 1);
                                    var anim = new A.Anim({
                                        node: '#' + confirmNode.get('id'),
                                        to: {opacity: 0}
                                    });
                                    A.later(5000, instance, function() {
                                        anim.run();
                                    }, [], false);
                                }
                            }
                        })

                    }

                    // Init editable for description node
//                    instance.descriptionEditable = new A.Editable({
//                        inputType: 'textarea',
//                        node: instance.get(DESCRIPTION_NODE)
//                    });

//                    instance.descriptionEditable.after('save', function(e){
//                        var instance = this;
//
//                        var node = instance.descriptionEditable.get('node');
//                        var nodeValue = node.html();
//                        var nodeInput = instance.get(DESCRIPTION_INPUT);
//                        var form = instance.get(META_DATA_FORM);
//
//                        nodeInput.set('value', nodeValue);
//                        form.submit(); //todo ajax istället?
//
//                    }, instance);
                },

                bindUI: function() {
                    var instance = this;

                    // Bind edit triggers
                    var portletWrap = instance.get(PORTLET_WRAP);
                    var editTriggers = portletWrap.all('.' + CSS_CLASS_EDIT_TRIGGER);
                    editTriggers.on('click', instance._onEditTriggersClick, instance);
                },

                /*_initConsole: function() {
                    var instance = this;

                    new A.Console({
                        //height: '250px',
                        newestOnTop: false,
                        style: 'block',
                        visible: true//,
                        //width: '600px'
                    }).render();
                },*/

                _onEditTriggersClick: function(e) {
                    var instance = this;
                    e.halt();

                    var editableNode = false;
                    var currentTarget = e.currentTarget;

                    if(currentTarget.hasClass(CSS_CLASS_EDIT_TRIGGER_JOB_TITLE)) {
                        editableNode = instance.jobTitleEditable.get('node');
                    }
                    else if(currentTarget.hasClass(CSS_CLASS_EDIT_TRIGGER_USER_ABOUT)) {
                        editableNode = instance.userAboutEditable.get('node');
                    }

                    if(editableNode) {
                        editableNode.simulate('click');
                    }
                }

            }
        }
    );

    A.SocialConfig = SocialConfig;

}, 1, {
    requires: [
    'aui-base',
    'aui-editable',
    'aui-io',
    'anim']
});
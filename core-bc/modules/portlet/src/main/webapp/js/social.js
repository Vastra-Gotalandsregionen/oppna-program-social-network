AUI().add('social-config', function(A) {

    var Lang = A.Lang,
        getClassName = A.ClassNameManager.getClassName,
        concat = function() {
            return Array.prototype.slice.call(arguments).join(SPACE);
        },

        CSS_CLASS_EDIT_TRIGGER = 'profile-edit-trigger',
        CSS_CLASS_EDIT_TRIGGER_JOB_TITLE = 'profile-edit-trigger-job-title',
        CSS_CLASS_EDIT_TRIGGER_USER_ABOUT = 'profile-edit-trigger-user-about',
        CSS_CLASS_EDIT_TRIGGER_LANGUAGE = 'profile-edit-trigger-language',
        JOB_TITLE_CHECK = 'jobTitleCheck',
        JOB_TITLE_NODE = 'jobTitleNode',
        LANGUAGE_CHECK = 'languageCheck',
        LANGUAGE_NODE = 'languageNode',
        NAME = 'social-config',
        NS = 'social-config',
        PARENT_NODE = 'parentNode',
        PORTLET_NAMESPACE = 'portletNamespace',
        PORTLET_WRAP = 'portletWrap',
        SUBMIT_URL = 'submitUrl',
        USER_ABOUT_CHECK = 'userAboutCheck',
        USER_ABOUT_NODE = 'userAboutNode'
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
                languageCheck: {
                    setter: A.one
                },
                languageNode: {
                    setter: A.one
                },
                portletWrap: {
                    setter: A.one
                },
                userAboutCheck: {
                    setter: A.one
                },
                userAboutNode: {
                    setter: A.one
                }
            },
            EXTENDS: A.Component,
            NAME: NAME,
            NS: NS,
            jobTitleEditable: null,
            languageEditable: null,
            userAboutEditable: null,
            prototype: {
                initializer: function(config) {
                    var instance = this;

                    // Init debugger console (if console is activated, console must be added to the module dependency list)
                    //instance._initConsole();
                },

                renderUI: function() {
                    var instance = this;

                    // Init editable for jobtitle node
                    instance.jobTitleEditable = new A.Editable({
                        node: instance.get(JOB_TITLE_NODE)
                    });

                    instance.languageEditable = new A.Editable({
                        node: instance.get(LANGUAGE_NODE)
                    });

                    instance.userAboutEditable = new A.Editable({
                        node: instance.get(USER_ABOUT_NODE),
                        inputType: 'textBox'
                    });

                    instance.jobTitleEditable.after('save', function () {
                        submitData(instance, instance.jobTitleEditable, 'jobTitle', JOB_TITLE_CHECK);
                    }, instance);

                    instance.languageEditable.after('save', function () {
                        submitData(instance, instance.languageEditable, 'language', LANGUAGE_CHECK);
                    }, instance);

                    instance.userAboutEditable.after('save', function () {
                        submitData(instance, instance.userAboutEditable, 'userAbout', USER_ABOUT_CHECK);
                    }, instance);

                    function submitData(instance, theEditable, key, confirmElement){
                        var node = theEditable.get('node');
                        var nodeValue = node.html();

                        A.io.request(instance._originalConfig.submitUrl, {
                            method: 'POST',
                            dataType: 'json',
                            data: {'key':key,'value':nodeValue},
                            on: {
                                success: function (event, id, xhr) {
                                    var res = this.get('responseData');
                                    var confirmNode = instance.get(confirmElement);
                                    confirmNode.setStyle('display', 'inline');
                                    var anim = new A.Anim({
                                        node: '#' + confirmNode.get('id'),
                                        to: {opacity: 0}
                                    });
                                    A.later(5000, instance, function() {
                                        confirmNode.setStyle('display', 'none');
                                    }, [], false);
                                }
                            }
                        })
                    }
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
                    else if(currentTarget.hasClass(CSS_CLASS_EDIT_TRIGGER_LANGUAGE)) {
                        editableNode = instance.languageEditable.get('node');
                    }
                    else if(currentTarget.hasClass(CSS_CLASS_EDIT_TRIGGER_USER_ABOUT)) {
                        editableNode = instance.userAboutEditable.get('node');
                    }

                    if(editableNode) {
                        editableNode.simulate('click'); // Doesn't work in IE9. Possibly fixed YUI in 3.3.0?
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
Ext.define('Admin.view.WizardHeader', {
    extend: 'Ext.form.Panel',
    alias: 'widget.wizardHeader',

    requires: [
        'Admin.view.AutosizeTextField'
    ],

    cls: 'admin-wizard-header-container',

    border: false,

    displayNameProperty: 'displayName',
    displayNameConfig: {
        emptyText: 'Display Name',
        enableKeyEvents: true,
        hideLabel: true,
        autoFocus: true
        // TODO: What is max length?
        //maxLength: 255,
        //enforceMaxLength: true
    },

    pathProperty: 'path',
    pathConfig: {
        hidden: false,
        emptyText: 'path/to/',
        hideLabel: true
    },

    nameProperty: 'name',

    nameConfig: {
        hidden: false,
        allowBlank: false,
        emptyText: 'Name',
        enableKeyEvents: true,
        hideLabel: true,
        vtype: 'name',
        stripCharsRe: /[^a-z0-9\-]+/ig
        // TODO: What is max length?
        //maxLength: 255,
        //enforceMaxLength: true
    },


    initComponent: function () {
        var me = this;

        me.appendVtypes();

        var headerData = this.prepareHeaderData(this.data);

        var displayNameValue = headerData[this.displayNameProperty];
        var pathValue = headerData[this.pathProperty];
        var nameValue = headerData[this.nameProperty];

        this.displayNameField = Ext.create('Admin.view.AutosizeTextField', Ext.apply({
            xtype: 'textfield',
            fieldHeight: 40,
            name: this.displayNameProperty,
            value: displayNameValue,
            cls: 'admin-display-name',
            dirtyCls: 'admin-display-name-dirty'
        }, me.displayNameConfig, Admin.view.WizardHeader.prototype.displayNameConfig));

        // add listeners separately so they don't get overridden by config
        this.displayNameField.on({
            afterrender: me.onDisplayNameAfterrender,
            keyup: me.onDisplayNameKey,
            change: me.onDisplayNameChanged,
            scope: me
        });

        this.pathField = Ext.create('Ext.form.field.Display', Ext.apply({
            xtype: 'displayfield',
            cls: 'admin-path',
            dirtyCls: 'admin-path-dirty',
            value: pathValue
        }, me.pathConfig, Admin.view.WizardHeader.prototype.pathConfig));

        this.nameField = Ext.create('Admin.view.AutosizeTextField', Ext.apply({
            xtype: 'textfield',
            fieldHeight: 30,
            cls: 'admin-name',
            dirtyCls: 'admin-name-dirty',
            name: this.nameProperty,
            value: nameValue,
            listeners: {
                change: function (textfield, newValue) {
                    textfield.setValue(textfield.processRawValue(newValue));
                }, scope: this
            }

        }, me.nameConfig, Admin.view.WizardHeader.prototype.nameConfig));

        // set autogenerate flags
        me.autogenerateDisplayName = Ext.isEmpty(displayNameValue) && !me.displayNameField.hidden;

        var generatedNameValue = this.nameField.processRawValue(this.preProcessName(displayNameValue));
        me.autogenerateName = ( Ext.isEmpty(nameValue) || nameValue == generatedNameValue ) && !me.nameField.hidden;

        // add listeners separately so they don't get overridden by config
        this.nameField.on({
            keyup: me.onNameKey,
            change: me.onNameChanged,
            scope: me
        });

        this.items = [
            me.displayNameField
        ];

        if (!me.pathField.hidden && !me.nameField.hidden) {
            this.items.push({
                xtype: 'fieldcontainer',
                hideLabel: true,
                layout: 'hbox',
                items: [
                    me.pathField,
                    me.nameField
                ]
            });
        } else if (!me.pathField.hidden) {
            this.items.push(me.pathField);
        } else if (!me.nameField.hidden) {
            this.items.push(me.nameField);
        }

        this.callParent(arguments);
        this.addEvents('displaynamechange', 'displaynameoverride', 'namechange', 'nameoverride');
    },

    onDisplayNameAfterrender: function (field) {
        if (!field.readOnly && field.autoFocus) {
            field.focus(false, 100);
            // Deselect text, for unknown reason text is always selected when focus is gained
            field.selectText(0, 0);
        }
    },

    onDisplayNameKey: function (field, event, opts) {
        var wasAutoGenerate = this.autogenerateDisplayName;
        var autoGenerate = Ext.isEmpty(field.getValue());
        if (wasAutoGenerate != autoGenerate) {
            this.fireEvent('displaynameoverride', !autoGenerate);
        }
        this.autogenerateDisplayName = autoGenerate;
    },

    onDisplayNameChanged: function (field, newVal, oldVal, opts) {
        if (this.fireEvent('displaynamechange', newVal, oldVal) !== false && this.autogenerateName) {
            var processedValue = this.nameField.processRawValue(this.preProcessName(newVal));
            this.nameField.setValue(processedValue);
        }
    },

    onNameKey: function (field, event, opts) {
        var wasAutoGenerate = this.autogenerateName;
        var autoGenerate = Ext.isEmpty(field.getValue());
        if (wasAutoGenerate != autoGenerate) {
            this.fireEvent('nameoverride', !autoGenerate);
        }
        this.autogenerateName = autoGenerate;
    },

    onNameChanged: function (field, newVal, oldVal, opts) {
        this.fireEvent('namechange', newVal, oldVal);
    },

    appendVtypes: function () {
        Ext.apply(Ext.form.field.VTypes, {
            name: function (val, field) {
                return /^[a-z0-9\-]+$/i.test(val);
            },
            nameText: 'Not a valid name. Can contain digits, letters and "-" only.',
            nameMask: /^[a-z0-9\-]+$/i
        });
        Ext.apply(Ext.form.field.VTypes, {
            qualifiedName: function (val, field) {
                return /^[a-z0-9\-:]+$/i.test(val);
            },
            qualifiedNameText: 'Not a valid qualified name. Can contain digits, letters, ":" and "-" only.',
            qualifiedNameMask: /^[a-z0-9\-:]+$/i
        });
        Ext.apply(Ext.form.field.VTypes, {
            path: function (val, field) {
                return /^[a-z0-9\-\/]+$/i.test(val);
            },
            pathText: 'Not a valid path. Can contain digits, letters, "/" and "-" only.',
            pathMask: /^[a-z0-9\-\/]+$/i
        });
    },

    preProcessName: function (displayName) {
        return !Ext.isEmpty(displayName) ? displayName.replace(/[\s+\./]/ig, '-')
            .replace(/-{2,}/g, '-')
            .replace(/^-|-$/g, '')
            .toLowerCase() : '';
    },

    prepareHeaderData: function (data) {
        return data && data.data || data || {};
    },

    setData: function (data) {
        this.data = data;
        this.getForm().setValues(this.resolveHeaderData(data));
        this.setDisplayName(data[this.displayNameProperty]);
        this.setName(data[this.nameProperty]);
    },

    getData: function () {
        var data = this.getForm().getFieldValues();
        data[this.displayNameProperty] = this.getDisplayName();
        data[this.nameProperty] = this.nameField.getValue();
        return data;
    },

    getDisplayName: function () {
        return this.displayNameField.getValue();
    },

    setDisplayName: function (displayName) {
        this.displayNameField.setValue(displayName);
    },

    setName: function (name) {
        this.nameField.setValue(name);
    }

});
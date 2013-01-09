Ext.define('Admin.controller.contentStudio.GridPanelController', {
    extend: 'Admin.controller.contentStudio.Controller',

    stores: [
        'Admin.store.contentStudio.ContentTypeStore',
        'Admin.store.contentStudio.ContentTypeTreeStore'
    ],

    models: [],

    views: [
        'Admin.view.contentStudio.TreeGridPanel',
        'Admin.view.contentStudio.ContextMenu'
    ],

    init: function () {

        this.control({
            'contentTypeTreeGridPanel grid, treepanel': {
                selectionchange: this.onGridSelectionChange,
                itemcontextmenu: this.showContextMenu,
                itemdblclick: function (btn, evt) {
                    this.showEditContentTypePanel();
                }
            }
        });

    },

    onGridSelectionChange: function (selModel, selected, opts) {
        this.updateDetailPanel(selModel, selected, opts);
        this.enableToolbarButtons(selected.length > 0);
    },


    updateDetailPanel: function (selModel, selected, opts) {
        if (selected.length > 0) {
            this.getDetailPanel().setData(selected);
        }
    },

    enableToolbarButtons: function (enable) {
        var buttons = Ext.ComponentQuery.query('button[action=editContentType], ' +
                                               'button[action=deleteContentType], ' +
                                               'button[action=viewContentType]');

        Ext.Array.each(buttons, function (button) {
            button.setDisabled(!enable);
        });
    },

    showContextMenu: function (view, rec, node, index, e) {
        e.stopEvent();
        this.getContextMenu().showAt(e.getXY());
        return false;
    },


    getContextMenu: function () {
        return Ext.ComponentQuery.query('contentStudioContextMenu')[0];
    }

});
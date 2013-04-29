module admin.ui {

    export class DeleteSpaceWindow {

        private container;
        private data;
        private title:String = "Delete space(s)";
        private deleteHandler = new admin.app.handler.DeleteSpacesHandler();

        private template = '<div class="delete-container">' +
                           '<tpl for=".">' +
                           '<div class="delete-item">' +
                           '<img class="icon" src="{data.iconUrl}"/>' +
                           '<h4>{data.displayName}</h4>' +
                           '<p>{data.type}</p>' +
                           '</div>' +
                           '</tpl>' +
                           '</div>';

        constructor() {
            var deleteCallback = (obj, success, result) => {
                this.container.hide();
                //TODO: Fire event
            };

            var c = new Ext.container.Container();
            c.border = false;
            c.floating = true;
            c.shadow = false;
            c.width = 500;
            c.modal = true;
            c.autoHeight = true;
            c.maxHeight = 600;
            c.cls = 'admin-window';
            c.padding = 20;

            var header = new Ext.Component();
            header.region = 'north';
            header.tpl = '<h2>{title}</h2><tpl if="subtitle != undefined"><p>{subtitle}</p></tpl>';
            header.data = { title: this.title };
            header.margin = '0 0 20 0';

            c.add(header);

            var content = new Ext.Component();
            content.region = 'center';
            content.itemId = 'modalDialog';
            content.cls = 'dialog-info';
            content.border = false;
            content.height = 150;
            content.styleHtmlContent = true;
            content.tpl = this.template;

            c.add(content);

            var buttonRow = new Ext.container.Container();
            buttonRow.layout = {type: 'hbox', pack: 'end'};

            var deleteButton = new Ext.button.Button();
            deleteButton.text = 'Delete';
            deleteButton.margin = '0 0 0 10';
            deleteButton.setHandler(
                (btn, evt) => {
                    this.deleteHandler.doDelete(this.data, deleteCallback);
                }
            );

            buttonRow.add(deleteButton);

            var cancelButton = new Ext.button.Button();
            cancelButton.text = 'Cancel';
            cancelButton.margin = '0 0 0 10';
            cancelButton.setHandler(
                (btn, evt) => {
                    c.hide();
                }
            );

            buttonRow.add(cancelButton);

            c.add(buttonRow);


            this.container = c;
        }

    ;


        setModel(model) {
            this.data = model;
            if (model) {
                var info = this.container.down('#modalDialog');
                if (info) {
                    info.update(model);
                }

            }
        }

        doShow() {
            this.container.show();
        }
    }
}
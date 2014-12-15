module app.create {

    import GetAllContentTypesRequest = api.schema.content.GetAllContentTypesRequest;
    import GetContentTypeByNameRequest = api.schema.content.GetContentTypeByNameRequest;
    import ContentName = api.content.ContentName;
    import Content = api.content.Content;
    import Attachment = api.content.attachment.Attachment;
    import AttachmentName = api.content.attachment.AttachmentName;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import ContentType = api.schema.content.ContentType;
    import FileUploadedEvent = api.ui.uploader.FileUploadedEvent;

    export class NewContentDialog extends api.ui.dialog.ModalDialog {

        private parentContent: api.content.Content;

        private fileUploaderParams: {parent: string};

        private contentDialogTitle: NewContentDialogTitle;

        private recentList: RecentItemsList;
        private contentList: NewContentDialogList;

        private contentListMask: api.ui.mask.LoadMask;
        private recentListMask: api.ui.mask.LoadMask;

        private input: api.ui.text.TextInput;

        private dropzone: api.content.ContentUploader;

        private listItems: NewContentDialogListItem[];

        constructor() {
            this.contentDialogTitle = new NewContentDialogTitle("What do you want to create?", "");

            super({
                title: this.contentDialogTitle
            });

            this.addClass("new-content-dialog");

            var section = new api.dom.SectionEl().setClass("column");
            this.appendChildToContentPanel(section);

            this.input = api.ui.text.TextInput.large("list-filter").setPlaceholder("Search");
            section.appendChild(this.input);

            this.contentList = new app.create.NewContentDialogList();
            section.appendChild(this.contentList);

            var aside = new api.dom.AsideEl("column");
            this.appendChildToContentPanel(aside);

            this.fileUploaderParams = {
                parent: undefined
            };

            this.dropzone = new api.content.ContentUploader({
                params: this.fileUploaderParams,
                name: 'new-content-uploader',
                showButtons: false,
                showResult: false,
                deferred: true  // wait till the window is shown
            });
            aside.appendChild(this.dropzone);
            this.dropzone.onFileUploaded((event: FileUploadedEvent<api.content.Content>) => {
                this.closeAndFireNewMediaEvent(event.getUploadItem());
            });

            var recentTitle = new api.dom.H1El();
            recentTitle.setHtml('Recently Used');
            aside.appendChild(recentTitle);

            this.recentList = new RecentItemsList();
            aside.appendChild(this.recentList);

            this.setCancelAction(new api.ui.Action("Cancel", "esc"));

            api.dom.Body.get().appendChild(this);

            this.contentListMask = new api.ui.mask.LoadMask(this.contentList);
            this.recentListMask = new api.ui.mask.LoadMask(this.recentList);

            this.contentList.appendChild(this.contentListMask);
            this.recentList.appendChild(this.recentListMask);

            this.listItems = [];

            this.input.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.filterList();
            });
            this.input.onKeyUp((event: KeyboardEvent) => {
                if (event.keyCode === 27) {
                    this.getCancelAction().execute();
                }
            });

            this.contentList.onSelected((event: app.create.NewContentDialogItemSelectedEvent) => {
                this.closeAndFireEventFromContentType(event.getItem());
            });

            this.recentList.onSelected((event: app.create.NewContentDialogItemSelectedEvent) => {
                this.closeAndFireEventFromContentType(event.getItem());
            });

            this.getCancelAction().onExecuted(()=> this.close());
        }

        private closeAndFireEventFromContentType(item: NewContentDialogListItem) {
            this.close();
            new NewContentEvent(item.getContentType(), this.parentContent).fire();
        }

        private closeAndFireNewMediaEvent(newMediaContent: Content) {

            this.close();

            new NewMediaEvent(newMediaContent, this.parentContent).fire();

        }

        private filterList() {
            var inputValue = this.input.getValue();

            var filteredItems = this.listItems.filter((item: NewContentDialogListItem) => {
                return (!inputValue || (item.getDisplayName().indexOf(inputValue) != -1) || (item.getName().indexOf(inputValue) != -1));
            });

            this.contentList.setItems(filteredItems);
        }

        private filterByParentContent(items: NewContentDialogListItem[]): NewContentDialogListItem[] {
            var isRootContent: boolean = !this.parentContent;
            var parentContentIsTemplateFolder = this.parentContent && this.parentContent.getType().isTemplateFolder();
            var parentContentIsSite = this.parentContent && this.parentContent.getType().isSite();
            var parentContentIsPageTemplate = this.parentContent && this.parentContent.getType().isPageTemplate();

            return items.filter((item: NewContentDialogListItem) => {
                var contentTypeName = item.getContentType().getContentTypeName();
                if (parentContentIsPageTemplate) {
                    return false; // children not allowed for page-template
                }
                else if (isRootContent && (contentTypeName.isTemplateFolder() || contentTypeName.isPageTemplate())) {
                    return false; // page-template or template-folder not allowed at root level
                }
                else if (contentTypeName.isTemplateFolder() && !parentContentIsSite) {
                    return false; // template-folder only allowed under site
                }
                else if (contentTypeName.isPageTemplate() && !parentContentIsTemplateFolder) {
                    return false; // page-template only allowed under a template-folder
                }
                else if (parentContentIsTemplateFolder && !contentTypeName.isPageTemplate()) {
                    return false; // in a template-folder allow only page-template
                }
                else {
                    return true;
                }

            });
        }

        setParentContent(parent: api.content.Content) {
            this.parentContent = parent;
            this.fileUploaderParams.parent = parent ? parent.getPath().toString() : api.content.ContentPath.ROOT.toString();
        }

        show() {
            if (this.parentContent) {
                this.contentDialogTitle.setPath(this.parentContent.getPath().toString());
            } else {
                this.contentDialogTitle.setPath('');
            }
            super.show();

            if (this.input.getValue()) {
                this.input.selectText();
            }
            this.input.giveFocus();

            this.dropzone.reset();

            // CMS-3711: reload content types each time when dialog is show.
            // It is slow but newly create content types are displayed.
            this.loadContentTypes();
        }

        hide() {
            super.hide();
            this.dropzone.stop();
        }

        private loadContentTypes() {
            this.contentListMask.show();
            this.recentListMask.show();

            var contentTypesRequest = new GetAllContentTypesRequest();

            wemQ.all([contentTypesRequest.sendAndParse()])
                .spread((contentTypes: ContentTypeSummary[]) => {

                    var listItems = this.createListItems(contentTypes);
                    this.listItems = this.filterByParentContent(listItems);

                    if (this.listItems.length > 0) {
                        this.contentList.setItems(this.listItems);
                        this.recentList.setItems(this.listItems);
                    } else {
                        this.contentList.clearItems();
                        this.recentList.clearItems();
                    }


                }).catch((reason: any) => {

                    api.DefaultErrorHandler.handle(reason);

                }).finally(() => {
                    this.filterList();
                    this.contentListMask.hide();
                    this.recentListMask.hide();

                }).done();
        }

        private createListItems(contentTypes: ContentTypeSummary[]): NewContentDialogListItem[] {
            var contentTypesByName: {[name: string]: ContentTypeSummary} = {};
            contentTypes.forEach((contentType: ContentTypeSummary) => {
                contentTypesByName[contentType.getName()] = contentType;
            });

            var items: NewContentDialogListItem[] = [];
            contentTypes.forEach((contentType: ContentTypeSummary) => {
                items.push(NewContentDialogListItem.fromContentType(contentType))
            });

            items.sort(this.compareListItems);
            return items;
        }

        private compareListItems(item1: NewContentDialogListItem, item2: NewContentDialogListItem): number {
            if (item1.getDisplayName().toLowerCase() > item2.getDisplayName().toLowerCase()) {
                return 1;
            } else if (item1.getDisplayName().toLowerCase() < item2.getDisplayName().toLowerCase()) {
                return -1;
            } else if (item1.getName() > item2.getName()) {
                return 1;
            } else if (item1.getName() < item2.getName()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    export class NewContentDialogTitle extends api.ui.dialog.ModalDialogHeader {

        private pathEl: api.dom.PEl;

        constructor(title: string, path: string) {
            super(title);

            this.pathEl = new api.dom.PEl('path');
            this.pathEl.setHtml(path);
            this.appendChild(this.pathEl);
        }

        setPath(path: string) {
            this.pathEl.setHtml(path);
        }
    }

}
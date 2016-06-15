import "../../api.ts";
import {NewContentDialogItemSelectedEvent} from "./NewContentDialogItemSelectedEvent";
import {NewContentDialogListItem} from "./NewContentDialogListItem";

import ContentTypeSummary = api.schema.content.ContentTypeSummary;

export class NewContentDialogList extends api.ui.selector.list.ListBox<NewContentDialogListItem> {

    private selectedListeners: {(event: NewContentDialogItemSelectedEvent):void}[] = [];

    private loadingMask: api.ui.mask.LoadMask;

    constructor(className = 'content-types-list') {
        super(className);

        this.loadingMask = new api.ui.mask.LoadMask(this);
    }

    onSelected(listener: (event: NewContentDialogItemSelectedEvent)=>void) {
        this.selectedListeners.push(listener);
    }

    unSelected(listener: (event: NewContentDialogItemSelectedEvent)=>void) {
        this.selectedListeners = this.selectedListeners.filter((currentListener: (event: NewContentDialogItemSelectedEvent)=>void)=> {
            return currentListener != listener;
        });
    }

    protected notifySelected(listItem: NewContentDialogListItem) {
        this.selectedListeners.forEach((listener: (event: NewContentDialogItemSelectedEvent)=>void) => {
            listener.call(this, new NewContentDialogItemSelectedEvent(listItem));
        });
    }

    createItemView(item: NewContentDialogListItem): api.dom.LiEl {
        var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
        namesAndIconView
            .setIconUrl(item.getIconUrl())
            .setMainName(item.getDisplayName())
            .setSubName(item.getName())
            .setDisplayIconLabel(item.isSite());

        var itemEl = new api.dom.LiEl('content-types-list-item' + (item.isSite() ? ' site' : ''));
        itemEl.getEl().setTabIndex(0);
        itemEl.appendChild(namesAndIconView);
        itemEl.onClicked((event: MouseEvent) => this.notifySelected(item));
        itemEl.onKeyPressed((event: KeyboardEvent) => {
            if (event.keyCode == 13) {
                this.notifySelected(item);
            }
        });
        return itemEl;
    }

    getItemId(item: NewContentDialogListItem): string {
        return item.getName();
    }

    showLoadingMask() {
        this.insertChild(this.loadingMask, 0); //need to insert mask element each time because it gets removed on items set/cleaned
        this.loadingMask.show();
    }

    hideLoadingMask() {
        this.loadingMask.hide();
    }
}

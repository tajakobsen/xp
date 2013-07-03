module api_app_browse{

    export class BrowsePanel extends api_ui.Panel {

        private browseToolbar:api_ui_toolbar.Toolbar;

        private grid:api_ui_grid.TreeGridPanel;

        private browseItemPanel:BrowseItemPanel;

        private filterPanel:any;

        constructor(browseToolbar:api_ui_toolbar.Toolbar, grid:api_ui_grid.TreeGridPanel, browseItemPanel:BrowseItemPanel, filterPanel:any) {
            super("BrowsePanel");

            this.browseToolbar = browseToolbar;
            this.grid = grid;
            this.browseItemPanel = browseItemPanel;
            this.filterPanel = filterPanel;
        }

        init() {
            this.appendChild(this.browseToolbar);
            // TODO: filterPanel.renderTo(this);
            this.grid.create('center', this.getId());
            this.appendChild(this.browseItemPanel);
        }
    }
}

module app.wizard.page.contextwindow.inspect {

    import SiteModel = api.content.site.SiteModel;
    import PartDescriptor = api.content.page.part.PartDescriptor;
    import DescriptorBasedPageComponent = api.content.page.DescriptorBasedPageComponent;
    import GetPartDescriptorByKeyRequest = api.content.page.part.GetPartDescriptorByKeyRequest;
    import PartComponent = api.content.page.part.PartComponent;
    import DescriptorKey = api.content.page.DescriptorKey;
    import PartComponentView = api.liveedit.part.PartComponentView;
    import LiveEditModel = api.liveedit.LiveEditModel;

    export class PartInspectionPanel extends DescriptorBasedPageComponentInspectionPanel<PartComponent, PartDescriptor> {

        private liveEditModel: LiveEditModel;

        constructor() {
            super(<PageComponentInspectionPanelConfig>{
                iconClass: "live-edit-font-icon-part icon-xlarge"
            });
        }

        setModel(liveEditModel: LiveEditModel) {
            this.liveEditModel = liveEditModel;
        }

        setPartComponent(partView: PartComponentView) {

            var component = <PartComponent>partView.getPageComponent();
            if (component.hasDescriptor()) {
                new GetPartDescriptorByKeyRequest(component.getDescriptor()).sendAndParse().then((descriptor: PartDescriptor) => {
                    this.setComponent(component);
                    this.setupComponentForm(component, descriptor);
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
            }
        }
    }
}
module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import ImageDescriptor = api.content.page.image.ImageDescriptor;

    export class ImageInspectionPanel extends PageComponentInspectionPanel<api.content.page.image.ImageComponent, ImageDescriptor> {

        private imageComponent: api.content.page.image.ImageComponent;
        private descriptorComboBox: api.content.page.image.ImageDescriptorComboBox;
        private imageDescriptors: {
            [key: string]: ImageDescriptor;
        };

        constructor(liveFormPanel: app.wizard.LiveFormPanel, siteTemplate: SiteTemplate) {
            super("live-edit-font-icon-image", liveFormPanel, siteTemplate);
            this.initElements();
        }

        private initElements() {
            var descriptorHeader = new api.dom.H6El();
            descriptorHeader.setText("Descriptor:");
            descriptorHeader.addClass("descriptor-header");
            this.appendChild(descriptorHeader);

            var imageDescriptorsRequest = new api.content.page.image.GetImageDescriptorsByModulesRequest(this.getSiteTemplate().getModules());
            var imageDescriptorLoader = new api.content.page.image.ImageDescriptorLoader(imageDescriptorsRequest);
            this.descriptorComboBox = new api.content.page.image.ImageDescriptorComboBox(imageDescriptorLoader);

            var onDescriptorsLoaded = (imageDescriptors:ImageDescriptor[]) => {
                imageDescriptors.forEach((imageDescriptor:ImageDescriptor) => {
                    this.imageDescriptors[imageDescriptor.getKey().toString()] = imageDescriptor;
                })
                this.descriptorComboBox.setDescriptor(this.getLiveFormPanel().getDefaultImageDescriptor().getKey());
                this.descriptorComboBox.removeLoadedListener(onDescriptorsLoaded); // execute only on the first loaded event
            };
            this.descriptorComboBox.addLoadedListener(onDescriptorsLoaded);

            this.descriptorComboBox.addOptionSelectedListener((option: api.ui.combobox.Option<ImageDescriptor>) => {
                if (this.imageComponent) {
                    var selectedDescriptor = option.displayValue.getKey();
                    this.imageComponent.setDescriptor(selectedDescriptor);
                }
                });
            this.appendChild(this.descriptorComboBox);
        }

        getDescriptor(key: api.content.page.DescriptorKey): ImageDescriptor {
            return this.imageDescriptors[key.toString()];
        }
        
        setImageComponent(component: api.content.page.image.ImageComponent) {
            this.setComponent(component);
            this.imageComponent = component;

            var descriptorKey = component.getDescriptor();
            if (descriptorKey) {
                this.descriptorComboBox.setDescriptor(descriptorKey);
                var imageDescriptorOption: api.ui.combobox.Option<ImageDescriptor> = this.descriptorComboBox.getSelectedData()[0];
                var imageDescriptor = imageDescriptorOption.displayValue;
                this.setupComponentForm(component, imageDescriptor);
            }
        }

    }
}
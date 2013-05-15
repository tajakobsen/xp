module API.content.schema.content.form{

    export class Input extends FormItem {

        private label:string;

        private immutable:bool;

        private occurrences:Occurrences;

        private indexed:bool;

        private customText:string;

        private validationRegex:string;

        private helpText:string;

        constructor(json) {

            super(json.name);
            this.label = json.label;
            this.immutable = json.immutable;
            this.occurrences = new Occurrences(json.occurrences);
            this.indexed = json.indexed;
            this.customText = json.customText;
            this.validationRegex = json.validationRegexp;
            this.helpText = json.helpText;
        }

        getLabel():string {
            return this.label;
        }

        isImmutable():bool {
            return this.immutable;
        }

        getOccurrences():Occurrences{
            return this.occurrences;
        }

        isIndexed():bool{
            return this.indexed;
        }

        getCustomText():string{
            return this.customText;
        }

        getValidationRegex():string{
            return this.validationRegex;
        }

        getHelpText():string{
            return this.helpText;
        }
    }
}

/*
 {
 "Input": {
 "name": "myColor",
 "label": null,
 "immutable": false,
 "occurrences": {
 "minimum": 0,
 "maximum": 1
 },
 "indexed": false,
 "customText": null,
 "validationRegexp": null,
 "helpText": null,
 "type": {
 "name": "Color",
 "builtIn": true
 }
 }
 }
 */
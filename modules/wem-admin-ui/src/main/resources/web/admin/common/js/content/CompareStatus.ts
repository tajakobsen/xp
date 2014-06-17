module api.content {

    export enum Status {
        NEW,
        NEWER,
        OLDER,
        CONFLICT,
        DELETED,
        EQUAL
    }

    export class CompareStatus {

        status: Status;

        constructor(status: Status) {
            this.status = status;
        }

        static fromJson(json: CompareStatusJson): CompareStatus {

            var status: Status = <Status>CompareStatus[json.status];

            return new CompareStatus(status);
        }
    }
}
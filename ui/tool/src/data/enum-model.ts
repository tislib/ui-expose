import { EnumKind } from './enum-kind';
import { EnumMemberModel } from './enum-member-model';

export interface EnumModel {
    kind: EnumKind;
    members: EnumMemberModel;
}



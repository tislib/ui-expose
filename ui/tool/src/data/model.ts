import { EnumMember } from 'ts-morph';
import { BeanModel } from './bean-model';
import { ServiceModel } from './service-model';

export interface Model {
    beans: BeanModel[];
    enums: EnumMember[];
    services: ServiceModel[];
}
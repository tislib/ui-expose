import { MethodModel } from './method-model';

export interface ServiceModel {
    name: string;
    group: string;
    methods: MethodModel[];
}
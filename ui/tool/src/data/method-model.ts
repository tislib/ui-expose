import { MethodArgument } from './method-argument';

export interface MethodModel {
    name: string;
    returnType: string;
    arguments: MethodArgument[];
}
import { Type } from './type';
export interface MethodInfo {
    name: string;
    returnType: Type;
    arguments: {
        [key: string]: string;
    };
}

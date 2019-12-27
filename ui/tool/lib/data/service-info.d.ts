import { MethodInfo } from './method-info';
export interface ServiceInfo {
    name: string;
    group: string;
    methods: MethodInfo[];
}

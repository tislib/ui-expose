import { MethodDeclarationStructure, OptionalKind, ParameterDeclarationStructure } from 'ts-morph';
import { MethodModel } from '../data/method-model';
import { BeanGenerator } from './bean-generator';

export class MethodGenerator {
    private beanGenerator: BeanGenerator = new BeanGenerator();
    
    public makeMethodDeclaration(serviceInfo: string, method: MethodModel): OptionalKind<MethodDeclarationStructure> {
        const parameters: OptionalKind<ParameterDeclarationStructure>[] = [];
        
        let argumentsText = '';
        for (let index in method.arguments) {
            parameters.push({
                name: method.arguments[index].name,
                type: method.arguments[index].type
            });
            if (argumentsText) {
                argumentsText += ',';
            }
            argumentsText = `${argumentsText}{value: ${method.arguments[index].name}, type: '${method.arguments[index].type}'}`;
        }
        
        return {
            name: method.name,
            returnType: `Observable<${this.beanGenerator.resolveType(method.returnType)}>`,
            parameters: parameters,
            statements: writer => {
                writer.write(`return this.backend.invoke({
                  serviceName: ${serviceInfo}.SERVICE_NAME,
                  methodName: '${method.name}',
                  arguments: [${argumentsText}],
                  returnType: '${method.returnType}'
                });`);
            }
        }
    }
}
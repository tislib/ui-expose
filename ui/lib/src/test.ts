function classDecorator<T extends { new(...args: any[]): {} }>(constructor: T) {
    return class extends constructor {
        newProperty = 'new property';
        hello = 'override';
    }
}

function PluginDecorator(name: string) {
    return (ctor: Function) => {
        ctor['property1'] = name;
        console.log('Plugin found: ' + name);
    }
}


@PluginDecorator('test-param-1')
abstract class Greeter {
    static property1: string;
    
    abstract test1(arg1: string, arg0: string): string;
    abstract test1(arg2: string, arg1: string, arg0: string): string;
    
    abstract test2(arg2: string, arg1: string, arg0: string): string;
}

console.log(Greeter.property1);
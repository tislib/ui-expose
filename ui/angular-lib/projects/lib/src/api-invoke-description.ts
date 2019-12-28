export interface ApiInvokeDescription<T> {
  groupName?: string,
  serviceName: string,
  methodName: string,
  arguments: {
    type: string,
    value: any
  }[],
  returnType: string
}

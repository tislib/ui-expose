package net.tislib.uiexpose.lib;

import java.util.List;
import java.util.Set;
import net.tislib.uiexpose.lib.data.ServiceInfo;
import net.tislib.uiexpose.lib.exporer.ServiceExplorer;
import net.tislib.uiexpose.lib.processor.ServiceProcessor;
import net.tislib.uiexpose.lib.processor.ServiceProcessorImpl;
import net.tislib.uiexpose.lib.publisher.OutputPublisher;

public class Main {

    public static void main(String... args) {
        ServiceExplorer serviceExplorer = new ServiceExplorer(args[0]);

        Set<Class<?>> exposedServices = serviceExplorer.findExposedServices();

        ServiceProcessor serviceProcessor = new ServiceProcessorImpl();

        Set<ServiceInfo> serviceInfoList = serviceProcessor.process(exposedServices);

        OutputPublisher outputPublisher = new OutputPublisher();
        outputPublisher.publish(serviceInfoList, System.out);
    }

}

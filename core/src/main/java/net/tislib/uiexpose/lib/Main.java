package net.tislib.uiexpose.lib;

import java.util.Set;
import net.tislib.uiexpose.lib.data.ServiceInfo;
import net.tislib.uiexpose.lib.exporer.BuildPathServiceExplorer;
import net.tislib.uiexpose.lib.processor.ServiceProcessor;
import net.tislib.uiexpose.lib.processor.ServiceProcessorImpl;
import net.tislib.uiexpose.lib.publisher.OutputPublisher;

public class Main {

    public static void main(String... args) {
        BuildPathServiceExplorer serviceExplorer = new BuildPathServiceExplorer(args[0]);

        serviceExplorer.loadExposedServices();
        Set<Class<?>> exposedServices = serviceExplorer.getExposedServices();

        ServiceProcessor serviceProcessor = new ServiceProcessorImpl();

        Set<ServiceInfo> serviceInfoList = serviceProcessor.process(exposedServices);

        OutputPublisher outputPublisher = new OutputPublisher();
        outputPublisher.publish(serviceInfoList, System.out);
    }

}

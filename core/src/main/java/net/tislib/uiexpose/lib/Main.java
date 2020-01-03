package net.tislib.uiexpose.lib;

import java.util.Set;
import net.tislib.uiexpose.lib.data.Model;
import net.tislib.uiexpose.lib.exporer.BuildPathServiceExplorer;
import net.tislib.uiexpose.lib.processor.BeanProcessor;
import net.tislib.uiexpose.lib.processor.Jackson2Configuration;
import net.tislib.uiexpose.lib.processor.JacksonBeanProcessor;
import net.tislib.uiexpose.lib.processor.ServiceProcessor;
import net.tislib.uiexpose.lib.processor.ServiceProcessorImpl;
import net.tislib.uiexpose.lib.publisher.ModelPublisher;

public class Main {

    public static void main(String... args) {
        BuildPathServiceExplorer serviceExplorer = new BuildPathServiceExplorer(args[0]);

        serviceExplorer.loadExposedServices();
        Set<Class<?>> exposedServices = serviceExplorer.getExposedServices();

        BeanProcessor beanProcessor = new JacksonBeanProcessor(new Jackson2Configuration());

        ServiceProcessor serviceProcessor = new ServiceProcessorImpl(beanProcessor);

        Model model = serviceProcessor.process(exposedServices);

        ModelPublisher outputPublisher = new ModelPublisher();
        outputPublisher.publish(model, System.out);
    }

}

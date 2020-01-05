package net.tislib.uiexpose.demo1;

import java.util.HashSet;
import java.util.Set;
import net.tislib.uiexpose.lib.data.Model;
import net.tislib.uiexpose.lib.processor.BeanProcessor;
import net.tislib.uiexpose.lib.processor.Jackson2Configuration;
import net.tislib.uiexpose.lib.processor.JacksonBeanProcessor;
import net.tislib.uiexpose.lib.processor.ServiceProcessor;
import net.tislib.uiexpose.lib.processor.ServiceProcessorImpl;
import net.tislib.uiexpose.lib.publisher.ModelPublisher;

public class Test2 {

    public static void main(String... args) {

        BeanProcessor beanProcessor = new JacksonBeanProcessor(new Jackson2Configuration());

        ServiceProcessor serviceProcessor = new ServiceProcessorImpl(beanProcessor);

        Set<Class<?>> services = new HashSet<>();
        services.add(TestService1.class);
        Model model = serviceProcessor.process(services);

        ModelPublisher outputPublisher = new ModelPublisher();

        outputPublisher.publish(model, System.out);
    }

}

package io.opentracing.contrib.ejb.example;

import com.lightstep.tracer.jre.JRETracer;

import com.lightstep.tracer.shared.Propagator;
import io.opentracing.Tracer;
//import io.opentracing.contrib.tracerresolver.TracerResolver;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.net.MalformedURLException;
import java.util.logging.Logger;

/**
 * This is not part of the "business" of this example, but can be used as an example of how to register a Tracer
 * with the GlobalTracer. On some platforms, like Wildfly Swarm, this is done automatically for you.
 *
 * @author Juraci Paixão Kröhling
 */
@Startup
@Singleton
public class TracerInitializer
{
    private static final Logger log = Logger.getLogger(TracerInitializer.class.getName());

    @PostConstruct
    public void init()
    {
        if (GlobalTracer.isRegistered())
        {
            log.info("A Tracer is already registered at the GlobalTracer. Skipping resolution via TraceResolver.");
            return;
        }

/*
        Tracer tracer = TracerResolver.resolveTracer();

        if (null == tracer) {
            log.info("Could not get a valid OpenTracing Tracer from the classpath. Skipping.");
            return;
        }


        log.info(String.format("Registering %s as the OpenTracing Tracer", tracer.getClass().getName()));
        GlobalTracer.register(tracer);
*/

        try
        {
            Tracer tracer = new JRETracer(
                    new com.lightstep.tracer.shared.Options.OptionsBuilder()
                            .withComponentName("exampleEJBApp")
                            .withAccessToken("developer")
                            .withCollectorHost("localhost")
                            .withCollectorPort(8360)
                            .withCollectorProtocol("http")
                            .build());
            log.info(String.format("Registering %s as the OpenTracing Tracer", tracer.getClass().getName()));
            GlobalTracer.register(tracer);
        }catch(MalformedURLException mal)
        {
            System.out.println(mal.getMessage());
            mal.printStackTrace();
        }

    }
}

package org.apache.logging.log4j.core.net.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LogEventListener;
import org.apache.logging.log4j.core.appender.mom.JmsManager;
import org.apache.logging.log4j.core.net.JndiManager;

public class JmsServer extends LogEventListener implements MessageListener, LifeCycle2 {
   private final AtomicReference<LifeCycle.State> state;
   private final JmsManager jmsManager;
   private MessageConsumer messageConsumer;

   public JmsServer(String var1, String var2, String var3, String var4) {
      super();
      this.state = new AtomicReference(LifeCycle.State.INITIALIZED);
      String var5 = JmsServer.class.getName() + '@' + JmsServer.class.hashCode();
      JndiManager var6 = JndiManager.getDefaultManager(var5);
      this.jmsManager = JmsManager.getJmsManager(var5, var6, var1, var2, var3, var4);
   }

   public LifeCycle.State getState() {
      return (LifeCycle.State)this.state.get();
   }

   public void onMessage(Message var1) {
      try {
         if (var1 instanceof ObjectMessage) {
            Serializable var2 = ((ObjectMessage)var1).getObject();
            if (var2 instanceof LogEvent) {
               this.log((LogEvent)var2);
            } else {
               LOGGER.warn("Expected ObjectMessage to contain LogEvent. Got type {} instead.", var2.getClass());
            }
         } else {
            LOGGER.warn("Received message of type {} and JMSType {} which cannot be handled.", var1.getClass(), var1.getJMSType());
         }
      } catch (JMSException var3) {
         LOGGER.catching(var3);
      }

   }

   public void initialize() {
   }

   public void start() {
      if (this.state.compareAndSet(LifeCycle.State.INITIALIZED, LifeCycle.State.STARTING)) {
         try {
            this.messageConsumer = this.jmsManager.createMessageConsumer();
            this.messageConsumer.setMessageListener(this);
         } catch (JMSException var2) {
            throw new LoggingException(var2);
         }
      }

   }

   public void stop() {
      this.stop(0L, AbstractLifeCycle.DEFAULT_STOP_TIMEUNIT);
   }

   public boolean stop(long var1, TimeUnit var3) {
      boolean var4 = true;

      try {
         this.messageConsumer.close();
      } catch (JMSException var6) {
         LOGGER.debug("Exception closing {}", this.messageConsumer, var6);
         var4 = false;
      }

      return var4 && this.jmsManager.stop(var1, var3);
   }

   public boolean isStarted() {
      return this.state.get() == LifeCycle.State.STARTED;
   }

   public boolean isStopped() {
      return this.state.get() == LifeCycle.State.STOPPED;
   }

   public void run() throws IOException {
      this.start();
      System.out.println("Type \"exit\" to quit.");
      BufferedReader var1 = new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));

      String var2;
      do {
         var2 = var1.readLine();
      } while(var2 != null && !var2.equalsIgnoreCase("exit"));

      System.out.println("Exiting. Kill the application if it does not exit due to daemon threads.");
      this.stop();
   }
}

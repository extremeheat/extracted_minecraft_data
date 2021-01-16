package org.apache.logging.log4j.core.appender.mom;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.net.JndiManager;
import org.apache.logging.log4j.status.StatusLogger;

public class JmsManager extends AbstractManager {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final JmsManager.JmsManagerFactory FACTORY = new JmsManager.JmsManagerFactory();
   private final JndiManager jndiManager;
   private final Connection connection;
   private final Session session;
   private final Destination destination;

   private JmsManager(String var1, JndiManager var2, String var3, String var4, String var5, String var6) throws NamingException, JMSException {
      super((LoggerContext)null, var1);
      this.jndiManager = var2;
      ConnectionFactory var7 = (ConnectionFactory)this.jndiManager.lookup(var3);
      if (var5 != null && var6 != null) {
         this.connection = var7.createConnection(var5, var6);
      } else {
         this.connection = var7.createConnection();
      }

      this.session = this.connection.createSession(false, 1);
      this.destination = (Destination)this.jndiManager.lookup(var4);
      this.connection.start();
   }

   public static JmsManager getJmsManager(String var0, JndiManager var1, String var2, String var3, String var4, String var5) {
      JmsManager.JmsConfiguration var6 = new JmsManager.JmsConfiguration(var1, var2, var3, var4, var5);
      return (JmsManager)getManager(var0, FACTORY, var6);
   }

   public MessageConsumer createMessageConsumer() throws JMSException {
      return this.session.createConsumer(this.destination);
   }

   public MessageProducer createMessageProducer() throws JMSException {
      return this.session.createProducer(this.destination);
   }

   public Message createMessage(Serializable var1) throws JMSException {
      return (Message)(var1 instanceof String ? this.session.createTextMessage((String)var1) : this.session.createObjectMessage(var1));
   }

   protected boolean releaseSub(long var1, TimeUnit var3) {
      boolean var4 = true;

      try {
         this.session.close();
      } catch (JMSException var7) {
         var4 = false;
      }

      try {
         this.connection.close();
      } catch (JMSException var6) {
         var4 = false;
      }

      return var4 && this.jndiManager.stop(var1, var3);
   }

   // $FF: synthetic method
   JmsManager(String var1, JndiManager var2, String var3, String var4, String var5, String var6, Object var7) throws NamingException, JMSException {
      this(var1, var2, var3, var4, var5, var6);
   }

   private static class JmsManagerFactory implements ManagerFactory<JmsManager, JmsManager.JmsConfiguration> {
      private JmsManagerFactory() {
         super();
      }

      public JmsManager createManager(String var1, JmsManager.JmsConfiguration var2) {
         try {
            return new JmsManager(var1, var2.jndiManager, var2.connectionFactoryName, var2.destinationName, var2.username, var2.password);
         } catch (Exception var4) {
            JmsManager.LOGGER.error((String)"Error creating JmsManager using ConnectionFactory [{}] and Destination [{}].", (Object)var2.connectionFactoryName, var2.destinationName, var4);
            return null;
         }
      }

      // $FF: synthetic method
      JmsManagerFactory(Object var1) {
         this();
      }
   }

   private static class JmsConfiguration {
      private final JndiManager jndiManager;
      private final String connectionFactoryName;
      private final String destinationName;
      private final String username;
      private final String password;

      private JmsConfiguration(JndiManager var1, String var2, String var3, String var4, String var5) {
         super();
         this.jndiManager = var1;
         this.connectionFactoryName = var2;
         this.destinationName = var3;
         this.username = var4;
         this.password = var5;
      }

      // $FF: synthetic method
      JmsConfiguration(JndiManager var1, String var2, String var3, String var4, String var5, Object var6) {
         this(var1, var2, var3, var4, var5);
      }
   }
}

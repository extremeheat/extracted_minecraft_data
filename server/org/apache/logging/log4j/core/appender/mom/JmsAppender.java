package org.apache.logging.log4j.core.appender.mom;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.SerializedLayout;
import org.apache.logging.log4j.core.net.JndiManager;

@Plugin(
   name = "JMS",
   category = "Core",
   elementType = "appender",
   printObject = true
)
@PluginAliases({"JMSQueue", "JMSTopic"})
public class JmsAppender extends AbstractAppender {
   private final JmsManager manager;
   private final MessageProducer producer;

   protected JmsAppender(String var1, Filter var2, Layout<? extends Serializable> var3, boolean var4, JmsManager var5) throws JMSException {
      super(var1, var2, var3, var4);
      this.manager = var5;
      this.producer = this.manager.createMessageProducer();
   }

   public void append(LogEvent var1) {
      try {
         Message var2 = this.manager.createMessage(this.getLayout().toSerializable(var1));
         var2.setJMSTimestamp(var1.getTimeMillis());
         this.producer.send(var2);
      } catch (JMSException var3) {
         throw new AppenderLoggingException(var3);
      }
   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      boolean var4 = super.stop(var1, var3, false);
      var4 &= this.manager.stop(var1, var3);
      this.setStopped();
      return var4;
   }

   @PluginBuilderFactory
   public static JmsAppender.Builder newBuilder() {
      return new JmsAppender.Builder();
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<JmsAppender> {
      @PluginBuilderAttribute
      @Required(
         message = "A name for the JmsAppender must be specified"
      )
      private String name;
      @PluginBuilderAttribute
      private String factoryName;
      @PluginBuilderAttribute
      private String providerUrl;
      @PluginBuilderAttribute
      private String urlPkgPrefixes;
      @PluginBuilderAttribute
      private String securityPrincipalName;
      @PluginBuilderAttribute(
         sensitive = true
      )
      private String securityCredentials;
      @PluginBuilderAttribute
      @Required(
         message = "A javax.jms.ConnectionFactory JNDI name must be specified"
      )
      private String factoryBindingName;
      @PluginBuilderAttribute
      @PluginAliases({"queueBindingName", "topicBindingName"})
      @Required(
         message = "A javax.jms.Destination JNDI name must be specified"
      )
      private String destinationBindingName;
      @PluginBuilderAttribute
      private String username;
      @PluginBuilderAttribute(
         sensitive = true
      )
      private String password;
      @PluginElement("Layout")
      private Layout<? extends Serializable> layout;
      @PluginElement("Filter")
      private Filter filter;
      @PluginBuilderAttribute
      private boolean ignoreExceptions;
      private JmsManager jmsManager;

      private Builder() {
         super();
         this.layout = SerializedLayout.createLayout();
         this.ignoreExceptions = true;
      }

      public JmsAppender.Builder setName(String var1) {
         this.name = var1;
         return this;
      }

      public JmsAppender.Builder setFactoryName(String var1) {
         this.factoryName = var1;
         return this;
      }

      public JmsAppender.Builder setProviderUrl(String var1) {
         this.providerUrl = var1;
         return this;
      }

      public JmsAppender.Builder setUrlPkgPrefixes(String var1) {
         this.urlPkgPrefixes = var1;
         return this;
      }

      public JmsAppender.Builder setSecurityPrincipalName(String var1) {
         this.securityPrincipalName = var1;
         return this;
      }

      public JmsAppender.Builder setSecurityCredentials(String var1) {
         this.securityCredentials = var1;
         return this;
      }

      public JmsAppender.Builder setFactoryBindingName(String var1) {
         this.factoryBindingName = var1;
         return this;
      }

      public JmsAppender.Builder setDestinationBindingName(String var1) {
         this.destinationBindingName = var1;
         return this;
      }

      public JmsAppender.Builder setUsername(String var1) {
         this.username = var1;
         return this;
      }

      public JmsAppender.Builder setPassword(String var1) {
         this.password = var1;
         return this;
      }

      public JmsAppender.Builder setLayout(Layout<? extends Serializable> var1) {
         this.layout = var1;
         return this;
      }

      public JmsAppender.Builder setFilter(Filter var1) {
         this.filter = var1;
         return this;
      }

      public JmsAppender.Builder setJmsManager(JmsManager var1) {
         this.jmsManager = var1;
         return this;
      }

      public JmsAppender.Builder setIgnoreExceptions(boolean var1) {
         this.ignoreExceptions = var1;
         return this;
      }

      public JmsAppender build() {
         JmsManager var1 = this.jmsManager;
         if (var1 == null) {
            JndiManager var2 = JndiManager.getJndiManager(this.factoryName, this.providerUrl, this.urlPkgPrefixes, this.securityPrincipalName, this.securityCredentials, (Properties)null);
            var1 = JmsManager.getJmsManager(this.name, var2, this.factoryBindingName, this.destinationBindingName, this.username, this.password);
         }

         try {
            return new JmsAppender(this.name, this.filter, this.layout, this.ignoreExceptions, var1);
         } catch (JMSException var3) {
            JmsAppender.LOGGER.error((String)"Error creating JmsAppender [{}].", (Object)this.name, (Object)var3);
            return null;
         }
      }

      // $FF: synthetic method
      Builder(Object var1) {
         this();
      }
   }
}

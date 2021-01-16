package org.apache.logging.log4j.core.net;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.util.JndiCloser;

public class JndiManager extends AbstractManager {
   private static final JndiManager.JndiManagerFactory FACTORY = new JndiManager.JndiManagerFactory();
   private final Context context;

   private JndiManager(String var1, Context var2) {
      super((LoggerContext)null, var1);
      this.context = var2;
   }

   public static JndiManager getDefaultManager() {
      return (JndiManager)getManager(JndiManager.class.getName(), FACTORY, (Object)null);
   }

   public static JndiManager getDefaultManager(String var0) {
      return (JndiManager)getManager(var0, FACTORY, (Object)null);
   }

   public static JndiManager getJndiManager(String var0, String var1, String var2, String var3, String var4, Properties var5) {
      String var6 = JndiManager.class.getName() + '@' + JndiManager.class.hashCode();
      if (var0 == null) {
         return (JndiManager)getManager(var6, FACTORY, (Object)null);
      } else {
         Properties var7 = new Properties();
         var7.setProperty("java.naming.factory.initial", var0);
         if (var1 != null) {
            var7.setProperty("java.naming.provider.url", var1);
         } else {
            LOGGER.warn((String)"The JNDI InitialContextFactory class name [{}] was provided, but there was no associated provider URL. This is likely to cause problems.", (Object)var0);
         }

         if (var2 != null) {
            var7.setProperty("java.naming.factory.url.pkgs", var2);
         }

         if (var3 != null) {
            var7.setProperty("java.naming.security.principal", var3);
            if (var4 != null) {
               var7.setProperty("java.naming.security.credentials", var4);
            } else {
               LOGGER.warn((String)"A security principal [{}] was provided, but with no corresponding security credentials.", (Object)var3);
            }
         }

         if (var5 != null) {
            var7.putAll(var5);
         }

         return (JndiManager)getManager(var6, FACTORY, var7);
      }
   }

   protected boolean releaseSub(long var1, TimeUnit var3) {
      return JndiCloser.closeSilently(this.context);
   }

   public <T> T lookup(String var1) throws NamingException {
      return this.context.lookup(var1);
   }

   // $FF: synthetic method
   JndiManager(String var1, Context var2, Object var3) {
      this(var1, var2);
   }

   private static class JndiManagerFactory implements ManagerFactory<JndiManager, Properties> {
      private JndiManagerFactory() {
         super();
      }

      public JndiManager createManager(String var1, Properties var2) {
         try {
            return new JndiManager(var1, new InitialContext(var2));
         } catch (NamingException var4) {
            JndiManager.LOGGER.error((String)"Error creating JNDI InitialContext.", (Throwable)var4);
            return null;
         }
      }

      // $FF: synthetic method
      JndiManagerFactory(Object var1) {
         this();
      }
   }
}

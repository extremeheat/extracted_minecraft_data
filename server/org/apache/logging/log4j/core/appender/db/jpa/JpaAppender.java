package org.apache.logging.log4j.core.appender.db.jpa;

import java.lang.reflect.Constructor;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "JPA",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class JpaAppender extends AbstractDatabaseAppender<JpaDatabaseManager> {
   private final String description = this.getName() + "{ manager=" + this.getManager() + " }";

   private JpaAppender(String var1, Filter var2, boolean var3, JpaDatabaseManager var4) {
      super(var1, var2, var3, var4);
   }

   public String toString() {
      return this.description;
   }

   @PluginFactory
   public static JpaAppender createAppender(@PluginAttribute("name") String var0, @PluginAttribute("ignoreExceptions") String var1, @PluginElement("Filter") Filter var2, @PluginAttribute("bufferSize") String var3, @PluginAttribute("entityClassName") String var4, @PluginAttribute("persistenceUnitName") String var5) {
      if (!Strings.isEmpty(var4) && !Strings.isEmpty(var5)) {
         int var6 = AbstractAppender.parseInt(var3, 0);
         boolean var7 = Booleans.parseBoolean(var1, true);

         try {
            Class var8 = LoaderUtil.loadClass(var4).asSubclass(AbstractLogEventWrapperEntity.class);

            try {
               var8.getConstructor();
            } catch (NoSuchMethodException var12) {
               LOGGER.error((String)"Entity class [{}] does not have a no-arg constructor. The JPA provider will reject it.", (Object)var4);
               return null;
            }

            Constructor var9 = var8.getConstructor(LogEvent.class);
            String var10 = "jpaManager{ description=" + var0 + ", bufferSize=" + var6 + ", persistenceUnitName=" + var5 + ", entityClass=" + var8.getName() + '}';
            JpaDatabaseManager var11 = JpaDatabaseManager.getJPADatabaseManager(var10, var6, var8, var9, var5);
            return var11 == null ? null : new JpaAppender(var0, var2, var7, var11);
         } catch (ClassNotFoundException var13) {
            LOGGER.error((String)"Could not load entity class [{}].", (Object)var4, (Object)var13);
            return null;
         } catch (NoSuchMethodException var14) {
            LOGGER.error((String)"Entity class [{}] does not have a constructor with a single argument of type LogEvent.", (Object)var4);
            return null;
         } catch (ClassCastException var15) {
            LOGGER.error((String)"Entity class [{}] does not extend AbstractLogEventWrapperEntity.", (Object)var4);
            return null;
         }
      } else {
         LOGGER.error("Attributes entityClassName and persistenceUnitName are required for JPA Appender.");
         return null;
      }
   }
}

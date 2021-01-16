package org.apache.logging.log4j.core.jmx;

import java.util.Objects;
import javax.management.ObjectName;
import org.apache.logging.log4j.core.selector.ContextSelector;

public class ContextSelectorAdmin implements ContextSelectorAdminMBean {
   private final ObjectName objectName;
   private final ContextSelector selector;

   public ContextSelectorAdmin(String var1, ContextSelector var2) {
      super();
      this.selector = (ContextSelector)Objects.requireNonNull(var2, "ContextSelector");

      try {
         String var3 = String.format("org.apache.logging.log4j2:type=%s,component=ContextSelector", Server.escape(var1));
         this.objectName = new ObjectName(var3);
      } catch (Exception var4) {
         throw new IllegalStateException(var4);
      }
   }

   public ObjectName getObjectName() {
      return this.objectName;
   }

   public String getImplementationClassName() {
      return this.selector.getClass().getName();
   }
}

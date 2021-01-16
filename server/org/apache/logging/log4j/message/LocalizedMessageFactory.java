package org.apache.logging.log4j.message;

import java.util.ResourceBundle;

public class LocalizedMessageFactory extends AbstractMessageFactory {
   private static final long serialVersionUID = -1996295808703146741L;
   private final transient ResourceBundle resourceBundle;
   private final String baseName;

   public LocalizedMessageFactory(ResourceBundle var1) {
      super();
      this.resourceBundle = var1;
      this.baseName = null;
   }

   public LocalizedMessageFactory(String var1) {
      super();
      this.resourceBundle = null;
      this.baseName = var1;
   }

   public String getBaseName() {
      return this.baseName;
   }

   public ResourceBundle getResourceBundle() {
      return this.resourceBundle;
   }

   public Message newMessage(String var1) {
      return this.resourceBundle == null ? new LocalizedMessage(this.baseName, var1) : new LocalizedMessage(this.resourceBundle, var1);
   }

   public Message newMessage(String var1, Object... var2) {
      return this.resourceBundle == null ? new LocalizedMessage(this.baseName, var1, var2) : new LocalizedMessage(this.resourceBundle, var1, var2);
   }
}

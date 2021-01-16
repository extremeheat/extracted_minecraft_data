package org.apache.logging.log4j.core.net.ssl;

import org.apache.logging.log4j.status.StatusLogger;

public class StoreConfiguration<T> {
   protected static final StatusLogger LOGGER = StatusLogger.getLogger();
   private String location;
   private String password;

   public StoreConfiguration(String var1, String var2) {
      super();
      this.location = var1;
      this.password = var2;
   }

   public String getLocation() {
      return this.location;
   }

   public void setLocation(String var1) {
      this.location = var1;
   }

   public String getPassword() {
      return this.password;
   }

   public char[] getPasswordAsCharArray() {
      return this.password == null ? null : this.password.toCharArray();
   }

   public void setPassword(String var1) {
      this.password = var1;
   }

   protected T load() throws StoreConfigurationException {
      return null;
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      int var3 = 31 * var2 + (this.location == null ? 0 : this.location.hashCode());
      var3 = 31 * var3 + (this.password == null ? 0 : this.password.hashCode());
      return var3;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!(var1 instanceof StoreConfiguration)) {
         return false;
      } else {
         StoreConfiguration var2 = (StoreConfiguration)var1;
         if (this.location == null) {
            if (var2.location != null) {
               return false;
            }
         } else if (!this.location.equals(var2.location)) {
            return false;
         }

         if (this.password == null) {
            if (var2.password != null) {
               return false;
            }
         } else if (!this.password.equals(var2.password)) {
            return false;
         }

         return true;
      }
   }
}

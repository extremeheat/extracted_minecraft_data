package com.mojang.authlib;

import java.util.StringJoiner;

public interface Environment {
   String getAuthHost();

   String getAccountsHost();

   String getSessionHost();

   String getServicesHost();

   String getName();

   String asString();

   static Environment create(final String var0, final String var1, final String var2, final String var3, final String var4) {
      return new Environment() {
         public String getAuthHost() {
            return var0;
         }

         public String getAccountsHost() {
            return var1;
         }

         public String getSessionHost() {
            return var2;
         }

         public String getServicesHost() {
            return var3;
         }

         public String getName() {
            return var4;
         }

         public String asString() {
            return (new StringJoiner(", ", "", "")).add("authHost='" + this.getAuthHost() + "'").add("accountsHost='" + this.getAccountsHost() + "'").add("sessionHost='" + this.getSessionHost() + "'").add("servicesHost='" + this.getServicesHost() + "'").add("name='" + this.getName() + "'").toString();
         }
      };
   }
}

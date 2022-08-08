package com.mojang.realmsclient.client;

import java.net.Proxy;
import javax.annotation.Nullable;

public class RealmsClientConfig {
   @Nullable
   private static Proxy proxy;

   public RealmsClientConfig() {
      super();
   }

   @Nullable
   public static Proxy getProxy() {
      return proxy;
   }

   public static void setProxy(Proxy var0) {
      if (proxy == null) {
         proxy = var0;
      }

   }
}

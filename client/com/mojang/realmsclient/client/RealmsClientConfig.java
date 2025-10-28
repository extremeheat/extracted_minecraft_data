package com.mojang.realmsclient.client;

import java.net.Proxy;
import org.jspecify.annotations.Nullable;

public class RealmsClientConfig {
   private static @Nullable Proxy proxy;

   public RealmsClientConfig() {
      super();
   }

   public static @Nullable Proxy getProxy() {
      return proxy;
   }

   public static void setProxy(Proxy var0) {
      if (proxy == null) {
         proxy = var0;
      }

   }
}

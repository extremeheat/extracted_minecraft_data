package com.mojang.realmsclient.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class UploadTokenCache {
   private static final Long2ObjectMap<String> TOKEN_CACHE = new Long2ObjectOpenHashMap();

   public UploadTokenCache() {
      super();
   }

   public static String get(long var0) {
      return (String)TOKEN_CACHE.get(var0);
   }

   public static void invalidate(long var0) {
      TOKEN_CACHE.remove(var0);
   }

   public static void put(long var0, String var2) {
      TOKEN_CACHE.put(var0, var2);
   }
}

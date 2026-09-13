package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Map;

public enum Session$Type {
   LEGACY("legacy"),
   MOJANG("mojang");

   private static final Map field_152425_c = Maps.newHashMap();
   private final String field_152426_d;

   private Session$Type(String var3) {
      this.field_152426_d = var3;
   }

   public static Session$Type func_152421_a(String var0) {
      return (Session$Type)field_152425_c.get(var0.toLowerCase());
   }

   static {
      for(Session$Type var3 : values()) {
         field_152425_c.put(var3.field_152426_d, var3);
      }
   }
}

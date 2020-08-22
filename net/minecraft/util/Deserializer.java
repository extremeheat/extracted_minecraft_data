package net.minecraft.util;

import com.mojang.datafixers.Dynamic;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Deserializer {
   Logger LOGGER = LogManager.getLogger();

   Object deserialize(Dynamic var1);

   static Object deserialize(Dynamic var0, Registry var1, String var2, Object var3) {
      Deserializer var4 = (Deserializer)var1.get(new ResourceLocation(var0.get(var2).asString("")));
      Object var5;
      if (var4 != null) {
         var5 = var4.deserialize(var0);
      } else {
         LOGGER.error("Unknown type {}, replacing with {}", var0.get(var2).asString(""), var3);
         var5 = var3;
      }

      return var5;
   }
}

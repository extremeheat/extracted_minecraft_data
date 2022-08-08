package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class OminousBannerRenameFix extends ItemStackTagFix {
   public OminousBannerRenameFix(Schema var1) {
      super(var1, "OminousBannerRenameFix", (var0) -> {
         return var0.equals("minecraft:white_banner");
      });
   }

   protected <T> Dynamic<T> fixItemStackTag(Dynamic<T> var1) {
      Optional var2 = var1.get("display").result();
      if (var2.isPresent()) {
         Dynamic var3 = (Dynamic)var2.get();
         Optional var4 = var3.get("Name").asString().result();
         if (var4.isPresent()) {
            String var5 = (String)var4.get();
            var5 = var5.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
            var3 = var3.set("Name", var3.createString(var5));
         }

         return var1.set("display", var3);
      } else {
         return var1;
      }
   }
}

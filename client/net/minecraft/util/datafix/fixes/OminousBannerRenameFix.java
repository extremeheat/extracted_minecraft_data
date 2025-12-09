package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.Util;

public class OminousBannerRenameFix extends ItemStackTagFix {
   public OminousBannerRenameFix(Schema var1) {
      super(var1, "OminousBannerRenameFix", (var0) -> var0.equals("minecraft:white_banner"));
   }

   private <T> Dynamic<T> fixItemStackTag(Dynamic<T> var1) {
      return var1.update("display", (var0) -> var0.update("Name", (var0x) -> {
            Optional var1 = var0x.asString().result();
            return var1.isPresent() ? var0x.createString(((String)var1.get()).replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"")) : var0x;
         }));
   }

   protected Typed<?> fixItemStackTag(Typed<?> var1) {
      return Util.writeAndReadTypedOrThrow(var1, var1.getType(), this::fixItemStackTag);
   }
}

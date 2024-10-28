package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Streams;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class HorseBodyArmorItemFix extends NamedEntityWriteReadFix {
   private final String previousBodyArmorTag;
   private final boolean clearArmorItems;

   public HorseBodyArmorItemFix(Schema var1, String var2, String var3, boolean var4) {
      super(var1, true, "Horse armor fix for " + var2, References.ENTITY, var2);
      this.previousBodyArmorTag = var3;
      this.clearArmorItems = var4;
   }

   protected <T> Dynamic<T> fix(Dynamic<T> var1) {
      Optional var2 = var1.get(this.previousBodyArmorTag).result();
      if (var2.isPresent()) {
         Dynamic var3 = (Dynamic)var2.get();
         Dynamic var4 = var1.remove(this.previousBodyArmorTag);
         if (this.clearArmorItems) {
            var4 = var4.update("ArmorItems", (var0) -> {
               return var0.createList(Streams.mapWithIndex(var0.asStream(), (var0x, var1) -> {
                  return var1 == 2L ? var0x.emptyMap() : var0x;
               }));
            });
            var4 = var4.update("ArmorDropChances", (var0) -> {
               return var0.createList(Streams.mapWithIndex(var0.asStream(), (var0x, var1) -> {
                  return var1 == 2L ? var0x.createFloat(0.085F) : var0x;
               }));
            });
         }

         var4 = var4.set("body_armor_item", var3);
         var4 = var4.set("body_armor_drop_chance", var1.createFloat(2.0F));
         return var4;
      } else {
         return var1;
      }
   }
}

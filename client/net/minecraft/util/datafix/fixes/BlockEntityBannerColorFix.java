package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;

public class BlockEntityBannerColorFix extends NamedEntityFix {
   public BlockEntityBannerColorFix(Schema var1, boolean var2) {
      super(var1, var2, "BlockEntityBannerColorFix", References.BLOCK_ENTITY, "minecraft:banner");
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      var1 = var1.update("Base", (var0) -> {
         return var0.createInt(15 - var0.asInt(0));
      });
      var1 = var1.update("Patterns", (var0) -> {
         Optional var10000 = var0.asStreamOpt().map((var0x) -> {
            return var0x.map((var0) -> {
               return var0.update("Color", (var0x) -> {
                  return var0x.createInt(15 - var0x.asInt(0));
               });
            });
         });
         var0.getClass();
         return (Dynamic)DataFixUtils.orElse(var10000.map(var0::createList), var0);
      });
      return var1;
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixTag);
   }
}

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class BlockEntityBannerColor extends NamedEntityFix {
   public BlockEntityBannerColor(Schema var1, boolean var2) {
      super(var1, var2, "BlockEntityBannerColorFix", TypeReferences.field_211294_j, "minecraft:banner");
   }

   public Dynamic<?> func_209643_a(Dynamic<?> var1) {
      var1 = var1.update("Base", (var0) -> {
         return var0.createInt(15 - var0.getNumberValue(0).intValue());
      });
      var1 = var1.update("Patterns", (var0) -> {
         Optional var10000 = var0.getStream().map((var0x) -> {
            return var0x.map((var0) -> {
               return var0.update("Color", (var0x) -> {
                  return var0x.createInt(15 - var0x.getNumberValue(0).intValue());
               });
            });
         });
         var0.getClass();
         return (Dynamic)DataFixUtils.orElse(var10000.map(var0::createList), var0);
      });
      return var1;
   }

   protected Typed<?> func_207419_a(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::func_209643_a);
   }
}

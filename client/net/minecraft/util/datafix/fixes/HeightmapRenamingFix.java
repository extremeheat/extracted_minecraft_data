package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class HeightmapRenamingFix extends DataFix {
   public HeightmapRenamingFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(TypeReferences.field_211287_c);
      OpticFinder var2 = var1.findField("Level");
      return this.fixTypeEverywhereTyped("HeightmapRenamingFix", var1, (var2x) -> {
         return var2x.updateTyped(var2, (var1) -> {
            return var1.update(DSL.remainderFinder(), this::func_209766_a);
         });
      });
   }

   private Dynamic<?> func_209766_a(Dynamic<?> var1) {
      Optional var2 = var1.get("Heightmaps");
      if (!var2.isPresent()) {
         return var1;
      } else {
         Dynamic var3 = (Dynamic)var2.get();
         Optional var4 = var3.get("LIQUID");
         if (var4.isPresent()) {
            var3 = var3.remove("LIQUID");
            var3 = var3.set("WORLD_SURFACE_WG", (Dynamic)var4.get());
         }

         Optional var5 = var3.get("SOLID");
         if (var5.isPresent()) {
            var3 = var3.remove("SOLID");
            var3 = var3.set("OCEAN_FLOOR_WG", (Dynamic)var5.get());
            var3 = var3.set("OCEAN_FLOOR", (Dynamic)var5.get());
         }

         Optional var6 = var3.get("LIGHT");
         if (var6.isPresent()) {
            var3 = var3.remove("LIGHT");
            var3 = var3.set("LIGHT_BLOCKING", (Dynamic)var6.get());
         }

         Optional var7 = var3.get("RAIN");
         if (var7.isPresent()) {
            var3 = var3.remove("RAIN");
            var3 = var3.set("MOTION_BLOCKING", (Dynamic)var7.get());
            var3 = var3.set("MOTION_BLOCKING_NO_LEAVES", (Dynamic)var7.get());
         }

         return var1.set("Heightmaps", var3);
      }
   }
}

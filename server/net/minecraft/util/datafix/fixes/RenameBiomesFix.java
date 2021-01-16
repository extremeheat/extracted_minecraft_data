package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class RenameBiomesFix extends DataFix {
   private final String name;
   private final Map<String, String> biomes;

   public RenameBiomesFix(Schema var1, boolean var2, String var3, Map<String, String> var4) {
      super(var1, var2);
      this.biomes = var4;
      this.name = var3;
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = DSL.named(References.BIOME.typeName(), NamespacedSchema.namespacedString());
      if (!Objects.equals(var1, this.getInputSchema().getType(References.BIOME))) {
         throw new IllegalStateException("Biome type is not what was expected.");
      } else {
         return this.fixTypeEverywhere(this.name, var1, (var1x) -> {
            return (var1) -> {
               return var1.mapSecond((var1x) -> {
                  return (String)this.biomes.getOrDefault(var1x, var1x);
               });
            };
         });
      }
   }
}

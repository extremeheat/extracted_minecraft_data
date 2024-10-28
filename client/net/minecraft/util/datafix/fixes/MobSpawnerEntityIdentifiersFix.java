package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.stream.Stream;

public class MobSpawnerEntityIdentifiersFix extends DataFix {
   public MobSpawnerEntityIdentifiersFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   private Dynamic<?> fix(Dynamic<?> var1) {
      if (!"MobSpawner".equals(var1.get("id").asString(""))) {
         return var1;
      } else {
         Optional var2 = var1.get("EntityId").asString().result();
         if (var2.isPresent()) {
            Dynamic var3 = (Dynamic)DataFixUtils.orElse(var1.get("SpawnData").result(), var1.emptyMap());
            var3 = var3.set("id", var3.createString(((String)var2.get()).isEmpty() ? "Pig" : (String)var2.get()));
            var1 = var1.set("SpawnData", var3);
            var1 = var1.remove("EntityId");
         }

         Optional var4 = var1.get("SpawnPotentials").asStreamOpt().result();
         if (var4.isPresent()) {
            var1 = var1.set("SpawnPotentials", var1.createList(((Stream)var4.get()).map((var0) -> {
               Optional var1 = var0.get("Type").asString().result();
               if (var1.isPresent()) {
                  Dynamic var2 = ((Dynamic)DataFixUtils.orElse(var0.get("Properties").result(), var0.emptyMap())).set("id", var0.createString((String)var1.get()));
                  return var0.set("Entity", var2).remove("Type").remove("Properties");
               } else {
                  return var0;
               }
            })));
         }

         return var1;
      }
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getOutputSchema().getType(References.UNTAGGED_SPAWNER);
      return this.fixTypeEverywhereTyped("MobSpawnerEntityIdentifiersFix", this.getInputSchema().getType(References.UNTAGGED_SPAWNER), var1, (var2) -> {
         Dynamic var3 = (Dynamic)var2.get(DSL.remainderFinder());
         var3 = var3.set("id", var3.createString("MobSpawner"));
         DataResult var4 = var1.readTyped(this.fix(var3));
         return var4.result().isEmpty() ? var2 : (Typed)((Pair)var4.result().get()).getFirst();
      });
   }
}

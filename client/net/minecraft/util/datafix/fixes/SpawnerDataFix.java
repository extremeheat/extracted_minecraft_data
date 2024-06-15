package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.List;

public class SpawnerDataFix extends DataFix {
   public SpawnerDataFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.UNTAGGED_SPAWNER);
      Type var2 = this.getOutputSchema().getType(References.UNTAGGED_SPAWNER);
      OpticFinder var3 = var1.findField("SpawnData");
      Type var4 = var2.findField("SpawnData").type();
      OpticFinder var5 = var1.findField("SpawnPotentials");
      Type var6 = var2.findField("SpawnPotentials").type();
      return this.fixTypeEverywhereTyped(
         "Fix mob spawner data structure",
         var1,
         var2,
         var5x -> var5x.updateTyped(var3, var4, var2xx -> this.wrapEntityToSpawnData(var4, var2xx))
               .updateTyped(var5, var6, var2xx -> this.wrapSpawnPotentialsToWeightedEntries(var6, var2xx))
      );
   }

   private <T> Typed<T> wrapEntityToSpawnData(Type<T> var1, Typed<?> var2) {
      DynamicOps var3 = var2.getOps();
      return new Typed(var1, var3, Pair.of(var2.getValue(), new Dynamic(var3)));
   }

   private <T> Typed<T> wrapSpawnPotentialsToWeightedEntries(Type<T> var1, Typed<?> var2) {
      DynamicOps var3 = var2.getOps();
      List var4 = (List)var2.getValue();
      List var5 = var4.stream().map(var1x -> {
         Pair var2x = (Pair)var1x;
         int var3x = ((Dynamic)var2x.getSecond()).get("Weight").asNumber().result().orElse(1).intValue();
         Dynamic var4x = new Dynamic(var3);
         var4x = var4x.set("weight", var4x.createInt(var3x));
         Dynamic var5x = ((Dynamic)var2x.getSecond()).remove("Weight").remove("Entity");
         return Pair.of(Pair.of(var2x.getFirst(), var5x), var4x);
      }).toList();
      return new Typed(var1, var3, var5);
   }
}

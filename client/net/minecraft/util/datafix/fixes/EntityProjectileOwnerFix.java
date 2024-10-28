package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Arrays;
import java.util.function.Function;

public class EntityProjectileOwnerFix extends DataFix {
   public EntityProjectileOwnerFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Schema var1 = this.getInputSchema();
      return this.fixTypeEverywhereTyped("EntityProjectileOwner", var1.getType(References.ENTITY), this::updateProjectiles);
   }

   private Typed<?> updateProjectiles(Typed<?> var1) {
      var1 = this.updateEntity(var1, "minecraft:egg", this::updateOwnerThrowable);
      var1 = this.updateEntity(var1, "minecraft:ender_pearl", this::updateOwnerThrowable);
      var1 = this.updateEntity(var1, "minecraft:experience_bottle", this::updateOwnerThrowable);
      var1 = this.updateEntity(var1, "minecraft:snowball", this::updateOwnerThrowable);
      var1 = this.updateEntity(var1, "minecraft:potion", this::updateOwnerThrowable);
      var1 = this.updateEntity(var1, "minecraft:potion", this::updateItemPotion);
      var1 = this.updateEntity(var1, "minecraft:llama_spit", this::updateOwnerLlamaSpit);
      var1 = this.updateEntity(var1, "minecraft:arrow", this::updateOwnerArrow);
      var1 = this.updateEntity(var1, "minecraft:spectral_arrow", this::updateOwnerArrow);
      var1 = this.updateEntity(var1, "minecraft:trident", this::updateOwnerArrow);
      return var1;
   }

   private Dynamic<?> updateOwnerArrow(Dynamic<?> var1) {
      long var2 = var1.get("OwnerUUIDMost").asLong(0L);
      long var4 = var1.get("OwnerUUIDLeast").asLong(0L);
      return this.setUUID(var1, var2, var4).remove("OwnerUUIDMost").remove("OwnerUUIDLeast");
   }

   private Dynamic<?> updateOwnerLlamaSpit(Dynamic<?> var1) {
      OptionalDynamic var2 = var1.get("Owner");
      long var3 = var2.get("OwnerUUIDMost").asLong(0L);
      long var5 = var2.get("OwnerUUIDLeast").asLong(0L);
      return this.setUUID(var1, var3, var5).remove("Owner");
   }

   private Dynamic<?> updateItemPotion(Dynamic<?> var1) {
      OptionalDynamic var2 = var1.get("Potion");
      return var1.set("Item", var2.orElseEmptyMap()).remove("Potion");
   }

   private Dynamic<?> updateOwnerThrowable(Dynamic<?> var1) {
      String var2 = "owner";
      OptionalDynamic var3 = var1.get("owner");
      long var4 = var3.get("M").asLong(0L);
      long var6 = var3.get("L").asLong(0L);
      return this.setUUID(var1, var4, var6).remove("owner");
   }

   private Dynamic<?> setUUID(Dynamic<?> var1, long var2, long var4) {
      String var6 = "OwnerUUID";
      return var2 != 0L && var4 != 0L ? var1.set("OwnerUUID", var1.createIntList(Arrays.stream(createUUIDArray(var2, var4)))) : var1;
   }

   private static int[] createUUIDArray(long var0, long var2) {
      return new int[]{(int)(var0 >> 32), (int)var0, (int)(var2 >> 32), (int)var2};
   }

   private Typed<?> updateEntity(Typed<?> var1, String var2, Function<Dynamic<?>, Dynamic<?>> var3) {
      Type var4 = this.getInputSchema().getChoiceType(References.ENTITY, var2);
      Type var5 = this.getOutputSchema().getChoiceType(References.ENTITY, var2);
      return var1.updateTyped(DSL.namedChoice(var2, var4), var5, (var1x) -> {
         return var1x.update(DSL.remainderFinder(), var3);
      });
   }
}

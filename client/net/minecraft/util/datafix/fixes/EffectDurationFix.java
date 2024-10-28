package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class EffectDurationFix extends DataFix {
   private static final Set<String> ITEM_TYPES = Set.of("minecraft:potion", "minecraft:splash_potion", "minecraft:lingering_potion", "minecraft:tipped_arrow");

   public EffectDurationFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Schema var1 = this.getInputSchema();
      Type var2 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var3 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder var4 = var2.findField("tag");
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("EffectDurationEntity", var1.getType(References.ENTITY), (var1x) -> {
         return var1x.update(DSL.remainderFinder(), this::updateEntity);
      }), new TypeRewriteRule[]{this.fixTypeEverywhereTyped("EffectDurationPlayer", var1.getType(References.PLAYER), (var1x) -> {
         return var1x.update(DSL.remainderFinder(), this::updateEntity);
      }), this.fixTypeEverywhereTyped("EffectDurationItem", var2, (var3x) -> {
         Optional var4x = var3x.getOptional(var3);
         Set var10001 = ITEM_TYPES;
         Objects.requireNonNull(var10001);
         if (var4x.filter(var10001::contains).isPresent()) {
            Optional var5 = var3x.getOptionalTyped(var4);
            if (var5.isPresent()) {
               Dynamic var6 = (Dynamic)((Typed)var5.get()).get(DSL.remainderFinder());
               Typed var7 = ((Typed)var5.get()).set(DSL.remainderFinder(), var6.update("CustomPotionEffects", this::fix));
               return var3x.set(var4, var7);
            }
         }

         return var3x;
      })});
   }

   private Dynamic<?> fixEffect(Dynamic<?> var1) {
      return var1.update("FactorCalculationData", (var1x) -> {
         int var2 = var1x.get("effect_changed_timestamp").asInt(-1);
         var1x = var1x.remove("effect_changed_timestamp");
         int var3 = var1.get("Duration").asInt(-1);
         int var4 = var2 - var3;
         return var1x.set("ticks_active", var1x.createInt(var4));
      });
   }

   private Dynamic<?> fix(Dynamic<?> var1) {
      return var1.createList(var1.asStream().map(this::fixEffect));
   }

   private Dynamic<?> updateEntity(Dynamic<?> var1) {
      var1 = var1.update("Effects", this::fix);
      var1 = var1.update("ActiveEffects", this::fix);
      var1 = var1.update("CustomPotionEffects", this::fix);
      return var1;
   }
}

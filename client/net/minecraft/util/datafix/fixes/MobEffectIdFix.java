package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class MobEffectIdFix extends DataFix {
   private static final Int2ObjectMap<String> ID_MAP = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(), (var0) -> {
      var0.put(1, "minecraft:speed");
      var0.put(2, "minecraft:slowness");
      var0.put(3, "minecraft:haste");
      var0.put(4, "minecraft:mining_fatigue");
      var0.put(5, "minecraft:strength");
      var0.put(6, "minecraft:instant_health");
      var0.put(7, "minecraft:instant_damage");
      var0.put(8, "minecraft:jump_boost");
      var0.put(9, "minecraft:nausea");
      var0.put(10, "minecraft:regeneration");
      var0.put(11, "minecraft:resistance");
      var0.put(12, "minecraft:fire_resistance");
      var0.put(13, "minecraft:water_breathing");
      var0.put(14, "minecraft:invisibility");
      var0.put(15, "minecraft:blindness");
      var0.put(16, "minecraft:night_vision");
      var0.put(17, "minecraft:hunger");
      var0.put(18, "minecraft:weakness");
      var0.put(19, "minecraft:poison");
      var0.put(20, "minecraft:wither");
      var0.put(21, "minecraft:health_boost");
      var0.put(22, "minecraft:absorption");
      var0.put(23, "minecraft:saturation");
      var0.put(24, "minecraft:glowing");
      var0.put(25, "minecraft:levitation");
      var0.put(26, "minecraft:luck");
      var0.put(27, "minecraft:unluck");
      var0.put(28, "minecraft:slow_falling");
      var0.put(29, "minecraft:conduit_power");
      var0.put(30, "minecraft:dolphins_grace");
      var0.put(31, "minecraft:bad_omen");
      var0.put(32, "minecraft:hero_of_the_village");
      var0.put(33, "minecraft:darkness");
   });
   private static final Set<String> MOB_EFFECT_INSTANCE_CARRIER_ITEMS = Set.of("minecraft:potion", "minecraft:splash_potion", "minecraft:lingering_potion", "minecraft:tipped_arrow");

   public MobEffectIdFix(Schema var1) {
      super(var1, false);
   }

   private static <T> Optional<Dynamic<T>> getAndConvertMobEffectId(Dynamic<T> var0, String var1) {
      Optional var10000 = var0.get(var1).asNumber().result().map((var0x) -> {
         return (String)ID_MAP.get(var0x.intValue());
      });
      Objects.requireNonNull(var0);
      return var10000.map(var0::createString);
   }

   private static <T> Dynamic<T> updateMobEffectIdField(Dynamic<T> var0, String var1, Dynamic<T> var2, String var3) {
      Optional var4 = getAndConvertMobEffectId(var0, var1);
      return var2.replaceField(var1, var3, var4);
   }

   private static <T> Dynamic<T> updateMobEffectIdField(Dynamic<T> var0, String var1, String var2) {
      return updateMobEffectIdField(var0, var1, var0, var2);
   }

   private static <T> Dynamic<T> updateMobEffectInstance(Dynamic<T> var0) {
      var0 = updateMobEffectIdField(var0, "Id", "id");
      var0 = var0.renameField("Ambient", "ambient");
      var0 = var0.renameField("Amplifier", "amplifier");
      var0 = var0.renameField("Duration", "duration");
      var0 = var0.renameField("ShowParticles", "show_particles");
      var0 = var0.renameField("ShowIcon", "show_icon");
      Optional var1 = var0.get("HiddenEffect").result().map(MobEffectIdFix::updateMobEffectInstance);
      return var0.replaceField("HiddenEffect", "hidden_effect", var1);
   }

   private static <T> Dynamic<T> updateMobEffectInstanceList(Dynamic<T> var0, String var1, String var2) {
      Optional var3 = var0.get(var1).asStreamOpt().result().map((var1x) -> {
         return var0.createList(var1x.map(MobEffectIdFix::updateMobEffectInstance));
      });
      return var0.replaceField(var1, var2, var3);
   }

   private static <T> Dynamic<T> updateSuspiciousStewEntry(Dynamic<T> var0, Dynamic<T> var1) {
      var1 = updateMobEffectIdField(var0, "EffectId", var1, "id");
      Optional var2 = var0.get("EffectDuration").result();
      return var1.replaceField("EffectDuration", "duration", var2);
   }

   private static <T> Dynamic<T> updateSuspiciousStewEntry(Dynamic<T> var0) {
      return updateSuspiciousStewEntry(var0, var0);
   }

   private Typed<?> updateNamedChoice(Typed<?> var1, DSL.TypeReference var2, String var3, Function<Dynamic<?>, Dynamic<?>> var4) {
      Type var5 = this.getInputSchema().getChoiceType(var2, var3);
      Type var6 = this.getOutputSchema().getChoiceType(var2, var3);
      return var1.updateTyped(DSL.namedChoice(var3, var5), var6, (var1x) -> {
         return var1x.update(DSL.remainderFinder(), var4);
      });
   }

   private TypeRewriteRule blockEntityFixer() {
      Type var1 = this.getInputSchema().getType(References.BLOCK_ENTITY);
      return this.fixTypeEverywhereTyped("BlockEntityMobEffectIdFix", var1, (var1x) -> {
         var1x = this.updateNamedChoice(var1x, References.BLOCK_ENTITY, "minecraft:beacon", (var0) -> {
            var0 = updateMobEffectIdField(var0, "Primary", "primary_effect");
            return updateMobEffectIdField(var0, "Secondary", "secondary_effect");
         });
         return var1x;
      });
   }

   private static <T> Dynamic<T> fixMooshroomTag(Dynamic<T> var0) {
      Dynamic var1 = var0.emptyMap();
      Dynamic var2 = updateSuspiciousStewEntry(var0, var1);
      if (!var2.equals(var1)) {
         var0 = var0.set("stew_effects", var0.createList(Stream.of(var2)));
      }

      return var0.remove("EffectId").remove("EffectDuration");
   }

   private static <T> Dynamic<T> fixArrowTag(Dynamic<T> var0) {
      return updateMobEffectInstanceList(var0, "CustomPotionEffects", "custom_potion_effects");
   }

   private static <T> Dynamic<T> fixAreaEffectCloudTag(Dynamic<T> var0) {
      return updateMobEffectInstanceList(var0, "Effects", "effects");
   }

   private static Dynamic<?> updateLivingEntityTag(Dynamic<?> var0) {
      return updateMobEffectInstanceList(var0, "ActiveEffects", "active_effects");
   }

   private TypeRewriteRule entityFixer() {
      Type var1 = this.getInputSchema().getType(References.ENTITY);
      return this.fixTypeEverywhereTyped("EntityMobEffectIdFix", var1, (var1x) -> {
         var1x = this.updateNamedChoice(var1x, References.ENTITY, "minecraft:mooshroom", MobEffectIdFix::fixMooshroomTag);
         var1x = this.updateNamedChoice(var1x, References.ENTITY, "minecraft:arrow", MobEffectIdFix::fixArrowTag);
         var1x = this.updateNamedChoice(var1x, References.ENTITY, "minecraft:area_effect_cloud", MobEffectIdFix::fixAreaEffectCloudTag);
         var1x = var1x.update(DSL.remainderFinder(), MobEffectIdFix::updateLivingEntityTag);
         return var1x;
      });
   }

   private TypeRewriteRule playerFixer() {
      Type var1 = this.getInputSchema().getType(References.PLAYER);
      return this.fixTypeEverywhereTyped("PlayerMobEffectIdFix", var1, (var0) -> {
         return var0.update(DSL.remainderFinder(), MobEffectIdFix::updateLivingEntityTag);
      });
   }

   private static <T> Dynamic<T> fixSuspiciousStewTag(Dynamic<T> var0) {
      Optional var1 = var0.get("Effects").asStreamOpt().result().map((var1x) -> {
         return var0.createList(var1x.map(MobEffectIdFix::updateSuspiciousStewEntry));
      });
      return var0.replaceField("Effects", "effects", var1);
   }

   private TypeRewriteRule itemStackFixer() {
      OpticFinder var1 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      Type var2 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var3 = var2.findField("tag");
      return this.fixTypeEverywhereTyped("ItemStackMobEffectIdFix", var2, (var2x) -> {
         Optional var3x = var2x.getOptional(var1);
         if (var3x.isPresent()) {
            String var4 = (String)((Pair)var3x.get()).getSecond();
            if (var4.equals("minecraft:suspicious_stew")) {
               return var2x.updateTyped(var3, (var0) -> {
                  return var0.update(DSL.remainderFinder(), MobEffectIdFix::fixSuspiciousStewTag);
               });
            }

            if (MOB_EFFECT_INSTANCE_CARRIER_ITEMS.contains(var4)) {
               return var2x.updateTyped(var3, (var0) -> {
                  return var0.update(DSL.remainderFinder(), (var0x) -> {
                     return updateMobEffectInstanceList(var0x, "CustomPotionEffects", "custom_potion_effects");
                  });
               });
            }
         }

         return var2x;
      });
   }

   protected TypeRewriteRule makeRule() {
      return TypeRewriteRule.seq(this.blockEntityFixer(), new TypeRewriteRule[]{this.entityFixer(), this.playerFixer(), this.itemStackFixer()});
   }
}

package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemStackSpawnEggFix extends DataFix {
   private static final Map<String, String> MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (var0) -> {
      var0.put("minecraft:bat", "minecraft:bat_spawn_egg");
      var0.put("minecraft:blaze", "minecraft:blaze_spawn_egg");
      var0.put("minecraft:cave_spider", "minecraft:cave_spider_spawn_egg");
      var0.put("minecraft:chicken", "minecraft:chicken_spawn_egg");
      var0.put("minecraft:cow", "minecraft:cow_spawn_egg");
      var0.put("minecraft:creeper", "minecraft:creeper_spawn_egg");
      var0.put("minecraft:donkey", "minecraft:donkey_spawn_egg");
      var0.put("minecraft:elder_guardian", "minecraft:elder_guardian_spawn_egg");
      var0.put("minecraft:enderman", "minecraft:enderman_spawn_egg");
      var0.put("minecraft:endermite", "minecraft:endermite_spawn_egg");
      var0.put("minecraft:evocation_illager", "minecraft:evocation_illager_spawn_egg");
      var0.put("minecraft:ghast", "minecraft:ghast_spawn_egg");
      var0.put("minecraft:guardian", "minecraft:guardian_spawn_egg");
      var0.put("minecraft:horse", "minecraft:horse_spawn_egg");
      var0.put("minecraft:husk", "minecraft:husk_spawn_egg");
      var0.put("minecraft:llama", "minecraft:llama_spawn_egg");
      var0.put("minecraft:magma_cube", "minecraft:magma_cube_spawn_egg");
      var0.put("minecraft:mooshroom", "minecraft:mooshroom_spawn_egg");
      var0.put("minecraft:mule", "minecraft:mule_spawn_egg");
      var0.put("minecraft:ocelot", "minecraft:ocelot_spawn_egg");
      var0.put("minecraft:pufferfish", "minecraft:pufferfish_spawn_egg");
      var0.put("minecraft:parrot", "minecraft:parrot_spawn_egg");
      var0.put("minecraft:pig", "minecraft:pig_spawn_egg");
      var0.put("minecraft:polar_bear", "minecraft:polar_bear_spawn_egg");
      var0.put("minecraft:rabbit", "minecraft:rabbit_spawn_egg");
      var0.put("minecraft:sheep", "minecraft:sheep_spawn_egg");
      var0.put("minecraft:shulker", "minecraft:shulker_spawn_egg");
      var0.put("minecraft:silverfish", "minecraft:silverfish_spawn_egg");
      var0.put("minecraft:skeleton", "minecraft:skeleton_spawn_egg");
      var0.put("minecraft:skeleton_horse", "minecraft:skeleton_horse_spawn_egg");
      var0.put("minecraft:slime", "minecraft:slime_spawn_egg");
      var0.put("minecraft:spider", "minecraft:spider_spawn_egg");
      var0.put("minecraft:squid", "minecraft:squid_spawn_egg");
      var0.put("minecraft:stray", "minecraft:stray_spawn_egg");
      var0.put("minecraft:turtle", "minecraft:turtle_spawn_egg");
      var0.put("minecraft:vex", "minecraft:vex_spawn_egg");
      var0.put("minecraft:villager", "minecraft:villager_spawn_egg");
      var0.put("minecraft:vindication_illager", "minecraft:vindication_illager_spawn_egg");
      var0.put("minecraft:witch", "minecraft:witch_spawn_egg");
      var0.put("minecraft:wither_skeleton", "minecraft:wither_skeleton_spawn_egg");
      var0.put("minecraft:wolf", "minecraft:wolf_spawn_egg");
      var0.put("minecraft:zombie", "minecraft:zombie_spawn_egg");
      var0.put("minecraft:zombie_horse", "minecraft:zombie_horse_spawn_egg");
      var0.put("minecraft:zombie_pigman", "minecraft:zombie_pigman_spawn_egg");
      var0.put("minecraft:zombie_villager", "minecraft:zombie_villager_spawn_egg");
   });

   public ItemStackSpawnEggFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder var3 = DSL.fieldFinder("id", NamespacedSchema.namespacedString());
      OpticFinder var4 = var1.findField("tag");
      OpticFinder var5 = var4.type().findField("EntityTag");
      return this.fixTypeEverywhereTyped("ItemInstanceSpawnEggFix", var1, (var4x) -> {
         Optional var5x = var4x.getOptional(var2);
         if (var5x.isPresent() && Objects.equals(((Pair)var5x.get()).getSecond(), "minecraft:spawn_egg")) {
            Typed var6 = var4x.getOrCreateTyped(var4);
            Typed var7 = var6.getOrCreateTyped(var5);
            Optional var8 = var7.getOptional(var3);
            if (var8.isPresent()) {
               return var4x.set(var2, Pair.of(References.ITEM_NAME.typeName(), (String)MAP.getOrDefault(var8.get(), "minecraft:pig_spawn_egg")));
            }
         }

         return var4x;
      });
   }
}

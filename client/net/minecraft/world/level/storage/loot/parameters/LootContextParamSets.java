package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;

public class LootContextParamSets {
   private static final BiMap<ResourceLocation, LootContextParamSet> REGISTRY = HashBiMap.create();
   public static final Codec<LootContextParamSet> CODEC;
   public static final LootContextParamSet EMPTY;
   public static final LootContextParamSet CHEST;
   public static final LootContextParamSet COMMAND;
   public static final LootContextParamSet SELECTOR;
   public static final LootContextParamSet FISHING;
   public static final LootContextParamSet ENTITY;
   public static final LootContextParamSet EQUIPMENT;
   public static final LootContextParamSet ARCHAEOLOGY;
   public static final LootContextParamSet GIFT;
   public static final LootContextParamSet PIGLIN_BARTER;
   public static final LootContextParamSet VAULT;
   public static final LootContextParamSet ADVANCEMENT_REWARD;
   public static final LootContextParamSet ADVANCEMENT_ENTITY;
   public static final LootContextParamSet ADVANCEMENT_LOCATION;
   public static final LootContextParamSet BLOCK_USE;
   public static final LootContextParamSet ALL_PARAMS;
   public static final LootContextParamSet BLOCK;
   public static final LootContextParamSet SHEARING;
   public static final LootContextParamSet ENCHANTED_DAMAGE;
   public static final LootContextParamSet ENCHANTED_ITEM;
   public static final LootContextParamSet ENCHANTED_LOCATION;
   public static final LootContextParamSet ENCHANTED_ENTITY;
   public static final LootContextParamSet HIT_BLOCK;

   public LootContextParamSets() {
      super();
   }

   private static LootContextParamSet register(String var0, Consumer<LootContextParamSet.Builder> var1) {
      LootContextParamSet.Builder var2 = new LootContextParamSet.Builder();
      var1.accept(var2);
      LootContextParamSet var3 = var2.build();
      ResourceLocation var4 = ResourceLocation.withDefaultNamespace(var0);
      LootContextParamSet var5 = (LootContextParamSet)REGISTRY.put(var4, var3);
      if (var5 != null) {
         throw new IllegalStateException("Loot table parameter set " + String.valueOf(var4) + " is already registered");
      } else {
         return var3;
      }
   }

   static {
      Codec var10000 = ResourceLocation.CODEC;
      Function var10001 = (var0) -> {
         return (DataResult)Optional.ofNullable((LootContextParamSet)REGISTRY.get(var0)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
               return "No parameter set exists with id: '" + String.valueOf(var0) + "'";
            });
         });
      };
      BiMap var10002 = REGISTRY.inverse();
      Objects.requireNonNull(var10002);
      CODEC = var10000.comapFlatMap(var10001, var10002::get);
      EMPTY = register("empty", (var0) -> {
      });
      CHEST = register("chest", (var0) -> {
         var0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY);
      });
      COMMAND = register("command", (var0) -> {
         var0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY);
      });
      SELECTOR = register("selector", (var0) -> {
         var0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY);
      });
      FISHING = register("fishing", (var0) -> {
         var0.required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY);
      });
      ENTITY = register("entity", (var0) -> {
         var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.DAMAGE_SOURCE).optional(LootContextParams.ATTACKING_ENTITY).optional(LootContextParams.DIRECT_ATTACKING_ENTITY).optional(LootContextParams.LAST_DAMAGE_PLAYER);
      });
      EQUIPMENT = register("equipment", (var0) -> {
         var0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY);
      });
      ARCHAEOLOGY = register("archaeology", (var0) -> {
         var0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY);
      });
      GIFT = register("gift", (var0) -> {
         var0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY);
      });
      PIGLIN_BARTER = register("barter", (var0) -> {
         var0.required(LootContextParams.THIS_ENTITY);
      });
      VAULT = register("vault", (var0) -> {
         var0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY);
      });
      ADVANCEMENT_REWARD = register("advancement_reward", (var0) -> {
         var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN);
      });
      ADVANCEMENT_ENTITY = register("advancement_entity", (var0) -> {
         var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN);
      });
      ADVANCEMENT_LOCATION = register("advancement_location", (var0) -> {
         var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).required(LootContextParams.BLOCK_STATE);
      });
      BLOCK_USE = register("block_use", (var0) -> {
         var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE);
      });
      ALL_PARAMS = register("generic", (var0) -> {
         var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.LAST_DAMAGE_PLAYER).required(LootContextParams.DAMAGE_SOURCE).required(LootContextParams.ATTACKING_ENTITY).required(LootContextParams.DIRECT_ATTACKING_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE).required(LootContextParams.BLOCK_ENTITY).required(LootContextParams.TOOL).required(LootContextParams.EXPLOSION_RADIUS);
      });
      BLOCK = register("block", (var0) -> {
         var0.required(LootContextParams.BLOCK_STATE).required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY).optional(LootContextParams.BLOCK_ENTITY).optional(LootContextParams.EXPLOSION_RADIUS);
      });
      SHEARING = register("shearing", (var0) -> {
         var0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY);
      });
      ENCHANTED_DAMAGE = register("enchanted_damage", (var0) -> {
         var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN).required(LootContextParams.DAMAGE_SOURCE).optional(LootContextParams.DIRECT_ATTACKING_ENTITY).optional(LootContextParams.ATTACKING_ENTITY);
      });
      ENCHANTED_ITEM = register("enchanted_item", (var0) -> {
         var0.required(LootContextParams.TOOL).required(LootContextParams.ENCHANTMENT_LEVEL);
      });
      ENCHANTED_LOCATION = register("enchanted_location", (var0) -> {
         var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN).required(LootContextParams.ENCHANTMENT_ACTIVE);
      });
      ENCHANTED_ENTITY = register("enchanted_entity", (var0) -> {
         var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN);
      });
      HIT_BLOCK = register("hit_block", (var0) -> {
         var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE);
      });
   }
}

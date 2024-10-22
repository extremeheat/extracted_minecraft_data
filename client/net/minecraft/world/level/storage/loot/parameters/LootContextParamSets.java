package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKeySet;

public class LootContextParamSets {
   private static final BiMap<ResourceLocation, ContextKeySet> REGISTRY = HashBiMap.create();
   public static final Codec<ContextKeySet> CODEC = ResourceLocation.CODEC
      .comapFlatMap(
         var0 -> Optional.ofNullable((ContextKeySet)REGISTRY.get(var0))
               .<DataResult>map(DataResult::success)
               .orElseGet(() -> DataResult.error(() -> "No parameter set exists with id: '" + var0 + "'")),
         REGISTRY.inverse()::get
      );
   public static final ContextKeySet EMPTY = register("empty", var0 -> {
   });
   public static final ContextKeySet CHEST = register("chest", var0 -> var0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY));
   public static final ContextKeySet COMMAND = register("command", var0 -> var0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY));
   public static final ContextKeySet SELECTOR = register("selector", var0 -> var0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY));
   public static final ContextKeySet FISHING = register(
      "fishing", var0 -> var0.required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY)
   );
   public static final ContextKeySet ENTITY = register(
      "entity",
      var0 -> var0.required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.DAMAGE_SOURCE)
            .optional(LootContextParams.ATTACKING_ENTITY)
            .optional(LootContextParams.DIRECT_ATTACKING_ENTITY)
            .optional(LootContextParams.LAST_DAMAGE_PLAYER)
   );
   public static final ContextKeySet EQUIPMENT = register("equipment", var0 -> var0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY));
   public static final ContextKeySet ARCHAEOLOGY = register(
      "archaeology", var0 -> var0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY).required(LootContextParams.TOOL)
   );
   public static final ContextKeySet GIFT = register("gift", var0 -> var0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY));
   public static final ContextKeySet PIGLIN_BARTER = register("barter", var0 -> var0.required(LootContextParams.THIS_ENTITY));
   public static final ContextKeySet VAULT = register(
      "vault", var0 -> var0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY).optional(LootContextParams.TOOL)
   );
   public static final ContextKeySet ADVANCEMENT_REWARD = register(
      "advancement_reward", var0 -> var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN)
   );
   public static final ContextKeySet ADVANCEMENT_ENTITY = register(
      "advancement_entity", var0 -> var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN)
   );
   public static final ContextKeySet ADVANCEMENT_LOCATION = register(
      "advancement_location",
      var0 -> var0.required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.TOOL)
            .required(LootContextParams.BLOCK_STATE)
   );
   public static final ContextKeySet BLOCK_USE = register(
      "block_use", var0 -> var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE)
   );
   public static final ContextKeySet ALL_PARAMS = register(
      "generic",
      var0 -> var0.required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.LAST_DAMAGE_PLAYER)
            .required(LootContextParams.DAMAGE_SOURCE)
            .required(LootContextParams.ATTACKING_ENTITY)
            .required(LootContextParams.DIRECT_ATTACKING_ENTITY)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.BLOCK_STATE)
            .required(LootContextParams.BLOCK_ENTITY)
            .required(LootContextParams.TOOL)
            .required(LootContextParams.EXPLOSION_RADIUS)
   );
   public static final ContextKeySet BLOCK = register(
      "block",
      var0 -> var0.required(LootContextParams.BLOCK_STATE)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.TOOL)
            .optional(LootContextParams.THIS_ENTITY)
            .optional(LootContextParams.BLOCK_ENTITY)
            .optional(LootContextParams.EXPLOSION_RADIUS)
   );
   public static final ContextKeySet SHEARING = register(
      "shearing", var0 -> var0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY).required(LootContextParams.TOOL)
   );
   public static final ContextKeySet ENCHANTED_DAMAGE = register(
      "enchanted_damage",
      var0 -> var0.required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.DAMAGE_SOURCE)
            .optional(LootContextParams.DIRECT_ATTACKING_ENTITY)
            .optional(LootContextParams.ATTACKING_ENTITY)
   );
   public static final ContextKeySet ENCHANTED_ITEM = register(
      "enchanted_item", var0 -> var0.required(LootContextParams.TOOL).required(LootContextParams.ENCHANTMENT_LEVEL)
   );
   public static final ContextKeySet ENCHANTED_LOCATION = register(
      "enchanted_location",
      var0 -> var0.required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.ENCHANTMENT_ACTIVE)
   );
   public static final ContextKeySet ENCHANTED_ENTITY = register(
      "enchanted_entity", var0 -> var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN)
   );
   public static final ContextKeySet HIT_BLOCK = register(
      "hit_block",
      var0 -> var0.required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.BLOCK_STATE)
   );

   public LootContextParamSets() {
      super();
   }

   private static ContextKeySet register(String var0, Consumer<ContextKeySet.Builder> var1) {
      ContextKeySet.Builder var2 = new ContextKeySet.Builder();
      var1.accept(var2);
      ContextKeySet var3 = var2.build();
      ResourceLocation var4 = ResourceLocation.withDefaultNamespace(var0);
      ContextKeySet var5 = (ContextKeySet)REGISTRY.put(var4, var3);
      if (var5 != null) {
         throw new IllegalStateException("Loot table parameter set " + var4 + " is already registered");
      } else {
         return var3;
      }
   }
}

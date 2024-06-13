package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;

public class LootContextParamSets {
   private static final BiMap<ResourceLocation, LootContextParamSet> REGISTRY = HashBiMap.create();
   public static final Codec<LootContextParamSet> CODEC = ResourceLocation.CODEC
      .comapFlatMap(
         var0 -> Optional.ofNullable((LootContextParamSet)REGISTRY.get(var0))
               .<DataResult>map(DataResult::success)
               .orElseGet(() -> DataResult.error(() -> "No parameter set exists with id: '" + var0 + "'")),
         REGISTRY.inverse()::get
      );
   public static final LootContextParamSet EMPTY = register("empty", var0 -> {
   });
   public static final LootContextParamSet CHEST = register("chest", var0 -> var0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY));
   public static final LootContextParamSet COMMAND = register(
      "command", var0 -> var0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY)
   );
   public static final LootContextParamSet SELECTOR = register(
      "selector", var0 -> var0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY)
   );
   public static final LootContextParamSet FISHING = register(
      "fishing", var0 -> var0.required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY)
   );
   public static final LootContextParamSet ENTITY = register(
      "entity",
      var0 -> var0.required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.DAMAGE_SOURCE)
            .optional(LootContextParams.ATTACKING_ENTITY)
            .optional(LootContextParams.DIRECT_ATTACKING_ENTITY)
            .optional(LootContextParams.LAST_DAMAGE_PLAYER)
   );
   public static final LootContextParamSet EQUIPMENT = register(
      "equipment", var0 -> var0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY)
   );
   public static final LootContextParamSet ARCHAEOLOGY = register(
      "archaeology", var0 -> var0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY)
   );
   public static final LootContextParamSet GIFT = register("gift", var0 -> var0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY));
   public static final LootContextParamSet PIGLIN_BARTER = register("barter", var0 -> var0.required(LootContextParams.THIS_ENTITY));
   public static final LootContextParamSet VAULT = register("vault", var0 -> var0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY));
   public static final LootContextParamSet ADVANCEMENT_REWARD = register(
      "advancement_reward", var0 -> var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN)
   );
   public static final LootContextParamSet ADVANCEMENT_ENTITY = register(
      "advancement_entity", var0 -> var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN)
   );
   public static final LootContextParamSet ADVANCEMENT_LOCATION = register(
      "advancement_location",
      var0 -> var0.required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.TOOL)
            .required(LootContextParams.BLOCK_STATE)
   );
   public static final LootContextParamSet BLOCK_USE = register(
      "block_use", var0 -> var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE)
   );
   public static final LootContextParamSet ALL_PARAMS = register(
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
   public static final LootContextParamSet BLOCK = register(
      "block",
      var0 -> var0.required(LootContextParams.BLOCK_STATE)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.TOOL)
            .optional(LootContextParams.THIS_ENTITY)
            .optional(LootContextParams.BLOCK_ENTITY)
            .optional(LootContextParams.EXPLOSION_RADIUS)
   );
   public static final LootContextParamSet SHEARING = register(
      "shearing", var0 -> var0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY)
   );
   public static final LootContextParamSet ENCHANTED_DAMAGE = register(
      "enchanted_damage",
      var0 -> var0.required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.DAMAGE_SOURCE)
            .optional(LootContextParams.DIRECT_ATTACKING_ENTITY)
            .optional(LootContextParams.ATTACKING_ENTITY)
   );
   public static final LootContextParamSet ENCHANTED_ITEM = register(
      "enchanted_item", var0 -> var0.required(LootContextParams.TOOL).required(LootContextParams.ENCHANTMENT_LEVEL)
   );
   public static final LootContextParamSet ENCHANTED_LOCATION = register(
      "enchanted_location",
      var0 -> var0.required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.ENCHANTMENT_ACTIVE)
   );
   public static final LootContextParamSet ENCHANTED_ENTITY = register(
      "enchanted_entity", var0 -> var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN)
   );

   public LootContextParamSets() {
      super();
   }

   private static LootContextParamSet register(String var0, Consumer<LootContextParamSet.Builder> var1) {
      LootContextParamSet.Builder var2 = new LootContextParamSet.Builder();
      var1.accept(var2);
      LootContextParamSet var3 = var2.build();
      ResourceLocation var4 = new ResourceLocation(var0);
      LootContextParamSet var5 = (LootContextParamSet)REGISTRY.put(var4, var3);
      if (var5 != null) {
         throw new IllegalStateException("Loot table parameter set " + var4 + " is already registered");
      } else {
         return var3;
      }
   }
}

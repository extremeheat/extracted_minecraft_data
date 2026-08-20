package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKeySet;

public class LootContextParamSets {
   private static final BiMap<Identifier, ContextKeySet> REGISTRY = HashBiMap.create();
   public static final Codec<ContextKeySet> CODEC;
   public static final ContextKeySet EMPTY;
   public static final ContextKeySet CHEST;
   public static final ContextKeySet COMMAND;
   public static final ContextKeySet COMMAND_SLOT_SOURCE;
   public static final ContextKeySet SELECTOR;
   public static final ContextKeySet VILLAGER_TRADE;
   public static final ContextKeySet FISHING;
   public static final ContextKeySet ENTITY;
   public static final ContextKeySet EQUIPMENT;
   public static final ContextKeySet ARCHAEOLOGY;
   public static final ContextKeySet GIFT;
   public static final ContextKeySet PIGLIN_BARTER;
   public static final ContextKeySet VAULT;
   public static final ContextKeySet ADVANCEMENT_REWARD;
   public static final ContextKeySet ADVANCEMENT_ENTITY;
   public static final ContextKeySet ADVANCEMENT_LOCATION;
   public static final ContextKeySet BLOCK_USE;
   public static final ContextKeySet ALL_PARAMS;
   public static final ContextKeySet BLOCK;
   public static final ContextKeySet SHEARING;
   public static final ContextKeySet ENTITY_INTERACT;
   public static final ContextKeySet BLOCK_INTERACT;
   public static final ContextKeySet CONTAINER_PROCESS;
   public static final ContextKeySet ENCHANTED_DAMAGE;
   public static final ContextKeySet ENCHANTED_ITEM;
   public static final ContextKeySet ENCHANTED_LOCATION;
   public static final ContextKeySet ENCHANTED_ENTITY;
   public static final ContextKeySet HIT_BLOCK;

   public LootContextParamSets() {
      super();
   }

   private static ContextKeySet register(final String name, final Consumer<ContextKeySet.Builder> consumer) {
      ContextKeySet.Builder builder = new ContextKeySet.Builder();
      consumer.accept(builder);
      ContextKeySet result = builder.build();
      Identifier id = Identifier.withDefaultNamespace(name);
      ContextKeySet prev = (ContextKeySet)REGISTRY.put(id, result);
      if (prev != null) {
         throw new IllegalStateException("Loot table parameter set " + String.valueOf(id) + " is already registered");
      } else {
         return result;
      }
   }

   static {
      Codec var10000 = Identifier.CODEC;
      Function var10001 = (location) -> (DataResult)Optional.ofNullable((ContextKeySet)REGISTRY.get(location)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "No parameter set exists with id: '" + String.valueOf(location) + "'"));
      BiMap var10002 = REGISTRY.inverse();
      Objects.requireNonNull(var10002);
      CODEC = var10000.comapFlatMap(var10001, var10002::get);
      EMPTY = register("empty", (builder) -> {
      });
      CHEST = register("chest", (builder) -> builder.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY));
      COMMAND = register("command", (builder) -> builder.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY));
      COMMAND_SLOT_SOURCE = register("command_slot_source", (builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.CONTAINER).optional(LootContextParams.THIS_ENTITY));
      SELECTOR = register("selector", (builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY));
      VILLAGER_TRADE = register("villager_trade", (builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY).required(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED));
      FISHING = register("fishing", (builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY));
      ENTITY = register("entity", (builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.DAMAGE_SOURCE).optional(LootContextParams.ATTACKING_ENTITY).optional(LootContextParams.DIRECT_ATTACKING_ENTITY).optional(LootContextParams.LAST_DAMAGE_PLAYER));
      EQUIPMENT = register("equipment", (builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY));
      ARCHAEOLOGY = register("archaeology", (builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY).required(LootContextParams.TOOL));
      GIFT = register("gift", (builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY));
      PIGLIN_BARTER = register("barter", (builder) -> builder.required(LootContextParams.THIS_ENTITY));
      VAULT = register("vault", (builder) -> builder.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY).optional(LootContextParams.TOOL));
      ADVANCEMENT_REWARD = register("advancement_reward", (builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN));
      ADVANCEMENT_ENTITY = register("advancement_entity", (builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN));
      ADVANCEMENT_LOCATION = register("advancement_location", (builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).required(LootContextParams.BLOCK_STATE).optional(LootContextParams.BLOCK_ENTITY));
      BLOCK_USE = register("block_use", (builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE));
      ALL_PARAMS = register("generic", (builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.LAST_DAMAGE_PLAYER).required(LootContextParams.DAMAGE_SOURCE).required(LootContextParams.ATTACKING_ENTITY).required(LootContextParams.DIRECT_ATTACKING_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE).required(LootContextParams.BLOCK_ENTITY).required(LootContextParams.TOOL).required(LootContextParams.EXPLOSION_RADIUS).required(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED).required(LootContextParams.CONTAINER));
      BLOCK = register("block", (builder) -> builder.required(LootContextParams.BLOCK_STATE).required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY).optional(LootContextParams.BLOCK_ENTITY).optional(LootContextParams.EXPLOSION_RADIUS));
      SHEARING = register("shearing", (builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY).required(LootContextParams.TOOL));
      ENTITY_INTERACT = register("entity_interact", (builder) -> builder.required(LootContextParams.TARGET_ENTITY).optional(LootContextParams.INTERACTING_ENTITY).required(LootContextParams.TOOL));
      BLOCK_INTERACT = register("block_interact", (builder) -> builder.required(LootContextParams.BLOCK_STATE).required(LootContextParams.ORIGIN).optional(LootContextParams.BLOCK_ENTITY).optional(LootContextParams.INTERACTING_ENTITY).optional(LootContextParams.TOOL));
      CONTAINER_PROCESS = register("container_process", (builder) -> builder.required(LootContextParams.BLOCK_ENTITY).required(LootContextParams.BLOCK_STATE).required(LootContextParams.CONTAINER).required(LootContextParams.ORIGIN));
      ENCHANTED_DAMAGE = register("enchanted_damage", (builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN).required(LootContextParams.DAMAGE_SOURCE).optional(LootContextParams.DIRECT_ATTACKING_ENTITY).optional(LootContextParams.ATTACKING_ENTITY));
      ENCHANTED_ITEM = register("enchanted_item", (builder) -> builder.required(LootContextParams.TOOL).required(LootContextParams.ENCHANTMENT_LEVEL));
      ENCHANTED_LOCATION = register("enchanted_location", (builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN).required(LootContextParams.ENCHANTMENT_ACTIVE));
      ENCHANTED_ENTITY = register("enchanted_entity", (builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN));
      HIT_BLOCK = register("hit_block", (builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE));
   }
}

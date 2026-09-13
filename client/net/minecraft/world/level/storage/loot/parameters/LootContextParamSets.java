package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;

public class LootContextParamSets {
   public static final Codec<ContextKeySet> CODEC;
   public static final ContextKeySet EMPTY;
   public static final ContextKeySet ALL_PARAMS;
   public static final ContextKeySet CHEST;
   public static final ContextKeySet COMMAND;
   public static final ContextKeySet COMMAND_SLOT_SOURCE;
   public static final ContextKeySet COMMAND_COMPUTE_DEFAULT;
   public static final ContextKeySet COMMAND_COMPUTE_POSITION;
   public static final ContextKeySet COMMAND_COMPUTE_ENTITY;
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
      ResourceKey<ContextKeySet> key = ResourceKey.create(Registries.CONTEXT_KEY_SET, Identifier.withDefaultNamespace(name));
      return register(consumer, key);
   }

   private static ContextKeySet register(final Consumer<ContextKeySet.Builder> consumer, final ResourceKey<ContextKeySet> key) {
      ContextKeySet.Builder builder = new ContextKeySet.Builder();
      consumer.accept(builder);
      return (ContextKeySet)Registry.register(BuiltInRegistries.CONTEXT_KEY_SET, (ResourceKey)key, builder.build());
   }

   public static ContextKeySet bootstrap(final Registry<ContextKeySet> registry) {
      return ALL_PARAMS;
   }

   public static void validate() {
      Set<ContextKey<?>> allParams = new HashSet();
      BuiltInRegistries.CONTEXT_KEY_SET.forEach((paramSet) -> {
         if (paramSet != ALL_PARAMS) {
            allParams.addAll(paramSet.allowed());
         }

      });
      Set<ContextKey<?>> missingFromAllParams = Sets.difference(allParams, ALL_PARAMS.required());
      if (!missingFromAllParams.isEmpty()) {
         throw new IllegalStateException("Missing parameters from 'all_params': " + String.valueOf(missingFromAllParams));
      }
   }

   static {
      CODEC = BuiltInRegistries.CONTEXT_KEY_SET.byNameCodec();
      EMPTY = register("empty", (Consumer)((var0) -> {
      }));
      ALL_PARAMS = register("generic", (Consumer)((builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.LAST_DAMAGE_PLAYER).required(LootContextParams.DAMAGE_SOURCE).required(LootContextParams.ATTACKING_ENTITY).required(LootContextParams.DIRECT_ATTACKING_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE).required(LootContextParams.BLOCK_ENTITY).required(LootContextParams.TOOL).required(LootContextParams.EXPLOSION_RADIUS).required(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED).required(LootContextParams.CONTAINER).required(LootContextParams.INTERACTING_ENTITY).required(LootContextParams.TARGET_ENTITY).required(LootContextParams.ENCHANTMENT_ACTIVE).required(LootContextParams.ENCHANTMENT_LEVEL)));
      CHEST = register("chest", (Consumer)((builder) -> builder.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY)));
      COMMAND = register("command", (Consumer)((builder) -> builder.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY)));
      COMMAND_SLOT_SOURCE = register("command_slot_source", (Consumer)((builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.CONTAINER).optional(LootContextParams.THIS_ENTITY)));
      COMMAND_COMPUTE_DEFAULT = register("command_compute_default", (Consumer)((builder) -> builder.optional(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN)));
      COMMAND_COMPUTE_POSITION = register("command_compute_position", (Consumer)((builder) -> builder.optional(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE).optional(LootContextParams.BLOCK_ENTITY)));
      COMMAND_COMPUTE_ENTITY = register("command_compute_entity", (Consumer)((builder) -> builder.optional(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.TARGET_ENTITY)));
      SELECTOR = register("selector", (Consumer)((builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY)));
      VILLAGER_TRADE = register("villager_trade", (Consumer)((builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY).required(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED)));
      FISHING = register("fishing", (Consumer)((builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY)));
      ENTITY = register("entity", (Consumer)((builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.DAMAGE_SOURCE).optional(LootContextParams.ATTACKING_ENTITY).optional(LootContextParams.DIRECT_ATTACKING_ENTITY).optional(LootContextParams.LAST_DAMAGE_PLAYER)));
      EQUIPMENT = register("equipment", (Consumer)((builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY)));
      ARCHAEOLOGY = register("archaeology", (Consumer)((builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY).required(LootContextParams.TOOL)));
      GIFT = register("gift", (Consumer)((builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY)));
      PIGLIN_BARTER = register("barter", (Consumer)((builder) -> builder.required(LootContextParams.THIS_ENTITY)));
      VAULT = register("vault", (Consumer)((builder) -> builder.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY).optional(LootContextParams.TOOL)));
      ADVANCEMENT_REWARD = register("advancement_reward", (Consumer)((builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN)));
      ADVANCEMENT_ENTITY = register("advancement_entity", (Consumer)((builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN)));
      ADVANCEMENT_LOCATION = register("advancement_location", (Consumer)((builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).required(LootContextParams.BLOCK_STATE).optional(LootContextParams.BLOCK_ENTITY)));
      BLOCK_USE = register("block_use", (Consumer)((builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE)));
      BLOCK = register("block", (Consumer)((builder) -> builder.required(LootContextParams.BLOCK_STATE).required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY).optional(LootContextParams.BLOCK_ENTITY).optional(LootContextParams.EXPLOSION_RADIUS)));
      SHEARING = register("shearing", (Consumer)((builder) -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY).required(LootContextParams.TOOL)));
      ENTITY_INTERACT = register("entity_interact", (Consumer)((builder) -> builder.required(LootContextParams.TARGET_ENTITY).optional(LootContextParams.INTERACTING_ENTITY).required(LootContextParams.TOOL)));
      BLOCK_INTERACT = register("block_interact", (Consumer)((builder) -> builder.required(LootContextParams.BLOCK_STATE).required(LootContextParams.ORIGIN).optional(LootContextParams.BLOCK_ENTITY).optional(LootContextParams.INTERACTING_ENTITY).optional(LootContextParams.TOOL)));
      CONTAINER_PROCESS = register("container_process", (Consumer)((builder) -> builder.required(LootContextParams.BLOCK_ENTITY).required(LootContextParams.BLOCK_STATE).required(LootContextParams.CONTAINER).required(LootContextParams.ORIGIN)));
      ENCHANTED_DAMAGE = register("enchanted_damage", (Consumer)((builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN).required(LootContextParams.DAMAGE_SOURCE).optional(LootContextParams.DIRECT_ATTACKING_ENTITY).optional(LootContextParams.ATTACKING_ENTITY)));
      ENCHANTED_ITEM = register("enchanted_item", (Consumer)((builder) -> builder.required(LootContextParams.TOOL).required(LootContextParams.ENCHANTMENT_LEVEL)));
      ENCHANTED_LOCATION = register("enchanted_location", (Consumer)((builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN).required(LootContextParams.ENCHANTMENT_ACTIVE)));
      ENCHANTED_ENTITY = register("enchanted_entity", (Consumer)((builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN)));
      HIT_BLOCK = register("hit_block", (Consumer)((builder) -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE)));
   }
}

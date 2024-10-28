package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Unit;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.enchantment.effects.DamageImmunity;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public interface EnchantmentEffectComponents {
   Codec<DataComponentType<?>> COMPONENT_CODEC = Codec.lazyInitialized(() -> {
      return BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE.byNameCodec();
   });
   Codec<DataComponentMap> CODEC = DataComponentMap.makeCodec(COMPONENT_CODEC);
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> DAMAGE_PROTECTION = register("damage_protection", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf());
   });
   DataComponentType<List<ConditionalEffect<DamageImmunity>>> DAMAGE_IMMUNITY = register("damage_immunity", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(DamageImmunity.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> DAMAGE = register("damage", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> SMASH_DAMAGE_PER_FALLEN_BLOCK = register("smash_damage_per_fallen_block", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> KNOCKBACK = register("knockback", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> ARMOR_EFFECTIVENESS = register("armor_effectiveness", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf());
   });
   DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>> POST_ATTACK = register("post_attack", (var0) -> {
      return var0.persistent(TargetedConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>> HIT_BLOCK = register("hit_block", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.HIT_BLOCK).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> ITEM_DAMAGE = register("item_damage", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf());
   });
   DataComponentType<List<EnchantmentAttributeEffect>> ATTRIBUTES = register("attributes", (var0) -> {
      return var0.persistent(EnchantmentAttributeEffect.CODEC.codec().listOf());
   });
   DataComponentType<List<TargetedConditionalEffect<EnchantmentValueEffect>>> EQUIPMENT_DROPS = register("equipment_drops", (var0) -> {
      return var0.persistent(TargetedConditionalEffect.equipmentDropsCodec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentLocationBasedEffect>>> LOCATION_CHANGED = register("location_changed", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentLocationBasedEffect.CODEC, LootContextParamSets.ENCHANTED_LOCATION).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>> TICK = register("tick", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> AMMO_USE = register("ammo_use", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> PROJECTILE_PIERCING = register("projectile_piercing", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>> PROJECTILE_SPAWNED = register("projectile_spawned", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> PROJECTILE_SPREAD = register("projectile_spread", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> PROJECTILE_COUNT = register("projectile_count", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> TRIDENT_RETURN_ACCELERATION = register("trident_return_acceleration", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> FISHING_TIME_REDUCTION = register("fishing_time_reduction", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> FISHING_LUCK_BONUS = register("fishing_luck_bonus", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> BLOCK_EXPERIENCE = register("block_experience", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> MOB_EXPERIENCE = register("mob_experience", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf());
   });
   DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> REPAIR_WITH_XP = register("repair_with_xp", (var0) -> {
      return var0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf());
   });
   DataComponentType<EnchantmentValueEffect> CROSSBOW_CHARGE_TIME = register("crossbow_charge_time", (var0) -> {
      return var0.persistent(EnchantmentValueEffect.CODEC);
   });
   DataComponentType<List<CrossbowItem.ChargingSounds>> CROSSBOW_CHARGING_SOUNDS = register("crossbow_charging_sounds", (var0) -> {
      return var0.persistent(CrossbowItem.ChargingSounds.CODEC.listOf());
   });
   DataComponentType<List<Holder<SoundEvent>>> TRIDENT_SOUND = register("trident_sound", (var0) -> {
      return var0.persistent(SoundEvent.CODEC.listOf());
   });
   DataComponentType<Unit> PREVENT_EQUIPMENT_DROP = register("prevent_equipment_drop", (var0) -> {
      return var0.persistent(Unit.CODEC);
   });
   DataComponentType<Unit> PREVENT_ARMOR_CHANGE = register("prevent_armor_change", (var0) -> {
      return var0.persistent(Unit.CODEC);
   });
   DataComponentType<EnchantmentValueEffect> TRIDENT_SPIN_ATTACK_STRENGTH = register("trident_spin_attack_strength", (var0) -> {
      return var0.persistent(EnchantmentValueEffect.CODEC);
   });

   static DataComponentType<?> bootstrap(Registry<DataComponentType<?>> var0) {
      return DAMAGE_PROTECTION;
   }

   private static <T> DataComponentType<T> register(String var0, UnaryOperator<DataComponentType.Builder<T>> var1) {
      return (DataComponentType)Registry.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, (String)var0, ((DataComponentType.Builder)var1.apply(DataComponentType.builder())).build());
   }
}

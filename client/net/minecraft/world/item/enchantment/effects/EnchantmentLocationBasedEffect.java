package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.phys.Vec3;

public interface EnchantmentLocationBasedEffect {
   Codec<EnchantmentLocationBasedEffect> CODEC = BuiltInRegistries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE.byNameCodec().dispatch(EnchantmentLocationBasedEffect::codec, Function.identity());

   static MapCodec<? extends EnchantmentLocationBasedEffect> bootstrap(final Registry<MapCodec<? extends EnchantmentLocationBasedEffect>> registry) {
      Registry.register(registry, (String)"all_of", AllOf.LocationBasedEffects.CODEC);
      Registry.register(registry, (String)"apply_mob_effect", ApplyMobEffect.CODEC);
      Registry.register(registry, (String)"attribute", EnchantmentAttributeEffect.MAP_CODEC);
      Registry.register(registry, (String)"change_item_damage", ChangeItemDamage.CODEC);
      Registry.register(registry, (String)"damage_entity", DamageEntity.CODEC);
      Registry.register(registry, (String)"explode", ExplodeEffect.CODEC);
      Registry.register(registry, (String)"ignite", Ignite.CODEC);
      Registry.register(registry, (String)"apply_impulse", ApplyEntityImpulse.CODEC);
      Registry.register(registry, (String)"apply_exhaustion", ApplyExhaustion.CODEC);
      Registry.register(registry, (String)"play_sound", PlaySoundEffect.CODEC);
      Registry.register(registry, (String)"replace_block", ReplaceBlock.CODEC);
      Registry.register(registry, (String)"replace_disk", ReplaceDisk.CODEC);
      Registry.register(registry, (String)"run_function", RunFunction.CODEC);
      Registry.register(registry, (String)"set_block_properties", SetBlockProperties.CODEC);
      Registry.register(registry, (String)"spawn_particles", SpawnParticlesEffect.CODEC);
      return (MapCodec)Registry.register(registry, (String)"summon_entity", SummonEntityEffect.CODEC);
   }

   void onChangedBlock(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 position, boolean becameActive);

   default void onDeactivated(final EnchantedItemInUse item, final Entity entity, final Vec3 position, final int level) {
   }

   MapCodec<? extends EnchantmentLocationBasedEffect> codec();
}

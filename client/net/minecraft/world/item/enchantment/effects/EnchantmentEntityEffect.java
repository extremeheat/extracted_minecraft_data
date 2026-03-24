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

public interface EnchantmentEntityEffect extends EnchantmentLocationBasedEffect {
   Codec<EnchantmentEntityEffect> CODEC = BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE.byNameCodec().dispatch(EnchantmentEntityEffect::codec, Function.identity());

   static MapCodec<? extends EnchantmentEntityEffect> bootstrap(final Registry<MapCodec<? extends EnchantmentEntityEffect>> registry) {
      Registry.register(registry, (String)"all_of", AllOf.EntityEffects.CODEC);
      Registry.register(registry, (String)"apply_mob_effect", ApplyMobEffect.CODEC);
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

   void apply(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 position);

   default void onChangedBlock(final ServerLevel serverLevel, final int enchantmentLevel, final EnchantedItemInUse item, final Entity entity, final Vec3 position, final boolean becameActive) {
      this.apply(serverLevel, enchantmentLevel, item, entity, position);
   }

   MapCodec<? extends EnchantmentEntityEffect> codec();
}

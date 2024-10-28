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

   static MapCodec<? extends EnchantmentEntityEffect> bootstrap(Registry<MapCodec<? extends EnchantmentEntityEffect>> var0) {
      Registry.register(var0, (String)"all_of", AllOf.EntityEffects.CODEC);
      Registry.register(var0, (String)"apply_mob_effect", ApplyMobEffect.CODEC);
      Registry.register(var0, (String)"damage_entity", DamageEntity.CODEC);
      Registry.register(var0, (String)"damage_item", DamageItem.CODEC);
      Registry.register(var0, (String)"explode", ExplodeEffect.CODEC);
      Registry.register(var0, (String)"ignite", Ignite.CODEC);
      Registry.register(var0, (String)"play_sound", PlaySoundEffect.CODEC);
      Registry.register(var0, (String)"replace_block", ReplaceBlock.CODEC);
      Registry.register(var0, (String)"replace_disk", ReplaceDisk.CODEC);
      Registry.register(var0, (String)"run_function", RunFunction.CODEC);
      Registry.register(var0, (String)"set_block_properties", SetBlockProperties.CODEC);
      Registry.register(var0, (String)"spawn_particles", SpawnParticlesEffect.CODEC);
      return (MapCodec)Registry.register(var0, (String)"summon_entity", SummonEntityEffect.CODEC);
   }

   void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5);

   default void onChangedBlock(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5, boolean var6) {
      this.apply(var1, var2, var3, var4, var5);
   }

   MapCodec<? extends EnchantmentEntityEffect> codec();
}

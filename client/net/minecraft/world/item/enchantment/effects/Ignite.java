package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

public record Ignite(LevelBasedValue duration) implements EnchantmentEntityEffect {
   public static final MapCodec<Ignite> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(LevelBasedValue.CODEC.fieldOf("duration").forGetter((var0x) -> {
         return var0x.duration;
      })).apply(var0, Ignite::new);
   });

   public Ignite(LevelBasedValue duration) {
      super();
      this.duration = duration;
   }

   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      var4.igniteForSeconds(this.duration.calculate(var2));
   }

   public MapCodec<Ignite> codec() {
      return CODEC;
   }

   public LevelBasedValue duration() {
      return this.duration;
   }
}

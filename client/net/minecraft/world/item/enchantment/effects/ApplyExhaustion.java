package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

public record ApplyExhaustion(LevelBasedValue amount) implements EnchantmentEntityEffect {
   public static final MapCodec<ApplyExhaustion> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(LevelBasedValue.CODEC.fieldOf("amount").forGetter(ApplyExhaustion::amount)).apply(var0, ApplyExhaustion::new));

   public ApplyExhaustion(LevelBasedValue var1) {
      super();
      this.amount = var1;
   }

   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      if (var4 instanceof Player var6) {
         var6.causeFoodExhaustion(this.amount.calculate(var2));
      }

   }

   public MapCodec<ApplyExhaustion> codec() {
      return CODEC;
   }
}

package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

public record DamageItem(LevelBasedValue amount) implements EnchantmentEntityEffect {
   public static final MapCodec<DamageItem> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(LevelBasedValue.CODEC.fieldOf("amount").forGetter((var0x) -> {
         return var0x.amount;
      })).apply(var0, DamageItem::new);
   });

   public DamageItem(LevelBasedValue var1) {
      super();
      this.amount = var1;
   }

   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      LivingEntity var8 = var3.owner();
      ServerPlayer var10000;
      if (var8 instanceof ServerPlayer var7) {
         var10000 = var7;
      } else {
         var10000 = null;
      }

      ServerPlayer var6 = var10000;
      var3.itemStack().hurtAndBreak((int)this.amount.calculate(var2), var1, var6, var3.onBreak());
   }

   public MapCodec<DamageItem> codec() {
      return CODEC;
   }

   public LevelBasedValue amount() {
      return this.amount;
   }
}
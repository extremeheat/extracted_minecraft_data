package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

public record ChangeItemDamage(LevelBasedValue amount) implements EnchantmentEntityEffect {
   public static final MapCodec<ChangeItemDamage> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(LevelBasedValue.CODEC.fieldOf("amount").forGetter((var0x) -> {
         return var0x.amount;
      })).apply(var0, ChangeItemDamage::new);
   });

   public ChangeItemDamage(LevelBasedValue var1) {
      super();
      this.amount = var1;
   }

   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      ItemStack var6 = var3.itemStack();
      if (var6.has(DataComponents.MAX_DAMAGE) && var6.has(DataComponents.DAMAGE)) {
         LivingEntity var9 = var3.owner();
         ServerPlayer var10000;
         if (var9 instanceof ServerPlayer) {
            ServerPlayer var8 = (ServerPlayer)var9;
            var10000 = var8;
         } else {
            var10000 = null;
         }

         ServerPlayer var7 = var10000;
         int var10 = (int)this.amount.calculate(var2);
         var6.hurtAndBreak(var10, var1, var7, var3.onBreak());
      }

   }

   public MapCodec<ChangeItemDamage> codec() {
      return CODEC;
   }

   public LevelBasedValue amount() {
      return this.amount;
   }
}

package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

public record DamageItem(LevelBasedValue amount) implements EnchantmentEntityEffect {
   public static final MapCodec<DamageItem> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(LevelBasedValue.CODEC.fieldOf("amount").forGetter(var0x -> var0x.amount)).apply(var0, DamageItem::new)
   );

   public DamageItem(LevelBasedValue amount) {
      super();
      this.amount = amount;
   }

   @Override
   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      ServerPlayer var6 = var3.owner() instanceof ServerPlayer var7 ? var7 : null;
      var3.itemStack().hurtAndBreak((int)this.amount.calculate(var2), var1, var6, var3::onBreak);
   }

   @Override
   public MapCodec<DamageItem> codec() {
      return CODEC;
   }
}

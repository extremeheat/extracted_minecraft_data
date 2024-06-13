package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

public record DamageEntity(LevelBasedValue minDamage, LevelBasedValue maxDamage, Holder<DamageType> damageType) implements EnchantmentEntityEffect {
   public static final MapCodec<DamageEntity> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               LevelBasedValue.CODEC.fieldOf("min_damage").forGetter(DamageEntity::minDamage),
               LevelBasedValue.CODEC.fieldOf("max_damage").forGetter(DamageEntity::maxDamage),
               DamageType.CODEC.fieldOf("damage_type").forGetter(DamageEntity::damageType)
            )
            .apply(var0, DamageEntity::new)
   );

   public DamageEntity(LevelBasedValue minDamage, LevelBasedValue maxDamage, Holder<DamageType> damageType) {
      super();
      this.minDamage = minDamage;
      this.maxDamage = maxDamage;
      this.damageType = damageType;
   }

   @Override
   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      float var6 = Mth.randomBetween(var4.getRandom(), this.minDamage.calculate(var2), this.maxDamage.calculate(var2));
      var4.hurt(new DamageSource(this.damageType, var3.owner()), var6);
   }

   @Override
   public MapCodec<DamageEntity> codec() {
      return CODEC;
   }
}

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
   public static final MapCodec<DamageEntity> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(LevelBasedValue.CODEC.fieldOf("min_damage").forGetter(DamageEntity::minDamage), LevelBasedValue.CODEC.fieldOf("max_damage").forGetter(DamageEntity::maxDamage), DamageType.CODEC.fieldOf("damage_type").forGetter(DamageEntity::damageType)).apply(var0, DamageEntity::new);
   });

   public DamageEntity(LevelBasedValue var1, LevelBasedValue var2, Holder<DamageType> var3) {
      super();
      this.minDamage = var1;
      this.maxDamage = var2;
      this.damageType = var3;
   }

   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      float var6 = Mth.randomBetween(var4.getRandom(), this.minDamage.calculate(var2), this.maxDamage.calculate(var2));
      var4.hurtServer(var1, new DamageSource(this.damageType, var3.owner()), var6);
   }

   public MapCodec<DamageEntity> codec() {
      return CODEC;
   }

   public LevelBasedValue minDamage() {
      return this.minDamage;
   }

   public LevelBasedValue maxDamage() {
      return this.maxDamage;
   }

   public Holder<DamageType> damageType() {
      return this.damageType;
   }
}

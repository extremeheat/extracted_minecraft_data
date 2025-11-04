package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

public record ApplyEntityImpulse(Vec3 direction, Vec3 coordinateScale, LevelBasedValue magnitude) implements EnchantmentEntityEffect {
   public static final MapCodec<ApplyEntityImpulse> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Vec3.CODEC.fieldOf("direction").forGetter(ApplyEntityImpulse::direction), Vec3.CODEC.fieldOf("coordinate_scale").forGetter(ApplyEntityImpulse::coordinateScale), LevelBasedValue.CODEC.fieldOf("magnitude").forGetter(ApplyEntityImpulse::magnitude)).apply(var0, ApplyEntityImpulse::new));

   public ApplyEntityImpulse(Vec3 var1, Vec3 var2, LevelBasedValue var3) {
      super();
      this.direction = var1;
      this.coordinateScale = var2;
      this.magnitude = var3;
   }

   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      Vec3 var6 = var4.getLookAngle();
      Vec3 var7 = var6.addLocalCoordinates(this.direction).multiply(this.coordinateScale).scale((double)this.magnitude.calculate(var2));
      var4.addDeltaMovement(var7);
      var4.hurtMarked = true;
      var4.needsSync = true;
   }

   public MapCodec<ApplyEntityImpulse> codec() {
      return CODEC;
   }
}

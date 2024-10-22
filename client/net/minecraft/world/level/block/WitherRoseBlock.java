package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WitherRoseBlock extends FlowerBlock {
   public static final MapCodec<WitherRoseBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(EFFECTS_FIELD.forGetter(FlowerBlock::getSuspiciousEffects), propertiesCodec()).apply(var0, WitherRoseBlock::new)
   );

   @Override
   public MapCodec<WitherRoseBlock> codec() {
      return CODEC;
   }

   public WitherRoseBlock(Holder<MobEffect> var1, float var2, BlockBehaviour.Properties var3) {
      this(makeEffectList(var1, var2), var3);
   }

   public WitherRoseBlock(SuspiciousStewEffects var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
   }

   @Override
   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return super.mayPlaceOn(var1, var2, var3) || var1.is(Blocks.NETHERRACK) || var1.is(Blocks.SOUL_SAND) || var1.is(Blocks.SOUL_SOIL);
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      VoxelShape var5 = this.getShape(var1, var2, var3, CollisionContext.empty());
      Vec3 var6 = var5.bounds().getCenter();
      double var7 = (double)var3.getX() + var6.x;
      double var9 = (double)var3.getZ() + var6.z;

      for (int var11 = 0; var11 < 3; var11++) {
         if (var4.nextBoolean()) {
            var2.addParticle(
               ParticleTypes.SMOKE,
               var7 + var4.nextDouble() / 5.0,
               (double)var3.getY() + (0.5 - var4.nextDouble()),
               var9 + var4.nextDouble() / 5.0,
               0.0,
               0.0,
               0.0
            );
         }
      }
   }

   @Override
   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (var2 instanceof ServerLevel var5
         && var2.getDifficulty() != Difficulty.PEACEFUL
         && var4 instanceof LivingEntity var6
         && !var6.isInvulnerableTo(var5, var2.damageSources().wither())) {
         var6.addEffect(new MobEffectInstance(MobEffects.WITHER, 40));
      }
   }
}

package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlowerBlock extends BushBlock implements SuspiciousEffectHolder {
   protected static final float AABB_OFFSET = 3.0F;
   protected static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);
   private final MobEffect suspiciousStewEffect;
   private final int effectDuration;

   public FlowerBlock(MobEffect var1, int var2, BlockBehaviour.Properties var3) {
      super(var3);
      this.suspiciousStewEffect = var1;
      if (var1.isInstantenous()) {
         this.effectDuration = var2;
      } else {
         this.effectDuration = var2 * 20;
      }
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Vec3 var5 = var1.getOffset(var2, var3);
      return SHAPE.move(var5.x, var5.y, var5.z);
   }

   @Override
   public MobEffect getSuspiciousEffect() {
      return this.suspiciousStewEffect;
   }

   @Override
   public int getEffectDuration() {
      return this.effectDuration;
   }
}

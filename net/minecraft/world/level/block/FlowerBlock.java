package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlowerBlock extends BushBlock {
   protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
   private final MobEffect suspiciousStewEffect;
   private final int effectDuration;

   public FlowerBlock(MobEffect var1, int var2, Block.Properties var3) {
      super(var3);
      this.suspiciousStewEffect = var1;
      if (var1.isInstantenous()) {
         this.effectDuration = var2;
      } else {
         this.effectDuration = var2 * 20;
      }

   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Vec3 var5 = var1.getOffset(var2, var3);
      return SHAPE.move(var5.x, var5.y, var5.z);
   }

   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.XZ;
   }

   public MobEffect getSuspiciousStewEffect() {
      return this.suspiciousStewEffect;
   }

   public int getEffectDuration() {
      return this.effectDuration;
   }
}

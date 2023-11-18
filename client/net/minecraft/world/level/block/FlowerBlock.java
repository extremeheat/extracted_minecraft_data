package net.minecraft.world.level.block;

import java.util.List;
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
   private final List<SuspiciousEffectHolder.EffectEntry> suspiciousStewEffects;

   public FlowerBlock(MobEffect var1, int var2, BlockBehaviour.Properties var3) {
      super(var3);
      int var4;
      if (var1.isInstantenous()) {
         var4 = var2;
      } else {
         var4 = var2 * 20;
      }

      this.suspiciousStewEffects = List.of(new SuspiciousEffectHolder.EffectEntry(var1, var4));
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Vec3 var5 = var1.getOffset(var2, var3);
      return SHAPE.move(var5.x, var5.y, var5.z);
   }

   @Override
   public List<SuspiciousEffectHolder.EffectEntry> getSuspiciousEffects() {
      return this.suspiciousStewEffects;
   }
}

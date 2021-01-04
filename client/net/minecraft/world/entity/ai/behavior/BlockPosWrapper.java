package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class BlockPosWrapper implements PositionWrapper {
   private final BlockPos pos;
   private final Vec3 lookAt;

   public BlockPosWrapper(BlockPos var1) {
      super();
      this.pos = var1;
      this.lookAt = new Vec3((double)var1.getX() + 0.5D, (double)var1.getY() + 0.5D, (double)var1.getZ() + 0.5D);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Vec3 getLookAtPos() {
      return this.lookAt;
   }

   public boolean isVisible(LivingEntity var1) {
      return true;
   }

   public String toString() {
      return "BlockPosWrapper{pos=" + this.pos + ", lookAt=" + this.lookAt + '}';
   }
}

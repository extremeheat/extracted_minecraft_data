package net.minecraft.client.renderer.state.level;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BlockBreakingRenderState {
   private final Vec3 pos;
   public BlockState blockState;
   private final int progress;

   public BlockBreakingRenderState(Vec3 pos, BlockState blockState, int progress) {
      super();
      this.pos = pos;
      this.blockState = blockState;
      this.progress = progress;
   }

   public Vec3 pos() {
      return this.pos;
   }

   public BlockState blockState() {
      return this.blockState;
   }

   public int progress() {
      return this.progress;
   }

   public void applyRotation(final PoseStack poseStack) {
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj != null && obj.getClass() == this.getClass()) {
         BlockBreakingRenderState that = (BlockBreakingRenderState)obj;
         return Objects.equals(this.pos, that.pos) && Objects.equals(this.blockState, that.blockState) && this.progress == that.progress;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.pos, this.blockState, this.progress});
   }

   public String toString() {
      String var10000 = String.valueOf(this.pos);
      return "BlockBreakingRenderState[pos=" + var10000 + ", blockState=" + String.valueOf(this.blockState) + ", progress=" + this.progress + "]";
   }
}

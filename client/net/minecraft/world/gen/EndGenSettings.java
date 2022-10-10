package net.minecraft.world.gen;

import net.minecraft.util.math.BlockPos;

public class EndGenSettings extends ChunkGenSettings {
   private BlockPos field_205540_n;

   public EndGenSettings() {
      super();
   }

   public EndGenSettings func_205538_a(BlockPos var1) {
      this.field_205540_n = var1;
      return this;
   }

   public BlockPos func_205539_n() {
      return this.field_205540_n;
   }
}

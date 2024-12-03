package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public record BlockSource(ServerLevel level, BlockPos pos, BlockState state, DispenserBlockEntity blockEntity) {
   public BlockSource(ServerLevel var1, BlockPos var2, BlockState var3, DispenserBlockEntity var4) {
      super();
      this.level = var1;
      this.pos = var2;
      this.state = var3;
      this.blockEntity = var4;
   }

   public Vec3 center() {
      return this.pos.getCenter();
   }
}

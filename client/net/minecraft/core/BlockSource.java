package net.minecraft.core;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockSource extends Position {
   // $FF: renamed from: x () double
   double method_2();

   // $FF: renamed from: y () double
   double method_3();

   // $FF: renamed from: z () double
   double method_4();

   BlockPos getPos();

   BlockState getBlockState();

   <T extends BlockEntity> T getEntity();

   ServerLevel getLevel();
}

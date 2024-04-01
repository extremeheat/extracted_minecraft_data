package net.minecraft.world.grid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface FlyingTickable {
   void flyingTick(Level var1, SubGridBlocks var2, BlockState var3, BlockPos var4, Vec3 var5, Direction var6);
}

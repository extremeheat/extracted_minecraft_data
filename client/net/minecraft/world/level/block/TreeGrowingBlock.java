package net.minecraft.world.level.block;

import net.minecraft.world.level.block.grower.TreeGrower;

public interface TreeGrowingBlock extends BonemealableBlock {
   TreeGrower treeGrower();
}

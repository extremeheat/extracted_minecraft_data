package net.minecraft.client.renderer.block.model;

import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.world.level.block.state.BlockState;

public interface UnbakedBlockStateModel extends UnbakedModel {
   Object visualEqualityGroup(BlockState var1);
}

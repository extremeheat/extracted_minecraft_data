package net.minecraft.client.data.models.blockstates;

import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.world.level.block.Block;

public interface BlockModelDefinitionGenerator {
   Block block();

   BlockModelDefinition create();
}

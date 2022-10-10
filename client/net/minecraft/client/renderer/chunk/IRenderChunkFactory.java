package net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.world.World;

public interface IRenderChunkFactory {
   RenderChunk create(World var1, WorldRenderer var2);
}

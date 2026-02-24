package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.renderer.block.model.BakedQuad;

@FunctionalInterface
public interface BlockQuadOutput {
   void put(float x, float y, float z, BakedQuad quad, QuadInstance instance);
}

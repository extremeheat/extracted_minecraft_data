package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadBrightness;
import com.mojang.blaze3d.vertex.QuadLightmapCoords;
import net.minecraft.client.renderer.block.model.BakedQuad;

@FunctionalInterface
public interface BakedQuadOutput {
   void put(PoseStack.Pose pose, BakedQuad quad, QuadBrightness brightness, int color, QuadLightmapCoords lightmapCoord, int overlayCoords);
}

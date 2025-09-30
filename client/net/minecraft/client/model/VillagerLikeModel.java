package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public interface VillagerLikeModel<T extends EntityRenderState> {
   void translateToArms(T var1, PoseStack var2);
}

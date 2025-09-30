package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.HumanoidArm;

public interface ArmedModel<T extends EntityRenderState> {
   void translateToHand(T var1, HumanoidArm var2, PoseStack var3);
}

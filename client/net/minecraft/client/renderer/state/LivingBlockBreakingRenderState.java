package net.minecraft.client.renderer.state;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.LivingBlockRenderer;
import net.minecraft.client.renderer.entity.state.LivingBlockRenderState;
import net.minecraft.client.renderer.state.level.BlockBreakingRenderState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class LivingBlockBreakingRenderState extends BlockBreakingRenderState {
   public float deathTime;
   public boolean hasRotation;
   public final Quaternionf rotation = new Quaternionf();
   public final Vec3 rotationPivot;
   public float ageInTicks;

   public LivingBlockBreakingRenderState(final Vec3 pos, final int progress, final BlockState state, final LivingBlockRenderState livingBlockRenderState) {
      super(pos, state, progress);
      this.blockState = state;
      this.deathTime = livingBlockRenderState.deathTime;
      this.hasRotation = livingBlockRenderState.hasRotation;
      this.rotationPivot = livingBlockRenderState.rotationPivot;
      this.rotation.set(livingBlockRenderState.rotation);
      this.ageInTicks = livingBlockRenderState.ageInTicks;
   }

   public void applyRotation(final PoseStack poseStack) {
      LivingBlockRenderer.applyRotation(poseStack, this.deathTime, this.hasRotation, this.rotationPivot, this.rotation, this.ageInTicks);
   }
}

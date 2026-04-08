package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.slime.MagmaCubeModel;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.MagmaCube;

public class MagmaCubeRenderer extends MobRenderer<MagmaCube, SlimeRenderState, MagmaCubeModel> {
   private static final Identifier MAGMACUBE_LOCATION = Identifier.withDefaultNamespace("textures/entity/slime/magmacube.png");

   public MagmaCubeRenderer(final EntityRendererProvider.Context context) {
      super(context, new MagmaCubeModel(context.bakeLayer(ModelLayers.MAGMA_CUBE)), 0.25F);
   }

   protected int getBlockLightLevel(final MagmaCube entity, final BlockPos blockPos) {
      return 15;
   }

   public Identifier getTextureLocation(final SlimeRenderState state) {
      return MAGMACUBE_LOCATION;
   }

   public SlimeRenderState createRenderState() {
      return new SlimeRenderState();
   }

   public void extractRenderState(final MagmaCube entity, final SlimeRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.squish = Mth.lerp(partialTicks, entity.oSquish, entity.squish);
      state.size = entity.getSize();
   }

   protected float getShadowRadius(final SlimeRenderState state) {
      return (float)state.size * 0.25F;
   }

   protected void scale(final SlimeRenderState state, final PoseStack poseStack) {
      int size = state.size;
      float ss = state.squish / ((float)size * 0.5F + 1.0F);
      float w = 1.0F / (ss + 1.0F);
      poseStack.scale(w * (float)size, 1.0F / w * (float)size, w * (float)size);
   }
}

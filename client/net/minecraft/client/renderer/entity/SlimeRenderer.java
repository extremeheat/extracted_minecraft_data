package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.slime.SlimeModel;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Slime;

public class SlimeRenderer extends MobRenderer<Slime, SlimeRenderState, SlimeModel> {
   public static final Identifier SLIME_LOCATION = Identifier.withDefaultNamespace("textures/entity/slime/slime.png");

   public SlimeRenderer(final EntityRendererProvider.Context context) {
      super(context, new SlimeModel(context.bakeLayer(ModelLayers.SLIME)), 0.25F);
      this.addLayer(new SlimeOuterLayer(this, context.getModelSet()));
   }

   protected float getShadowRadius(final SlimeRenderState state) {
      return (float)state.size * 0.25F;
   }

   protected void scale(final SlimeRenderState state, final PoseStack poseStack) {
      float s = 0.999F;
      poseStack.scale(0.999F, 0.999F, 0.999F);
      poseStack.translate(0.0F, 0.001F, 0.0F);
      float size = (float)state.size;
      float ss = state.squish / (size * 0.5F + 1.0F);
      float w = 1.0F / (ss + 1.0F);
      poseStack.scale(w * size, 1.0F / w * size, w * size);
   }

   public Identifier getTextureLocation(final SlimeRenderState state) {
      return SLIME_LOCATION;
   }

   public SlimeRenderState createRenderState() {
      return new SlimeRenderState();
   }

   public void extractRenderState(final Slime entity, final SlimeRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.squish = Mth.lerp(partialTicks, entity.oSquish, entity.squish);
      state.size = entity.getSize();
   }
}

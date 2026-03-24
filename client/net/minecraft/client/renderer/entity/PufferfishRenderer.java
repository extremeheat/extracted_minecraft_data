package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.animal.fish.PufferfishBigModel;
import net.minecraft.client.model.animal.fish.PufferfishMidModel;
import net.minecraft.client.model.animal.fish.PufferfishSmallModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PufferfishRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.fish.Pufferfish;

public class PufferfishRenderer extends MobRenderer<Pufferfish, PufferfishRenderState, EntityModel<EntityRenderState>> {
   private static final Identifier PUFFER_LOCATION = Identifier.withDefaultNamespace("textures/entity/fish/pufferfish.png");
   private final EntityModel<EntityRenderState> small;
   private final EntityModel<EntityRenderState> mid;
   private final EntityModel<EntityRenderState> big = this.getModel();

   public PufferfishRenderer(final EntityRendererProvider.Context context) {
      super(context, new PufferfishBigModel(context.bakeLayer(ModelLayers.PUFFERFISH_BIG)), 0.2F);
      this.mid = new PufferfishMidModel(context.bakeLayer(ModelLayers.PUFFERFISH_MEDIUM));
      this.small = new PufferfishSmallModel(context.bakeLayer(ModelLayers.PUFFERFISH_SMALL));
   }

   public Identifier getTextureLocation(final PufferfishRenderState state) {
      return PUFFER_LOCATION;
   }

   public PufferfishRenderState createRenderState() {
      return new PufferfishRenderState();
   }

   protected float getShadowRadius(final PufferfishRenderState state) {
      return 0.1F + 0.1F * (float)state.puffState;
   }

   public void submit(final PufferfishRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      EntityModel var10001;
      switch (state.puffState) {
         case 0 -> var10001 = this.small;
         case 1 -> var10001 = this.mid;
         default -> var10001 = this.big;
      }

      this.model = var10001;
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public void extractRenderState(final Pufferfish entity, final PufferfishRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.puffState = entity.getPuffState();
   }

   protected void setupRotations(final PufferfishRenderState state, final PoseStack poseStack, final float bodyRot, final float entityScale) {
      poseStack.translate(0.0F, Mth.cos((double)(state.ageInTicks * 0.05F)) * 0.08F, 0.0F);
      super.setupRotations(state, poseStack, bodyRot, entityScale);
   }
}

package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PufferfishBigModel;
import net.minecraft.client.model.PufferfishMidModel;
import net.minecraft.client.model.PufferfishSmallModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PufferfishRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Pufferfish;

public class PufferfishRenderer extends MobRenderer<Pufferfish, PufferfishRenderState, EntityModel<EntityRenderState>> {
   private static final ResourceLocation PUFFER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/fish/pufferfish.png");
   private final EntityModel<EntityRenderState> small;
   private final EntityModel<EntityRenderState> mid;
   private final EntityModel<EntityRenderState> big = this.getModel();

   public PufferfishRenderer(EntityRendererProvider.Context var1) {
      super(var1, new PufferfishBigModel(var1.bakeLayer(ModelLayers.PUFFERFISH_BIG)), 0.2F);
      this.mid = new PufferfishMidModel(var1.bakeLayer(ModelLayers.PUFFERFISH_MEDIUM));
      this.small = new PufferfishSmallModel(var1.bakeLayer(ModelLayers.PUFFERFISH_SMALL));
   }

   public ResourceLocation getTextureLocation(PufferfishRenderState var1) {
      return PUFFER_LOCATION;
   }

   public PufferfishRenderState createRenderState() {
      return new PufferfishRenderState();
   }

   protected float getShadowRadius(PufferfishRenderState var1) {
      return 0.1F + 0.1F * (float)var1.puffState;
   }

   public void render(PufferfishRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      EntityModel var10001;
      switch (var1.puffState) {
         case 0 -> var10001 = this.small;
         case 1 -> var10001 = this.mid;
         default -> var10001 = this.big;
      }

      this.model = var10001;
      super.render(var1, var2, var3, var4);
   }

   public void extractRenderState(Pufferfish var1, PufferfishRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.puffState = var1.getPuffState();
   }

   protected void setupRotations(PufferfishRenderState var1, PoseStack var2, float var3, float var4) {
      var2.translate(0.0F, Mth.cos(var1.ageInTicks * 0.05F) * 0.08F, 0.0F);
      super.setupRotations(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   protected float getShadowRadius(final LivingEntityRenderState var1) {
      return this.getShadowRadius((PufferfishRenderState)var1);
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((PufferfishRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   // $FF: synthetic method
   protected float getShadowRadius(final EntityRenderState var1) {
      return this.getShadowRadius((PufferfishRenderState)var1);
   }
}

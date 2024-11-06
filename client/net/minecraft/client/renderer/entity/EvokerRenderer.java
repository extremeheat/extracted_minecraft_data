package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.EvokerRenderState;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.SpellcasterIllager;

public class EvokerRenderer<T extends SpellcasterIllager> extends IllagerRenderer<T, EvokerRenderState> {
   private static final ResourceLocation EVOKER_ILLAGER = ResourceLocation.withDefaultNamespace("textures/entity/illager/evoker.png");

   public EvokerRenderer(EntityRendererProvider.Context var1) {
      super(var1, new IllagerModel(var1.bakeLayer(ModelLayers.EVOKER)), 0.5F);
      this.addLayer(new ItemInHandLayer<EvokerRenderState, IllagerModel<EvokerRenderState>>(this, this) {
         public void render(PoseStack var1, MultiBufferSource var2, int var3, EvokerRenderState var4, float var5, float var6) {
            if (var4.isCastingSpell) {
               super.render(var1, var2, var3, (ArmedEntityRenderState)var4, var5, var6);
            }

         }
      });
   }

   public ResourceLocation getTextureLocation(EvokerRenderState var1) {
      return EVOKER_ILLAGER;
   }

   public EvokerRenderState createRenderState() {
      return new EvokerRenderState();
   }

   public void extractRenderState(T var1, EvokerRenderState var2, float var3) {
      super.extractRenderState((AbstractIllager)var1, (IllagerRenderState)var2, var3);
      var2.isCastingSpell = var1.isCastingSpell();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((EvokerRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.illager.IllagerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.EvokerRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;

public class EvokerRenderer<T extends SpellcasterIllager> extends IllagerRenderer<T, EvokerRenderState> {
   private static final Identifier EVOKER_ILLAGER = Identifier.withDefaultNamespace("textures/entity/illager/evoker.png");

   public EvokerRenderer(EntityRendererProvider.Context var1) {
      super(var1, new IllagerModel(var1.bakeLayer(ModelLayers.EVOKER)), 0.5F);
      this.addLayer(new ItemInHandLayer<EvokerRenderState, IllagerModel<EvokerRenderState>>(this) {
         public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, EvokerRenderState var4, float var5, float var6) {
            if (var4.isCastingSpell) {
               super.submit(var1, var2, var3, var4, var5, var6);
            }

         }
      });
   }

   public Identifier getTextureLocation(EvokerRenderState var1) {
      return EVOKER_ILLAGER;
   }

   public EvokerRenderState createRenderState() {
      return new EvokerRenderState();
   }

   public void extractRenderState(T var1, EvokerRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isCastingSpell = var1.isCastingSpell();
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((EvokerRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

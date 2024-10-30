package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.AbstractEquineModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class UndeadHorseRenderer extends AbstractHorseRenderer<AbstractHorse, EquineRenderState, AbstractEquineModel<EquineRenderState>> {
   private static final ResourceLocation ZOMBIE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_zombie.png");
   private static final ResourceLocation SKELETON_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_skeleton.png");
   private final ResourceLocation texture;

   public UndeadHorseRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2, ModelLayerLocation var3, boolean var4) {
      super(var1, new HorseModel(var1.bakeLayer(var2)), new HorseModel(var1.bakeLayer(var3)));
      this.texture = var4 ? SKELETON_TEXTURE : ZOMBIE_TEXTURE;
   }

   public ResourceLocation getTextureLocation(EquineRenderState var1) {
      return this.texture;
   }

   public EquineRenderState createRenderState() {
      return new EquineRenderState();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((EquineRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

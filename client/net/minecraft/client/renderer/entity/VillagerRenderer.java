package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;

public class VillagerRenderer extends MobRenderer<Villager, VillagerModel<Villager>> {
   private static final ResourceLocation VILLAGER_BASE_SKIN = ResourceLocation.withDefaultNamespace("textures/entity/villager/villager.png");

   public VillagerRenderer(EntityRendererProvider.Context var1) {
      super(var1, new VillagerModel(var1.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
      this.addLayer(new CustomHeadLayer(this, var1.getModelSet(), var1.getItemInHandRenderer()));
      this.addLayer(new VillagerProfessionLayer(this, var1.getResourceManager(), "villager"));
      this.addLayer(new CrossedArmsItemLayer(this, var1.getItemInHandRenderer()));
   }

   public ResourceLocation getTextureLocation(Villager var1) {
      return VILLAGER_BASE_SKIN;
   }

   protected void scale(Villager var1, PoseStack var2, float var3) {
      float var4 = 0.9375F * var1.getAgeScale();
      var2.scale(var4, var4, var4);
   }

   protected float getShadowRadius(Villager var1) {
      float var2 = super.getShadowRadius((Mob)var1);
      return var1.isBaby() ? var2 * 0.5F : var2;
   }

   // $FF: synthetic method
   protected float getShadowRadius(final LivingEntity var1) {
      return this.getShadowRadius((Villager)var1);
   }

   // $FF: synthetic method
   protected float getShadowRadius(final Entity var1) {
      return this.getShadowRadius((Villager)var1);
   }
}

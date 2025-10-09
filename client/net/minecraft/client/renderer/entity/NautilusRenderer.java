package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.NautilusArmorModel;
import net.minecraft.client.model.NautilusModel;
import net.minecraft.client.model.NautilusSaddleModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.NautilusRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;

public class NautilusRenderer<T extends AbstractNautilus> extends AgeableMobRenderer<T, NautilusRenderState, NautilusModel> {
   private final ResourceLocation texture;
   private final ResourceLocation babyTexture;

   public NautilusRenderer(EntityRendererProvider.Context var1, Type var2) {
      super(var1, new NautilusModel(var1.bakeLayer(var2.model)), new NautilusModel(var1.bakeLayer(var2.babyModel)), 0.7F);
      this.addLayer(new SimpleEquipmentLayer(this, var1.getEquipmentRenderer(), EquipmentClientInfo.LayerType.NAUTILUS_BODY, (var0) -> var0.bodyArmorItem, new NautilusArmorModel(var1.bakeLayer(ModelLayers.NAUTILUS_ARMOR)), (EntityModel)null));
      this.addLayer(new SimpleEquipmentLayer(this, var1.getEquipmentRenderer(), EquipmentClientInfo.LayerType.NAUTILUS_SADDLE, (var0) -> var0.saddle, new NautilusSaddleModel(var1.bakeLayer(ModelLayers.NAUTILUS_SADDLE)), (EntityModel)null));
      this.texture = var2.texture;
      this.babyTexture = var2.babyTexture;
   }

   public ResourceLocation getTextureLocation(NautilusRenderState var1) {
      return var1.isBaby ? this.babyTexture : this.texture;
   }

   public NautilusRenderState createRenderState() {
      return new NautilusRenderState();
   }

   public void extractRenderState(T var1, NautilusRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.saddle = var1.getItemBySlot(EquipmentSlot.SADDLE).copy();
      var2.bodyArmorItem = var1.getBodyArmorItem().copy();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((NautilusRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   public static enum Type {
      NAUTILUS(ResourceLocation.withDefaultNamespace("textures/entity/nautilus/nautilus.png"), ResourceLocation.withDefaultNamespace("textures/entity/nautilus/nautilus_baby.png"), ModelLayers.NAUTILUS, ModelLayers.NAUTILUS_BABY),
      ZOMBIE_NAUTILUS(ResourceLocation.withDefaultNamespace("textures/entity/nautilus/zombie_nautilus.png"), ResourceLocation.withDefaultNamespace("textures/entity/nautilus/zombie_nautilus.png"), ModelLayers.ZOMBIE_NAUTILUS, ModelLayers.ZOMBIE_NAUTILUS);

      final ResourceLocation texture;
      final ResourceLocation babyTexture;
      final ModelLayerLocation model;
      final ModelLayerLocation babyModel;

      private Type(final ResourceLocation var3, final ResourceLocation var4, final ModelLayerLocation var5, final ModelLayerLocation var6) {
         this.texture = var3;
         this.babyTexture = var4;
         this.model = var5;
         this.babyModel = var6;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{NAUTILUS, ZOMBIE_NAUTILUS};
      }
   }
}

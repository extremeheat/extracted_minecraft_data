package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.animal.nautilus.NautilusArmorModel;
import net.minecraft.client.model.animal.nautilus.NautilusModel;
import net.minecraft.client.model.animal.nautilus.NautilusSaddleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.nautilus.ZombieNautilusCoralModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.NautilusRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilus;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilusVariant;

public class ZombieNautilusRenderer extends MobRenderer<ZombieNautilus, NautilusRenderState, NautilusModel> {
   private final Map<ZombieNautilusVariant.ModelType, NautilusModel> models;

   public ZombieNautilusRenderer(EntityRendererProvider.Context var1) {
      super(var1, new NautilusModel(var1.bakeLayer(ModelLayers.ZOMBIE_NAUTILUS)), 0.7F);
      this.addLayer(new SimpleEquipmentLayer(this, var1.getEquipmentRenderer(), EquipmentClientInfo.LayerType.NAUTILUS_BODY, (var0) -> var0.bodyArmorItem, new NautilusArmorModel(var1.bakeLayer(ModelLayers.NAUTILUS_ARMOR)), (EntityModel)null));
      this.addLayer(new SimpleEquipmentLayer(this, var1.getEquipmentRenderer(), EquipmentClientInfo.LayerType.NAUTILUS_SADDLE, (var0) -> var0.saddle, new NautilusSaddleModel(var1.bakeLayer(ModelLayers.NAUTILUS_SADDLE)), (EntityModel)null));
      this.models = bakeModels(var1);
   }

   private static Map<ZombieNautilusVariant.ModelType, NautilusModel> bakeModels(EntityRendererProvider.Context var0) {
      return Maps.newEnumMap(Map.of(ZombieNautilusVariant.ModelType.NORMAL, new NautilusModel(var0.bakeLayer(ModelLayers.ZOMBIE_NAUTILUS)), ZombieNautilusVariant.ModelType.WARM, new ZombieNautilusCoralModel(var0.bakeLayer(ModelLayers.ZOMBIE_NAUTILUS_CORAL))));
   }

   public void submit(NautilusRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      if (var1.variant != null) {
         this.model = (EntityModel)this.models.get(var1.variant.modelAndTexture().model());
         super.submit(var1, var2, var3, var4);
      }
   }

   public Identifier getTextureLocation(NautilusRenderState var1) {
      return var1.variant == null ? MissingTextureAtlasSprite.getLocation() : var1.variant.modelAndTexture().asset().texturePath();
   }

   public NautilusRenderState createRenderState() {
      return new NautilusRenderState();
   }

   public void extractRenderState(ZombieNautilus var1, NautilusRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.saddle = var1.getItemBySlot(EquipmentSlot.SADDLE).copy();
      var2.bodyArmorItem = var1.getBodyArmorItem().copy();
      var2.variant = (ZombieNautilusVariant)var1.getVariant().value();
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((NautilusRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

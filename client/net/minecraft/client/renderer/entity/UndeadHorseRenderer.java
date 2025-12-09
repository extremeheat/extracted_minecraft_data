package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.animal.equine.AbstractEquineModel;
import net.minecraft.client.model.animal.equine.EquineSaddleModel;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.equine.AbstractHorse;

public class UndeadHorseRenderer extends AbstractHorseRenderer<AbstractHorse, EquineRenderState, AbstractEquineModel<EquineRenderState>> {
   private final Identifier texture;

   public UndeadHorseRenderer(EntityRendererProvider.Context var1, Type var2) {
      super(var1, new HorseModel(var1.bakeLayer(var2.model)), new HorseModel(var1.bakeLayer(var2.babyModel)));
      this.texture = var2.texture;
      this.addLayer(new SimpleEquipmentLayer(this, var1.getEquipmentRenderer(), EquipmentClientInfo.LayerType.HORSE_BODY, (var0) -> var0.bodyArmorItem, new HorseModel(var1.bakeLayer(ModelLayers.UNDEAD_HORSE_ARMOR)), new HorseModel(var1.bakeLayer(ModelLayers.UNDEAD_HORSE_BABY_ARMOR))));
      this.addLayer(new SimpleEquipmentLayer(this, var1.getEquipmentRenderer(), var2.saddleLayer, (var0) -> var0.saddle, new EquineSaddleModel(var1.bakeLayer(var2.saddleModel)), new EquineSaddleModel(var1.bakeLayer(var2.babySaddleModel))));
   }

   public Identifier getTextureLocation(EquineRenderState var1) {
      return this.texture;
   }

   public EquineRenderState createRenderState() {
      return new EquineRenderState();
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((EquineRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   public static enum Type {
      SKELETON(Identifier.withDefaultNamespace("textures/entity/horse/horse_skeleton.png"), ModelLayers.SKELETON_HORSE, ModelLayers.SKELETON_HORSE_BABY, EquipmentClientInfo.LayerType.SKELETON_HORSE_SADDLE, ModelLayers.SKELETON_HORSE_SADDLE, ModelLayers.SKELETON_HORSE_BABY_SADDLE),
      ZOMBIE(Identifier.withDefaultNamespace("textures/entity/horse/horse_zombie.png"), ModelLayers.ZOMBIE_HORSE, ModelLayers.ZOMBIE_HORSE_BABY, EquipmentClientInfo.LayerType.ZOMBIE_HORSE_SADDLE, ModelLayers.ZOMBIE_HORSE_SADDLE, ModelLayers.ZOMBIE_HORSE_BABY_SADDLE);

      final Identifier texture;
      final ModelLayerLocation model;
      final ModelLayerLocation babyModel;
      final EquipmentClientInfo.LayerType saddleLayer;
      final ModelLayerLocation saddleModel;
      final ModelLayerLocation babySaddleModel;

      private Type(final Identifier var3, final ModelLayerLocation var4, final ModelLayerLocation var5, final EquipmentClientInfo.LayerType var6, final ModelLayerLocation var7, final ModelLayerLocation var8) {
         this.texture = var3;
         this.model = var4;
         this.babyModel = var5;
         this.saddleLayer = var6;
         this.saddleModel = var7;
         this.babySaddleModel = var8;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{SKELETON, ZOMBIE};
      }
   }
}

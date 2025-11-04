package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.NautilusArmorModel;
import net.minecraft.client.model.NautilusModel;
import net.minecraft.client.model.NautilusSaddleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.NautilusRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;

public class NautilusRenderer<T extends AbstractNautilus> extends AgeableMobRenderer<T, NautilusRenderState, NautilusModel> {
   private static final Identifier NAUTILUS_LOCATION = Identifier.withDefaultNamespace("textures/entity/nautilus/nautilus.png");
   private static final Identifier NAUTILUS_BABY_LOCATION = Identifier.withDefaultNamespace("textures/entity/nautilus/nautilus_baby.png");

   public NautilusRenderer(EntityRendererProvider.Context var1) {
      super(var1, new NautilusModel(var1.bakeLayer(ModelLayers.NAUTILUS)), new NautilusModel(var1.bakeLayer(ModelLayers.NAUTILUS_BABY)), 0.7F);
      this.addLayer(new SimpleEquipmentLayer(this, var1.getEquipmentRenderer(), EquipmentClientInfo.LayerType.NAUTILUS_BODY, (var0) -> var0.bodyArmorItem, new NautilusArmorModel(var1.bakeLayer(ModelLayers.NAUTILUS_ARMOR)), (EntityModel)null));
      this.addLayer(new SimpleEquipmentLayer(this, var1.getEquipmentRenderer(), EquipmentClientInfo.LayerType.NAUTILUS_SADDLE, (var0) -> var0.saddle, new NautilusSaddleModel(var1.bakeLayer(ModelLayers.NAUTILUS_SADDLE)), (EntityModel)null));
   }

   public Identifier getTextureLocation(NautilusRenderState var1) {
      return var1.isBaby ? NAUTILUS_BABY_LOCATION : NAUTILUS_LOCATION;
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
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((NautilusRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

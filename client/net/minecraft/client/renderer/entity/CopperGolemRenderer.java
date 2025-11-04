package net.minecraft.client.renderer.entity;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.model.CopperGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.BlockDecorationLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import net.minecraft.world.entity.animal.coppergolem.CopperGolemOxidationLevels;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.BlockItemStateProperties;

public class CopperGolemRenderer extends MobRenderer<CopperGolem, CopperGolemRenderState, CopperGolemModel> {
   public CopperGolemRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CopperGolemModel(var1.bakeLayer(ModelLayers.COPPER_GOLEM)), 0.5F);
      this.addLayer(new LivingEntityEmissiveLayer(this, getEyeTextureLocationProvider(), (var0, var1x) -> 1.0F, new CopperGolemModel(var1.bakeLayer(ModelLayers.COPPER_GOLEM)), RenderTypes::eyes, false));
      this.addLayer(new ItemInHandLayer(this));
      Function var10004 = (var0) -> var0.blockOnAntenna;
      CopperGolemModel var10005 = this.model;
      Objects.requireNonNull(var10005);
      this.addLayer(new BlockDecorationLayer(this, var10004, var10005::applyBlockOnAntennaTransform));
      this.addLayer(new CustomHeadLayer(this, var1.getModelSet(), var1.getPlayerSkinRenderCache()));
   }

   public Identifier getTextureLocation(CopperGolemRenderState var1) {
      return CopperGolemOxidationLevels.getOxidationLevel(var1.weathering).texture();
   }

   private static Function<CopperGolemRenderState, Identifier> getEyeTextureLocationProvider() {
      return (var0) -> CopperGolemOxidationLevels.getOxidationLevel(var0.weathering).eyeTexture();
   }

   public CopperGolemRenderState createRenderState() {
      return new CopperGolemRenderState();
   }

   public void extractRenderState(CopperGolem var1, CopperGolemRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      ArmedEntityRenderState.extractArmedEntityRenderState(var1, var2, this.itemModelResolver, var3);
      var2.weathering = var1.getWeatherState();
      var2.copperGolemState = var1.getState();
      var2.idleAnimationState.copyFrom(var1.getIdleAnimationState());
      var2.interactionGetItem.copyFrom(var1.getInteractionGetItemAnimationState());
      var2.interactionGetNoItem.copyFrom(var1.getInteractionGetNoItemAnimationState());
      var2.interactionDropItem.copyFrom(var1.getInteractionDropItemAnimationState());
      var2.interactionDropNoItem.copyFrom(var1.getInteractionDropNoItemAnimationState());
      var2.blockOnAntenna = Optional.of(var1.getItemBySlot(CopperGolem.EQUIPMENT_SLOT_ANTENNA)).flatMap((var0) -> {
         Item var2 = var0.getItem();
         if (var2 instanceof BlockItem var1) {
            BlockItemStateProperties var3 = (BlockItemStateProperties)var0.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
            return Optional.of(var3.apply(var1.getBlock().defaultBlockState()));
         } else {
            return Optional.empty();
         }
      });
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((CopperGolemRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

package net.minecraft.client.renderer.entity;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.model.animal.golem.CopperGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.BlockDecorationLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.animal.golem.CopperGolemOxidationLevels;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.BlockItemStateProperties;

public class CopperGolemRenderer extends MobRenderer<CopperGolem, CopperGolemRenderState, CopperGolemModel> {
   public CopperGolemRenderer(final EntityRendererProvider.Context context) {
      super(context, new CopperGolemModel(context.bakeLayer(ModelLayers.COPPER_GOLEM)), 0.5F);
      this.addLayer(new LivingEntityEmissiveLayer(this, getEyeTextureLocationProvider(), (copperGolem, ageInTicks) -> 1.0F, new CopperGolemModel(context.bakeLayer(ModelLayers.COPPER_GOLEM)), RenderTypes::eyes, false));
      this.addLayer(new ItemInHandLayer(this));
      Function var10004 = (s) -> s.blockOnAntenna;
      CopperGolemModel var10005 = this.model;
      Objects.requireNonNull(var10005);
      this.addLayer(new BlockDecorationLayer(this, var10004, var10005::applyBlockOnAntennaTransform));
      this.addLayer(new CustomHeadLayer(this, context.getModelSet(), context.getPlayerSkinRenderCache()));
   }

   public Identifier getTextureLocation(final CopperGolemRenderState state) {
      return CopperGolemOxidationLevels.getOxidationLevel(state.weathering).texture();
   }

   private static Function<CopperGolemRenderState, Identifier> getEyeTextureLocationProvider() {
      return (renderState) -> CopperGolemOxidationLevels.getOxidationLevel(renderState.weathering).eyeTexture();
   }

   public CopperGolemRenderState createRenderState() {
      return new CopperGolemRenderState();
   }

   public void extractRenderState(final CopperGolem entity, final CopperGolemRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      ArmedEntityRenderState.extractArmedEntityRenderState(entity, state, this.itemModelResolver, partialTicks);
      state.weathering = entity.getWeatherState();
      state.copperGolemState = entity.getState();
      state.idleAnimationState.copyFrom(entity.getIdleAnimationState());
      state.interactionGetItem.copyFrom(entity.getInteractionGetItemAnimationState());
      state.interactionGetNoItem.copyFrom(entity.getInteractionGetNoItemAnimationState());
      state.interactionDropItem.copyFrom(entity.getInteractionDropItemAnimationState());
      state.interactionDropNoItem.copyFrom(entity.getInteractionDropNoItemAnimationState());
      state.blockOnAntenna = Optional.of(entity.getItemBySlot(CopperGolem.EQUIPMENT_SLOT_ANTENNA)).flatMap((itemStack) -> {
         Item patt0$temp = itemStack.getItem();
         if (patt0$temp instanceof BlockItem blockItem) {
            BlockItemStateProperties blockItemState = (BlockItemStateProperties)itemStack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
            return Optional.of(blockItemState.apply(blockItem.getBlock().defaultBlockState()));
         } else {
            return Optional.empty();
         }
      });
   }
}

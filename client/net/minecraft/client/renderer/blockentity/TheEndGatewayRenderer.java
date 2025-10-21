package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.EndGatewayRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.phys.Vec3;

public class TheEndGatewayRenderer extends AbstractEndPortalRenderer<TheEndGatewayBlockEntity, EndGatewayRenderState> {
   private static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_gateway_beam.png");

   public TheEndGatewayRenderer() {
      super();
   }

   public EndGatewayRenderState createRenderState() {
      return new EndGatewayRenderState();
   }

   public void extractRenderState(TheEndGatewayBlockEntity var1, EndGatewayRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      super.extractRenderState(var1, var2, var3, var4, var5);
      Level var6 = var1.getLevel();
      if (var1.isSpawning() || var1.isCoolingDown() && var6 != null) {
         var2.scale = var1.isSpawning() ? var1.getSpawnPercent(var3) : var1.getCooldownPercent(var3);
         double var7 = var1.isSpawning() ? (double)var1.getLevel().getMaxY() : 50.0;
         var2.scale = Mth.sin(var2.scale * 3.1415927F);
         var2.height = Mth.floor((double)var2.scale * var7);
         var2.color = var1.isSpawning() ? DyeColor.MAGENTA.getTextureDiffuseColor() : DyeColor.PURPLE.getTextureDiffuseColor();
         var2.animationTime = var1.getLevel() != null ? (float)Math.floorMod(var1.getLevel().getGameTime(), 40) + var3 : 0.0F;
      } else {
         var2.height = 0;
      }

   }

   public void submit(EndGatewayRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      if (var1.height > 0) {
         BeaconRenderer.submitBeaconBeam(var2, var3, BEAM_LOCATION, var1.scale, var1.animationTime, -var1.height, var1.height * 2, var1.color, 0.15F, 0.175F);
      }

      super.submit(var1, var2, var3, var4);
   }

   protected float getOffsetUp() {
      return 1.0F;
   }

   protected float getOffsetDown() {
      return 0.0F;
   }

   protected RenderType renderType() {
      return RenderTypes.endGateway();
   }

   public int getViewDistance() {
      return 256;
   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

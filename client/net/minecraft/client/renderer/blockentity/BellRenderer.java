package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.model.BellModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BellRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.phys.Vec3;

public class BellRenderer implements BlockEntityRenderer<BellBlockEntity, BellRenderState> {
   public static final Material BELL_RESOURCE_LOCATION;
   private final MaterialSet materials;
   private final BellModel model;

   public BellRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.materials = var1.materials();
      this.model = new BellModel(var1.bakeLayer(ModelLayers.BELL));
   }

   public BellRenderState createRenderState() {
      return new BellRenderState();
   }

   public void extractRenderState(BellBlockEntity var1, BellRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.ticks = (float)var1.ticks + var3;
      var2.shakeDirection = var1.shaking ? var1.clickDirection : null;
   }

   public void submit(BellRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      BellModel.State var5 = new BellModel.State(var1.ticks, var1.shakeDirection);
      this.model.setupAnim(var5);
      RenderType var6 = BELL_RESOURCE_LOCATION.renderType(RenderType::entitySolid);
      var3.submitModel(this.model, var5, var2, var6, var1.lightCoords, OverlayTexture.NO_OVERLAY, -1, this.materials.get(BELL_RESOURCE_LOCATION), 0, var1.breakProgress);
   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }

   static {
      BELL_RESOURCE_LOCATION = Sheets.BLOCK_ENTITIES_MAPPER.defaultNamespaceApply("bell/bell_body");
   }
}

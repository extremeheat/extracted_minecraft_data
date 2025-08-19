package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.model.BellModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.phys.Vec3;

public class BellRenderer implements BlockEntityRenderer<BellBlockEntity> {
   public static final Material BELL_RESOURCE_LOCATION;
   private final MaterialSet materials;
   private final BellModel model;
   private final BellModel.State modelState = new BellModel.State();

   public BellRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.materials = var1.materials();
      this.model = new BellModel(var1.bakeLayer(ModelLayers.BELL));
   }

   public void submit(BellBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      this.modelState.ticks = (float)var1.ticks + var2;
      this.modelState.shakeDirection = var1.shaking ? var1.clickDirection : null;
      this.model.setupAnim(this.modelState);
      RenderType var9 = BELL_RESOURCE_LOCATION.renderType(RenderType::entitySolid);
      var8.submitModel(this.model, this.modelState, var3, var9, var4, var5, -1, this.materials.get(BELL_RESOURCE_LOCATION), 0, var7);
   }

   static {
      BELL_RESOURCE_LOCATION = Sheets.BLOCK_ENTITIES_MAPPER.defaultNamespaceApply("bell/bell_body");
   }
}

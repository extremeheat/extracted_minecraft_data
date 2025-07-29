package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.BellModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
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

   public void render(BellBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      VertexConsumer var8 = BELL_RESOURCE_LOCATION.buffer(this.materials, var4, RenderType::entitySolid);
      this.modelState.ticks = (float)var1.ticks + var2;
      this.modelState.shakeDirection = var1.shaking ? var1.clickDirection : null;
      this.model.setupAnim(this.modelState);
      this.model.renderToBuffer(var3, var8, var5, var6);
   }

   static {
      BELL_RESOURCE_LOCATION = Sheets.BLOCK_ENTITIES_MAPPER.defaultNamespaceApply("bell/bell_body");
   }
}

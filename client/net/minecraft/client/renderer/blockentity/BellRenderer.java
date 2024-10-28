package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.BellModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BellBlockEntity;

public class BellRenderer implements BlockEntityRenderer<BellBlockEntity> {
   public static final Material BELL_RESOURCE_LOCATION;
   private final BellModel model;

   public BellRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.model = new BellModel(var1.bakeLayer(ModelLayers.BELL));
   }

   public void render(BellBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      VertexConsumer var7 = BELL_RESOURCE_LOCATION.buffer(var4, RenderType::entitySolid);
      this.model.setupAnim(var1, var2);
      this.model.renderToBuffer(var3, var7, var5, var6);
   }

   static {
      BELL_RESOURCE_LOCATION = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/bell/bell_body"));
   }
}

package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BellBlockEntity;

public class BellRenderer extends BlockEntityRenderer {
   public static final Material BELL_RESOURCE_LOCATION;
   private final ModelPart bellBody = new ModelPart(32, 32, 0, 0);

   public BellRenderer(BlockEntityRenderDispatcher var1) {
      super(var1);
      this.bellBody.addBox(-3.0F, -6.0F, -3.0F, 6.0F, 7.0F, 6.0F);
      this.bellBody.setPos(8.0F, 12.0F, 8.0F);
      ModelPart var2 = new ModelPart(32, 32, 0, 13);
      var2.addBox(4.0F, 4.0F, 4.0F, 8.0F, 2.0F, 8.0F);
      var2.setPos(-8.0F, -12.0F, -8.0F);
      this.bellBody.addChild(var2);
   }

   public void render(BellBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      float var7 = (float)var1.ticks + var2;
      float var8 = 0.0F;
      float var9 = 0.0F;
      if (var1.shaking) {
         float var10 = Mth.sin(var7 / 3.1415927F) / (4.0F + var7 / 3.0F);
         if (var1.clickDirection == Direction.NORTH) {
            var8 = -var10;
         } else if (var1.clickDirection == Direction.SOUTH) {
            var8 = var10;
         } else if (var1.clickDirection == Direction.EAST) {
            var9 = -var10;
         } else if (var1.clickDirection == Direction.WEST) {
            var9 = var10;
         }
      }

      this.bellBody.xRot = var8;
      this.bellBody.zRot = var9;
      VertexConsumer var11 = BELL_RESOURCE_LOCATION.buffer(var4, RenderType::entitySolid);
      this.bellBody.render(var3, var11, var5, var6);
   }

   static {
      BELL_RESOURCE_LOCATION = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/bell/bell_body"));
   }
}

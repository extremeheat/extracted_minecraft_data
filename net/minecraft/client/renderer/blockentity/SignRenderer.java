package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class SignRenderer extends BlockEntityRenderer {
   private final SignRenderer.SignModel signModel = new SignRenderer.SignModel();

   public SignRenderer(BlockEntityRenderDispatcher var1) {
      super(var1);
   }

   public void render(SignBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      BlockState var7 = var1.getBlockState();
      var3.pushPose();
      float var8 = 0.6666667F;
      float var9;
      if (var7.getBlock() instanceof StandingSignBlock) {
         var3.translate(0.5D, 0.5D, 0.5D);
         var9 = -((float)((Integer)var7.getValue(StandingSignBlock.ROTATION) * 360) / 16.0F);
         var3.mulPose(Vector3f.YP.rotationDegrees(var9));
         this.signModel.stick.visible = true;
      } else {
         var3.translate(0.5D, 0.5D, 0.5D);
         var9 = -((Direction)var7.getValue(WallSignBlock.FACING)).toYRot();
         var3.mulPose(Vector3f.YP.rotationDegrees(var9));
         var3.translate(0.0D, -0.3125D, -0.4375D);
         this.signModel.stick.visible = false;
      }

      var3.pushPose();
      var3.scale(0.6666667F, -0.6666667F, -0.6666667F);
      Material var23 = getMaterial(var7.getBlock());
      SignRenderer.SignModel var10002 = this.signModel;
      var10002.getClass();
      VertexConsumer var10 = var23.buffer(var4, var10002::renderType);
      this.signModel.sign.render(var3, var10, var5, var6);
      this.signModel.stick.render(var3, var10, var5, var6);
      var3.popPose();
      Font var11 = this.renderer.getFont();
      float var12 = 0.010416667F;
      var3.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
      var3.scale(0.010416667F, -0.010416667F, 0.010416667F);
      int var13 = var1.getColor().getTextColor();
      double var14 = 0.4D;
      int var16 = (int)((double)NativeImage.getR(var13) * 0.4D);
      int var17 = (int)((double)NativeImage.getG(var13) * 0.4D);
      int var18 = (int)((double)NativeImage.getB(var13) * 0.4D);
      int var19 = NativeImage.combine(0, var18, var17, var16);

      for(int var20 = 0; var20 < 4; ++var20) {
         String var21 = var1.getRenderMessage(var20, (var1x) -> {
            List var2 = ComponentRenderUtils.wrapComponents(var1x, 90, var11, false, true);
            return var2.isEmpty() ? "" : ((Component)var2.get(0)).getColoredString();
         });
         if (var21 != null) {
            float var22 = (float)(-var11.width(var21) / 2);
            var11.drawInBatch(var21, var22, (float)(var20 * 10 - var1.messages.length * 5), var19, false, var3.last().pose(), var4, false, 0, var5);
         }
      }

      var3.popPose();
   }

   public static Material getMaterial(Block var0) {
      WoodType var1;
      if (var0 instanceof SignBlock) {
         var1 = ((SignBlock)var0).type();
      } else {
         var1 = WoodType.OAK;
      }

      return Sheets.signTexture(var1);
   }

   public static final class SignModel extends Model {
      public final ModelPart sign = new ModelPart(64, 32, 0, 0);
      public final ModelPart stick;

      public SignModel() {
         super(RenderType::entityCutoutNoCull);
         this.sign.addBox(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F, 0.0F);
         this.stick = new ModelPart(64, 32, 0, 14);
         this.stick.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F, 0.0F);
      }

      public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
         this.sign.render(var1, var2, var3, var4, var5, var6, var7, var8);
         this.stick.render(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }
}

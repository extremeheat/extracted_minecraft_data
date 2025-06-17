package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Objects;
import java.util.Set;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class ShulkerBoxRenderer implements BlockEntityRenderer<ShulkerBoxBlockEntity> {
   private final ShulkerBoxModel model;

   public ShulkerBoxRenderer(BlockEntityRendererProvider.Context var1) {
      this(var1.getModelSet());
   }

   public ShulkerBoxRenderer(EntityModelSet var1) {
      super();
      this.model = new ShulkerBoxModel(var1.bakeLayer(ModelLayers.SHULKER_BOX));
   }

   public void render(ShulkerBoxBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      Direction var8 = (Direction)var1.getBlockState().getValueOrElse(ShulkerBoxBlock.FACING, Direction.UP);
      DyeColor var9 = var1.getColor();
      Material var10;
      if (var9 == null) {
         var10 = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
      } else {
         var10 = Sheets.getShulkerBoxMaterial(var9);
      }

      float var11 = var1.getProgress(var2);
      this.render(var3, var4, var5, var6, var8, var11, var10);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, int var4, Direction var5, float var6, Material var7) {
      var1.pushPose();
      this.prepareModel(var1, var5, var6);
      ShulkerBoxModel var10002 = this.model;
      Objects.requireNonNull(var10002);
      VertexConsumer var8 = var7.buffer(var2, var10002::renderType);
      this.model.renderToBuffer(var1, var8, var3, var4);
      var1.popPose();
   }

   private void prepareModel(PoseStack var1, Direction var2, float var3) {
      var1.translate(0.5F, 0.5F, 0.5F);
      float var4 = 0.9995F;
      var1.scale(0.9995F, 0.9995F, 0.9995F);
      var1.mulPose((Quaternionfc)var2.getRotation());
      var1.scale(1.0F, -1.0F, -1.0F);
      var1.translate(0.0F, -1.0F, 0.0F);
      this.model.animate(var3);
   }

   public void getExtents(Direction var1, float var2, Set<Vector3f> var3) {
      PoseStack var4 = new PoseStack();
      this.prepareModel(var4, var1, var2);
      this.model.root().getExtentsForGui(var4, var3);
   }

   static class ShulkerBoxModel extends Model {
      private final ModelPart lid;

      public ShulkerBoxModel(ModelPart var1) {
         super(var1, RenderType::entityCutoutNoCull);
         this.lid = var1.getChild("lid");
      }

      public void animate(float var1) {
         this.lid.setPos(0.0F, 24.0F - var1 * 0.5F * 16.0F, 0.0F);
         this.lid.yRot = 270.0F * var1 * 0.017453292F;
      }
   }
}

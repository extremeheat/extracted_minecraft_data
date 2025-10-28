package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import java.util.Set;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.ShulkerBoxRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class ShulkerBoxRenderer implements BlockEntityRenderer<ShulkerBoxBlockEntity, ShulkerBoxRenderState> {
   private final MaterialSet materials;
   private final ShulkerBoxModel model;

   public ShulkerBoxRenderer(BlockEntityRendererProvider.Context var1) {
      this(var1.entityModelSet(), var1.materials());
   }

   public ShulkerBoxRenderer(SpecialModelRenderer.BakingContext var1) {
      this(var1.entityModelSet(), var1.materials());
   }

   public ShulkerBoxRenderer(EntityModelSet var1, MaterialSet var2) {
      super();
      this.materials = var2;
      this.model = new ShulkerBoxModel(var1.bakeLayer(ModelLayers.SHULKER_BOX));
   }

   public ShulkerBoxRenderState createRenderState() {
      return new ShulkerBoxRenderState();
   }

   public void extractRenderState(ShulkerBoxBlockEntity var1, ShulkerBoxRenderState var2, float var3, Vec3 var4, ModelFeatureRenderer.@Nullable CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.direction = (Direction)var1.getBlockState().getValueOrElse(ShulkerBoxBlock.FACING, Direction.UP);
      var2.color = var1.getColor();
      var2.progress = var1.getProgress(var3);
   }

   public void submit(ShulkerBoxRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      DyeColor var5 = var1.color;
      Material var6;
      if (var5 == null) {
         var6 = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
      } else {
         var6 = Sheets.getShulkerBoxMaterial(var5);
      }

      this.submit(var2, var3, var1.lightCoords, OverlayTexture.NO_OVERLAY, var1.direction, var1.progress, var1.breakProgress, var6, 0);
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, int var4, Direction var5, float var6, ModelFeatureRenderer.@Nullable CrumblingOverlay var7, Material var8, int var9) {
      var1.pushPose();
      this.prepareModel(var1, var5, var6);
      ShulkerBoxModel var10001 = this.model;
      Float var10002 = var6;
      ShulkerBoxModel var10005 = this.model;
      Objects.requireNonNull(var10005);
      var2.submitModel(var10001, var10002, var1, var8.renderType(var10005::renderType), var3, var4, -1, this.materials.get(var8), var9, var7);
      var1.popPose();
   }

   private void prepareModel(PoseStack var1, Direction var2, float var3) {
      var1.translate(0.5F, 0.5F, 0.5F);
      float var4 = 0.9995F;
      var1.scale(0.9995F, 0.9995F, 0.9995F);
      var1.mulPose((Quaternionfc)var2.getRotation());
      var1.scale(1.0F, -1.0F, -1.0F);
      var1.translate(0.0F, -1.0F, 0.0F);
      this.model.setupAnim(var3);
   }

   public void getExtents(Direction var1, float var2, Set<Vector3f> var3) {
      PoseStack var4 = new PoseStack();
      this.prepareModel(var4, var1, var2);
      this.model.root().getExtentsForGui(var4, var3);
   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }

   static class ShulkerBoxModel extends Model<Float> {
      private final ModelPart lid;

      public ShulkerBoxModel(ModelPart var1) {
         super(var1, RenderTypes::entityCutoutNoCull);
         this.lid = var1.getChild("lid");
      }

      public void setupAnim(Float var1) {
         super.setupAnim(var1);
         this.lid.setPos(0.0F, 24.0F - var1 * 0.5F * 16.0F, 0.0F);
         this.lid.yRot = 270.0F * var1 * 0.017453292F;
      }
   }
}

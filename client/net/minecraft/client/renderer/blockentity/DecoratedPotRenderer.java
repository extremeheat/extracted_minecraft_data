package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.DecoratedPotRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class DecoratedPotRenderer implements BlockEntityRenderer<DecoratedPotBlockEntity, DecoratedPotRenderState> {
   private final MaterialSet materials;
   private static final String NECK = "neck";
   private static final String FRONT = "front";
   private static final String BACK = "back";
   private static final String LEFT = "left";
   private static final String RIGHT = "right";
   private static final String TOP = "top";
   private static final String BOTTOM = "bottom";
   private final ModelPart neck;
   private final ModelPart frontSide;
   private final ModelPart backSide;
   private final ModelPart leftSide;
   private final ModelPart rightSide;
   private final ModelPart top;
   private final ModelPart bottom;
   private static final float WOBBLE_AMPLITUDE = 0.125F;

   public DecoratedPotRenderer(BlockEntityRendererProvider.Context var1) {
      this(var1.entityModelSet(), var1.materials());
   }

   public DecoratedPotRenderer(SpecialModelRenderer.BakingContext var1) {
      this(var1.entityModelSet(), var1.materials());
   }

   public DecoratedPotRenderer(EntityModelSet var1, MaterialSet var2) {
      super();
      this.materials = var2;
      ModelPart var3 = var1.bakeLayer(ModelLayers.DECORATED_POT_BASE);
      this.neck = var3.getChild("neck");
      this.top = var3.getChild("top");
      this.bottom = var3.getChild("bottom");
      ModelPart var4 = var1.bakeLayer(ModelLayers.DECORATED_POT_SIDES);
      this.frontSide = var4.getChild("front");
      this.backSide = var4.getChild("back");
      this.leftSide = var4.getChild("left");
      this.rightSide = var4.getChild("right");
   }

   public static LayerDefinition createBaseLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      CubeDeformation var2 = new CubeDeformation(0.2F);
      CubeDeformation var3 = new CubeDeformation(-0.1F);
      var1.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 0).addBox(4.0F, 17.0F, 4.0F, 8.0F, 3.0F, 8.0F, var3).texOffs(0, 5).addBox(5.0F, 20.0F, 5.0F, 6.0F, 1.0F, 6.0F, var2), PartPose.offsetAndRotation(0.0F, 37.0F, 16.0F, 3.1415927F, 0.0F, 0.0F));
      CubeListBuilder var4 = CubeListBuilder.create().texOffs(-14, 13).addBox(0.0F, 0.0F, 0.0F, 14.0F, 0.0F, 14.0F);
      var1.addOrReplaceChild("top", var4, PartPose.offsetAndRotation(1.0F, 16.0F, 1.0F, 0.0F, 0.0F, 0.0F));
      var1.addOrReplaceChild("bottom", var4, PartPose.offsetAndRotation(1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F));
      return LayerDefinition.create(var0, 32, 32);
   }

   public static LayerDefinition createSidesLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      CubeListBuilder var2 = CubeListBuilder.create().texOffs(1, 0).addBox(0.0F, 0.0F, 0.0F, 14.0F, 16.0F, 0.0F, EnumSet.of(Direction.NORTH));
      var1.addOrReplaceChild("back", var2, PartPose.offsetAndRotation(15.0F, 16.0F, 1.0F, 0.0F, 0.0F, 3.1415927F));
      var1.addOrReplaceChild("left", var2, PartPose.offsetAndRotation(1.0F, 16.0F, 1.0F, 0.0F, -1.5707964F, 3.1415927F));
      var1.addOrReplaceChild("right", var2, PartPose.offsetAndRotation(15.0F, 16.0F, 15.0F, 0.0F, 1.5707964F, 3.1415927F));
      var1.addOrReplaceChild("front", var2, PartPose.offsetAndRotation(1.0F, 16.0F, 15.0F, 3.1415927F, 0.0F, 0.0F));
      return LayerDefinition.create(var0, 16, 16);
   }

   private static Material getSideMaterial(Optional<Item> var0) {
      if (var0.isPresent()) {
         Material var1 = Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.getPatternFromItem((Item)var0.get()));
         if (var1 != null) {
            return var1;
         }
      }

      return Sheets.DECORATED_POT_SIDE;
   }

   public DecoratedPotRenderState createRenderState() {
      return new DecoratedPotRenderState();
   }

   public void extractRenderState(DecoratedPotBlockEntity var1, DecoratedPotRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.decorations = var1.getDecorations();
      var2.direction = var1.getDirection();
      DecoratedPotBlockEntity.WobbleStyle var6 = var1.lastWobbleStyle;
      if (var6 != null && var1.getLevel() != null) {
         var2.wobbleProgress = ((float)(var1.getLevel().getGameTime() - var1.wobbleStartedAtTick) + var3) / (float)var6.duration;
      } else {
         var2.wobbleProgress = 0.0F;
      }

   }

   public void submit(DecoratedPotRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var2.pushPose();
      Direction var5 = var1.direction;
      var2.translate(0.5, 0.0, 0.5);
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F - var5.toYRot()));
      var2.translate(-0.5, 0.0, -0.5);
      if (var1.wobbleProgress >= 0.0F && var1.wobbleProgress <= 1.0F) {
         if (var1.wobbleStyle == DecoratedPotBlockEntity.WobbleStyle.POSITIVE) {
            float var6 = 0.015625F;
            float var7 = var1.wobbleProgress * 6.2831855F;
            float var8 = -1.5F * (Mth.cos(var7) + 0.5F) * Mth.sin(var7 / 2.0F);
            var2.rotateAround(Axis.XP.rotation(var8 * 0.015625F), 0.5F, 0.0F, 0.5F);
            float var9 = Mth.sin(var7);
            var2.rotateAround(Axis.ZP.rotation(var9 * 0.015625F), 0.5F, 0.0F, 0.5F);
         } else {
            float var10 = Mth.sin(-var1.wobbleProgress * 3.0F * 3.1415927F) * 0.125F;
            float var11 = 1.0F - var1.wobbleProgress;
            var2.rotateAround(Axis.YP.rotation(var10 * var11), 0.5F, 0.0F, 0.5F);
         }
      }

      this.submit(var2, var3, var1.lightCoords, OverlayTexture.NO_OVERLAY, var1.decorations, 0);
      var2.popPose();
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, int var4, PotDecorations var5, int var6) {
      RenderType var7 = Sheets.DECORATED_POT_BASE.renderType(RenderTypes::entitySolid);
      TextureAtlasSprite var8 = this.materials.get(Sheets.DECORATED_POT_BASE);
      var2.submitModelPart(this.neck, var1, var7, var3, var4, var8, false, false, -1, (ModelFeatureRenderer.CrumblingOverlay)null, var6);
      var2.submitModelPart(this.top, var1, var7, var3, var4, var8, false, false, -1, (ModelFeatureRenderer.CrumblingOverlay)null, var6);
      var2.submitModelPart(this.bottom, var1, var7, var3, var4, var8, false, false, -1, (ModelFeatureRenderer.CrumblingOverlay)null, var6);
      Material var9 = getSideMaterial(var5.front());
      var2.submitModelPart(this.frontSide, var1, var9.renderType(RenderTypes::entitySolid), var3, var4, this.materials.get(var9), false, false, -1, (ModelFeatureRenderer.CrumblingOverlay)null, var6);
      Material var10 = getSideMaterial(var5.back());
      var2.submitModelPart(this.backSide, var1, var10.renderType(RenderTypes::entitySolid), var3, var4, this.materials.get(var10), false, false, -1, (ModelFeatureRenderer.CrumblingOverlay)null, var6);
      Material var11 = getSideMaterial(var5.left());
      var2.submitModelPart(this.leftSide, var1, var11.renderType(RenderTypes::entitySolid), var3, var4, this.materials.get(var11), false, false, -1, (ModelFeatureRenderer.CrumblingOverlay)null, var6);
      Material var12 = getSideMaterial(var5.right());
      var2.submitModelPart(this.rightSide, var1, var12.renderType(RenderTypes::entitySolid), var3, var4, this.materials.get(var12), false, false, -1, (ModelFeatureRenderer.CrumblingOverlay)null, var6);
   }

   public void getExtents(Set<Vector3f> var1) {
      PoseStack var2 = new PoseStack();
      this.neck.getExtentsForGui(var2, var1);
      this.top.getExtentsForGui(var2, var1);
      this.bottom.getExtentsForGui(var2, var1);
   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

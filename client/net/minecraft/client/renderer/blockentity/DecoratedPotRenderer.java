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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
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

public class DecoratedPotRenderer implements BlockEntityRenderer<DecoratedPotBlockEntity> {
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

   public void submit(DecoratedPotBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      var3.pushPose();
      Direction var9 = var1.getDirection();
      var3.translate(0.5, 0.0, 0.5);
      var3.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F - var9.toYRot()));
      var3.translate(-0.5, 0.0, -0.5);
      DecoratedPotBlockEntity.WobbleStyle var10 = var1.lastWobbleStyle;
      if (var10 != null && var1.getLevel() != null) {
         float var11 = ((float)(var1.getLevel().getGameTime() - var1.wobbleStartedAtTick) + var2) / (float)var10.duration;
         if (var11 >= 0.0F && var11 <= 1.0F) {
            if (var10 == DecoratedPotBlockEntity.WobbleStyle.POSITIVE) {
               float var12 = 0.015625F;
               float var13 = var11 * 6.2831855F;
               float var14 = -1.5F * (Mth.cos(var13) + 0.5F) * Mth.sin(var13 / 2.0F);
               var3.rotateAround(Axis.XP.rotation(var14 * 0.015625F), 0.5F, 0.0F, 0.5F);
               float var15 = Mth.sin(var13);
               var3.rotateAround(Axis.ZP.rotation(var15 * 0.015625F), 0.5F, 0.0F, 0.5F);
            } else {
               float var16 = Mth.sin(-var11 * 3.0F * 3.1415927F) * 0.125F;
               float var17 = 1.0F - var11;
               var3.rotateAround(Axis.YP.rotation(var16 * var17), 0.5F, 0.0F, 0.5F);
            }
         }
      }

      this.submit(var3, var8, var4, var5, var1.getDecorations());
      var3.popPose();
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, int var4, PotDecorations var5) {
      RenderType var6 = Sheets.DECORATED_POT_BASE.renderType(RenderType::entitySolid);
      TextureAtlasSprite var7 = this.materials.get(Sheets.DECORATED_POT_BASE);
      var2.submitModelPart(this.neck, var1, var6, var3, var4, var7);
      var2.submitModelPart(this.top, var1, var6, var3, var4, var7);
      var2.submitModelPart(this.bottom, var1, var6, var3, var4, var7);
      Material var8 = getSideMaterial(var5.front());
      var2.submitModelPart(this.frontSide, var1, var8.renderType(RenderType::entitySolid), var3, var4, this.materials.get(var8));
      Material var9 = getSideMaterial(var5.back());
      var2.submitModelPart(this.backSide, var1, var9.renderType(RenderType::entitySolid), var3, var4, this.materials.get(var9));
      Material var10 = getSideMaterial(var5.left());
      var2.submitModelPart(this.leftSide, var1, var10.renderType(RenderType::entitySolid), var3, var4, this.materials.get(var10));
      Material var11 = getSideMaterial(var5.right());
      var2.submitModelPart(this.rightSide, var1, var11.renderType(RenderType::entitySolid), var3, var4, this.materials.get(var11));
   }

   public void getExtents(Set<Vector3f> var1) {
      PoseStack var2 = new PoseStack();
      this.neck.getExtentsForGui(var2, var1);
      this.top.getExtentsForGui(var2, var1);
      this.bottom.getExtentsForGui(var2, var1);
   }
}

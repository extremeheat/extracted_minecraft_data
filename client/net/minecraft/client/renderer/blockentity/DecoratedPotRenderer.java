package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.EnumSet;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;

public class DecoratedPotRenderer implements BlockEntityRenderer<DecoratedPotBlockEntity> {
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
   private final Material baseMaterial = Objects.requireNonNull(Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.BASE));

   public DecoratedPotRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      ModelPart var2 = var1.bakeLayer(ModelLayers.DECORATED_POT_BASE);
      this.neck = var2.getChild("neck");
      this.top = var2.getChild("top");
      this.bottom = var2.getChild("bottom");
      ModelPart var3 = var1.bakeLayer(ModelLayers.DECORATED_POT_SIDES);
      this.frontSide = var3.getChild("front");
      this.backSide = var3.getChild("back");
      this.leftSide = var3.getChild("left");
      this.rightSide = var3.getChild("right");
   }

   public static LayerDefinition createBaseLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      CubeDeformation var2 = new CubeDeformation(0.2F);
      CubeDeformation var3 = new CubeDeformation(-0.1F);
      var1.addOrReplaceChild(
         "neck",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(4.0F, 17.0F, 4.0F, 8.0F, 3.0F, 8.0F, var3)
            .texOffs(0, 5)
            .addBox(5.0F, 20.0F, 5.0F, 6.0F, 1.0F, 6.0F, var2),
         PartPose.offsetAndRotation(0.0F, 37.0F, 16.0F, 3.1415927F, 0.0F, 0.0F)
      );
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

   @Nullable
   private static Material getMaterial(Item var0) {
      Material var1 = Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.getResourceKey(var0));
      if (var1 == null) {
         var1 = Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.getResourceKey(Items.BRICK));
      }

      return var1;
   }

   public void render(DecoratedPotBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      var3.pushPose();
      Direction var7 = var1.getDirection();
      var3.translate(0.5, 0.0, 0.5);
      var3.mulPose(Axis.YP.rotationDegrees(180.0F - var7.toYRot()));
      var3.translate(-0.5, 0.0, -0.5);
      VertexConsumer var8 = this.baseMaterial.buffer(var4, RenderType::entitySolid);
      this.neck.render(var3, var8, var5, var6);
      this.top.render(var3, var8, var5, var6);
      this.bottom.render(var3, var8, var5, var6);
      DecoratedPotBlockEntity.Decorations var9 = var1.getDecorations();
      this.renderSide(this.frontSide, var3, var4, var5, var6, getMaterial(var9.front()));
      this.renderSide(this.backSide, var3, var4, var5, var6, getMaterial(var9.back()));
      this.renderSide(this.leftSide, var3, var4, var5, var6, getMaterial(var9.left()));
      this.renderSide(this.rightSide, var3, var4, var5, var6, getMaterial(var9.right()));
      var3.popPose();
   }

   private void renderSide(ModelPart var1, PoseStack var2, MultiBufferSource var3, int var4, int var5, @Nullable Material var6) {
      if (var6 == null) {
         var6 = getMaterial(Items.BRICK);
      }

      if (var6 != null) {
         var1.render(var2, var6.buffer(var3, RenderType::entitySolid), var4, var5);
      }
   }
}

package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class SignRenderer extends AbstractSignRenderer {
   public static final float RENDER_SCALE = 0.6666667F;
   private static final Vec3 TEXT_OFFSET = new Vec3(0.0, 0.3333333432674408, 0.046666666865348816);
   private final Map<WoodType, Models> signModels;

   public SignRenderer(BlockEntityRendererProvider.Context var1) {
      super(var1);
      this.signModels = (Map)WoodType.values().collect(ImmutableMap.toImmutableMap((var0) -> var0, (var1x) -> new Models(createSignModel(var1.entityModelSet(), var1x, true), createSignModel(var1.entityModelSet(), var1x, false))));
   }

   protected Model.Simple getSignModel(BlockState var1, WoodType var2) {
      Models var3 = (Models)this.signModels.get(var2);
      return var1.getBlock() instanceof StandingSignBlock ? var3.standing() : var3.wall();
   }

   protected Material getSignMaterial(WoodType var1) {
      return Sheets.getSignMaterial(var1);
   }

   protected float getSignModelRenderScale() {
      return 0.6666667F;
   }

   protected float getSignTextRenderScale() {
      return 0.6666667F;
   }

   private static void translateBase(PoseStack var0, float var1) {
      var0.translate(0.5F, 0.5F, 0.5F);
      var0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var1));
   }

   protected void translateSign(PoseStack var1, float var2, BlockState var3) {
      translateBase(var1, var2);
      if (!(var3.getBlock() instanceof StandingSignBlock)) {
         var1.translate(0.0F, -0.3125F, -0.4375F);
      }

   }

   protected Vec3 getTextOffset() {
      return TEXT_OFFSET;
   }

   public static void submitSpecial(MaterialSet var0, PoseStack var1, SubmitNodeCollector var2, int var3, int var4, Model.Simple var5, Material var6) {
      var1.pushPose();
      applyInHandTransforms(var1);
      Unit var10002 = Unit.INSTANCE;
      Objects.requireNonNull(var5);
      var2.submitModel(var5, var10002, var1, var6.renderType(var5::renderType), var3, var4, -1, var0.get(var6), 0, (ModelFeatureRenderer.CrumblingOverlay)null);
      var1.popPose();
   }

   public static void applyInHandTransforms(PoseStack var0) {
      translateBase(var0, 0.0F);
      var0.scale(0.6666667F, -0.6666667F, -0.6666667F);
   }

   public static Model.Simple createSignModel(EntityModelSet var0, WoodType var1, boolean var2) {
      ModelLayerLocation var3 = var2 ? ModelLayers.createStandingSignModelName(var1) : ModelLayers.createWallSignModelName(var1);
      return new Model.Simple(var0.bakeLayer(var3), RenderTypes::entityCutoutNoCull);
   }

   public static LayerDefinition createSignLayer(boolean var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      var2.addOrReplaceChild("sign", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F), PartPose.ZERO);
      if (var0) {
         var2.addOrReplaceChild("stick", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F), PartPose.ZERO);
      }

      return LayerDefinition.create(var1, 64, 32);
   }

   static record Models(Model.Simple standing, Model.Simple wall) {
      Models(Model.Simple var1, Model.Simple var2) {
         super();
         this.standing = var1;
         this.wall = var2;
      }
   }
}

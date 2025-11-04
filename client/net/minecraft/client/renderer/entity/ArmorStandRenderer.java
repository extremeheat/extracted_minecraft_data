package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

public class ArmorStandRenderer extends LivingEntityRenderer<ArmorStand, ArmorStandRenderState, ArmorStandArmorModel> {
   public static final Identifier DEFAULT_SKIN_LOCATION = Identifier.withDefaultNamespace("textures/entity/armorstand/wood.png");
   private final ArmorStandArmorModel bigModel = (ArmorStandArmorModel)this.getModel();
   private final ArmorStandArmorModel smallModel;

   public ArmorStandRenderer(EntityRendererProvider.Context var1) {
      super(var1, new ArmorStandModel(var1.bakeLayer(ModelLayers.ARMOR_STAND)), 0.0F);
      this.smallModel = new ArmorStandModel(var1.bakeLayer(ModelLayers.ARMOR_STAND_SMALL));
      this.addLayer(new HumanoidArmorLayer(this, ArmorModelSet.bake(ModelLayers.ARMOR_STAND_ARMOR, var1.getModelSet(), ArmorStandArmorModel::new), ArmorModelSet.bake(ModelLayers.ARMOR_STAND_SMALL_ARMOR, var1.getModelSet(), ArmorStandArmorModel::new), var1.getEquipmentRenderer()));
      this.addLayer(new ItemInHandLayer(this));
      this.addLayer(new WingsLayer(this, var1.getModelSet(), var1.getEquipmentRenderer()));
      this.addLayer(new CustomHeadLayer(this, var1.getModelSet(), var1.getPlayerSkinRenderCache()));
   }

   public Identifier getTextureLocation(ArmorStandRenderState var1) {
      return DEFAULT_SKIN_LOCATION;
   }

   public ArmorStandRenderState createRenderState() {
      return new ArmorStandRenderState();
   }

   public void extractRenderState(ArmorStand var1, ArmorStandRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      HumanoidMobRenderer.extractHumanoidRenderState(var1, var2, var3, this.itemModelResolver);
      var2.yRot = Mth.rotLerp(var3, var1.yRotO, var1.getYRot());
      var2.isMarker = var1.isMarker();
      var2.isSmall = var1.isSmall();
      var2.showArms = var1.showArms();
      var2.showBasePlate = var1.showBasePlate();
      var2.bodyPose = var1.getBodyPose();
      var2.headPose = var1.getHeadPose();
      var2.leftArmPose = var1.getLeftArmPose();
      var2.rightArmPose = var1.getRightArmPose();
      var2.leftLegPose = var1.getLeftLegPose();
      var2.rightLegPose = var1.getRightLegPose();
      var2.wiggle = (float)(var1.level().getGameTime() - var1.lastHit) + var3;
   }

   public void submit(ArmorStandRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      this.model = var1.isSmall ? this.smallModel : this.bigModel;
      super.submit(var1, var2, var3, var4);
   }

   protected void setupRotations(ArmorStandRenderState var1, PoseStack var2, float var3, float var4) {
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F - var3));
      if (var1.wiggle < 5.0F) {
         var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(Mth.sin((double)(var1.wiggle / 1.5F * 3.1415927F)) * 3.0F));
      }

   }

   protected boolean shouldShowName(ArmorStand var1, double var2) {
      return var1.isCustomNameVisible();
   }

   protected @Nullable RenderType getRenderType(ArmorStandRenderState var1, boolean var2, boolean var3, boolean var4) {
      if (!var1.isMarker) {
         return super.getRenderType(var1, var2, var3, var4);
      } else {
         Identifier var5 = this.getTextureLocation(var1);
         if (var3) {
            return RenderTypes.entityTranslucent(var5, false);
         } else {
            return var2 ? RenderTypes.entityCutoutNoCull(var5, false) : null;
         }
      }
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((ArmorStandRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

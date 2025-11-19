package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.zombie.DrownedModel;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DrownedRenderer extends AbstractZombieRenderer<Drowned, ZombieRenderState, DrownedModel> {
   private static final Identifier DROWNED_LOCATION = Identifier.withDefaultNamespace("textures/entity/zombie/drowned.png");

   public DrownedRenderer(EntityRendererProvider.Context var1) {
      super(var1, new DrownedModel(var1.bakeLayer(ModelLayers.DROWNED)), new DrownedModel(var1.bakeLayer(ModelLayers.DROWNED_BABY)), ArmorModelSet.bake(ModelLayers.DROWNED_ARMOR, var1.getModelSet(), DrownedModel::new), ArmorModelSet.bake(ModelLayers.DROWNED_BABY_ARMOR, var1.getModelSet(), DrownedModel::new));
      this.addLayer(new DrownedOuterLayer(this, var1.getModelSet()));
   }

   public ZombieRenderState createRenderState() {
      return new ZombieRenderState();
   }

   public Identifier getTextureLocation(ZombieRenderState var1) {
      return DROWNED_LOCATION;
   }

   protected void setupRotations(ZombieRenderState var1, PoseStack var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      float var5 = var1.swimAmount;
      if (var5 > 0.0F) {
         float var6 = -10.0F - var1.xRot;
         float var7 = Mth.lerp(var5, 0.0F, var6);
         var2.rotateAround(Axis.XP.rotationDegrees(var7), 0.0F, var1.boundingBoxHeight / 2.0F / var4, 0.0F);
      }

   }

   protected HumanoidModel.ArmPose getArmPose(Drowned var1, HumanoidArm var2) {
      ItemStack var3 = var1.getItemHeldByArm(var2);
      return var1.getMainArm() == var2 && var1.isAggressive() && var3.is(Items.TRIDENT) ? HumanoidModel.ArmPose.THROW_TRIDENT : super.getArmPose(var1, var2);
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((ZombieRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

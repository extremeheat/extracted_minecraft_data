package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Random;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CarriedBlockLayer;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EndermanRenderer extends MobRenderer<EnderMan, EndermanModel<EnderMan>> {
   private static final ResourceLocation ENDERMAN_LOCATION = new ResourceLocation("textures/entity/enderman/enderman.png");
   private final Random random = new Random();

   public EndermanRenderer(EntityRendererProvider.Context var1) {
      super(var1, new EndermanModel(var1.bakeLayer(ModelLayers.ENDERMAN)), 0.5F);
      this.addLayer(new EnderEyesLayer(this));
      this.addLayer(new CarriedBlockLayer(this));
   }

   public void render(EnderMan var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      BlockState var7 = var1.getCarriedBlock();
      EndermanModel var8 = (EndermanModel)this.getModel();
      var8.carrying = var7 != null;
      var8.creepy = var1.isCreepy();
      super.render((Mob)var1, var2, var3, var4, var5, var6);
   }

   public Vec3 getRenderOffset(EnderMan var1, float var2) {
      if (var1.isCreepy()) {
         double var3 = 0.02D;
         return new Vec3(this.random.nextGaussian() * 0.02D, 0.0D, this.random.nextGaussian() * 0.02D);
      } else {
         return super.getRenderOffset(var1, var2);
      }
   }

   public ResourceLocation getTextureLocation(EnderMan var1) {
      return ENDERMAN_LOCATION;
   }
}

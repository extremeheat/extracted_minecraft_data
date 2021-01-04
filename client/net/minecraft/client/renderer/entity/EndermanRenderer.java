package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.entity.layers.CarriedBlockLayer;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.state.BlockState;

public class EndermanRenderer extends MobRenderer<EnderMan, EndermanModel<EnderMan>> {
   private static final ResourceLocation ENDERMAN_LOCATION = new ResourceLocation("textures/entity/enderman/enderman.png");
   private final Random random = new Random();

   public EndermanRenderer(EntityRenderDispatcher var1) {
      super(var1, new EndermanModel(0.0F), 0.5F);
      this.addLayer(new EnderEyesLayer(this));
      this.addLayer(new CarriedBlockLayer(this));
   }

   public void render(EnderMan var1, double var2, double var4, double var6, float var8, float var9) {
      BlockState var10 = var1.getCarriedBlock();
      EndermanModel var11 = (EndermanModel)this.getModel();
      var11.carrying = var10 != null;
      var11.creepy = var1.isCreepy();
      if (var1.isCreepy()) {
         double var12 = 0.02D;
         var2 += this.random.nextGaussian() * 0.02D;
         var6 += this.random.nextGaussian() * 0.02D;
      }

      super.render((Mob)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(EnderMan var1) {
      return ENDERMAN_LOCATION;
   }
}

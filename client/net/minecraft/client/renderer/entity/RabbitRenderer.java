package net.minecraft.client.renderer.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Rabbit;

public class RabbitRenderer extends MobRenderer<Rabbit, RabbitModel<Rabbit>> {
   private static final ResourceLocation RABBIT_BROWN_LOCATION = new ResourceLocation("textures/entity/rabbit/brown.png");
   private static final ResourceLocation RABBIT_WHITE_LOCATION = new ResourceLocation("textures/entity/rabbit/white.png");
   private static final ResourceLocation RABBIT_BLACK_LOCATION = new ResourceLocation("textures/entity/rabbit/black.png");
   private static final ResourceLocation RABBIT_GOLD_LOCATION = new ResourceLocation("textures/entity/rabbit/gold.png");
   private static final ResourceLocation RABBIT_SALT_LOCATION = new ResourceLocation("textures/entity/rabbit/salt.png");
   private static final ResourceLocation RABBIT_WHITE_SPLOTCHED_LOCATION = new ResourceLocation("textures/entity/rabbit/white_splotched.png");
   private static final ResourceLocation RABBIT_TOAST_LOCATION = new ResourceLocation("textures/entity/rabbit/toast.png");
   private static final ResourceLocation RABBIT_EVIL_LOCATION = new ResourceLocation("textures/entity/rabbit/caerbannog.png");

   public RabbitRenderer(EntityRendererProvider.Context var1) {
      super(var1, new RabbitModel(var1.bakeLayer(ModelLayers.RABBIT)), 0.3F);
   }

   public ResourceLocation getTextureLocation(Rabbit var1) {
      String var2 = ChatFormatting.stripFormatting(var1.getName().getString());
      if (var2 != null && "Toast".equals(var2)) {
         return RABBIT_TOAST_LOCATION;
      } else {
         switch(var1.getRabbitType()) {
         case 0:
         default:
            return RABBIT_BROWN_LOCATION;
         case 1:
            return RABBIT_WHITE_LOCATION;
         case 2:
            return RABBIT_BLACK_LOCATION;
         case 3:
            return RABBIT_WHITE_SPLOTCHED_LOCATION;
         case 4:
            return RABBIT_GOLD_LOCATION;
         case 5:
            return RABBIT_SALT_LOCATION;
         case 99:
            return RABBIT_EVIL_LOCATION;
         }
      }
   }
}

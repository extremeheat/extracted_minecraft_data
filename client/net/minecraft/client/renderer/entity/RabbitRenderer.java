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
      if ("Toast".equals(var2)) {
         return RABBIT_TOAST_LOCATION;
      } else {
         ResourceLocation var10000;
         switch (var1.getVariant()) {
            case BROWN -> var10000 = RABBIT_BROWN_LOCATION;
            case WHITE -> var10000 = RABBIT_WHITE_LOCATION;
            case BLACK -> var10000 = RABBIT_BLACK_LOCATION;
            case GOLD -> var10000 = RABBIT_GOLD_LOCATION;
            case SALT -> var10000 = RABBIT_SALT_LOCATION;
            case WHITE_SPLOTCHED -> var10000 = RABBIT_WHITE_SPLOTCHED_LOCATION;
            case EVIL -> var10000 = RABBIT_EVIL_LOCATION;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }
}

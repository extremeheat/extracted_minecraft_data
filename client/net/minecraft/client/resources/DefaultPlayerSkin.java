package net.minecraft.client.resources;

import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public class DefaultPlayerSkin {
   private static final ResourceLocation STEVE_SKIN_LOCATION = new ResourceLocation("textures/entity/steve.png");
   private static final ResourceLocation ALEX_SKIN_LOCATION = new ResourceLocation("textures/entity/alex.png");
   private static final String STEVE_MODEL = "default";
   private static final String ALEX_MODEL = "slim";

   public DefaultPlayerSkin() {
      super();
   }

   public static ResourceLocation getDefaultSkin() {
      return STEVE_SKIN_LOCATION;
   }

   public static ResourceLocation getDefaultSkin(UUID var0) {
      return isAlexDefault(var0) ? ALEX_SKIN_LOCATION : STEVE_SKIN_LOCATION;
   }

   public static String getSkinModelName(UUID var0) {
      return isAlexDefault(var0) ? "slim" : "default";
   }

   private static boolean isAlexDefault(UUID var0) {
      return (var0.hashCode() & 1) == 1;
   }
}

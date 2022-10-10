package net.minecraft.client.resources;

import java.util.UUID;
import net.minecraft.util.ResourceLocation;

public class DefaultPlayerSkin {
   private static final ResourceLocation field_177337_a = new ResourceLocation("textures/entity/steve.png");
   private static final ResourceLocation field_177336_b = new ResourceLocation("textures/entity/alex.png");

   public static ResourceLocation func_177335_a() {
      return field_177337_a;
   }

   public static ResourceLocation func_177334_a(UUID var0) {
      return func_177333_c(var0) ? field_177336_b : field_177337_a;
   }

   public static String func_177332_b(UUID var0) {
      return func_177333_c(var0) ? "slim" : "default";
   }

   private static boolean func_177333_c(UUID var0) {
      return (var0.hashCode() & 1) == 1;
   }
}

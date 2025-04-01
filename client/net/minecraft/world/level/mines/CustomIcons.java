package net.minecraft.world.level.mines;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class CustomIcons {
   public static Set<ResourceLocation> ICONS = new HashSet();

   public CustomIcons() {
      super();
   }

   public static ResourceLocation register(String var0) {
      ResourceLocation var1 = ResourceLocation.withDefaultNamespace(var0);
      ICONS.add(var1);
      return var1;
   }
}

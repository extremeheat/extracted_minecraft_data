package net.minecraft.client.resources;

import com.google.common.cache.CacheLoader;
import com.mojang.authlib.GameProfile;
import java.util.Map;
import net.minecraft.client.Minecraft;

class SkinManager$1 extends CacheLoader {
   SkinManager$1(SkinManager var1) {
      super();
      this.field_152787_a = var1;
   }

   public Map func_152786_a(GameProfile var1) {
      return Minecraft.func_71410_x().func_152347_ac().getTextures(var1, false);
   }
}

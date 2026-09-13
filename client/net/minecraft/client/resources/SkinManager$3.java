package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import java.util.HashMap;
import net.minecraft.client.Minecraft;

class SkinManager$3 implements Runnable {
   SkinManager$3(SkinManager var1, GameProfile var2, boolean var3, SkinManager$SkinAvailableCallback var4) {
      super();
      this.field_152802_d = var1;
      this.field_152799_a = var2;
      this.field_152800_b = var3;
      this.field_152801_c = var4;
   }

   @Override
   public void run() {
      HashMap var1 = Maps.newHashMap();

      try {
         var1.putAll(SkinManager.access$000(this.field_152802_d).getTextures(this.field_152799_a, this.field_152800_b));
      } catch (InsecureTextureException var3) {
      }

      if (var1.isEmpty() && this.field_152799_a.getId().equals(Minecraft.func_71410_x().func_110432_I().func_148256_e().getId())) {
         var1.putAll(
            SkinManager.access$000(this.field_152802_d)
               .getTextures(SkinManager.access$000(this.field_152802_d).fillProfileProperties(this.field_152799_a, false), false)
         );
      }

      Minecraft.func_71410_x().func_152344_a(new SkinManager$3$1(this, var1));
   }
}

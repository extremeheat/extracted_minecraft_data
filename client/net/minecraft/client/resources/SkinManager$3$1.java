package net.minecraft.client.resources;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;

class SkinManager$3$1 implements Runnable {
   SkinManager$3$1(SkinManager$3 var1, Map var2) {
      super();
      this.field_152804_b = var1;
      this.field_152803_a = var2;
   }

   @Override
   public void run() {
      if (this.field_152803_a.containsKey(Type.SKIN)) {
         this.field_152804_b
            .field_152802_d
            .func_152789_a((MinecraftProfileTexture)this.field_152803_a.get(Type.SKIN), Type.SKIN, this.field_152804_b.field_152801_c);
      }

      if (this.field_152803_a.containsKey(Type.CAPE)) {
         this.field_152804_b
            .field_152802_d
            .func_152789_a((MinecraftProfileTexture)this.field_152803_a.get(Type.CAPE), Type.CAPE, this.field_152804_b.field_152801_c);
      }
   }
}

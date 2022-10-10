package net.minecraft.client.audio;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.SoundEvents;

public class UnderwaterAmbientSoundHandler implements IAmbientSoundHandler {
   private final EntityPlayerSP field_204254_a;
   private final SoundHandler field_204255_b;
   private int field_204256_c = 0;

   public UnderwaterAmbientSoundHandler(EntityPlayerSP var1, SoundHandler var2) {
      super();
      this.field_204254_a = var1;
      this.field_204255_b = var2;
   }

   public void func_204253_a() {
      --this.field_204256_c;
      if (this.field_204256_c <= 0 && this.field_204254_a.func_204231_K()) {
         float var1 = this.field_204254_a.field_70170_p.field_73012_v.nextFloat();
         if (var1 < 1.0E-4F) {
            this.field_204256_c = 0;
            this.field_204255_b.func_147682_a(new UnderwaterAmbientSounds.SubSound(this.field_204254_a, SoundEvents.field_204410_e));
         } else if (var1 < 0.001F) {
            this.field_204256_c = 0;
            this.field_204255_b.func_147682_a(new UnderwaterAmbientSounds.SubSound(this.field_204254_a, SoundEvents.field_204325_d));
         } else if (var1 < 0.01F) {
            this.field_204256_c = 0;
            this.field_204255_b.func_147682_a(new UnderwaterAmbientSounds.SubSound(this.field_204254_a, SoundEvents.field_204324_c));
         }
      }

   }
}

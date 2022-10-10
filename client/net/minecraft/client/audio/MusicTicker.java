package net.minecraft.client.audio;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;

public class MusicTicker implements ITickable {
   private final Random field_147679_a = new Random();
   private final Minecraft field_147677_b;
   private ISound field_147678_c;
   private int field_147676_d = 100;
   private boolean field_209508_e;

   public MusicTicker(Minecraft var1) {
      super();
      this.field_147677_b = var1;
   }

   public void func_73660_a() {
      MusicTicker.MusicType var1 = this.field_147677_b.func_147109_W();
      if (this.field_147678_c != null) {
         if (!var1.func_188768_a().func_187503_a().equals(this.field_147678_c.func_147650_b())) {
            this.field_147677_b.func_147118_V().func_147683_b(this.field_147678_c);
            this.field_147676_d = MathHelper.func_76136_a(this.field_147679_a, 0, var1.func_148634_b() / 2);
            this.field_209508_e = false;
         }

         if (!this.field_209508_e && !this.field_147677_b.func_147118_V().func_147692_c(this.field_147678_c)) {
            this.field_147678_c = null;
            this.field_147676_d = Math.min(MathHelper.func_76136_a(this.field_147679_a, var1.func_148634_b(), var1.func_148633_c()), this.field_147676_d);
         } else if (this.field_147677_b.func_147118_V().func_147692_c(this.field_147678_c)) {
            this.field_209508_e = false;
         }
      }

      this.field_147676_d = Math.min(this.field_147676_d, var1.func_148633_c());
      if (this.field_147678_c == null && this.field_147676_d-- <= 0) {
         this.func_181558_a(var1);
      }

   }

   public void func_181558_a(MusicTicker.MusicType var1) {
      this.field_147678_c = SimpleSound.func_184370_a(var1.func_188768_a());
      this.field_147677_b.func_147118_V().func_147682_a(this.field_147678_c);
      this.field_147676_d = 2147483647;
      this.field_209508_e = true;
   }

   public void func_209200_a() {
      if (this.field_147678_c != null) {
         this.field_147677_b.func_147118_V().func_147683_b(this.field_147678_c);
         this.field_147678_c = null;
         this.field_147676_d = 0;
         this.field_209508_e = false;
      }

   }

   public boolean func_209100_b(MusicTicker.MusicType var1) {
      return this.field_147678_c == null ? false : var1.func_188768_a().func_187503_a().equals(this.field_147678_c.func_147650_b());
   }

   public static enum MusicType {
      MENU(SoundEvents.field_187671_dC, 20, 600),
      GAME(SoundEvents.field_187669_dB, 12000, 24000),
      CREATIVE(SoundEvents.field_187792_dx, 1200, 3600),
      CREDITS(SoundEvents.field_187794_dy, 0, 0),
      NETHER(SoundEvents.field_187673_dD, 1200, 3600),
      END_BOSS(SoundEvents.field_187796_dz, 0, 0),
      END(SoundEvents.field_187667_dA, 6000, 24000),
      UNDER_WATER(SoundEvents.field_209163_fp, 12000, 24000);

      private final SoundEvent field_148645_h;
      private final int field_148646_i;
      private final int field_148643_j;

      private MusicType(SoundEvent var3, int var4, int var5) {
         this.field_148645_h = var3;
         this.field_148646_i = var4;
         this.field_148643_j = var5;
      }

      public SoundEvent func_188768_a() {
         return this.field_148645_h;
      }

      public int func_148634_b() {
         return this.field_148646_i;
      }

      public int func_148633_c() {
         return this.field_148643_j;
      }
   }
}

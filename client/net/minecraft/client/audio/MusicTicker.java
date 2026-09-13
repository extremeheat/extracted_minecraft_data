package net.minecraft.client.audio;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.MathHelper;

public class MusicTicker implements IUpdatePlayerListBox {
   private final Random field_147679_a = new Random();
   private final Minecraft field_147677_b;
   private ISound field_147678_c;
   private int field_147676_d = 100;

   public MusicTicker(Minecraft var1) {
      super();
      this.field_147677_b = var1;
   }

   @Override
   public void func_73660_a() {
      MusicTicker$MusicType var1 = this.field_147677_b.func_147109_W();
      if (this.field_147678_c != null) {
         if (!var1.func_148635_a().equals(this.field_147678_c.func_147650_b())) {
            this.field_147677_b.func_147118_V().func_147683_b(this.field_147678_c);
            this.field_147676_d = MathHelper.func_76136_a(this.field_147679_a, 0, var1.func_148634_b() / 2);
         }

         if (!this.field_147677_b.func_147118_V().func_147692_c(this.field_147678_c)) {
            this.field_147678_c = null;
            this.field_147676_d = Math.min(MathHelper.func_76136_a(this.field_147679_a, var1.func_148634_b(), var1.func_148633_c()), this.field_147676_d);
         }
      }

      if (this.field_147678_c == null && this.field_147676_d-- <= 0) {
         this.field_147678_c = PositionedSoundRecord.func_147673_a(var1.func_148635_a());
         this.field_147677_b.func_147118_V().func_147682_a(this.field_147678_c);
         this.field_147676_d = 2147483647;
      }
   }
}

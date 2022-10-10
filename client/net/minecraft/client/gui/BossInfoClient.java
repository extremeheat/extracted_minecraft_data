package net.minecraft.client.gui;

import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;

public class BossInfoClient extends BossInfo {
   protected float field_186766_h;
   protected long field_186767_i;

   public BossInfoClient(SPacketUpdateBossInfo var1) {
      super(var1.func_186908_a(), var1.func_186907_c(), var1.func_186900_e(), var1.func_186904_f());
      this.field_186766_h = var1.func_186906_d();
      this.field_186750_b = var1.func_186906_d();
      this.field_186767_i = Util.func_211177_b();
      this.func_186741_a(var1.func_186909_g());
      this.func_186742_b(var1.func_186910_h());
      this.func_186743_c(var1.func_186901_i());
   }

   public void func_186735_a(float var1) {
      this.field_186750_b = this.func_186738_f();
      this.field_186766_h = var1;
      this.field_186767_i = Util.func_211177_b();
   }

   public float func_186738_f() {
      long var1 = Util.func_211177_b() - this.field_186767_i;
      float var3 = MathHelper.func_76131_a((float)var1 / 100.0F, 0.0F, 1.0F);
      return this.field_186750_b + (this.field_186766_h - this.field_186750_b) * var3;
   }

   public void func_186765_a(SPacketUpdateBossInfo var1) {
      switch(var1.func_186902_b()) {
      case UPDATE_NAME:
         this.func_186739_a(var1.func_186907_c());
         break;
      case UPDATE_PCT:
         this.func_186735_a(var1.func_186906_d());
         break;
      case UPDATE_STYLE:
         this.func_186745_a(var1.func_186900_e());
         this.func_186746_a(var1.func_186904_f());
         break;
      case UPDATE_PROPERTIES:
         this.func_186741_a(var1.func_186909_g());
         this.func_186742_b(var1.func_186910_h());
      }

   }
}

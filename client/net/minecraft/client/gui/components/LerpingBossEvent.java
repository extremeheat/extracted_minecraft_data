package net.minecraft.client.gui.components;

import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.phys.Vec3;

public class LerpingBossEvent extends BossEvent {
   private static final long LERP_MILLISECONDS = 100L;
   protected float targetPercent;
   protected long setTime;

   public LerpingBossEvent(
      UUID var1,
      Component var2,
      float var3,
      BossEvent.BossBarColor var4,
      BossEvent.BossBarOverlay var5,
      boolean var6,
      boolean var7,
      boolean var8,
      Vec3 var9,
      int var10
   ) {
      super(var1, var2, var4, var5, var9, var10);
      this.targetPercent = var3;
      this.progress = var3;
      this.setTime = Util.getMillis();
      this.setDarkenScreen(var6);
      this.setPlayBossMusic(var7);
      this.setCreateWorldFog(var8);
   }

   @Override
   public void setProgress(float var1) {
      this.progress = this.getProgress();
      this.targetPercent = var1;
      this.setTime = Util.getMillis();
   }

   @Override
   public float getProgress() {
      long var1 = Util.getMillis() - this.setTime;
      float var3 = Mth.clamp((float)var1 / 100.0F, 0.0F, 1.0F);
      return Mth.lerp(var3, this.progress, this.targetPercent);
   }

   public boolean isActiveFor(Vec3 var1) {
      if (this.radius > 0) {
         return this.center.distanceToSqr(var1) < (double)(this.radius * this.radius);
      } else {
         return this.radius >= 0;
      }
   }
}

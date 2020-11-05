package net.minecraft.client.gui.components;

import net.minecraft.Util;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

public class LerpingBossEvent extends BossEvent {
   protected float targetPercent;
   protected long setTime;

   public LerpingBossEvent(ClientboundBossEventPacket var1) {
      super(var1.getId(), var1.getName(), var1.getColor(), var1.getOverlay());
      this.targetPercent = var1.getPercent();
      this.percent = var1.getPercent();
      this.setTime = Util.getMillis();
      this.setDarkenScreen(var1.shouldDarkenScreen());
      this.setPlayBossMusic(var1.shouldPlayMusic());
      this.setCreateWorldFog(var1.shouldCreateWorldFog());
   }

   public void setPercent(float var1) {
      this.percent = this.getPercent();
      this.targetPercent = var1;
      this.setTime = Util.getMillis();
   }

   public float getPercent() {
      long var1 = Util.getMillis() - this.setTime;
      float var3 = Mth.clamp((float)var1 / 100.0F, 0.0F, 1.0F);
      return Mth.lerp(var3, this.percent, this.targetPercent);
   }

   public void update(ClientboundBossEventPacket var1) {
      switch(var1.getOperation()) {
      case UPDATE_NAME:
         this.setName(var1.getName());
         break;
      case UPDATE_PCT:
         this.setPercent(var1.getPercent());
         break;
      case UPDATE_STYLE:
         this.setColor(var1.getColor());
         this.setOverlay(var1.getOverlay());
         break;
      case UPDATE_PROPERTIES:
         this.setDarkenScreen(var1.shouldDarkenScreen());
         this.setPlayBossMusic(var1.shouldPlayMusic());
      }

   }
}

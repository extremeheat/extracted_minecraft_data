package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;

public record ServerHitboxesRenderState(boolean missing, double serverEntityX, double serverEntityY, double serverEntityZ, double deltaMovementX, double deltaMovementY, double deltaMovementZ, float eyeHeight, @Nullable HitboxesRenderState hitboxes) {
   public ServerHitboxesRenderState(boolean var1) {
      this(var1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0F, (HitboxesRenderState)null);
   }

   public ServerHitboxesRenderState(boolean var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14, @Nullable HitboxesRenderState var15) {
      super();
      this.missing = var1;
      this.serverEntityX = var2;
      this.serverEntityY = var4;
      this.serverEntityZ = var6;
      this.deltaMovementX = var8;
      this.deltaMovementY = var10;
      this.deltaMovementZ = var12;
      this.eyeHeight = var14;
      this.hitboxes = var15;
   }
}

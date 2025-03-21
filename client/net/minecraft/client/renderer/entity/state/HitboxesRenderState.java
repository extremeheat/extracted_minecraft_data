package net.minecraft.client.renderer.entity.state;

import com.google.common.collect.ImmutableList;

public record HitboxesRenderState(double viewX, double viewY, double viewZ, ImmutableList<HitboxRenderState> hitboxes) {
   public HitboxesRenderState(double var1, double var3, double var5, ImmutableList<HitboxRenderState> var7) {
      super();
      this.viewX = var1;
      this.viewY = var3;
      this.viewZ = var5;
      this.hitboxes = var7;
   }
}

package com.mojang.blaze3d.audio;

import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL10;

public class Listener {
   private float gain = 1.0F;
   private ListenerTransform transform;

   public Listener() {
      super();
      this.transform = ListenerTransform.INITIAL;
   }

   public void setTransform(ListenerTransform var1) {
      this.transform = var1;
      Vec3 var2 = var1.position();
      Vec3 var3 = var1.forward();
      Vec3 var4 = var1.up();
      AL10.alListener3f(4100, (float)var2.x, (float)var2.y, (float)var2.z);
      AL10.alListenerfv(4111, new float[]{(float)var3.x, (float)var3.y, (float)var3.z, (float)var4.x(), (float)var4.y(), (float)var4.z()});
   }

   public void setGain(float var1) {
      AL10.alListenerf(4106, var1);
      this.gain = var1;
   }

   public float getGain() {
      return this.gain;
   }

   public void reset() {
      this.setTransform(ListenerTransform.INITIAL);
   }

   public ListenerTransform getTransform() {
      return this.transform;
   }
}

package com.mojang.blaze3d.audio;

import com.mojang.math.Vector3f;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL10;

public class Listener {
   private float gain = 1.0F;

   public void setListenerPosition(Vec3 var1) {
      AL10.alListener3f(4100, (float)var1.x, (float)var1.y, (float)var1.z);
   }

   public void setListenerOrientation(Vector3f var1, Vector3f var2) {
      AL10.alListenerfv(4111, new float[]{var1.x(), var1.y(), var1.z(), var2.x(), var2.y(), var2.z()});
   }

   public void setGain(float var1) {
      AL10.alListenerf(4106, var1);
      this.gain = var1;
   }

   public float getGain() {
      return this.gain;
   }

   public void reset() {
      this.setListenerPosition(Vec3.ZERO);
      this.setListenerOrientation(Vector3f.ZN, Vector3f.YP);
   }
}

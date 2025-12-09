package net.minecraft.client.renderer.fog.environment;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.jspecify.annotations.Nullable;

public abstract class FogEnvironment {
   public FogEnvironment() {
      super();
   }

   public abstract void setupFog(FogData var1, Camera var2, ClientLevel var3, float var4, DeltaTracker var5);

   public boolean providesColor() {
      return true;
   }

   public int getBaseColor(ClientLevel var1, Camera var2, int var3, float var4) {
      return -1;
   }

   public boolean modifiesDarkness() {
      return false;
   }

   public float getModifiedDarkness(LivingEntity var1, float var2, float var3) {
      return var2;
   }

   public abstract boolean isApplicable(@Nullable FogType var1, Entity var2);
}

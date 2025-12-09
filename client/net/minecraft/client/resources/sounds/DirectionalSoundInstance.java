package net.minecraft.client.resources.sounds;

import net.minecraft.client.Camera;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class DirectionalSoundInstance extends AbstractTickableSoundInstance {
   private final Camera camera;
   private final float xAngle;
   private final float yAngle;

   public DirectionalSoundInstance(SoundEvent var1, SoundSource var2, RandomSource var3, Camera var4, float var5, float var6) {
      super(var1, var2, var3);
      this.camera = var4;
      this.xAngle = var5;
      this.yAngle = var6;
      this.setPosition();
   }

   private void setPosition() {
      Vec3 var1 = Vec3.directionFromRotation(this.xAngle, this.yAngle).scale(10.0);
      this.x = this.camera.position().x + var1.x;
      this.y = this.camera.position().y + var1.y;
      this.z = this.camera.position().z + var1.z;
      this.attenuation = SoundInstance.Attenuation.NONE;
   }

   public void tick() {
      this.setPosition();
   }
}

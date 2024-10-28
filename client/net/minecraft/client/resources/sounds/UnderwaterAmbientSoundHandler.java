package net.minecraft.client.resources.sounds;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;

public class UnderwaterAmbientSoundHandler implements AmbientSoundHandler {
   public static final float CHANCE_PER_TICK = 0.01F;
   public static final float RARE_CHANCE_PER_TICK = 0.001F;
   public static final float ULTRA_RARE_CHANCE_PER_TICK = 1.0E-4F;
   private static final int MINIMUM_TICK_DELAY = 0;
   private final LocalPlayer player;
   private final SoundManager soundManager;
   private int tickDelay = 0;

   public UnderwaterAmbientSoundHandler(LocalPlayer var1, SoundManager var2) {
      super();
      this.player = var1;
      this.soundManager = var2;
   }

   public void tick() {
      --this.tickDelay;
      if (this.tickDelay <= 0 && this.player.isUnderWater()) {
         float var1 = this.player.level().random.nextFloat();
         if (var1 < 1.0E-4F) {
            this.tickDelay = 0;
            this.soundManager.play(new UnderwaterAmbientSoundInstances.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE));
         } else if (var1 < 0.001F) {
            this.tickDelay = 0;
            this.soundManager.play(new UnderwaterAmbientSoundInstances.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE));
         } else if (var1 < 0.01F) {
            this.tickDelay = 0;
            this.soundManager.play(new UnderwaterAmbientSoundInstances.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS));
         }
      }

   }
}

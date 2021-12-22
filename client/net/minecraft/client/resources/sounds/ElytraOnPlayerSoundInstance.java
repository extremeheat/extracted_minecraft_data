package net.minecraft.client.resources.sounds;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class ElytraOnPlayerSoundInstance extends AbstractTickableSoundInstance {
   public static final int DELAY = 20;
   private final LocalPlayer player;
   private int time;

   public ElytraOnPlayerSoundInstance(LocalPlayer var1) {
      super(SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS);
      this.player = var1;
      this.looping = true;
      this.delay = 0;
      this.volume = 0.1F;
   }

   public void tick() {
      ++this.time;
      if (!this.player.isRemoved() && (this.time <= 20 || this.player.isFallFlying())) {
         this.x = (double)((float)this.player.getX());
         this.y = (double)((float)this.player.getY());
         this.z = (double)((float)this.player.getZ());
         float var1 = (float)this.player.getDeltaMovement().lengthSqr();
         if ((double)var1 >= 1.0E-7D) {
            this.volume = Mth.clamp(var1 / 4.0F, 0.0F, 1.0F);
         } else {
            this.volume = 0.0F;
         }

         if (this.time < 20) {
            this.volume = 0.0F;
         } else if (this.time < 40) {
            this.volume = (float)((double)this.volume * ((double)(this.time - 20) / 20.0D));
         }

         float var2 = 0.8F;
         if (this.volume > 0.8F) {
            this.pitch = 1.0F + (this.volume - 0.8F);
         } else {
            this.pitch = 1.0F;
         }

      } else {
         this.stop();
      }
   }
}

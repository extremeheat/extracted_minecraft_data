package net.minecraft.client.resources.sounds;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.AmbientAdditionsSettings;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class BiomeAmbientSoundsHandler implements AmbientSoundHandler {
   private static final int LOOP_SOUND_CROSS_FADE_TIME = 40;
   private static final float SKY_MOOD_RECOVERY_RATE = 0.001F;
   private final LocalPlayer player;
   private final SoundManager soundManager;
   private final RandomSource random;
   private final Object2ObjectArrayMap<Holder<SoundEvent>, LoopSoundInstance> loopSounds = new Object2ObjectArrayMap();
   private float moodiness;
   @Nullable
   private Holder<SoundEvent> previousLoopSound;

   public BiomeAmbientSoundsHandler(LocalPlayer var1, SoundManager var2) {
      super();
      this.random = var1.level().getRandom();
      this.player = var1;
      this.soundManager = var2;
   }

   public float getMoodiness() {
      return this.moodiness;
   }

   public void tick() {
      this.loopSounds.values().removeIf(AbstractTickableSoundInstance::isStopped);
      Level var1 = this.player.level();
      EnvironmentAttributeSystem var2 = var1.environmentAttributes();
      AmbientSounds var3 = (AmbientSounds)var2.getValue(EnvironmentAttributes.AMBIENT_SOUNDS, this.player.position());
      Holder var4 = (Holder)var3.loop().orElse((Object)null);
      if (!Objects.equals(var4, this.previousLoopSound)) {
         this.previousLoopSound = var4;
         this.loopSounds.values().forEach(LoopSoundInstance::fadeOut);
         if (var4 != null) {
            this.loopSounds.compute(var4, (var2x, var3x) -> {
               if (var3x == null) {
                  var3x = new LoopSoundInstance((SoundEvent)var4.value());
                  this.soundManager.play(var3x);
               }

               var3x.fadeIn();
               return var3x;
            });
         }
      }

      for(AmbientAdditionsSettings var6 : var3.additions()) {
         if (this.random.nextDouble() < var6.tickChance()) {
            this.soundManager.play(SimpleSoundInstance.forAmbientAddition((SoundEvent)var6.soundEvent().value()));
         }
      }

      var3.mood().ifPresent((var2x) -> {
         int var3 = var2x.blockSearchExtent() * 2 + 1;
         BlockPos var4 = BlockPos.containing(this.player.getX() + (double)this.random.nextInt(var3) - (double)var2x.blockSearchExtent(), this.player.getEyeY() + (double)this.random.nextInt(var3) - (double)var2x.blockSearchExtent(), this.player.getZ() + (double)this.random.nextInt(var3) - (double)var2x.blockSearchExtent());
         int var5 = var1.getBrightness(LightLayer.SKY, var4);
         if (var5 > 0) {
            this.moodiness -= (float)var5 / 15.0F * 0.001F;
         } else {
            this.moodiness -= (float)(var1.getBrightness(LightLayer.BLOCK, var4) - 1) / (float)var2x.tickDelay();
         }

         if (this.moodiness >= 1.0F) {
            double var6 = (double)var4.getX() + 0.5;
            double var8 = (double)var4.getY() + 0.5;
            double var10 = (double)var4.getZ() + 0.5;
            double var12 = var6 - this.player.getX();
            double var14 = var8 - this.player.getEyeY();
            double var16 = var10 - this.player.getZ();
            double var18 = Math.sqrt(var12 * var12 + var14 * var14 + var16 * var16);
            double var20 = var18 + var2x.soundPositionOffset();
            SimpleSoundInstance var22 = SimpleSoundInstance.forAmbientMood((SoundEvent)var2x.soundEvent().value(), this.random, this.player.getX() + var12 / var18 * var20, this.player.getEyeY() + var14 / var18 * var20, this.player.getZ() + var16 / var18 * var20);
            this.soundManager.play(var22);
            this.moodiness = 0.0F;
         } else {
            this.moodiness = Math.max(this.moodiness, 0.0F);
         }

      });
   }

   public static class LoopSoundInstance extends AbstractTickableSoundInstance {
      private int fadeDirection;
      private int fade;

      public LoopSoundInstance(SoundEvent var1) {
         super(var1, SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
         this.looping = true;
         this.delay = 0;
         this.volume = 1.0F;
         this.relative = true;
      }

      public void tick() {
         if (this.fade < 0) {
            this.stop();
         }

         this.fade += this.fadeDirection;
         this.volume = Mth.clamp((float)this.fade / 40.0F, 0.0F, 1.0F);
      }

      public void fadeOut() {
         this.fade = Math.min(this.fade, 40);
         this.fadeDirection = -1;
      }

      public void fadeIn() {
         this.fade = Math.max(0, this.fade);
         this.fadeDirection = 1;
      }
   }
}

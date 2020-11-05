package net.minecraft.client.resources.sounds;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Optional;
import java.util.Random;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;

public class BiomeAmbientSoundsHandler implements AmbientSoundHandler {
   private final LocalPlayer player;
   private final SoundManager soundManager;
   private final BiomeManager biomeManager;
   private final Random random;
   private final Object2ObjectArrayMap<Biome, BiomeAmbientSoundsHandler.LoopSoundInstance> loopSounds = new Object2ObjectArrayMap();
   private Optional<AmbientMoodSettings> moodSettings = Optional.empty();
   private Optional<AmbientAdditionsSettings> additionsSettings = Optional.empty();
   private float moodiness;
   private Biome previousBiome;

   public BiomeAmbientSoundsHandler(LocalPlayer var1, SoundManager var2, BiomeManager var3) {
      super();
      this.random = var1.level.getRandom();
      this.player = var1;
      this.soundManager = var2;
      this.biomeManager = var3;
   }

   public float getMoodiness() {
      return this.moodiness;
   }

   public void tick() {
      this.loopSounds.values().removeIf(AbstractTickableSoundInstance::isStopped);
      Biome var1 = this.biomeManager.getNoiseBiomeAtPosition(this.player.getX(), this.player.getY(), this.player.getZ());
      if (var1 != this.previousBiome) {
         this.previousBiome = var1;
         this.moodSettings = var1.getAmbientMood();
         this.additionsSettings = var1.getAmbientAdditions();
         this.loopSounds.values().forEach(BiomeAmbientSoundsHandler.LoopSoundInstance::fadeOut);
         var1.getAmbientLoop().ifPresent((var2) -> {
            BiomeAmbientSoundsHandler.LoopSoundInstance var10000 = (BiomeAmbientSoundsHandler.LoopSoundInstance)this.loopSounds.compute(var1, (var2x, var3) -> {
               if (var3 == null) {
                  var3 = new BiomeAmbientSoundsHandler.LoopSoundInstance(var2);
                  this.soundManager.play(var3);
               }

               var3.fadeIn();
               return var3;
            });
         });
      }

      this.additionsSettings.ifPresent((var1x) -> {
         if (this.random.nextDouble() < var1x.getTickChance()) {
            this.soundManager.play(SimpleSoundInstance.forAmbientAddition(var1x.getSoundEvent()));
         }

      });
      this.moodSettings.ifPresent((var1x) -> {
         Level var2 = this.player.level;
         int var3 = var1x.getBlockSearchExtent() * 2 + 1;
         BlockPos var4 = new BlockPos(this.player.getX() + (double)this.random.nextInt(var3) - (double)var1x.getBlockSearchExtent(), this.player.getEyeY() + (double)this.random.nextInt(var3) - (double)var1x.getBlockSearchExtent(), this.player.getZ() + (double)this.random.nextInt(var3) - (double)var1x.getBlockSearchExtent());
         int var5 = var2.getBrightness(LightLayer.SKY, var4);
         if (var5 > 0) {
            this.moodiness -= (float)var5 / (float)var2.getMaxLightLevel() * 0.001F;
         } else {
            this.moodiness -= (float)(var2.getBrightness(LightLayer.BLOCK, var4) - 1) / (float)var1x.getTickDelay();
         }

         if (this.moodiness >= 1.0F) {
            double var6 = (double)var4.getX() + 0.5D;
            double var8 = (double)var4.getY() + 0.5D;
            double var10 = (double)var4.getZ() + 0.5D;
            double var12 = var6 - this.player.getX();
            double var14 = var8 - this.player.getEyeY();
            double var16 = var10 - this.player.getZ();
            double var18 = (double)Mth.sqrt(var12 * var12 + var14 * var14 + var16 * var16);
            double var20 = var18 + var1x.getSoundPositionOffset();
            SimpleSoundInstance var22 = SimpleSoundInstance.forAmbientMood(var1x.getSoundEvent(), this.player.getX() + var12 / var18 * var20, this.player.getEyeY() + var14 / var18 * var20, this.player.getZ() + var16 / var18 * var20);
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
         super(var1, SoundSource.AMBIENT);
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

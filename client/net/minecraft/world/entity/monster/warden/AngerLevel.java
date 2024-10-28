package net.minecraft.world.entity.monster.warden;

import java.util.Arrays;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public enum AngerLevel {
   CALM(0, SoundEvents.WARDEN_AMBIENT, SoundEvents.WARDEN_LISTENING),
   AGITATED(40, SoundEvents.WARDEN_AGITATED, SoundEvents.WARDEN_LISTENING_ANGRY),
   ANGRY(80, SoundEvents.WARDEN_ANGRY, SoundEvents.WARDEN_LISTENING_ANGRY);

   private static final AngerLevel[] SORTED_LEVELS = (AngerLevel[])Util.make(values(), (var0) -> {
      Arrays.sort(var0, (var0x, var1) -> {
         return Integer.compare(var1.minimumAnger, var0x.minimumAnger);
      });
   });
   private final int minimumAnger;
   private final SoundEvent ambientSound;
   private final SoundEvent listeningSound;

   private AngerLevel(final int var3, final SoundEvent var4, final SoundEvent var5) {
      this.minimumAnger = var3;
      this.ambientSound = var4;
      this.listeningSound = var5;
   }

   public int getMinimumAnger() {
      return this.minimumAnger;
   }

   public SoundEvent getAmbientSound() {
      return this.ambientSound;
   }

   public SoundEvent getListeningSound() {
      return this.listeningSound;
   }

   public static AngerLevel byAnger(int var0) {
      AngerLevel[] var1 = SORTED_LEVELS;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         AngerLevel var4 = var1[var3];
         if (var0 >= var4.minimumAnger) {
            return var4;
         }
      }

      return CALM;
   }

   public boolean isAngry() {
      return this == ANGRY;
   }

   // $FF: synthetic method
   private static AngerLevel[] $values() {
      return new AngerLevel[]{CALM, AGITATED, ANGRY};
   }
}

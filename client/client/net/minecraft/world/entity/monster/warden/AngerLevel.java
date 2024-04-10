package net.minecraft.world.entity.monster.warden;

import java.util.Arrays;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public enum AngerLevel {
   CALM(0, SoundEvents.WARDEN_AMBIENT, SoundEvents.WARDEN_LISTENING),
   AGITATED(40, SoundEvents.WARDEN_AGITATED, SoundEvents.WARDEN_LISTENING_ANGRY),
   ANGRY(80, SoundEvents.WARDEN_ANGRY, SoundEvents.WARDEN_LISTENING_ANGRY);

   private static final AngerLevel[] SORTED_LEVELS = Util.make(
      values(), var0 -> Arrays.sort(var0, (var0x, var1) -> Integer.compare(var1.minimumAnger, var0x.minimumAnger))
   );
   private final int minimumAnger;
   private final SoundEvent ambientSound;
   private final SoundEvent listeningSound;

   private AngerLevel(final int param3, final SoundEvent param4, final SoundEvent param5) {
      this.minimumAnger = nullxx;
      this.ambientSound = nullxxx;
      this.listeningSound = nullxxxx;
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
      for (AngerLevel var4 : SORTED_LEVELS) {
         if (var0 >= var4.minimumAnger) {
            return var4;
         }
      }

      return CALM;
   }

   public boolean isAngry() {
      return this == ANGRY;
   }
}

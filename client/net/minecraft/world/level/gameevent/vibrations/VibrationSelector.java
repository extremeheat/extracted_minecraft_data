package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

public class VibrationSelector {
   public static final Codec<VibrationSelector> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(VibrationInfo.CODEC.lenientOptionalFieldOf("event").forGetter((var0x) -> {
         return var0x.currentVibrationData.map(Pair::getLeft);
      }), Codec.LONG.fieldOf("tick").forGetter((var0x) -> {
         return (Long)var0x.currentVibrationData.map(Pair::getRight).orElse(-1L);
      })).apply(var0, VibrationSelector::new);
   });
   private Optional<Pair<VibrationInfo, Long>> currentVibrationData;

   public VibrationSelector(Optional<VibrationInfo> var1, long var2) {
      super();
      this.currentVibrationData = var1.map((var2x) -> {
         return Pair.of(var2x, var2);
      });
   }

   public VibrationSelector() {
      super();
      this.currentVibrationData = Optional.empty();
   }

   public void addCandidate(VibrationInfo var1, long var2) {
      if (this.shouldReplaceVibration(var1, var2)) {
         this.currentVibrationData = Optional.of(Pair.of(var1, var2));
      }

   }

   private boolean shouldReplaceVibration(VibrationInfo var1, long var2) {
      if (this.currentVibrationData.isEmpty()) {
         return true;
      } else {
         Pair var4 = (Pair)this.currentVibrationData.get();
         long var5 = (Long)var4.getRight();
         if (var2 != var5) {
            return false;
         } else {
            VibrationInfo var7 = (VibrationInfo)var4.getLeft();
            if (var1.distance() < var7.distance()) {
               return true;
            } else if (var1.distance() > var7.distance()) {
               return false;
            } else {
               return VibrationSystem.getGameEventFrequency(var1.gameEvent()) > VibrationSystem.getGameEventFrequency(var7.gameEvent());
            }
         }
      }
   }

   public Optional<VibrationInfo> chosenCandidate(long var1) {
      if (this.currentVibrationData.isEmpty()) {
         return Optional.empty();
      } else {
         return (Long)((Pair)this.currentVibrationData.get()).getRight() < var1 ? Optional.of((VibrationInfo)((Pair)this.currentVibrationData.get()).getLeft()) : Optional.empty();
      }
   }

   public void startOver() {
      this.currentVibrationData = Optional.empty();
   }
}

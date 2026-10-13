package net.minecraft.client.sounds;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class WeighedSoundEvents implements Weighted<Sound> {
   private final List<Weighted<Sound>> list = new ArrayList();
   private final @Nullable Component subtitle;

   public WeighedSoundEvents(final @Nullable Component subtitle) {
      super();
      this.subtitle = subtitle;
   }

   public int getWeight() {
      int sum = 0;

      for(Weighted<Sound> sound : this.list) {
         sum += sound.getWeight();
      }

      return sum;
   }

   public @Nullable Sound getSound(final RandomSource random) {
      int weight = this.getWeight();
      if (!this.list.isEmpty() && weight != 0) {
         int index = random.nextInt(weight);

         for(Weighted<Sound> weighted : this.list) {
            index -= weighted.getWeight();
            if (index < 0) {
               return weighted.getSound(random);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public void addSound(final Weighted<Sound> sound) {
      this.list.add(sound);
   }

   public @Nullable Component getSubtitle() {
      return this.subtitle;
   }

   public void preloadIfRequired(final SoundEngine soundEngine) {
      for(Weighted<Sound> weighted : this.list) {
         weighted.preloadIfRequired(soundEngine);
      }

   }
}

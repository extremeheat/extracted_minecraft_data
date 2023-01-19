package net.minecraft.client.sounds;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class WeighedSoundEvents implements Weighted<Sound> {
   private final List<Weighted<Sound>> list = Lists.newArrayList();
   @Nullable
   private final Component subtitle;

   public WeighedSoundEvents(ResourceLocation var1, @Nullable String var2) {
      super();
      this.subtitle = var2 == null ? null : Component.translatable(var2);
   }

   @Override
   public int getWeight() {
      int var1 = 0;

      for(Weighted var3 : this.list) {
         var1 += var3.getWeight();
      }

      return var1;
   }

   public Sound getSound(RandomSource var1) {
      int var2 = this.getWeight();
      if (!this.list.isEmpty() && var2 != 0) {
         int var3 = var1.nextInt(var2);

         for(Weighted var5 : this.list) {
            var3 -= var5.getWeight();
            if (var3 < 0) {
               return (Sound)var5.getSound(var1);
            }
         }

         return SoundManager.EMPTY_SOUND;
      } else {
         return SoundManager.EMPTY_SOUND;
      }
   }

   public void addSound(Weighted<Sound> var1) {
      this.list.add(var1);
   }

   @Nullable
   public Component getSubtitle() {
      return this.subtitle;
   }

   @Override
   public void preloadIfRequired(SoundEngine var1) {
      for(Weighted var3 : this.list) {
         var3.preloadIfRequired(var1);
      }
   }
}

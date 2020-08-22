package net.minecraft.client.sounds;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class WeighedSoundEvents implements Weighted {
   private final List list = Lists.newArrayList();
   private final Random random = new Random();
   private final ResourceLocation location;
   private final Component subtitle;

   public WeighedSoundEvents(ResourceLocation var1, @Nullable String var2) {
      this.location = var1;
      this.subtitle = var2 == null ? null : new TranslatableComponent(var2, new Object[0]);
   }

   public int getWeight() {
      int var1 = 0;

      Weighted var3;
      for(Iterator var2 = this.list.iterator(); var2.hasNext(); var1 += var3.getWeight()) {
         var3 = (Weighted)var2.next();
      }

      return var1;
   }

   public Sound getSound() {
      int var1 = this.getWeight();
      if (!this.list.isEmpty() && var1 != 0) {
         int var2 = this.random.nextInt(var1);
         Iterator var3 = this.list.iterator();

         Weighted var4;
         do {
            if (!var3.hasNext()) {
               return SoundManager.EMPTY_SOUND;
            }

            var4 = (Weighted)var3.next();
            var2 -= var4.getWeight();
         } while(var2 >= 0);

         return (Sound)var4.getSound();
      } else {
         return SoundManager.EMPTY_SOUND;
      }
   }

   public void addSound(Weighted var1) {
      this.list.add(var1);
   }

   @Nullable
   public Component getSubtitle() {
      return this.subtitle;
   }

   public void preloadIfRequired(SoundEngine var1) {
      Iterator var2 = this.list.iterator();

      while(var2.hasNext()) {
         Weighted var3 = (Weighted)var2.next();
         var3.preloadIfRequired(var1);
      }

   }

   // $FF: synthetic method
   public Object getSound() {
      return this.getSound();
   }
}

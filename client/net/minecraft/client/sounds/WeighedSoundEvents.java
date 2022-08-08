package net.minecraft.client.sounds;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class WeighedSoundEvents implements Weighted<Sound> {
   private final List<Weighted<Sound>> list = Lists.newArrayList();
   private final RandomSource random = RandomSource.create();
   private final ResourceLocation location;
   @Nullable
   private final Component subtitle;

   public WeighedSoundEvents(ResourceLocation var1, @Nullable String var2) {
      super();
      this.location = var1;
      this.subtitle = var2 == null ? null : Component.translatable(var2);
   }

   public int getWeight() {
      int var1 = 0;

      Weighted var3;
      for(Iterator var2 = this.list.iterator(); var2.hasNext(); var1 += var3.getWeight()) {
         var3 = (Weighted)var2.next();
      }

      return var1;
   }

   public Sound getSound(RandomSource var1) {
      int var2 = this.getWeight();
      if (!this.list.isEmpty() && var2 != 0) {
         int var3 = var1.nextInt(var2);
         Iterator var4 = this.list.iterator();

         Weighted var5;
         do {
            if (!var4.hasNext()) {
               return SoundManager.EMPTY_SOUND;
            }

            var5 = (Weighted)var4.next();
            var3 -= var5.getWeight();
         } while(var3 >= 0);

         return (Sound)var5.getSound(var1);
      } else {
         return SoundManager.EMPTY_SOUND;
      }
   }

   public void addSound(Weighted<Sound> var1) {
      this.list.add(var1);
   }

   public ResourceLocation getResourceLocation() {
      return this.location;
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
   public Object getSound(RandomSource var1) {
      return this.getSound(var1);
   }
}

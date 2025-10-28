package net.minecraft.client.sounds;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class WeighedSoundEvents implements Weighted<Sound> {
   private final List<Weighted<Sound>> list = Lists.newArrayList();
   private final @Nullable Component subtitle;

   public WeighedSoundEvents(ResourceLocation var1, @Nullable String var2) {
      super();
      if (SharedConstants.DEBUG_SUBTITLES) {
         MutableComponent var3 = Component.literal(var1.getPath());
         if ("FOR THE DEBUG!".equals(var2)) {
            var3 = var3.append((Component)Component.literal(" missing").withStyle(ChatFormatting.RED));
         }

         this.subtitle = var3;
      } else {
         this.subtitle = var2 == null ? null : Component.translatable(var2);
      }

   }

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

   public @Nullable Component getSubtitle() {
      return this.subtitle;
   }

   public void preloadIfRequired(SoundEngine var1) {
      for(Weighted var3 : this.list) {
         var3.preloadIfRequired(var1);
      }

   }

   // $FF: synthetic method
   public Object getSound(final RandomSource var1) {
      return this.getSound(var1);
   }
}

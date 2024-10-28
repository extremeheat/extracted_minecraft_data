package net.minecraft.sounds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;

public class Music {
   public static final Codec<Music> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(SoundEvent.CODEC.fieldOf("sound").forGetter((var0x) -> {
         return var0x.event;
      }), Codec.INT.fieldOf("min_delay").forGetter((var0x) -> {
         return var0x.minDelay;
      }), Codec.INT.fieldOf("max_delay").forGetter((var0x) -> {
         return var0x.maxDelay;
      }), Codec.BOOL.fieldOf("replace_current_music").forGetter((var0x) -> {
         return var0x.replaceCurrentMusic;
      })).apply(var0, Music::new);
   });
   private final Holder<SoundEvent> event;
   private final int minDelay;
   private final int maxDelay;
   private final boolean replaceCurrentMusic;

   public Music(Holder<SoundEvent> var1, int var2, int var3, boolean var4) {
      super();
      this.event = var1;
      this.minDelay = var2;
      this.maxDelay = var3;
      this.replaceCurrentMusic = var4;
   }

   public Holder<SoundEvent> getEvent() {
      return this.event;
   }

   public int getMinDelay() {
      return this.minDelay;
   }

   public int getMaxDelay() {
      return this.maxDelay;
   }

   public boolean replaceCurrentMusic() {
      return this.replaceCurrentMusic;
   }
}

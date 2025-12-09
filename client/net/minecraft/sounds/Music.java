package net.minecraft.sounds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;

public record Music(Holder<SoundEvent> sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
   public static final Codec<Music> CODEC = RecordCodecBuilder.create((var0) -> var0.group(SoundEvent.CODEC.fieldOf("sound").forGetter(Music::sound), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("min_delay").forGetter(Music::minDelay), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_delay").forGetter(Music::maxDelay), Codec.BOOL.optionalFieldOf("replace_current_music", false).forGetter(Music::replaceCurrentMusic)).apply(var0, Music::new));

   public Music(Holder<SoundEvent> var1, int var2, int var3, boolean var4) {
      super();
      this.sound = var1;
      this.minDelay = var2;
      this.maxDelay = var3;
      this.replaceCurrentMusic = var4;
   }
}

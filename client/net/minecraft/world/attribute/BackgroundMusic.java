package net.minecraft.world.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;

public record BackgroundMusic(Optional<Music> defaultMusic, Optional<Music> creativeMusic, Optional<Music> underwaterMusic) {
   public static final BackgroundMusic EMPTY = new BackgroundMusic(Optional.empty(), Optional.empty(), Optional.empty());
   public static final BackgroundMusic OVERWORLD;
   public static final Codec<BackgroundMusic> CODEC;

   public BackgroundMusic(Music var1) {
      this(Optional.of(var1), Optional.empty(), Optional.empty());
   }

   public BackgroundMusic(Holder<SoundEvent> var1) {
      this(Musics.createGameMusic(var1));
   }

   public BackgroundMusic(Optional<Music> var1, Optional<Music> var2, Optional<Music> var3) {
      super();
      this.defaultMusic = var1;
      this.creativeMusic = var2;
      this.underwaterMusic = var3;
   }

   public BackgroundMusic withUnderwater(Music var1) {
      return new BackgroundMusic(this.defaultMusic, this.creativeMusic, Optional.of(var1));
   }

   public Optional<Music> select(boolean var1, boolean var2) {
      if (var2 && this.underwaterMusic.isPresent()) {
         return this.underwaterMusic;
      } else {
         return var1 && this.creativeMusic.isPresent() ? this.creativeMusic : this.defaultMusic;
      }
   }

   static {
      OVERWORLD = new BackgroundMusic(Optional.of(Musics.GAME), Optional.of(Musics.CREATIVE), Optional.empty());
      CODEC = RecordCodecBuilder.create((var0) -> var0.group(Music.CODEC.optionalFieldOf("default").forGetter(BackgroundMusic::defaultMusic), Music.CODEC.optionalFieldOf("creative").forGetter(BackgroundMusic::creativeMusic), Music.CODEC.optionalFieldOf("underwater").forGetter(BackgroundMusic::underwaterMusic)).apply(var0, BackgroundMusic::new));
   }
}

package net.minecraft.client.sounds;

import javax.annotation.Nullable;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;

public record MusicInfo(@Nullable Music music, float volume) {
   public MusicInfo(Music var1) {
      this(var1, 1.0F);
   }

   public MusicInfo(@Nullable Music var1, float var2) {
      super();
      this.music = var1;
      this.volume = var2;
   }

   public boolean canReplace(SoundInstance var1) {
      if (this.music == null) {
         return false;
      } else {
         return this.music.replaceCurrentMusic() && !((SoundEvent)this.music.getEvent().value()).location().equals(var1.getLocation());
      }
   }

   @Nullable
   public Music music() {
      return this.music;
   }

   public float volume() {
      return this.volume;
   }
}

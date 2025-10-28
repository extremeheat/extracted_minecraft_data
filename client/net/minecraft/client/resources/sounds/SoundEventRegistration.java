package net.minecraft.client.resources.sounds;

import java.util.List;
import org.jspecify.annotations.Nullable;

public class SoundEventRegistration {
   private final List<Sound> sounds;
   private final boolean replace;
   private final @Nullable String subtitle;

   public SoundEventRegistration(List<Sound> var1, boolean var2, @Nullable String var3) {
      super();
      this.sounds = var1;
      this.replace = var2;
      this.subtitle = var3;
   }

   public List<Sound> getSounds() {
      return this.sounds;
   }

   public boolean isReplace() {
      return this.replace;
   }

   public @Nullable String getSubtitle() {
      return this.subtitle;
   }
}

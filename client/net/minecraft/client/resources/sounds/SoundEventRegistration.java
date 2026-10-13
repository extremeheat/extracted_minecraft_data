package net.minecraft.client.resources.sounds;

import java.util.List;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class SoundEventRegistration {
   private final List<Sound> sounds;
   private final boolean replace;
   private final @Nullable String subtitle;

   public SoundEventRegistration(final List<Sound> sounds, final boolean replace, final @Nullable String subtitle) {
      super();
      this.sounds = sounds;
      this.replace = replace;
      this.subtitle = subtitle;
   }

   public List<Sound> getSounds() {
      return this.sounds;
   }

   public boolean isReplace() {
      return this.replace;
   }

   public @Nullable Component getSubtitle() {
      return this.subtitle == null ? null : Component.translatable(this.subtitle);
   }
}

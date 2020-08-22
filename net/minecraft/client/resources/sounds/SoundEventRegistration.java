package net.minecraft.client.resources.sounds;

import java.util.List;
import javax.annotation.Nullable;

public class SoundEventRegistration {
   private final List sounds;
   private final boolean replace;
   private final String subtitle;

   public SoundEventRegistration(List var1, boolean var2, String var3) {
      this.sounds = var1;
      this.replace = var2;
      this.subtitle = var3;
   }

   public List getSounds() {
      return this.sounds;
   }

   public boolean isReplace() {
      return this.replace;
   }

   @Nullable
   public String getSubtitle() {
      return this.subtitle;
   }
}

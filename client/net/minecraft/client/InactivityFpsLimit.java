package net.minecraft.client;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum InactivityFpsLimit implements StringRepresentable {
   MINIMIZED("minimized", "options.inactivityFpsLimit.minimized"),
   AFK("afk", "options.inactivityFpsLimit.afk");

   public static final Codec<InactivityFpsLimit> CODEC = StringRepresentable.<InactivityFpsLimit>fromEnum(InactivityFpsLimit::values);
   private final String serializedName;
   private final Component caption;

   private InactivityFpsLimit(final String serializedName, final String key) {
      this.serializedName = serializedName;
      this.caption = Component.translatable(key);
   }

   public Component caption() {
      return this.caption;
   }

   public String getSerializedName() {
      return this.serializedName;
   }

   // $FF: synthetic method
   private static InactivityFpsLimit[] $values() {
      return new InactivityFpsLimit[]{MINIMIZED, AFK};
   }
}

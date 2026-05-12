package net.minecraft.client;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum PresenceSharing implements StringRepresentable {
   NONE("none"),
   LIMITED("limited"),
   ALL("all");

   public static final Codec<PresenceSharing> CODEC = StringRepresentable.<PresenceSharing>fromEnum(PresenceSharing::values);
   public static final String TRANSLATION_KEY_BASE = "options.sharePresence";
   private final String name;
   private final Component translatable;
   private final Component tooltip;

   private PresenceSharing(final String name) {
      this.name = name;
      this.translatable = Component.translatable("options.sharePresence." + name);
      this.tooltip = Component.translatable("options.sharePresence." + name + ".tooltip");
   }

   public String getSerializedName() {
      return this.name;
   }

   public Component getTranslation() {
      return this.translatable;
   }

   public Component getTooltip() {
      return this.tooltip;
   }

   // $FF: synthetic method
   private static PresenceSharing[] $values() {
      return new PresenceSharing[]{NONE, LIMITED, ALL};
   }
}

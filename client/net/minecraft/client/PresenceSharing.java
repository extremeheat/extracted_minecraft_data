package net.minecraft.client;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ScaledWidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
   private final ScaledWidgetSprites sprites;

   private PresenceSharing(final String name) {
      this.name = name;
      this.sprites = new ScaledWidgetSprites(Identifier.withDefaultNamespace("friends/presence_" + name), 16);
      this.translatable = Component.translatable("options.sharePresence." + name);
      this.tooltip = Component.translatable("options.sharePresence." + name + ".name").append("\n").append((Component)Component.translatable("options.sharePresence." + name + ".description").withStyle(ChatFormatting.GRAY));
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

   public ScaledWidgetSprites getSprites() {
      return this.sprites;
   }

   // $FF: synthetic method
   private static PresenceSharing[] $values() {
      return new PresenceSharing[]{NONE, LIMITED, ALL};
   }
}

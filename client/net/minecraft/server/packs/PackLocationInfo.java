package net.minecraft.server.packs;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.PackSource;

public record PackLocationInfo(String id, Component title, PackSource source, Optional<KnownPack> knownPackInfo) {
   public PackLocationInfo {
      super();
   }

   public Component createChatLink(final boolean enabled, final Component description) {
      return ComponentUtils.wrapInSquareBrackets(this.source.decorate(Component.literal(this.id))).withStyle((UnaryOperator)((s) -> s.withColor(enabled ? ChatFormatting.GREEN : ChatFormatting.RED).withInsertion(StringArgumentType.escapeIfRequired(this.id)).withHoverEvent(new HoverEvent.ShowText(Component.empty().append(this.title).append("\n").append(description)))));
   }
}

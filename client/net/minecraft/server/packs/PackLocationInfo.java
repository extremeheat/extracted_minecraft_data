package net.minecraft.server.packs;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.PackSource;

public record PackLocationInfo(String id, Component title, PackSource source, Optional<KnownPack> knownPackInfo) {
   public PackLocationInfo(String id, Component title, PackSource source, Optional<KnownPack> knownPackInfo) {
      super();
      this.id = id;
      this.title = title;
      this.source = source;
      this.knownPackInfo = knownPackInfo;
   }

   public Component createChatLink(boolean var1, Component var2) {
      return ComponentUtils.wrapInSquareBrackets(this.source.decorate(Component.literal(this.id)))
         .withStyle(
            var3 -> var3.withColor(var1 ? ChatFormatting.GREEN : ChatFormatting.RED)
                  .withInsertion(StringArgumentType.escapeIfRequired(this.id))
                  .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.empty().append(this.title).append("\n").append(var2)))
         );
   }
}

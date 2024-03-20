package net.minecraft.server.packs;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.PackSource;

public record PackLocationInfo(String a, Component b, PackSource c, Optional<KnownPack> d) {
   private final String id;
   private final Component title;
   private final PackSource source;
   private final Optional<KnownPack> knownPackInfo;

   public PackLocationInfo(String var1, Component var2, PackSource var3, Optional<KnownPack> var4) {
      super();
      this.id = var1;
      this.title = var2;
      this.source = var3;
      this.knownPackInfo = var4;
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
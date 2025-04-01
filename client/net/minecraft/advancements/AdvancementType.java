package net.minecraft.advancements;

import com.mojang.serialization.Codec;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerUnlock;
import net.minecraft.util.StringRepresentable;

public enum AdvancementType implements StringRepresentable {
   TASK("task", ChatFormatting.GREEN),
   CHALLENGE("challenge", ChatFormatting.DARK_PURPLE),
   GOAL("goal", ChatFormatting.GREEN);

   public static final Codec<AdvancementType> CODEC = StringRepresentable.<AdvancementType>fromEnum(AdvancementType::values);
   private static final Component HIDDEN_DESCRIPTION = Component.literal("You're not allowed to see the description of this unlock >:)").withStyle(ChatFormatting.OBFUSCATED);
   private final String name;
   private final ChatFormatting chatColor;
   private final Component displayName;

   private AdvancementType(final String var3, final ChatFormatting var4) {
      this.name = var3;
      this.chatColor = var4;
      this.displayName = Component.translatable("advancements.toast." + var3);
   }

   public ChatFormatting getChatColor() {
      return this.chatColor;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public String getSerializedName() {
      return this.name;
   }

   public Component createAnnouncement(AdvancementHolder var1, ServerPlayer var2) {
      return Component.translatable("chat.type.advancement." + this.name, var2.getDisplayName(), Advancement.name(var1));
   }

   public Component createPlayerUnlockAnnouncement(Holder<PlayerUnlock> var1, ServerPlayer var2) {
      DisplayInfo var3 = ((PlayerUnlock)var1.value()).display();
      boolean var4 = ((PlayerUnlock)var1.value()).defaultVisibility() == PlayerUnlock.UnlockVisibility.VISIBLE;
      Component var5 = var4 ? var3.getDescription() : HIDDEN_DESCRIPTION;
      MutableComponent var6 = ComponentUtils.wrapInSquareBrackets(var3.getTitle()).withStyle((UnaryOperator)((var1x) -> var1x.withColor(ChatFormatting.GREEN).withHoverEvent(new HoverEvent.ShowText(var5))));
      MutableComponent var7 = ComponentUtils.mergeStyles(var2.getDisplayName().copy(), Style.EMPTY.withColor(ChatFormatting.WHITE));
      return Component.translatable("unlocks.player.unlocked", var7, var6).withStyle(ChatFormatting.GRAY);
   }

   // $FF: synthetic method
   private static AdvancementType[] $values() {
      return new AdvancementType[]{TASK, CHALLENGE, GOAL};
   }
}

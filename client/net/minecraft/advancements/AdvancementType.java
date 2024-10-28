package net.minecraft.advancements;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;

public enum AdvancementType implements StringRepresentable {
   TASK("task", ChatFormatting.GREEN),
   CHALLENGE("challenge", ChatFormatting.DARK_PURPLE),
   GOAL("goal", ChatFormatting.GREEN);

   public static final Codec<AdvancementType> CODEC = StringRepresentable.fromEnum(AdvancementType::values);
   private final String name;
   private final ChatFormatting chatColor;
   private final Component displayName;

   private AdvancementType(String var3, ChatFormatting var4) {
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

   public MutableComponent createAnnouncement(AdvancementHolder var1, ServerPlayer var2) {
      return Component.translatable("chat.type.advancement." + this.name, var2.getDisplayName(), Advancement.name(var1));
   }

   // $FF: synthetic method
   private static AdvancementType[] $values() {
      return new AdvancementType[]{TASK, CHALLENGE, GOAL};
   }
}

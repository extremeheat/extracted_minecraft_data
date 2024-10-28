package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;

public class SignText {
   private static final Codec<Component[]> LINES_CODEC;
   public static final Codec<SignText> DIRECT_CODEC;
   public static final int LINES = 4;
   private final Component[] messages;
   private final Component[] filteredMessages;
   private final DyeColor color;
   private final boolean hasGlowingText;
   @Nullable
   private FormattedCharSequence[] renderMessages;
   private boolean renderMessagedFiltered;

   public SignText() {
      this(emptyMessages(), emptyMessages(), DyeColor.BLACK, false);
   }

   public SignText(Component[] var1, Component[] var2, DyeColor var3, boolean var4) {
      super();
      this.messages = var1;
      this.filteredMessages = var2;
      this.color = var3;
      this.hasGlowingText = var4;
   }

   private static Component[] emptyMessages() {
      return new Component[]{CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY};
   }

   private static SignText load(Component[] var0, Optional<Component[]> var1, DyeColor var2, boolean var3) {
      return new SignText(var0, (Component[])var1.orElse((Component[])Arrays.copyOf(var0, var0.length)), var2, var3);
   }

   public boolean hasGlowingText() {
      return this.hasGlowingText;
   }

   public SignText setHasGlowingText(boolean var1) {
      return var1 == this.hasGlowingText ? this : new SignText(this.messages, this.filteredMessages, this.color, var1);
   }

   public DyeColor getColor() {
      return this.color;
   }

   public SignText setColor(DyeColor var1) {
      return var1 == this.getColor() ? this : new SignText(this.messages, this.filteredMessages, var1, this.hasGlowingText);
   }

   public Component getMessage(int var1, boolean var2) {
      return this.getMessages(var2)[var1];
   }

   public SignText setMessage(int var1, Component var2) {
      return this.setMessage(var1, var2, var2);
   }

   public SignText setMessage(int var1, Component var2, Component var3) {
      Component[] var4 = (Component[])Arrays.copyOf(this.messages, this.messages.length);
      Component[] var5 = (Component[])Arrays.copyOf(this.filteredMessages, this.filteredMessages.length);
      var4[var1] = var2;
      var5[var1] = var3;
      return new SignText(var4, var5, this.color, this.hasGlowingText);
   }

   public boolean hasMessage(Player var1) {
      return Arrays.stream(this.getMessages(var1.isTextFilteringEnabled())).anyMatch((var0) -> {
         return !var0.getString().isEmpty();
      });
   }

   public Component[] getMessages(boolean var1) {
      return var1 ? this.filteredMessages : this.messages;
   }

   public FormattedCharSequence[] getRenderMessages(boolean var1, Function<Component, FormattedCharSequence> var2) {
      if (this.renderMessages == null || this.renderMessagedFiltered != var1) {
         this.renderMessagedFiltered = var1;
         this.renderMessages = new FormattedCharSequence[4];

         for(int var3 = 0; var3 < 4; ++var3) {
            this.renderMessages[var3] = (FormattedCharSequence)var2.apply(this.getMessage(var3, var1));
         }
      }

      return this.renderMessages;
   }

   private Optional<Component[]> filteredMessages() {
      for(int var1 = 0; var1 < 4; ++var1) {
         if (!this.filteredMessages[var1].equals(this.messages[var1])) {
            return Optional.of(this.filteredMessages);
         }
      }

      return Optional.empty();
   }

   public boolean hasAnyClickCommands(Player var1) {
      Component[] var2 = this.getMessages(var1.isTextFilteringEnabled());
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Component var5 = var2[var4];
         Style var6 = var5.getStyle();
         ClickEvent var7 = var6.getClickEvent();
         if (var7 != null && var7.getAction() == ClickEvent.Action.RUN_COMMAND) {
            return true;
         }
      }

      return false;
   }

   static {
      LINES_CODEC = ComponentSerialization.FLAT_CODEC.listOf().comapFlatMap((var0) -> {
         return Util.fixedSize((List)var0, 4).map((var0x) -> {
            return new Component[]{(Component)var0x.get(0), (Component)var0x.get(1), (Component)var0x.get(2), (Component)var0x.get(3)};
         });
      }, (var0) -> {
         return List.of(var0[0], var0[1], var0[2], var0[3]);
      });
      DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(LINES_CODEC.fieldOf("messages").forGetter((var0x) -> {
            return var0x.messages;
         }), LINES_CODEC.lenientOptionalFieldOf("filtered_messages").forGetter(SignText::filteredMessages), DyeColor.CODEC.fieldOf("color").orElse(DyeColor.BLACK).forGetter((var0x) -> {
            return var0x.color;
         }), Codec.BOOL.fieldOf("has_glowing_text").orElse(false).forGetter((var0x) -> {
            return var0x.hasGlowingText;
         })).apply(var0, SignText::load);
      });
   }
}

package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.Nullable;

public class SignText {
   public static final int LINES = 4;
   public static final Codec<SignText> CODEC = RecordCodecBuilder.create((i) -> i.group(ComponentSerialization.CODEC.listOf(4, 4).fieldOf("messages").forGetter((o) -> o.messages), ComponentSerialization.CODEC.listOf(4, 4).lenientOptionalFieldOf("filtered_messages").forGetter(SignText::filteredMessagesForSerialization), ExtraCodecs.optionalAlwaysPresentFieldOf(DyeColor.CODEC, "color", DyeColor.BLACK).forGetter((o) -> o.color), ExtraCodecs.optionalAlwaysPresentFieldOf(Codec.BOOL, "has_glowing_text", false).forGetter((o) -> o.hasGlowingText)).apply(i, SignText::load)).validate(SignText::validateLineCounts);
   public static final StreamCodec<RegistryFriendlyByteBuf, SignText> STREAM_CODEC;
   public static final TooltipProvider.Getter<SignText> FRONT_TEXT;
   public static final TooltipProvider.Getter<SignText> BACK_TEXT;
   private static final List<Component> EMPTY_MESSAGES;
   public static final SignText EMPTY;
   private final List<Component> messages;
   private final List<Component> filteredMessages;
   private final DyeColor color;
   private final boolean hasGlowingText;
   private FormattedCharSequence @Nullable [] renderMessages;
   private boolean renderMessagedFiltered;

   public SignText(final List<Component> messages, final List<Component> filteredMessages, final DyeColor color, final boolean hasGlowingText) {
      super();
      this.messages = messages;
      this.filteredMessages = filteredMessages;
      this.color = color;
      this.hasGlowingText = hasGlowingText;
   }

   private static SignText load(final List<Component> messages, final Optional<List<Component>> filteredMessages, final DyeColor color, final boolean hasGlowingText) {
      return new SignText(messages, (List)filteredMessages.orElse(messages), color, hasGlowingText);
   }

   private static DataResult<SignText> validateLineCounts(final SignText signText) {
      return signText.filteredMessages.size() != signText.messages.size() ? DataResult.error(() -> "Filtered and raw line counts are not equal") : DataResult.success(signText);
   }

   public boolean hasGlowingText() {
      return this.hasGlowingText;
   }

   public SignText withGlowingText(final boolean hasGlowingText) {
      return hasGlowingText == this.hasGlowingText ? this : new SignText(this.messages, this.filteredMessages, this.color, hasGlowingText);
   }

   public DyeColor getColor() {
      return this.color;
   }

   public SignText withColor(final DyeColor color) {
      return color == this.getColor() ? this : new SignText(this.messages, this.filteredMessages, color, this.hasGlowingText);
   }

   public boolean hasMessage(final boolean shouldFilter) {
      return this.getMessages(shouldFilter).stream().anyMatch((component) -> !component.getString().isEmpty());
   }

   public List<Component> getMessages(final boolean shouldFilter) {
      return shouldFilter ? this.filteredMessages : this.messages;
   }

   public FormattedCharSequence[] getRenderMessages(final boolean shouldFilter, final Function<Component, FormattedCharSequence> prepare) {
      if (this.renderMessages == null || this.renderMessagedFiltered != shouldFilter) {
         List<Component> messages = this.getMessages(shouldFilter);
         this.renderMessagedFiltered = shouldFilter;
         this.renderMessages = new FormattedCharSequence[messages.size()];

         for(int i = 0; i < messages.size(); ++i) {
            this.renderMessages[i] = (FormattedCharSequence)prepare.apply((Component)messages.get(i));
         }
      }

      return this.renderMessages;
   }

   private Optional<List<Component>> filteredMessagesForSerialization() {
      return this.filteredMessages.equals(this.messages) ? Optional.empty() : Optional.of(this.filteredMessages);
   }

   public boolean hasAnyClickCommands(final boolean shouldFilter) {
      for(Component message : this.getMessages(shouldFilter)) {
         Style style = message.getStyle();
         ClickEvent event = style.getClickEvent();
         if (event != null && event.action() == ClickEvent.Action.RUN_COMMAND) {
            return true;
         }
      }

      return false;
   }

   public boolean hasEditableText(final boolean shouldFilter) {
      return this.getMessages(shouldFilter).stream().allMatch((message) -> message.equals(CommonComponents.EMPTY) || message.getContents() instanceof PlainTextContents);
   }

   public boolean equals(final Object object) {
      if (!(object instanceof SignText signText)) {
         return false;
      } else {
         return this.hasGlowingText == signText.hasGlowingText && this.messages.equals(signText.messages) && this.filteredMessages.equals(signText.filteredMessages) && this.color == signText.color;
      }
   }

   public int hashCode() {
      int result = 1;
      result = 31 * result + this.messages.hashCode();
      result = 31 * result + this.filteredMessages.hashCode();
      result = 31 * result + this.color.hashCode();
      result = 31 * result + Boolean.hashCode(this.hasGlowingText);
      return result;
   }

   public Mutable asMutable() {
      return new Mutable(this);
   }

   public static TooltipProvider.Getter<SignText> createTooltip(final Component title) {
      return (component) -> component.equals(EMPTY) ? (var0, var1, var2, var3) -> {
         } : (var2, consumer, var4, var5) -> {
            consumer.accept(title);

            for(Component message : component.messages) {
               consumer.accept(Component.literal("  ").append(message));
            }

         };
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.fixedSizeList(4)), (o) -> o.messages, ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.fixedSizeList(4)).apply(ByteBufCodecs::optional), SignText::filteredMessagesForSerialization, DyeColor.STREAM_CODEC, (o) -> o.color, ByteBufCodecs.BOOL, (o) -> o.hasGlowingText, SignText::load);
      FRONT_TEXT = createTooltip(Component.translatable("sign.front_text").withStyle(ChatFormatting.GRAY));
      BACK_TEXT = createTooltip(Component.translatable("sign.back_text").withStyle(ChatFormatting.GRAY));
      EMPTY_MESSAGES = Collections.nCopies(4, CommonComponents.EMPTY);
      EMPTY = new SignText(EMPTY_MESSAGES, EMPTY_MESSAGES, DyeColor.BLACK, false);
   }

   public static class Mutable {
      private final Component[] messages;
      private final Component[] filteredMessages;
      private DyeColor color;
      private boolean isTextGlowing;

      private Mutable(final SignText original) {
         super();
         this.messages = (Component[])original.messages.toArray((x$0) -> new Component[x$0]);
         this.filteredMessages = (Component[])original.messages.toArray((x$0) -> new Component[x$0]);
         this.color = original.color;
         this.isTextGlowing = original.hasGlowingText;
      }

      public SignText asImmutable() {
         return new SignText(List.of(this.messages), List.of(this.filteredMessages), this.color, this.isTextGlowing);
      }

      public Mutable setLine(final int index, final Component raw, final Component filtered) {
         this.messages[index] = raw;
         this.filteredMessages[index] = filtered;
         return this;
      }

      public Mutable setLine(final int index, final Component text) {
         return this.setLine(index, text, text);
      }

      public Mutable modifyLine(final int index, final UnaryOperator<Component> modifier) {
         this.messages[index] = (Component)modifier.apply(this.messages[index]);
         this.filteredMessages[index] = (Component)modifier.apply(this.filteredMessages[index]);
         return this;
      }

      public Mutable modifyLines(final UnaryOperator<Component> modifier) {
         for(int i = 0; i < this.messages.length; ++i) {
            this.modifyLine(i, modifier);
         }

         return this;
      }

      public Mutable setColor(final DyeColor color) {
         this.color = color;
         return this;
      }

      public Mutable setTextGlowing(final boolean flag) {
         this.isTextGlowing = flag;
         return this;
      }
   }
}

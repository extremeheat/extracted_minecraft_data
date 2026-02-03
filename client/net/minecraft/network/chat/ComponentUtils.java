package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.annotation.CheckReturnValue;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public class ComponentUtils {
   public static final String DEFAULT_SEPARATOR_TEXT = ", ";
   public static final Component DEFAULT_SEPARATOR;
   public static final Component DEFAULT_NO_STYLE_SEPARATOR;

   public ComponentUtils() {
      super();
   }

   @CheckReturnValue
   public static MutableComponent mergeStyles(final MutableComponent component, final Style style) {
      if (style.isEmpty()) {
         return component;
      } else {
         Style inner = component.getStyle();
         if (inner.isEmpty()) {
            return component.setStyle(style);
         } else {
            return inner.equals(style) ? component : component.setStyle(inner.applyTo(style));
         }
      }
   }

   @CheckReturnValue
   public static Component mergeStyles(final Component component, final Style style) {
      if (style.isEmpty()) {
         return component;
      } else {
         Style inner = component.getStyle();
         if (inner.isEmpty()) {
            return component.copy().setStyle(style);
         } else {
            return (Component)(inner.equals(style) ? component : component.copy().setStyle(inner.applyTo(style)));
         }
      }
   }

   public static Optional<MutableComponent> updateForEntity(final @Nullable CommandSourceStack source, final Optional<Component> component, final @Nullable Entity entity, final int recursionDepth) throws CommandSyntaxException {
      return component.isPresent() ? Optional.of(updateForEntity(source, (Component)component.get(), entity, recursionDepth)) : Optional.empty();
   }

   public static MutableComponent updateForEntity(final @Nullable CommandSourceStack source, final Component component, final @Nullable Entity entity, final int recursionDepth) throws CommandSyntaxException {
      if (recursionDepth > 100) {
         return component.copy();
      } else {
         MutableComponent result = component.getContents().resolve(source, entity, recursionDepth + 1);

         for(Component sibling : component.getSiblings()) {
            result.append((Component)updateForEntity(source, sibling, entity, recursionDepth + 1));
         }

         return result.withStyle(resolveStyle(source, component.getStyle(), entity, recursionDepth));
      }
   }

   private static Style resolveStyle(final @Nullable CommandSourceStack source, final Style style, final @Nullable Entity entity, final int recursionDepth) throws CommandSyntaxException {
      HoverEvent hoverEvent = style.getHoverEvent();
      if (hoverEvent instanceof HoverEvent.ShowText var5) {
         HoverEvent.ShowText var10000 = var5;

         try {
            var10 = var10000.value();
         } catch (Throwable var8) {
            throw new MatchException(var8.toString(), var8);
         }

         HoverEvent resolved = var10;
         resolved = new HoverEvent.ShowText(updateForEntity(source, resolved, entity, recursionDepth + 1));
         return style.withHoverEvent(resolved);
      } else {
         return style;
      }
   }

   public static Component formatList(final Collection<String> values) {
      return formatAndSortList(values, (v) -> Component.literal(v).withStyle(ChatFormatting.GREEN));
   }

   public static <T extends Comparable<T>> Component formatAndSortList(final Collection<T> values, final Function<T, Component> formatter) {
      if (values.isEmpty()) {
         return CommonComponents.EMPTY;
      } else if (values.size() == 1) {
         return (Component)formatter.apply((Comparable)values.iterator().next());
      } else {
         List<T> sorted = Lists.newArrayList(values);
         sorted.sort(Comparable::compareTo);
         return formatList(sorted, formatter);
      }
   }

   public static <T> Component formatList(final Collection<? extends T> values, final Function<T, Component> formatter) {
      return formatList(values, DEFAULT_SEPARATOR, formatter);
   }

   public static <T> MutableComponent formatList(final Collection<? extends T> values, final Optional<? extends Component> separator, final Function<T, Component> formatter) {
      return formatList(values, (Component)DataFixUtils.orElse(separator, DEFAULT_SEPARATOR), formatter);
   }

   public static Component formatList(final Collection<? extends Component> values, final Component separator) {
      return formatList(values, separator, Function.identity());
   }

   public static <T> MutableComponent formatList(final Collection<? extends T> values, final Component separator, final Function<T, Component> formatter) {
      if (values.isEmpty()) {
         return Component.empty();
      } else if (values.size() == 1) {
         return ((Component)formatter.apply(values.iterator().next())).copy();
      } else {
         MutableComponent result = Component.empty();
         boolean first = true;

         for(T value : values) {
            if (!first) {
               result.append(separator);
            }

            result.append((Component)formatter.apply(value));
            first = false;
         }

         return result;
      }
   }

   public static MutableComponent wrapInSquareBrackets(final Component inner) {
      return Component.translatable("chat.square_brackets", inner);
   }

   public static Component fromMessage(final Message message) {
      if (message instanceof Component component) {
         return component;
      } else {
         return Component.literal(message.getString());
      }
   }

   public static boolean isTranslationResolvable(final @Nullable Component component) {
      if (component != null) {
         ComponentContents var2 = component.getContents();
         if (var2 instanceof TranslatableContents) {
            TranslatableContents translatable = (TranslatableContents)var2;
            String key = translatable.getKey();
            String fallback = translatable.getFallback();
            return fallback != null || Language.getInstance().has(key);
         }
      }

      return true;
   }

   public static MutableComponent copyOnClickText(final String text) {
      return wrapInSquareBrackets(Component.literal(text).withStyle((UnaryOperator)((s) -> s.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent.CopyToClipboard(text)).withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.copy.click"))).withInsertion(text))));
   }

   static {
      DEFAULT_SEPARATOR = Component.literal(", ").withStyle(ChatFormatting.GRAY);
      DEFAULT_NO_STYLE_SEPARATOR = Component.literal(", ");
   }
}

package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.Entity;

public class ComponentUtils {
   public static final String DEFAULT_SEPARATOR_TEXT = ", ";
   public static final Component DEFAULT_SEPARATOR = Component.literal(", ").withStyle(ChatFormatting.GRAY);
   public static final Component DEFAULT_NO_STYLE_SEPARATOR = Component.literal(", ");

   public ComponentUtils() {
      super();
   }

   public static MutableComponent mergeStyles(MutableComponent var0, Style var1) {
      if (var1.isEmpty()) {
         return var0;
      } else {
         Style var2 = var0.getStyle();
         if (var2.isEmpty()) {
            return var0.setStyle(var1);
         } else {
            return var2.equals(var1) ? var0 : var0.setStyle(var2.applyTo(var1));
         }
      }
   }

   public static Optional<MutableComponent> updateForEntity(@Nullable CommandSourceStack var0, Optional<Component> var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      return var1.isPresent() ? Optional.of(updateForEntity(var0, (Component)var1.get(), var2, var3)) : Optional.empty();
   }

   public static MutableComponent updateForEntity(@Nullable CommandSourceStack var0, Component var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      if (var3 > 100) {
         return var1.copy();
      } else {
         MutableComponent var4 = var1.getContents().resolve(var0, var2, var3 + 1);

         for(Component var6 : var1.getSiblings()) {
            var4.append(updateForEntity(var0, var6, var2, var3 + 1));
         }

         return var4.withStyle(resolveStyle(var0, var1.getStyle(), var2, var3));
      }
   }

   private static Style resolveStyle(@Nullable CommandSourceStack var0, Style var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      HoverEvent var4 = var1.getHoverEvent();
      if (var4 != null) {
         Component var5 = var4.getValue(HoverEvent.Action.SHOW_TEXT);
         if (var5 != null) {
            HoverEvent var6 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, updateForEntity(var0, var5, var2, var3 + 1));
            return var1.withHoverEvent(var6);
         }
      }

      return var1;
   }

   public static Component formatList(Collection<String> var0) {
      return formatAndSortList(var0, var0x -> Component.literal(var0x).withStyle(ChatFormatting.GREEN));
   }

   public static <T extends Comparable<T>> Component formatAndSortList(Collection<T> var0, Function<T, Component> var1) {
      if (var0.isEmpty()) {
         return CommonComponents.EMPTY;
      } else if (var0.size() == 1) {
         return (Component)var1.apply((Comparable)var0.iterator().next());
      } else {
         ArrayList var2 = Lists.newArrayList(var0);
         var2.sort(Comparable::compareTo);
         return formatList(var2, var1);
      }
   }

   public static <T> Component formatList(Collection<? extends T> var0, Function<T, Component> var1) {
      return formatList(var0, DEFAULT_SEPARATOR, var1);
   }

   public static <T> MutableComponent formatList(Collection<? extends T> var0, Optional<? extends Component> var1, Function<T, Component> var2) {
      return formatList(var0, (Component)DataFixUtils.orElse(var1, DEFAULT_SEPARATOR), var2);
   }

   public static Component formatList(Collection<? extends Component> var0, Component var1) {
      return formatList(var0, var1, Function.identity());
   }

   public static <T> MutableComponent formatList(Collection<? extends T> var0, Component var1, Function<T, Component> var2) {
      if (var0.isEmpty()) {
         return Component.empty();
      } else if (var0.size() == 1) {
         return ((Component)var2.apply(var0.iterator().next())).copy();
      } else {
         MutableComponent var3 = Component.empty();
         boolean var4 = true;

         for(Object var6 : var0) {
            if (!var4) {
               var3.append(var1);
            }

            var3.append((Component)var2.apply(var6));
            var4 = false;
         }

         return var3;
      }
   }

   public static MutableComponent wrapInSquareBrackets(Component var0) {
      return Component.translatable("chat.square_brackets", var0);
   }

   public static Component fromMessage(Message var0) {
      return (Component)(var0 instanceof Component ? (Component)var0 : Component.literal(var0.getString()));
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static boolean isTranslationResolvable(@Nullable Component var0) {
      if (var0 != null) {
         ComponentContents var2 = var0.getContents();
         if (var2 instanceof TranslatableContents var1) {
            String var4 = var1.getKey();
            String var3 = var1.getFallback();
            return var3 != null || Language.getInstance().has(var4);
         }
      }

      return true;
   }

   public static MutableComponent copyOnClickText(String var0) {
      return wrapInSquareBrackets(
         Component.literal(var0)
            .withStyle(
               var1 -> var1.withColor(ChatFormatting.GREEN)
                     .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, var0))
                     .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))
                     .withInsertion(var0)
            )
      );
   }
}

package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;

public class ComponentUtils {
   public static final String DEFAULT_SEPARATOR_TEXT = ", ";
   public static final Component DEFAULT_SEPARATOR;
   public static final Component DEFAULT_NO_STYLE_SEPARATOR;

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
         MutableComponent var4 = var1 instanceof ContextAwareComponent ? ((ContextAwareComponent)var1).resolve(var0, var2, var3 + 1) : var1.plainCopy();
         Iterator var5 = var1.getSiblings().iterator();

         while(var5.hasNext()) {
            Component var6 = (Component)var5.next();
            var4.append((Component)updateForEntity(var0, var6, var2, var3 + 1));
         }

         return var4.withStyle(resolveStyle(var0, var1.getStyle(), var2, var3));
      }
   }

   private static Style resolveStyle(@Nullable CommandSourceStack var0, Style var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      HoverEvent var4 = var1.getHoverEvent();
      if (var4 != null) {
         Component var5 = (Component)var4.getValue(HoverEvent.Action.SHOW_TEXT);
         if (var5 != null) {
            HoverEvent var6 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, updateForEntity(var0, var5, var2, var3 + 1));
            return var1.withHoverEvent(var6);
         }
      }

      return var1;
   }

   public static Component getDisplayName(GameProfile var0) {
      if (var0.getName() != null) {
         return new TextComponent(var0.getName());
      } else {
         return var0.getId() != null ? new TextComponent(var0.getId().toString()) : new TextComponent("(unknown)");
      }
   }

   public static Component formatList(Collection<String> var0) {
      return formatAndSortList(var0, (var0x) -> {
         return (new TextComponent(var0x)).withStyle(ChatFormatting.GREEN);
      });
   }

   public static <T extends Comparable<T>> Component formatAndSortList(Collection<T> var0, Function<T, Component> var1) {
      if (var0.isEmpty()) {
         return TextComponent.EMPTY;
      } else if (var0.size() == 1) {
         return (Component)var1.apply((Comparable)var0.iterator().next());
      } else {
         ArrayList var2 = Lists.newArrayList(var0);
         var2.sort(Comparable::compareTo);
         return formatList(var2, (Function)var1);
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
         return new TextComponent("");
      } else if (var0.size() == 1) {
         return ((Component)var2.apply(var0.iterator().next())).copy();
      } else {
         TextComponent var3 = new TextComponent("");
         boolean var4 = true;

         for(Iterator var5 = var0.iterator(); var5.hasNext(); var4 = false) {
            Object var6 = var5.next();
            if (!var4) {
               var3.append(var1);
            }

            var3.append((Component)var2.apply(var6));
         }

         return var3;
      }
   }

   public static MutableComponent wrapInSquareBrackets(Component var0) {
      return new TranslatableComponent("chat.square_brackets", new Object[]{var0});
   }

   public static Component fromMessage(Message var0) {
      return (Component)(var0 instanceof Component ? (Component)var0 : new TextComponent(var0.getString()));
   }

   static {
      DEFAULT_SEPARATOR = (new TextComponent(", ")).withStyle(ChatFormatting.GRAY);
      DEFAULT_NO_STYLE_SEPARATOR = new TextComponent(", ");
   }
}

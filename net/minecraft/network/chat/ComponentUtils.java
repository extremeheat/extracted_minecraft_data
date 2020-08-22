package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;

public class ComponentUtils {
   public static Component mergeStyles(Component var0, Style var1) {
      if (var1.isEmpty()) {
         return var0;
      } else {
         return var0.getStyle().isEmpty() ? var0.setStyle(var1.copy()) : (new TextComponent("")).append(var0).setStyle(var1.copy());
      }
   }

   public static Component updateForEntity(@Nullable CommandSourceStack var0, Component var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      if (var3 > 100) {
         return var1;
      } else {
         ++var3;
         Component var4 = var1 instanceof ContextAwareComponent ? ((ContextAwareComponent)var1).resolve(var0, var2, var3) : var1.copy();
         Iterator var5 = var1.getSiblings().iterator();

         while(var5.hasNext()) {
            Component var6 = (Component)var5.next();
            var4.append(updateForEntity(var0, var6, var2, var3));
         }

         return mergeStyles(var4, var1.getStyle());
      }
   }

   public static Component getDisplayName(GameProfile var0) {
      if (var0.getName() != null) {
         return new TextComponent(var0.getName());
      } else {
         return var0.getId() != null ? new TextComponent(var0.getId().toString()) : new TextComponent("(unknown)");
      }
   }

   public static Component formatList(Collection var0) {
      return formatAndSortList(var0, (var0x) -> {
         return (new TextComponent(var0x)).withStyle(ChatFormatting.GREEN);
      });
   }

   public static Component formatAndSortList(Collection var0, Function var1) {
      if (var0.isEmpty()) {
         return new TextComponent("");
      } else if (var0.size() == 1) {
         return (Component)var1.apply(var0.iterator().next());
      } else {
         ArrayList var2 = Lists.newArrayList(var0);
         var2.sort(Comparable::compareTo);
         return formatList(var2, var1);
      }
   }

   public static Component formatList(Collection var0, Function var1) {
      if (var0.isEmpty()) {
         return new TextComponent("");
      } else if (var0.size() == 1) {
         return (Component)var1.apply(var0.iterator().next());
      } else {
         TextComponent var2 = new TextComponent("");
         boolean var3 = true;

         for(Iterator var4 = var0.iterator(); var4.hasNext(); var3 = false) {
            Object var5 = var4.next();
            if (!var3) {
               var2.append((new TextComponent(", ")).withStyle(ChatFormatting.GRAY));
            }

            var2.append((Component)var1.apply(var5));
         }

         return var2;
      }
   }

   public static Component wrapInSquareBrackets(Component var0) {
      return (new TextComponent("[")).append(var0).append("]");
   }

   public static Component fromMessage(Message var0) {
      return (Component)(var0 instanceof Component ? (Component)var0 : new TextComponent(var0.getString()));
   }
}

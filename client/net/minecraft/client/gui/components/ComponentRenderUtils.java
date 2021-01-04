package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ComponentRenderUtils {
   public static String stripColor(String var0, boolean var1) {
      return !var1 && !Minecraft.getInstance().options.chatColors ? ChatFormatting.stripFormatting(var0) : var0;
   }

   public static List<Component> wrapComponents(Component var0, int var1, Font var2, boolean var3, boolean var4) {
      int var5 = 0;
      TextComponent var6 = new TextComponent("");
      ArrayList var7 = Lists.newArrayList();
      ArrayList var8 = Lists.newArrayList(var0);

      for(int var9 = 0; var9 < var8.size(); ++var9) {
         Component var10 = (Component)var8.get(var9);
         String var11 = var10.getContents();
         boolean var12 = false;
         String var14;
         if (var11.contains("\n")) {
            int var13 = var11.indexOf(10);
            var14 = var11.substring(var13 + 1);
            var11 = var11.substring(0, var13 + 1);
            Component var15 = (new TextComponent(var14)).setStyle(var10.getStyle().copy());
            var8.add(var9 + 1, var15);
            var12 = true;
         }

         String var21 = stripColor(var10.getStyle().getLegacyFormatCodes() + var11, var4);
         var14 = var21.endsWith("\n") ? var21.substring(0, var21.length() - 1) : var21;
         int var22 = var2.width(var14);
         Object var16 = (new TextComponent(var14)).setStyle(var10.getStyle().copy());
         if (var5 + var22 > var1) {
            String var17 = var2.substrByWidth(var21, var1 - var5, false);
            String var18 = var17.length() < var21.length() ? var21.substring(var17.length()) : null;
            if (var18 != null && !var18.isEmpty()) {
               int var19 = var18.charAt(0) != ' ' ? var17.lastIndexOf(32) : var17.length();
               if (var19 >= 0 && var2.width(var21.substring(0, var19)) > 0) {
                  var17 = var21.substring(0, var19);
                  if (var3) {
                     ++var19;
                  }

                  var18 = var21.substring(var19);
               } else if (var5 > 0 && !var21.contains(" ")) {
                  var17 = "";
                  var18 = var21;
               }

               Component var20 = (new TextComponent(var18)).setStyle(var10.getStyle().copy());
               var8.add(var9 + 1, var20);
            }

            var22 = var2.width(var17);
            var16 = new TextComponent(var17);
            ((Component)var16).setStyle(var10.getStyle().copy());
            var12 = true;
         }

         if (var5 + var22 <= var1) {
            var5 += var22;
            var6.append((Component)var16);
         } else {
            var12 = true;
         }

         if (var12) {
            var7.add(var6);
            var5 = 0;
            var6 = new TextComponent("");
         }
      }

      var7.add(var6);
      return var7;
   }
}

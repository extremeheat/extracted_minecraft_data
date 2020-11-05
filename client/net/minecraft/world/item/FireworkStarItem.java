package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class FireworkStarItem extends Item {
   public FireworkStarItem(Item.Properties var1) {
      super(var1);
   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      CompoundTag var5 = var1.getTagElement("Explosion");
      if (var5 != null) {
         appendHoverText(var5, var3);
      }

   }

   public static void appendHoverText(CompoundTag var0, List<Component> var1) {
      FireworkRocketItem.Shape var2 = FireworkRocketItem.Shape.byId(var0.getByte("Type"));
      var1.add((new TranslatableComponent("item.minecraft.firework_star.shape." + var2.getName())).withStyle(ChatFormatting.GRAY));
      int[] var3 = var0.getIntArray("Colors");
      if (var3.length > 0) {
         var1.add(appendColors((new TextComponent("")).withStyle(ChatFormatting.GRAY), var3));
      }

      int[] var4 = var0.getIntArray("FadeColors");
      if (var4.length > 0) {
         var1.add(appendColors((new TranslatableComponent("item.minecraft.firework_star.fade_to")).append(" ").withStyle(ChatFormatting.GRAY), var4));
      }

      if (var0.getBoolean("Trail")) {
         var1.add((new TranslatableComponent("item.minecraft.firework_star.trail")).withStyle(ChatFormatting.GRAY));
      }

      if (var0.getBoolean("Flicker")) {
         var1.add((new TranslatableComponent("item.minecraft.firework_star.flicker")).withStyle(ChatFormatting.GRAY));
      }

   }

   private static Component appendColors(MutableComponent var0, int[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var2 > 0) {
            var0.append(", ");
         }

         var0.append(getColorName(var1[var2]));
      }

      return var0;
   }

   private static Component getColorName(int var0) {
      DyeColor var1 = DyeColor.byFireworkColor(var0);
      return var1 == null ? new TranslatableComponent("item.minecraft.firework_star.custom_color") : new TranslatableComponent("item.minecraft.firework_star." + var1.getName());
   }
}

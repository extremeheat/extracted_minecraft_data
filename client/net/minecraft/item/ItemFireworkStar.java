package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemFireworkStar extends Item {
   public ItemFireworkStar(Item.Properties var1) {
      super(var1);
   }

   public void func_77624_a(ItemStack var1, @Nullable World var2, List<ITextComponent> var3, ITooltipFlag var4) {
      NBTTagCompound var5 = var1.func_179543_a("Explosion");
      if (var5 != null) {
         func_195967_a(var5, var3);
      }

   }

   public static void func_195967_a(NBTTagCompound var0, List<ITextComponent> var1) {
      ItemFireworkRocket.Shape var2 = ItemFireworkRocket.Shape.func_196070_a(var0.func_74771_c("Type"));
      var1.add((new TextComponentTranslation("item.minecraft.firework_star.shape." + var2.func_196068_b(), new Object[0])).func_211708_a(TextFormatting.GRAY));
      int[] var3 = var0.func_74759_k("Colors");
      if (var3.length > 0) {
         var1.add(func_200298_a((new TextComponentString("")).func_211708_a(TextFormatting.GRAY), var3));
      }

      int[] var4 = var0.func_74759_k("FadeColors");
      if (var4.length > 0) {
         var1.add(func_200298_a((new TextComponentTranslation("item.minecraft.firework_star.fade_to", new Object[0])).func_150258_a(" ").func_211708_a(TextFormatting.GRAY), var4));
      }

      if (var0.func_74767_n("Trail")) {
         var1.add((new TextComponentTranslation("item.minecraft.firework_star.trail", new Object[0])).func_211708_a(TextFormatting.GRAY));
      }

      if (var0.func_74767_n("Flicker")) {
         var1.add((new TextComponentTranslation("item.minecraft.firework_star.flicker", new Object[0])).func_211708_a(TextFormatting.GRAY));
      }

   }

   private static ITextComponent func_200298_a(ITextComponent var0, int[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var2 > 0) {
            var0.func_150258_a(", ");
         }

         var0.func_150257_a(func_200297_a(var1[var2]));
      }

      return var0;
   }

   private static ITextComponent func_200297_a(int var0) {
      EnumDyeColor var1 = EnumDyeColor.func_196058_b(var0);
      return var1 == null ? new TextComponentTranslation("item.minecraft.firework_star.custom_color", new Object[0]) : new TextComponentTranslation("item.minecraft.firework_star." + var1.func_176762_d(), new Object[0]);
   }
}

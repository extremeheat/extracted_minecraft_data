package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;

public class GiveCommand {
   public static void func_198494_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("give").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197056_a("targets", EntityArgument.func_197094_d()).then(((RequiredArgumentBuilder)Commands.func_197056_a("item", ItemArgument.func_197317_a()).executes((var0x) -> {
         return func_198497_a((CommandSource)var0x.getSource(), ItemArgument.func_197316_a(var0x, "item"), EntityArgument.func_197090_e(var0x, "targets"), 1);
      })).then(Commands.func_197056_a("count", IntegerArgumentType.integer(1)).executes((var0x) -> {
         return func_198497_a((CommandSource)var0x.getSource(), ItemArgument.func_197316_a(var0x, "item"), EntityArgument.func_197090_e(var0x, "targets"), IntegerArgumentType.getInteger(var0x, "count"));
      })))));
   }

   private static int func_198497_a(CommandSource var0, ItemInput var1, Collection<EntityPlayerMP> var2, int var3) throws CommandSyntaxException {
      Iterator var4 = var2.iterator();

      label40:
      while(var4.hasNext()) {
         EntityPlayerMP var5 = (EntityPlayerMP)var4.next();
         int var6 = var3;

         while(true) {
            while(true) {
               if (var6 <= 0) {
                  continue label40;
               }

               int var7 = Math.min(var1.func_197319_a().func_77639_j(), var6);
               var6 -= var7;
               ItemStack var8 = var1.func_197320_a(var7, false);
               boolean var9 = var5.field_71071_by.func_70441_a(var8);
               EntityItem var10;
               if (var9 && var8.func_190926_b()) {
                  var8.func_190920_e(1);
                  var10 = var5.func_71019_a(var8, false);
                  if (var10 != null) {
                     var10.func_174870_v();
                  }

                  var5.field_70170_p.func_184148_a((EntityPlayer)null, var5.field_70165_t, var5.field_70163_u, var5.field_70161_v, SoundEvents.field_187638_cR, SoundCategory.PLAYERS, 0.2F, ((var5.func_70681_au().nextFloat() - var5.func_70681_au().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                  var5.field_71069_bz.func_75142_b();
               } else {
                  var10 = var5.func_71019_a(var8, false);
                  if (var10 != null) {
                     var10.func_174868_q();
                     var10.func_200217_b(var5.func_110124_au());
                  }
               }
            }
         }
      }

      if (var2.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.give.success.single", new Object[]{var3, var1.func_197320_a(var3, false).func_151000_E(), ((EntityPlayerMP)var2.iterator().next()).func_145748_c_()}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.give.success.single", new Object[]{var3, var1.func_197320_a(var3, false).func_151000_E(), var2.size()}), true);
      }

      return var2.size();
   }
}

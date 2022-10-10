package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.SlotArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class ReplaceItemCommand {
   private static final SimpleCommandExceptionType field_198608_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.replaceitem.block.failed", new Object[0]));
   private static final DynamicCommandExceptionType field_198609_b = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.replaceitem.slot.inapplicable", new Object[]{var0});
   });
   private static final Dynamic2CommandExceptionType field_211412_c = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("commands.replaceitem.entity.failed", new Object[]{var0, var1});
   });

   public static void func_198602_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("replaceitem").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197057_a("block").then(Commands.func_197056_a("pos", BlockPosArgument.func_197276_a()).then(Commands.func_197056_a("slot", SlotArgument.func_197223_a()).then(((RequiredArgumentBuilder)Commands.func_197056_a("item", ItemArgument.func_197317_a()).executes((var0x) -> {
         return func_198603_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "pos"), SlotArgument.func_197221_a(var0x, "slot"), ItemArgument.func_197316_a(var0x, "item").func_197320_a(1, false));
      })).then(Commands.func_197056_a("count", IntegerArgumentType.integer(1, 64)).executes((var0x) -> {
         return func_198603_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "pos"), SlotArgument.func_197221_a(var0x, "slot"), ItemArgument.func_197316_a(var0x, "item").func_197320_a(IntegerArgumentType.getInteger(var0x, "count"), true));
      }))))))).then(Commands.func_197057_a("entity").then(Commands.func_197056_a("targets", EntityArgument.func_197093_b()).then(Commands.func_197056_a("slot", SlotArgument.func_197223_a()).then(((RequiredArgumentBuilder)Commands.func_197056_a("item", ItemArgument.func_197317_a()).executes((var0x) -> {
         return func_198604_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), SlotArgument.func_197221_a(var0x, "slot"), ItemArgument.func_197316_a(var0x, "item").func_197320_a(1, false));
      })).then(Commands.func_197056_a("count", IntegerArgumentType.integer(1, 64)).executes((var0x) -> {
         return func_198604_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), SlotArgument.func_197221_a(var0x, "slot"), ItemArgument.func_197316_a(var0x, "item").func_197320_a(IntegerArgumentType.getInteger(var0x, "count"), true));
      })))))));
   }

   private static int func_198603_a(CommandSource var0, BlockPos var1, int var2, ItemStack var3) throws CommandSyntaxException {
      TileEntity var4 = var0.func_197023_e().func_175625_s(var1);
      if (!(var4 instanceof IInventory)) {
         throw field_198608_a.create();
      } else {
         IInventory var5 = (IInventory)var4;
         if (var2 >= 0 && var2 < var5.func_70302_i_()) {
            var5.func_70299_a(var2, var3);
            var0.func_197030_a(new TextComponentTranslation("commands.replaceitem.block.success", new Object[]{var1.func_177958_n(), var1.func_177956_o(), var1.func_177952_p(), var3.func_151000_E()}), true);
            return 1;
         } else {
            throw field_198609_b.create(var2);
         }
      }
   }

   private static int func_198604_a(CommandSource var0, Collection<? extends Entity> var1, int var2, ItemStack var3) throws CommandSyntaxException {
      ArrayList var4 = Lists.newArrayListWithCapacity(var1.size());
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         Entity var6 = (Entity)var5.next();
         if (var6 instanceof EntityPlayerMP) {
            ((EntityPlayerMP)var6).field_71069_bz.func_75142_b();
         }

         if (var6.func_174820_d(var2, var3.func_77946_l())) {
            var4.add(var6);
            if (var6 instanceof EntityPlayerMP) {
               ((EntityPlayerMP)var6).field_71069_bz.func_75142_b();
            }
         }
      }

      if (var4.isEmpty()) {
         throw field_211412_c.create(var3.func_151000_E(), var2);
      } else {
         if (var4.size() == 1) {
            var0.func_197030_a(new TextComponentTranslation("commands.replaceitem.entity.success.single", new Object[]{((Entity)var4.iterator().next()).func_145748_c_(), var3.func_151000_E()}), true);
         } else {
            var0.func_197030_a(new TextComponentTranslation("commands.replaceitem.entity.success.multiple", new Object[]{var4.size(), var3.func_151000_E()}), true);
         }

         return var4.size();
      }
   }
}

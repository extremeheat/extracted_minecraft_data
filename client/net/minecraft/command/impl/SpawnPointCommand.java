package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class SpawnPointCommand {
   public static void func_198695_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("spawnpoint").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).executes((var0x) -> {
         return func_198696_a((CommandSource)var0x.getSource(), Collections.singleton(((CommandSource)var0x.getSource()).func_197035_h()), new BlockPos(((CommandSource)var0x.getSource()).func_197036_d()));
      })).then(((RequiredArgumentBuilder)Commands.func_197056_a("targets", EntityArgument.func_197094_d()).executes((var0x) -> {
         return func_198696_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), new BlockPos(((CommandSource)var0x.getSource()).func_197036_d()));
      })).then(Commands.func_197056_a("pos", BlockPosArgument.func_197276_a()).executes((var0x) -> {
         return func_198696_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), BlockPosArgument.func_197274_b(var0x, "pos"));
      }))));
   }

   private static int func_198696_a(CommandSource var0, Collection<EntityPlayerMP> var1, BlockPos var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
         var4.func_180473_a(var2, true);
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.spawnpoint.success.single", new Object[]{var2.func_177958_n(), var2.func_177956_o(), var2.func_177952_p(), ((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.spawnpoint.success.multiple", new Object[]{var2.func_177958_n(), var2.func_177956_o(), var2.func_177952_p(), var1.size()}), true);
      }

      return var1.size();
   }
}

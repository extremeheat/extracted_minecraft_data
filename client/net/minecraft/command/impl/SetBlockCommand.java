package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class SetBlockCommand {
   private static final SimpleCommandExceptionType field_198689_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.setblock.failed", new Object[0]));

   public static void func_198684_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("setblock").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197056_a("pos", BlockPosArgument.func_197276_a()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("block", BlockStateArgument.func_197239_a()).executes((var0x) -> {
         return func_198683_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "pos"), BlockStateArgument.func_197238_a(var0x, "block"), SetBlockCommand.Mode.REPLACE, (Predicate)null);
      })).then(Commands.func_197057_a("destroy").executes((var0x) -> {
         return func_198683_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "pos"), BlockStateArgument.func_197238_a(var0x, "block"), SetBlockCommand.Mode.DESTROY, (Predicate)null);
      }))).then(Commands.func_197057_a("keep").executes((var0x) -> {
         return func_198683_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "pos"), BlockStateArgument.func_197238_a(var0x, "block"), SetBlockCommand.Mode.REPLACE, (var0) -> {
            return var0.func_196960_c().func_175623_d(var0.func_177508_d());
         });
      }))).then(Commands.func_197057_a("replace").executes((var0x) -> {
         return func_198683_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "pos"), BlockStateArgument.func_197238_a(var0x, "block"), SetBlockCommand.Mode.REPLACE, (Predicate)null);
      })))));
   }

   private static int func_198683_a(CommandSource var0, BlockPos var1, BlockStateInput var2, SetBlockCommand.Mode var3, @Nullable Predicate<BlockWorldState> var4) throws CommandSyntaxException {
      WorldServer var5 = var0.func_197023_e();
      if (var4 != null && !var4.test(new BlockWorldState(var5, var1, true))) {
         throw field_198689_a.create();
      } else {
         boolean var6;
         if (var3 == SetBlockCommand.Mode.DESTROY) {
            var5.func_175655_b(var1, true);
            var6 = !var2.func_197231_a().func_196958_f();
         } else {
            TileEntity var7 = var5.func_175625_s(var1);
            if (var7 instanceof IInventory) {
               ((IInventory)var7).func_174888_l();
            }

            var6 = true;
         }

         if (var6 && !var2.func_197230_a(var5, var1, 2)) {
            throw field_198689_a.create();
         } else {
            var5.func_195592_c(var1, var2.func_197231_a().func_177230_c());
            var0.func_197030_a(new TextComponentTranslation("commands.setblock.success", new Object[]{var1.func_177958_n(), var1.func_177956_o(), var1.func_177952_p()}), true);
            return 1;
         }
      }
   }

   public interface IFilter {
      @Nullable
      BlockStateInput filter(MutableBoundingBox var1, BlockPos var2, BlockStateInput var3, WorldServer var4);
   }

   public static enum Mode {
      REPLACE,
      OUTLINE,
      HOLLOW,
      DESTROY;

      private Mode() {
      }
   }
}

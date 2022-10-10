package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class FillCommand {
   private static final Dynamic2CommandExceptionType field_198473_a = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("commands.fill.toobig", new Object[]{var0, var1});
   });
   private static final BlockStateInput field_198474_b;
   private static final SimpleCommandExceptionType field_198475_c;

   public static void func_198465_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("fill").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197056_a("from", BlockPosArgument.func_197276_a()).then(Commands.func_197056_a("to", BlockPosArgument.func_197276_a()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("block", BlockStateArgument.func_197239_a()).executes((var0x) -> {
         return func_198463_a((CommandSource)var0x.getSource(), new MutableBoundingBox(BlockPosArgument.func_197273_a(var0x, "from"), BlockPosArgument.func_197273_a(var0x, "to")), BlockStateArgument.func_197238_a(var0x, "block"), FillCommand.Mode.REPLACE, (Predicate)null);
      })).then(((LiteralArgumentBuilder)Commands.func_197057_a("replace").executes((var0x) -> {
         return func_198463_a((CommandSource)var0x.getSource(), new MutableBoundingBox(BlockPosArgument.func_197273_a(var0x, "from"), BlockPosArgument.func_197273_a(var0x, "to")), BlockStateArgument.func_197238_a(var0x, "block"), FillCommand.Mode.REPLACE, (Predicate)null);
      })).then(Commands.func_197056_a("filter", BlockPredicateArgument.func_199824_a()).executes((var0x) -> {
         return func_198463_a((CommandSource)var0x.getSource(), new MutableBoundingBox(BlockPosArgument.func_197273_a(var0x, "from"), BlockPosArgument.func_197273_a(var0x, "to")), BlockStateArgument.func_197238_a(var0x, "block"), FillCommand.Mode.REPLACE, BlockPredicateArgument.func_199825_a(var0x, "filter"));
      })))).then(Commands.func_197057_a("keep").executes((var0x) -> {
         return func_198463_a((CommandSource)var0x.getSource(), new MutableBoundingBox(BlockPosArgument.func_197273_a(var0x, "from"), BlockPosArgument.func_197273_a(var0x, "to")), BlockStateArgument.func_197238_a(var0x, "block"), FillCommand.Mode.REPLACE, (var0) -> {
            return var0.func_196960_c().func_175623_d(var0.func_177508_d());
         });
      }))).then(Commands.func_197057_a("outline").executes((var0x) -> {
         return func_198463_a((CommandSource)var0x.getSource(), new MutableBoundingBox(BlockPosArgument.func_197273_a(var0x, "from"), BlockPosArgument.func_197273_a(var0x, "to")), BlockStateArgument.func_197238_a(var0x, "block"), FillCommand.Mode.OUTLINE, (Predicate)null);
      }))).then(Commands.func_197057_a("hollow").executes((var0x) -> {
         return func_198463_a((CommandSource)var0x.getSource(), new MutableBoundingBox(BlockPosArgument.func_197273_a(var0x, "from"), BlockPosArgument.func_197273_a(var0x, "to")), BlockStateArgument.func_197238_a(var0x, "block"), FillCommand.Mode.HOLLOW, (Predicate)null);
      }))).then(Commands.func_197057_a("destroy").executes((var0x) -> {
         return func_198463_a((CommandSource)var0x.getSource(), new MutableBoundingBox(BlockPosArgument.func_197273_a(var0x, "from"), BlockPosArgument.func_197273_a(var0x, "to")), BlockStateArgument.func_197238_a(var0x, "block"), FillCommand.Mode.DESTROY, (Predicate)null);
      }))))));
   }

   private static int func_198463_a(CommandSource var0, MutableBoundingBox var1, BlockStateInput var2, FillCommand.Mode var3, @Nullable Predicate<BlockWorldState> var4) throws CommandSyntaxException {
      int var5 = var1.func_78883_b() * var1.func_78882_c() * var1.func_78880_d();
      if (var5 > 32768) {
         throw field_198473_a.create(32768, var5);
      } else {
         ArrayList var6 = Lists.newArrayList();
         WorldServer var7 = var0.func_197023_e();
         int var8 = 0;
         Iterator var9 = BlockPos.MutableBlockPos.func_191532_a(var1.field_78897_a, var1.field_78895_b, var1.field_78896_c, var1.field_78893_d, var1.field_78894_e, var1.field_78892_f).iterator();

         while(true) {
            BlockPos var10;
            do {
               if (!var9.hasNext()) {
                  var9 = var6.iterator();

                  while(var9.hasNext()) {
                     var10 = (BlockPos)var9.next();
                     Block var13 = var7.func_180495_p(var10).func_177230_c();
                     var7.func_195592_c(var10, var13);
                  }

                  if (var8 == 0) {
                     throw field_198475_c.create();
                  }

                  var0.func_197030_a(new TextComponentTranslation("commands.fill.success", new Object[]{var8}), true);
                  return var8;
               }

               var10 = (BlockPos)var9.next();
            } while(var4 != null && !var4.test(new BlockWorldState(var7, var10, true)));

            BlockStateInput var11 = var3.field_198459_e.filter(var1, var10, var2, var7);
            if (var11 != null) {
               TileEntity var12 = var7.func_175625_s(var10);
               if (var12 != null && var12 instanceof IInventory) {
                  ((IInventory)var12).func_174888_l();
               }

               if (var11.func_197230_a(var7, var10, 2)) {
                  var6.add(var10.func_185334_h());
                  ++var8;
               }
            }
         }
      }
   }

   static {
      field_198474_b = new BlockStateInput(Blocks.field_150350_a.func_176223_P(), Collections.emptySet(), (NBTTagCompound)null);
      field_198475_c = new SimpleCommandExceptionType(new TextComponentTranslation("commands.fill.failed", new Object[0]));
   }

   static enum Mode {
      REPLACE((var0, var1, var2, var3) -> {
         return var2;
      }),
      OUTLINE((var0, var1, var2, var3) -> {
         return var1.func_177958_n() != var0.field_78897_a && var1.func_177958_n() != var0.field_78893_d && var1.func_177956_o() != var0.field_78895_b && var1.func_177956_o() != var0.field_78894_e && var1.func_177952_p() != var0.field_78896_c && var1.func_177952_p() != var0.field_78892_f ? null : var2;
      }),
      HOLLOW((var0, var1, var2, var3) -> {
         return var1.func_177958_n() != var0.field_78897_a && var1.func_177958_n() != var0.field_78893_d && var1.func_177956_o() != var0.field_78895_b && var1.func_177956_o() != var0.field_78894_e && var1.func_177952_p() != var0.field_78896_c && var1.func_177952_p() != var0.field_78892_f ? FillCommand.field_198474_b : var2;
      }),
      DESTROY((var0, var1, var2, var3) -> {
         var3.func_175655_b(var1, true);
         return var2;
      });

      public final SetBlockCommand.IFilter field_198459_e;

      private Mode(SetBlockCommand.IFilter var3) {
         this.field_198459_e = var3;
      }
   }
}

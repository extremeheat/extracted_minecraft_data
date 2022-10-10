package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class CloneCommand {
   private static final SimpleCommandExceptionType field_198284_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.clone.overlap", new Object[0]));
   private static final Dynamic2CommandExceptionType field_198285_c = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("commands.clone.toobig", new Object[]{var0, var1});
   });
   private static final SimpleCommandExceptionType field_198286_d = new SimpleCommandExceptionType(new TextComponentTranslation("commands.clone.failed", new Object[0]));
   public static final Predicate<BlockWorldState> field_198283_a = (var0) -> {
      return !var0.func_177509_a().func_196958_f();
   };

   public static void func_198265_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("clone").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197056_a("begin", BlockPosArgument.func_197276_a()).then(Commands.func_197056_a("end", BlockPosArgument.func_197276_a()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("destination", BlockPosArgument.func_197276_a()).executes((var0x) -> {
         return func_198274_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "begin"), BlockPosArgument.func_197273_a(var0x, "end"), BlockPosArgument.func_197273_a(var0x, "destination"), (var0) -> {
            return true;
         }, CloneCommand.Mode.NORMAL);
      })).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("replace").executes((var0x) -> {
         return func_198274_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "begin"), BlockPosArgument.func_197273_a(var0x, "end"), BlockPosArgument.func_197273_a(var0x, "destination"), (var0) -> {
            return true;
         }, CloneCommand.Mode.NORMAL);
      })).then(Commands.func_197057_a("force").executes((var0x) -> {
         return func_198274_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "begin"), BlockPosArgument.func_197273_a(var0x, "end"), BlockPosArgument.func_197273_a(var0x, "destination"), (var0) -> {
            return true;
         }, CloneCommand.Mode.FORCE);
      }))).then(Commands.func_197057_a("move").executes((var0x) -> {
         return func_198274_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "begin"), BlockPosArgument.func_197273_a(var0x, "end"), BlockPosArgument.func_197273_a(var0x, "destination"), (var0) -> {
            return true;
         }, CloneCommand.Mode.MOVE);
      }))).then(Commands.func_197057_a("normal").executes((var0x) -> {
         return func_198274_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "begin"), BlockPosArgument.func_197273_a(var0x, "end"), BlockPosArgument.func_197273_a(var0x, "destination"), (var0) -> {
            return true;
         }, CloneCommand.Mode.NORMAL);
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("masked").executes((var0x) -> {
         return func_198274_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "begin"), BlockPosArgument.func_197273_a(var0x, "end"), BlockPosArgument.func_197273_a(var0x, "destination"), field_198283_a, CloneCommand.Mode.NORMAL);
      })).then(Commands.func_197057_a("force").executes((var0x) -> {
         return func_198274_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "begin"), BlockPosArgument.func_197273_a(var0x, "end"), BlockPosArgument.func_197273_a(var0x, "destination"), field_198283_a, CloneCommand.Mode.FORCE);
      }))).then(Commands.func_197057_a("move").executes((var0x) -> {
         return func_198274_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "begin"), BlockPosArgument.func_197273_a(var0x, "end"), BlockPosArgument.func_197273_a(var0x, "destination"), field_198283_a, CloneCommand.Mode.MOVE);
      }))).then(Commands.func_197057_a("normal").executes((var0x) -> {
         return func_198274_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "begin"), BlockPosArgument.func_197273_a(var0x, "end"), BlockPosArgument.func_197273_a(var0x, "destination"), field_198283_a, CloneCommand.Mode.NORMAL);
      })))).then(Commands.func_197057_a("filtered").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("filter", BlockPredicateArgument.func_199824_a()).executes((var0x) -> {
         return func_198274_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "begin"), BlockPosArgument.func_197273_a(var0x, "end"), BlockPosArgument.func_197273_a(var0x, "destination"), BlockPredicateArgument.func_199825_a(var0x, "filter"), CloneCommand.Mode.NORMAL);
      })).then(Commands.func_197057_a("force").executes((var0x) -> {
         return func_198274_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "begin"), BlockPosArgument.func_197273_a(var0x, "end"), BlockPosArgument.func_197273_a(var0x, "destination"), BlockPredicateArgument.func_199825_a(var0x, "filter"), CloneCommand.Mode.FORCE);
      }))).then(Commands.func_197057_a("move").executes((var0x) -> {
         return func_198274_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "begin"), BlockPosArgument.func_197273_a(var0x, "end"), BlockPosArgument.func_197273_a(var0x, "destination"), BlockPredicateArgument.func_199825_a(var0x, "filter"), CloneCommand.Mode.MOVE);
      }))).then(Commands.func_197057_a("normal").executes((var0x) -> {
         return func_198274_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197273_a(var0x, "begin"), BlockPosArgument.func_197273_a(var0x, "end"), BlockPosArgument.func_197273_a(var0x, "destination"), BlockPredicateArgument.func_199825_a(var0x, "filter"), CloneCommand.Mode.NORMAL);
      }))))))));
   }

   private static int func_198274_a(CommandSource var0, BlockPos var1, BlockPos var2, BlockPos var3, Predicate<BlockWorldState> var4, CloneCommand.Mode var5) throws CommandSyntaxException {
      MutableBoundingBox var6 = new MutableBoundingBox(var1, var2);
      MutableBoundingBox var7 = new MutableBoundingBox(var3, var3.func_177971_a(var6.func_175896_b()));
      if (!var5.func_198254_a() && var7.func_78884_a(var6)) {
         throw field_198284_b.create();
      } else {
         int var8 = var6.func_78883_b() * var6.func_78882_c() * var6.func_78880_d();
         if (var8 > 32768) {
            throw field_198285_c.create(32768, var8);
         } else {
            WorldServer var9 = var0.func_197023_e();
            if (var9.func_175711_a(var6) && var9.func_175711_a(var7)) {
               ArrayList var10 = Lists.newArrayList();
               ArrayList var11 = Lists.newArrayList();
               ArrayList var12 = Lists.newArrayList();
               LinkedList var13 = Lists.newLinkedList();
               BlockPos var14 = new BlockPos(var7.field_78897_a - var6.field_78897_a, var7.field_78895_b - var6.field_78895_b, var7.field_78896_c - var6.field_78896_c);

               int var17;
               for(int var15 = var6.field_78896_c; var15 <= var6.field_78892_f; ++var15) {
                  for(int var16 = var6.field_78895_b; var16 <= var6.field_78894_e; ++var16) {
                     for(var17 = var6.field_78897_a; var17 <= var6.field_78893_d; ++var17) {
                        BlockPos var18 = new BlockPos(var17, var16, var15);
                        BlockPos var19 = var18.func_177971_a(var14);
                        BlockWorldState var20 = new BlockWorldState(var9, var18, false);
                        IBlockState var21 = var20.func_177509_a();
                        if (var4.test(var20)) {
                           TileEntity var22 = var9.func_175625_s(var18);
                           if (var22 != null) {
                              NBTTagCompound var23 = var22.func_189515_b(new NBTTagCompound());
                              var11.add(new CloneCommand.BlockInfo(var19, var21, var23));
                              var13.addLast(var18);
                           } else if (!var21.func_200015_d(var9, var18) && !var21.func_185917_h()) {
                              var12.add(new CloneCommand.BlockInfo(var19, var21, (NBTTagCompound)null));
                              var13.addFirst(var18);
                           } else {
                              var10.add(new CloneCommand.BlockInfo(var19, var21, (NBTTagCompound)null));
                              var13.addLast(var18);
                           }
                        }
                     }
                  }
               }

               if (var5 == CloneCommand.Mode.MOVE) {
                  Iterator var24;
                  BlockPos var26;
                  for(var24 = var13.iterator(); var24.hasNext(); var9.func_180501_a(var26, Blocks.field_180401_cv.func_176223_P(), 2)) {
                     var26 = (BlockPos)var24.next();
                     TileEntity var28 = var9.func_175625_s(var26);
                     if (var28 instanceof IInventory) {
                        ((IInventory)var28).func_174888_l();
                     }
                  }

                  var24 = var13.iterator();

                  while(var24.hasNext()) {
                     var26 = (BlockPos)var24.next();
                     var9.func_180501_a(var26, Blocks.field_150350_a.func_176223_P(), 3);
                  }
               }

               ArrayList var25 = Lists.newArrayList();
               var25.addAll(var10);
               var25.addAll(var11);
               var25.addAll(var12);
               List var27 = Lists.reverse(var25);

               CloneCommand.BlockInfo var30;
               for(Iterator var29 = var27.iterator(); var29.hasNext(); var9.func_180501_a(var30.field_198251_a, Blocks.field_180401_cv.func_176223_P(), 2)) {
                  var30 = (CloneCommand.BlockInfo)var29.next();
                  TileEntity var32 = var9.func_175625_s(var30.field_198251_a);
                  if (var32 instanceof IInventory) {
                     ((IInventory)var32).func_174888_l();
                  }
               }

               var17 = 0;
               Iterator var31 = var25.iterator();

               CloneCommand.BlockInfo var33;
               while(var31.hasNext()) {
                  var33 = (CloneCommand.BlockInfo)var31.next();
                  if (var9.func_180501_a(var33.field_198251_a, var33.field_198252_b, 2)) {
                     ++var17;
                  }
               }

               for(var31 = var11.iterator(); var31.hasNext(); var9.func_180501_a(var33.field_198251_a, var33.field_198252_b, 2)) {
                  var33 = (CloneCommand.BlockInfo)var31.next();
                  TileEntity var34 = var9.func_175625_s(var33.field_198251_a);
                  if (var33.field_198253_c != null && var34 != null) {
                     var33.field_198253_c.func_74768_a("x", var33.field_198251_a.func_177958_n());
                     var33.field_198253_c.func_74768_a("y", var33.field_198251_a.func_177956_o());
                     var33.field_198253_c.func_74768_a("z", var33.field_198251_a.func_177952_p());
                     var34.func_145839_a(var33.field_198253_c);
                     var34.func_70296_d();
                  }
               }

               var31 = var27.iterator();

               while(var31.hasNext()) {
                  var33 = (CloneCommand.BlockInfo)var31.next();
                  var9.func_195592_c(var33.field_198251_a, var33.field_198252_b.func_177230_c());
               }

               var9.func_205220_G_().func_205368_a(var6, var14);
               if (var17 == 0) {
                  throw field_198286_d.create();
               } else {
                  var0.func_197030_a(new TextComponentTranslation("commands.clone.success", new Object[]{var17}), true);
                  return var17;
               }
            } else {
               throw BlockPosArgument.field_197278_b.create();
            }
         }
      }
   }

   static class BlockInfo {
      public final BlockPos field_198251_a;
      public final IBlockState field_198252_b;
      @Nullable
      public final NBTTagCompound field_198253_c;

      public BlockInfo(BlockPos var1, IBlockState var2, @Nullable NBTTagCompound var3) {
         super();
         this.field_198251_a = var1;
         this.field_198252_b = var2;
         this.field_198253_c = var3;
      }
   }

   static enum Mode {
      FORCE(true),
      MOVE(true),
      NORMAL(false);

      private final boolean field_198259_d;

      private Mode(boolean var3) {
         this.field_198259_d = var3;
      }

      public boolean func_198254_a() {
         return this.field_198259_d;
      }
   }
}

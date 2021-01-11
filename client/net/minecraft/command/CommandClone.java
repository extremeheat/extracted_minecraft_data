package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CommandClone extends CommandBase {
   public CommandClone() {
      super();
   }

   public String func_71517_b() {
      return "clone";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.clone.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 9) {
         throw new WrongUsageException("commands.clone.usage", new Object[0]);
      } else {
         var1.func_174794_a(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
         BlockPos var3 = func_175757_a(var1, var2, 0, false);
         BlockPos var4 = func_175757_a(var1, var2, 3, false);
         BlockPos var5 = func_175757_a(var1, var2, 6, false);
         StructureBoundingBox var6 = new StructureBoundingBox(var3, var4);
         StructureBoundingBox var7 = new StructureBoundingBox(var5, var5.func_177971_a(var6.func_175896_b()));
         int var8 = var6.func_78883_b() * var6.func_78882_c() * var6.func_78880_d();
         if (var8 > 32768) {
            throw new CommandException("commands.clone.tooManyBlocks", new Object[]{var8, 32768});
         } else {
            boolean var9 = false;
            Block var10 = null;
            int var11 = -1;
            if ((var2.length < 11 || !var2[10].equals("force") && !var2[10].equals("move")) && var6.func_78884_a(var7)) {
               throw new CommandException("commands.clone.noOverlap", new Object[0]);
            } else {
               if (var2.length >= 11 && var2[10].equals("move")) {
                  var9 = true;
               }

               if (var6.field_78895_b >= 0 && var6.field_78894_e < 256 && var7.field_78895_b >= 0 && var7.field_78894_e < 256) {
                  World var12 = var1.func_130014_f_();
                  if (var12.func_175711_a(var6) && var12.func_175711_a(var7)) {
                     boolean var13 = false;
                     if (var2.length >= 10) {
                        if (var2[9].equals("masked")) {
                           var13 = true;
                        } else if (var2[9].equals("filtered")) {
                           if (var2.length < 12) {
                              throw new WrongUsageException("commands.clone.usage", new Object[0]);
                           }

                           var10 = func_147180_g(var1, var2[11]);
                           if (var2.length >= 13) {
                              var11 = func_175764_a(var2[12], 0, 15);
                           }
                        }
                     }

                     ArrayList var14 = Lists.newArrayList();
                     ArrayList var15 = Lists.newArrayList();
                     ArrayList var16 = Lists.newArrayList();
                     LinkedList var17 = Lists.newLinkedList();
                     BlockPos var18 = new BlockPos(var7.field_78897_a - var6.field_78897_a, var7.field_78895_b - var6.field_78895_b, var7.field_78896_c - var6.field_78896_c);

                     for(int var19 = var6.field_78896_c; var19 <= var6.field_78892_f; ++var19) {
                        for(int var20 = var6.field_78895_b; var20 <= var6.field_78894_e; ++var20) {
                           for(int var21 = var6.field_78897_a; var21 <= var6.field_78893_d; ++var21) {
                              BlockPos var22 = new BlockPos(var21, var20, var19);
                              BlockPos var23 = var22.func_177971_a(var18);
                              IBlockState var24 = var12.func_180495_p(var22);
                              if ((!var13 || var24.func_177230_c() != Blocks.field_150350_a) && (var10 == null || var24.func_177230_c() == var10 && (var11 < 0 || var24.func_177230_c().func_176201_c(var24) == var11))) {
                                 TileEntity var25 = var12.func_175625_s(var22);
                                 if (var25 != null) {
                                    NBTTagCompound var26 = new NBTTagCompound();
                                    var25.func_145841_b(var26);
                                    var15.add(new CommandClone.StaticCloneData(var23, var24, var26));
                                    var17.addLast(var22);
                                 } else if (!var24.func_177230_c().func_149730_j() && !var24.func_177230_c().func_149686_d()) {
                                    var16.add(new CommandClone.StaticCloneData(var23, var24, (NBTTagCompound)null));
                                    var17.addFirst(var22);
                                 } else {
                                    var14.add(new CommandClone.StaticCloneData(var23, var24, (NBTTagCompound)null));
                                    var17.addLast(var22);
                                 }
                              }
                           }
                        }
                     }

                     if (var9) {
                        Iterator var27;
                        BlockPos var29;
                        for(var27 = var17.iterator(); var27.hasNext(); var12.func_180501_a(var29, Blocks.field_180401_cv.func_176223_P(), 2)) {
                           var29 = (BlockPos)var27.next();
                           TileEntity var31 = var12.func_175625_s(var29);
                           if (var31 instanceof IInventory) {
                              ((IInventory)var31).func_174888_l();
                           }
                        }

                        var27 = var17.iterator();

                        while(var27.hasNext()) {
                           var29 = (BlockPos)var27.next();
                           var12.func_180501_a(var29, Blocks.field_150350_a.func_176223_P(), 3);
                        }
                     }

                     ArrayList var28 = Lists.newArrayList();
                     var28.addAll(var14);
                     var28.addAll(var15);
                     var28.addAll(var16);
                     List var30 = Lists.reverse(var28);

                     Iterator var32;
                     CommandClone.StaticCloneData var33;
                     TileEntity var34;
                     for(var32 = var30.iterator(); var32.hasNext(); var12.func_180501_a(var33.field_179537_a, Blocks.field_180401_cv.func_176223_P(), 2)) {
                        var33 = (CommandClone.StaticCloneData)var32.next();
                        var34 = var12.func_175625_s(var33.field_179537_a);
                        if (var34 instanceof IInventory) {
                           ((IInventory)var34).func_174888_l();
                        }
                     }

                     var8 = 0;
                     var32 = var28.iterator();

                     while(var32.hasNext()) {
                        var33 = (CommandClone.StaticCloneData)var32.next();
                        if (var12.func_180501_a(var33.field_179537_a, var33.field_179535_b, 2)) {
                           ++var8;
                        }
                     }

                     for(var32 = var15.iterator(); var32.hasNext(); var12.func_180501_a(var33.field_179537_a, var33.field_179535_b, 2)) {
                        var33 = (CommandClone.StaticCloneData)var32.next();
                        var34 = var12.func_175625_s(var33.field_179537_a);
                        if (var33.field_179536_c != null && var34 != null) {
                           var33.field_179536_c.func_74768_a("x", var33.field_179537_a.func_177958_n());
                           var33.field_179536_c.func_74768_a("y", var33.field_179537_a.func_177956_o());
                           var33.field_179536_c.func_74768_a("z", var33.field_179537_a.func_177952_p());
                           var34.func_145839_a(var33.field_179536_c);
                           var34.func_70296_d();
                        }
                     }

                     var32 = var30.iterator();

                     while(var32.hasNext()) {
                        var33 = (CommandClone.StaticCloneData)var32.next();
                        var12.func_175722_b(var33.field_179537_a, var33.field_179535_b.func_177230_c());
                     }

                     List var35 = var12.func_175712_a(var6, false);
                     if (var35 != null) {
                        Iterator var36 = var35.iterator();

                        while(var36.hasNext()) {
                           NextTickListEntry var37 = (NextTickListEntry)var36.next();
                           if (var6.func_175898_b(var37.field_180282_a)) {
                              BlockPos var38 = var37.field_180282_a.func_177971_a(var18);
                              var12.func_180497_b(var38, var37.func_151351_a(), (int)(var37.field_77180_e - var12.func_72912_H().func_82573_f()), var37.field_82754_f);
                           }
                        }
                     }

                     if (var8 <= 0) {
                        throw new CommandException("commands.clone.failed", new Object[0]);
                     } else {
                        var1.func_174794_a(CommandResultStats.Type.AFFECTED_BLOCKS, var8);
                        func_152373_a(var1, this, "commands.clone.success", new Object[]{var8});
                     }
                  } else {
                     throw new CommandException("commands.clone.outOfWorld", new Object[0]);
                  }
               } else {
                  throw new CommandException("commands.clone.outOfWorld", new Object[0]);
               }
            }
         }
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length > 0 && var2.length <= 3) {
         return func_175771_a(var2, 0, var3);
      } else if (var2.length > 3 && var2.length <= 6) {
         return func_175771_a(var2, 3, var3);
      } else if (var2.length > 6 && var2.length <= 9) {
         return func_175771_a(var2, 6, var3);
      } else if (var2.length == 10) {
         return func_71530_a(var2, new String[]{"replace", "masked", "filtered"});
      } else if (var2.length == 11) {
         return func_71530_a(var2, new String[]{"normal", "force", "move"});
      } else {
         return var2.length == 12 && "filtered".equals(var2[9]) ? func_175762_a(var2, Block.field_149771_c.func_148742_b()) : null;
      }
   }

   static class StaticCloneData {
      public final BlockPos field_179537_a;
      public final IBlockState field_179535_b;
      public final NBTTagCompound field_179536_c;

      public StaticCloneData(BlockPos var1, IBlockState var2, NBTTagCompound var3) {
         super();
         this.field_179537_a = var1;
         this.field_179535_b = var2;
         this.field_179536_c = var3;
      }
   }
}

package net.minecraft.command;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CommandCompare extends CommandBase {
   public CommandCompare() {
      super();
   }

   public String func_71517_b() {
      return "testforblocks";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.compare.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 9) {
         throw new WrongUsageException("commands.compare.usage", new Object[0]);
      } else {
         var1.func_174794_a(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
         BlockPos var3 = func_175757_a(var1, var2, 0, false);
         BlockPos var4 = func_175757_a(var1, var2, 3, false);
         BlockPos var5 = func_175757_a(var1, var2, 6, false);
         StructureBoundingBox var6 = new StructureBoundingBox(var3, var4);
         StructureBoundingBox var7 = new StructureBoundingBox(var5, var5.func_177971_a(var6.func_175896_b()));
         int var8 = var6.func_78883_b() * var6.func_78882_c() * var6.func_78880_d();
         if (var8 > 524288) {
            throw new CommandException("commands.compare.tooManyBlocks", new Object[]{var8, 524288});
         } else if (var6.field_78895_b >= 0 && var6.field_78894_e < 256 && var7.field_78895_b >= 0 && var7.field_78894_e < 256) {
            World var9 = var1.func_130014_f_();
            if (var9.func_175711_a(var6) && var9.func_175711_a(var7)) {
               boolean var10 = false;
               if (var2.length > 9 && var2[9].equals("masked")) {
                  var10 = true;
               }

               var8 = 0;
               BlockPos var11 = new BlockPos(var7.field_78897_a - var6.field_78897_a, var7.field_78895_b - var6.field_78895_b, var7.field_78896_c - var6.field_78896_c);
               BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos();
               BlockPos.MutableBlockPos var13 = new BlockPos.MutableBlockPos();

               for(int var14 = var6.field_78896_c; var14 <= var6.field_78892_f; ++var14) {
                  for(int var15 = var6.field_78895_b; var15 <= var6.field_78894_e; ++var15) {
                     for(int var16 = var6.field_78897_a; var16 <= var6.field_78893_d; ++var16) {
                        var12.func_181079_c(var16, var15, var14);
                        var13.func_181079_c(var16 + var11.func_177958_n(), var15 + var11.func_177956_o(), var14 + var11.func_177952_p());
                        boolean var17 = false;
                        IBlockState var18 = var9.func_180495_p(var12);
                        if (!var10 || var18.func_177230_c() != Blocks.field_150350_a) {
                           if (var18 == var9.func_180495_p(var13)) {
                              TileEntity var19 = var9.func_175625_s(var12);
                              TileEntity var20 = var9.func_175625_s(var13);
                              if (var19 != null && var20 != null) {
                                 NBTTagCompound var21 = new NBTTagCompound();
                                 var19.func_145841_b(var21);
                                 var21.func_82580_o("x");
                                 var21.func_82580_o("y");
                                 var21.func_82580_o("z");
                                 NBTTagCompound var22 = new NBTTagCompound();
                                 var20.func_145841_b(var22);
                                 var22.func_82580_o("x");
                                 var22.func_82580_o("y");
                                 var22.func_82580_o("z");
                                 if (!var21.equals(var22)) {
                                    var17 = true;
                                 }
                              } else if (var19 != null) {
                                 var17 = true;
                              }
                           } else {
                              var17 = true;
                           }

                           ++var8;
                           if (var17) {
                              throw new CommandException("commands.compare.failed", new Object[0]);
                           }
                        }
                     }
                  }
               }

               var1.func_174794_a(CommandResultStats.Type.AFFECTED_BLOCKS, var8);
               func_152373_a(var1, this, "commands.compare.success", new Object[]{var8});
            } else {
               throw new CommandException("commands.compare.outOfWorld", new Object[0]);
            }
         } else {
            throw new CommandException("commands.compare.outOfWorld", new Object[0]);
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
      } else {
         return var2.length == 10 ? func_71530_a(var2, new String[]{"masked", "all"}) : null;
      }
   }
}

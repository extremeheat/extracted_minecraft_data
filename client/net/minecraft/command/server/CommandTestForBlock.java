package net.minecraft.command.server;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandTestForBlock extends CommandBase {
   public CommandTestForBlock() {
      super();
   }

   public String func_71517_b() {
      return "testforblock";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.testforblock.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 4) {
         throw new WrongUsageException("commands.testforblock.usage", new Object[0]);
      } else {
         var1.func_174794_a(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
         BlockPos var3 = func_175757_a(var1, var2, 0, false);
         Block var4 = Block.func_149684_b(var2[3]);
         if (var4 == null) {
            throw new NumberInvalidException("commands.setblock.notFound", new Object[]{var2[3]});
         } else {
            int var5 = -1;
            if (var2.length >= 5) {
               var5 = func_175764_a(var2[4], -1, 15);
            }

            World var6 = var1.func_130014_f_();
            if (!var6.func_175667_e(var3)) {
               throw new CommandException("commands.testforblock.outOfWorld", new Object[0]);
            } else {
               NBTTagCompound var7 = new NBTTagCompound();
               boolean var8 = false;
               if (var2.length >= 6 && var4.func_149716_u()) {
                  String var9 = func_147178_a(var1, var2, 5).func_150260_c();

                  try {
                     var7 = JsonToNBT.func_180713_a(var9);
                     var8 = true;
                  } catch (NBTException var13) {
                     throw new CommandException("commands.setblock.tagError", new Object[]{var13.getMessage()});
                  }
               }

               IBlockState var14 = var6.func_180495_p(var3);
               Block var10 = var14.func_177230_c();
               if (var10 != var4) {
                  throw new CommandException("commands.testforblock.failed.tile", new Object[]{var3.func_177958_n(), var3.func_177956_o(), var3.func_177952_p(), var10.func_149732_F(), var4.func_149732_F()});
               } else {
                  if (var5 > -1) {
                     int var11 = var14.func_177230_c().func_176201_c(var14);
                     if (var11 != var5) {
                        throw new CommandException("commands.testforblock.failed.data", new Object[]{var3.func_177958_n(), var3.func_177956_o(), var3.func_177952_p(), var11, var5});
                     }
                  }

                  if (var8) {
                     TileEntity var15 = var6.func_175625_s(var3);
                     if (var15 == null) {
                        throw new CommandException("commands.testforblock.failed.tileEntity", new Object[]{var3.func_177958_n(), var3.func_177956_o(), var3.func_177952_p()});
                     }

                     NBTTagCompound var12 = new NBTTagCompound();
                     var15.func_145841_b(var12);
                     if (!NBTUtil.func_181123_a(var7, var12, true)) {
                        throw new CommandException("commands.testforblock.failed.nbt", new Object[]{var3.func_177958_n(), var3.func_177956_o(), var3.func_177952_p()});
                     }
                  }

                  var1.func_174794_a(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
                  func_152373_a(var1, this, "commands.testforblock.success", new Object[]{var3.func_177958_n(), var3.func_177956_o(), var3.func_177952_p()});
               }
            }
         }
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length > 0 && var2.length <= 3) {
         return func_175771_a(var2, 0, var3);
      } else {
         return var2.length == 4 ? func_175762_a(var2, Block.field_149771_c.func_148742_b()) : null;
      }
   }
}

package net.minecraft.command;

import java.util.List;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandBlockData extends CommandBase {
   public CommandBlockData() {
      super();
   }

   public String func_71517_b() {
      return "blockdata";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.blockdata.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 4) {
         throw new WrongUsageException("commands.blockdata.usage", new Object[0]);
      } else {
         var1.func_174794_a(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
         BlockPos var3 = func_175757_a(var1, var2, 0, false);
         World var4 = var1.func_130014_f_();
         if (!var4.func_175667_e(var3)) {
            throw new CommandException("commands.blockdata.outOfWorld", new Object[0]);
         } else {
            TileEntity var5 = var4.func_175625_s(var3);
            if (var5 == null) {
               throw new CommandException("commands.blockdata.notValid", new Object[0]);
            } else {
               NBTTagCompound var6 = new NBTTagCompound();
               var5.func_145841_b(var6);
               NBTTagCompound var7 = (NBTTagCompound)var6.func_74737_b();

               NBTTagCompound var8;
               try {
                  var8 = JsonToNBT.func_180713_a(func_147178_a(var1, var2, 3).func_150260_c());
               } catch (NBTException var10) {
                  throw new CommandException("commands.blockdata.tagError", new Object[]{var10.getMessage()});
               }

               var6.func_179237_a(var8);
               var6.func_74768_a("x", var3.func_177958_n());
               var6.func_74768_a("y", var3.func_177956_o());
               var6.func_74768_a("z", var3.func_177952_p());
               if (var6.equals(var7)) {
                  throw new CommandException("commands.blockdata.failed", new Object[]{var6.toString()});
               } else {
                  var5.func_145839_a(var6);
                  var5.func_70296_d();
                  var4.func_175689_h(var3);
                  var1.func_174794_a(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
                  func_152373_a(var1, this, "commands.blockdata.success", new Object[]{var6.toString()});
               }
            }
         }
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length > 0 && var2.length <= 3 ? func_175771_a(var2, 0, var3) : null;
   }
}

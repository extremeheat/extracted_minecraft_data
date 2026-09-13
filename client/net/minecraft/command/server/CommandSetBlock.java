package net.minecraft.command.server;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandSetBlock extends CommandBase {
   public CommandSetBlock() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "setblock";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.setblock.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length >= 4) {
         int var3 = var1.func_82114_b().field_71574_a;
         int var4 = var1.func_82114_b().field_71572_b;
         int var5 = var1.func_82114_b().field_71573_c;
         var3 = MathHelper.func_76128_c(func_110666_a(var1, (double)var3, var2[0]));
         var4 = MathHelper.func_76128_c(func_110666_a(var1, (double)var4, var2[1]));
         var5 = MathHelper.func_76128_c(func_110666_a(var1, (double)var5, var2[2]));
         Block var6 = CommandBase.func_147180_g(var1, var2[3]);
         int var7 = 0;
         if (var2.length >= 5) {
            var7 = func_71532_a(var1, var2[4], 0, 15);
         }

         World var8 = var1.func_130014_f_();
         if (!var8.func_72899_e(var3, var4, var5)) {
            throw new CommandException("commands.setblock.outOfWorld");
         } else {
            NBTTagCompound var9 = new NBTTagCompound();
            boolean var10 = false;
            if (var2.length >= 7 && var6.func_149716_u()) {
               String var11 = func_147178_a(var1, var2, 6).func_150260_c();

               try {
                  NBTBase var12 = JsonToNBT.func_150315_a(var11);
                  if (!(var12 instanceof NBTTagCompound)) {
                     throw new CommandException("commands.setblock.tagError", "Not a valid tag");
                  }

                  var9 = (NBTTagCompound)var12;
                  var10 = true;
               } catch (NBTException var13) {
                  throw new CommandException("commands.setblock.tagError", var13.getMessage());
               }
            }

            if (var2.length >= 6) {
               if (var2[5].equals("destroy")) {
                  var8.func_147480_a(var3, var4, var5, true);
               } else if (var2[5].equals("keep") && !var8.func_147437_c(var3, var4, var5)) {
                  throw new CommandException("commands.setblock.noChange");
               }
            }

            if (!var8.func_147465_d(var3, var4, var5, var6, var7, 3)) {
               throw new CommandException("commands.setblock.noChange");
            } else {
               if (var10) {
                  TileEntity var17 = var8.func_147438_o(var3, var4, var5);
                  if (var17 != null) {
                     var9.func_74768_a("x", var3);
                     var9.func_74768_a("y", var4);
                     var9.func_74768_a("z", var5);
                     var17.func_145839_a(var9);
                  }
               }

               func_152373_a(var1, this, "commands.setblock.success", new Object[0]);
            }
         }
      } else {
         throw new WrongUsageException("commands.setblock.usage");
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      if (var2.length == 4) {
         return func_71531_a(var2, Block.field_149771_c.func_148742_b());
      } else {
         return var2.length == 6 ? func_71530_a(var2, new String[]{"replace", "destroy", "keep"}) : null;
      }
   }
}

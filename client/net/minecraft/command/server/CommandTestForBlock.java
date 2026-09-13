package net.minecraft.command.server;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandTestForBlock extends CommandBase {
   public CommandTestForBlock() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "testforblock";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.testforblock.usage";
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
         Block var6 = Block.func_149684_b(var2[3]);
         if (var6 == null) {
            throw new NumberInvalidException("commands.setblock.notFound", var2[3]);
         } else {
            int var7 = -1;
            if (var2.length >= 5) {
               var7 = func_71532_a(var1, var2[4], -1, 15);
            }

            World var8 = var1.func_130014_f_();
            if (!var8.func_72899_e(var3, var4, var5)) {
               throw new CommandException("commands.testforblock.outOfWorld");
            } else {
               NBTTagCompound var9 = new NBTTagCompound();
               boolean var10 = false;
               if (var2.length >= 6 && var6.func_149716_u()) {
                  String var11 = func_147178_a(var1, var2, 5).func_150260_c();

                  try {
                     NBTBase var12 = JsonToNBT.func_150315_a(var11);
                     if (!(var12 instanceof NBTTagCompound)) {
                        throw new CommandException("commands.setblock.tagError", "Not a valid tag");
                     }

                     var9 = (NBTTagCompound)var12;
                     var10 = true;
                  } catch (NBTException var14) {
                     throw new CommandException("commands.setblock.tagError", var14.getMessage());
                  }
               }

               Block var18 = var8.func_147439_a(var3, var4, var5);
               if (var18 != var6) {
                  throw new CommandException("commands.testforblock.failed.tile", var3, var4, var5, var18.func_149732_F(), var6.func_149732_F());
               } else {
                  if (var7 > -1) {
                     int var19 = var8.func_72805_g(var3, var4, var5);
                     if (var19 != var7) {
                        throw new CommandException("commands.testforblock.failed.data", var3, var4, var5, var19, var7);
                     }
                  }

                  if (var10) {
                     TileEntity var20 = var8.func_147438_o(var3, var4, var5);
                     if (var20 == null) {
                        throw new CommandException("commands.testforblock.failed.tileEntity", var3, var4, var5);
                     }

                     NBTTagCompound var13 = new NBTTagCompound();
                     var20.func_145841_b(var13);
                     if (!this.func_147181_a(var9, var13)) {
                        throw new CommandException("commands.testforblock.failed.nbt", var3, var4, var5);
                     }
                  }

                  var1.func_145747_a(new ChatComponentTranslation("commands.testforblock.success", var3, var4, var5));
               }
            }
         }
      } else {
         throw new WrongUsageException("commands.testforblock.usage");
      }
   }

   public boolean func_147181_a(NBTBase var1, NBTBase var2) {
      if (var1 == var2) {
         return true;
      } else if (var1 == null) {
         return true;
      } else if (var2 == null) {
         return false;
      } else if (!var1.getClass().equals(var2.getClass())) {
         return false;
      } else if (var1 instanceof NBTTagCompound) {
         NBTTagCompound var3 = (NBTTagCompound)var1;
         NBTTagCompound var4 = (NBTTagCompound)var2;

         for(String var6 : var3.func_150296_c()) {
            NBTBase var7 = var3.func_74781_a(var6);
            if (!this.func_147181_a(var7, var4.func_74781_a(var6))) {
               return false;
            }
         }

         return true;
      } else {
         return var1.equals(var2);
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return var2.length == 4 ? func_71531_a(var2, Block.field_149771_c.func_148742_b()) : null;
   }
}

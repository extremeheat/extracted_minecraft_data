package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class CommandGive extends CommandBase {
   public CommandGive() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "give";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.give.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.give.usage");
      } else {
         EntityPlayerMP var3 = func_82359_c(var1, var2[0]);
         Item var4 = func_147179_f(var1, var2[1]);
         int var5 = 1;
         int var6 = 0;
         if (var2.length >= 3) {
            var5 = func_71532_a(var1, var2[2], 1, 64);
         }

         if (var2.length >= 4) {
            var6 = func_71526_a(var1, var2[3]);
         }

         ItemStack var7 = new ItemStack(var4, var5, var6);
         if (var2.length >= 5) {
            String var8 = func_147178_a(var1, var2, 4).func_150260_c();

            try {
               NBTBase var9 = JsonToNBT.func_150315_a(var8);
               if (!(var9 instanceof NBTTagCompound)) {
                  func_152373_a(var1, this, "commands.give.tagError", new Object[]{"Not a valid tag"});
                  return;
               }

               var7.func_77982_d((NBTTagCompound)var9);
            } catch (NBTException var10) {
               func_152373_a(var1, this, "commands.give.tagError", new Object[]{var10.getMessage()});
               return;
            }
         }

         EntityItem var11 = var3.func_71019_a(var7, false);
         var11.field_145804_b = 0;
         var11.func_145797_a(var3.func_70005_c_());
         func_152373_a(var1, this, "commands.give.success", new Object[]{var7.func_151000_E(), var5, var3.func_70005_c_()});
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      if (var2.length == 1) {
         return func_71530_a(var2, this.func_71536_c());
      } else {
         return var2.length == 2 ? func_71531_a(var2, Item.field_150901_e.func_148742_b()) : null;
      }
   }

   protected String[] func_71536_c() {
      return MinecraftServer.func_71276_C().func_71213_z();
   }

   @Override
   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}

package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandGive extends CommandBase {
   public CommandGive() {
      super();
   }

   public String func_71517_b() {
      return "give";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.give.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.give.usage", new Object[0]);
      } else {
         EntityPlayerMP var3 = func_82359_c(var1, var2[0]);
         Item var4 = func_147179_f(var1, var2[1]);
         int var5 = var2.length >= 3 ? func_175764_a(var2[2], 1, 64) : 1;
         int var6 = var2.length >= 4 ? func_175755_a(var2[3]) : 0;
         ItemStack var7 = new ItemStack(var4, var5, var6);
         if (var2.length >= 5) {
            String var8 = func_147178_a(var1, var2, 4).func_150260_c();

            try {
               var7.func_77982_d(JsonToNBT.func_180713_a(var8));
            } catch (NBTException var10) {
               throw new CommandException("commands.give.tagError", new Object[]{var10.getMessage()});
            }
         }

         boolean var11 = var3.field_71071_by.func_70441_a(var7);
         if (var11) {
            var3.field_70170_p.func_72956_a(var3, "random.pop", 0.2F, ((var3.func_70681_au().nextFloat() - var3.func_70681_au().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            var3.field_71069_bz.func_75142_b();
         }

         EntityItem var9;
         if (var11 && var7.field_77994_a <= 0) {
            var7.field_77994_a = 1;
            var1.func_174794_a(CommandResultStats.Type.AFFECTED_ITEMS, var5);
            var9 = var3.func_71019_a(var7, false);
            if (var9 != null) {
               var9.func_174870_v();
            }
         } else {
            var1.func_174794_a(CommandResultStats.Type.AFFECTED_ITEMS, var5 - var7.field_77994_a);
            var9 = var3.func_71019_a(var7, false);
            if (var9 != null) {
               var9.func_174868_q();
               var9.func_145797_a(var3.func_70005_c_());
            }
         }

         func_152373_a(var1, this, "commands.give.success", new Object[]{var7.func_151000_E(), var5, var3.func_70005_c_()});
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return func_71530_a(var2, this.func_71536_c());
      } else {
         return var2.length == 2 ? func_175762_a(var2, Item.field_150901_e.func_148742_b()) : null;
      }
   }

   protected String[] func_71536_c() {
      return MinecraftServer.func_71276_C().func_71213_z();
   }

   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}

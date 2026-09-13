package net.minecraft.command;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;

public class CommandEnchant extends CommandBase {
   public CommandEnchant() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "enchant";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.enchant.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.enchant.usage");
      } else {
         EntityPlayerMP var3 = func_82359_c(var1, var2[0]);
         int var4 = func_71532_a(var1, var2[1], 0, Enchantment.field_77331_b.length - 1);
         int var5 = 1;
         ItemStack var6 = var3.func_71045_bC();
         if (var6 == null) {
            throw new CommandException("commands.enchant.noItem");
         } else {
            Enchantment var7 = Enchantment.field_77331_b[var4];
            if (var7 == null) {
               throw new NumberInvalidException("commands.enchant.notFound", var4);
            } else if (!var7.func_92089_a(var6)) {
               throw new CommandException("commands.enchant.cantEnchant");
            } else {
               if (var2.length >= 3) {
                  var5 = func_71532_a(var1, var2[2], var7.func_77319_d(), var7.func_77325_b());
               }

               if (var6.func_77942_o()) {
                  NBTTagList var8 = var6.func_77986_q();
                  if (var8 != null) {
                     for(int var9 = 0; var9 < var8.func_74745_c(); ++var9) {
                        short var10 = var8.func_150305_b(var9).func_74765_d("id");
                        if (Enchantment.field_77331_b[var10] != null) {
                           Enchantment var11 = Enchantment.field_77331_b[var10];
                           if (!var11.func_77326_a(var7)) {
                              throw new CommandException(
                                 "commands.enchant.cantCombine", var7.func_77316_c(var5), var11.func_77316_c(var8.func_150305_b(var9).func_74765_d("lvl"))
                              );
                           }
                        }
                     }
                  }
               }

               var6.func_77966_a(var7, var5);
               func_152373_a(var1, this, "commands.enchant.success", new Object[0]);
            }
         }
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return var2.length == 1 ? func_71530_a(var2, this.func_90022_d()) : null;
   }

   protected String[] func_90022_d() {
      return MinecraftServer.func_71276_C().func_71213_z();
   }

   @Override
   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}

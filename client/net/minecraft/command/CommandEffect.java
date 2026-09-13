package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

public class CommandEffect extends CommandBase {
   public CommandEffect() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "effect";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.effect.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.effect.usage");
      } else {
         EntityPlayerMP var3 = func_82359_c(var1, var2[0]);
         if (var2[1].equals("clear")) {
            if (var3.func_70651_bq().isEmpty()) {
               throw new CommandException("commands.effect.failure.notActive.all", var3.func_70005_c_());
            }

            var3.func_70674_bp();
            func_152373_a(var1, this, "commands.effect.success.removed.all", new Object[]{var3.func_70005_c_()});
         } else {
            int var4 = func_71528_a(var1, var2[1], 1);
            int var5 = 600;
            int var6 = 30;
            int var7 = 0;
            if (var4 < 0 || var4 >= Potion.field_76425_a.length || Potion.field_76425_a[var4] == null) {
               throw new NumberInvalidException("commands.effect.notFound", var4);
            }

            if (var2.length >= 3) {
               var6 = func_71532_a(var1, var2[2], 0, 1000000);
               if (Potion.field_76425_a[var4].func_76403_b()) {
                  var5 = var6;
               } else {
                  var5 = var6 * 20;
               }
            } else if (Potion.field_76425_a[var4].func_76403_b()) {
               var5 = 1;
            }

            if (var2.length >= 4) {
               var7 = func_71532_a(var1, var2[3], 0, 255);
            }

            if (var6 == 0) {
               if (!var3.func_82165_m(var4)) {
                  throw new CommandException(
                     "commands.effect.failure.notActive", new ChatComponentTranslation(Potion.field_76425_a[var4].func_76393_a()), var3.func_70005_c_()
                  );
               }

               var3.func_82170_o(var4);
               func_152373_a(
                  var1,
                  this,
                  "commands.effect.success.removed",
                  new Object[]{new ChatComponentTranslation(Potion.field_76425_a[var4].func_76393_a()), var3.func_70005_c_()}
               );
            } else {
               PotionEffect var8 = new PotionEffect(var4, var5, var7);
               var3.func_70690_d(var8);
               func_152373_a(
                  var1,
                  this,
                  "commands.effect.success",
                  new Object[]{new ChatComponentTranslation(var8.func_76453_d()), var4, var7, var3.func_70005_c_(), var6}
               );
            }
         }
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return var2.length == 1 ? func_71530_a(var2, this.func_98152_d()) : null;
   }

   protected String[] func_98152_d() {
      return MinecraftServer.func_71276_C().func_71213_z();
   }

   @Override
   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}

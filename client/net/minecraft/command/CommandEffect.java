package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

public class CommandEffect extends CommandBase {
   public CommandEffect() {
      super();
   }

   public String func_71517_b() {
      return "effect";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.effect.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.effect.usage", new Object[0]);
      } else {
         EntityLivingBase var3 = (EntityLivingBase)func_175759_a(var1, var2[0], EntityLivingBase.class);
         if (var2[1].equals("clear")) {
            if (var3.func_70651_bq().isEmpty()) {
               throw new CommandException("commands.effect.failure.notActive.all", new Object[]{var3.func_70005_c_()});
            } else {
               var3.func_70674_bp();
               func_152373_a(var1, this, "commands.effect.success.removed.all", new Object[]{var3.func_70005_c_()});
            }
         } else {
            int var4;
            try {
               var4 = func_180528_a(var2[1], 1);
            } catch (NumberInvalidException var11) {
               Potion var6 = Potion.func_180142_b(var2[1]);
               if (var6 == null) {
                  throw var11;
               }

               var4 = var6.field_76415_H;
            }

            int var5 = 600;
            int var12 = 30;
            int var7 = 0;
            if (var4 >= 0 && var4 < Potion.field_76425_a.length && Potion.field_76425_a[var4] != null) {
               Potion var8 = Potion.field_76425_a[var4];
               if (var2.length >= 3) {
                  var12 = func_175764_a(var2[2], 0, 1000000);
                  if (var8.func_76403_b()) {
                     var5 = var12;
                  } else {
                     var5 = var12 * 20;
                  }
               } else if (var8.func_76403_b()) {
                  var5 = 1;
               }

               if (var2.length >= 4) {
                  var7 = func_175764_a(var2[3], 0, 255);
               }

               boolean var9 = true;
               if (var2.length >= 5 && "true".equalsIgnoreCase(var2[4])) {
                  var9 = false;
               }

               if (var12 > 0) {
                  PotionEffect var10 = new PotionEffect(var4, var5, var7, false, var9);
                  var3.func_70690_d(var10);
                  func_152373_a(var1, this, "commands.effect.success", new Object[]{new ChatComponentTranslation(var10.func_76453_d(), new Object[0]), var4, var7, var3.func_70005_c_(), var12});
               } else if (var3.func_82165_m(var4)) {
                  var3.func_82170_o(var4);
                  func_152373_a(var1, this, "commands.effect.success.removed", new Object[]{new ChatComponentTranslation(var8.func_76393_a(), new Object[0]), var3.func_70005_c_()});
               } else {
                  throw new CommandException("commands.effect.failure.notActive", new Object[]{new ChatComponentTranslation(var8.func_76393_a(), new Object[0]), var3.func_70005_c_()});
               }
            } else {
               throw new NumberInvalidException("commands.effect.notFound", new Object[]{var4});
            }
         }
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return func_71530_a(var2, this.func_98152_d());
      } else if (var2.length == 2) {
         return func_175762_a(var2, Potion.func_181168_c());
      } else {
         return var2.length == 5 ? func_71530_a(var2, new String[]{"true", "false"}) : null;
      }
   }

   protected String[] func_98152_d() {
      return MinecraftServer.func_71276_C().func_71213_z();
   }

   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}

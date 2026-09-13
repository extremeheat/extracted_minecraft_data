package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;

public class CommandPlaySound extends CommandBase {
   public CommandPlaySound() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "playsound";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.playsound.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length < 2) {
         throw new WrongUsageException(this.func_71518_a(var1));
      } else {
         int var3 = 0;
         String var4 = var2[var3++];
         EntityPlayerMP var5 = func_82359_c(var1, var2[var3++]);
         double var6 = (double)var5.func_82114_b().field_71574_a;
         double var8 = (double)var5.func_82114_b().field_71572_b;
         double var10 = (double)var5.func_82114_b().field_71573_c;
         double var12 = 1.0;
         double var14 = 1.0;
         double var16 = 0.0;
         if (var2.length > var3) {
            var6 = func_110666_a(var1, var6, var2[var3++]);
         }

         if (var2.length > var3) {
            var8 = func_110665_a(var1, var8, var2[var3++], 0, 0);
         }

         if (var2.length > var3) {
            var10 = func_110666_a(var1, var10, var2[var3++]);
         }

         if (var2.length > var3) {
            var12 = func_110661_a(var1, var2[var3++], 0.0, 3.4028234663852886E38);
         }

         if (var2.length > var3) {
            var14 = func_110661_a(var1, var2[var3++], 0.0, 2.0);
         }

         if (var2.length > var3) {
            var16 = func_110661_a(var1, var2[var3++], 0.0, 1.0);
         }

         double var18 = var12 > 1.0 ? var12 * 16.0 : 16.0;
         double var20 = var5.func_70011_f(var6, var8, var10);
         if (var20 > var18) {
            if (!(var16 > 0.0)) {
               throw new CommandException("commands.playsound.playerTooFar", var5.func_70005_c_());
            }

            double var22 = var6 - var5.field_70165_t;
            double var24 = var8 - var5.field_70163_u;
            double var26 = var10 - var5.field_70161_v;
            double var28 = Math.sqrt(var22 * var22 + var24 * var24 + var26 * var26);
            double var30 = var5.field_70165_t;
            double var32 = var5.field_70163_u;
            double var34 = var5.field_70161_v;
            if (var28 > 0.0) {
               var30 += var22 / var28 * 2.0;
               var32 += var24 / var28 * 2.0;
               var34 += var26 / var28 * 2.0;
            }

            var5.field_71135_a.func_147359_a(new S29PacketSoundEffect(var4, var30, var32, var34, (float)var16, (float)var14));
         } else {
            var5.field_71135_a.func_147359_a(new S29PacketSoundEffect(var4, var6, var8, var10, (float)var12, (float)var14));
         }

         func_152373_a(var1, this, "commands.playsound.success", new Object[]{var4, var5.func_70005_c_()});
      }
   }

   @Override
   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 1;
   }
}

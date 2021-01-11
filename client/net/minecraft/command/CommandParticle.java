package net.minecraft.command;

import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class CommandParticle extends CommandBase {
   public CommandParticle() {
      super();
   }

   public String func_71517_b() {
      return "particle";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.particle.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 8) {
         throw new WrongUsageException("commands.particle.usage", new Object[0]);
      } else {
         boolean var3 = false;
         EnumParticleTypes var4 = null;
         EnumParticleTypes[] var5 = EnumParticleTypes.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            EnumParticleTypes var8 = var5[var7];
            if (var8.func_179343_f()) {
               if (var2[0].startsWith(var8.func_179346_b())) {
                  var3 = true;
                  var4 = var8;
                  break;
               }
            } else if (var2[0].equals(var8.func_179346_b())) {
               var3 = true;
               var4 = var8;
               break;
            }
         }

         if (!var3) {
            throw new CommandException("commands.particle.notFound", new Object[]{var2[0]});
         } else {
            String var30 = var2[0];
            Vec3 var31 = var1.func_174791_d();
            double var32 = (double)((float)func_175761_b(var31.field_72450_a, var2[1], true));
            double var9 = (double)((float)func_175761_b(var31.field_72448_b, var2[2], true));
            double var11 = (double)((float)func_175761_b(var31.field_72449_c, var2[3], true));
            double var13 = (double)((float)func_175765_c(var2[4]));
            double var15 = (double)((float)func_175765_c(var2[5]));
            double var17 = (double)((float)func_175765_c(var2[6]));
            double var19 = (double)((float)func_175765_c(var2[7]));
            int var21 = 0;
            if (var2.length > 8) {
               var21 = func_180528_a(var2[8], 0);
            }

            boolean var22 = false;
            if (var2.length > 9 && "force".equals(var2[9])) {
               var22 = true;
            }

            World var23 = var1.func_130014_f_();
            if (var23 instanceof WorldServer) {
               WorldServer var24 = (WorldServer)var23;
               int[] var25 = new int[var4.func_179345_d()];
               if (var4.func_179343_f()) {
                  String[] var26 = var2[0].split("_", 3);

                  for(int var27 = 1; var27 < var26.length; ++var27) {
                     try {
                        var25[var27 - 1] = Integer.parseInt(var26[var27]);
                     } catch (NumberFormatException var29) {
                        throw new CommandException("commands.particle.notFound", new Object[]{var2[0]});
                     }
                  }
               }

               var24.func_180505_a(var4, var22, var32, var9, var11, var21, var13, var15, var17, var19, var25);
               func_152373_a(var1, this, "commands.particle.success", new Object[]{var30, Math.max(var21, 1)});
            }

         }
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return func_71530_a(var2, EnumParticleTypes.func_179349_a());
      } else if (var2.length > 1 && var2.length <= 4) {
         return func_175771_a(var2, 1, var3);
      } else {
         return var2.length == 10 ? func_71530_a(var2, new String[]{"normal", "force"}) : null;
      }
   }
}

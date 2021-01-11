package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.border.WorldBorder;

public class CommandWorldBorder extends CommandBase {
   public CommandWorldBorder() {
      super();
   }

   public String func_71517_b() {
      return "worldborder";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.worldborder.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new WrongUsageException("commands.worldborder.usage", new Object[0]);
      } else {
         WorldBorder var3 = this.func_175772_d();
         double var4;
         double var6;
         long var8;
         if (var2[0].equals("set")) {
            if (var2.length != 2 && var2.length != 3) {
               throw new WrongUsageException("commands.worldborder.set.usage", new Object[0]);
            }

            var4 = var3.func_177751_j();
            var6 = func_175756_a(var2[1], 1.0D, 6.0E7D);
            var8 = var2.length > 2 ? func_175760_a(var2[2], 0L, 9223372036854775L) * 1000L : 0L;
            if (var8 > 0L) {
               var3.func_177738_a(var4, var6, var8);
               if (var4 > var6) {
                  func_152373_a(var1, this, "commands.worldborder.setSlowly.shrink.success", new Object[]{String.format("%.1f", var6), String.format("%.1f", var4), Long.toString(var8 / 1000L)});
               } else {
                  func_152373_a(var1, this, "commands.worldborder.setSlowly.grow.success", new Object[]{String.format("%.1f", var6), String.format("%.1f", var4), Long.toString(var8 / 1000L)});
               }
            } else {
               var3.func_177750_a(var6);
               func_152373_a(var1, this, "commands.worldborder.set.success", new Object[]{String.format("%.1f", var6), String.format("%.1f", var4)});
            }
         } else if (var2[0].equals("add")) {
            if (var2.length != 2 && var2.length != 3) {
               throw new WrongUsageException("commands.worldborder.add.usage", new Object[0]);
            }

            var4 = var3.func_177741_h();
            var6 = var4 + func_175756_a(var2[1], -var4, 6.0E7D - var4);
            var8 = var3.func_177732_i() + (var2.length > 2 ? func_175760_a(var2[2], 0L, 9223372036854775L) * 1000L : 0L);
            if (var8 > 0L) {
               var3.func_177738_a(var4, var6, var8);
               if (var4 > var6) {
                  func_152373_a(var1, this, "commands.worldborder.setSlowly.shrink.success", new Object[]{String.format("%.1f", var6), String.format("%.1f", var4), Long.toString(var8 / 1000L)});
               } else {
                  func_152373_a(var1, this, "commands.worldborder.setSlowly.grow.success", new Object[]{String.format("%.1f", var6), String.format("%.1f", var4), Long.toString(var8 / 1000L)});
               }
            } else {
               var3.func_177750_a(var6);
               func_152373_a(var1, this, "commands.worldborder.set.success", new Object[]{String.format("%.1f", var6), String.format("%.1f", var4)});
            }
         } else if (var2[0].equals("center")) {
            if (var2.length != 3) {
               throw new WrongUsageException("commands.worldborder.center.usage", new Object[0]);
            }

            BlockPos var10 = var1.func_180425_c();
            double var5 = func_175761_b((double)var10.func_177958_n() + 0.5D, var2[1], true);
            double var7 = func_175761_b((double)var10.func_177952_p() + 0.5D, var2[2], true);
            var3.func_177739_c(var5, var7);
            func_152373_a(var1, this, "commands.worldborder.center.success", new Object[]{var5, var7});
         } else if (var2[0].equals("damage")) {
            if (var2.length < 2) {
               throw new WrongUsageException("commands.worldborder.damage.usage", new Object[0]);
            }

            if (var2[1].equals("buffer")) {
               if (var2.length != 3) {
                  throw new WrongUsageException("commands.worldborder.damage.buffer.usage", new Object[0]);
               }

               var4 = func_180526_a(var2[2], 0.0D);
               var6 = var3.func_177742_m();
               var3.func_177724_b(var4);
               func_152373_a(var1, this, "commands.worldborder.damage.buffer.success", new Object[]{String.format("%.1f", var4), String.format("%.1f", var6)});
            } else if (var2[1].equals("amount")) {
               if (var2.length != 3) {
                  throw new WrongUsageException("commands.worldborder.damage.amount.usage", new Object[0]);
               }

               var4 = func_180526_a(var2[2], 0.0D);
               var6 = var3.func_177727_n();
               var3.func_177744_c(var4);
               func_152373_a(var1, this, "commands.worldborder.damage.amount.success", new Object[]{String.format("%.2f", var4), String.format("%.2f", var6)});
            }
         } else if (var2[0].equals("warning")) {
            if (var2.length < 2) {
               throw new WrongUsageException("commands.worldborder.warning.usage", new Object[0]);
            }

            int var12 = func_180528_a(var2[2], 0);
            int var11;
            if (var2[1].equals("time")) {
               if (var2.length != 3) {
                  throw new WrongUsageException("commands.worldborder.warning.time.usage", new Object[0]);
               }

               var11 = var3.func_177740_p();
               var3.func_177723_b(var12);
               func_152373_a(var1, this, "commands.worldborder.warning.time.success", new Object[]{var12, var11});
            } else if (var2[1].equals("distance")) {
               if (var2.length != 3) {
                  throw new WrongUsageException("commands.worldborder.warning.distance.usage", new Object[0]);
               }

               var11 = var3.func_177748_q();
               var3.func_177747_c(var12);
               func_152373_a(var1, this, "commands.worldborder.warning.distance.success", new Object[]{var12, var11});
            }
         } else {
            if (!var2[0].equals("get")) {
               throw new WrongUsageException("commands.worldborder.usage", new Object[0]);
            }

            var4 = var3.func_177741_h();
            var1.func_174794_a(CommandResultStats.Type.QUERY_RESULT, MathHelper.func_76128_c(var4 + 0.5D));
            var1.func_145747_a(new ChatComponentTranslation("commands.worldborder.get.success", new Object[]{String.format("%.0f", var4)}));
         }

      }
   }

   protected WorldBorder func_175772_d() {
      return MinecraftServer.func_71276_C().field_71305_c[0].func_175723_af();
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return func_71530_a(var2, new String[]{"set", "center", "damage", "warning", "add", "get"});
      } else if (var2.length == 2 && var2[0].equals("damage")) {
         return func_71530_a(var2, new String[]{"buffer", "amount"});
      } else if (var2.length >= 2 && var2.length <= 3 && var2[0].equals("center")) {
         return func_181043_b(var2, 1, var3);
      } else {
         return var2.length == 2 && var2[0].equals("warning") ? func_71530_a(var2, new String[]{"time", "distance"}) : null;
      }
   }
}

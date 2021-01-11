package net.minecraft.command;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandExecuteAt extends CommandBase {
   public CommandExecuteAt() {
      super();
   }

   public String func_71517_b() {
      return "execute";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.execute.usage";
   }

   public void func_71515_b(final ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 5) {
         throw new WrongUsageException("commands.execute.usage", new Object[0]);
      } else {
         final Entity var3 = func_175759_a(var1, var2[0], Entity.class);
         final double var4 = func_175761_b(var3.field_70165_t, var2[1], false);
         final double var6 = func_175761_b(var3.field_70163_u, var2[2], false);
         final double var8 = func_175761_b(var3.field_70161_v, var2[3], false);
         final BlockPos var10 = new BlockPos(var4, var6, var8);
         byte var11 = 4;
         if ("detect".equals(var2[4]) && var2.length > 10) {
            World var12 = var3.func_130014_f_();
            double var13 = func_175761_b(var4, var2[5], false);
            double var15 = func_175761_b(var6, var2[6], false);
            double var17 = func_175761_b(var8, var2[7], false);
            Block var19 = func_147180_g(var1, var2[8]);
            int var20 = func_175764_a(var2[9], -1, 15);
            BlockPos var21 = new BlockPos(var13, var15, var17);
            IBlockState var22 = var12.func_180495_p(var21);
            if (var22.func_177230_c() != var19 || var20 >= 0 && var22.func_177230_c().func_176201_c(var22) != var20) {
               throw new CommandException("commands.execute.failed", new Object[]{"detect", var3.func_70005_c_()});
            }

            var11 = 10;
         }

         String var24 = func_180529_a(var2, var11);
         ICommandSender var14 = new ICommandSender() {
            public String func_70005_c_() {
               return var3.func_70005_c_();
            }

            public IChatComponent func_145748_c_() {
               return var3.func_145748_c_();
            }

            public void func_145747_a(IChatComponent var1x) {
               var1.func_145747_a(var1x);
            }

            public boolean func_70003_b(int var1x, String var2) {
               return var1.func_70003_b(var1x, var2);
            }

            public BlockPos func_180425_c() {
               return var10;
            }

            public Vec3 func_174791_d() {
               return new Vec3(var4, var6, var8);
            }

            public World func_130014_f_() {
               return var3.field_70170_p;
            }

            public Entity func_174793_f() {
               return var3;
            }

            public boolean func_174792_t_() {
               MinecraftServer var1x = MinecraftServer.func_71276_C();
               return var1x == null || var1x.field_71305_c[0].func_82736_K().func_82766_b("commandBlockOutput");
            }

            public void func_174794_a(CommandResultStats.Type var1x, int var2) {
               var3.func_174794_a(var1x, var2);
            }
         };
         ICommandManager var25 = MinecraftServer.func_71276_C().func_71187_D();

         try {
            int var16 = var25.func_71556_a(var14, var24);
            if (var16 < 1) {
               throw new CommandException("commands.execute.allInvocationsFailed", new Object[]{var24});
            }
         } catch (Throwable var23) {
            throw new CommandException("commands.execute.failed", new Object[]{var24, var3.func_70005_c_()});
         }
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z());
      } else if (var2.length > 1 && var2.length <= 4) {
         return func_175771_a(var2, 1, var3);
      } else if (var2.length > 5 && var2.length <= 8 && "detect".equals(var2[4])) {
         return func_175771_a(var2, 5, var3);
      } else {
         return var2.length == 9 && "detect".equals(var2[4]) ? func_175762_a(var2, Block.field_149771_c.func_148742_b()) : null;
      }
   }

   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}

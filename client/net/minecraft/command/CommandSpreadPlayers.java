package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandSpreadPlayers extends CommandBase {
   public CommandSpreadPlayers() {
      super();
   }

   public String func_71517_b() {
      return "spreadplayers";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.spreadplayers.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 6) {
         throw new WrongUsageException("commands.spreadplayers.usage", new Object[0]);
      } else {
         byte var3 = 0;
         BlockPos var4 = var1.func_180425_c();
         double var10000 = (double)var4.func_177958_n();
         int var17 = var3 + 1;
         double var5 = func_175761_b(var10000, var2[var3], true);
         double var7 = func_175761_b((double)var4.func_177952_p(), var2[var17++], true);
         double var9 = func_180526_a(var2[var17++], 0.0D);
         double var11 = func_180526_a(var2[var17++], var9 + 1.0D);
         boolean var13 = func_180527_d(var2[var17++]);
         ArrayList var14 = Lists.newArrayList();

         while(var17 < var2.length) {
            String var15 = var2[var17++];
            if (PlayerSelector.func_82378_b(var15)) {
               List var16 = PlayerSelector.func_179656_b(var1, var15, Entity.class);
               if (var16.size() == 0) {
                  throw new EntityNotFoundException();
               }

               var14.addAll(var16);
            } else {
               EntityPlayerMP var18 = MinecraftServer.func_71276_C().func_71203_ab().func_152612_a(var15);
               if (var18 == null) {
                  throw new PlayerNotFoundException();
               }

               var14.add(var18);
            }
         }

         var1.func_174794_a(CommandResultStats.Type.AFFECTED_ENTITIES, var14.size());
         if (var14.isEmpty()) {
            throw new EntityNotFoundException();
         } else {
            var1.func_145747_a(new ChatComponentTranslation("commands.spreadplayers.spreading." + (var13 ? "teams" : "players"), new Object[]{var14.size(), var11, var5, var7, var9}));
            this.func_110669_a(var1, var14, new CommandSpreadPlayers.Position(var5, var7), var9, var11, ((Entity)var14.get(0)).field_70170_p, var13);
         }
      }
   }

   private void func_110669_a(ICommandSender var1, List<Entity> var2, CommandSpreadPlayers.Position var3, double var4, double var6, World var8, boolean var9) throws CommandException {
      Random var10 = new Random();
      double var11 = var3.field_111101_a - var6;
      double var13 = var3.field_111100_b - var6;
      double var15 = var3.field_111101_a + var6;
      double var17 = var3.field_111100_b + var6;
      CommandSpreadPlayers.Position[] var19 = this.func_110670_a(var10, var9 ? this.func_110667_a(var2) : var2.size(), var11, var13, var15, var17);
      int var20 = this.func_110668_a(var3, var4, var8, var10, var11, var13, var15, var17, var19, var9);
      double var21 = this.func_110671_a(var2, var8, var19, var9);
      func_152373_a(var1, this, "commands.spreadplayers.success." + (var9 ? "teams" : "players"), new Object[]{var19.length, var3.field_111101_a, var3.field_111100_b});
      if (var19.length > 1) {
         var1.func_145747_a(new ChatComponentTranslation("commands.spreadplayers.info." + (var9 ? "teams" : "players"), new Object[]{String.format("%.2f", var21), var20}));
      }

   }

   private int func_110667_a(List<Entity> var1) {
      HashSet var2 = Sets.newHashSet();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Entity var4 = (Entity)var3.next();
         if (var4 instanceof EntityPlayer) {
            var2.add(((EntityPlayer)var4).func_96124_cp());
         } else {
            var2.add((Object)null);
         }
      }

      return var2.size();
   }

   private int func_110668_a(CommandSpreadPlayers.Position var1, double var2, World var4, Random var5, double var6, double var8, double var10, double var12, CommandSpreadPlayers.Position[] var14, boolean var15) throws CommandException {
      boolean var16 = true;
      double var18 = 3.4028234663852886E38D;

      int var17;
      for(var17 = 0; var17 < 10000 && var16; ++var17) {
         var16 = false;
         var18 = 3.4028234663852886E38D;

         int var22;
         CommandSpreadPlayers.Position var23;
         for(int var20 = 0; var20 < var14.length; ++var20) {
            CommandSpreadPlayers.Position var21 = var14[var20];
            var22 = 0;
            var23 = new CommandSpreadPlayers.Position();

            for(int var24 = 0; var24 < var14.length; ++var24) {
               if (var20 != var24) {
                  CommandSpreadPlayers.Position var25 = var14[var24];
                  double var26 = var21.func_111099_a(var25);
                  var18 = Math.min(var26, var18);
                  if (var26 < var2) {
                     ++var22;
                     var23.field_111101_a += var25.field_111101_a - var21.field_111101_a;
                     var23.field_111100_b += var25.field_111100_b - var21.field_111100_b;
                  }
               }
            }

            if (var22 > 0) {
               var23.field_111101_a /= (double)var22;
               var23.field_111100_b /= (double)var22;
               double var30 = (double)var23.func_111096_b();
               if (var30 > 0.0D) {
                  var23.func_111095_a();
                  var21.func_111094_b(var23);
               } else {
                  var21.func_111097_a(var5, var6, var8, var10, var12);
               }

               var16 = true;
            }

            if (var21.func_111093_a(var6, var8, var10, var12)) {
               var16 = true;
            }
         }

         if (!var16) {
            CommandSpreadPlayers.Position[] var28 = var14;
            int var29 = var14.length;

            for(var22 = 0; var22 < var29; ++var22) {
               var23 = var28[var22];
               if (!var23.func_111098_b(var4)) {
                  var23.func_111097_a(var5, var6, var8, var10, var12);
                  var16 = true;
               }
            }
         }
      }

      if (var17 >= 10000) {
         throw new CommandException("commands.spreadplayers.failure." + (var15 ? "teams" : "players"), new Object[]{var14.length, var1.field_111101_a, var1.field_111100_b, String.format("%.2f", var18)});
      } else {
         return var17;
      }
   }

   private double func_110671_a(List<Entity> var1, World var2, CommandSpreadPlayers.Position[] var3, boolean var4) {
      double var5 = 0.0D;
      int var7 = 0;
      HashMap var8 = Maps.newHashMap();

      for(int var9 = 0; var9 < var1.size(); ++var9) {
         Entity var10 = (Entity)var1.get(var9);
         CommandSpreadPlayers.Position var11;
         if (var4) {
            Team var12 = var10 instanceof EntityPlayer ? ((EntityPlayer)var10).func_96124_cp() : null;
            if (!var8.containsKey(var12)) {
               var8.put(var12, var3[var7++]);
            }

            var11 = (CommandSpreadPlayers.Position)var8.get(var12);
         } else {
            var11 = var3[var7++];
         }

         var10.func_70634_a((double)((float)MathHelper.func_76128_c(var11.field_111101_a) + 0.5F), (double)var11.func_111092_a(var2), (double)MathHelper.func_76128_c(var11.field_111100_b) + 0.5D);
         double var17 = 1.7976931348623157E308D;

         for(int var14 = 0; var14 < var3.length; ++var14) {
            if (var11 != var3[var14]) {
               double var15 = var11.func_111099_a(var3[var14]);
               var17 = Math.min(var15, var17);
            }
         }

         var5 += var17;
      }

      var5 /= (double)var1.size();
      return var5;
   }

   private CommandSpreadPlayers.Position[] func_110670_a(Random var1, int var2, double var3, double var5, double var7, double var9) {
      CommandSpreadPlayers.Position[] var11 = new CommandSpreadPlayers.Position[var2];

      for(int var12 = 0; var12 < var11.length; ++var12) {
         CommandSpreadPlayers.Position var13 = new CommandSpreadPlayers.Position();
         var13.func_111097_a(var1, var3, var5, var7, var9);
         var11[var12] = var13;
      }

      return var11;
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length >= 1 && var2.length <= 2 ? func_181043_b(var2, 0, var3) : null;
   }

   static class Position {
      double field_111101_a;
      double field_111100_b;

      Position() {
         super();
      }

      Position(double var1, double var3) {
         super();
         this.field_111101_a = var1;
         this.field_111100_b = var3;
      }

      double func_111099_a(CommandSpreadPlayers.Position var1) {
         double var2 = this.field_111101_a - var1.field_111101_a;
         double var4 = this.field_111100_b - var1.field_111100_b;
         return Math.sqrt(var2 * var2 + var4 * var4);
      }

      void func_111095_a() {
         double var1 = (double)this.func_111096_b();
         this.field_111101_a /= var1;
         this.field_111100_b /= var1;
      }

      float func_111096_b() {
         return MathHelper.func_76133_a(this.field_111101_a * this.field_111101_a + this.field_111100_b * this.field_111100_b);
      }

      public void func_111094_b(CommandSpreadPlayers.Position var1) {
         this.field_111101_a -= var1.field_111101_a;
         this.field_111100_b -= var1.field_111100_b;
      }

      public boolean func_111093_a(double var1, double var3, double var5, double var7) {
         boolean var9 = false;
         if (this.field_111101_a < var1) {
            this.field_111101_a = var1;
            var9 = true;
         } else if (this.field_111101_a > var5) {
            this.field_111101_a = var5;
            var9 = true;
         }

         if (this.field_111100_b < var3) {
            this.field_111100_b = var3;
            var9 = true;
         } else if (this.field_111100_b > var7) {
            this.field_111100_b = var7;
            var9 = true;
         }

         return var9;
      }

      public int func_111092_a(World var1) {
         BlockPos var2 = new BlockPos(this.field_111101_a, 256.0D, this.field_111100_b);

         do {
            if (var2.func_177956_o() <= 0) {
               return 257;
            }

            var2 = var2.func_177977_b();
         } while(var1.func_180495_p(var2).func_177230_c().func_149688_o() == Material.field_151579_a);

         return var2.func_177956_o() + 1;
      }

      public boolean func_111098_b(World var1) {
         BlockPos var2 = new BlockPos(this.field_111101_a, 256.0D, this.field_111100_b);

         Material var3;
         do {
            if (var2.func_177956_o() <= 0) {
               return false;
            }

            var2 = var2.func_177977_b();
            var3 = var1.func_180495_p(var2).func_177230_c().func_149688_o();
         } while(var3 == Material.field_151579_a);

         return !var3.func_76224_d() && var3 != Material.field_151581_o;
      }

      public void func_111097_a(Random var1, double var2, double var4, double var6, double var8) {
         this.field_111101_a = MathHelper.func_82716_a(var1, var2, var6);
         this.field_111100_b = MathHelper.func_82716_a(var1, var4, var8);
      }
   }
}

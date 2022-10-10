package net.minecraft.command.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.Vec2Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.WorldServer;

public class SpreadPlayersCommand {
   private static final Dynamic4CommandExceptionType field_198723_a = new Dynamic4CommandExceptionType((var0, var1, var2, var3) -> {
      return new TextComponentTranslation("commands.spreadplayers.failed.teams", new Object[]{var0, var1, var2, var3});
   });
   private static final Dynamic4CommandExceptionType field_198724_b = new Dynamic4CommandExceptionType((var0, var1, var2, var3) -> {
      return new TextComponentTranslation("commands.spreadplayers.failed.entities", new Object[]{var0, var1, var2, var3});
   });

   public static void func_198716_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("spreadplayers").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197056_a("center", Vec2Argument.func_197296_a()).then(Commands.func_197056_a("spreadDistance", FloatArgumentType.floatArg(0.0F)).then(Commands.func_197056_a("maxRange", FloatArgumentType.floatArg(1.0F)).then(Commands.func_197056_a("respectTeams", BoolArgumentType.bool()).then(Commands.func_197056_a("targets", EntityArgument.func_197093_b()).executes((var0x) -> {
         return func_198722_a((CommandSource)var0x.getSource(), Vec2Argument.func_197295_a(var0x, "center"), FloatArgumentType.getFloat(var0x, "spreadDistance"), FloatArgumentType.getFloat(var0x, "maxRange"), BoolArgumentType.getBool(var0x, "respectTeams"), EntityArgument.func_197097_b(var0x, "targets"));
      })))))));
   }

   private static int func_198722_a(CommandSource var0, Vec2f var1, float var2, float var3, boolean var4, Collection<? extends Entity> var5) throws CommandSyntaxException {
      Random var6 = new Random();
      double var7 = (double)(var1.field_189982_i - var3);
      double var9 = (double)(var1.field_189983_j - var3);
      double var11 = (double)(var1.field_189982_i + var3);
      double var13 = (double)(var1.field_189983_j + var3);
      SpreadPlayersCommand.Position[] var15 = func_198720_a(var6, var4 ? func_198715_a(var5) : var5.size(), var7, var9, var11, var13);
      func_198717_a(var1, (double)var2, var0.func_197023_e(), var6, var7, var9, var11, var13, var15, var4);
      double var16 = func_198719_a(var5, var0.func_197023_e(), var15, var4);
      var0.func_197030_a(new TextComponentTranslation("commands.spreadplayers.success." + (var4 ? "teams" : "entities"), new Object[]{var15.length, var1.field_189982_i, var1.field_189983_j, String.format(Locale.ROOT, "%.2f", var16)}), true);
      return var15.length;
   }

   private static int func_198715_a(Collection<? extends Entity> var0) {
      HashSet var1 = Sets.newHashSet();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
         if (var3 instanceof EntityPlayer) {
            var1.add(var3.func_96124_cp());
         } else {
            var1.add((Object)null);
         }
      }

      return var1.size();
   }

   private static void func_198717_a(Vec2f var0, double var1, WorldServer var3, Random var4, double var5, double var7, double var9, double var11, SpreadPlayersCommand.Position[] var13, boolean var14) throws CommandSyntaxException {
      boolean var15 = true;
      double var17 = 3.4028234663852886E38D;

      int var16;
      for(var16 = 0; var16 < 10000 && var15; ++var16) {
         var15 = false;
         var17 = 3.4028234663852886E38D;

         int var21;
         SpreadPlayersCommand.Position var22;
         for(int var19 = 0; var19 < var13.length; ++var19) {
            SpreadPlayersCommand.Position var20 = var13[var19];
            var21 = 0;
            var22 = new SpreadPlayersCommand.Position();

            for(int var23 = 0; var23 < var13.length; ++var23) {
               if (var19 != var23) {
                  SpreadPlayersCommand.Position var24 = var13[var23];
                  double var25 = var20.func_198708_a(var24);
                  var17 = Math.min(var25, var17);
                  if (var25 < var1) {
                     ++var21;
                     var22.field_198713_a = var22.field_198713_a + (var24.field_198713_a - var20.field_198713_a);
                     var22.field_198714_b = var22.field_198714_b + (var24.field_198714_b - var20.field_198714_b);
                  }
               }
            }

            if (var21 > 0) {
               var22.field_198713_a = var22.field_198713_a / (double)var21;
               var22.field_198714_b = var22.field_198714_b / (double)var21;
               double var29 = (double)var22.func_198712_b();
               if (var29 > 0.0D) {
                  var22.func_198707_a();
                  var20.func_198705_b(var22);
               } else {
                  var20.func_198711_a(var4, var5, var7, var9, var11);
               }

               var15 = true;
            }

            if (var20.func_198709_a(var5, var7, var9, var11)) {
               var15 = true;
            }
         }

         if (!var15) {
            SpreadPlayersCommand.Position[] var27 = var13;
            int var28 = var13.length;

            for(var21 = 0; var21 < var28; ++var21) {
               var22 = var27[var21];
               if (!var22.func_198706_b(var3)) {
                  var22.func_198711_a(var4, var5, var7, var9, var11);
                  var15 = true;
               }
            }
         }
      }

      if (var17 == 3.4028234663852886E38D) {
         var17 = 0.0D;
      }

      if (var16 >= 10000) {
         if (var14) {
            throw field_198723_a.create(var13.length, var0.field_189982_i, var0.field_189983_j, String.format(Locale.ROOT, "%.2f", var17));
         } else {
            throw field_198724_b.create(var13.length, var0.field_189982_i, var0.field_189983_j, String.format(Locale.ROOT, "%.2f", var17));
         }
      }
   }

   private static double func_198719_a(Collection<? extends Entity> var0, WorldServer var1, SpreadPlayersCommand.Position[] var2, boolean var3) {
      double var4 = 0.0D;
      int var6 = 0;
      HashMap var7 = Maps.newHashMap();

      double var19;
      for(Iterator var8 = var0.iterator(); var8.hasNext(); var4 += var19) {
         Entity var9 = (Entity)var8.next();
         SpreadPlayersCommand.Position var10;
         if (var3) {
            Team var11 = var9 instanceof EntityPlayer ? var9.func_96124_cp() : null;
            if (!var7.containsKey(var11)) {
               var7.put(var11, var2[var6++]);
            }

            var10 = (SpreadPlayersCommand.Position)var7.get(var11);
         } else {
            var10 = var2[var6++];
         }

         var9.func_70634_a((double)((float)MathHelper.func_76128_c(var10.field_198713_a) + 0.5F), (double)var10.func_198710_a(var1), (double)MathHelper.func_76128_c(var10.field_198714_b) + 0.5D);
         var19 = 1.7976931348623157E308D;
         SpreadPlayersCommand.Position[] var13 = var2;
         int var14 = var2.length;

         for(int var15 = 0; var15 < var14; ++var15) {
            SpreadPlayersCommand.Position var16 = var13[var15];
            if (var10 != var16) {
               double var17 = var10.func_198708_a(var16);
               var19 = Math.min(var17, var19);
            }
         }
      }

      if (var0.size() < 2) {
         return 0.0D;
      } else {
         var4 /= (double)var0.size();
         return var4;
      }
   }

   private static SpreadPlayersCommand.Position[] func_198720_a(Random var0, int var1, double var2, double var4, double var6, double var8) {
      SpreadPlayersCommand.Position[] var10 = new SpreadPlayersCommand.Position[var1];

      for(int var11 = 0; var11 < var10.length; ++var11) {
         SpreadPlayersCommand.Position var12 = new SpreadPlayersCommand.Position();
         var12.func_198711_a(var0, var2, var4, var6, var8);
         var10[var11] = var12;
      }

      return var10;
   }

   static class Position {
      private double field_198713_a;
      private double field_198714_b;

      Position() {
         super();
      }

      double func_198708_a(SpreadPlayersCommand.Position var1) {
         double var2 = this.field_198713_a - var1.field_198713_a;
         double var4 = this.field_198714_b - var1.field_198714_b;
         return Math.sqrt(var2 * var2 + var4 * var4);
      }

      void func_198707_a() {
         double var1 = (double)this.func_198712_b();
         this.field_198713_a /= var1;
         this.field_198714_b /= var1;
      }

      float func_198712_b() {
         return MathHelper.func_76133_a(this.field_198713_a * this.field_198713_a + this.field_198714_b * this.field_198714_b);
      }

      public void func_198705_b(SpreadPlayersCommand.Position var1) {
         this.field_198713_a -= var1.field_198713_a;
         this.field_198714_b -= var1.field_198714_b;
      }

      public boolean func_198709_a(double var1, double var3, double var5, double var7) {
         boolean var9 = false;
         if (this.field_198713_a < var1) {
            this.field_198713_a = var1;
            var9 = true;
         } else if (this.field_198713_a > var5) {
            this.field_198713_a = var5;
            var9 = true;
         }

         if (this.field_198714_b < var3) {
            this.field_198714_b = var3;
            var9 = true;
         } else if (this.field_198714_b > var7) {
            this.field_198714_b = var7;
            var9 = true;
         }

         return var9;
      }

      public int func_198710_a(IBlockReader var1) {
         BlockPos var2 = new BlockPos(this.field_198713_a, 256.0D, this.field_198714_b);

         do {
            if (var2.func_177956_o() <= 0) {
               return 257;
            }

            var2 = var2.func_177977_b();
         } while(var1.func_180495_p(var2).func_196958_f());

         return var2.func_177956_o() + 1;
      }

      public boolean func_198706_b(IBlockReader var1) {
         BlockPos var2 = new BlockPos(this.field_198713_a, 256.0D, this.field_198714_b);

         IBlockState var3;
         do {
            if (var2.func_177956_o() <= 0) {
               return false;
            }

            var2 = var2.func_177977_b();
            var3 = var1.func_180495_p(var2);
         } while(var3.func_196958_f());

         Material var4 = var3.func_185904_a();
         return !var4.func_76224_d() && var4 != Material.field_151581_o;
      }

      public void func_198711_a(Random var1, double var2, double var4, double var6, double var8) {
         this.field_198713_a = MathHelper.func_82716_a(var1, var2, var6);
         this.field_198714_b = MathHelper.func_82716_a(var1, var4, var8);
      }
   }
}

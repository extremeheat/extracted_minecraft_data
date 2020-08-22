package net.minecraft.server.commands;

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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.scores.Team;

public class SpreadPlayersCommand {
   private static final Dynamic4CommandExceptionType ERROR_FAILED_TO_SPREAD_TEAMS = new Dynamic4CommandExceptionType((var0, var1, var2, var3) -> {
      return new TranslatableComponent("commands.spreadplayers.failed.teams", new Object[]{var0, var1, var2, var3});
   });
   private static final Dynamic4CommandExceptionType ERROR_FAILED_TO_SPREAD_ENTITIES = new Dynamic4CommandExceptionType((var0, var1, var2, var3) -> {
      return new TranslatableComponent("commands.spreadplayers.failed.entities", new Object[]{var0, var1, var2, var3});
   });

   public static void register(CommandDispatcher var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spreadplayers").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("center", Vec2Argument.vec2()).then(Commands.argument("spreadDistance", FloatArgumentType.floatArg(0.0F)).then(Commands.argument("maxRange", FloatArgumentType.floatArg(1.0F)).then(Commands.argument("respectTeams", BoolArgumentType.bool()).then(Commands.argument("targets", EntityArgument.entities()).executes((var0x) -> {
         return spreadPlayers((CommandSourceStack)var0x.getSource(), Vec2Argument.getVec2(var0x, "center"), FloatArgumentType.getFloat(var0x, "spreadDistance"), FloatArgumentType.getFloat(var0x, "maxRange"), BoolArgumentType.getBool(var0x, "respectTeams"), EntityArgument.getEntities(var0x, "targets"));
      })))))));
   }

   private static int spreadPlayers(CommandSourceStack var0, Vec2 var1, float var2, float var3, boolean var4, Collection var5) throws CommandSyntaxException {
      Random var6 = new Random();
      double var7 = (double)(var1.x - var3);
      double var9 = (double)(var1.y - var3);
      double var11 = (double)(var1.x + var3);
      double var13 = (double)(var1.y + var3);
      SpreadPlayersCommand.Position[] var15 = createInitialPositions(var6, var4 ? getNumberOfTeams(var5) : var5.size(), var7, var9, var11, var13);
      spreadPositions(var1, (double)var2, var0.getLevel(), var6, var7, var9, var11, var13, var15, var4);
      double var16 = setPlayerPositions(var5, var0.getLevel(), var15, var4);
      var0.sendSuccess(new TranslatableComponent("commands.spreadplayers.success." + (var4 ? "teams" : "entities"), new Object[]{var15.length, var1.x, var1.y, String.format(Locale.ROOT, "%.2f", var16)}), true);
      return var15.length;
   }

   private static int getNumberOfTeams(Collection var0) {
      HashSet var1 = Sets.newHashSet();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
         if (var3 instanceof Player) {
            var1.add(var3.getTeam());
         } else {
            var1.add((Object)null);
         }
      }

      return var1.size();
   }

   private static void spreadPositions(Vec2 var0, double var1, ServerLevel var3, Random var4, double var5, double var7, double var9, double var11, SpreadPlayersCommand.Position[] var13, boolean var14) throws CommandSyntaxException {
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
                  double var25 = var20.dist(var24);
                  var17 = Math.min(var25, var17);
                  if (var25 < var1) {
                     ++var21;
                     var22.x = var22.x + (var24.x - var20.x);
                     var22.z = var22.z + (var24.z - var20.z);
                  }
               }
            }

            if (var21 > 0) {
               var22.x = var22.x / (double)var21;
               var22.z = var22.z / (double)var21;
               double var29 = (double)var22.getLength();
               if (var29 > 0.0D) {
                  var22.normalize();
                  var20.moveAway(var22);
               } else {
                  var20.randomize(var4, var5, var7, var9, var11);
               }

               var15 = true;
            }

            if (var20.clamp(var5, var7, var9, var11)) {
               var15 = true;
            }
         }

         if (!var15) {
            SpreadPlayersCommand.Position[] var27 = var13;
            int var28 = var13.length;

            for(var21 = 0; var21 < var28; ++var21) {
               var22 = var27[var21];
               if (!var22.isSafe(var3)) {
                  var22.randomize(var4, var5, var7, var9, var11);
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
            throw ERROR_FAILED_TO_SPREAD_TEAMS.create(var13.length, var0.x, var0.y, String.format(Locale.ROOT, "%.2f", var17));
         } else {
            throw ERROR_FAILED_TO_SPREAD_ENTITIES.create(var13.length, var0.x, var0.y, String.format(Locale.ROOT, "%.2f", var17));
         }
      }
   }

   private static double setPlayerPositions(Collection var0, ServerLevel var1, SpreadPlayersCommand.Position[] var2, boolean var3) {
      double var4 = 0.0D;
      int var6 = 0;
      HashMap var7 = Maps.newHashMap();

      double var19;
      for(Iterator var8 = var0.iterator(); var8.hasNext(); var4 += var19) {
         Entity var9 = (Entity)var8.next();
         SpreadPlayersCommand.Position var10;
         if (var3) {
            Team var11 = var9 instanceof Player ? var9.getTeam() : null;
            if (!var7.containsKey(var11)) {
               var7.put(var11, var2[var6++]);
            }

            var10 = (SpreadPlayersCommand.Position)var7.get(var11);
         } else {
            var10 = var2[var6++];
         }

         var9.teleportToWithTicket((double)((float)Mth.floor(var10.x) + 0.5F), (double)var10.getSpawnY(var1), (double)Mth.floor(var10.z) + 0.5D);
         var19 = Double.MAX_VALUE;
         SpreadPlayersCommand.Position[] var13 = var2;
         int var14 = var2.length;

         for(int var15 = 0; var15 < var14; ++var15) {
            SpreadPlayersCommand.Position var16 = var13[var15];
            if (var10 != var16) {
               double var17 = var10.dist(var16);
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

   private static SpreadPlayersCommand.Position[] createInitialPositions(Random var0, int var1, double var2, double var4, double var6, double var8) {
      SpreadPlayersCommand.Position[] var10 = new SpreadPlayersCommand.Position[var1];

      for(int var11 = 0; var11 < var10.length; ++var11) {
         SpreadPlayersCommand.Position var12 = new SpreadPlayersCommand.Position();
         var12.randomize(var0, var2, var4, var6, var8);
         var10[var11] = var12;
      }

      return var10;
   }

   static class Position {
      private double x;
      private double z;

      double dist(SpreadPlayersCommand.Position var1) {
         double var2 = this.x - var1.x;
         double var4 = this.z - var1.z;
         return Math.sqrt(var2 * var2 + var4 * var4);
      }

      void normalize() {
         double var1 = (double)this.getLength();
         this.x /= var1;
         this.z /= var1;
      }

      float getLength() {
         return Mth.sqrt(this.x * this.x + this.z * this.z);
      }

      public void moveAway(SpreadPlayersCommand.Position var1) {
         this.x -= var1.x;
         this.z -= var1.z;
      }

      public boolean clamp(double var1, double var3, double var5, double var7) {
         boolean var9 = false;
         if (this.x < var1) {
            this.x = var1;
            var9 = true;
         } else if (this.x > var5) {
            this.x = var5;
            var9 = true;
         }

         if (this.z < var3) {
            this.z = var3;
            var9 = true;
         } else if (this.z > var7) {
            this.z = var7;
            var9 = true;
         }

         return var9;
      }

      public int getSpawnY(BlockGetter var1) {
         BlockPos var2 = new BlockPos(this.x, 256.0D, this.z);

         do {
            if (var2.getY() <= 0) {
               return 257;
            }

            var2 = var2.below();
         } while(var1.getBlockState(var2).isAir());

         return var2.getY() + 1;
      }

      public boolean isSafe(BlockGetter var1) {
         BlockPos var2 = new BlockPos(this.x, 256.0D, this.z);

         BlockState var3;
         do {
            if (var2.getY() <= 0) {
               return false;
            }

            var2 = var2.below();
            var3 = var1.getBlockState(var2);
         } while(var3.isAir());

         Material var4 = var3.getMaterial();
         return !var4.isLiquid() && var4 != Material.FIRE;
      }

      public void randomize(Random var1, double var2, double var4, double var6, double var8) {
         this.x = Mth.nextDouble(var1, var2, var6);
         this.z = Mth.nextDouble(var1, var4, var8);
      }
   }
}

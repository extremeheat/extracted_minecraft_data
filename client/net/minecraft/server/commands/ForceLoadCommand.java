package net.minecraft.server.commands;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.dimension.DimensionType;

public class ForceLoadCommand {
   private static final Dynamic2CommandExceptionType ERROR_TOO_MANY_CHUNKS = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.forceload.toobig", new Object[]{var0, var1});
   });
   private static final Dynamic2CommandExceptionType ERROR_NOT_TICKING = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.forceload.query.failure", new Object[]{var0, var1});
   });
   private static final SimpleCommandExceptionType ERROR_ALL_ADDED = new SimpleCommandExceptionType(new TranslatableComponent("commands.forceload.added.failure", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_NONE_REMOVED = new SimpleCommandExceptionType(new TranslatableComponent("commands.forceload.removed.failure", new Object[0]));

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("forceload").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("add").then(((RequiredArgumentBuilder)Commands.argument("from", ColumnPosArgument.columnPos()).executes((var0x) -> {
         return changeForceLoad((CommandSourceStack)var0x.getSource(), ColumnPosArgument.getColumnPos(var0x, "from"), ColumnPosArgument.getColumnPos(var0x, "from"), true);
      })).then(Commands.argument("to", ColumnPosArgument.columnPos()).executes((var0x) -> {
         return changeForceLoad((CommandSourceStack)var0x.getSource(), ColumnPosArgument.getColumnPos(var0x, "from"), ColumnPosArgument.getColumnPos(var0x, "to"), true);
      }))))).then(((LiteralArgumentBuilder)Commands.literal("remove").then(((RequiredArgumentBuilder)Commands.argument("from", ColumnPosArgument.columnPos()).executes((var0x) -> {
         return changeForceLoad((CommandSourceStack)var0x.getSource(), ColumnPosArgument.getColumnPos(var0x, "from"), ColumnPosArgument.getColumnPos(var0x, "from"), false);
      })).then(Commands.argument("to", ColumnPosArgument.columnPos()).executes((var0x) -> {
         return changeForceLoad((CommandSourceStack)var0x.getSource(), ColumnPosArgument.getColumnPos(var0x, "from"), ColumnPosArgument.getColumnPos(var0x, "to"), false);
      })))).then(Commands.literal("all").executes((var0x) -> {
         return removeAll((CommandSourceStack)var0x.getSource());
      })))).then(((LiteralArgumentBuilder)Commands.literal("query").executes((var0x) -> {
         return listForceLoad((CommandSourceStack)var0x.getSource());
      })).then(Commands.argument("pos", ColumnPosArgument.columnPos()).executes((var0x) -> {
         return queryForceLoad((CommandSourceStack)var0x.getSource(), ColumnPosArgument.getColumnPos(var0x, "pos"));
      }))));
   }

   private static int queryForceLoad(CommandSourceStack var0, ColumnPos var1) throws CommandSyntaxException {
      ChunkPos var2 = new ChunkPos(var1.x >> 4, var1.z >> 4);
      DimensionType var3 = var0.getLevel().getDimension().getType();
      boolean var4 = var0.getServer().getLevel(var3).getForcedChunks().contains(var2.toLong());
      if (var4) {
         var0.sendSuccess(new TranslatableComponent("commands.forceload.query.success", new Object[]{var2, var3}), false);
         return 1;
      } else {
         throw ERROR_NOT_TICKING.create(var2, var3);
      }
   }

   private static int listForceLoad(CommandSourceStack var0) {
      DimensionType var1 = var0.getLevel().getDimension().getType();
      LongSet var2 = var0.getServer().getLevel(var1).getForcedChunks();
      int var3 = var2.size();
      if (var3 > 0) {
         String var4 = Joiner.on(", ").join(var2.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
         if (var3 == 1) {
            var0.sendSuccess(new TranslatableComponent("commands.forceload.list.single", new Object[]{var1, var4}), false);
         } else {
            var0.sendSuccess(new TranslatableComponent("commands.forceload.list.multiple", new Object[]{var3, var1, var4}), false);
         }
      } else {
         var0.sendFailure(new TranslatableComponent("commands.forceload.added.none", new Object[]{var1}));
      }

      return var3;
   }

   private static int removeAll(CommandSourceStack var0) {
      DimensionType var1 = var0.getLevel().getDimension().getType();
      ServerLevel var2 = var0.getServer().getLevel(var1);
      LongSet var3 = var2.getForcedChunks();
      var3.forEach((var1x) -> {
         var2.setChunkForced(ChunkPos.getX(var1x), ChunkPos.getZ(var1x), false);
      });
      var0.sendSuccess(new TranslatableComponent("commands.forceload.removed.all", new Object[]{var1}), true);
      return 0;
   }

   private static int changeForceLoad(CommandSourceStack var0, ColumnPos var1, ColumnPos var2, boolean var3) throws CommandSyntaxException {
      int var4 = Math.min(var1.x, var2.x);
      int var5 = Math.min(var1.z, var2.z);
      int var6 = Math.max(var1.x, var2.x);
      int var7 = Math.max(var1.z, var2.z);
      if (var4 >= -30000000 && var5 >= -30000000 && var6 < 30000000 && var7 < 30000000) {
         int var8 = var4 >> 4;
         int var9 = var5 >> 4;
         int var10 = var6 >> 4;
         int var11 = var7 >> 4;
         long var12 = ((long)(var10 - var8) + 1L) * ((long)(var11 - var9) + 1L);
         if (var12 > 256L) {
            throw ERROR_TOO_MANY_CHUNKS.create(256, var12);
         } else {
            DimensionType var14 = var0.getLevel().getDimension().getType();
            ServerLevel var15 = var0.getServer().getLevel(var14);
            ChunkPos var16 = null;
            int var17 = 0;

            for(int var18 = var8; var18 <= var10; ++var18) {
               for(int var19 = var9; var19 <= var11; ++var19) {
                  boolean var20 = var15.setChunkForced(var18, var19, var3);
                  if (var20) {
                     ++var17;
                     if (var16 == null) {
                        var16 = new ChunkPos(var18, var19);
                     }
                  }
               }
            }

            if (var17 == 0) {
               throw (var3 ? ERROR_ALL_ADDED : ERROR_NONE_REMOVED).create();
            } else {
               if (var17 == 1) {
                  var0.sendSuccess(new TranslatableComponent("commands.forceload." + (var3 ? "added" : "removed") + ".single", new Object[]{var16, var14}), true);
               } else {
                  ChunkPos var21 = new ChunkPos(var8, var9);
                  ChunkPos var22 = new ChunkPos(var10, var11);
                  var0.sendSuccess(new TranslatableComponent("commands.forceload." + (var3 ? "added" : "removed") + ".multiple", new Object[]{var17, var14, var21, var22}), true);
               }

               return var17;
            }
         }
      } else {
         throw BlockPosArgument.ERROR_OUT_OF_WORLD.create();
      }
   }
}

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
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public class ForceLoadCommand {
   private static final int MAX_CHUNK_LIMIT = 256;
   private static final Dynamic2CommandExceptionType ERROR_TOO_MANY_CHUNKS = new Dynamic2CommandExceptionType((var0, var1) -> {
      return Component.translatableEscape("commands.forceload.toobig", var0, var1);
   });
   private static final Dynamic2CommandExceptionType ERROR_NOT_TICKING = new Dynamic2CommandExceptionType((var0, var1) -> {
      return Component.translatableEscape("commands.forceload.query.failure", var0, var1);
   });
   private static final SimpleCommandExceptionType ERROR_ALL_ADDED = new SimpleCommandExceptionType(Component.translatable("commands.forceload.added.failure"));
   private static final SimpleCommandExceptionType ERROR_NONE_REMOVED = new SimpleCommandExceptionType(Component.translatable("commands.forceload.removed.failure"));

   public ForceLoadCommand() {
      super();
   }

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
      ChunkPos var2 = var1.toChunkPos();
      ServerLevel var3 = var0.getLevel();
      ResourceKey var4 = var3.dimension();
      boolean var5 = var3.getForcedChunks().contains(var2.toLong());
      if (var5) {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.forceload.query.success", Component.translationArg(var2), Component.translationArg(var4.location()));
         }, false);
         return 1;
      } else {
         throw ERROR_NOT_TICKING.create(var2, var4.location());
      }
   }

   private static int listForceLoad(CommandSourceStack var0) {
      ServerLevel var1 = var0.getLevel();
      ResourceKey var2 = var1.dimension();
      LongSet var3 = var1.getForcedChunks();
      int var4 = var3.size();
      if (var4 > 0) {
         String var5 = Joiner.on(", ").join(var3.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
         if (var4 == 1) {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.forceload.list.single", Component.translationArg(var2.location()), var5);
            }, false);
         } else {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.forceload.list.multiple", var4, Component.translationArg(var2.location()), var5);
            }, false);
         }
      } else {
         var0.sendFailure(Component.translatable("commands.forceload.added.none", Component.translationArg(var2.location())));
      }

      return var4;
   }

   private static int removeAll(CommandSourceStack var0) {
      ServerLevel var1 = var0.getLevel();
      ResourceKey var2 = var1.dimension();
      LongSet var3 = var1.getForcedChunks();
      var3.forEach((var1x) -> {
         var1.setChunkForced(ChunkPos.getX(var1x), ChunkPos.getZ(var1x), false);
      });
      var0.sendSuccess(() -> {
         return Component.translatable("commands.forceload.removed.all", Component.translationArg(var2.location()));
      }, true);
      return 0;
   }

   private static int changeForceLoad(CommandSourceStack var0, ColumnPos var1, ColumnPos var2, boolean var3) throws CommandSyntaxException {
      int var4 = Math.min(var1.x(), var2.x());
      int var5 = Math.min(var1.z(), var2.z());
      int var6 = Math.max(var1.x(), var2.x());
      int var7 = Math.max(var1.z(), var2.z());
      if (var4 >= -30000000 && var5 >= -30000000 && var6 < 30000000 && var7 < 30000000) {
         int var8 = SectionPos.blockToSectionCoord(var4);
         int var9 = SectionPos.blockToSectionCoord(var5);
         int var10 = SectionPos.blockToSectionCoord(var6);
         int var11 = SectionPos.blockToSectionCoord(var7);
         long var12 = ((long)(var10 - var8) + 1L) * ((long)(var11 - var9) + 1L);
         if (var12 > 256L) {
            throw ERROR_TOO_MANY_CHUNKS.create(256, var12);
         } else {
            ServerLevel var14 = var0.getLevel();
            ResourceKey var15 = var14.dimension();
            ChunkPos var16 = null;
            int var17 = 0;

            for(int var18 = var8; var18 <= var10; ++var18) {
               for(int var19 = var9; var19 <= var11; ++var19) {
                  boolean var20 = var14.setChunkForced(var18, var19, var3);
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
                  var0.sendSuccess(() -> {
                     return Component.translatable("commands.forceload." + (var3 ? "added" : "removed") + ".single", Component.translationArg(var16), Component.translationArg(var15.location()));
                  }, true);
               } else {
                  ChunkPos var22 = new ChunkPos(var8, var9);
                  ChunkPos var21 = new ChunkPos(var10, var11);
                  var0.sendSuccess(() -> {
                     return Component.translatable("commands.forceload." + (var3 ? "added" : "removed") + ".multiple", var17, Component.translationArg(var15.location()), Component.translationArg(var22), Component.translationArg(var21));
                  }, true);
               }

               return var17;
            }
         }
      } else {
         throw BlockPosArgument.ERROR_OUT_OF_WORLD.create();
      }
   }
}

package net.minecraft.command.impl;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.ColumnPosArgument;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;

public class ForceLoadCommand {
   private static final Dynamic2CommandExceptionType field_212726_a = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("commands.forceload.toobig", new Object[]{var0, var1});
   });
   private static final Dynamic2CommandExceptionType field_212727_b = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("commands.forceload.query.failure", new Object[]{var0, var1});
   });
   private static final SimpleCommandExceptionType field_212728_c = new SimpleCommandExceptionType(new TextComponentTranslation("commands.forceload.added.failure", new Object[0]));
   private static final SimpleCommandExceptionType field_212729_d = new SimpleCommandExceptionType(new TextComponentTranslation("commands.forceload.removed.failure", new Object[0]));

   public static void func_212712_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("forceload").requires((var0x) -> {
         return var0x.func_197034_c(4);
      })).then(Commands.func_197057_a("add").then(((RequiredArgumentBuilder)Commands.func_197056_a("from", ColumnPosArgument.func_212603_a()).executes((var0x) -> {
         return func_212719_a((CommandSource)var0x.getSource(), ColumnPosArgument.func_212602_a(var0x, "from"), ColumnPosArgument.func_212602_a(var0x, "from"), true);
      })).then(Commands.func_197056_a("to", ColumnPosArgument.func_212603_a()).executes((var0x) -> {
         return func_212719_a((CommandSource)var0x.getSource(), ColumnPosArgument.func_212602_a(var0x, "from"), ColumnPosArgument.func_212602_a(var0x, "to"), true);
      }))))).then(((LiteralArgumentBuilder)Commands.func_197057_a("remove").then(((RequiredArgumentBuilder)Commands.func_197056_a("from", ColumnPosArgument.func_212603_a()).executes((var0x) -> {
         return func_212719_a((CommandSource)var0x.getSource(), ColumnPosArgument.func_212602_a(var0x, "from"), ColumnPosArgument.func_212602_a(var0x, "from"), false);
      })).then(Commands.func_197056_a("to", ColumnPosArgument.func_212603_a()).executes((var0x) -> {
         return func_212719_a((CommandSource)var0x.getSource(), ColumnPosArgument.func_212602_a(var0x, "from"), ColumnPosArgument.func_212602_a(var0x, "to"), false);
      })))).then(Commands.func_197057_a("all").executes((var0x) -> {
         return func_212722_b((CommandSource)var0x.getSource());
      })))).then(((LiteralArgumentBuilder)Commands.func_197057_a("query").executes((var0x) -> {
         return func_212721_a((CommandSource)var0x.getSource());
      })).then(Commands.func_197056_a("pos", ColumnPosArgument.func_212603_a()).executes((var0x) -> {
         return func_212713_a((CommandSource)var0x.getSource(), ColumnPosArgument.func_212602_a(var0x, "pos"));
      }))));
   }

   private static int func_212713_a(CommandSource var0, ColumnPosArgument.ColumnPos var1) throws CommandSyntaxException {
      ChunkPos var2 = new ChunkPos(var1.field_212600_a >> 4, var1.field_212601_b >> 4);
      DimensionType var3 = var0.func_197023_e().func_201675_m().func_186058_p();
      boolean var4 = var0.func_197028_i().func_71218_a(var3).func_212416_f(var2.field_77276_a, var2.field_77275_b);
      if (var4) {
         var0.func_197030_a(new TextComponentTranslation("commands.forceload.query.success", new Object[]{var2, var3}), false);
         return 1;
      } else {
         throw field_212727_b.create(var2, var3);
      }
   }

   private static int func_212721_a(CommandSource var0) {
      DimensionType var1 = var0.func_197023_e().func_201675_m().func_186058_p();
      LongSet var2 = var0.func_197028_i().func_71218_a(var1).func_212412_ag();
      int var3 = var2.size();
      if (var3 > 0) {
         String var4 = Joiner.on(", ").join(var2.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
         if (var3 == 1) {
            var0.func_197030_a(new TextComponentTranslation("commands.forceload.list.single", new Object[]{var1, var4}), false);
         } else {
            var0.func_197030_a(new TextComponentTranslation("commands.forceload.list.multiple", new Object[]{var3, var1, var4}), false);
         }
      } else {
         var0.func_197021_a(new TextComponentTranslation("commands.forceload.added.none", new Object[]{var1}));
      }

      return var3;
   }

   private static int func_212722_b(CommandSource var0) {
      DimensionType var1 = var0.func_197023_e().func_201675_m().func_186058_p();
      WorldServer var2 = var0.func_197028_i().func_71218_a(var1);
      LongSet var3 = var2.func_212412_ag();
      var3.forEach((var1x) -> {
         var2.func_212414_b(ChunkPos.func_212578_a(var1x), ChunkPos.func_212579_b(var1x), false);
      });
      var0.func_197030_a(new TextComponentTranslation("commands.forceload.removed.all", new Object[]{var1}), true);
      return 0;
   }

   private static int func_212719_a(CommandSource var0, ColumnPosArgument.ColumnPos var1, ColumnPosArgument.ColumnPos var2, boolean var3) throws CommandSyntaxException {
      int var4 = Math.min(var1.field_212600_a, var2.field_212600_a);
      int var5 = Math.min(var1.field_212601_b, var2.field_212601_b);
      int var6 = Math.max(var1.field_212600_a, var2.field_212600_a);
      int var7 = Math.max(var1.field_212601_b, var2.field_212601_b);
      if (var4 >= -30000000 && var5 >= -30000000 && var6 < 30000000 && var7 < 30000000) {
         int var8 = var4 >> 4;
         int var9 = var5 >> 4;
         int var10 = var6 >> 4;
         int var11 = var7 >> 4;
         long var12 = ((long)(var10 - var8) + 1L) * ((long)(var11 - var9) + 1L);
         if (var12 > 256L) {
            throw field_212726_a.create(256, var12);
         } else {
            DimensionType var14 = var0.func_197023_e().func_201675_m().func_186058_p();
            WorldServer var15 = var0.func_197028_i().func_71218_a(var14);
            ChunkPos var16 = null;
            int var17 = 0;

            for(int var18 = var8; var18 <= var10; ++var18) {
               for(int var19 = var9; var19 <= var11; ++var19) {
                  boolean var20 = var15.func_212414_b(var18, var19, var3);
                  if (var20) {
                     ++var17;
                     if (var16 == null) {
                        var16 = new ChunkPos(var18, var19);
                     }
                  }
               }
            }

            if (var17 == 0) {
               throw (var3 ? field_212728_c : field_212729_d).create();
            } else {
               if (var17 == 1) {
                  var0.func_197030_a(new TextComponentTranslation("commands.forceload." + (var3 ? "added" : "removed") + ".single", new Object[]{var16, var14}), true);
               } else {
                  ChunkPos var21 = new ChunkPos(var8, var9);
                  ChunkPos var22 = new ChunkPos(var10, var11);
                  var0.func_197030_a(new TextComponentTranslation("commands.forceload." + (var3 ? "added" : "removed") + ".multiple", new Object[]{var17, var14, var21, var22}), true);
               }

               return var17;
            }
         }
      } else {
         throw BlockPosArgument.field_197279_c.create();
      }
   }
}

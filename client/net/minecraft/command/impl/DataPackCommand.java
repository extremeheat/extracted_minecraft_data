package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldInfo;

public class DataPackCommand {
   private static final DynamicCommandExceptionType field_198316_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.datapack.unknown", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType field_198317_b = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.datapack.enable.failed", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType field_198318_c = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.datapack.disable.failed", new Object[]{var0});
   });
   private static final SuggestionProvider<CommandSource> field_198319_d = (var0, var1) -> {
      return ISuggestionProvider.func_197013_a(((CommandSource)var0.getSource()).func_197028_i().func_195561_aH().func_198980_d().stream().map(ResourcePackInfo::func_195790_f).map(StringArgumentType::escapeIfRequired), var1);
   };
   private static final SuggestionProvider<CommandSource> field_198320_e = (var0, var1) -> {
      return ISuggestionProvider.func_197013_a(((CommandSource)var0.getSource()).func_197028_i().func_195561_aH().func_198979_c().stream().map(ResourcePackInfo::func_195790_f).map(StringArgumentType::escapeIfRequired), var1);
   };

   public static void func_198299_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("datapack").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197057_a("enable").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("name", StringArgumentType.string()).suggests(field_198320_e).executes((var0x) -> {
         return func_198297_a((CommandSource)var0x.getSource(), func_198303_a(var0x, "name", true), (var0, var1) -> {
            var1.func_195792_i().func_198993_a(var0, var1, (var0x) -> {
               return var0x;
            }, false);
         });
      })).then(Commands.func_197057_a("after").then(Commands.func_197056_a("existing", StringArgumentType.string()).suggests(field_198319_d).executes((var0x) -> {
         return func_198297_a((CommandSource)var0x.getSource(), func_198303_a(var0x, "name", true), (var1, var2) -> {
            var1.add(var1.indexOf(func_198303_a(var0x, "existing", false)) + 1, var2);
         });
      })))).then(Commands.func_197057_a("before").then(Commands.func_197056_a("existing", StringArgumentType.string()).suggests(field_198319_d).executes((var0x) -> {
         return func_198297_a((CommandSource)var0x.getSource(), func_198303_a(var0x, "name", true), (var1, var2) -> {
            var1.add(var1.indexOf(func_198303_a(var0x, "existing", false)), var2);
         });
      })))).then(Commands.func_197057_a("last").executes((var0x) -> {
         return func_198297_a((CommandSource)var0x.getSource(), func_198303_a(var0x, "name", true), List::add);
      }))).then(Commands.func_197057_a("first").executes((var0x) -> {
         return func_198297_a((CommandSource)var0x.getSource(), func_198303_a(var0x, "name", true), (var0, var1) -> {
            var0.add(0, var1);
         });
      }))))).then(Commands.func_197057_a("disable").then(Commands.func_197056_a("name", StringArgumentType.string()).suggests(field_198319_d).executes((var0x) -> {
         return func_198312_a((CommandSource)var0x.getSource(), func_198303_a(var0x, "name", false));
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("list").executes((var0x) -> {
         return func_198313_a((CommandSource)var0x.getSource());
      })).then(Commands.func_197057_a("available").executes((var0x) -> {
         return func_198314_b((CommandSource)var0x.getSource());
      }))).then(Commands.func_197057_a("enabled").executes((var0x) -> {
         return func_198315_c((CommandSource)var0x.getSource());
      }))));
   }

   private static int func_198297_a(CommandSource var0, ResourcePackInfo var1, DataPackCommand.IHandler var2) throws CommandSyntaxException {
      ResourcePackList var3 = var0.func_197028_i().func_195561_aH();
      ArrayList var4 = Lists.newArrayList(var3.func_198980_d());
      var2.apply(var4, var1);
      var3.func_198985_a(var4);
      WorldInfo var5 = var0.func_197028_i().func_71218_a(DimensionType.OVERWORLD).func_72912_H();
      var5.func_197720_O().clear();
      var3.func_198980_d().forEach((var1x) -> {
         var5.func_197720_O().add(var1x.func_195790_f());
      });
      var5.func_197719_N().remove(var1.func_195790_f());
      var0.func_197030_a(new TextComponentTranslation("commands.datapack.enable.success", new Object[]{var1.func_195794_a(true)}), true);
      var0.func_197028_i().func_193031_aM();
      return var3.func_198980_d().size();
   }

   private static int func_198312_a(CommandSource var0, ResourcePackInfo var1) {
      ResourcePackList var2 = var0.func_197028_i().func_195561_aH();
      ArrayList var3 = Lists.newArrayList(var2.func_198980_d());
      var3.remove(var1);
      var2.func_198985_a(var3);
      WorldInfo var4 = var0.func_197028_i().func_71218_a(DimensionType.OVERWORLD).func_72912_H();
      var4.func_197720_O().clear();
      var2.func_198980_d().forEach((var1x) -> {
         var4.func_197720_O().add(var1x.func_195790_f());
      });
      var4.func_197719_N().add(var1.func_195790_f());
      var0.func_197030_a(new TextComponentTranslation("commands.datapack.disable.success", new Object[]{var1.func_195794_a(true)}), true);
      var0.func_197028_i().func_193031_aM();
      return var2.func_198980_d().size();
   }

   private static int func_198313_a(CommandSource var0) {
      return func_198315_c(var0) + func_198314_b(var0);
   }

   private static int func_198314_b(CommandSource var0) {
      ResourcePackList var1 = var0.func_197028_i().func_195561_aH();
      if (var1.func_198979_c().isEmpty()) {
         var0.func_197030_a(new TextComponentTranslation("commands.datapack.list.available.none", new Object[0]), false);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.datapack.list.available.success", new Object[]{var1.func_198979_c().size(), TextComponentUtils.func_197677_b(var1.func_198979_c(), (var0x) -> {
            return var0x.func_195794_a(false);
         })}), false);
      }

      return var1.func_198979_c().size();
   }

   private static int func_198315_c(CommandSource var0) {
      ResourcePackList var1 = var0.func_197028_i().func_195561_aH();
      if (var1.func_198980_d().isEmpty()) {
         var0.func_197030_a(new TextComponentTranslation("commands.datapack.list.enabled.none", new Object[0]), false);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.datapack.list.enabled.success", new Object[]{var1.func_198980_d().size(), TextComponentUtils.func_197677_b(var1.func_198980_d(), (var0x) -> {
            return var0x.func_195794_a(true);
         })}), false);
      }

      return var1.func_198980_d().size();
   }

   private static ResourcePackInfo func_198303_a(CommandContext<CommandSource> var0, String var1, boolean var2) throws CommandSyntaxException {
      String var3 = StringArgumentType.getString(var0, var1);
      ResourcePackList var4 = ((CommandSource)var0.getSource()).func_197028_i().func_195561_aH();
      ResourcePackInfo var5 = var4.func_198981_a(var3);
      if (var5 == null) {
         throw field_198316_a.create(var3);
      } else {
         boolean var6 = var4.func_198980_d().contains(var5);
         if (var2 && var6) {
            throw field_198317_b.create(var3);
         } else if (!var2 && !var6) {
            throw field_198318_c.create(var3);
         } else {
            return var5;
         }
      }
   }

   interface IHandler {
      void apply(List<ResourcePackInfo> var1, ResourcePackInfo var2) throws CommandSyntaxException;
   }
}

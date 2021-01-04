package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

public class LocateCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.locate.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("locate").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("Pillager_Outpost").executes((var0x) -> {
         return locate((CommandSourceStack)var0x.getSource(), "Pillager_Outpost");
      }))).then(Commands.literal("Mineshaft").executes((var0x) -> {
         return locate((CommandSourceStack)var0x.getSource(), "Mineshaft");
      }))).then(Commands.literal("Mansion").executes((var0x) -> {
         return locate((CommandSourceStack)var0x.getSource(), "Mansion");
      }))).then(Commands.literal("Igloo").executes((var0x) -> {
         return locate((CommandSourceStack)var0x.getSource(), "Igloo");
      }))).then(Commands.literal("Desert_Pyramid").executes((var0x) -> {
         return locate((CommandSourceStack)var0x.getSource(), "Desert_Pyramid");
      }))).then(Commands.literal("Jungle_Pyramid").executes((var0x) -> {
         return locate((CommandSourceStack)var0x.getSource(), "Jungle_Pyramid");
      }))).then(Commands.literal("Swamp_Hut").executes((var0x) -> {
         return locate((CommandSourceStack)var0x.getSource(), "Swamp_Hut");
      }))).then(Commands.literal("Stronghold").executes((var0x) -> {
         return locate((CommandSourceStack)var0x.getSource(), "Stronghold");
      }))).then(Commands.literal("Monument").executes((var0x) -> {
         return locate((CommandSourceStack)var0x.getSource(), "Monument");
      }))).then(Commands.literal("Fortress").executes((var0x) -> {
         return locate((CommandSourceStack)var0x.getSource(), "Fortress");
      }))).then(Commands.literal("EndCity").executes((var0x) -> {
         return locate((CommandSourceStack)var0x.getSource(), "EndCity");
      }))).then(Commands.literal("Ocean_Ruin").executes((var0x) -> {
         return locate((CommandSourceStack)var0x.getSource(), "Ocean_Ruin");
      }))).then(Commands.literal("Buried_Treasure").executes((var0x) -> {
         return locate((CommandSourceStack)var0x.getSource(), "Buried_Treasure");
      }))).then(Commands.literal("Shipwreck").executes((var0x) -> {
         return locate((CommandSourceStack)var0x.getSource(), "Shipwreck");
      }))).then(Commands.literal("Village").executes((var0x) -> {
         return locate((CommandSourceStack)var0x.getSource(), "Village");
      })));
   }

   private static int locate(CommandSourceStack var0, String var1) throws CommandSyntaxException {
      BlockPos var2 = new BlockPos(var0.getPosition());
      BlockPos var3 = var0.getLevel().findNearestMapFeature(var1, var2, 100, false);
      if (var3 == null) {
         throw ERROR_FAILED.create();
      } else {
         int var4 = Mth.floor(dist(var2.getX(), var2.getZ(), var3.getX(), var3.getZ()));
         Component var5 = ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("chat.coordinates", new Object[]{var3.getX(), "~", var3.getZ()})).withStyle((var1x) -> {
            var1x.setColor(ChatFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + var3.getX() + " ~ " + var3.getZ())).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.coordinates.tooltip", new Object[0])));
         });
         var0.sendSuccess(new TranslatableComponent("commands.locate.success", new Object[]{var1, var5, var4}), false);
         return var4;
      }
   }

   private static float dist(int var0, int var1, int var2, int var3) {
      int var4 = var2 - var0;
      int var5 = var3 - var1;
      return Mth.sqrt((float)(var4 * var4 + var5 * var5));
   }
}

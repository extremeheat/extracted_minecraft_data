package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class LocateCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.locate.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralArgumentBuilder var1 = (LiteralArgumentBuilder)Commands.literal("locate").requires((var0x) -> {
         return var0x.hasPermission(2);
      });

      Entry var3;
      for(Iterator var2 = StructureFeature.STRUCTURES_REGISTRY.entrySet().iterator(); var2.hasNext(); var1 = (LiteralArgumentBuilder)var1.then(Commands.literal((String)var3.getKey()).executes((var1x) -> {
         return locate((CommandSourceStack)var1x.getSource(), (StructureFeature)var3.getValue());
      }))) {
         var3 = (Entry)var2.next();
      }

      var0.register(var1);
   }

   private static int locate(CommandSourceStack var0, StructureFeature<?> var1) throws CommandSyntaxException {
      BlockPos var2 = new BlockPos(var0.getPosition());
      BlockPos var3 = var0.getLevel().findNearestMapFeature(var1, var2, 100, false);
      if (var3 == null) {
         throw ERROR_FAILED.create();
      } else {
         return showLocateResult(var0, var1.getFeatureName(), var2, var3, "commands.locate.success");
      }
   }

   public static int showLocateResult(CommandSourceStack var0, String var1, BlockPos var2, BlockPos var3, String var4) {
      int var5 = Mth.floor(dist(var2.getX(), var2.getZ(), var3.getX(), var3.getZ()));
      MutableComponent var6 = ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("chat.coordinates", new Object[]{var3.getX(), "~", var3.getZ()})).withStyle((var1x) -> {
         return var1x.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + var3.getX() + " ~ " + var3.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.coordinates.tooltip")));
      });
      var0.sendSuccess(new TranslatableComponent(var4, new Object[]{var1, var6, var5}), false);
      return var5;
   }

   private static float dist(int var0, int var1, int var2, int var3) {
      int var4 = var2 - var0;
      int var5 = var3 - var1;
      return Mth.sqrt((float)(var4 * var4 + var5 * var5));
   }
}

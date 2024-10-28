package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.WorldData;
import org.slf4j.Logger;

public class ReloadCommand {
   private static final Logger LOGGER = LogUtils.getLogger();

   public ReloadCommand() {
      super();
   }

   public static void reloadPacks(Collection<String> var0, CommandSourceStack var1) {
      var1.getServer().reloadResources(var0).exceptionally((var1x) -> {
         LOGGER.warn("Failed to execute reload", var1x);
         var1.sendFailure(Component.translatable("commands.reload.failure"));
         return null;
      });
   }

   private static Collection<String> discoverNewPacks(PackRepository var0, WorldData var1, Collection<String> var2) {
      var0.reload();
      ArrayList var3 = Lists.newArrayList(var2);
      List var4 = var1.getDataConfiguration().dataPacks().getDisabled();
      Iterator var5 = var0.getAvailableIds().iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         if (!var4.contains(var6) && !var3.contains(var6)) {
            var3.add(var6);
         }
      }

      return var3;
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("reload").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).executes((var0x) -> {
         CommandSourceStack var1 = (CommandSourceStack)var0x.getSource();
         MinecraftServer var2 = var1.getServer();
         PackRepository var3 = var2.getPackRepository();
         WorldData var4 = var2.getWorldData();
         Collection var5 = var3.getSelectedIds();
         Collection var6 = discoverNewPacks(var3, var4, var5);
         var1.sendSuccess(() -> {
            return Component.translatable("commands.reload.success");
         }, true);
         reloadPacks(var6, var1);
         return 0;
      }));
   }
}

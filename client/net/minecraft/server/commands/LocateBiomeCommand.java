package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class LocateBiomeCommand {
   public static final DynamicCommandExceptionType ERROR_INVALID_BIOME = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.locatebiome.invalid", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType ERROR_BIOME_NOT_FOUND = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.locatebiome.notFound", new Object[]{var0});
   });
   private static final int MAX_SEARCH_RADIUS = 6400;
   private static final int SEARCH_STEP = 8;

   public LocateBiomeCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("locatebiome").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("biome", ResourceLocationArgument.method_50()).suggests(SuggestionProviders.AVAILABLE_BIOMES).executes((var0x) -> {
         return locateBiome((CommandSourceStack)var0x.getSource(), (ResourceLocation)var0x.getArgument("biome", ResourceLocation.class));
      })));
   }

   private static int locateBiome(CommandSourceStack var0, ResourceLocation var1) throws CommandSyntaxException {
      Biome var2 = (Biome)var0.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOptional(var1).orElseThrow(() -> {
         return ERROR_INVALID_BIOME.create(var1);
      });
      BlockPos var3 = new BlockPos(var0.getPosition());
      BlockPos var4 = var0.getLevel().findNearestBiome(var2, var3, 6400, 8);
      String var5 = var1.toString();
      if (var4 == null) {
         throw ERROR_BIOME_NOT_FOUND.create(var5);
      } else {
         return LocateCommand.showLocateResult(var0, var5, var3, var4, "commands.locatebiome.success");
      }
   }
}

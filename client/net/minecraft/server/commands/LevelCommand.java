package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.DimensionGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WorldModifiers;
import net.minecraft.world.level.mines.SpecialMine;
import net.minecraft.world.level.mines.WorldEffect;

public class LevelCommand {
   public LevelCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("level").requires((var0x) -> var0x.hasPermission(2))).then(Commands.literal("hub").executes((var0x) -> teleportToHub((CommandSourceStack)var0x.getSource())))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("from").then(Commands.literal("item").executes((var0x) -> fromItem((CommandSourceStack)var0x.getSource())))).then(Commands.literal("effects").then(effectList(var1, 0, 20)))).then(Commands.literal("mine").then(Commands.argument("mine", ResourceArgument.resource(var1, Registries.SPECIAL_MINE)).executes((var0x) -> fromMine((CommandSourceStack)var0x.getSource(), ResourceArgument.getResource(var0x, "mine", Registries.SPECIAL_MINE)))))));
   }

   private static RequiredArgumentBuilder<CommandSourceStack, Holder.Reference<WorldEffect>> effectList(CommandBuildContext var0, int var1, int var2) {
      RequiredArgumentBuilder var3 = (RequiredArgumentBuilder)Commands.argument("effect" + var1, ResourceArgument.resource(var0, Registries.WORLD_EFFECT)).executes((var1x) -> fromEffectList(var1x, var1));
      return var1 == var2 ? var3 : (RequiredArgumentBuilder)var3.then(effectList(var0, var1 + 1, var2));
   }

   private static int fromEffectList(CommandContext<CommandSourceStack> var0, int var1) throws CommandSyntaxException {
      ArrayList var2 = new ArrayList();

      for(int var3 = 0; var3 <= var1; ++var3) {
         var2.add((WorldEffect)ResourceArgument.getResource(var0, "effect" + var3, Registries.WORLD_EFFECT).value());
      }

      return fromEffects((CommandSourceStack)var0.getSource(), var2);
   }

   private static int fromItem(CommandSourceStack var0) throws CommandSyntaxException {
      ServerPlayer var1 = var0.getPlayerOrException();
      ItemStack var2 = var1.getMainHandItem();
      WorldModifiers var3 = (WorldModifiers)var2.get(DataComponents.WORLD_MODIFIERS);
      if (var3 == null) {
         throw (new SimpleCommandExceptionType(Component.literal("Item bork. Plz fix"))).create();
      } else {
         List var4 = var3.effects();
         return fromEffects(var0, var4);
      }
   }

   private static int fromMine(CommandSourceStack var0, Holder<SpecialMine> var1) throws CommandSyntaxException {
      List var2 = ((SpecialMine)var1.value()).instantiate(var0.getLevel());
      return fromEffects(var0, var2, Optional.of((SpecialMine)var1.value()));
   }

   private static int fromEffects(CommandSourceStack var0, List<WorldEffect> var1) throws CommandSyntaxException {
      return fromEffects(var0, var1, Optional.empty());
   }

   private static int fromEffects(CommandSourceStack var0, List<WorldEffect> var1, Optional<SpecialMine> var2) throws CommandSyntaxException {
      DimensionGenerator.GeneratedDimension var3 = DimensionGenerator.generateDimension(var0.theGame(), var1, var2);
      ResourceLocation var4 = var3.id().location();
      var0.sendSuccess(() -> Component.translatable("commands.level.success", Component.translationArg(var4)), true);
      var3.synchronize().run();

      for(ServerPlayer var6 : var0.getLevel().players()) {
         var6.swapInventoryFromHub();
      }

      var0.theGame().server().sayGoodbye().thenAcceptAsync((var1x) -> {
         ServerLevel var2 = var1x.theGame().getLevel(Registries.levelStemToLevel(var3.id()));
         if (var2 != null) {
            var2.teleportAllPlayersToMine(false, Optional.empty());
         }

      }, var0.theGame().server());
      return 1;
   }

   private static int teleportToHub(CommandSourceStack var0) throws CommandSyntaxException {
      var0.getLevel().players().forEach((var0x) -> var0x.setRevisiting(false));
      var0.getLevel().handleMineWin(BlockPos.ZERO);
      return 1;
   }
}

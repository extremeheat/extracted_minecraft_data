package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class PlaySoundCommand {
   private static final SimpleCommandExceptionType ERROR_TOO_FAR = new SimpleCommandExceptionType(Component.translatable("commands.playsound.failed"));

   public PlaySoundCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      RequiredArgumentBuilder var1 = (RequiredArgumentBuilder)Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.cast(SuggestionProviders.AVAILABLE_SOUNDS)).executes((var0x) -> playSound((CommandSourceStack)var0x.getSource(), getCallingPlayerAsCollection(((CommandSourceStack)var0x.getSource()).getPlayer()), ResourceLocationArgument.getId(var0x, "sound"), SoundSource.MASTER, ((CommandSourceStack)var0x.getSource()).getPosition(), 1.0F, 1.0F, 0.0F));

      for(SoundSource var5 : SoundSource.values()) {
         var1.then(source(var5));
      }

      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("playsound").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).then(var1));
   }

   private static LiteralArgumentBuilder<CommandSourceStack> source(SoundSource var0) {
      return (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(var0.getName()).executes((var1) -> playSound((CommandSourceStack)var1.getSource(), getCallingPlayerAsCollection(((CommandSourceStack)var1.getSource()).getPlayer()), ResourceLocationArgument.getId(var1, "sound"), var0, ((CommandSourceStack)var1.getSource()).getPosition(), 1.0F, 1.0F, 0.0F))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((var1) -> playSound((CommandSourceStack)var1.getSource(), EntityArgument.getPlayers(var1, "targets"), ResourceLocationArgument.getId(var1, "sound"), var0, ((CommandSourceStack)var1.getSource()).getPosition(), 1.0F, 1.0F, 0.0F))).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes((var1) -> playSound((CommandSourceStack)var1.getSource(), EntityArgument.getPlayers(var1, "targets"), ResourceLocationArgument.getId(var1, "sound"), var0, Vec3Argument.getVec3(var1, "pos"), 1.0F, 1.0F, 0.0F))).then(((RequiredArgumentBuilder)Commands.argument("volume", FloatArgumentType.floatArg(0.0F)).executes((var1) -> playSound((CommandSourceStack)var1.getSource(), EntityArgument.getPlayers(var1, "targets"), ResourceLocationArgument.getId(var1, "sound"), var0, Vec3Argument.getVec3(var1, "pos"), (Float)var1.getArgument("volume", Float.class), 1.0F, 0.0F))).then(((RequiredArgumentBuilder)Commands.argument("pitch", FloatArgumentType.floatArg(0.0F, 2.0F)).executes((var1) -> playSound((CommandSourceStack)var1.getSource(), EntityArgument.getPlayers(var1, "targets"), ResourceLocationArgument.getId(var1, "sound"), var0, Vec3Argument.getVec3(var1, "pos"), (Float)var1.getArgument("volume", Float.class), (Float)var1.getArgument("pitch", Float.class), 0.0F))).then(Commands.argument("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F)).executes((var1) -> playSound((CommandSourceStack)var1.getSource(), EntityArgument.getPlayers(var1, "targets"), ResourceLocationArgument.getId(var1, "sound"), var0, Vec3Argument.getVec3(var1, "pos"), (Float)var1.getArgument("volume", Float.class), (Float)var1.getArgument("pitch", Float.class), (Float)var1.getArgument("minVolume", Float.class))))))));
   }

   private static Collection<ServerPlayer> getCallingPlayerAsCollection(@Nullable ServerPlayer var0) {
      return var0 != null ? List.of(var0) : List.of();
   }

   private static int playSound(CommandSourceStack var0, Collection<ServerPlayer> var1, ResourceLocation var2, SoundSource var3, Vec3 var4, float var5, float var6, float var7) throws CommandSyntaxException {
      Holder var8 = Holder.direct(SoundEvent.createVariableRangeEvent(var2));
      double var9 = (double)Mth.square(((SoundEvent)var8.value()).getRange(var5));
      ServerLevel var11 = var0.getLevel();
      long var12 = var11.getRandom().nextLong();
      ArrayList var14 = new ArrayList();

      for(ServerPlayer var16 : var1) {
         if (var16.level() == var11) {
            double var17 = var4.x - var16.getX();
            double var19 = var4.y - var16.getY();
            double var21 = var4.z - var16.getZ();
            double var23 = var17 * var17 + var19 * var19 + var21 * var21;
            Vec3 var25 = var4;
            float var26 = var5;
            if (var23 > var9) {
               if (var7 <= 0.0F) {
                  continue;
               }

               double var27 = Math.sqrt(var23);
               var25 = new Vec3(var16.getX() + var17 / var27 * 2.0, var16.getY() + var19 / var27 * 2.0, var16.getZ() + var21 / var27 * 2.0);
               var26 = var7;
            }

            var16.connection.send(new ClientboundSoundPacket(var8, var3, var25.x(), var25.y(), var25.z(), var26, var6, var12));
            var14.add(var16);
         }
      }

      int var29 = var14.size();
      if (var29 == 0) {
         throw ERROR_TOO_FAR.create();
      } else {
         if (var29 == 1) {
            var0.sendSuccess(() -> Component.translatable("commands.playsound.success.single", Component.translationArg(var2), ((ServerPlayer)var14.getFirst()).getDisplayName()), true);
         } else {
            var0.sendSuccess(() -> Component.translatable("commands.playsound.success.multiple", Component.translationArg(var2), var29), true);
         }

         return var29;
      }
   }
}

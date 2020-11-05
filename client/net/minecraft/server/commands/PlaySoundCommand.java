package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class PlaySoundCommand {
   private static final SimpleCommandExceptionType ERROR_TOO_FAR = new SimpleCommandExceptionType(new TranslatableComponent("commands.playsound.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      RequiredArgumentBuilder var1 = Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS);
      SoundSource[] var2 = SoundSource.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SoundSource var5 = var2[var4];
         var1.then(source(var5));
      }

      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("playsound").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(var1));
   }

   private static LiteralArgumentBuilder<CommandSourceStack> source(SoundSource var0) {
      return (LiteralArgumentBuilder)Commands.literal(var0.getName()).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((var1) -> {
         return playSound((CommandSourceStack)var1.getSource(), EntityArgument.getPlayers(var1, "targets"), ResourceLocationArgument.getId(var1, "sound"), var0, ((CommandSourceStack)var1.getSource()).getPosition(), 1.0F, 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes((var1) -> {
         return playSound((CommandSourceStack)var1.getSource(), EntityArgument.getPlayers(var1, "targets"), ResourceLocationArgument.getId(var1, "sound"), var0, Vec3Argument.getVec3(var1, "pos"), 1.0F, 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)Commands.argument("volume", FloatArgumentType.floatArg(0.0F)).executes((var1) -> {
         return playSound((CommandSourceStack)var1.getSource(), EntityArgument.getPlayers(var1, "targets"), ResourceLocationArgument.getId(var1, "sound"), var0, Vec3Argument.getVec3(var1, "pos"), (Float)var1.getArgument("volume", Float.class), 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)Commands.argument("pitch", FloatArgumentType.floatArg(0.0F, 2.0F)).executes((var1) -> {
         return playSound((CommandSourceStack)var1.getSource(), EntityArgument.getPlayers(var1, "targets"), ResourceLocationArgument.getId(var1, "sound"), var0, Vec3Argument.getVec3(var1, "pos"), (Float)var1.getArgument("volume", Float.class), (Float)var1.getArgument("pitch", Float.class), 0.0F);
      })).then(Commands.argument("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F)).executes((var1) -> {
         return playSound((CommandSourceStack)var1.getSource(), EntityArgument.getPlayers(var1, "targets"), ResourceLocationArgument.getId(var1, "sound"), var0, Vec3Argument.getVec3(var1, "pos"), (Float)var1.getArgument("volume", Float.class), (Float)var1.getArgument("pitch", Float.class), (Float)var1.getArgument("minVolume", Float.class));
      }))))));
   }

   private static int playSound(CommandSourceStack var0, Collection<ServerPlayer> var1, ResourceLocation var2, SoundSource var3, Vec3 var4, float var5, float var6, float var7) throws CommandSyntaxException {
      double var8 = Math.pow(var5 > 1.0F ? (double)(var5 * 16.0F) : 16.0D, 2.0D);
      int var10 = 0;
      Iterator var11 = var1.iterator();

      while(true) {
         ServerPlayer var12;
         Vec3 var21;
         float var22;
         while(true) {
            if (!var11.hasNext()) {
               if (var10 == 0) {
                  throw ERROR_TOO_FAR.create();
               }

               if (var1.size() == 1) {
                  var0.sendSuccess(new TranslatableComponent("commands.playsound.success.single", new Object[]{var2, ((ServerPlayer)var1.iterator().next()).getDisplayName()}), true);
               } else {
                  var0.sendSuccess(new TranslatableComponent("commands.playsound.success.multiple", new Object[]{var2, var1.size()}), true);
               }

               return var10;
            }

            var12 = (ServerPlayer)var11.next();
            double var13 = var4.x - var12.getX();
            double var15 = var4.y - var12.getY();
            double var17 = var4.z - var12.getZ();
            double var19 = var13 * var13 + var15 * var15 + var17 * var17;
            var21 = var4;
            var22 = var5;
            if (var19 <= var8) {
               break;
            }

            if (var7 > 0.0F) {
               double var23 = (double)Mth.sqrt(var19);
               var21 = new Vec3(var12.getX() + var13 / var23 * 2.0D, var12.getY() + var15 / var23 * 2.0D, var12.getZ() + var17 / var23 * 2.0D);
               var22 = var7;
               break;
            }
         }

         var12.connection.send(new ClientboundCustomSoundPacket(var2, var3, var21, var22, var6));
         ++var10;
      }
   }
}

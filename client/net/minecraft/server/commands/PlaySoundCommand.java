package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class PlaySoundCommand {
   private static final SimpleCommandExceptionType ERROR_TOO_FAR = new SimpleCommandExceptionType(Component.translatable("commands.playsound.failed"));

   public PlaySoundCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      RequiredArgumentBuilder var1 = Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS);

      for(SoundSource var5 : SoundSource.values()) {
         var1.then(source(var5));
      }

      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("playsound").requires(var0x -> var0x.hasPermission(2))).then(var1));
   }

   private static LiteralArgumentBuilder<CommandSourceStack> source(SoundSource var0) {
      return (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal(var0.getName())
         .then(
            ((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players())
                  .executes(
                     var1 -> playSound(
                           (CommandSourceStack)var1.getSource(),
                           EntityArgument.getPlayers(var1, "targets"),
                           ResourceLocationArgument.getId(var1, "sound"),
                           var0,
                           ((CommandSourceStack)var1.getSource()).getPosition(),
                           1.0F,
                           1.0F,
                           0.0F
                        )
                  ))
               .then(
                  ((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3())
                        .executes(
                           var1 -> playSound(
                                 (CommandSourceStack)var1.getSource(),
                                 EntityArgument.getPlayers(var1, "targets"),
                                 ResourceLocationArgument.getId(var1, "sound"),
                                 var0,
                                 Vec3Argument.getVec3(var1, "pos"),
                                 1.0F,
                                 1.0F,
                                 0.0F
                              )
                        ))
                     .then(
                        ((RequiredArgumentBuilder)Commands.argument("volume", FloatArgumentType.floatArg(0.0F))
                              .executes(
                                 var1 -> playSound(
                                       (CommandSourceStack)var1.getSource(),
                                       EntityArgument.getPlayers(var1, "targets"),
                                       ResourceLocationArgument.getId(var1, "sound"),
                                       var0,
                                       Vec3Argument.getVec3(var1, "pos"),
                                       var1.getArgument("volume", Float.class),
                                       1.0F,
                                       0.0F
                                    )
                              ))
                           .then(
                              ((RequiredArgumentBuilder)Commands.argument("pitch", FloatArgumentType.floatArg(0.0F, 2.0F))
                                    .executes(
                                       var1 -> playSound(
                                             (CommandSourceStack)var1.getSource(),
                                             EntityArgument.getPlayers(var1, "targets"),
                                             ResourceLocationArgument.getId(var1, "sound"),
                                             var0,
                                             Vec3Argument.getVec3(var1, "pos"),
                                             var1.getArgument("volume", Float.class),
                                             var1.getArgument("pitch", Float.class),
                                             0.0F
                                          )
                                    ))
                                 .then(
                                    Commands.argument("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F))
                                       .executes(
                                          var1 -> playSound(
                                                (CommandSourceStack)var1.getSource(),
                                                EntityArgument.getPlayers(var1, "targets"),
                                                ResourceLocationArgument.getId(var1, "sound"),
                                                var0,
                                                Vec3Argument.getVec3(var1, "pos"),
                                                var1.getArgument("volume", Float.class),
                                                var1.getArgument("pitch", Float.class),
                                                var1.getArgument("minVolume", Float.class)
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

   private static int playSound(
      CommandSourceStack var0, Collection<ServerPlayer> var1, ResourceLocation var2, SoundSource var3, Vec3 var4, float var5, float var6, float var7
   ) throws CommandSyntaxException {
      double var8 = Math.pow(var5 > 1.0F ? (double)(var5 * 16.0F) : 16.0, 2.0);
      int var10 = 0;
      long var11 = var0.getLevel().getRandom().nextLong();

      for(ServerPlayer var14 : var1) {
         double var15 = var4.x - var14.getX();
         double var17 = var4.y - var14.getY();
         double var19 = var4.z - var14.getZ();
         double var21 = var15 * var15 + var17 * var17 + var19 * var19;
         Vec3 var23 = var4;
         float var24 = var5;
         if (var21 > var8) {
            if (var7 <= 0.0F) {
               continue;
            }

            double var25 = Math.sqrt(var21);
            var23 = new Vec3(var14.getX() + var15 / var25 * 2.0, var14.getY() + var17 / var25 * 2.0, var14.getZ() + var19 / var25 * 2.0);
            var24 = var7;
         }

         var14.connection.send(new ClientboundCustomSoundPacket(var2, var3, var23, var24, var6, var11));
         ++var10;
      }

      if (var10 == 0) {
         throw ERROR_TOO_FAR.create();
      } else {
         if (var1.size() == 1) {
            var0.sendSuccess(Component.translatable("commands.playsound.success.single", var2, ((ServerPlayer)var1.iterator().next()).getDisplayName()), true);
         } else {
            var0.sendSuccess(Component.translatable("commands.playsound.success.multiple", var2, var1.size()), true);
         }

         return var10;
      }
   }
}

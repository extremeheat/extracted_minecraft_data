package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class ParticleCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.particle.failed"));

   public ParticleCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("particle").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((RequiredArgumentBuilder)Commands.argument("name", ParticleArgument.particle(var1)).executes((var0x) -> {
         return sendParticles((CommandSourceStack)var0x.getSource(), ParticleArgument.getParticle(var0x, "name"), ((CommandSourceStack)var0x.getSource()).getPosition(), Vec3.ZERO, 0.0F, 0, false, ((CommandSourceStack)var0x.getSource()).getServer().getPlayerList().getPlayers());
      })).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes((var0x) -> {
         return sendParticles((CommandSourceStack)var0x.getSource(), ParticleArgument.getParticle(var0x, "name"), Vec3Argument.getVec3(var0x, "pos"), Vec3.ZERO, 0.0F, 0, false, ((CommandSourceStack)var0x.getSource()).getServer().getPlayerList().getPlayers());
      })).then(Commands.argument("delta", Vec3Argument.vec3(false)).then(Commands.argument("speed", FloatArgumentType.floatArg(0.0F)).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("count", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return sendParticles((CommandSourceStack)var0x.getSource(), ParticleArgument.getParticle(var0x, "name"), Vec3Argument.getVec3(var0x, "pos"), Vec3Argument.getVec3(var0x, "delta"), FloatArgumentType.getFloat(var0x, "speed"), IntegerArgumentType.getInteger(var0x, "count"), false, ((CommandSourceStack)var0x.getSource()).getServer().getPlayerList().getPlayers());
      })).then(((LiteralArgumentBuilder)Commands.literal("force").executes((var0x) -> {
         return sendParticles((CommandSourceStack)var0x.getSource(), ParticleArgument.getParticle(var0x, "name"), Vec3Argument.getVec3(var0x, "pos"), Vec3Argument.getVec3(var0x, "delta"), FloatArgumentType.getFloat(var0x, "speed"), IntegerArgumentType.getInteger(var0x, "count"), true, ((CommandSourceStack)var0x.getSource()).getServer().getPlayerList().getPlayers());
      })).then(Commands.argument("viewers", EntityArgument.players()).executes((var0x) -> {
         return sendParticles((CommandSourceStack)var0x.getSource(), ParticleArgument.getParticle(var0x, "name"), Vec3Argument.getVec3(var0x, "pos"), Vec3Argument.getVec3(var0x, "delta"), FloatArgumentType.getFloat(var0x, "speed"), IntegerArgumentType.getInteger(var0x, "count"), true, EntityArgument.getPlayers(var0x, "viewers"));
      })))).then(((LiteralArgumentBuilder)Commands.literal("normal").executes((var0x) -> {
         return sendParticles((CommandSourceStack)var0x.getSource(), ParticleArgument.getParticle(var0x, "name"), Vec3Argument.getVec3(var0x, "pos"), Vec3Argument.getVec3(var0x, "delta"), FloatArgumentType.getFloat(var0x, "speed"), IntegerArgumentType.getInteger(var0x, "count"), false, ((CommandSourceStack)var0x.getSource()).getServer().getPlayerList().getPlayers());
      })).then(Commands.argument("viewers", EntityArgument.players()).executes((var0x) -> {
         return sendParticles((CommandSourceStack)var0x.getSource(), ParticleArgument.getParticle(var0x, "name"), Vec3Argument.getVec3(var0x, "pos"), Vec3Argument.getVec3(var0x, "delta"), FloatArgumentType.getFloat(var0x, "speed"), IntegerArgumentType.getInteger(var0x, "count"), false, EntityArgument.getPlayers(var0x, "viewers"));
      })))))))));
   }

   private static int sendParticles(CommandSourceStack var0, ParticleOptions var1, Vec3 var2, Vec3 var3, float var4, int var5, boolean var6, Collection<ServerPlayer> var7) throws CommandSyntaxException {
      int var8 = 0;
      Iterator var9 = var7.iterator();

      while(var9.hasNext()) {
         ServerPlayer var10 = (ServerPlayer)var9.next();
         if (var0.getLevel().sendParticles(var10, var1, var6, var2.x, var2.y, var2.z, var5, var3.x, var3.y, var3.z, (double)var4)) {
            ++var8;
         }
      }

      if (var8 == 0) {
         throw ERROR_FAILED.create();
      } else {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.particle.success", BuiltInRegistries.PARTICLE_TYPE.getKey(var1.getType()).toString());
         }, true);
         return var8;
      }
   }
}

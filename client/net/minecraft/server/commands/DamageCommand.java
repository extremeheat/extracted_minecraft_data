package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class DamageCommand {
   private static final SimpleCommandExceptionType ERROR_INVULNERABLE = new SimpleCommandExceptionType(Component.translatable("commands.damage.invulnerable"));

   public DamageCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("damage").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("target", EntityArgument.entity()).then(((RequiredArgumentBuilder)Commands.argument("amount", FloatArgumentType.floatArg(0.0F)).executes((var0x) -> {
         return damage((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), FloatArgumentType.getFloat(var0x, "amount"), ((CommandSourceStack)var0x.getSource()).getLevel().damageSources().generic());
      })).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("damageType", ResourceArgument.resource(var1, Registries.DAMAGE_TYPE)).executes((var0x) -> {
         return damage((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), FloatArgumentType.getFloat(var0x, "amount"), new DamageSource(ResourceArgument.getResource(var0x, "damageType", Registries.DAMAGE_TYPE)));
      })).then(Commands.literal("at").then(Commands.argument("location", Vec3Argument.vec3()).executes((var0x) -> {
         return damage((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), FloatArgumentType.getFloat(var0x, "amount"), new DamageSource(ResourceArgument.getResource(var0x, "damageType", Registries.DAMAGE_TYPE), Vec3Argument.getVec3(var0x, "location")));
      })))).then(Commands.literal("by").then(((RequiredArgumentBuilder)Commands.argument("entity", EntityArgument.entity()).executes((var0x) -> {
         return damage((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), FloatArgumentType.getFloat(var0x, "amount"), new DamageSource(ResourceArgument.getResource(var0x, "damageType", Registries.DAMAGE_TYPE), EntityArgument.getEntity(var0x, "entity")));
      })).then(Commands.literal("from").then(Commands.argument("cause", EntityArgument.entity()).executes((var0x) -> {
         return damage((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), FloatArgumentType.getFloat(var0x, "amount"), new DamageSource(ResourceArgument.getResource(var0x, "damageType", Registries.DAMAGE_TYPE), EntityArgument.getEntity(var0x, "entity"), EntityArgument.getEntity(var0x, "cause")));
      })))))))));
   }

   private static int damage(CommandSourceStack var0, Entity var1, float var2, DamageSource var3) throws CommandSyntaxException {
      if (var1.hurt(var3, var2)) {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.damage.success", var2, var1.getDisplayName());
         }, true);
         return 1;
      } else {
         throw ERROR_INVULNERABLE.create();
      }
   }
}

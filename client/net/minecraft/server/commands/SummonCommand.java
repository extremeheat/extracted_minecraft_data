package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SummonCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed"));
   private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed.uuid"));
   private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.summon.invalidPosition"));

   public SummonCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("summon").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((RequiredArgumentBuilder)Commands.argument("entity", ResourceArgument.resource(var1, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((var0x) -> {
         return spawnEntity((CommandSourceStack)var0x.getSource(), ResourceArgument.getSummonableEntityType(var0x, "entity"), ((CommandSourceStack)var0x.getSource()).getPosition(), new CompoundTag(), true);
      })).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes((var0x) -> {
         return spawnEntity((CommandSourceStack)var0x.getSource(), ResourceArgument.getSummonableEntityType(var0x, "entity"), Vec3Argument.getVec3(var0x, "pos"), new CompoundTag(), true);
      })).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes((var0x) -> {
         return spawnEntity((CommandSourceStack)var0x.getSource(), ResourceArgument.getSummonableEntityType(var0x, "entity"), Vec3Argument.getVec3(var0x, "pos"), CompoundTagArgument.getCompoundTag(var0x, "nbt"), false);
      })))));
   }

   public static Entity createEntity(CommandSourceStack var0, Holder.Reference<EntityType<?>> var1, Vec3 var2, CompoundTag var3, boolean var4) throws CommandSyntaxException {
      BlockPos var5 = BlockPos.containing(var2);
      if (!Level.isInSpawnableBounds(var5)) {
         throw INVALID_POSITION.create();
      } else {
         CompoundTag var6 = var3.copy();
         var6.putString("id", var1.key().location().toString());
         ServerLevel var7 = var0.getLevel();
         Entity var8 = EntityType.loadEntityRecursive(var6, var7, (var1x) -> {
            var1x.moveTo(var2.x, var2.y, var2.z, var1x.getYRot(), var1x.getXRot());
            return var1x;
         });
         if (var8 == null) {
            throw ERROR_FAILED.create();
         } else {
            if (var4 && var8 instanceof Mob) {
               ((Mob)var8).finalizeSpawn(var0.getLevel(), var0.getLevel().getCurrentDifficultyAt(var8.blockPosition()), MobSpawnType.COMMAND, (SpawnGroupData)null);
            }

            if (!var7.tryAddFreshEntityWithPassengers(var8)) {
               throw ERROR_DUPLICATE_UUID.create();
            } else {
               return var8;
            }
         }
      }
   }

   private static int spawnEntity(CommandSourceStack var0, Holder.Reference<EntityType<?>> var1, Vec3 var2, CompoundTag var3, boolean var4) throws CommandSyntaxException {
      Entity var5 = createEntity(var0, var1, var2, var3, var4);
      var0.sendSuccess(() -> {
         return Component.translatable("commands.summon.success", var5.getDisplayName());
      }, true);
      return 1;
   }
}

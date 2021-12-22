package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SummonCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.summon.failed"));
   private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(new TranslatableComponent("commands.summon.failed.uuid"));
   private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(new TranslatableComponent("commands.summon.invalidPosition"));

   public SummonCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("summon").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((RequiredArgumentBuilder)Commands.argument("entity", EntitySummonArgument.method_52()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((var0x) -> {
         return spawnEntity((CommandSourceStack)var0x.getSource(), EntitySummonArgument.getSummonableEntity(var0x, "entity"), ((CommandSourceStack)var0x.getSource()).getPosition(), new CompoundTag(), true);
      })).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes((var0x) -> {
         return spawnEntity((CommandSourceStack)var0x.getSource(), EntitySummonArgument.getSummonableEntity(var0x, "entity"), Vec3Argument.getVec3(var0x, "pos"), new CompoundTag(), true);
      })).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes((var0x) -> {
         return spawnEntity((CommandSourceStack)var0x.getSource(), EntitySummonArgument.getSummonableEntity(var0x, "entity"), Vec3Argument.getVec3(var0x, "pos"), CompoundTagArgument.getCompoundTag(var0x, "nbt"), false);
      })))));
   }

   private static int spawnEntity(CommandSourceStack var0, ResourceLocation var1, Vec3 var2, CompoundTag var3, boolean var4) throws CommandSyntaxException {
      BlockPos var5 = new BlockPos(var2);
      if (!Level.isInSpawnableBounds(var5)) {
         throw INVALID_POSITION.create();
      } else {
         CompoundTag var6 = var3.copy();
         var6.putString("id", var1.toString());
         ServerLevel var7 = var0.getLevel();
         Entity var8 = EntityType.loadEntityRecursive(var6, var7, (var1x) -> {
            var1x.moveTo(var2.field_414, var2.field_415, var2.field_416, var1x.getYRot(), var1x.getXRot());
            return var1x;
         });
         if (var8 == null) {
            throw ERROR_FAILED.create();
         } else {
            if (var4 && var8 instanceof Mob) {
               ((Mob)var8).finalizeSpawn(var0.getLevel(), var0.getLevel().getCurrentDifficultyAt(var8.blockPosition()), MobSpawnType.COMMAND, (SpawnGroupData)null, (CompoundTag)null);
            }

            if (!var7.tryAddFreshEntityWithPassengers(var8)) {
               throw ERROR_DUPLICATE_UUID.create();
            } else {
               var0.sendSuccess(new TranslatableComponent("commands.summon.success", new Object[]{var8.getDisplayName()}), true);
               return 1;
            }
         }
      }
   }
}

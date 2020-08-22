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
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.phys.Vec3;

public class SummonCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.summon.failed", new Object[0]));

   public static void register(CommandDispatcher var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("summon").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((RequiredArgumentBuilder)Commands.argument("entity", EntitySummonArgument.id()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((var0x) -> {
         return spawnEntity((CommandSourceStack)var0x.getSource(), EntitySummonArgument.getSummonableEntity(var0x, "entity"), ((CommandSourceStack)var0x.getSource()).getPosition(), new CompoundTag(), true);
      })).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes((var0x) -> {
         return spawnEntity((CommandSourceStack)var0x.getSource(), EntitySummonArgument.getSummonableEntity(var0x, "entity"), Vec3Argument.getVec3(var0x, "pos"), new CompoundTag(), true);
      })).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes((var0x) -> {
         return spawnEntity((CommandSourceStack)var0x.getSource(), EntitySummonArgument.getSummonableEntity(var0x, "entity"), Vec3Argument.getVec3(var0x, "pos"), CompoundTagArgument.getCompoundTag(var0x, "nbt"), false);
      })))));
   }

   private static int spawnEntity(CommandSourceStack var0, ResourceLocation var1, Vec3 var2, CompoundTag var3, boolean var4) throws CommandSyntaxException {
      CompoundTag var5 = var3.copy();
      var5.putString("id", var1.toString());
      if (EntityType.getKey(EntityType.LIGHTNING_BOLT).equals(var1)) {
         LightningBolt var8 = new LightningBolt(var0.getLevel(), var2.x, var2.y, var2.z, false);
         var0.getLevel().addGlobalEntity(var8);
         var0.sendSuccess(new TranslatableComponent("commands.summon.success", new Object[]{var8.getDisplayName()}), true);
         return 1;
      } else {
         ServerLevel var6 = var0.getLevel();
         Entity var7 = EntityType.loadEntityRecursive(var5, var6, (var2x) -> {
            var2x.moveTo(var2.x, var2.y, var2.z, var2x.yRot, var2x.xRot);
            return !var6.addWithUUID(var2x) ? null : var2x;
         });
         if (var7 == null) {
            throw ERROR_FAILED.create();
         } else {
            if (var4 && var7 instanceof Mob) {
               ((Mob)var7).finalizeSpawn(var0.getLevel(), var0.getLevel().getCurrentDifficultyAt(new BlockPos(var7)), MobSpawnType.COMMAND, (SpawnGroupData)null, (CompoundTag)null);
            }

            var0.sendSuccess(new TranslatableComponent("commands.summon.success", new Object[]{var7.getDisplayName()}), true);
            return 1;
         }
      }
   }
}

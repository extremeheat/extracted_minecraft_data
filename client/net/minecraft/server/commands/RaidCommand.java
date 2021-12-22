package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.raid.Raids;

public class RaidCommand {
   public RaidCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("raid").requires((var0x) -> {
         return var0x.hasPermission(3);
      })).then(Commands.literal("start").then(Commands.argument("omenlvl", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return start((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "omenlvl"));
      })))).then(Commands.literal("stop").executes((var0x) -> {
         return stop((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("check").executes((var0x) -> {
         return check((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("sound").then(Commands.argument("type", ComponentArgument.textComponent()).executes((var0x) -> {
         return playSound((CommandSourceStack)var0x.getSource(), ComponentArgument.getComponent(var0x, "type"));
      })))).then(Commands.literal("spawnleader").executes((var0x) -> {
         return spawnLeader((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("setomen").then(Commands.argument("level", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return setBadOmenLevel((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "level"));
      })))).then(Commands.literal("glow").executes((var0x) -> {
         return glow((CommandSourceStack)var0x.getSource());
      })));
   }

   private static int glow(CommandSourceStack var0) throws CommandSyntaxException {
      Raid var1 = getRaid(var0.getPlayerOrException());
      if (var1 != null) {
         Set var2 = var1.getAllRaiders();
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            Raider var4 = (Raider)var3.next();
            var4.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1000, 1));
         }
      }

      return 1;
   }

   private static int setBadOmenLevel(CommandSourceStack var0, int var1) throws CommandSyntaxException {
      Raid var2 = getRaid(var0.getPlayerOrException());
      if (var2 != null) {
         int var3 = var2.getMaxBadOmenLevel();
         if (var1 > var3) {
            var0.sendFailure(new TextComponent("Sorry, the max bad omen level you can set is " + var3));
         } else {
            int var4 = var2.getBadOmenLevel();
            var2.setBadOmenLevel(var1);
            var0.sendSuccess(new TextComponent("Changed village's bad omen level from " + var4 + " to " + var1), false);
         }
      } else {
         var0.sendFailure(new TextComponent("No raid found here"));
      }

      return 1;
   }

   private static int spawnLeader(CommandSourceStack var0) {
      var0.sendSuccess(new TextComponent("Spawned a raid captain"), false);
      Raider var1 = (Raider)EntityType.PILLAGER.create(var0.getLevel());
      var1.setPatrolLeader(true);
      var1.setItemSlot(EquipmentSlot.HEAD, Raid.getLeaderBannerInstance());
      var1.setPos(var0.getPosition().field_414, var0.getPosition().field_415, var0.getPosition().field_416);
      var1.finalizeSpawn(var0.getLevel(), var0.getLevel().getCurrentDifficultyAt(new BlockPos(var0.getPosition())), MobSpawnType.COMMAND, (SpawnGroupData)null, (CompoundTag)null);
      var0.getLevel().addFreshEntityWithPassengers(var1);
      return 1;
   }

   private static int playSound(CommandSourceStack var0, Component var1) {
      if (var1 != null && var1.getString().equals("local")) {
         var0.getLevel().playSound((Player)null, new BlockPos(var0.getPosition().add(5.0D, 0.0D, 0.0D)), SoundEvents.RAID_HORN, SoundSource.NEUTRAL, 2.0F, 1.0F);
      }

      return 1;
   }

   private static int start(CommandSourceStack var0, int var1) throws CommandSyntaxException {
      ServerPlayer var2 = var0.getPlayerOrException();
      BlockPos var3 = var2.blockPosition();
      if (var2.getLevel().isRaided(var3)) {
         var0.sendFailure(new TextComponent("Raid already started close by"));
         return -1;
      } else {
         Raids var4 = var2.getLevel().getRaids();
         Raid var5 = var4.createOrExtendRaid(var2);
         if (var5 != null) {
            var5.setBadOmenLevel(var1);
            var4.setDirty();
            var0.sendSuccess(new TextComponent("Created a raid in your local village"), false);
         } else {
            var0.sendFailure(new TextComponent("Failed to create a raid in your local village"));
         }

         return 1;
      }
   }

   private static int stop(CommandSourceStack var0) throws CommandSyntaxException {
      ServerPlayer var1 = var0.getPlayerOrException();
      BlockPos var2 = var1.blockPosition();
      Raid var3 = var1.getLevel().getRaidAt(var2);
      if (var3 != null) {
         var3.stop();
         var0.sendSuccess(new TextComponent("Stopped raid"), false);
         return 1;
      } else {
         var0.sendFailure(new TextComponent("No raid here"));
         return -1;
      }
   }

   private static int check(CommandSourceStack var0) throws CommandSyntaxException {
      Raid var1 = getRaid(var0.getPlayerOrException());
      if (var1 != null) {
         StringBuilder var2 = new StringBuilder();
         var2.append("Found a started raid! ");
         var0.sendSuccess(new TextComponent(var2.toString()), false);
         var2 = new StringBuilder();
         var2.append("Num groups spawned: ");
         var2.append(var1.getGroupsSpawned());
         var2.append(" Bad omen level: ");
         var2.append(var1.getBadOmenLevel());
         var2.append(" Num mobs: ");
         var2.append(var1.getTotalRaidersAlive());
         var2.append(" Raid health: ");
         var2.append(var1.getHealthOfLivingRaiders());
         var2.append(" / ");
         var2.append(var1.getTotalHealth());
         var0.sendSuccess(new TextComponent(var2.toString()), false);
         return 1;
      } else {
         var0.sendFailure(new TextComponent("Found no started raids"));
         return 0;
      }
   }

   @Nullable
   private static Raid getRaid(ServerPlayer var0) {
      return var0.getLevel().getRaidAt(var0.blockPosition());
   }
}

package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.phys.Vec3;

public class RaidCommand {
   public RaidCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                                    "raid"
                                 )
                                 .requires(var0x -> var0x.hasPermission(3)))
                              .then(
                                 Commands.literal("start")
                                    .then(
                                       Commands.argument("omenlvl", IntegerArgumentType.integer(0))
                                          .executes(var0x -> start((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "omenlvl")))
                                    )
                              ))
                           .then(Commands.literal("stop").executes(var0x -> stop((CommandSourceStack)var0x.getSource()))))
                        .then(Commands.literal("check").executes(var0x -> check((CommandSourceStack)var0x.getSource()))))
                     .then(
                        Commands.literal("sound")
                           .then(
                              Commands.argument("type", ComponentArgument.textComponent())
                                 .executes(var0x -> playSound((CommandSourceStack)var0x.getSource(), ComponentArgument.getComponent(var0x, "type")))
                           )
                     ))
                  .then(Commands.literal("spawnleader").executes(var0x -> spawnLeader((CommandSourceStack)var0x.getSource()))))
               .then(
                  Commands.literal("setomen")
                     .then(
                        Commands.argument("level", IntegerArgumentType.integer(0))
                           .executes(var0x -> setBadOmenLevel((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "level")))
                     )
               ))
            .then(Commands.literal("glow").executes(var0x -> glow((CommandSourceStack)var0x.getSource())))
      );
   }

   private static int glow(CommandSourceStack var0) throws CommandSyntaxException {
      Raid var1 = getRaid(var0.getPlayerOrException());
      if (var1 != null) {
         for(Raider var4 : var1.getAllRaiders()) {
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
            var0.sendFailure(Component.literal("Sorry, the max bad omen level you can set is " + var3));
         } else {
            int var4 = var2.getBadOmenLevel();
            var2.setBadOmenLevel(var1);
            var0.sendSuccess(() -> Component.literal("Changed village's bad omen level from " + var4 + " to " + var1), false);
         }
      } else {
         var0.sendFailure(Component.literal("No raid found here"));
      }

      return 1;
   }

   private static int spawnLeader(CommandSourceStack var0) {
      var0.sendSuccess(() -> Component.literal("Spawned a raid captain"), false);
      Raider var1 = EntityType.PILLAGER.create(var0.getLevel());
      if (var1 == null) {
         var0.sendFailure(Component.literal("Pillager failed to spawn"));
         return 0;
      } else {
         var1.setPatrolLeader(true);
         var1.setItemSlot(EquipmentSlot.HEAD, Raid.getLeaderBannerInstance());
         var1.setPos(var0.getPosition().x, var0.getPosition().y, var0.getPosition().z);
         var1.finalizeSpawn(var0.getLevel(), var0.getLevel().getCurrentDifficultyAt(BlockPos.containing(var0.getPosition())), MobSpawnType.COMMAND, null, null);
         var0.getLevel().addFreshEntityWithPassengers(var1);
         return 1;
      }
   }

   private static int playSound(CommandSourceStack var0, @Nullable Component var1) {
      if (var1 != null && var1.getString().equals("local")) {
         ServerLevel var2 = var0.getLevel();
         Vec3 var3 = var0.getPosition().add(5.0, 0.0, 0.0);
         var2.playSeededSound(null, var3.x, var3.y, var3.z, SoundEvents.RAID_HORN, SoundSource.NEUTRAL, 2.0F, 1.0F, var2.random.nextLong());
      }

      return 1;
   }

   private static int start(CommandSourceStack var0, int var1) throws CommandSyntaxException {
      ServerPlayer var2 = var0.getPlayerOrException();
      BlockPos var3 = var2.blockPosition();
      if (var2.serverLevel().isRaided(var3)) {
         var0.sendFailure(Component.literal("Raid already started close by"));
         return -1;
      } else {
         Raids var4 = var2.serverLevel().getRaids();
         Raid var5 = var4.createOrExtendRaid(var2);
         if (var5 != null) {
            var5.setBadOmenLevel(var1);
            var4.setDirty();
            var0.sendSuccess(() -> Component.literal("Created a raid in your local village"), false);
         } else {
            var0.sendFailure(Component.literal("Failed to create a raid in your local village"));
         }

         return 1;
      }
   }

   private static int stop(CommandSourceStack var0) throws CommandSyntaxException {
      ServerPlayer var1 = var0.getPlayerOrException();
      BlockPos var2 = var1.blockPosition();
      Raid var3 = var1.serverLevel().getRaidAt(var2);
      if (var3 != null) {
         var3.stop();
         var0.sendSuccess(() -> Component.literal("Stopped raid"), false);
         return 1;
      } else {
         var0.sendFailure(Component.literal("No raid here"));
         return -1;
      }
   }

   private static int check(CommandSourceStack var0) throws CommandSyntaxException {
      Raid var1 = getRaid(var0.getPlayerOrException());
      if (var1 != null) {
         StringBuilder var2 = new StringBuilder();
         var2.append("Found a started raid! ");
         var0.sendSuccess(() -> Component.literal(var2.toString()), false);
         StringBuilder var3 = new StringBuilder();
         var3.append("Num groups spawned: ");
         var3.append(var1.getGroupsSpawned());
         var3.append(" Bad omen level: ");
         var3.append(var1.getBadOmenLevel());
         var3.append(" Num mobs: ");
         var3.append(var1.getTotalRaidersAlive());
         var3.append(" Raid health: ");
         var3.append(var1.getHealthOfLivingRaiders());
         var3.append(" / ");
         var3.append(var1.getTotalHealth());
         var0.sendSuccess(() -> Component.literal(var3.toString()), false);
         return 1;
      } else {
         var0.sendFailure(Component.literal("Found no started raids"));
         return 0;
      }
   }

   @Nullable
   private static Raid getRaid(ServerPlayer var0) {
      return var0.serverLevel().getRaidAt(var0.blockPosition());
   }
}

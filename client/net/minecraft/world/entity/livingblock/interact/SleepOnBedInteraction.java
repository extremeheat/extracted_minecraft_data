package net.minecraft.world.entity.livingblock.interact;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SleepOnBedInteraction implements OnInteract {
   public SleepOnBedInteraction() {
      super();
   }

   public InteractionResult apply(final Player player, final InteractionHand hand, final Vec3 location, final LivingBlock bedEntity) {
      if (bedEntity.level().isClientSide()) {
         return InteractionResult.SUCCESS_SERVER;
      } else {
         ServerLevel level = (ServerLevel)bedEntity.level();
         BlockPos bedPos = bedEntity.blockPosition();
         BedRule bedRule = (BedRule)level.environmentAttributes().getValue(EnvironmentAttributes.BED_RULE, bedPos);
         if (bedRule.explodes()) {
            Optional var17 = bedRule.errorMessage();
            Objects.requireNonNull(player);
            var17.ifPresent(player::sendOverlayMessage);
            bedEntity.discard();
            Vec3 boomPos = bedEntity.position().add(0.0, 0.5, 0.0);
            level.explode((Entity)null, level.damageSources().badRespawnPointExplosion(boomPos), (ExplosionDamageCalculator)null, boomPos, 5.0F, true, Level.ExplosionInteraction.BLOCK);
            return InteractionResult.SUCCESS_SERVER;
         } else if (bedEntity.isVehicle()) {
            player.sendOverlayMessage(Component.translatable("block.minecraft.bed.occupied"));
            return InteractionResult.SUCCESS_SERVER;
         } else {
            BedRule rule = (BedRule)level.environmentAttributes().getValue(EnvironmentAttributes.BED_RULE, bedPos);
            boolean canSleep = rule.canSleep(level);
            if (!canSleep) {
               Optional var10000 = rule.errorMessage();
               Objects.requireNonNull(player);
               var10000.ifPresent(player::sendOverlayMessage);
               return InteractionResult.SUCCESS_SERVER;
            } else {
               double hRange = 8.0;
               double vRange = 5.0;
               Vec3 bedCenter = Vec3.atBottomCenterOf(bedPos);
               List<Monster> monsters = level.getEntitiesOfClass(Monster.class, new AABB(bedCenter.x() - 8.0, bedCenter.y() - 5.0, bedCenter.z() - 8.0, bedCenter.x() + 8.0, bedCenter.y() + 5.0, bedCenter.z() + 8.0), (monster) -> monster.isPreventingPlayerRest(level, player));
               if (!monsters.isEmpty()) {
                  player.sendOverlayMessage(Component.translatable("block.minecraft.bed.not_safe"));
                  return InteractionResult.SUCCESS_SERVER;
               } else if (!player.startRiding(bedEntity)) {
                  return InteractionResult.CONSUME;
               } else {
                  player.startSleepOnEntity(bedEntity);
                  return InteractionResult.SUCCESS_SERVER;
               }
            }
         }
      }
   }
}

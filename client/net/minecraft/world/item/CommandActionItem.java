package net.minecraft.world.item;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingBlockCommand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Targetable;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.LivingBlockGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public abstract class CommandActionItem extends ActionItem {
   private final LivingBlockCommand.Type commandType;

   public CommandActionItem(final Item.Properties properties, final LivingBlockCommand.Type commandType) {
      super(properties);
      this.commandType = commandType;
   }

   public void command(final LivingBlock block, final ServerPlayer player, final Vec3 pos, final BlockPos blockPos, final Direction direction) {
   }

   public void command(final LivingBlock block, final ServerPlayer player, final Targetable target) {
   }

   public boolean actionOnBlock(final Player player, final BlockPos pos, final Direction direction) {
      if (player.isSpectator()) {
         return false;
      } else if (player instanceof ServerPlayer) {
         ServerPlayer serverPlayer = (ServerPlayer)player;
         Vec3 targetPos = pos.getCenter().add(direction.getUnitVec3().scale(0.5));
         this.applyCommand(serverPlayer, targetPos, (livingBlock) -> this.command(livingBlock, serverPlayer, targetPos, pos, direction));
         return true;
      } else {
         return true;
      }
   }

   public boolean actionOnEntity(final Player player, final Entity entity) {
      if (player.isSpectator()) {
         return false;
      } else if (player instanceof ServerPlayer) {
         ServerPlayer serverPlayer = (ServerPlayer)player;
         if (entity instanceof LivingEntity) {
            LivingEntity target = (LivingEntity)entity;
            this.applyCommand(serverPlayer, target.position(), (livingBlock) -> this.command(livingBlock, serverPlayer, target));
            return true;
         } else if (entity instanceof EnderDragonPart) {
            EnderDragonPart target = (EnderDragonPart)entity;
            this.applyCommand(serverPlayer, target.position(), (livingBlock) -> this.command(livingBlock, serverPlayer, target.parentMob));
            return true;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   protected boolean applyCommand(final ServerPlayer serverPlayer, final Vec3 targetPos, final Consumer<LivingBlock> action) {
      List<LivingBlock> livingBlockEntities = serverPlayer.getCommandedBlocks();

      for(LivingBlock livingBlock : livingBlockEntities) {
         if (livingBlock.isSelected() && livingBlock.getGroup() == LivingBlockGroup.NONE) {
            livingBlock.setOwner((Player)null);
         }

         livingBlock.setCommander(serverPlayer);
         livingBlock.setSelected(false);
         livingBlock.removeLeash();
         action.accept(livingBlock);
      }

      if (!livingBlockEntities.isEmpty()) {
         this.spawnCommandEntity(serverPlayer.level(), targetPos);
         return true;
      } else {
         return false;
      }
   }

   public InteractionResult interactLivingBlock(final Player player, final LivingBlock target) {
      if (target.canBeControlledBy(player) && !player.isShiftKeyDown()) {
         boolean selected = !target.isSelected();
         if (selected) {
            target.setOwner(player);
            target.setSelected(true);
         } else {
            target.setSelected(false);
            if (target.getGroup() == LivingBlockGroup.NONE) {
               target.setOwner((Player)null);
            }
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.FAIL;
      }
   }

   protected void spawnCommandEntity(final ServerLevel level, final Vec3 position) {
      LivingBlockCommand command = LivingBlockCommand.create(level, position.add(0.0, 0.1, 0.0), this.commandType);
      level.addFreshEntity(command);
      level.sendParticles(ParticleTypes.END_ROD, position.x(), position.y() + 0.1, position.z(), 1, 0.0, 0.0, 0.0, 0.0);
   }
}

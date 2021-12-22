package net.minecraft.world.level.block.entity;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandBlockEntity extends BlockEntity {
   private boolean powered;
   private boolean auto;
   private boolean conditionMet;
   private final BaseCommandBlock commandBlock = new BaseCommandBlock() {
      public void setCommand(String var1) {
         super.setCommand(var1);
         CommandBlockEntity.this.setChanged();
      }

      public ServerLevel getLevel() {
         return (ServerLevel)CommandBlockEntity.this.level;
      }

      public void onUpdated() {
         BlockState var1 = CommandBlockEntity.this.level.getBlockState(CommandBlockEntity.this.worldPosition);
         this.getLevel().sendBlockUpdated(CommandBlockEntity.this.worldPosition, var1, var1, 3);
      }

      public Vec3 getPosition() {
         return Vec3.atCenterOf(CommandBlockEntity.this.worldPosition);
      }

      public CommandSourceStack createCommandSourceStack() {
         return new CommandSourceStack(this, Vec3.atCenterOf(CommandBlockEntity.this.worldPosition), Vec2.ZERO, this.getLevel(), 2, this.getName().getString(), this.getName(), this.getLevel().getServer(), (Entity)null);
      }
   };

   public CommandBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.COMMAND_BLOCK, var1, var2);
   }

   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      this.commandBlock.save(var1);
      var1.putBoolean("powered", this.isPowered());
      var1.putBoolean("conditionMet", this.wasConditionMet());
      var1.putBoolean("auto", this.isAutomatic());
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.commandBlock.load(var1);
      this.powered = var1.getBoolean("powered");
      this.conditionMet = var1.getBoolean("conditionMet");
      this.setAutomatic(var1.getBoolean("auto"));
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public BaseCommandBlock getCommandBlock() {
      return this.commandBlock;
   }

   public void setPowered(boolean var1) {
      this.powered = var1;
   }

   public boolean isPowered() {
      return this.powered;
   }

   public boolean isAutomatic() {
      return this.auto;
   }

   public void setAutomatic(boolean var1) {
      boolean var2 = this.auto;
      this.auto = var1;
      if (!var2 && var1 && !this.powered && this.level != null && this.getMode() != CommandBlockEntity.Mode.SEQUENCE) {
         this.scheduleTick();
      }

   }

   public void onModeSwitch() {
      CommandBlockEntity.Mode var1 = this.getMode();
      if (var1 == CommandBlockEntity.Mode.AUTO && (this.powered || this.auto) && this.level != null) {
         this.scheduleTick();
      }

   }

   private void scheduleTick() {
      Block var1 = this.getBlockState().getBlock();
      if (var1 instanceof CommandBlock) {
         this.markConditionMet();
         this.level.scheduleTick(this.worldPosition, var1, 1);
      }

   }

   public boolean wasConditionMet() {
      return this.conditionMet;
   }

   public boolean markConditionMet() {
      this.conditionMet = true;
      if (this.isConditional()) {
         BlockPos var1 = this.worldPosition.relative(((Direction)this.level.getBlockState(this.worldPosition).getValue(CommandBlock.FACING)).getOpposite());
         if (this.level.getBlockState(var1).getBlock() instanceof CommandBlock) {
            BlockEntity var2 = this.level.getBlockEntity(var1);
            this.conditionMet = var2 instanceof CommandBlockEntity && ((CommandBlockEntity)var2).getCommandBlock().getSuccessCount() > 0;
         } else {
            this.conditionMet = false;
         }
      }

      return this.conditionMet;
   }

   public CommandBlockEntity.Mode getMode() {
      BlockState var1 = this.getBlockState();
      if (var1.is(Blocks.COMMAND_BLOCK)) {
         return CommandBlockEntity.Mode.REDSTONE;
      } else if (var1.is(Blocks.REPEATING_COMMAND_BLOCK)) {
         return CommandBlockEntity.Mode.AUTO;
      } else {
         return var1.is(Blocks.CHAIN_COMMAND_BLOCK) ? CommandBlockEntity.Mode.SEQUENCE : CommandBlockEntity.Mode.REDSTONE;
      }
   }

   public boolean isConditional() {
      BlockState var1 = this.level.getBlockState(this.getBlockPos());
      return var1.getBlock() instanceof CommandBlock ? (Boolean)var1.getValue(CommandBlock.CONDITIONAL) : false;
   }

   public static enum Mode {
      SEQUENCE,
      AUTO,
      REDSTONE;

      private Mode() {
      }

      // $FF: synthetic method
      private static CommandBlockEntity.Mode[] $values() {
         return new CommandBlockEntity.Mode[]{SEQUENCE, AUTO, REDSTONE};
      }
   }
}

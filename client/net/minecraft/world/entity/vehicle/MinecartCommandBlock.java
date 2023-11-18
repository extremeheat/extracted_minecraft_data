package net.minecraft.world.entity.vehicle;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MinecartCommandBlock extends AbstractMinecart {
   static final EntityDataAccessor<String> DATA_ID_COMMAND_NAME = SynchedEntityData.defineId(MinecartCommandBlock.class, EntityDataSerializers.STRING);
   static final EntityDataAccessor<Component> DATA_ID_LAST_OUTPUT = SynchedEntityData.defineId(MinecartCommandBlock.class, EntityDataSerializers.COMPONENT);
   private final BaseCommandBlock commandBlock = new MinecartCommandBlock.MinecartCommandBase();
   private static final int ACTIVATION_DELAY = 4;
   private int lastActivated;

   public MinecartCommandBlock(EntityType<? extends MinecartCommandBlock> var1, Level var2) {
      super(var1, var2);
   }

   public MinecartCommandBlock(Level var1, double var2, double var4, double var6) {
      super(EntityType.COMMAND_BLOCK_MINECART, var1, var2, var4, var6);
   }

   @Override
   protected Item getDropItem() {
      return Items.MINECART;
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.getEntityData().define(DATA_ID_COMMAND_NAME, "");
      this.getEntityData().define(DATA_ID_LAST_OUTPUT, CommonComponents.EMPTY);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.commandBlock.load(var1);
      this.getEntityData().set(DATA_ID_COMMAND_NAME, this.getCommandBlock().getCommand());
      this.getEntityData().set(DATA_ID_LAST_OUTPUT, this.getCommandBlock().getLastOutput());
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      this.commandBlock.save(var1);
   }

   @Override
   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.COMMAND_BLOCK;
   }

   @Override
   public BlockState getDefaultDisplayBlockState() {
      return Blocks.COMMAND_BLOCK.defaultBlockState();
   }

   public BaseCommandBlock getCommandBlock() {
      return this.commandBlock;
   }

   @Override
   public void activateMinecart(int var1, int var2, int var3, boolean var4) {
      if (var4 && this.tickCount - this.lastActivated >= 4) {
         this.getCommandBlock().performCommand(this.level());
         this.lastActivated = this.tickCount;
      }
   }

   @Override
   public InteractionResult interact(Player var1, InteractionHand var2) {
      return this.commandBlock.usedBy(var1);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      super.onSyncedDataUpdated(var1);
      if (DATA_ID_LAST_OUTPUT.equals(var1)) {
         try {
            this.commandBlock.setLastOutput(this.getEntityData().get(DATA_ID_LAST_OUTPUT));
         } catch (Throwable var3) {
         }
      } else if (DATA_ID_COMMAND_NAME.equals(var1)) {
         this.commandBlock.setCommand(this.getEntityData().get(DATA_ID_COMMAND_NAME));
      }
   }

   @Override
   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public class MinecartCommandBase extends BaseCommandBlock {
      public MinecartCommandBase() {
         super();
      }

      @Override
      public ServerLevel getLevel() {
         return (ServerLevel)MinecartCommandBlock.this.level();
      }

      @Override
      public void onUpdated() {
         MinecartCommandBlock.this.getEntityData().set(MinecartCommandBlock.DATA_ID_COMMAND_NAME, this.getCommand());
         MinecartCommandBlock.this.getEntityData().set(MinecartCommandBlock.DATA_ID_LAST_OUTPUT, this.getLastOutput());
      }

      @Override
      public Vec3 getPosition() {
         return MinecartCommandBlock.this.position();
      }

      public MinecartCommandBlock getMinecart() {
         return MinecartCommandBlock.this;
      }

      @Override
      public CommandSourceStack createCommandSourceStack() {
         return new CommandSourceStack(
            this,
            MinecartCommandBlock.this.position(),
            MinecartCommandBlock.this.getRotationVector(),
            this.getLevel(),
            2,
            this.getName().getString(),
            MinecartCommandBlock.this.getDisplayName(),
            this.getLevel().getServer(),
            MinecartCommandBlock.this
         );
      }

      @Override
      public boolean isValid() {
         return !MinecartCommandBlock.this.isRemoved();
      }
   }
}

package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.TestBlockMode;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.slf4j.Logger;

public class TestBlockEntity extends BlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String DEFAULT_MESSAGE = "";
   private static final boolean DEFAULT_POWERED = false;
   private TestBlockMode mode;
   private String message = "";
   private boolean powered = false;
   private boolean triggered;

   public TestBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.TEST_BLOCK, var1, var2);
      this.mode = (TestBlockMode)var2.getValue(TestBlock.MODE);
   }

   protected void saveAdditional(ValueOutput var1) {
      var1.store("mode", TestBlockMode.CODEC, this.mode);
      var1.putString("message", this.message);
      var1.putBoolean("powered", this.powered);
   }

   protected void loadAdditional(ValueInput var1) {
      this.mode = (TestBlockMode)var1.read("mode", TestBlockMode.CODEC).orElse(TestBlockMode.FAIL);
      this.message = var1.getStringOr("message", "");
      this.powered = var1.getBooleanOr("powered", false);
   }

   private void updateBlockState() {
      if (this.level != null) {
         BlockPos var1 = this.getBlockPos();
         BlockState var2 = this.level.getBlockState(var1);
         if (var2.is(Blocks.TEST_BLOCK)) {
            this.level.setBlock(var1, (BlockState)var2.setValue(TestBlock.MODE, this.mode), 2);
         }

      }
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   public boolean isPowered() {
      return this.powered;
   }

   public void setPowered(boolean var1) {
      this.powered = var1;
   }

   public TestBlockMode getMode() {
      return this.mode;
   }

   public void setMode(TestBlockMode var1) {
      this.mode = var1;
      this.updateBlockState();
   }

   private Block getBlockType() {
      return this.getBlockState().getBlock();
   }

   public void reset() {
      this.triggered = false;
      if (this.mode == TestBlockMode.START && this.level != null) {
         this.setPowered(false);
         this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockType());
      }

   }

   public void trigger() {
      if (this.mode == TestBlockMode.START && this.level != null) {
         this.setPowered(true);
         BlockPos var1 = this.getBlockPos();
         this.level.updateNeighborsAt(var1, this.getBlockType());
         this.level.getBlockTicks().willTickThisTick(var1, this.getBlockType());
         this.log();
      } else {
         if (this.mode == TestBlockMode.LOG) {
            this.log();
         }

         this.triggered = true;
      }
   }

   public void log() {
      if (!this.message.isBlank()) {
         LOGGER.info("Test {} (at {}): {}", new Object[]{this.mode.getSerializedName(), this.getBlockPos(), this.message});
      }

   }

   public boolean hasTriggered() {
      return this.triggered;
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }

   // $FF: synthetic method
   @Nullable
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}

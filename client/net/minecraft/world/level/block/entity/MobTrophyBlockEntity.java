package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.MobTrophyInfo;
import net.minecraft.world.level.block.state.BlockState;

public class MobTrophyBlockEntity extends BlockEntity {
   public MobTrophyBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.MOB_TROPHY, var1, var2);
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveWithoutMetadata(var1);
   }

   @Nullable
   public MobTrophyInfo getEntityType() {
      return (MobTrophyInfo)this.components().get(DataComponents.MOB_TROPHY_TYPE);
   }

   // $FF: synthetic method
   @Nullable
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}

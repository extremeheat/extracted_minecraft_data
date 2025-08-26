package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CopperGolemStatueBlockEntity extends BlockEntity {
   public CopperGolemStatueBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.COPPER_GOLEM_STATUE, var1, var2);
   }

   public void createStatue(CopperGolem var1) {
      this.setComponents(DataComponentMap.builder().addAll(this.components()).set(DataComponents.CUSTOM_NAME, var1.getCustomName()).build());
      super.setChanged();
   }

   @Nullable
   public CopperGolem removeStatue(BlockState var1) {
      Component var2 = (Component)this.components().get(DataComponents.CUSTOM_NAME);
      CopperGolem var3 = EntityType.COPPER_GOLEM.create(this.level, EntitySpawnReason.TRIGGERED);
      if (var3 != null) {
         if (var2 != null) {
            var3.setCustomName(var2);
         }

         return this.initCopperGolem(var1, var3);
      } else {
         return null;
      }
   }

   private CopperGolem initCopperGolem(BlockState var1, CopperGolem var2) {
      BlockPos var3 = this.getBlockPos();
      var2.snapTo(var3.getCenter().x, (double)var3.getY(), var3.getCenter().z, ((Direction)var1.getValue(CopperGolemStatueBlock.FACING)).toYRot(), 0.0F);
      var2.yHeadRot = var2.getYRot();
      var2.yBodyRot = var2.getYRot();
      var2.playSpawnSound();
      return var2;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}

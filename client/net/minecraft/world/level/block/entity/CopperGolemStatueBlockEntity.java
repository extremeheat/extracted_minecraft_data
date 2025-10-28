package net.minecraft.world.level.block.entity;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class CopperGolemStatueBlockEntity extends BlockEntity {
   public CopperGolemStatueBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.COPPER_GOLEM_STATUE, var1, var2);
   }

   public void createStatue(CopperGolem var1) {
      this.setComponents(DataComponentMap.builder().addAll(this.components()).set(DataComponents.CUSTOM_NAME, var1.getCustomName()).build());
      super.setChanged();
   }

   public @Nullable CopperGolem removeStatue(BlockState var1) {
      CopperGolem var2 = EntityType.COPPER_GOLEM.create(this.level, EntitySpawnReason.TRIGGERED);
      if (var2 != null) {
         var2.setCustomName((Component)this.components().get(DataComponents.CUSTOM_NAME));
         return this.initCopperGolem(var1, var2);
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

   public ItemStack getItem(ItemStack var1, CopperGolemStatueBlock.Pose var2) {
      var1.applyComponents(this.collectComponents());
      var1.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(CopperGolemStatueBlock.POSE, var2));
      return var1;
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}

package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import org.slf4j.Logger;

public class CopperGolemStatueBlockEntity extends BlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final List<String> SAVED_COPPER_GOLEM_TAGS = List.of("CustomName");

   public CopperGolemStatueBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.COPPER_GOLEM_STATUE, var1, var2);
   }

   public void createStatue(CopperGolem var1) {
      this.setComponents(DataComponentMap.builder().addAll(this.components()).set(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.COPPER_GOLEM, createCustomData(var1))).build());
      super.setChanged();
   }

   private static CompoundTag createCustomData(CopperGolem var0) {
      try (ProblemReporter.ScopedCollector var1 = new ProblemReporter.ScopedCollector(var0.problemPath(), LOGGER)) {
         TagValueOutput var2 = TagValueOutput.createWithContext(var1, var0.registryAccess());
         var0.save(var2);
         CompoundTag var3 = var2.buildResult();
         var3.keySet().removeIf((var0x) -> !SAVED_COPPER_GOLEM_TAGS.contains(var0x));
         return var3;
      }
   }

   @Nullable
   public CopperGolem removeStatue(BlockState var1) {
      TypedEntityData var2 = (TypedEntityData)this.components().get(DataComponents.ENTITY_DATA);
      CopperGolem var3 = EntityType.COPPER_GOLEM.create(this.level, EntitySpawnReason.TRIGGERED);
      if (var3 != null) {
         if (var2 != null) {
            var2.loadInto(var3);
         }

         return this.initCopperGolem(var1, var3, this.level);
      } else {
         return null;
      }
   }

   private CopperGolem initCopperGolem(BlockState var1, CopperGolem var2, Level var3) {
      BlockPos var4 = this.getBlockPos();
      var2.snapTo(var4.getCenter().x, (double)var4.getY(), var4.getCenter().z, ((Direction)var1.getValue(CopperGolemStatueBlock.FACING)).toYRot(), 0.0F);
      var2.yHeadRot = var2.getYRot();
      var2.yBodyRot = var2.getYRot();
      var2.playSpawnSound(var3);
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

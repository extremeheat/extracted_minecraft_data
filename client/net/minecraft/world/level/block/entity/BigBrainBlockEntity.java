package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.component.XpComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BigBrainBlockEntity extends BlockEntity {
   public static final String TAG_AMOUNT = "amount";
   private static final double ORB_TARGET_DISTANCE = 10.0;
   private static final int TICK_RATE = 5;
   private static final String TAG_TICK_DELAY = "delay";
   private int tickDelay = 5;
   private int xpAmount;

   public BigBrainBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.BIG_BRAIN, var1, var2);
   }

   @Override
   public void load(CompoundTag var1, HolderLookup.Provider var2) {
      this.xpAmount = var1.getInt("amount");
      this.tickDelay = var1.getInt("delay");
   }

   @Override
   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      var1.putInt("amount", this.xpAmount);
      var1.putInt("delay", this.tickDelay);
   }

   public int getXp() {
      return this.xpAmount;
   }

   public void setXp(int var1) {
      this.xpAmount = var1;
   }

   @Override
   public void collectComponents(DataComponentMap.Builder var1) {
      super.collectComponents(var1);
      var1.set(DataComponents.XP, new XpComponent(this.xpAmount));
   }

   @Override
   public void removeComponentsFromTag(CompoundTag var1) {
      var1.remove("amount");
   }

   public static void tick(Level var0, BlockPos var1, BlockState var2, BigBrainBlockEntity var3) {
      if (--var3.tickDelay <= 0) {
         var3.tickDelay = 5;
         AABB var4 = AABB.ofSize(Vec3.atCenterOf(var1), 10.0, 10.0, 10.0);

         for(ExperienceOrb var6 : var0.getEntities(EntityTypeTest.forClass(ExperienceOrb.class), var4, var0x -> true)) {
            var6.targetBlock(var1);
         }
      }
   }
}

package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.AABB;

public class ChestBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity, TickableBlockEntity {
   private NonNullList items;
   protected float openness;
   protected float oOpenness;
   protected int openCount;
   private int tickInterval;

   protected ChestBlockEntity(BlockEntityType var1) {
      super(var1);
      this.items = NonNullList.withSize(27, ItemStack.EMPTY);
   }

   public ChestBlockEntity() {
      this(BlockEntityType.CHEST);
   }

   public int getContainerSize() {
      return 27;
   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.chest", new Object[0]);
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(var1)) {
         ContainerHelper.loadAllItems(var1, this.items);
      }

   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      if (!this.trySaveLootTable(var1)) {
         ContainerHelper.saveAllItems(var1, this.items);
      }

      return var1;
   }

   public void tick() {
      int var1 = this.worldPosition.getX();
      int var2 = this.worldPosition.getY();
      int var3 = this.worldPosition.getZ();
      ++this.tickInterval;
      this.openCount = getOpenCount(this.level, this, this.tickInterval, var1, var2, var3, this.openCount);
      this.oOpenness = this.openness;
      float var4 = 0.1F;
      if (this.openCount > 0 && this.openness == 0.0F) {
         this.playSound(SoundEvents.CHEST_OPEN);
      }

      if (this.openCount == 0 && this.openness > 0.0F || this.openCount > 0 && this.openness < 1.0F) {
         float var5 = this.openness;
         if (this.openCount > 0) {
            this.openness += 0.1F;
         } else {
            this.openness -= 0.1F;
         }

         if (this.openness > 1.0F) {
            this.openness = 1.0F;
         }

         float var6 = 0.5F;
         if (this.openness < 0.5F && var5 >= 0.5F) {
            this.playSound(SoundEvents.CHEST_CLOSE);
         }

         if (this.openness < 0.0F) {
            this.openness = 0.0F;
         }
      }

   }

   public static int getOpenCount(Level var0, BaseContainerBlockEntity var1, int var2, int var3, int var4, int var5, int var6) {
      if (!var0.isClientSide && var6 != 0 && (var2 + var3 + var4 + var5) % 200 == 0) {
         var6 = getOpenCount(var0, var1, var3, var4, var5);
      }

      return var6;
   }

   public static int getOpenCount(Level var0, BaseContainerBlockEntity var1, int var2, int var3, int var4) {
      int var5 = 0;
      float var6 = 5.0F;
      List var7 = var0.getEntitiesOfClass(Player.class, new AABB((double)((float)var2 - 5.0F), (double)((float)var3 - 5.0F), (double)((float)var4 - 5.0F), (double)((float)(var2 + 1) + 5.0F), (double)((float)(var3 + 1) + 5.0F), (double)((float)(var4 + 1) + 5.0F)));
      Iterator var8 = var7.iterator();

      while(true) {
         Container var10;
         do {
            Player var9;
            do {
               if (!var8.hasNext()) {
                  return var5;
               }

               var9 = (Player)var8.next();
            } while(!(var9.containerMenu instanceof ChestMenu));

            var10 = ((ChestMenu)var9.containerMenu).getContainer();
         } while(var10 != var1 && (!(var10 instanceof CompoundContainer) || !((CompoundContainer)var10).contains(var1)));

         ++var5;
      }
   }

   private void playSound(SoundEvent var1) {
      ChestType var2 = (ChestType)this.getBlockState().getValue(ChestBlock.TYPE);
      if (var2 != ChestType.LEFT) {
         double var3 = (double)this.worldPosition.getX() + 0.5D;
         double var5 = (double)this.worldPosition.getY() + 0.5D;
         double var7 = (double)this.worldPosition.getZ() + 0.5D;
         if (var2 == ChestType.RIGHT) {
            Direction var9 = ChestBlock.getConnectedDirection(this.getBlockState());
            var3 += (double)var9.getStepX() * 0.5D;
            var7 += (double)var9.getStepZ() * 0.5D;
         }

         this.level.playSound((Player)null, var3, var5, var7, var1, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
      }
   }

   public boolean triggerEvent(int var1, int var2) {
      if (var1 == 1) {
         this.openCount = var2;
         return true;
      } else {
         return super.triggerEvent(var1, var2);
      }
   }

   public void startOpen(Player var1) {
      if (!var1.isSpectator()) {
         if (this.openCount < 0) {
            this.openCount = 0;
         }

         ++this.openCount;
         this.signalOpenCount();
      }

   }

   public void stopOpen(Player var1) {
      if (!var1.isSpectator()) {
         --this.openCount;
         this.signalOpenCount();
      }

   }

   protected void signalOpenCount() {
      Block var1 = this.getBlockState().getBlock();
      if (var1 instanceof ChestBlock) {
         this.level.blockEvent(this.worldPosition, var1, 1, this.openCount);
         this.level.updateNeighborsAt(this.worldPosition, var1);
      }

   }

   protected NonNullList getItems() {
      return this.items;
   }

   protected void setItems(NonNullList var1) {
      this.items = var1;
   }

   public float getOpenNess(float var1) {
      return Mth.lerp(var1, this.oOpenness, this.openness);
   }

   public static int getOpenCount(BlockGetter var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      if (var2.getBlock().isEntityBlock()) {
         BlockEntity var3 = var0.getBlockEntity(var1);
         if (var3 instanceof ChestBlockEntity) {
            return ((ChestBlockEntity)var3).openCount;
         }
      }

      return 0;
   }

   public static void swapContents(ChestBlockEntity var0, ChestBlockEntity var1) {
      NonNullList var2 = var0.getItems();
      var0.setItems(var1.getItems());
      var1.setItems(var2);
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return ChestMenu.threeRows(var1, var2, this);
   }
}

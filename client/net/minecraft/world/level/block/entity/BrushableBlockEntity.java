package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class BrushableBlockEntity extends BlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String LOOT_TABLE_TAG = "LootTable";
   private static final String LOOT_TABLE_SEED_TAG = "LootTableSeed";
   private static final String HIT_DIRECTION_TAG = "hit_direction";
   private static final String ITEM_TAG = "item";
   private static final int BRUSH_COOLDOWN_TICKS = 10;
   private static final int BRUSH_RESET_TICKS = 40;
   private static final int REQUIRED_BRUSHES_TO_BREAK = 10;
   private int brushCount;
   private long brushCountResetsAtTick;
   private long coolDownEndsAtTick;
   private ItemStack item;
   @Nullable
   private Direction hitDirection;
   @Nullable
   private ResourceKey<LootTable> lootTable;
   private long lootTableSeed;

   public BrushableBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.BRUSHABLE_BLOCK, var1, var2);
      this.item = ItemStack.EMPTY;
   }

   public boolean brush(long var1, Player var3, Direction var4) {
      if (this.hitDirection == null) {
         this.hitDirection = var4;
      }

      this.brushCountResetsAtTick = var1 + 40L;
      if (var1 >= this.coolDownEndsAtTick && this.level instanceof ServerLevel) {
         this.coolDownEndsAtTick = var1 + 10L;
         this.unpackLootTable(var3);
         int var5 = this.getCompletionState();
         if (++this.brushCount >= 10) {
            this.brushingCompleted(var3);
            return true;
         } else {
            this.level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 2);
            int var6 = this.getCompletionState();
            if (var5 != var6) {
               BlockState var7 = this.getBlockState();
               BlockState var8 = (BlockState)var7.setValue(BlockStateProperties.DUSTED, var6);
               this.level.setBlock(this.getBlockPos(), var8, 3);
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public void unpackLootTable(Player var1) {
      if (this.lootTable != null && this.level != null && !this.level.isClientSide() && this.level.getServer() != null) {
         LootTable var2 = this.level.getServer().reloadableRegistries().getLootTable(this.lootTable);
         if (var1 instanceof ServerPlayer) {
            ServerPlayer var3 = (ServerPlayer)var1;
            CriteriaTriggers.GENERATE_LOOT.trigger(var3, this.lootTable);
         }

         LootParams var5 = (new LootParams.Builder((ServerLevel)this.level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition)).withLuck(var1.getLuck()).withParameter(LootContextParams.THIS_ENTITY, var1).create(LootContextParamSets.CHEST);
         ObjectArrayList var4 = var2.getRandomItems(var5, this.lootTableSeed);
         ItemStack var10001;
         switch (var4.size()) {
            case 0:
               var10001 = ItemStack.EMPTY;
               break;
            case 1:
               var10001 = (ItemStack)var4.get(0);
               break;
            default:
               LOGGER.warn("Expected max 1 loot from loot table {}, but got {}", this.lootTable.location(), var4.size());
               var10001 = (ItemStack)var4.get(0);
         }

         this.item = var10001;
         this.lootTable = null;
         this.setChanged();
      }
   }

   private void brushingCompleted(Player var1) {
      if (this.level != null && this.level.getServer() != null) {
         this.dropContent(var1);
         BlockState var2 = this.getBlockState();
         this.level.levelEvent(3008, this.getBlockPos(), Block.getId(var2));
         Block var3 = this.getBlockState().getBlock();
         Block var4;
         if (var3 instanceof BrushableBlock) {
            BrushableBlock var5 = (BrushableBlock)var3;
            var4 = var5.getTurnsInto();
         } else {
            var4 = Blocks.AIR;
         }

         this.level.setBlock(this.worldPosition, var4.defaultBlockState(), 3);
      }
   }

   private void dropContent(Player var1) {
      if (this.level != null && this.level.getServer() != null) {
         this.unpackLootTable(var1);
         if (!this.item.isEmpty()) {
            double var2 = (double)EntityType.ITEM.getWidth();
            double var4 = 1.0 - var2;
            double var6 = var2 / 2.0;
            Direction var8 = (Direction)Objects.requireNonNullElse(this.hitDirection, Direction.UP);
            BlockPos var9 = this.worldPosition.relative((Direction)var8, 1);
            double var10 = (double)var9.getX() + 0.5 * var4 + var6;
            double var12 = (double)var9.getY() + 0.5 + (double)(EntityType.ITEM.getHeight() / 2.0F);
            double var14 = (double)var9.getZ() + 0.5 * var4 + var6;
            ItemEntity var16 = new ItemEntity(this.level, var10, var12, var14, this.item.split(this.level.random.nextInt(21) + 10));
            var16.setDeltaMovement(Vec3.ZERO);
            this.level.addFreshEntity(var16);
            this.item = ItemStack.EMPTY;
         }

      }
   }

   public void checkReset() {
      if (this.level != null) {
         if (this.brushCount != 0 && this.level.getGameTime() >= this.brushCountResetsAtTick) {
            int var1 = this.getCompletionState();
            this.brushCount = Math.max(0, this.brushCount - 2);
            int var2 = this.getCompletionState();
            if (var1 != var2) {
               this.level.setBlock(this.getBlockPos(), (BlockState)this.getBlockState().setValue(BlockStateProperties.DUSTED, var2), 3);
            }

            boolean var3 = true;
            this.brushCountResetsAtTick = this.level.getGameTime() + 4L;
         }

         if (this.brushCount == 0) {
            this.hitDirection = null;
            this.brushCountResetsAtTick = 0L;
            this.coolDownEndsAtTick = 0L;
         } else {
            this.level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 2);
         }

      }
   }

   private boolean tryLoadLootTable(CompoundTag var1) {
      if (var1.contains("LootTable", 8)) {
         this.lootTable = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(var1.getString("LootTable")));
         this.lootTableSeed = var1.getLong("LootTableSeed");
         return true;
      } else {
         return false;
      }
   }

   private boolean trySaveLootTable(CompoundTag var1) {
      if (this.lootTable == null) {
         return false;
      } else {
         var1.putString("LootTable", this.lootTable.location().toString());
         if (this.lootTableSeed != 0L) {
            var1.putLong("LootTableSeed", this.lootTableSeed);
         }

         return true;
      }
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      CompoundTag var2 = super.getUpdateTag(var1);
      if (this.hitDirection != null) {
         var2.putInt("hit_direction", this.hitDirection.ordinal());
      }

      if (!this.item.isEmpty()) {
         var2.put("item", this.item.save(var1));
      }

      return var2;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      if (!this.tryLoadLootTable(var1) && var1.contains("item")) {
         this.item = (ItemStack)ItemStack.parse(var2, var1.getCompound("item")).orElse(ItemStack.EMPTY);
      } else {
         this.item = ItemStack.EMPTY;
      }

      if (var1.contains("hit_direction")) {
         this.hitDirection = Direction.values()[var1.getInt("hit_direction")];
      }

   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      if (!this.trySaveLootTable(var1) && !this.item.isEmpty()) {
         var1.put("item", this.item.save(var2));
      }

   }

   public void setLootTable(ResourceKey<LootTable> var1, long var2) {
      this.lootTable = var1;
      this.lootTableSeed = var2;
   }

   private int getCompletionState() {
      if (this.brushCount == 0) {
         return 0;
      } else if (this.brushCount < 3) {
         return 1;
      } else {
         return this.brushCount < 6 ? 2 : 3;
      }
   }

   @Nullable
   public Direction getHitDirection() {
      return this.hitDirection;
   }

   public ItemStack getItem() {
      return this.item;
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}

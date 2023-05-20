package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class SuspiciousSandBlockEntity extends BlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String LOOT_TABLE_TAG = "loot_table";
   private static final String LOOT_TABLE_SEED_TAG = "loot_table_seed";
   private static final String HIT_DIRECTION_TAG = "hit_direction";
   private static final String ITEM_TAG = "item";
   private static final int BRUSH_COOLDOWN_TICKS = 10;
   private static final int BRUSH_RESET_TICKS = 40;
   private static final int REQUIRED_BRUSHES_TO_BREAK = 10;
   private int brushCount;
   private long brushCountResetsAtTick;
   private long coolDownEndsAtTick;
   private ItemStack item = ItemStack.EMPTY;
   @Nullable
   private Direction hitDirection;
   @Nullable
   private ResourceLocation lootTable;
   private long lootTableSeed;

   public SuspiciousSandBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SUSPICIOUS_SAND, var1, var2);
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
            this.level.scheduleTick(this.getBlockPos(), Blocks.SUSPICIOUS_SAND, 40);
            int var6 = this.getCompletionState();
            if (var5 != var6) {
               BlockState var7 = this.getBlockState();
               BlockState var8 = var7.setValue(BlockStateProperties.DUSTED, Integer.valueOf(var6));
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
         LootTable var2 = this.level.getServer().getLootTables().get(this.lootTable);
         if (var1 instanceof ServerPlayer var3) {
            CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)var3, this.lootTable);
         }

         LootContext.Builder var5 = new LootContext.Builder((ServerLevel)this.level)
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition))
            .withOptionalRandomSeed(this.lootTableSeed)
            .withLuck(var1.getLuck())
            .withParameter(LootContextParams.THIS_ENTITY, var1);
         ObjectArrayList var4 = var2.getRandomItems(var5.create(LootContextParamSets.CHEST));

         this.item = switch(var4.size()) {
            case 0 -> ItemStack.EMPTY;
            case 1 -> (ItemStack)var4.get(0);
            default -> {
               LOGGER.warn("Expected max 1 loot from loot table " + this.lootTable + " got " + var4.size());
               yield (ItemStack)var4.get(0);
            }
         };
         this.lootTable = null;
         this.setChanged();
      }
   }

   private void brushingCompleted(Player var1) {
      if (this.level != null && this.level.getServer() != null) {
         this.dropContent(var1);
         this.level.levelEvent(3008, this.getBlockPos(), Block.getId(this.getBlockState()));
         this.level.setBlock(this.worldPosition, Blocks.SAND.defaultBlockState(), 3);
      }
   }

   private void dropContent(Player var1) {
      if (this.level != null && this.level.getServer() != null) {
         this.unpackLootTable(var1);
         if (!this.item.isEmpty()) {
            double var2 = (double)EntityType.ITEM.getWidth();
            double var4 = 1.0 - var2;
            double var6 = var2 / 2.0;
            Direction var8 = Objects.requireNonNullElse(this.hitDirection, Direction.UP);
            BlockPos var9 = this.worldPosition.relative(var8, 1);
            double var10 = Math.floor((double)var9.getX()) + 0.5 * var4 + var6;
            double var12 = Math.floor((double)var9.getY() + 0.5) + (double)(EntityType.ITEM.getHeight() / 2.0F);
            double var14 = Math.floor((double)var9.getZ()) + 0.5 * var4 + var6;
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
               this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(BlockStateProperties.DUSTED, Integer.valueOf(var2)), 3);
            }

            boolean var3 = true;
            this.brushCountResetsAtTick = this.level.getGameTime() + 4L;
         }

         if (this.brushCount == 0) {
            this.hitDirection = null;
            this.brushCountResetsAtTick = 0L;
            this.coolDownEndsAtTick = 0L;
         } else {
            this.level.scheduleTick(this.getBlockPos(), Blocks.SUSPICIOUS_SAND, (int)(this.brushCountResetsAtTick - this.level.getGameTime()));
         }
      }
   }

   private boolean tryLoadLootTable(CompoundTag var1) {
      if (var1.contains("loot_table", 8)) {
         this.lootTable = new ResourceLocation(var1.getString("loot_table"));
         this.lootTableSeed = var1.getLong("loot_table_seed");
         return true;
      } else {
         return false;
      }
   }

   private boolean trySaveLootTable(CompoundTag var1) {
      if (this.lootTable == null) {
         return false;
      } else {
         var1.putString("loot_table", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            var1.putLong("loot_table_seed", this.lootTableSeed);
         }

         return true;
      }
   }

   @Override
   public CompoundTag getUpdateTag() {
      CompoundTag var1 = super.getUpdateTag();
      if (this.hitDirection != null) {
         var1.putInt("hit_direction", this.hitDirection.ordinal());
      }

      var1.put("item", this.item.save(new CompoundTag()));
      return var1;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public void load(CompoundTag var1) {
      if (!this.tryLoadLootTable(var1) && var1.contains("item")) {
         this.item = ItemStack.of(var1.getCompound("item"));
      }

      if (var1.contains("hit_direction")) {
         this.hitDirection = Direction.values()[var1.getInt("hit_direction")];
      }
   }

   @Override
   protected void saveAdditional(CompoundTag var1) {
      if (!this.trySaveLootTable(var1)) {
         var1.put("item", this.item.save(new CompoundTag()));
      }
   }

   public void setLootTable(ResourceLocation var1, long var2) {
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
}

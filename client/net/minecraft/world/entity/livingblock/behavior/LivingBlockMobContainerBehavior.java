package net.minecraft.world.entity.livingblock.behavior;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BehaviorSimpleContainer;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.LivingBlockGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class LivingBlockMobContainerBehavior implements SimpleContainerBehavior {
   public static final String DATA_TAG = "container_behavior_data";
   private static final int BACK_AWAY_COOLDOWN_TICKS = 15;
   public static final MutableComponent DISPLAY_NAME = Component.translatable("container.shulkerBox");
   private BehaviorSimpleContainer inventory;
   private static final int REEVALUATION_TICKS = 10;
   private SoundEvent eatSound;
   private SoundEvent burpSound;
   private boolean triggeredAttackResponse;
   private boolean inventoryInitialized = false;
   private int lastTriggeredTick;

   public LivingBlockMobContainerBehavior(final int inventorySize, final SoundEvent eatSound, final SoundEvent burpSound) {
      super();
      this.inventory = new BehaviorSimpleContainer(inventorySize);
      this.eatSound = eatSound;
      this.burpSound = burpSound;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return this.lastTriggeredTick + 10 < entity.tickCount || entity.getAttackedBy() != null;
   }

   private ItemStack eatItem(final ItemStack itemStack, final LivingBlock livingBlock) {
      ItemStack result = this.inventory.addItem(itemStack);
      this.saveItems(livingBlock);
      return result;
   }

   public boolean isEmptyContainer() {
      return this.inventory.isEmpty();
   }

   public void onStop(final LivingBlock entity) {
      if (this.triggeredAttackResponse && entity.getAttackedBy() != null) {
         entity.setAttackedBy((Player)null);
      }

   }

   public void onStart(final LivingBlock entity) {
      this.triggeredAttackResponse = false;
      if (!this.inventoryInitialized) {
         this.inventory.fromItemList(new NonNullList(((ItemContainerContents)entity.getItemStack().getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)).nonEmptyItemCopyStream().toList(), ItemStack.EMPTY));
         this.inventoryInitialized = true;
      }

   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      List<Mob> mobs = level.getEntities(EntityTypeTest.forClass(Mob.class), entity.getBoundingBox().inflate(1.0), (e) -> true);
      List<LivingBlock> entities = level.getEntities(EntityTypeTest.forClass(LivingBlock.class), entity.getBoundingBox().inflate(1.0), (e) -> e.getGroup() == LivingBlockGroup.NONE && !e.isSelected());
      this.lastTriggeredTick = tickCount;
      RandomSource random = level.getRandom();
      if (entity.getAttackedBy() != null) {
         Vec3 targetPos = getTargetPosition(entity.position(), entity.getAttackedBy().position());
         if (!this.triggeredAttackResponse) {
            if (this.dropLastItem(level, new BlockPos((int)targetPos.x, (int)targetPos.y, (int)targetPos.z))) {
               Vec3 dir = targetPos.subtract(entity.position());
               level.levelEvent(2010, entity.getOnPos().above(), Direction.getApproximateNearest(dir).get3DDataValue());
               level.playSound(entity, entity.getOnPos(), this.burpSound, SoundSource.BLOCKS, 1.0F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F);
               entity.setHealth(entity.previousHealthAmount);
               this.saveItems(entity);
            }

            entity.lastAttackedTick = tickCount;
            this.triggeredAttackResponse = true;
         }

         return entity.lastAttackedTick + 15 >= tickCount;
      } else {
         if (!mobs.isEmpty()) {
            for(Mob mob : mobs) {
               ItemStack stack = mob.getPickResult();
               if (stack != null) {
                  ItemStack result = this.eatItem(stack, entity);
                  if (result.isEmpty()) {
                     level.playSound(entity, entity.getOnPos(), this.eatSound, SoundSource.BLOCKS, 1.0F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F);
                     mob.discard();
                  }
               }
            }
         }

         if (!entities.isEmpty()) {
            for(LivingBlock livingBlock : entities) {
               if (livingBlock != entity && !livingBlock.getItemStack().is(Items.SHULKER_BOX) && livingBlock.tickCount >= 10 && !livingBlock.isNonEmptyChest()) {
                  ItemStack stack = livingBlock.getItemStack();
                  ItemStack result = this.eatItem(stack, entity);
                  if (result.isEmpty()) {
                     level.playSound(entity, entity.getOnPos(), this.eatSound, SoundSource.BLOCKS, 1.0F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F);
                     livingBlock.discard();
                  } else if (result.getCount() < stack.getCount()) {
                     level.playSound(entity, entity.getOnPos(), this.eatSound, SoundSource.BLOCKS, 1.0F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F);
                     livingBlock.setItemStack(result);
                  }
               }
            }
         }

         return false;
      }
   }

   private static Vec3 getTargetPosition(final Vec3 entityPos, final Vec3 playerPos) {
      double dx = entityPos.x() - playerPos.x();
      double dz = entityPos.z() - playerPos.z();
      double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
      if (horizontalDistance < 0.01) {
         return new Vec3(entityPos.x() + 5.0, entityPos.y(), entityPos.z());
      } else {
         double scale = 3.0 / horizontalDistance;
         return new Vec3(entityPos.x() + dx * scale, entityPos.y(), entityPos.z() + dz * scale);
      }
   }

   private boolean dropLastItem(final ServerLevel level, final BlockPos pos) {
      if (this.isEmptyContainer()) {
         return false;
      } else {
         for(int i = this.inventory.getContainerSize() - 1; i >= 0; --i) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()) {
               EntityType<?> type = SpawnEggItem.getType(stack);
               if (type != null) {
                  int count = stack.getCount();

                  for(int j = 0; j < count; ++j) {
                     SpawnEggItem.spawnMob((LivingEntity)null, stack, level, pos, true, false);
                  }
               }

               LivingBlock.createStack(level, pos, (Entity)null, stack);
               this.inventory.setItem(i, ItemStack.EMPTY);
               return true;
            }
         }

         return false;
      }
   }

   public void save(final ValueOutput output, final LivingBlock livingBlock) {
      this.inventory.storeAsItemWithSlotList(output.list("Items", ItemStackWithSlot.CODEC));
      this.saveItems(livingBlock);
   }

   public void loadData(final ValueInput input) {
      input.list("Items", ItemStackWithSlot.CODEC).ifPresent((list) -> this.inventory.fromItemWithSlotList(list));
   }

   public static LivingBlockBehaviorType mobContainerBlockEntity(final int containerSize, final SoundEvent eatSound, final SoundEvent burpSound) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new LivingBlockMobContainerBehavior(containerSize, eatSound, burpSound)));
   }

   public String getDataTag() {
      return "container_behavior_data";
   }

   public void onDeath(final LivingBlock entity, final ServerLevel level) {
      this.inventory.clearContent();
   }

   public void onRemoval(final LivingBlock entity, final ServerLevel level) {
      this.inventory.setValid(false);
   }

   private void saveItems(final LivingBlock livingBlock) {
      livingBlock.getItemStack().set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.inventory.getItems()));
   }

   public SimpleContainer getContainer() {
      return this.inventory;
   }

   public Component displayName(final LivingBlock entity) {
      return DISPLAY_NAME;
   }
}

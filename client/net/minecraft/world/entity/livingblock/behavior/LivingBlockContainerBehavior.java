package net.minecraft.world.entity.livingblock.behavior;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BehaviorSimpleContainer;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.LivingBlockGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class LivingBlockContainerBehavior implements SimpleContainerBehavior {
   private static final int BACK_AWAY_COOLDOWN_TICKS = 15;
   private BehaviorSimpleContainer inventory;
   private static final int REEVALUATION_TICKS = 10;
   private SoundEvent eatSound;
   private SoundEvent burpSound;
   private final boolean onlySame;
   private boolean triggeredAttackResponse;
   private int lastTriggeredTick;
   private boolean inventoryInitialized = false;

   public LivingBlockContainerBehavior(final int inventorySize, final SoundEvent eatSound, final SoundEvent burpSound, final boolean onlySame) {
      super();
      this.inventory = new BehaviorSimpleContainer(inventorySize);
      this.eatSound = eatSound;
      this.burpSound = burpSound;
      this.onlySame = onlySame;
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
      List<LivingBlock> entities;
      if (this.onlySame && !this.inventory.isEmpty()) {
         Optional<ItemStack> first = this.inventory.getItems().stream().filter((item) -> !item.isEmpty()).findFirst();
         if (first.isPresent()) {
            ItemStack itemStack = (ItemStack)first.get();
            entities = level.getEntities(EntityType.LIVING_BLOCK, entity.getBoundingBox().inflate(1.0), (block) -> block.getGroup() == LivingBlockGroup.NONE && !block.isSelected() && itemStack.is(block.getItemStack().getItem()));
         } else {
            entities = level.getEntities(EntityType.LIVING_BLOCK, entity.getBoundingBox().inflate(1.0), (block) -> block.getGroup() == LivingBlockGroup.NONE && !block.isSelected());
         }
      } else {
         entities = level.getEntities(EntityType.LIVING_BLOCK, entity.getBoundingBox().inflate(1.0), (block) -> block.getGroup() == LivingBlockGroup.NONE && !block.isSelected());
      }

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
         if (!entities.isEmpty()) {
            for(LivingBlock livingBlock : entities) {
               if (livingBlock != entity && livingBlock.tickCount >= 40 && !livingBlock.isNonEmptyChest()) {
                  Optional<SimpleContainerBehavior> simpleContainer = livingBlock.getBehaviorOfType(LivingBlockMobContainerBehavior.class).flatMap((e) -> {
                     LivingBlockBehavior instance = e.instance;
                     if (instance instanceof SimpleContainerBehavior simpleContainerBehavior) {
                        return Optional.of(simpleContainerBehavior);
                     } else {
                        return Optional.empty();
                     }
                  });
                  ItemStack stack = livingBlock.getItemStack();
                  simpleContainer.ifPresent((simpleContainerBehavior) -> stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(simpleContainerBehavior.getContainer().getItems())));
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

   private void saveItems(final LivingBlock livingBlock) {
      livingBlock.getItemStack().set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.inventory.getItems()));
   }

   public void loadData(final ValueInput input) {
      input.list("Items", ItemStackWithSlot.CODEC).ifPresent((list) -> this.inventory.fromItemWithSlotList(list));
   }

   public static LivingBlockBehaviorType containerBlockEntity(final int containerSize, final SoundEvent eatSound, final SoundEvent burpSound) {
      return containerBlockEntity(containerSize, eatSound, burpSound, false);
   }

   public static LivingBlockBehaviorType containerBlockEntity(final int containerSize, final SoundEvent eatSound, final SoundEvent burpSound, final boolean onlySame) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new LivingBlockContainerBehavior(containerSize, eatSound, burpSound, onlySame)));
   }

   public String getDataTag() {
      return "container_behavior_data";
   }

   public void onDeath(final LivingBlock entity, final ServerLevel level) {
      while(this.dropLastItem(level, entity.getOnPos().above())) {
      }

      this.inventory.clearContent();
      this.saveItems(entity);
   }

   public void onRemoval(final LivingBlock entity, final ServerLevel level) {
      this.inventory.setValid(false);
   }

   public SimpleContainer getContainer() {
      return this.inventory;
   }

   public Component displayName(final LivingBlock entity) {
      ItemStack itemStack = entity.getItemStack();
      if (itemStack.is(ItemTags.COPPER_CHESTS)) {
         return Component.translatable("container.chest");
      } else {
         return itemStack.is(Items.ENDER_CHEST) ? Component.translatable("container.enderchest") : Component.translatable("container." + itemStack.getItem().builtInRegistryHolder().key().identifier().getPath());
      }
   }
}

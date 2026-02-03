package net.minecraft.world.inventory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IdMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantingTableBlock;

public class EnchantmentMenu extends AbstractContainerMenu {
   private static final Identifier EMPTY_SLOT_LAPIS_LAZULI = Identifier.withDefaultNamespace("container/slot/lapis_lazuli");
   private final Container enchantSlots;
   private final ContainerLevelAccess access;
   private final RandomSource random;
   private final DataSlot enchantmentSeed;
   public final int[] costs;
   public final int[] enchantClue;
   public final int[] levelClue;

   public EnchantmentMenu(final int containerId, final Inventory inventory) {
      this(containerId, inventory, ContainerLevelAccess.NULL);
   }

   public EnchantmentMenu(final int containerId, final Inventory inventory, final ContainerLevelAccess access) {
      super(MenuType.ENCHANTMENT, containerId);
      this.enchantSlots = new SimpleContainer(2) {
         {
            Objects.requireNonNull(EnchantmentMenu.this);
         }

         public void setChanged() {
            super.setChanged();
            EnchantmentMenu.this.slotsChanged(this);
         }
      };
      this.random = RandomSource.create();
      this.enchantmentSeed = DataSlot.standalone();
      this.costs = new int[3];
      this.enchantClue = new int[]{-1, -1, -1};
      this.levelClue = new int[]{-1, -1, -1};
      this.access = access;
      this.addSlot(new Slot(this.enchantSlots, 0, 15, 47) {
         {
            Objects.requireNonNull(EnchantmentMenu.this);
         }

         public int getMaxStackSize() {
            return 1;
         }
      });
      this.addSlot(new Slot(this.enchantSlots, 1, 35, 47) {
         {
            Objects.requireNonNull(EnchantmentMenu.this);
         }

         public boolean mayPlace(final ItemStack itemStack) {
            return itemStack.is(Items.LAPIS_LAZULI);
         }

         public Identifier getNoItemIcon() {
            return EnchantmentMenu.EMPTY_SLOT_LAPIS_LAZULI;
         }
      });
      this.addStandardInventorySlots(inventory, 8, 84);
      this.addDataSlot(DataSlot.shared(this.costs, 0));
      this.addDataSlot(DataSlot.shared(this.costs, 1));
      this.addDataSlot(DataSlot.shared(this.costs, 2));
      this.addDataSlot(this.enchantmentSeed).set(inventory.player.getEnchantmentSeed());
      this.addDataSlot(DataSlot.shared(this.enchantClue, 0));
      this.addDataSlot(DataSlot.shared(this.enchantClue, 1));
      this.addDataSlot(DataSlot.shared(this.enchantClue, 2));
      this.addDataSlot(DataSlot.shared(this.levelClue, 0));
      this.addDataSlot(DataSlot.shared(this.levelClue, 1));
      this.addDataSlot(DataSlot.shared(this.levelClue, 2));
   }

   public void slotsChanged(final Container container) {
      if (container == this.enchantSlots) {
         ItemStack itemStack = container.getItem(0);
         if (!itemStack.isEmpty() && itemStack.isEnchantable()) {
            this.access.execute((level, pos) -> {
               IdMap<Holder<Enchantment>> holders = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).asHolderIdMap();
               int bookcases = 0;

               for(BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
                  if (EnchantingTableBlock.isValidBookShelf(level, pos, offset)) {
                     ++bookcases;
                  }
               }

               this.random.setSeed((long)this.enchantmentSeed.get());

               for(int i = 0; i < 3; ++i) {
                  this.costs[i] = EnchantmentHelper.getEnchantmentCost(this.random, i, bookcases, itemStack);
                  this.enchantClue[i] = -1;
                  this.levelClue[i] = -1;
                  if (this.costs[i] < i + 1) {
                     this.costs[i] = 0;
                  }
               }

               for(int i = 0; i < 3; ++i) {
                  if (this.costs[i] > 0) {
                     List<EnchantmentInstance> list = this.getEnchantmentList(level.registryAccess(), itemStack, i, this.costs[i]);
                     if (!list.isEmpty()) {
                        EnchantmentInstance ench = (EnchantmentInstance)list.get(this.random.nextInt(list.size()));
                        this.enchantClue[i] = holders.getId(ench.enchantment());
                        this.levelClue[i] = ench.level();
                     }
                  }
               }

               this.broadcastChanges();
            });
         } else {
            for(int i = 0; i < 3; ++i) {
               this.costs[i] = 0;
               this.enchantClue[i] = -1;
               this.levelClue[i] = -1;
            }
         }
      }

   }

   public boolean clickMenuButton(final Player player, final int buttonId) {
      if (buttonId >= 0 && buttonId < this.costs.length) {
         ItemStack itemStack = this.enchantSlots.getItem(0);
         ItemStack currency = this.enchantSlots.getItem(1);
         int enchantmentCost = buttonId + 1;
         if ((currency.isEmpty() || currency.getCount() < enchantmentCost) && !player.hasInfiniteMaterials()) {
            return false;
         } else if (this.costs[buttonId] <= 0 || itemStack.isEmpty() || (player.experienceLevel < enchantmentCost || player.experienceLevel < this.costs[buttonId]) && !player.hasInfiniteMaterials()) {
            return false;
         } else {
            this.access.execute((level, pos) -> {
               ItemStack enchantmentItem = itemStack;
               List<EnchantmentInstance> newEnchantment = this.getEnchantmentList(level.registryAccess(), itemStack, buttonId, this.costs[buttonId]);
               if (!newEnchantment.isEmpty()) {
                  player.onEnchantmentPerformed(itemStack, enchantmentCost);
                  if (itemStack.is(Items.BOOK)) {
                     enchantmentItem = itemStack.transmuteCopy(Items.ENCHANTED_BOOK);
                     this.enchantSlots.setItem(0, enchantmentItem);
                  }

                  for(EnchantmentInstance enchantment : newEnchantment) {
                     enchantmentItem.enchant(enchantment.enchantment(), enchantment.level());
                  }

                  currency.consume(enchantmentCost, player);
                  if (currency.isEmpty()) {
                     this.enchantSlots.setItem(1, ItemStack.EMPTY);
                  }

                  player.awardStat(Stats.ENCHANT_ITEM);
                  if (player instanceof ServerPlayer) {
                     CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)player, enchantmentItem, enchantmentCost);
                  }

                  this.enchantSlots.setChanged();
                  this.enchantmentSeed.set(player.getEnchantmentSeed());
                  this.slotsChanged(this.enchantSlots);
                  level.playSound((Entity)null, (BlockPos)pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
               }

            });
            return true;
         }
      } else {
         String var10000 = player.getPlainTextName();
         Util.logAndPauseIfInIde(var10000 + " pressed invalid button id: " + buttonId);
         return false;
      }
   }

   private List<EnchantmentInstance> getEnchantmentList(final RegistryAccess access, final ItemStack itemStack, final int slot, final int enchantmentCost) {
      this.random.setSeed((long)(this.enchantmentSeed.get() + slot));
      Optional<HolderSet.Named<Enchantment>> tag = access.lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.IN_ENCHANTING_TABLE);
      if (tag.isEmpty()) {
         return List.of();
      } else {
         List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(this.random, itemStack, enchantmentCost, ((HolderSet.Named)tag.get()).stream());
         if (itemStack.is(Items.BOOK) && list.size() > 1) {
            list.remove(this.random.nextInt(list.size()));
         }

         return list;
      }
   }

   public int getGoldCount() {
      ItemStack goldStack = this.enchantSlots.getItem(1);
      return goldStack.isEmpty() ? 0 : goldStack.getCount();
   }

   public int getEnchantmentSeed() {
      return this.enchantmentSeed.get();
   }

   public void removed(final Player player) {
      super.removed(player);
      this.access.execute((level, pos) -> this.clearContainer(player, this.enchantSlots));
   }

   public boolean stillValid(final Player player) {
      return stillValid(this.access, player, Blocks.ENCHANTING_TABLE);
   }

   public ItemStack quickMoveStack(final Player player, final int slotIndex) {
      ItemStack clicked = ItemStack.EMPTY;
      Slot slot = this.slots.get(slotIndex);
      if (slot != null && slot.hasItem()) {
         ItemStack stack = slot.getItem();
         clicked = stack.copy();
         if (slotIndex == 0) {
            if (!this.moveItemStackTo(stack, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (slotIndex == 1) {
            if (!this.moveItemStackTo(stack, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (stack.is(Items.LAPIS_LAZULI)) {
            if (!this.moveItemStackTo(stack, 1, 2, true)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (((Slot)this.slots.get(0)).hasItem() || !((Slot)this.slots.get(0)).mayPlace(stack)) {
               return ItemStack.EMPTY;
            }

            ItemStack singleItem = stack.copyWithCount(1);
            stack.shrink(1);
            ((Slot)this.slots.get(0)).setByPlayer(singleItem);
         }

         if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (stack.getCount() == clicked.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, stack);
      }

      return clicked;
   }
}

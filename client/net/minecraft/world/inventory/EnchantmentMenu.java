package net.minecraft.world.inventory;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IdMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantingTableBlock;

public class EnchantmentMenu extends AbstractContainerMenu {
   static final ResourceLocation EMPTY_SLOT_LAPIS_LAZULI = ResourceLocation.withDefaultNamespace("item/empty_slot_lapis_lazuli");
   private final Container enchantSlots = new SimpleContainer(2) {
      @Override
      public void setChanged() {
         super.setChanged();
         EnchantmentMenu.this.slotsChanged(this);
      }
   };
   private final ContainerLevelAccess access;
   private final RandomSource random = RandomSource.create();
   private final DataSlot enchantmentSeed = DataSlot.standalone();
   public final int[] costs = new int[3];
   public final int[] enchantClue = new int[]{-1, -1, -1};
   public final int[] levelClue = new int[]{-1, -1, -1};

   public EnchantmentMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public EnchantmentMenu(int var1, Inventory var2, ContainerLevelAccess var3) {
      super(MenuType.ENCHANTMENT, var1);
      this.access = var3;
      this.addSlot(new Slot(this.enchantSlots, 0, 15, 47) {
         @Override
         public int getMaxStackSize() {
            return 1;
         }
      });
      this.addSlot(new Slot(this.enchantSlots, 1, 35, 47) {
         @Override
         public boolean mayPlace(ItemStack var1) {
            return var1.is(Items.LAPIS_LAZULI);
         }

         @Override
         public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(InventoryMenu.BLOCK_ATLAS, EnchantmentMenu.EMPTY_SLOT_LAPIS_LAZULI);
         }
      });
      this.addStandardInventorySlots(var2, 8, 84);
      this.addDataSlot(DataSlot.shared(this.costs, 0));
      this.addDataSlot(DataSlot.shared(this.costs, 1));
      this.addDataSlot(DataSlot.shared(this.costs, 2));
      this.addDataSlot(this.enchantmentSeed).set(var2.player.getEnchantmentSeed());
      this.addDataSlot(DataSlot.shared(this.enchantClue, 0));
      this.addDataSlot(DataSlot.shared(this.enchantClue, 1));
      this.addDataSlot(DataSlot.shared(this.enchantClue, 2));
      this.addDataSlot(DataSlot.shared(this.levelClue, 0));
      this.addDataSlot(DataSlot.shared(this.levelClue, 1));
      this.addDataSlot(DataSlot.shared(this.levelClue, 2));
   }

   @Override
   public void slotsChanged(Container var1) {
      if (var1 == this.enchantSlots) {
         ItemStack var2 = var1.getItem(0);
         if (!var2.isEmpty() && var2.isEnchantable()) {
            this.access.execute((var2x, var3x) -> {
               IdMap var4 = var2x.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).asHolderIdMap();
               int var5 = 0;

               for (BlockPos var7 : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
                  if (EnchantingTableBlock.isValidBookShelf(var2x, var3x, var7)) {
                     var5++;
                  }
               }

               this.random.setSeed((long)this.enchantmentSeed.get());

               for (int var9 = 0; var9 < 3; var9++) {
                  this.costs[var9] = EnchantmentHelper.getEnchantmentCost(this.random, var9, var5, var2);
                  this.enchantClue[var9] = -1;
                  this.levelClue[var9] = -1;
                  if (this.costs[var9] < var9 + 1) {
                     this.costs[var9] = 0;
                  }
               }

               for (int var10 = 0; var10 < 3; var10++) {
                  if (this.costs[var10] > 0) {
                     List var11 = this.getEnchantmentList(var2x.registryAccess(), var2, var10, this.costs[var10]);
                     if (var11 != null && !var11.isEmpty()) {
                        EnchantmentInstance var8 = (EnchantmentInstance)var11.get(this.random.nextInt(var11.size()));
                        this.enchantClue[var10] = var4.getId(var8.enchantment);
                        this.levelClue[var10] = var8.level;
                     }
                  }
               }

               this.broadcastChanges();
            });
         } else {
            for (int var3 = 0; var3 < 3; var3++) {
               this.costs[var3] = 0;
               this.enchantClue[var3] = -1;
               this.levelClue[var3] = -1;
            }
         }
      }
   }

   @Override
   public boolean clickMenuButton(Player var1, int var2) {
      if (var2 >= 0 && var2 < this.costs.length) {
         ItemStack var3 = this.enchantSlots.getItem(0);
         ItemStack var4 = this.enchantSlots.getItem(1);
         int var5 = var2 + 1;
         if ((var4.isEmpty() || var4.getCount() < var5) && !var1.hasInfiniteMaterials()) {
            return false;
         } else if (this.costs[var2] <= 0
            || var3.isEmpty()
            || (var1.experienceLevel < var5 || var1.experienceLevel < this.costs[var2]) && !var1.getAbilities().instabuild) {
            return false;
         } else {
            this.access.execute((var6, var7) -> {
               ItemStack var8 = var3;
               List var9 = this.getEnchantmentList(var6.registryAccess(), var3, var2, this.costs[var2]);
               if (!var9.isEmpty()) {
                  var1.onEnchantmentPerformed(var3, var5);
                  if (var3.is(Items.BOOK)) {
                     var8 = var3.transmuteCopy(Items.ENCHANTED_BOOK);
                     this.enchantSlots.setItem(0, var8);
                  }

                  for (EnchantmentInstance var11 : var9) {
                     var8.enchant(var11.enchantment, var11.level);
                  }

                  var4.consume(var5, var1);
                  if (var4.isEmpty()) {
                     this.enchantSlots.setItem(1, ItemStack.EMPTY);
                  }

                  var1.awardStat(Stats.ENCHANT_ITEM);
                  if (var1 instanceof ServerPlayer) {
                     CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)var1, var8, var5);
                  }

                  this.enchantSlots.setChanged();
                  this.enchantmentSeed.set(var1.getEnchantmentSeed());
                  this.slotsChanged(this.enchantSlots);
                  var6.playSound(null, var7, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, var6.random.nextFloat() * 0.1F + 0.9F);
               }
            });
            return true;
         }
      } else {
         Util.logAndPauseIfInIde(var1.getName() + " pressed invalid button id: " + var2);
         return false;
      }
   }

   private List<EnchantmentInstance> getEnchantmentList(RegistryAccess var1, ItemStack var2, int var3, int var4) {
      this.random.setSeed((long)(this.enchantmentSeed.get() + var3));
      Optional var5 = var1.lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.IN_ENCHANTING_TABLE);
      if (var5.isEmpty()) {
         return List.of();
      } else {
         List var6 = EnchantmentHelper.selectEnchantment(this.random, var2, var4, ((HolderSet.Named)var5.get()).stream());
         if (var2.is(Items.BOOK) && var6.size() > 1) {
            var6.remove(this.random.nextInt(var6.size()));
         }

         return var6;
      }
   }

   public int getGoldCount() {
      ItemStack var1 = this.enchantSlots.getItem(1);
      return var1.isEmpty() ? 0 : var1.getCount();
   }

   public int getEnchantmentSeed() {
      return this.enchantmentSeed.get();
   }

   @Override
   public void removed(Player var1) {
      super.removed(var1);
      this.access.execute((var2, var3) -> this.clearContainer(var1, this.enchantSlots));
   }

   @Override
   public boolean stillValid(Player var1) {
      return stillValid(this.access, var1, Blocks.ENCHANTING_TABLE);
   }

   @Override
   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 == 0) {
            if (!this.moveItemStackTo(var5, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (var2 == 1) {
            if (!this.moveItemStackTo(var5, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (var5.is(Items.LAPIS_LAZULI)) {
            if (!this.moveItemStackTo(var5, 1, 2, true)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (this.slots.get(0).hasItem() || !this.slots.get(0).mayPlace(var5)) {
               return ItemStack.EMPTY;
            }

            ItemStack var6 = var5.copyWithCount(1);
            var5.shrink(1);
            this.slots.get(0).setByPlayer(var6);
         }

         if (var5.isEmpty()) {
            var4.setByPlayer(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }

         if (var5.getCount() == var3.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(var1, var5);
      }

      return var3;
   }
}

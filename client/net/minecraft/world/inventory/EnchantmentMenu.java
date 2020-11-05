package net.minecraft.world.inventory;

import java.util.List;
import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Blocks;

public class EnchantmentMenu extends AbstractContainerMenu {
   private final Container enchantSlots;
   private final ContainerLevelAccess access;
   private final Random random;
   private final DataSlot enchantmentSeed;
   public final int[] costs;
   public final int[] enchantClue;
   public final int[] levelClue;

   public EnchantmentMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public EnchantmentMenu(int var1, Inventory var2, ContainerLevelAccess var3) {
      super(MenuType.ENCHANTMENT, var1);
      this.enchantSlots = new SimpleContainer(2) {
         public void setChanged() {
            super.setChanged();
            EnchantmentMenu.this.slotsChanged(this);
         }
      };
      this.random = new Random();
      this.enchantmentSeed = DataSlot.standalone();
      this.costs = new int[3];
      this.enchantClue = new int[]{-1, -1, -1};
      this.levelClue = new int[]{-1, -1, -1};
      this.access = var3;
      this.addSlot(new Slot(this.enchantSlots, 0, 15, 47) {
         public boolean mayPlace(ItemStack var1) {
            return true;
         }

         public int getMaxStackSize() {
            return 1;
         }
      });
      this.addSlot(new Slot(this.enchantSlots, 1, 35, 47) {
         public boolean mayPlace(ItemStack var1) {
            return var1.is(Items.LAPIS_LAZULI);
         }
      });

      int var4;
      for(var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new Slot(var2, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 9; ++var4) {
         this.addSlot(new Slot(var2, var4, 8 + var4 * 18, 142));
      }

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

   public void slotsChanged(Container var1) {
      if (var1 == this.enchantSlots) {
         ItemStack var2 = var1.getItem(0);
         if (!var2.isEmpty() && var2.isEnchantable()) {
            this.access.execute((var2x, var3x) -> {
               int var4 = 0;

               int var5;
               for(var5 = -1; var5 <= 1; ++var5) {
                  for(int var6 = -1; var6 <= 1; ++var6) {
                     if ((var5 != 0 || var6 != 0) && var2x.isEmptyBlock(var3x.offset(var6, 0, var5)) && var2x.isEmptyBlock(var3x.offset(var6, 1, var5))) {
                        if (var2x.getBlockState(var3x.offset(var6 * 2, 0, var5 * 2)).is(Blocks.BOOKSHELF)) {
                           ++var4;
                        }

                        if (var2x.getBlockState(var3x.offset(var6 * 2, 1, var5 * 2)).is(Blocks.BOOKSHELF)) {
                           ++var4;
                        }

                        if (var6 != 0 && var5 != 0) {
                           if (var2x.getBlockState(var3x.offset(var6 * 2, 0, var5)).is(Blocks.BOOKSHELF)) {
                              ++var4;
                           }

                           if (var2x.getBlockState(var3x.offset(var6 * 2, 1, var5)).is(Blocks.BOOKSHELF)) {
                              ++var4;
                           }

                           if (var2x.getBlockState(var3x.offset(var6, 0, var5 * 2)).is(Blocks.BOOKSHELF)) {
                              ++var4;
                           }

                           if (var2x.getBlockState(var3x.offset(var6, 1, var5 * 2)).is(Blocks.BOOKSHELF)) {
                              ++var4;
                           }
                        }
                     }
                  }
               }

               this.random.setSeed((long)this.enchantmentSeed.get());

               for(var5 = 0; var5 < 3; ++var5) {
                  this.costs[var5] = EnchantmentHelper.getEnchantmentCost(this.random, var5, var4, var2);
                  this.enchantClue[var5] = -1;
                  this.levelClue[var5] = -1;
                  if (this.costs[var5] < var5 + 1) {
                     this.costs[var5] = 0;
                  }
               }

               for(var5 = 0; var5 < 3; ++var5) {
                  if (this.costs[var5] > 0) {
                     List var8 = this.getEnchantmentList(var2, var5, this.costs[var5]);
                     if (var8 != null && !var8.isEmpty()) {
                        EnchantmentInstance var7 = (EnchantmentInstance)var8.get(this.random.nextInt(var8.size()));
                        this.enchantClue[var5] = Registry.ENCHANTMENT.getId(var7.enchantment);
                        this.levelClue[var5] = var7.level;
                     }
                  }
               }

               this.broadcastChanges();
            });
         } else {
            for(int var3 = 0; var3 < 3; ++var3) {
               this.costs[var3] = 0;
               this.enchantClue[var3] = -1;
               this.levelClue[var3] = -1;
            }
         }
      }

   }

   public boolean clickMenuButton(Player var1, int var2) {
      ItemStack var3 = this.enchantSlots.getItem(0);
      ItemStack var4 = this.enchantSlots.getItem(1);
      int var5 = var2 + 1;
      if ((var4.isEmpty() || var4.getCount() < var5) && !var1.getAbilities().instabuild) {
         return false;
      } else if (this.costs[var2] <= 0 || var3.isEmpty() || (var1.experienceLevel < var5 || var1.experienceLevel < this.costs[var2]) && !var1.getAbilities().instabuild) {
         return false;
      } else {
         this.access.execute((var6, var7) -> {
            ItemStack var8 = var3;
            List var9 = this.getEnchantmentList(var3, var2, this.costs[var2]);
            if (!var9.isEmpty()) {
               var1.onEnchantmentPerformed(var3, var5);
               boolean var10 = var3.is(Items.BOOK);
               if (var10) {
                  var8 = new ItemStack(Items.ENCHANTED_BOOK);
                  CompoundTag var11 = var3.getTag();
                  if (var11 != null) {
                     var8.setTag(var11.copy());
                  }

                  this.enchantSlots.setItem(0, var8);
               }

               for(int var13 = 0; var13 < var9.size(); ++var13) {
                  EnchantmentInstance var12 = (EnchantmentInstance)var9.get(var13);
                  if (var10) {
                     EnchantedBookItem.addEnchantment(var8, var12);
                  } else {
                     var8.enchant(var12.enchantment, var12.level);
                  }
               }

               if (!var1.getAbilities().instabuild) {
                  var4.shrink(var5);
                  if (var4.isEmpty()) {
                     this.enchantSlots.setItem(1, ItemStack.EMPTY);
                  }
               }

               var1.awardStat(Stats.ENCHANT_ITEM);
               if (var1 instanceof ServerPlayer) {
                  CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)var1, var8, var5);
               }

               this.enchantSlots.setChanged();
               this.enchantmentSeed.set(var1.getEnchantmentSeed());
               this.slotsChanged(this.enchantSlots);
               var6.playSound((Player)null, (BlockPos)var7, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, var6.random.nextFloat() * 0.1F + 0.9F);
            }

         });
         return true;
      }
   }

   private List<EnchantmentInstance> getEnchantmentList(ItemStack var1, int var2, int var3) {
      this.random.setSeed((long)(this.enchantmentSeed.get() + var2));
      List var4 = EnchantmentHelper.selectEnchantment(this.random, var1, var3, false);
      if (var1.is(Items.BOOK) && var4.size() > 1) {
         var4.remove(this.random.nextInt(var4.size()));
      }

      return var4;
   }

   public int getGoldCount() {
      ItemStack var1 = this.enchantSlots.getItem(1);
      return var1.isEmpty() ? 0 : var1.getCount();
   }

   public int getEnchantmentSeed() {
      return this.enchantmentSeed.get();
   }

   public void removed(Player var1) {
      super.removed(var1);
      this.access.execute((var2, var3) -> {
         this.clearContainer(var1, this.enchantSlots);
      });
   }

   public boolean stillValid(Player var1) {
      return stillValid(this.access, var1, Blocks.ENCHANTING_TABLE);
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
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
            if (((Slot)this.slots.get(0)).hasItem() || !((Slot)this.slots.get(0)).mayPlace(var5)) {
               return ItemStack.EMPTY;
            }

            ItemStack var6 = var5.copy();
            var6.setCount(1);
            var5.shrink(1);
            ((Slot)this.slots.get(0)).set(var6);
         }

         if (var5.isEmpty()) {
            var4.set(ItemStack.EMPTY);
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

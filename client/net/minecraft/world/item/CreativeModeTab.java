package net.minecraft.world.item;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.Blocks;

public abstract class CreativeModeTab {
   public static final CreativeModeTab[] TABS = new CreativeModeTab[12];
   public static final CreativeModeTab TAB_BUILDING_BLOCKS = (new CreativeModeTab(0, "buildingBlocks") {
      public ItemStack makeIcon() {
         return new ItemStack(Blocks.BRICKS);
      }
   }).setRecipeFolderName("building_blocks");
   public static final CreativeModeTab TAB_DECORATIONS = new CreativeModeTab(1, "decorations") {
      public ItemStack makeIcon() {
         return new ItemStack(Blocks.PEONY);
      }
   };
   public static final CreativeModeTab TAB_REDSTONE = new CreativeModeTab(2, "redstone") {
      public ItemStack makeIcon() {
         return new ItemStack(Items.REDSTONE);
      }
   };
   public static final CreativeModeTab TAB_TRANSPORTATION = new CreativeModeTab(3, "transportation") {
      public ItemStack makeIcon() {
         return new ItemStack(Blocks.POWERED_RAIL);
      }
   };
   public static final CreativeModeTab TAB_MISC = new CreativeModeTab(6, "misc") {
      public ItemStack makeIcon() {
         return new ItemStack(Items.LAVA_BUCKET);
      }
   };
   public static final CreativeModeTab TAB_SEARCH = (new CreativeModeTab(5, "search") {
      public ItemStack makeIcon() {
         return new ItemStack(Items.COMPASS);
      }
   }).setBackgroundSuffix("item_search.png");
   public static final CreativeModeTab TAB_FOOD = new CreativeModeTab(7, "food") {
      public ItemStack makeIcon() {
         return new ItemStack(Items.APPLE);
      }
   };
   public static final CreativeModeTab TAB_TOOLS;
   public static final CreativeModeTab TAB_COMBAT;
   public static final CreativeModeTab TAB_BREWING;
   public static final CreativeModeTab TAB_MATERIALS;
   public static final CreativeModeTab TAB_HOTBAR;
   public static final CreativeModeTab TAB_INVENTORY;
   // $FF: renamed from: id int
   private final int field_216;
   private final String langId;
   private final Component displayName;
   private String recipeFolderName;
   private String backgroundSuffix = "items.png";
   private boolean canScroll = true;
   private boolean showTitle = true;
   private EnchantmentCategory[] enchantmentCategories = new EnchantmentCategory[0];
   private ItemStack iconItemStack;

   public CreativeModeTab(int var1, String var2) {
      super();
      this.field_216 = var1;
      this.langId = var2;
      this.displayName = new TranslatableComponent("itemGroup." + var2);
      this.iconItemStack = ItemStack.EMPTY;
      TABS[var1] = this;
   }

   public int getId() {
      return this.field_216;
   }

   public String getRecipeFolderName() {
      return this.recipeFolderName == null ? this.langId : this.recipeFolderName;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public ItemStack getIconItem() {
      if (this.iconItemStack.isEmpty()) {
         this.iconItemStack = this.makeIcon();
      }

      return this.iconItemStack;
   }

   public abstract ItemStack makeIcon();

   public String getBackgroundSuffix() {
      return this.backgroundSuffix;
   }

   public CreativeModeTab setBackgroundSuffix(String var1) {
      this.backgroundSuffix = var1;
      return this;
   }

   public CreativeModeTab setRecipeFolderName(String var1) {
      this.recipeFolderName = var1;
      return this;
   }

   public boolean showTitle() {
      return this.showTitle;
   }

   public CreativeModeTab hideTitle() {
      this.showTitle = false;
      return this;
   }

   public boolean canScroll() {
      return this.canScroll;
   }

   public CreativeModeTab hideScroll() {
      this.canScroll = false;
      return this;
   }

   public int getColumn() {
      return this.field_216 % 6;
   }

   public boolean isTopRow() {
      return this.field_216 < 6;
   }

   public boolean isAlignedRight() {
      return this.getColumn() == 5;
   }

   public EnchantmentCategory[] getEnchantmentCategories() {
      return this.enchantmentCategories;
   }

   public CreativeModeTab setEnchantmentCategories(EnchantmentCategory... var1) {
      this.enchantmentCategories = var1;
      return this;
   }

   public boolean hasEnchantmentCategory(@Nullable EnchantmentCategory var1) {
      if (var1 != null) {
         EnchantmentCategory[] var2 = this.enchantmentCategories;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EnchantmentCategory var5 = var2[var4];
            if (var5 == var1) {
               return true;
            }
         }
      }

      return false;
   }

   public void fillItemList(NonNullList<ItemStack> var1) {
      Iterator var2 = Registry.ITEM.iterator();

      while(var2.hasNext()) {
         Item var3 = (Item)var2.next();
         var3.fillItemCategory(this, var1);
      }

   }

   static {
      TAB_TOOLS = (new CreativeModeTab(8, "tools") {
         public ItemStack makeIcon() {
            return new ItemStack(Items.IRON_AXE);
         }
      }).setEnchantmentCategories(new EnchantmentCategory[]{EnchantmentCategory.VANISHABLE, EnchantmentCategory.DIGGER, EnchantmentCategory.FISHING_ROD, EnchantmentCategory.BREAKABLE});
      TAB_COMBAT = (new CreativeModeTab(9, "combat") {
         public ItemStack makeIcon() {
            return new ItemStack(Items.GOLDEN_SWORD);
         }
      }).setEnchantmentCategories(new EnchantmentCategory[]{EnchantmentCategory.VANISHABLE, EnchantmentCategory.ARMOR, EnchantmentCategory.ARMOR_FEET, EnchantmentCategory.ARMOR_HEAD, EnchantmentCategory.ARMOR_LEGS, EnchantmentCategory.ARMOR_CHEST, EnchantmentCategory.BOW, EnchantmentCategory.WEAPON, EnchantmentCategory.WEARABLE, EnchantmentCategory.BREAKABLE, EnchantmentCategory.TRIDENT, EnchantmentCategory.CROSSBOW});
      TAB_BREWING = new CreativeModeTab(10, "brewing") {
         public ItemStack makeIcon() {
            return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
         }
      };
      TAB_MATERIALS = TAB_MISC;
      TAB_HOTBAR = new CreativeModeTab(4, "hotbar") {
         public ItemStack makeIcon() {
            return new ItemStack(Blocks.BOOKSHELF);
         }

         public void fillItemList(NonNullList<ItemStack> var1) {
            throw new RuntimeException("Implement exception client-side.");
         }

         public boolean isAlignedRight() {
            return true;
         }
      };
      TAB_INVENTORY = (new CreativeModeTab(11, "inventory") {
         public ItemStack makeIcon() {
            return new ItemStack(Blocks.CHEST);
         }
      }).setBackgroundSuffix("inventory.png").hideScroll().hideTitle();
   }
}

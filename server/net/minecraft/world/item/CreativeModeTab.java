package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public abstract class CreativeModeTab {
   public static final CreativeModeTab[] TABS = new CreativeModeTab[12];
   public static final CreativeModeTab TAB_BUILDING_BLOCKS = (new CreativeModeTab(0, "buildingBlocks") {
   }).setRecipeFolderName("building_blocks");
   public static final CreativeModeTab TAB_DECORATIONS = new CreativeModeTab(1, "decorations") {
   };
   public static final CreativeModeTab TAB_REDSTONE = new CreativeModeTab(2, "redstone") {
   };
   public static final CreativeModeTab TAB_TRANSPORTATION = new CreativeModeTab(3, "transportation") {
   };
   public static final CreativeModeTab TAB_MISC = new CreativeModeTab(6, "misc") {
   };
   public static final CreativeModeTab TAB_SEARCH = (new CreativeModeTab(5, "search") {
   }).setBackgroundSuffix("item_search.png");
   public static final CreativeModeTab TAB_FOOD = new CreativeModeTab(7, "food") {
   };
   public static final CreativeModeTab TAB_TOOLS;
   public static final CreativeModeTab TAB_COMBAT;
   public static final CreativeModeTab TAB_BREWING;
   public static final CreativeModeTab TAB_MATERIALS;
   public static final CreativeModeTab TAB_HOTBAR;
   public static final CreativeModeTab TAB_INVENTORY;
   private final int id;
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
      this.id = var1;
      this.langId = var2;
      this.displayName = new TranslatableComponent("itemGroup." + var2);
      this.iconItemStack = ItemStack.EMPTY;
      TABS[var1] = this;
   }

   public String getRecipeFolderName() {
      return this.recipeFolderName == null ? this.langId : this.recipeFolderName;
   }

   public CreativeModeTab setBackgroundSuffix(String var1) {
      this.backgroundSuffix = var1;
      return this;
   }

   public CreativeModeTab setRecipeFolderName(String var1) {
      this.recipeFolderName = var1;
      return this;
   }

   public CreativeModeTab hideTitle() {
      this.showTitle = false;
      return this;
   }

   public CreativeModeTab hideScroll() {
      this.canScroll = false;
      return this;
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

   static {
      TAB_TOOLS = (new CreativeModeTab(8, "tools") {
      }).setEnchantmentCategories(new EnchantmentCategory[]{EnchantmentCategory.VANISHABLE, EnchantmentCategory.DIGGER, EnchantmentCategory.FISHING_ROD, EnchantmentCategory.BREAKABLE});
      TAB_COMBAT = (new CreativeModeTab(9, "combat") {
      }).setEnchantmentCategories(new EnchantmentCategory[]{EnchantmentCategory.VANISHABLE, EnchantmentCategory.ARMOR, EnchantmentCategory.ARMOR_FEET, EnchantmentCategory.ARMOR_HEAD, EnchantmentCategory.ARMOR_LEGS, EnchantmentCategory.ARMOR_CHEST, EnchantmentCategory.BOW, EnchantmentCategory.WEAPON, EnchantmentCategory.WEARABLE, EnchantmentCategory.BREAKABLE, EnchantmentCategory.TRIDENT, EnchantmentCategory.CROSSBOW});
      TAB_BREWING = new CreativeModeTab(10, "brewing") {
      };
      TAB_MATERIALS = TAB_MISC;
      TAB_HOTBAR = new CreativeModeTab(4, "hotbar") {
      };
      TAB_INVENTORY = (new CreativeModeTab(11, "inventory") {
      }).setBackgroundSuffix("inventory.png").hideScroll().hideTitle();
   }
}

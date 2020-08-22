package net.minecraft.world.inventory;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Inventory;

public class MenuType {
   public static final MenuType GENERIC_9x1 = register("generic_9x1", ChestMenu::oneRow);
   public static final MenuType GENERIC_9x2 = register("generic_9x2", ChestMenu::twoRows);
   public static final MenuType GENERIC_9x3 = register("generic_9x3", ChestMenu::threeRows);
   public static final MenuType GENERIC_9x4 = register("generic_9x4", ChestMenu::fourRows);
   public static final MenuType GENERIC_9x5 = register("generic_9x5", ChestMenu::fiveRows);
   public static final MenuType GENERIC_9x6 = register("generic_9x6", ChestMenu::sixRows);
   public static final MenuType GENERIC_3x3 = register("generic_3x3", DispenserMenu::new);
   public static final MenuType ANVIL = register("anvil", AnvilMenu::new);
   public static final MenuType BEACON = register("beacon", BeaconMenu::new);
   public static final MenuType BLAST_FURNACE = register("blast_furnace", BlastFurnaceMenu::new);
   public static final MenuType BREWING_STAND = register("brewing_stand", BrewingStandMenu::new);
   public static final MenuType CRAFTING = register("crafting", CraftingMenu::new);
   public static final MenuType ENCHANTMENT = register("enchantment", EnchantmentMenu::new);
   public static final MenuType FURNACE = register("furnace", FurnaceMenu::new);
   public static final MenuType GRINDSTONE = register("grindstone", GrindstoneMenu::new);
   public static final MenuType HOPPER = register("hopper", HopperMenu::new);
   public static final MenuType LECTERN = register("lectern", (var0, var1) -> {
      return new LecternMenu(var0);
   });
   public static final MenuType LOOM = register("loom", LoomMenu::new);
   public static final MenuType MERCHANT = register("merchant", MerchantMenu::new);
   public static final MenuType SHULKER_BOX = register("shulker_box", ShulkerBoxMenu::new);
   public static final MenuType SMOKER = register("smoker", SmokerMenu::new);
   public static final MenuType CARTOGRAPHY_TABLE = register("cartography_table", CartographyTableMenu::new);
   public static final MenuType STONECUTTER = register("stonecutter", StonecutterMenu::new);
   private final MenuType.MenuSupplier constructor;

   private static MenuType register(String var0, MenuType.MenuSupplier var1) {
      return (MenuType)Registry.register(Registry.MENU, (String)var0, new MenuType(var1));
   }

   private MenuType(MenuType.MenuSupplier var1) {
      this.constructor = var1;
   }

   public AbstractContainerMenu create(int var1, Inventory var2) {
      return this.constructor.create(var1, var2);
   }

   interface MenuSupplier {
      AbstractContainerMenu create(int var1, Inventory var2);
   }
}

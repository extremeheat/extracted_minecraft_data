package net.minecraft.world.inventory;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Inventory;

public class MenuType<T extends AbstractContainerMenu> {
   public static final MenuType<ChestMenu> GENERIC_9x1 = register("generic_9x1", ChestMenu::oneRow);
   public static final MenuType<ChestMenu> GENERIC_9x2 = register("generic_9x2", ChestMenu::twoRows);
   public static final MenuType<ChestMenu> GENERIC_9x3 = register("generic_9x3", ChestMenu::threeRows);
   public static final MenuType<ChestMenu> GENERIC_9x4 = register("generic_9x4", ChestMenu::fourRows);
   public static final MenuType<ChestMenu> GENERIC_9x5 = register("generic_9x5", ChestMenu::fiveRows);
   public static final MenuType<ChestMenu> GENERIC_9x6 = register("generic_9x6", ChestMenu::sixRows);
   public static final MenuType<DispenserMenu> GENERIC_3x3 = register("generic_3x3", DispenserMenu::new);
   public static final MenuType<AnvilMenu> ANVIL = register("anvil", AnvilMenu::new);
   public static final MenuType<BeaconMenu> BEACON = register("beacon", BeaconMenu::new);
   public static final MenuType<BlastFurnaceMenu> BLAST_FURNACE = register("blast_furnace", BlastFurnaceMenu::new);
   public static final MenuType<BrewingStandMenu> BREWING_STAND = register("brewing_stand", BrewingStandMenu::new);
   public static final MenuType<CraftingMenu> CRAFTING = register("crafting", CraftingMenu::new);
   public static final MenuType<EnchantmentMenu> ENCHANTMENT = register("enchantment", EnchantmentMenu::new);
   public static final MenuType<FurnaceMenu> FURNACE = register("furnace", FurnaceMenu::new);
   public static final MenuType<GrindstoneMenu> GRINDSTONE = register("grindstone", GrindstoneMenu::new);
   public static final MenuType<HopperMenu> HOPPER = register("hopper", HopperMenu::new);
   public static final MenuType<LecternMenu> LECTERN = register("lectern", (var0, var1) -> {
      return new LecternMenu(var0);
   });
   public static final MenuType<LoomMenu> LOOM = register("loom", LoomMenu::new);
   public static final MenuType<MerchantMenu> MERCHANT = register("merchant", MerchantMenu::new);
   public static final MenuType<ShulkerBoxMenu> SHULKER_BOX = register("shulker_box", ShulkerBoxMenu::new);
   public static final MenuType<SmokerMenu> SMOKER = register("smoker", SmokerMenu::new);
   public static final MenuType<CartographyMenu> CARTOGRAPHY = register("cartography", CartographyMenu::new);
   public static final MenuType<StonecutterMenu> STONECUTTER = register("stonecutter", StonecutterMenu::new);
   private final MenuType.MenuSupplier<T> constructor;

   private static <T extends AbstractContainerMenu> MenuType<T> register(String var0, MenuType.MenuSupplier<T> var1) {
      return (MenuType)Registry.register(Registry.MENU, (String)var0, new MenuType(var1));
   }

   private MenuType(MenuType.MenuSupplier<T> var1) {
      super();
      this.constructor = var1;
   }

   public T create(int var1, Inventory var2) {
      return this.constructor.create(var1, var2);
   }

   interface MenuSupplier<T extends AbstractContainerMenu> {
      T create(int var1, Inventory var2);
   }
}

package net.minecraft.client.gui.screens;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.gui.screens.inventory.BlastFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screens.inventory.CartographyTableScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.DispenserScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.client.gui.screens.inventory.GrindstoneScreen;
import net.minecraft.client.gui.screens.inventory.HopperScreen;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.gui.screens.inventory.SmokerScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MenuScreens {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<MenuType<?>, MenuScreens.ScreenConstructor<?, ?>> SCREENS = Maps.newHashMap();

   public static <T extends AbstractContainerMenu> void create(@Nullable MenuType<T> var0, Minecraft var1, int var2, Component var3) {
      if (var0 == null) {
         LOGGER.warn("Trying to open invalid screen with name: {}", var3.getString());
      } else {
         MenuScreens.ScreenConstructor var4 = getConstructor(var0);
         if (var4 == null) {
            LOGGER.warn("Failed to create screen for menu type: {}", Registry.MENU.getKey(var0));
         } else {
            var4.fromPacket(var3, var0, var1, var2);
         }
      }
   }

   @Nullable
   private static <T extends AbstractContainerMenu> MenuScreens.ScreenConstructor<T, ?> getConstructor(MenuType<T> var0) {
      return (MenuScreens.ScreenConstructor)SCREENS.get(var0);
   }

   private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> var0, MenuScreens.ScreenConstructor<M, U> var1) {
      MenuScreens.ScreenConstructor var2 = (MenuScreens.ScreenConstructor)SCREENS.put(var0, var1);
      if (var2 != null) {
         throw new IllegalStateException("Duplicate registration for " + Registry.MENU.getKey(var0));
      }
   }

   public static boolean selfTest() {
      boolean var0 = false;
      Iterator var1 = Registry.MENU.iterator();

      while(var1.hasNext()) {
         MenuType var2 = (MenuType)var1.next();
         if (!SCREENS.containsKey(var2)) {
            LOGGER.debug("Menu {} has no matching screen", Registry.MENU.getKey(var2));
            var0 = true;
         }
      }

      return var0;
   }

   static {
      register(MenuType.GENERIC_9x1, ContainerScreen::new);
      register(MenuType.GENERIC_9x2, ContainerScreen::new);
      register(MenuType.GENERIC_9x3, ContainerScreen::new);
      register(MenuType.GENERIC_9x4, ContainerScreen::new);
      register(MenuType.GENERIC_9x5, ContainerScreen::new);
      register(MenuType.GENERIC_9x6, ContainerScreen::new);
      register(MenuType.GENERIC_3x3, DispenserScreen::new);
      register(MenuType.ANVIL, AnvilScreen::new);
      register(MenuType.BEACON, BeaconScreen::new);
      register(MenuType.BLAST_FURNACE, BlastFurnaceScreen::new);
      register(MenuType.BREWING_STAND, BrewingStandScreen::new);
      register(MenuType.CRAFTING, CraftingScreen::new);
      register(MenuType.ENCHANTMENT, EnchantmentScreen::new);
      register(MenuType.FURNACE, FurnaceScreen::new);
      register(MenuType.GRINDSTONE, GrindstoneScreen::new);
      register(MenuType.HOPPER, HopperScreen::new);
      register(MenuType.LECTERN, LecternScreen::new);
      register(MenuType.LOOM, LoomScreen::new);
      register(MenuType.MERCHANT, MerchantScreen::new);
      register(MenuType.SHULKER_BOX, ShulkerBoxScreen::new);
      register(MenuType.SMITHING, SmithingScreen::new);
      register(MenuType.SMOKER, SmokerScreen::new);
      register(MenuType.CARTOGRAPHY_TABLE, CartographyTableScreen::new);
      register(MenuType.STONECUTTER, StonecutterScreen::new);
   }

   interface ScreenConstructor<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {
      default void fromPacket(Component var1, MenuType<T> var2, Minecraft var3, int var4) {
         Screen var5 = this.create(var2.create(var4, var3.player.getInventory()), var3.player.getInventory(), var1);
         var3.player.containerMenu = ((MenuAccess)var5).getMenu();
         var3.setScreen(var5);
      }

      U create(T var1, Inventory var2, Component var3);
   }
}

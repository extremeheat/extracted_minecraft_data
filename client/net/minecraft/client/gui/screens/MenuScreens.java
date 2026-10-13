package net.minecraft.client.gui.screens;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.client.core.registries.ClientRegistries;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.gui.screens.inventory.BlastFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screens.inventory.CartographyTableScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CrafterScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.DispenserScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.client.gui.screens.inventory.GrindstoneScreen;
import net.minecraft.client.gui.screens.inventory.HopperScreen;
import net.minecraft.client.gui.screens.inventory.JukeboxScreen;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.gui.screens.inventory.SmokerScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class MenuScreens {
   private static final Logger LOGGER = LogUtils.getLogger();

   public MenuScreens() {
      super();
   }

   public static <T extends AbstractContainerMenu> void create(final MenuType<T> type, final Minecraft minecraft, final int containerId, final Component title) {
      ScreenConstructor<T, ?> constructor = getConstructor(type);
      if (constructor == null) {
         LOGGER.warn("Failed to create screen for menu type: {}", BuiltInRegistries.MENU.getKey(type));
      } else {
         constructor.fromPacket(title, type, minecraft, containerId);
      }
   }

   private static <T extends AbstractContainerMenu> @Nullable ScreenConstructor<T, ?> getConstructor(final MenuType<T> type) {
      return (ScreenConstructor)BuiltInClientRegistries.MENU_SCREEN.getValue(menuTypeKey(type));
   }

   private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> ScreenConstructor<?, ?> register(final Registry<ScreenConstructor<?, ?>> registry, final MenuType<? extends M> type, final ScreenConstructor<M, U> factory) {
      return (ScreenConstructor)Registry.register(registry, (ResourceKey)menuTypeKey(type), factory);
   }

   private static ResourceKey<ScreenConstructor<?, ?>> menuTypeKey(final MenuType<?> type) {
      return ResourceKey.create(ClientRegistries.MENU_SCREEN, (Identifier)Objects.requireNonNull(BuiltInRegistries.MENU.getKey(type), () -> "Unregistered menu type: " + String.valueOf(type)));
   }

   public static ScreenConstructor<?, ?> bootstrap(final Registry<ScreenConstructor<?, ?>> registry) {
      register(registry, MenuType.GENERIC_9x1, ContainerScreen::new);
      register(registry, MenuType.GENERIC_9x2, ContainerScreen::new);
      register(registry, MenuType.GENERIC_9x3, ContainerScreen::new);
      register(registry, MenuType.GENERIC_9x4, ContainerScreen::new);
      register(registry, MenuType.GENERIC_9x5, ContainerScreen::new);
      register(registry, MenuType.GENERIC_9x6, ContainerScreen::new);
      register(registry, MenuType.GENERIC_3x3, DispenserScreen::new);
      register(registry, MenuType.CRAFTER_3x3, CrafterScreen::new);
      register(registry, MenuType.JUKEBOX, JukeboxScreen::new);
      register(registry, MenuType.ANVIL, AnvilScreen::new);
      register(registry, MenuType.BEACON, BeaconScreen::new);
      register(registry, MenuType.BLAST_FURNACE, BlastFurnaceScreen::new);
      register(registry, MenuType.BREWING_STAND, BrewingStandScreen::new);
      register(registry, MenuType.CRAFTING, CraftingScreen::new);
      register(registry, MenuType.ENCHANTMENT, EnchantmentScreen::new);
      register(registry, MenuType.FURNACE, FurnaceScreen::new);
      register(registry, MenuType.GRINDSTONE, GrindstoneScreen::new);
      register(registry, MenuType.HOPPER, HopperScreen::new);
      register(registry, MenuType.LECTERN, LecternScreen::new);
      register(registry, MenuType.LOOM, LoomScreen::new);
      register(registry, MenuType.MERCHANT, MerchantScreen::new);
      register(registry, MenuType.SHULKER_BOX, ShulkerBoxScreen::new);
      register(registry, MenuType.SMITHING, SmithingScreen::new);
      register(registry, MenuType.SMOKER, SmokerScreen::new);
      register(registry, MenuType.CARTOGRAPHY_TABLE, CartographyTableScreen::new);
      return register(registry, MenuType.STONECUTTER, StonecutterScreen::new);
   }

   public static boolean selfTest() {
      boolean failed = false;

      for(MenuType<?> menuType : BuiltInRegistries.MENU) {
         ResourceKey<ScreenConstructor<?, ?>> key = menuTypeKey(menuType);
         if (BuiltInClientRegistries.MENU_SCREEN.getValue(key) == null) {
            LOGGER.debug("Menu {} has no matching screen", key);
            failed = true;
         }
      }

      return failed;
   }

   public interface ScreenConstructor<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {
      default void fromPacket(final Component title, final MenuType<T> type, final Minecraft minecraft, final int containerId) {
         U screen = this.create(type.create(containerId, minecraft.player.getInventory()), minecraft.player.getInventory(), title);
         minecraft.player.containerMenu = (screen).getMenu();
         minecraft.gui.setScreen(screen);
      }

      U create(T menu, Inventory inventory, final Component title);
   }
}

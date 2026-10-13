package net.minecraft.client.core.registries;

import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class ClientRegistries {
   public static final Identifier ROOT_REGISTRY_NAME = Identifier.withDefaultNamespace("client_root");
   public static final ResourceKey<Registry<BlockEntityRendererProvider<?, ?>>> BLOCK_ENTITY_RENDERER = createRegistryKey("block_entity_renderer");
   public static final ResourceKey<Registry<DebugScreenEntry>> DEBUG_SCREEN_ENTRY = createRegistryKey("debug_screen_entry");
   public static final ResourceKey<Registry<EntityRendererProvider<?>>> ENTITY_RENDERER = createRegistryKey("entity_renderer");
   public static final ResourceKey<Registry<MenuScreens.ScreenConstructor<?, ?>>> MENU_SCREEN = createRegistryKey("menu_screen");
   public static final ResourceKey<Registry<ParticleProvider<?>>> PARTICLE_PROVIDER = createRegistryKey("particle_provider");

   public ClientRegistries() {
      super();
   }

   private static <T> ResourceKey<Registry<T>> createRegistryKey(final String name) {
      return ResourceKey.createRegistryKey(ROOT_REGISTRY_NAME, Identifier.withDefaultNamespace(name));
   }
}

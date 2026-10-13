package net.minecraft.client.core.registries;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltinRegistryBuilder;

public class BuiltInClientRegistries {
   private static final BuiltinRegistryBuilder BUILDER;
   public static final Registry<BlockEntityRendererProvider<?, ?>> BLOCK_ENTITY_RENDERER;
   public static final Registry<DebugScreenEntry> DEBUG_SCREEN_ENTRY;
   public static final Registry<EntityRendererProvider<?>> ENTITY_RENDERER;
   public static final Registry<MenuScreens.ScreenConstructor<?, ?>> MENU_SCREEN;
   public static final Registry<ParticleProvider<?>> PARTICLE_PROVIDER;
   public static final Registry<? extends Registry<?>> REGISTRY;

   public BuiltInClientRegistries() {
      super();
   }

   public static void bootStrap() {
      BUILDER.bootStrap();
   }

   static {
      BUILDER = new BuiltinRegistryBuilder(ClientRegistries.ROOT_REGISTRY_NAME);
      BLOCK_ENTITY_RENDERER = BUILDER.<BlockEntityRendererProvider<?, ?>>registerSimple(ClientRegistries.BLOCK_ENTITY_RENDERER, BlockEntityRenderers::bootstrap);
      DEBUG_SCREEN_ENTRY = BUILDER.<DebugScreenEntry>registerSimple(ClientRegistries.DEBUG_SCREEN_ENTRY, DebugScreenEntries::bootstrap);
      ENTITY_RENDERER = BUILDER.<EntityRendererProvider<?>>registerSimple(ClientRegistries.ENTITY_RENDERER, EntityRenderers::bootstrap);
      MENU_SCREEN = BUILDER.<MenuScreens.ScreenConstructor<?, ?>>registerSimple(ClientRegistries.MENU_SCREEN, MenuScreens::bootstrap);
      PARTICLE_PROVIDER = BUILDER.<ParticleProvider<?>>registerSimple(ClientRegistries.PARTICLE_PROVIDER, ParticleResources::bootstrap);
      REGISTRY = BUILDER.build();
   }
}

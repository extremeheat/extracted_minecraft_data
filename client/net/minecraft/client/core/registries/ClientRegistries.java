package net.minecraft.client.core.registries;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class ClientRegistries {
   public static final Identifier ROOT_REGISTRY_NAME = Identifier.withDefaultNamespace("client_root");
   public static final ResourceKey<Registry<BlockEntityRendererProvider<?, ?>>> BLOCK_ENTITY_RENDERER = createRegistryKey("block_entity_renderer");
   public static final ResourceKey<Registry<MapCodec<? extends ConditionalItemModelProperty>>> CONDITIONAL_ITEM_MODEL_PROPERTY_TYPE = createRegistryKey("conditional_item_model_property_type");
   public static final ResourceKey<Registry<DebugScreenEntry>> DEBUG_SCREEN_ENTRY = createRegistryKey("debug_screen_entry");
   public static final ResourceKey<Registry<EntityRendererProvider<?>>> ENTITY_RENDERER = createRegistryKey("entity_renderer");
   public static final ResourceKey<Registry<MapCodec<? extends ItemModel.Unbaked>>> ITEM_MODEL_TYPE = createRegistryKey("item_model_type");
   public static final ResourceKey<Registry<MapCodec<? extends ItemTintSource>>> ITEM_TINT_SOURCE_TYPE = createRegistryKey("item_tint_source_type");
   public static final ResourceKey<Registry<MenuScreens.ScreenConstructor<?, ?>>> MENU_SCREEN = createRegistryKey("menu_screen");
   public static final ResourceKey<Registry<ParticleProvider<?>>> PARTICLE_PROVIDER = createRegistryKey("particle_provider");
   public static final ResourceKey<Registry<MapCodec<? extends RangeSelectItemModelProperty>>> RANGE_SELECT_ITEM_MODEL_PROPERTY_TYPE = createRegistryKey("range_select_item_model_property_type");
   public static final ResourceKey<Registry<SelectItemModelProperty.Type<?, ?>>> SELECT_ITEM_MODEL_PROPERTY_TYPE = createRegistryKey("select_item_model_property_type");
   public static final ResourceKey<Registry<MapCodec<? extends SpecialModelRenderer.Unbaked<?>>>> SPECIAL_MODEL_RENDERER_TYPE = createRegistryKey("special_model_renderer_type");
   public static final ResourceKey<Registry<MapCodec<? extends SpriteSource>>> SPRITE_SOURCE_TYPE = createRegistryKey("sprite_source_type");

   public ClientRegistries() {
      super();
   }

   private static <T> ResourceKey<Registry<T>> createRegistryKey(final String name) {
      return ResourceKey.createRegistryKey(ROOT_REGISTRY_NAME, Identifier.withDefaultNamespace(name));
   }
}

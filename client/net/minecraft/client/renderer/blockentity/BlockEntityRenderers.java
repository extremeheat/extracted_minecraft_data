package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.client.core.registries.ClientRegistries;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;

public class BlockEntityRenderers {
   public BlockEntityRenderers() {
      super();
   }

   public static BlockEntityRendererProvider<?, ?> bootstrap(final Registry<BlockEntityRendererProvider<?, ?>> registry) {
      register(registry, BlockEntityTypes.SIGN, StandingSignRenderer::new);
      register(registry, BlockEntityTypes.HANGING_SIGN, HangingSignRenderer::new);
      register(registry, BlockEntityTypes.MOB_SPAWNER, SpawnerRenderer::new);
      register(registry, BlockEntityTypes.PISTON, (var0) -> new PistonHeadRenderer());
      register(registry, BlockEntityTypes.CHEST, ChestRenderer::new);
      register(registry, BlockEntityTypes.ENDER_CHEST, ChestRenderer::new);
      register(registry, BlockEntityTypes.TRAPPED_CHEST, ChestRenderer::new);
      register(registry, BlockEntityTypes.ENCHANTING_TABLE, EnchantTableRenderer::new);
      register(registry, BlockEntityTypes.LECTERN, LecternRenderer::new);
      register(registry, BlockEntityTypes.END_PORTAL, (var0) -> new TheEndPortalRenderer());
      register(registry, BlockEntityTypes.END_GATEWAY, (var0) -> new TheEndGatewayRenderer());
      register(registry, BlockEntityTypes.BEACON, (var0) -> new BeaconRenderer());
      register(registry, BlockEntityTypes.SKULL, SkullBlockRenderer::new);
      register(registry, BlockEntityTypes.BANNER, BannerRenderer::new);
      register(registry, BlockEntityTypes.STRUCTURE_BLOCK, (var0) -> new BlockEntityWithBoundingBoxRenderer());
      register(registry, BlockEntityTypes.TEST_INSTANCE_BLOCK, (var0) -> new TestInstanceRenderer());
      register(registry, BlockEntityTypes.SHULKER_BOX, ShulkerBoxRenderer::new);
      register(registry, BlockEntityTypes.CONDUIT, ConduitRenderer::new);
      register(registry, BlockEntityTypes.BELL, BellRenderer::new);
      register(registry, BlockEntityTypes.CAMPFIRE, CampfireRenderer::new);
      register(registry, BlockEntityTypes.BRUSHABLE_BLOCK, BrushableBlockRenderer::new);
      register(registry, BlockEntityTypes.DECORATED_POT, DecoratedPotRenderer::new);
      register(registry, BlockEntityTypes.TRIAL_SPAWNER, TrialSpawnerRenderer::new);
      register(registry, BlockEntityTypes.VAULT, VaultRenderer::new);
      register(registry, BlockEntityTypes.COPPER_GOLEM_STATUE, CopperGolemStatueBlockRenderer::new);
      return register(registry, BlockEntityTypes.SHELF, ShelfRenderer::new);
   }

   private static <T extends BlockEntity, S extends BlockEntityRenderState> BlockEntityRendererProvider<?, ?> register(final Registry<BlockEntityRendererProvider<?, ?>> registry, final BlockEntityType<? extends T> type, final BlockEntityRendererProvider<T, S> renderer) {
      return (BlockEntityRendererProvider)Registry.register(registry, (ResourceKey)blockEntityKey(type), renderer);
   }

   private static ResourceKey<BlockEntityRendererProvider<?, ?>> blockEntityKey(final BlockEntityType<?> type) {
      return ResourceKey.create(ClientRegistries.BLOCK_ENTITY_RENDERER, (Identifier)Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type), () -> "Unregistered block entity type: " + String.valueOf(type)));
   }

   public static Map<BlockEntityType<?>, BlockEntityRenderer<?, ?>> createEntityRenderers(final BlockEntityRendererProvider.Context context) {
      ImmutableMap.Builder<BlockEntityType<?>, BlockEntityRenderer<?, ?>> result = ImmutableMap.builder();

      for(BlockEntityType<?> type : BuiltInRegistries.BLOCK_ENTITY_TYPE) {
         ResourceKey<BlockEntityRendererProvider<?, ?>> key = blockEntityKey(type);
         BlockEntityRendererProvider<?, ?> provider = (BlockEntityRendererProvider)BuiltInClientRegistries.BLOCK_ENTITY_RENDERER.getValue(key);
         if (provider != null) {
            try {
               result.put(type, provider.create(context));
            } catch (Exception e) {
               throw new IllegalStateException("Failed to create model for " + String.valueOf(key.identifier()), e);
            }
         }
      }

      return result.build();
   }
}

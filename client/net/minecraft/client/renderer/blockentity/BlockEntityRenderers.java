package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityRenderers {
   private static final Map<BlockEntityType<?>, BlockEntityRendererProvider<?, ?>> PROVIDERS = Maps.newHashMap();

   public BlockEntityRenderers() {
      super();
   }

   private static <T extends BlockEntity, S extends BlockEntityRenderState> void register(final BlockEntityType<? extends T> type, final BlockEntityRendererProvider<T, S> renderer) {
      PROVIDERS.put(type, renderer);
   }

   public static Map<BlockEntityType<?>, BlockEntityRenderer<?, ?>> createEntityRenderers(final BlockEntityRendererProvider.Context context) {
      ImmutableMap.Builder<BlockEntityType<?>, BlockEntityRenderer<?, ?>> result = ImmutableMap.builder();
      PROVIDERS.forEach((type, provider) -> {
         try {
            result.put(type, provider.create(context));
         } catch (Exception e) {
            throw new IllegalStateException("Failed to create model for " + String.valueOf(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type)), e);
         }
      });
      return result.build();
   }

   static {
      register(BlockEntityType.SIGN, StandingSignRenderer::new);
      register(BlockEntityType.HANGING_SIGN, HangingSignRenderer::new);
      register(BlockEntityType.MOB_SPAWNER, SpawnerRenderer::new);
      register(BlockEntityType.PISTON, (var0) -> new PistonHeadRenderer());
      register(BlockEntityType.CHEST, ChestRenderer::new);
      register(BlockEntityType.ENDER_CHEST, ChestRenderer::new);
      register(BlockEntityType.TRAPPED_CHEST, ChestRenderer::new);
      register(BlockEntityType.ENCHANTING_TABLE, EnchantTableRenderer::new);
      register(BlockEntityType.LECTERN, LecternRenderer::new);
      register(BlockEntityType.END_PORTAL, (var0) -> new TheEndPortalRenderer());
      register(BlockEntityType.END_GATEWAY, (var0) -> new TheEndGatewayRenderer());
      register(BlockEntityType.BEACON, (var0) -> new BeaconRenderer());
      register(BlockEntityType.SKULL, SkullBlockRenderer::new);
      register(BlockEntityType.BANNER, BannerRenderer::new);
      register(BlockEntityType.STRUCTURE_BLOCK, (var0) -> new BlockEntityWithBoundingBoxRenderer());
      register(BlockEntityType.TEST_INSTANCE_BLOCK, (var0) -> new TestInstanceRenderer());
      register(BlockEntityType.SHULKER_BOX, ShulkerBoxRenderer::new);
      register(BlockEntityType.BED, BedRenderer::new);
      register(BlockEntityType.CONDUIT, ConduitRenderer::new);
      register(BlockEntityType.BELL, BellRenderer::new);
      register(BlockEntityType.CAMPFIRE, CampfireRenderer::new);
      register(BlockEntityType.BRUSHABLE_BLOCK, BrushableBlockRenderer::new);
      register(BlockEntityType.DECORATED_POT, DecoratedPotRenderer::new);
      register(BlockEntityType.TRIAL_SPAWNER, TrialSpawnerRenderer::new);
      register(BlockEntityType.VAULT, VaultRenderer::new);
      register(BlockEntityType.COPPER_GOLEM_STATUE, CopperGolemStatueBlockRenderer::new);
      register(BlockEntityType.SHELF, ShelfRenderer::new);
   }
}

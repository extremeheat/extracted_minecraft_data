package net.minecraft.client.renderer.special;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.core.Registry;

public class SpecialModelRenderers {
   public static final Codec<SpecialModelRenderer.Unbaked<?>> CODEC;

   public SpecialModelRenderers() {
      super();
   }

   public static Object bootstrap(final Registry<MapCodec<? extends SpecialModelRenderer.Unbaked<?>>> registry) {
      Registry.register(registry, (String)"bell", BellSpecialRenderer.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"banner", BannerSpecialRenderer.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"book", BookSpecialRenderer.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"conduit", ConduitSpecialRenderer.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"chest", ChestSpecialRenderer.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"copper_golem_statue", CopperGolemStatueSpecialRenderer.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"head", SkullSpecialRenderer.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"player_head", PlayerHeadSpecialRenderer.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"shulker_box", ShulkerBoxSpecialRenderer.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"shield", ShieldSpecialRenderer.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"trident", TridentSpecialRenderer.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"decorated_pot", DecoratedPotSpecialRenderer.Unbaked.MAP_CODEC);
      return Registry.register(registry, (String)"end_cube", EndCubeSpecialRenderer.Unbaked.MAP_CODEC);
   }

   static {
      CODEC = BuiltInClientRegistries.SPECIAL_MODEL_RENDERER_TYPE.byNameCodec().dispatch(SpecialModelRenderer.Unbaked::type, (c) -> c);
   }
}

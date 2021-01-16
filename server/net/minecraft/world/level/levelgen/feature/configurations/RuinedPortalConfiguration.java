package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.RuinedPortalFeature;

public class RuinedPortalConfiguration implements FeatureConfiguration {
   public static final Codec<RuinedPortalConfiguration> CODEC;
   public final RuinedPortalFeature.Type portalType;

   public RuinedPortalConfiguration(RuinedPortalFeature.Type var1) {
      super();
      this.portalType = var1;
   }

   static {
      CODEC = RuinedPortalFeature.Type.CODEC.fieldOf("portal_type").xmap(RuinedPortalConfiguration::new, (var0) -> {
         return var0.portalType;
      }).codec();
   }
}

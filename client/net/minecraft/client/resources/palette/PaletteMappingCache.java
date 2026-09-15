package net.minecraft.client.resources.palette;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

public class PaletteMappingCache {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final LoadingCache<IdPair, PaletteMapping> cache;

   public PaletteMappingCache(final Map<Identifier, Palette> palettes) {
      super();
      this.cache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(5L)).maximumSize(256L).build(new CacheLoader<IdPair, PaletteMapping>() {
         {
            Objects.requireNonNull(PaletteMappingCache.this);
         }

         public PaletteMapping load(final IdPair pair) {
            Palette basePalette = (Palette)palettes.get(pair.base);
            Palette targetPalette = (Palette)palettes.get(pair.target);
            if (basePalette == null) {
               PaletteMappingCache.LOGGER.warn("Couldn't find base palette {}", pair.base);
               return PaletteMapping.NONE;
            } else if (targetPalette == null) {
               PaletteMappingCache.LOGGER.warn("Couldn't find target palette {}", pair.target);
               return PaletteMapping.NONE;
            } else if (basePalette.size() != targetPalette.size()) {
               PaletteMappingCache.LOGGER.warn("Could not create palette mapping for {}, had different sizes", pair);
               return PaletteMapping.NONE;
            } else {
               return PaletteMapping.create(basePalette, targetPalette);
            }
         }
      });
   }

   public PaletteMapping get(final Identifier baseId, final Identifier targetId) {
      return (PaletteMapping)this.cache.getUnchecked(new IdPair(baseId, targetId));
   }

   private static record IdPair(Identifier base, Identifier target) {
      private IdPair {
         super();
      }
   }
}

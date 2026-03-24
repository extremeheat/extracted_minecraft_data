package net.minecraft.world.level.chunk;

import net.minecraft.core.IdMap;
import net.minecraft.util.Mth;

public abstract class Strategy<T> {
   private static final Palette.Factory SINGLE_VALUE_PALETTE_FACTORY = SingleValuePalette::create;
   private static final Palette.Factory LINEAR_PALETTE_FACTORY = LinearPalette::create;
   private static final Palette.Factory HASHMAP_PALETTE_FACTORY = HashMapPalette::create;
   private static final Configuration ZERO_BITS;
   private static final Configuration ONE_BIT_LINEAR;
   private static final Configuration TWO_BITS_LINEAR;
   private static final Configuration THREE_BITS_LINEAR;
   private static final Configuration FOUR_BITS_LINEAR;
   private static final Configuration FIVE_BITS_HASHMAP;
   private static final Configuration SIX_BITS_HASHMAP;
   private static final Configuration SEVEN_BITS_HASHMAP;
   private static final Configuration EIGHT_BITS_HASHMAP;
   private final IdMap<T> globalMap;
   private final GlobalPalette<T> globalPalette;
   protected final int globalPaletteBitsInMemory;
   private final int bitsPerAxis;
   private final int entryCount;

   private Strategy(final IdMap<T> globalMap, final int bitsPerAxis) {
      super();
      this.globalMap = globalMap;
      this.globalPalette = new GlobalPalette<T>(globalMap);
      this.globalPaletteBitsInMemory = minimumBitsRequiredForDistinctValues(globalMap.size());
      this.bitsPerAxis = bitsPerAxis;
      this.entryCount = 1 << bitsPerAxis * 3;
   }

   public static <T> Strategy<T> createForBlockStates(final IdMap<T> registry) {
      return new Strategy<T>(registry, 4) {
         public Configuration getConfigurationForBitCount(final int entryBits) {
            Object var10000;
            switch (entryBits) {
               case 0:
                  var10000 = Strategy.ZERO_BITS;
                  break;
               case 1:
               case 2:
               case 3:
               case 4:
                  var10000 = Strategy.FOUR_BITS_LINEAR;
                  break;
               case 5:
                  var10000 = Strategy.FIVE_BITS_HASHMAP;
                  break;
               case 6:
                  var10000 = Strategy.SIX_BITS_HASHMAP;
                  break;
               case 7:
                  var10000 = Strategy.SEVEN_BITS_HASHMAP;
                  break;
               case 8:
                  var10000 = Strategy.EIGHT_BITS_HASHMAP;
                  break;
               default:
                  var10000 = new Configuration.Global(this.globalPaletteBitsInMemory, entryBits);
            }

            return (Configuration)var10000;
         }
      };
   }

   public static <T> Strategy<T> createForBiomes(final IdMap<T> registry) {
      return new Strategy<T>(registry, 2) {
         public Configuration getConfigurationForBitCount(final int entryBits) {
            Object var10000;
            switch (entryBits) {
               case 0 -> var10000 = Strategy.ZERO_BITS;
               case 1 -> var10000 = Strategy.ONE_BIT_LINEAR;
               case 2 -> var10000 = Strategy.TWO_BITS_LINEAR;
               case 3 -> var10000 = Strategy.THREE_BITS_LINEAR;
               default -> var10000 = new Configuration.Global(this.globalPaletteBitsInMemory, entryBits);
            }

            return (Configuration)var10000;
         }
      };
   }

   public int entryCount() {
      return this.entryCount;
   }

   public int getIndex(final int x, final int y, final int z) {
      return (y << this.bitsPerAxis | z) << this.bitsPerAxis | x;
   }

   public IdMap<T> globalMap() {
      return this.globalMap;
   }

   public GlobalPalette<T> globalPalette() {
      return this.globalPalette;
   }

   protected abstract Configuration getConfigurationForBitCount(int entryBits);

   protected Configuration getConfigurationForPaletteSize(final int paletteSize) {
      int bits = minimumBitsRequiredForDistinctValues(paletteSize);
      return this.getConfigurationForBitCount(bits);
   }

   private static int minimumBitsRequiredForDistinctValues(final int count) {
      return Mth.ceillog2(count);
   }

   static {
      ZERO_BITS = new Configuration.Simple(SINGLE_VALUE_PALETTE_FACTORY, 0);
      ONE_BIT_LINEAR = new Configuration.Simple(LINEAR_PALETTE_FACTORY, 1);
      TWO_BITS_LINEAR = new Configuration.Simple(LINEAR_PALETTE_FACTORY, 2);
      THREE_BITS_LINEAR = new Configuration.Simple(LINEAR_PALETTE_FACTORY, 3);
      FOUR_BITS_LINEAR = new Configuration.Simple(LINEAR_PALETTE_FACTORY, 4);
      FIVE_BITS_HASHMAP = new Configuration.Simple(HASHMAP_PALETTE_FACTORY, 5);
      SIX_BITS_HASHMAP = new Configuration.Simple(HASHMAP_PALETTE_FACTORY, 6);
      SEVEN_BITS_HASHMAP = new Configuration.Simple(HASHMAP_PALETTE_FACTORY, 7);
      EIGHT_BITS_HASHMAP = new Configuration.Simple(HASHMAP_PALETTE_FACTORY, 8);
   }
}

package net.minecraft.world.level.chunk;

import net.minecraft.core.IdMap;
import net.minecraft.util.Mth;

public abstract class Strategy<T> {
   private static final Palette.Factory SINGLE_VALUE_PALETTE_FACTORY = SingleValuePalette::create;
   private static final Palette.Factory LINEAR_PALETTE_FACTORY = LinearPalette::create;
   private static final Palette.Factory HASHMAP_PALETTE_FACTORY = HashMapPalette::create;
   static final Configuration ZERO_BITS;
   static final Configuration ONE_BIT_LINEAR;
   static final Configuration TWO_BITS_LINEAR;
   static final Configuration THREE_BITS_LINEAR;
   static final Configuration FOUR_BITS_LINEAR;
   static final Configuration FIVE_BITS_HASHMAP;
   static final Configuration SIX_BITS_HASHMAP;
   static final Configuration SEVEN_BITS_HASHMAP;
   static final Configuration EIGHT_BITS_HASHMAP;
   private final IdMap<T> globalMap;
   private final GlobalPalette<T> globalPalette;
   protected final int globalPaletteBitsInMemory;
   private final int bitsPerAxis;
   private final int entryCount;

   Strategy(IdMap<T> var1, int var2) {
      super();
      this.globalMap = var1;
      this.globalPalette = new GlobalPalette<T>(var1);
      this.globalPaletteBitsInMemory = minimumBitsRequiredForDistinctValues(var1.size());
      this.bitsPerAxis = var2;
      this.entryCount = 1 << var2 * 3;
   }

   public static <T> Strategy<T> createForBlockStates(IdMap<T> var0) {
      return new Strategy<T>(var0, 4) {
         public Configuration getConfigurationForBitCount(int var1) {
            Object var10000;
            switch (var1) {
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
                  var10000 = new Configuration.Global(this.globalPaletteBitsInMemory, var1);
            }

            return (Configuration)var10000;
         }
      };
   }

   public static <T> Strategy<T> createForBiomes(IdMap<T> var0) {
      return new Strategy<T>(var0, 2) {
         public Configuration getConfigurationForBitCount(int var1) {
            Object var10000;
            switch (var1) {
               case 0 -> var10000 = Strategy.ZERO_BITS;
               case 1 -> var10000 = Strategy.ONE_BIT_LINEAR;
               case 2 -> var10000 = Strategy.TWO_BITS_LINEAR;
               case 3 -> var10000 = Strategy.THREE_BITS_LINEAR;
               default -> var10000 = new Configuration.Global(this.globalPaletteBitsInMemory, var1);
            }

            return (Configuration)var10000;
         }
      };
   }

   public int entryCount() {
      return this.entryCount;
   }

   public int getIndex(int var1, int var2, int var3) {
      return (var2 << this.bitsPerAxis | var3) << this.bitsPerAxis | var1;
   }

   public IdMap<T> globalMap() {
      return this.globalMap;
   }

   public GlobalPalette<T> globalPalette() {
      return this.globalPalette;
   }

   protected abstract Configuration getConfigurationForBitCount(int var1);

   protected Configuration getConfigurationForPaletteSize(int var1) {
      int var2 = minimumBitsRequiredForDistinctValues(var1);
      return this.getConfigurationForBitCount(var2);
   }

   private static int minimumBitsRequiredForDistinctValues(int var0) {
      return Mth.ceillog2(var0);
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

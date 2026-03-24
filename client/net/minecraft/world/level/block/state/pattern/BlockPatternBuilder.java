package net.minecraft.world.level.block.state.pattern;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

public class BlockPatternBuilder {
   private final List<String[]> pattern = Lists.newArrayList();
   private final Map<Character, Predicate<@Nullable BlockInWorld>> lookup = Maps.newHashMap();
   private int height;
   private int width;
   private final CharSet unknownCharacters = new CharOpenHashSet();

   private BlockPatternBuilder() {
      super();
      this.lookup.put(' ', (Predicate)(blockInWorld) -> true);
   }

   public BlockPatternBuilder aisle(final String... aisle) {
      if (!ArrayUtils.isEmpty(aisle) && !StringUtils.isEmpty(aisle[0])) {
         if (this.pattern.isEmpty()) {
            this.height = aisle.length;
            this.width = aisle[0].length();
         }

         if (aisle.length != this.height) {
            throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + aisle.length + ")");
         } else {
            for(String row : aisle) {
               if (row.length() != this.width) {
                  int var10002 = this.width;
                  throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + var10002 + ", found one with " + row.length() + ")");
               }

               for(char c : row.toCharArray()) {
                  if (!this.lookup.containsKey(c)) {
                     this.unknownCharacters.add(c);
                  }
               }
            }

            this.pattern.add(aisle);
            return this;
         }
      } else {
         throw new IllegalArgumentException("Empty pattern for aisle");
      }
   }

   public static BlockPatternBuilder start() {
      return new BlockPatternBuilder();
   }

   public BlockPatternBuilder where(final char character, final Predicate<@Nullable BlockInWorld> predicate) {
      this.lookup.put(character, predicate);
      this.unknownCharacters.remove(character);
      return this;
   }

   public BlockPattern build() {
      return new BlockPattern(this.createPattern());
   }

   private Predicate<BlockInWorld>[][][] createPattern() {
      if (!this.unknownCharacters.isEmpty()) {
         throw new IllegalStateException("Predicates for character(s) " + String.valueOf(this.unknownCharacters) + " are missing");
      } else {
         Predicate<BlockInWorld>[][][] result = (Predicate[][][])Array.newInstance(Predicate.class, new int[]{this.pattern.size(), this.height, this.width});

         for(int aisle = 0; aisle < this.pattern.size(); ++aisle) {
            for(int row = 0; row < this.height; ++row) {
               for(int col = 0; col < this.width; ++col) {
                  result[aisle][row][col] = (Predicate)this.lookup.get(((String[])this.pattern.get(aisle))[row].charAt(col));
               }
            }
         }

         return result;
      }
   }
}

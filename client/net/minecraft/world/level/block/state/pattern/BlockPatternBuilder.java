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
      this.lookup.put(' ', (Predicate)(var0) -> true);
   }

   public BlockPatternBuilder aisle(String... var1) {
      if (!ArrayUtils.isEmpty(var1) && !StringUtils.isEmpty(var1[0])) {
         if (this.pattern.isEmpty()) {
            this.height = var1.length;
            this.width = var1[0].length();
         }

         if (var1.length != this.height) {
            throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + var1.length + ")");
         } else {
            for(String var5 : var1) {
               if (var5.length() != this.width) {
                  int var10002 = this.width;
                  throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + var10002 + ", found one with " + var5.length() + ")");
               }

               for(char var9 : var5.toCharArray()) {
                  if (!this.lookup.containsKey(var9)) {
                     this.unknownCharacters.add(var9);
                  }
               }
            }

            this.pattern.add(var1);
            return this;
         }
      } else {
         throw new IllegalArgumentException("Empty pattern for aisle");
      }
   }

   public static BlockPatternBuilder start() {
      return new BlockPatternBuilder();
   }

   public BlockPatternBuilder where(char var1, Predicate<@Nullable BlockInWorld> var2) {
      this.lookup.put(var1, var2);
      this.unknownCharacters.remove(var1);
      return this;
   }

   public BlockPattern build() {
      return new BlockPattern(this.createPattern());
   }

   private Predicate<BlockInWorld>[][][] createPattern() {
      if (!this.unknownCharacters.isEmpty()) {
         throw new IllegalStateException("Predicates for character(s) " + String.valueOf(this.unknownCharacters) + " are missing");
      } else {
         Predicate[][][] var1 = (Predicate[][][])Array.newInstance(Predicate.class, new int[]{this.pattern.size(), this.height, this.width});

         for(int var2 = 0; var2 < this.pattern.size(); ++var2) {
            for(int var3 = 0; var3 < this.height; ++var3) {
               for(int var4 = 0; var4 < this.width; ++var4) {
                  var1[var2][var3][var4] = (Predicate)this.lookup.get(((String[])this.pattern.get(var2))[var3].charAt(var4));
               }
            }
         }

         return var1;
      }
   }
}

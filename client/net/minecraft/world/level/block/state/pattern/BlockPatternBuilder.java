package net.minecraft.world.level.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class BlockPatternBuilder {
   private static final Joiner COMMA_JOINED = Joiner.on(",");
   private final List<String[]> pattern = Lists.newArrayList();
   private final Map<Character, Predicate<BlockInWorld>> lookup = Maps.newHashMap();
   private int height;
   private int width;

   private BlockPatternBuilder() {
      super();
      this.lookup.put(' ', (var0) -> {
         return true;
      });
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
            String[] var2 = var1;
            int var3 = var1.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               String var5 = var2[var4];
               if (var5.length() != this.width) {
                  int var10002 = this.width;
                  throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + var10002 + ", found one with " + var5.length() + ")");
               }

               char[] var6 = var5.toCharArray();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  char var9 = var6[var8];
                  if (!this.lookup.containsKey(var9)) {
                     this.lookup.put(var9, (Object)null);
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

   public BlockPatternBuilder where(char var1, Predicate<BlockInWorld> var2) {
      this.lookup.put(var1, var2);
      return this;
   }

   public BlockPattern build() {
      return new BlockPattern(this.createPattern());
   }

   private Predicate<BlockInWorld>[][][] createPattern() {
      this.ensureAllCharactersMatched();
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

   private void ensureAllCharactersMatched() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.lookup.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (var3.getValue() == null) {
            var1.add((Character)var3.getKey());
         }
      }

      if (!var1.isEmpty()) {
         throw new IllegalStateException("Predicates for character(s) " + COMMA_JOINED.join(var1) + " are missing");
      }
   }
}

package net.minecraft.advancements;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public record AdvancementRequirements(String[][] b) {
   private final String[][] requirements;
   public static final AdvancementRequirements EMPTY = new AdvancementRequirements(new String[0][]);

   public AdvancementRequirements(FriendlyByteBuf var1) {
      this(read(var1));
   }

   public AdvancementRequirements(String[][] var1) {
      super();
      this.requirements = var1;
   }

   private static String[][] read(FriendlyByteBuf var0) {
      String[][] var1 = new String[var0.readVarInt()][];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = new String[var0.readVarInt()];

         for(int var3 = 0; var3 < var1[var2].length; ++var3) {
            var1[var2][var3] = var0.readUtf();
         }
      }

      return var1;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.requirements.length);

      for(String[] var5 : this.requirements) {
         var1.writeVarInt(var5.length);

         for(String var9 : var5) {
            var1.writeUtf(var9);
         }
      }
   }

   public static AdvancementRequirements allOf(Collection<String> var0) {
      return new AdvancementRequirements(var0.stream().map(var0x -> new String[]{var0x}).toArray(var0x -> new String[var0x][]));
   }

   public static AdvancementRequirements anyOf(Collection<String> var0) {
      return new AdvancementRequirements(new String[][]{var0.toArray(var0x -> new String[var0x])});
   }

   public int size() {
      return this.requirements.length;
   }

   public boolean test(Predicate<String> var1) {
      if (this.requirements.length == 0) {
         return false;
      } else {
         for(String[] var5 : this.requirements) {
            if (!anyMatch(var5, var1)) {
               return false;
            }
         }

         return true;
      }
   }

   public int count(Predicate<String> var1) {
      int var2 = 0;

      for(String[] var6 : this.requirements) {
         if (anyMatch(var6, var1)) {
            ++var2;
         }
      }

      return var2;
   }

   private static boolean anyMatch(String[] var0, Predicate<String> var1) {
      for(String var5 : var0) {
         if (var1.test(var5)) {
            return true;
         }
      }

      return false;
   }

   public static AdvancementRequirements fromJson(JsonArray var0, Set<String> var1) {
      String[][] var2 = new String[var0.size()][];
      ObjectOpenHashSet var3 = new ObjectOpenHashSet();

      for(int var4 = 0; var4 < var0.size(); ++var4) {
         JsonArray var5 = GsonHelper.convertToJsonArray(var0.get(var4), "requirements[" + var4 + "]");
         if (var5.isEmpty() && var1.isEmpty()) {
            throw new JsonSyntaxException("Requirement entry cannot be empty");
         }

         var2[var4] = new String[var5.size()];

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            String var7 = GsonHelper.convertToString(var5.get(var6), "requirements[" + var4 + "][" + var6 + "]");
            var2[var4][var6] = var7;
            var3.add(var7);
         }
      }

      if (!var1.equals(var3)) {
         SetView var8 = Sets.difference(var1, var3);
         SetView var9 = Sets.difference(var3, var1);
         throw new JsonSyntaxException("Advancement completion requirements did not exactly match specified criteria. Missing: " + var8 + ". Unknown: " + var9);
      } else {
         return new AdvancementRequirements(var2);
      }
   }

   public JsonArray toJson() {
      JsonArray var1 = new JsonArray();

      for(String[] var5 : this.requirements) {
         JsonArray var6 = new JsonArray();
         Arrays.stream(var5).forEach(var6::add);
         var1.add(var6);
      }

      return var1;
   }

   public boolean isEmpty() {
      return this.requirements.length == 0;
   }

   @Override
   public String toString() {
      return Arrays.deepToString(this.requirements);
   }

   public Set<String> names() {
      ObjectOpenHashSet var1 = new ObjectOpenHashSet();

      for(String[] var5 : this.requirements) {
         Collections.addAll(var1, var5);
      }

      return var1;
   }

   public interface Strategy {
      AdvancementRequirements.Strategy AND = AdvancementRequirements::allOf;
      AdvancementRequirements.Strategy OR = AdvancementRequirements::anyOf;

      AdvancementRequirements create(Collection<String> var1);
   }
}

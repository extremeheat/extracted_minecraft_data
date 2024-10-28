package net.minecraft.advancements;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.network.FriendlyByteBuf;

public record AdvancementRequirements(List<List<String>> requirements) {
   public static final Codec<AdvancementRequirements> CODEC;
   public static final AdvancementRequirements EMPTY;

   public AdvancementRequirements(FriendlyByteBuf var1) {
      this(var1.readList((var0) -> {
         return var0.readList(FriendlyByteBuf::readUtf);
      }));
   }

   public AdvancementRequirements(List<List<String>> var1) {
      super();
      this.requirements = var1;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeCollection(this.requirements, (var0, var1x) -> {
         var0.writeCollection(var1x, FriendlyByteBuf::writeUtf);
      });
   }

   public static AdvancementRequirements allOf(Collection<String> var0) {
      return new AdvancementRequirements(var0.stream().map(List::of).toList());
   }

   public static AdvancementRequirements anyOf(Collection<String> var0) {
      return new AdvancementRequirements(List.of(List.copyOf(var0)));
   }

   public int size() {
      return this.requirements.size();
   }

   public boolean test(Predicate<String> var1) {
      if (this.requirements.isEmpty()) {
         return false;
      } else {
         Iterator var2 = this.requirements.iterator();

         List var3;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (List)var2.next();
         } while(anyMatch(var3, var1));

         return false;
      }
   }

   public int count(Predicate<String> var1) {
      int var2 = 0;
      Iterator var3 = this.requirements.iterator();

      while(var3.hasNext()) {
         List var4 = (List)var3.next();
         if (anyMatch(var4, var1)) {
            ++var2;
         }
      }

      return var2;
   }

   private static boolean anyMatch(List<String> var0, Predicate<String> var1) {
      Iterator var2 = var0.iterator();

      String var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (String)var2.next();
      } while(!var1.test(var3));

      return true;
   }

   public DataResult<AdvancementRequirements> validate(Set<String> var1) {
      ObjectOpenHashSet var2 = new ObjectOpenHashSet();
      Iterator var3 = this.requirements.iterator();

      while(var3.hasNext()) {
         List var4 = (List)var3.next();
         if (var4.isEmpty() && var1.isEmpty()) {
            return DataResult.error(() -> {
               return "Requirement entry cannot be empty";
            });
         }

         var2.addAll(var4);
      }

      if (!var1.equals(var2)) {
         Sets.SetView var5 = Sets.difference(var1, var2);
         Sets.SetView var6 = Sets.difference(var2, var1);
         return DataResult.error(() -> {
            String var10000 = String.valueOf(var5);
            return "Advancement completion requirements did not exactly match specified criteria. Missing: " + var10000 + ". Unknown: " + String.valueOf(var6);
         });
      } else {
         return DataResult.success(this);
      }
   }

   public boolean isEmpty() {
      return this.requirements.isEmpty();
   }

   public String toString() {
      return this.requirements.toString();
   }

   public Set<String> names() {
      ObjectOpenHashSet var1 = new ObjectOpenHashSet();
      Iterator var2 = this.requirements.iterator();

      while(var2.hasNext()) {
         List var3 = (List)var2.next();
         var1.addAll(var3);
      }

      return var1;
   }

   public List<List<String>> requirements() {
      return this.requirements;
   }

   static {
      CODEC = Codec.STRING.listOf().listOf().xmap(AdvancementRequirements::new, AdvancementRequirements::requirements);
      EMPTY = new AdvancementRequirements(List.of());
   }

   public interface Strategy {
      Strategy AND = AdvancementRequirements::allOf;
      Strategy OR = AdvancementRequirements::anyOf;

      AdvancementRequirements create(Collection<String> var1);
   }
}

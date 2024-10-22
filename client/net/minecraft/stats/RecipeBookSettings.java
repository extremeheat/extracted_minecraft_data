package net.minecraft.stats;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.RecipeBookType;

public final class RecipeBookSettings {
   public static final StreamCodec<FriendlyByteBuf, RecipeBookSettings> STREAM_CODEC = StreamCodec.ofMember(RecipeBookSettings::write, RecipeBookSettings::read);
   private static final Map<RecipeBookType, Pair<String, String>> TAG_FIELDS = ImmutableMap.of(
      RecipeBookType.CRAFTING,
      Pair.of("isGuiOpen", "isFilteringCraftable"),
      RecipeBookType.FURNACE,
      Pair.of("isFurnaceGuiOpen", "isFurnaceFilteringCraftable"),
      RecipeBookType.BLAST_FURNACE,
      Pair.of("isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable"),
      RecipeBookType.SMOKER,
      Pair.of("isSmokerGuiOpen", "isSmokerFilteringCraftable")
   );
   private final Map<RecipeBookType, RecipeBookSettings.TypeSettings> states;

   private RecipeBookSettings(Map<RecipeBookType, RecipeBookSettings.TypeSettings> var1) {
      super();
      this.states = var1;
   }

   public RecipeBookSettings() {
      this(new EnumMap<>(RecipeBookType.class));
   }

   private RecipeBookSettings.TypeSettings getSettings(RecipeBookType var1) {
      return this.states.getOrDefault(var1, RecipeBookSettings.TypeSettings.DEFAULT);
   }

   private void updateSettings(RecipeBookType var1, UnaryOperator<RecipeBookSettings.TypeSettings> var2) {
      this.states.compute(var1, (var1x, var2x) -> {
         if (var2x == null) {
            var2x = RecipeBookSettings.TypeSettings.DEFAULT;
         }

         var2x = var2.apply(var2x);
         if (var2x.equals(RecipeBookSettings.TypeSettings.DEFAULT)) {
            var2x = null;
         }

         return var2x;
      });
   }

   public boolean isOpen(RecipeBookType var1) {
      return this.getSettings(var1).open;
   }

   public void setOpen(RecipeBookType var1, boolean var2) {
      this.updateSettings(var1, var1x -> var1x.setOpen(var2));
   }

   public boolean isFiltering(RecipeBookType var1) {
      return this.getSettings(var1).filtering;
   }

   public void setFiltering(RecipeBookType var1, boolean var2) {
      this.updateSettings(var1, var1x -> var1x.setFiltering(var2));
   }

   private static RecipeBookSettings read(FriendlyByteBuf var0) {
      EnumMap var1 = new EnumMap<>(RecipeBookType.class);

      for (RecipeBookType var5 : RecipeBookType.values()) {
         boolean var6 = var0.readBoolean();
         boolean var7 = var0.readBoolean();
         if (var6 || var7) {
            var1.put(var5, new RecipeBookSettings.TypeSettings(var6, var7));
         }
      }

      return new RecipeBookSettings(var1);
   }

   private void write(FriendlyByteBuf var1) {
      for (RecipeBookType var5 : RecipeBookType.values()) {
         RecipeBookSettings.TypeSettings var6 = this.states.getOrDefault(var5, RecipeBookSettings.TypeSettings.DEFAULT);
         var1.writeBoolean(var6.open);
         var1.writeBoolean(var6.filtering);
      }
   }

   public static RecipeBookSettings read(CompoundTag var0) {
      EnumMap var1 = new EnumMap<>(RecipeBookType.class);
      TAG_FIELDS.forEach((var2, var3) -> {
         boolean var4 = var0.getBoolean((String)var3.getFirst());
         boolean var5 = var0.getBoolean((String)var3.getSecond());
         if (var4 || var5) {
            var1.put(var2, new RecipeBookSettings.TypeSettings(var4, var5));
         }
      });
      return new RecipeBookSettings(var1);
   }

   public void write(CompoundTag var1) {
      TAG_FIELDS.forEach((var2, var3) -> {
         RecipeBookSettings.TypeSettings var4 = this.states.getOrDefault(var2, RecipeBookSettings.TypeSettings.DEFAULT);
         var1.putBoolean((String)var3.getFirst(), var4.open);
         var1.putBoolean((String)var3.getSecond(), var4.filtering);
      });
   }

   public RecipeBookSettings copy() {
      return new RecipeBookSettings(new EnumMap<>(this.states));
   }

   public void replaceFrom(RecipeBookSettings var1) {
      this.states.clear();
      this.states.putAll(var1.states);
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof RecipeBookSettings && this.states.equals(((RecipeBookSettings)var1).states);
   }

   @Override
   public int hashCode() {
      return this.states.hashCode();
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}

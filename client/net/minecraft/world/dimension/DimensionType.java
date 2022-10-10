package net.minecraft.world.dimension;

import java.io.File;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class DimensionType {
   public static final DimensionType OVERWORLD = func_212677_a("overworld", new DimensionType(1, "", "", OverworldDimension::new));
   public static final DimensionType NETHER = func_212677_a("the_nether", new DimensionType(0, "_nether", "DIM-1", NetherDimension::new));
   public static final DimensionType THE_END = func_212677_a("the_end", new DimensionType(2, "_end", "DIM1", EndDimension::new));
   private final int field_186074_d;
   private final String field_186076_f;
   private final String field_212682_f;
   private final Supplier<? extends Dimension> field_201038_g;

   public static void func_212680_a() {
   }

   private static DimensionType func_212677_a(String var0, DimensionType var1) {
      IRegistry.field_212622_k.func_177775_a(var1.field_186074_d, new ResourceLocation(var0), var1);
      return var1;
   }

   protected DimensionType(int var1, String var2, String var3, Supplier<? extends Dimension> var4) {
      super();
      this.field_186074_d = var1;
      this.field_186076_f = var2;
      this.field_212682_f = var3;
      this.field_201038_g = var4;
   }

   public static Iterable<DimensionType> func_212681_b() {
      return IRegistry.field_212622_k;
   }

   public int func_186068_a() {
      return this.field_186074_d + -1;
   }

   public String func_186067_c() {
      return this.field_186076_f;
   }

   public File func_212679_a(File var1) {
      return this.field_212682_f.isEmpty() ? var1 : new File(var1, this.field_212682_f);
   }

   public Dimension func_186070_d() {
      return (Dimension)this.field_201038_g.get();
   }

   public String toString() {
      return func_212678_a(this).toString();
   }

   @Nullable
   public static DimensionType func_186069_a(int var0) {
      return (DimensionType)IRegistry.field_212622_k.func_148754_a(var0 - -1);
   }

   @Nullable
   public static DimensionType func_193417_a(ResourceLocation var0) {
      return (DimensionType)IRegistry.field_212622_k.func_212608_b(var0);
   }

   @Nullable
   public static ResourceLocation func_212678_a(DimensionType var0) {
      return IRegistry.field_212622_k.func_177774_c(var0);
   }
}

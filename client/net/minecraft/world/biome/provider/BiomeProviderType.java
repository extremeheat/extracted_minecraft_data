package net.minecraft.world.biome.provider;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class BiomeProviderType<C extends IBiomeProviderSettings, T extends BiomeProvider> {
   public static final BiomeProviderType<CheckerboardBiomeProviderSettings, CheckerboardBiomeProvider> field_205460_b = func_212581_a("checkerboard", CheckerboardBiomeProvider::new, CheckerboardBiomeProviderSettings::new);
   public static final BiomeProviderType<SingleBiomeProviderSettings, SingleBiomeProvider> field_205461_c = func_212581_a("fixed", SingleBiomeProvider::new, SingleBiomeProviderSettings::new);
   public static final BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider> field_206859_d = func_212581_a("vanilla_layered", OverworldBiomeProvider::new, OverworldBiomeProviderSettings::new);
   public static final BiomeProviderType<EndBiomeProviderSettings, EndBiomeProvider> field_205463_e = func_212581_a("the_end", EndBiomeProvider::new, EndBiomeProviderSettings::new);
   private final ResourceLocation field_205464_f;
   private final Function<C, T> field_205465_g;
   private final Supplier<C> field_205466_h;

   public static void func_212580_a() {
   }

   public BiomeProviderType(Function<C, T> var1, Supplier<C> var2, ResourceLocation var3) {
      super();
      this.field_205465_g = var1;
      this.field_205466_h = var2;
      this.field_205464_f = var3;
   }

   public static <C extends IBiomeProviderSettings, T extends BiomeProvider> BiomeProviderType<C, T> func_212581_a(String var0, Function<C, T> var1, Supplier<C> var2) {
      ResourceLocation var3 = new ResourceLocation(var0);
      BiomeProviderType var4 = new BiomeProviderType(var1, var2, var3);
      IRegistry.field_212625_n.func_82595_a(var3, var4);
      return var4;
   }

   public T func_205457_a(C var1) {
      return (BiomeProvider)this.field_205465_g.apply(var1);
   }

   public C func_205458_a() {
      return (IBiomeProviderSettings)this.field_205466_h.get();
   }

   public ResourceLocation func_206858_b() {
      return this.field_205464_f;
   }
}

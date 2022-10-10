package net.minecraft.world.gen;

import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;

public class ChunkGeneratorType<C extends IChunkGenSettings, T extends IChunkGenerator<C>> implements IChunkGeneratorFactory<C, T> {
   public static final ChunkGeneratorType<OverworldGenSettings, ChunkGeneratorOverworld> field_206911_b = func_212676_a("surface", ChunkGeneratorOverworld::new, OverworldGenSettings::new, true);
   public static final ChunkGeneratorType<NetherGenSettings, ChunkGeneratorNether> field_206912_c = func_212676_a("caves", ChunkGeneratorNether::new, NetherGenSettings::new, true);
   public static final ChunkGeneratorType<EndGenSettings, ChunkGeneratorEnd> field_206913_d = func_212676_a("floating_islands", ChunkGeneratorEnd::new, EndGenSettings::new, true);
   public static final ChunkGeneratorType<DebugGenSettings, ChunkGeneratorDebug> field_205488_e = func_212676_a("debug", ChunkGeneratorDebug::new, DebugGenSettings::new, false);
   public static final ChunkGeneratorType<FlatGenSettings, ChunkGeneratorFlat> field_205489_f = func_212676_a("flat", ChunkGeneratorFlat::new, FlatGenSettings::new, false);
   private final ResourceLocation field_205490_g;
   private final IChunkGeneratorFactory<C, T> field_205491_h;
   private final boolean field_205492_i;
   private final Supplier<C> field_205493_j;

   public static void func_212675_a() {
   }

   public ChunkGeneratorType(IChunkGeneratorFactory<C, T> var1, boolean var2, Supplier<C> var3, ResourceLocation var4) {
      super();
      this.field_205491_h = var1;
      this.field_205492_i = var2;
      this.field_205493_j = var3;
      this.field_205490_g = var4;
   }

   public static <C extends IChunkGenSettings, T extends IChunkGenerator<C>> ChunkGeneratorType<C, T> func_212676_a(String var0, IChunkGeneratorFactory<C, T> var1, Supplier<C> var2, boolean var3) {
      ResourceLocation var4 = new ResourceLocation(var0);
      ChunkGeneratorType var5 = new ChunkGeneratorType(var1, var3, var2, var4);
      IRegistry.field_212627_p.func_82595_a(var4, var5);
      return var5;
   }

   public T create(World var1, BiomeProvider var2, C var3) {
      return this.field_205491_h.create(var1, var2, var3);
   }

   public C func_205483_a() {
      return (IChunkGenSettings)this.field_205493_j.get();
   }

   public boolean func_205481_b() {
      return this.field_205492_i;
   }

   public ResourceLocation func_205482_c() {
      return this.field_205490_g;
   }
}

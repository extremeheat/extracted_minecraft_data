package net.minecraft.world.biome.provider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;

public abstract class BiomeProvider implements ITickable {
   private static final List<Biome> field_201540_a;
   protected final Map<Structure<?>, Boolean> field_205005_a = Maps.newHashMap();
   protected final Set<IBlockState> field_205707_b = Sets.newHashSet();

   protected BiomeProvider() {
      super();
   }

   public List<Biome> func_76932_a() {
      return field_201540_a;
   }

   public void func_73660_a() {
   }

   @Nullable
   public abstract Biome func_180300_a(BlockPos var1, @Nullable Biome var2);

   public abstract Biome[] func_201535_a(int var1, int var2, int var3, int var4);

   public Biome[] func_201539_b(int var1, int var2, int var3, int var4) {
      return this.func_201537_a(var1, var2, var3, var4, true);
   }

   public abstract Biome[] func_201537_a(int var1, int var2, int var3, int var4, boolean var5);

   public abstract Set<Biome> func_201538_a(int var1, int var2, int var3);

   @Nullable
   public abstract BlockPos func_180630_a(int var1, int var2, int var3, List<Biome> var4, Random var5);

   public float func_201536_c(int var1, int var2, int var3, int var4) {
      return 0.0F;
   }

   public abstract boolean func_205004_a(Structure<?> var1);

   public abstract Set<IBlockState> func_205706_b();

   static {
      field_201540_a = Lists.newArrayList(new Biome[]{Biomes.field_76767_f, Biomes.field_76772_c, Biomes.field_76768_g, Biomes.field_76784_u, Biomes.field_76785_t, Biomes.field_76782_w, Biomes.field_76792_x});
   }
}

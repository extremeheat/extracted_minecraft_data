package net.minecraft.world.gen.feature.template;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;

public class PlacementSettings {
   private Mirror field_186228_a;
   private Rotation field_186229_b;
   private BlockPos field_207666_c;
   private boolean field_186230_c;
   @Nullable
   private Block field_186231_d;
   @Nullable
   private ChunkPos field_186232_e;
   @Nullable
   private MutableBoundingBox field_186233_f;
   private boolean field_186234_g;
   private boolean field_204765_h;
   private float field_189951_h;
   @Nullable
   private Random field_189952_i;
   @Nullable
   private Long field_189953_j;
   @Nullable
   private Integer field_204766_l;
   private int field_204767_m;

   public PlacementSettings() {
      super();
      this.field_186228_a = Mirror.NONE;
      this.field_186229_b = Rotation.NONE;
      this.field_207666_c = new BlockPos(0, 0, 0);
      this.field_186234_g = true;
      this.field_204765_h = true;
      this.field_189951_h = 1.0F;
   }

   public PlacementSettings func_186217_a() {
      PlacementSettings var1 = new PlacementSettings();
      var1.field_186228_a = this.field_186228_a;
      var1.field_186229_b = this.field_186229_b;
      var1.field_207666_c = this.field_207666_c;
      var1.field_186230_c = this.field_186230_c;
      var1.field_186231_d = this.field_186231_d;
      var1.field_186232_e = this.field_186232_e;
      var1.field_186233_f = this.field_186233_f;
      var1.field_186234_g = this.field_186234_g;
      var1.field_204765_h = this.field_204765_h;
      var1.field_189951_h = this.field_189951_h;
      var1.field_189952_i = this.field_189952_i;
      var1.field_189953_j = this.field_189953_j;
      var1.field_204766_l = this.field_204766_l;
      var1.field_204767_m = this.field_204767_m;
      return var1;
   }

   public PlacementSettings func_186214_a(Mirror var1) {
      this.field_186228_a = var1;
      return this;
   }

   public PlacementSettings func_186220_a(Rotation var1) {
      this.field_186229_b = var1;
      return this;
   }

   public PlacementSettings func_207665_a(BlockPos var1) {
      this.field_207666_c = var1;
      return this;
   }

   public PlacementSettings func_186222_a(boolean var1) {
      this.field_186230_c = var1;
      return this;
   }

   public PlacementSettings func_186225_a(Block var1) {
      this.field_186231_d = var1;
      return this;
   }

   public PlacementSettings func_186218_a(ChunkPos var1) {
      this.field_186232_e = var1;
      return this;
   }

   public PlacementSettings func_186223_a(MutableBoundingBox var1) {
      this.field_186233_f = var1;
      return this;
   }

   public PlacementSettings func_189949_a(@Nullable Long var1) {
      this.field_189953_j = var1;
      return this;
   }

   public PlacementSettings func_189950_a(@Nullable Random var1) {
      this.field_189952_i = var1;
      return this;
   }

   public PlacementSettings func_189946_a(float var1) {
      this.field_189951_h = var1;
      return this;
   }

   public Mirror func_186212_b() {
      return this.field_186228_a;
   }

   public PlacementSettings func_186226_b(boolean var1) {
      this.field_186234_g = var1;
      return this;
   }

   public Rotation func_186215_c() {
      return this.field_186229_b;
   }

   public BlockPos func_207664_d() {
      return this.field_207666_c;
   }

   public Random func_189947_a(@Nullable BlockPos var1) {
      if (this.field_189952_i != null) {
         return this.field_189952_i;
      } else if (this.field_189953_j != null) {
         return this.field_189953_j == 0L ? new Random(Util.func_211177_b()) : new Random(this.field_189953_j);
      } else {
         return var1 == null ? new Random(Util.func_211177_b()) : SharedSeedRandom.func_205190_a(var1.func_177958_n(), var1.func_177952_p(), 0L, 987234911L);
      }
   }

   public float func_189948_f() {
      return this.field_189951_h;
   }

   public boolean func_186221_e() {
      return this.field_186230_c;
   }

   @Nullable
   public Block func_186219_f() {
      return this.field_186231_d;
   }

   @Nullable
   public MutableBoundingBox func_186213_g() {
      if (this.field_186233_f == null && this.field_186232_e != null) {
         this.func_186224_i();
      }

      return this.field_186233_f;
   }

   public boolean func_186227_h() {
      return this.field_186234_g;
   }

   void func_186224_i() {
      if (this.field_186232_e != null) {
         this.field_186233_f = this.func_186216_b(this.field_186232_e);
      }

   }

   public boolean func_204763_l() {
      return this.field_204765_h;
   }

   public List<Template.BlockInfo> func_204764_a(List<List<Template.BlockInfo>> var1, @Nullable BlockPos var2) {
      this.field_204766_l = 8;
      if (this.field_204766_l != null && this.field_204766_l >= 0 && this.field_204766_l < var1.size()) {
         return (List)var1.get(this.field_204766_l);
      } else {
         this.field_204766_l = this.func_189947_a(var2).nextInt(var1.size());
         return (List)var1.get(this.field_204766_l);
      }
   }

   @Nullable
   private MutableBoundingBox func_186216_b(@Nullable ChunkPos var1) {
      if (var1 == null) {
         return this.field_186233_f;
      } else {
         int var2 = var1.field_77276_a * 16;
         int var3 = var1.field_77275_b * 16;
         return new MutableBoundingBox(var2, 0, var3, var2 + 16 - 1, 255, var3 + 16 - 1);
      }
   }
}

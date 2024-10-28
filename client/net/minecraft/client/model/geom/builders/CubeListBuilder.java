package net.minecraft.client.model.geom.builders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Direction;

public class CubeListBuilder {
   private static final Set<Direction> ALL_VISIBLE = EnumSet.allOf(Direction.class);
   private final List<CubeDefinition> cubes = Lists.newArrayList();
   private int xTexOffs;
   private int yTexOffs;
   private boolean mirror;

   public CubeListBuilder() {
      super();
   }

   public CubeListBuilder texOffs(int var1, int var2) {
      this.xTexOffs = var1;
      this.yTexOffs = var2;
      return this;
   }

   public CubeListBuilder mirror() {
      return this.mirror(true);
   }

   public CubeListBuilder mirror(boolean var1) {
      this.mirror = var1;
      return this;
   }

   public CubeListBuilder addBox(String var1, float var2, float var3, float var4, int var5, int var6, int var7, CubeDeformation var8, int var9, int var10) {
      this.texOffs(var9, var10);
      this.cubes.add(new CubeDefinition(var1, (float)this.xTexOffs, (float)this.yTexOffs, var2, var3, var4, (float)var5, (float)var6, (float)var7, var8, this.mirror, 1.0F, 1.0F, ALL_VISIBLE));
      return this;
   }

   public CubeListBuilder addBox(String var1, float var2, float var3, float var4, int var5, int var6, int var7, int var8, int var9) {
      this.texOffs(var8, var9);
      this.cubes.add(new CubeDefinition(var1, (float)this.xTexOffs, (float)this.yTexOffs, var2, var3, var4, (float)var5, (float)var6, (float)var7, CubeDeformation.NONE, this.mirror, 1.0F, 1.0F, ALL_VISIBLE));
      return this;
   }

   public CubeListBuilder addBox(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.cubes.add(new CubeDefinition((String)null, (float)this.xTexOffs, (float)this.yTexOffs, var1, var2, var3, var4, var5, var6, CubeDeformation.NONE, this.mirror, 1.0F, 1.0F, ALL_VISIBLE));
      return this;
   }

   public CubeListBuilder addBox(float var1, float var2, float var3, float var4, float var5, float var6, Set<Direction> var7) {
      this.cubes.add(new CubeDefinition((String)null, (float)this.xTexOffs, (float)this.yTexOffs, var1, var2, var3, var4, var5, var6, CubeDeformation.NONE, this.mirror, 1.0F, 1.0F, var7));
      return this;
   }

   public CubeListBuilder addBox(String var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.cubes.add(new CubeDefinition(var1, (float)this.xTexOffs, (float)this.yTexOffs, var2, var3, var4, var5, var6, var7, CubeDeformation.NONE, this.mirror, 1.0F, 1.0F, ALL_VISIBLE));
      return this;
   }

   public CubeListBuilder addBox(String var1, float var2, float var3, float var4, float var5, float var6, float var7, CubeDeformation var8) {
      this.cubes.add(new CubeDefinition(var1, (float)this.xTexOffs, (float)this.yTexOffs, var2, var3, var4, var5, var6, var7, var8, this.mirror, 1.0F, 1.0F, ALL_VISIBLE));
      return this;
   }

   public CubeListBuilder addBox(float var1, float var2, float var3, float var4, float var5, float var6, boolean var7) {
      this.cubes.add(new CubeDefinition((String)null, (float)this.xTexOffs, (float)this.yTexOffs, var1, var2, var3, var4, var5, var6, CubeDeformation.NONE, var7, 1.0F, 1.0F, ALL_VISIBLE));
      return this;
   }

   public CubeListBuilder addBox(float var1, float var2, float var3, float var4, float var5, float var6, CubeDeformation var7, float var8, float var9) {
      this.cubes.add(new CubeDefinition((String)null, (float)this.xTexOffs, (float)this.yTexOffs, var1, var2, var3, var4, var5, var6, var7, this.mirror, var8, var9, ALL_VISIBLE));
      return this;
   }

   public CubeListBuilder addBox(float var1, float var2, float var3, float var4, float var5, float var6, CubeDeformation var7) {
      this.cubes.add(new CubeDefinition((String)null, (float)this.xTexOffs, (float)this.yTexOffs, var1, var2, var3, var4, var5, var6, var7, this.mirror, 1.0F, 1.0F, ALL_VISIBLE));
      return this;
   }

   public List<CubeDefinition> getCubes() {
      return ImmutableList.copyOf(this.cubes);
   }

   public static CubeListBuilder create() {
      return new CubeListBuilder();
   }
}

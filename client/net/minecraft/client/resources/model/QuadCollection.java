package net.minecraft.client.resources.model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class QuadCollection {
   public static final QuadCollection EMPTY = new QuadCollection(List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
   private final List<BakedQuad> all;
   private final List<BakedQuad> unculled;
   private final List<BakedQuad> north;
   private final List<BakedQuad> south;
   private final List<BakedQuad> east;
   private final List<BakedQuad> west;
   private final List<BakedQuad> up;
   private final List<BakedQuad> down;

   QuadCollection(List<BakedQuad> var1, List<BakedQuad> var2, List<BakedQuad> var3, List<BakedQuad> var4, List<BakedQuad> var5, List<BakedQuad> var6, List<BakedQuad> var7, List<BakedQuad> var8) {
      super();
      this.all = var1;
      this.unculled = var2;
      this.north = var3;
      this.south = var4;
      this.east = var5;
      this.west = var6;
      this.up = var7;
      this.down = var8;
   }

   public List<BakedQuad> getQuads(@Nullable Direction var1) {
      byte var3 = 0;
      List var10000;
      //$FF: var3->value
      //0->NORTH
      //1->SOUTH
      //2->EAST
      //3->WEST
      //4->UP
      //5->DOWN
      switch (var1.enumSwitch<invokedynamic>(var1, var3)) {
         case -1 -> var10000 = this.unculled;
         case 0 -> var10000 = this.north;
         case 1 -> var10000 = this.south;
         case 2 -> var10000 = this.east;
         case 3 -> var10000 = this.west;
         case 4 -> var10000 = this.up;
         case 5 -> var10000 = this.down;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public List<BakedQuad> getAll() {
      return this.all;
   }

   public static class Builder {
      private final ImmutableList.Builder<BakedQuad> unculledFaces = ImmutableList.builder();
      private final Multimap<Direction, BakedQuad> culledFaces = ArrayListMultimap.create();

      public Builder() {
         super();
      }

      public Builder addCulledFace(Direction var1, BakedQuad var2) {
         this.culledFaces.put(var1, var2);
         return this;
      }

      public Builder addUnculledFace(BakedQuad var1) {
         this.unculledFaces.add(var1);
         return this;
      }

      private static QuadCollection createFromSublists(List<BakedQuad> var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         int var8 = 0;
         int var16;
         List var9 = var0.subList(var8, var16 = var8 + var1);
         List var10 = var0.subList(var16, var8 = var16 + var2);
         int var18;
         List var11 = var0.subList(var8, var18 = var8 + var3);
         List var12 = var0.subList(var18, var8 = var18 + var4);
         int var20;
         List var13 = var0.subList(var8, var20 = var8 + var5);
         List var14 = var0.subList(var20, var8 = var20 + var6);
         List var15 = var0.subList(var8, var8 + var7);
         return new QuadCollection(var0, var9, var10, var11, var12, var13, var14, var15);
      }

      public QuadCollection build() {
         ImmutableList var1 = this.unculledFaces.build();
         if (this.culledFaces.isEmpty()) {
            return var1.isEmpty() ? QuadCollection.EMPTY : new QuadCollection(var1, var1, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
         } else {
            ImmutableList.Builder var2 = ImmutableList.builder();
            var2.addAll(var1);
            Collection var3 = this.culledFaces.get(Direction.NORTH);
            var2.addAll(var3);
            Collection var4 = this.culledFaces.get(Direction.SOUTH);
            var2.addAll(var4);
            Collection var5 = this.culledFaces.get(Direction.EAST);
            var2.addAll(var5);
            Collection var6 = this.culledFaces.get(Direction.WEST);
            var2.addAll(var6);
            Collection var7 = this.culledFaces.get(Direction.UP);
            var2.addAll(var7);
            Collection var8 = this.culledFaces.get(Direction.DOWN);
            var2.addAll(var8);
            return createFromSublists(var2.build(), var1.size(), var3.size(), var4.size(), var5.size(), var6.size(), var7.size(), var8.size());
         }
      }
   }
}

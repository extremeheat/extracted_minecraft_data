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

   private QuadCollection(final List<BakedQuad> all, final List<BakedQuad> unculled, final List<BakedQuad> north, final List<BakedQuad> south, final List<BakedQuad> east, final List<BakedQuad> west, final List<BakedQuad> up, final List<BakedQuad> down) {
      super();
      this.all = all;
      this.unculled = unculled;
      this.north = north;
      this.south = south;
      this.east = east;
      this.west = west;
      this.up = up;
      this.down = down;
   }

   public List<BakedQuad> getQuads(final @Nullable Direction direction) {
      byte var3 = 0;
      List var10000;
      //$FF: var3->value
      //0->NORTH
      //1->SOUTH
      //2->EAST
      //3->WEST
      //4->UP
      //5->DOWN
      switch (direction.enumSwitch<invokedynamic>(direction, var3)) {
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

      public Builder addCulledFace(final Direction direction, final BakedQuad quad) {
         this.culledFaces.put(direction, quad);
         return this;
      }

      public Builder addUnculledFace(final BakedQuad quad) {
         this.unculledFaces.add(quad);
         return this;
      }

      public Builder addAll(final QuadCollection quadCollection) {
         this.culledFaces.putAll(Direction.UP, quadCollection.up);
         this.culledFaces.putAll(Direction.DOWN, quadCollection.down);
         this.culledFaces.putAll(Direction.NORTH, quadCollection.north);
         this.culledFaces.putAll(Direction.SOUTH, quadCollection.south);
         this.culledFaces.putAll(Direction.EAST, quadCollection.east);
         this.culledFaces.putAll(Direction.WEST, quadCollection.west);
         this.unculledFaces.addAll(quadCollection.unculled);
         return this;
      }

      private static QuadCollection createFromSublists(final List<BakedQuad> all, final int unculledCount, final int northCount, final int southCount, final int eastCount, final int westCount, final int upCount, final int downCount) {
         int index = 0;
         int var16;
         List<BakedQuad> unculled = all.subList(index, var16 = index + unculledCount);
         List<BakedQuad> north = all.subList(var16, index = var16 + northCount);
         int var18;
         List<BakedQuad> south = all.subList(index, var18 = index + southCount);
         List<BakedQuad> east = all.subList(var18, index = var18 + eastCount);
         int var20;
         List<BakedQuad> west = all.subList(index, var20 = index + westCount);
         List<BakedQuad> up = all.subList(var20, index = var20 + upCount);
         List<BakedQuad> down = all.subList(index, index + downCount);
         return new QuadCollection(all, unculled, north, south, east, west, up, down);
      }

      public QuadCollection build() {
         ImmutableList<BakedQuad> unculledFaces = this.unculledFaces.build();
         if (this.culledFaces.isEmpty()) {
            return unculledFaces.isEmpty() ? QuadCollection.EMPTY : new QuadCollection(unculledFaces, unculledFaces, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
         } else {
            ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
            quads.addAll(unculledFaces);
            Collection<BakedQuad> north = this.culledFaces.get(Direction.NORTH);
            quads.addAll(north);
            Collection<BakedQuad> south = this.culledFaces.get(Direction.SOUTH);
            quads.addAll(south);
            Collection<BakedQuad> east = this.culledFaces.get(Direction.EAST);
            quads.addAll(east);
            Collection<BakedQuad> west = this.culledFaces.get(Direction.WEST);
            quads.addAll(west);
            Collection<BakedQuad> up = this.culledFaces.get(Direction.UP);
            quads.addAll(up);
            Collection<BakedQuad> down = this.culledFaces.get(Direction.DOWN);
            quads.addAll(down);
            return createFromSublists(quads.build(), unculledFaces.size(), north.size(), south.size(), east.size(), west.size(), up.size(), down.size());
         }
      }
   }
}

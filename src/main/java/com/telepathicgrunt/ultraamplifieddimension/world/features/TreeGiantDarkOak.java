package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADTags;
import net.minecraft.block.*;
import net.minecraft.structure.Structure;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.io.Reader;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;


public class TreeGiantDarkOak extends Feature<TreeFeatureConfig> {
    private static final BlockState DARK_OAK_LOG = Blocks.DARK_OAK_LOG.getDefaultState();
    private static final BlockState DARK_OAK_LEAVES = Blocks.DARK_OAK_LEAVES.getDefaultState().with(LeavesBlock.DISTANCE, 1);


    public TreeGiantDarkOak(Codec<TreeFeatureConfig> config) {
        super(config);
    }

    @Override
    public final boolean generate(FeatureContext<TreeFeatureConfig> context) {
        Set<BlockPos> set = Sets.newHashSet();
        Set<BlockPos> set1 = Sets.newHashSet();
        Set<BlockPos> set2 = Sets.newHashSet();
        BlockBox mutableboundingbox = new BlockBox(0, 0, 0, 0, 0, 0);
        BiConsumer<BlockPos, BlockState> biConsumer3 = (pos, state) -> {
            set2.add(pos.toImmutable());
            context.getWorld().setBlockState(pos, state, Block.NOTIFY_ALL | Block.FORCE_STATE);
        };
        boolean flag = this.place(context.getWorld(), context.getGenerator(), context.getRandom(), context.getOrigin(), set, set1, mutableboundingbox, context.getConfig());
        if (mutableboundingbox.getMinX() <= mutableboundingbox.getMaxX() && flag && !set.isEmpty()) {
            if (!context.getConfig().decorators.isEmpty()) {
                List<BlockPos> list = Lists.newArrayList(set);
                List<BlockPos> list1 = Lists.newArrayList(set1);
                list.sort(Comparator.comparingInt(Vec3i::getY));
                list1.sort(Comparator.comparingInt(Vec3i::getY));
                context.getConfig().decorators.forEach((treeDecorator) -> {
                    treeDecorator.generate(context.getWorld(), biConsumer3, context.getRandom(), list, list1);
                });
            }

            VoxelSet voxelshapepart = new BitSetVoxelSet(1, 1, 1);
            Structure.updateCorner(context.getWorld(), 3, voxelshapepart, mutableboundingbox.getMinX(), mutableboundingbox.getMinY(), mutableboundingbox.getMinZ());
            return true;
        }
        else {
            return false;
        }
    }

    public boolean place(ModifiableTestableWorld worldReader, ChunkGenerator chunkGenerator, Random rand, BlockPos position, Set<BlockPos> leafSet, Set<BlockPos> trunkSet, BlockBox boundingBox, TreeFeatureConfig config) {
        int height = 11 + rand.nextInt(3);
        WorldAccess world = (WorldAccess) worldReader;

        //checks to see if there is room to generate tree
        if (!this.isSpaceAt( world, chunkGenerator, position, height + 4)) {
            return false;
        }

        BlockPos blockpos = position.down();
        boolean isSoil = isDirtOrGrass(world, blockpos);
        if (!isSoil) {
            return false;
        }
        else if (!this.placeTreeOfHeight(world, position, height)) {
            return false;
        }
        else {
            for (int x = -1; x < 3; x++) {
                for (int z = -1; z < 3; z++) {
                    if (x + z != -2 && x * z != -2 && x + z != 4) {
                        this.setBlockState(world, blockpos.east(x).south(z), Blocks.DIRT.getDefaultState());
                    }
                }
            }

            //creates the dome like crown of leaves first,
            //then puts the ring of wood in the leaves that also makes a smooth transition between leaves and trunk,
            //then lastly, generates the trunk
            this.createCrown(world, position.getX(), position.getZ(), position.getY() + height, 0, rand, trunkSet, boundingBox, config);
            this.createWoodCrown(world, position.getX(), position.getZ(), position.getY() + height, rand, trunkSet, boundingBox, config);

            // we want to generate the trunk as a thick plus sign like this:
            //    [_][_]
            // [_][_][_][_]
            // [_][_][_][_]
            //    [_][_]

            //In addition, every wood block placed has a tiny chance of spawning a tiny patch of leaves or "mini crown".
            int ymax = height + position.getY();
            if (position.getY() > 3) {
                position = position.down(2);
            }

            this.placeColumnOfWood(world, ymax, rand, position, trunkSet, boundingBox, config);
            this.placeColumnOfWood(world, ymax, rand, position.add(1, 0, 0), trunkSet, boundingBox, config);
            this.placeColumnOfWood(world, ymax, rand, position.add(1, 0, 1), trunkSet, boundingBox, config);
            this.placeColumnOfWood(world, ymax, rand, position.add(0, 0, 1), trunkSet, boundingBox, config);
            this.placeColumnOfWood(world, ymax, rand, position.add(-1, 0, 0), trunkSet, boundingBox, config);
            this.placeColumnOfWood(world, ymax, rand, position.add(0, 0, -1), trunkSet, boundingBox, config);
            this.placeColumnOfWood(world, ymax, rand, position.add(-1, 0, 1), trunkSet, boundingBox, config);
            this.placeColumnOfWood(world, ymax, rand, position.add(1, 0, -1), trunkSet, boundingBox, config);
            this.placeColumnOfWood(world, ymax, rand, position.add(0, 0, 2), trunkSet, boundingBox, config);
            this.placeColumnOfWood(world, ymax, rand, position.add(1, 0, 2), trunkSet, boundingBox, config);
            this.placeColumnOfWood(world, ymax, rand, position.add(2, 0, 0), trunkSet, boundingBox, config);
            this.placeColumnOfWood(world, ymax, rand, position.add(2, 0, 1), trunkSet, boundingBox, config);

        }

        return true;
    }


    private void createCrown(WorldAccess world, int x, int z, int y, int extraRadius, Random rand, Set<BlockPos> p_214596_8_, BlockBox p_208519_5_, TreeFeatureConfig p_227252_7_) {
        int i = 4;

        for (int k = y - i; k <= y + 3; ++k) {
            int l = y - k;
            int radius = extraRadius + MathHelper.floor((float) l / (float) i * 1.5F);
            this.growLeavesLayerStrict(world, new BlockPos(x, k, z), radius + (int) ((l > 0 && (k & 1) == 0 ? 0.9 : 1) * 5.5), p_214596_8_, p_208519_5_, p_227252_7_);
        }

        this.growLeavesLayerStrict(world, new BlockPos(x, y + 4, z), 1, p_214596_8_, p_208519_5_, p_227252_7_);
    }


    //generates the wood as an upside-down cone that curves out at end.
    private void createWoodCrown(WorldAccess world, int x, int z, int y, Random rand, Set<BlockPos> p_214596_8_, BlockBox p_208519_5_, TreeFeatureConfig p_227252_7_) {
        int i = 2;

        for (int k = y - (i + 4); k <= y - 1; ++k) {
            int l = y - k;
            int radius;

            if (l < 3) {
                radius = 4;
            }
            else if (l < 5) {
                radius = 3;
            }
            else {
                radius = 2;
            }

            this.growWoodLayerStrict(world, new BlockPos(x, k, z), radius, p_214596_8_, p_208519_5_, p_227252_7_);
        }
    }


    private void createMiniCrown(WorldAccess world, int x, int z, int y, int extraRadius, Random rand, Set<BlockPos> p_214596_8_, BlockBox p_208519_5_, TreeFeatureConfig p_227252_7_) {
        //generates a tiny patch of leaves
        int i = rand.nextInt(2) + 1;

        for (int k = y - i; k <= y + 1; ++k) {
            int l = y - k;
            int radius = extraRadius + MathHelper.floor((float) l / (float) i * 1.5F);
            this.growLeavesLayerStrict(world, new BlockPos(x, k, z), radius + (int) ((l > 0 && (k & 1) == 0 ? 0.9 : 1) * 2), p_214596_8_, p_208519_5_, p_227252_7_);
        }
    }


    /**
     * grow leaves in a circle with the outsides being within the circle
     */
    protected void growLeavesLayerStrict(WorldAccess world, BlockPos layerCenter, int width, Set<BlockPos> p_214596_8_, BlockBox p_208519_5_, TreeFeatureConfig p_227252_7_) {
        int i = width * width;

        for (int j = -width; j <= width + 1; ++j) {
            for (int k = -width; k <= width + 1; ++k) {
                int l = j - 1;
                int i1 = k - 1;

                if (j * j + k * k <= i || l * l + i1 * i1 <= i || j * j + i1 * i1 <= i || l * l + k * k <= i) {
                    BlockPos blockpos = layerCenter.add(j, 0, k);
                    BlockState state = world.getBlockState(blockpos);

                    if (state.isAir() || state.getMaterial() == Material.LEAVES) {
                        this.setBlockState(world, blockpos, DARK_OAK_LEAVES);
                    }
                }
            }
        }
    }


    /**
     * grow wood in a circle with the outsides being within the circle
     */
    protected void growWoodLayerStrict(WorldAccess world, BlockPos layerCenter, int width, Set<BlockPos> p_214596_8_, BlockBox p_208519_5_, TreeFeatureConfig p_227252_7_) {
        int i = width * width;

        for (int j = -width; j <= width + 1; ++j) {
            for (int k = -width; k <= width + 1; ++k) {
                int l = j - 1;
                int i1 = k - 1;

                if (j * j + k * k <= i || l * l + i1 * i1 <= i || j * j + i1 * i1 <= i || l * l + k * k <= i) {
                    BlockPos blockpos = layerCenter.add(j, 0, k);
                    BlockState state = world.getBlockState(blockpos);

                    if (state.isAir() || state.getMaterial() == Material.LEAVES) {
                        this.setBlockState(world, blockpos, DARK_OAK_LOG);
                    }
                }
            }
        }
    }


    private void placeColumnOfWood(WorldAccess world, int yMax, Random rand, BlockPos tempPos, Set<BlockPos> p_214596_8_, BlockBox p_208519_5_, TreeFeatureConfig p_227252_7_) {
        while (tempPos.getY() < yMax) {
            tempPos = tempPos.up();
            BlockState iblockstate = world.getBlockState(tempPos);

            if (iblockstate.getMaterial() == Material.AIR || iblockstate.getMaterial() == Material.LEAVES) {
                if (rand.nextInt(70) == 0) {
                    createMiniCrown(world, tempPos.getX(), tempPos.getZ(), tempPos.getY(), 0, rand, p_214596_8_, p_208519_5_, p_227252_7_);
                }
                else {
                    this.setBlockState(world, tempPos, DARK_OAK_LOG);
                }
            }
        }
    }


    private boolean placeTreeOfHeight(TestableWorld world, BlockPos pos, int height) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable();

        for (int l = 0; l <= height + 1; ++l) {
            int i1 = 1;
            if (l == 0) {
                i1 = 0;
            }

            if (l >= height - 1) {
                i1 = 2;
            }

            for (int j1 = -i1; j1 <= i1; ++j1) {
                for (int k1 = -i1; k1 <= i1; ++k1) {
                    if (cannotBeReplacedByLogs(world, blockpos$Mutable.set(i + j1, j + l, k + k1))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }


    private boolean isSpaceAt(TestableWorld world, ChunkGenerator chunkGenerator, BlockPos leavesPos, int height) {
        boolean flag = true;
        if (leavesPos.getY() >= 1 && leavesPos.getY() + height + 1 <= chunkGenerator.getWorldHeight()) {
            for (int i = 0; i <= 1 + height; ++i) {
                int j = 2;
                if (i == 0) {
                    j = 1;
                }
                else if (i >= 1 + height - 2) {
                    j = 2;
                }

                for (int k = -j; k <= j && flag; ++k) {
                    for (int l = -j; l <= j && flag; ++l) {
                        if (leavesPos.getY() + i < 0 || leavesPos.getY() + i >= chunkGenerator.getWorldHeight() || cannotBeReplacedByLogs(world, leavesPos.add(k, i, l))) {
                            flag = false;
                        }
                    }
                }
            }

            return flag;
        }
        else {
            return false;
        }
    }

    private static boolean isDirtOrGrass(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, (blockState) -> {
            Block block = blockState.getBlock();
            return isSoil(blockState) || block == Blocks.FARMLAND || blockState.isIn(UADTags.COMMON_DIRT_BLOCKS);
        });
    }

    protected static boolean cannotBeReplacedByLogs(TestableWorld world, BlockPos pos) {
        return !world.testBlockState(pos, (blockState) -> {
            Block block = blockState.getBlock();
            return blockState.isAir() || blockState.isIn(BlockTags.LEAVES) || isSoil(blockState) || blockState.isIn(BlockTags.LOGS) || blockState.isIn(BlockTags.SAPLINGS) || block == Blocks.VINE;
        });
    }
}

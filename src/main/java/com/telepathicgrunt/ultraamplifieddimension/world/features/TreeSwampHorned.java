package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import java.util.*;


public class TreeSwampHorned extends Feature<TreeFeatureConfig> {

	public TreeSwampHorned(Codec<TreeFeatureConfig> config) {
		super(config);
	}

	@Override
	public boolean generate(StructureWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, TreeFeatureConfig config) {
		Set<BlockPos> set = Sets.newHashSet();
		Set<BlockPos> set2 = Sets.newHashSet();
		Set<BlockPos> set3 = Sets.newHashSet();
		BlockBox blockBox = BlockBox.empty();
		boolean bl = this.generate(serverWorldAccess, chunkGenerator, random, blockPos, set, set2, config);
		if (blockBox.minX <= blockBox.maxX && bl && !set.isEmpty()) {
			if (!config.decorators.isEmpty()) {
				List<BlockPos> list = Lists.newArrayList(set);
				List<BlockPos> list2 = Lists.newArrayList(set2);
				list.sort(Comparator.comparingInt(Vec3i::getY));
				list2.sort(Comparator.comparingInt(Vec3i::getY));
				config.decorators.forEach((decorator) -> decorator.generate(serverWorldAccess, random, list, list2, set3, blockBox));
			}

			VoxelSet voxelSet = this.placeLogsAndLeaves(serverWorldAccess, blockBox, set, set3);
			Structure.updateCorner(serverWorldAccess, 3, voxelSet, blockBox.minX, blockBox.minY, blockBox.minZ);
			return true;
		} else {
			return false;
		}
	}


	// generate the spooky horned swamp m trees
	private boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos position, Set<BlockPos> logPositions, Set<BlockPos> leavesPositions, TreeFeatureConfig config) {
		int height = config.trunkPlacer.getHeight(random);
		BlockState currentPos = world.getBlockState(position);
		BlockState belowPos = world.getBlockState(position.down());

		// checks to see if there is room to generate tree
		if (!this.isSpaceAt(world, chunkGenerator, position, height)) {
			return false;
		}

		// sets tree in water if there is water below
		if (world.getBlockState(position.down()).getFluidState().isIn(FluidTags.WATER)) {
			position = position.down();
		}

		boolean flag = true;

		if (position.getY() >= 1 && position.getY() + height + 1 <= chunkGenerator.getWorldHeight()) {
			for (int y = position.getY(); y <= position.getY() + 1 + height; ++y) {
				int radius = 1;

				if (y == position.getY()) {
					radius = 0;
				}

				if (y >= position.getY() + 1 + height - 2) {
					radius = 3;
				}

				BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable();

				for (int x = position.getX() - radius; x <= position.getX() + radius && flag; ++x) {
					for (int z = position.getZ() - radius; z <= position.getZ() + radius && flag; ++z) {
						if (y >= 0 && y < chunkGenerator.getWorldHeight()) {
							blockpos$Mutable.set(x, y, z);
							if (!isAirOrLeaves(world, blockpos$Mutable)) {
								if (y > position.getY() && !isWater(world, blockpos$Mutable)) {
									flag = false;
								}
							}
						} else {
							flag = false;
						}
					}
				}
			}

			if (!flag) {
				return false;
			}
			else if (isDirtOrGrass(world, position.down()) && position.getY() < chunkGenerator.getWorldHeight() - height - 1) {

				for (int currentHeight = position.getY() - 4 + height; currentHeight <= position.getY() + height; ++currentHeight) {
					int heightDiff = currentHeight - (position.getY() + height);
					int leavesWidth = 2 - heightDiff / 2;

					for (int x = position.getX() - leavesWidth - 1; x <= position.getX() + leavesWidth; ++x) {
						int xPos = x - position.getX();

						for (int z = position.getZ() - leavesWidth - 1; z <= position.getZ() + leavesWidth; ++z) {
							int zPos = z - position.getZ();
							int isCornerIfThisIsTwo = 0;

							if (xPos == leavesWidth) {
								isCornerIfThisIsTwo++;
							}
							if (zPos == leavesWidth) {
								isCornerIfThisIsTwo++;
							}
							if (xPos == -leavesWidth - 1) {
								isCornerIfThisIsTwo++;
							}
							if (zPos == -leavesWidth - 1) {
								isCornerIfThisIsTwo++;
							}

							// generate leaves if is in corners or if 2/3rd rng is true
							if (isCornerIfThisIsTwo == 2 || random.nextInt(3) < 2 && heightDiff != 0) {
								BlockPos blockpos = new BlockPos(x, currentHeight, z);

								if (isAirOrLeaves(world, blockpos) || isReplaceablePlant(world, blockpos)) {
									this.setBlockState(world, blockpos, config.leavesProvider.getBlockState(random, blockpos)); // .with(LeavesBlock.DISTANCE, 1)
									leavesPositions.add(blockpos);
								}
							}
						}
					}
				}

				// the following four for statements generates the trunk of the tree
				genTrunk(world, position, height, random, logPositions, leavesPositions, config);
				genTrunk(world, position.west(), height, random, logPositions, leavesPositions, config);
				genTrunk(world, position.north(), height, random, logPositions, leavesPositions, config);
				genTrunk(world, position.west().north(), height, random, logPositions, leavesPositions, config);

				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}
	}


	private void genTrunk(StructureWorldAccess world, BlockPos position, int height, Random rand, Set<BlockPos> logPositions, Set<BlockPos> leavesPositions, TreeFeatureConfig config) {
		this.setBlockState(world, position.down(), Blocks.DIRT.getDefaultState());
		BlockPos.Mutable mutable = new BlockPos.Mutable().set(position);

		for (int currentHeight = 0; currentHeight < height; currentHeight++) {
			BlockState iblockstate1 = world.getBlockState(mutable);

			if(!iblockstate1.isOpaque()){
				if (currentHeight != height - 1) {
					this.setBlockState(world, mutable, config.trunkProvider.getBlockState(rand, mutable));
					logPositions.add(mutable);
				} else if(currentHeight == height - 1) {
					this.setBlockState(world, mutable, config.leavesProvider.getBlockState(rand, mutable)); //.with(LeavesBlock.DISTANCE, 1)
					leavesPositions.add(mutable);
				}
			}

			mutable.move(Direction.UP);
		}
	}


	private boolean isSpaceAt(StructureWorldAccess world, ChunkGenerator chunkGenerator, BlockPos leavesPos, int height) {
		boolean spaceFound = true;
		if (leavesPos.getY() >= 1 && leavesPos.getY() + height + 1 <= chunkGenerator.getWorldHeight()) {
			for (int y = 0; y <= 1 + height; ++y) {
				int radius = 2;
				if (y == 0) {
					radius = 1;
				}

				for (int x = -radius; x <= radius && spaceFound; ++x) {
					for (int z = -radius; z <= radius && spaceFound; ++z) {
						if (leavesPos.getY() + y < 0 || leavesPos.getY() + y >= chunkGenerator.getWorldHeight() || !canTreeReplace(world, leavesPos.add(x, y, z))) {
							spaceFound = false;
						}
					}
				}
			}

			return spaceFound;
		} else {
			return false;
		}
	}


	public static boolean canTreeReplace(TestableWorld world, BlockPos pos) {
		return canReplace(world, pos) || world.testBlockState(pos, (state) -> state.isIn(BlockTags.LOGS));
	}

	private static boolean isWater(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, (state) -> state.isOf(Blocks.WATER));
	}


	public static boolean isAirOrLeaves(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, (state) -> state.isAir() || state.isIn(BlockTags.LEAVES));
	}


	private static boolean isDirtOrGrass(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, (state) -> isSoil(state.getBlock()) || state.isOf(Blocks.FARMLAND));
	}


	private static boolean isReplaceablePlant(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, (state) -> {
			Material material = state.getMaterial();
			return material == Material.REPLACEABLE_PLANT;
		});
	}


	public static void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state, 19);
	}


	public static boolean canReplace(TestableWorld IWorldGenerationBaseReader, BlockPos pos) {
		return isAirOrLeaves(IWorldGenerationBaseReader, pos) || isReplaceablePlant(IWorldGenerationBaseReader, pos) || isWater(IWorldGenerationBaseReader, pos);
	}


	private VoxelSet placeLogsAndLeaves(WorldAccess world, BlockBox box, Set<BlockPos> logs, Set<BlockPos> leaves) {
		List<Set<BlockPos>> list = Lists.newArrayList();
		VoxelSet voxelSet = new BitSetVoxelSet(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ());

		for (int j = 0; j < 6; ++j) {
			list.add(Sets.newHashSet());
		}

		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Iterator<BlockPos> var9 = Lists.newArrayList(leaves).iterator();

		BlockPos blockPos2;
		while (var9.hasNext()) {
			blockPos2 = var9.next();
			if (box.contains(blockPos2)) {
				voxelSet.set(blockPos2.getX() - box.minX, blockPos2.getY() - box.minY, blockPos2.getZ() - box.minZ, true, true);
			}
		}

		var9 = Lists.newArrayList(logs).iterator();

		while (var9.hasNext()) {
			blockPos2 = var9.next();
			if (box.contains(blockPos2)) {
				voxelSet.set(blockPos2.getX() - box.minX, blockPos2.getY() - box.minY, blockPos2.getZ() - box.minZ, true, true);
			}

			Direction[] var11 = Direction.values();

			for (Direction direction : var11) {
				mutable.set(blockPos2, direction);
				if (!logs.contains(mutable)) {
					BlockState blockState = world.getBlockState(mutable);
					if (blockState.contains(Properties.DISTANCE_1_7)) {
						list.get(0).add(mutable.toImmutable());
						setBlockStateWithoutUpdatingNeighbors(world, mutable, blockState.with(Properties.DISTANCE_1_7, 1));
						if (box.contains(mutable)) {
							voxelSet.set(mutable.getX() - box.minX, mutable.getY() - box.minY, mutable.getZ() - box.minZ, true, true);
						}
					}
				}
			}
		}

		for (int k = 1; k < 6; ++k) {
			Set<BlockPos> set = list.get(k - 1);
			Set<BlockPos> set2 = list.get(k);

			for (BlockPos blockPos3 : set) {
				if (box.contains(blockPos3)) {
					voxelSet.set(blockPos3.getX() - box.minX, blockPos3.getY() - box.minY, blockPos3.getZ() - box.minZ, true, true);
				}

				Direction[] var27 = Direction.values();

				for (Direction direction2 : var27) {
					mutable.set(blockPos3, direction2);
					if (!set.contains(mutable) && !set2.contains(mutable)) {
						BlockState blockState2 = world.getBlockState(mutable);
						if (blockState2.contains(Properties.DISTANCE_1_7)) {
							int l = blockState2.get(Properties.DISTANCE_1_7);
							if (l > k + 1) {
								BlockState blockState3 = blockState2.with(Properties.DISTANCE_1_7, k + 1);
								setBlockStateWithoutUpdatingNeighbors(world, mutable, blockState3);
								if (box.contains(mutable)) {
									voxelSet.set(mutable.getX() - box.minX, mutable.getY() - box.minY, mutable.getZ() - box.minZ, true, true);
								}

								set2.add(mutable.toImmutable());
							}
						}
					}
				}
			}
		}

		return voxelSet;
	}
}
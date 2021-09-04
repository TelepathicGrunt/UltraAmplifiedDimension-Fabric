package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.EllipsoidFeatureConfig;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Map;
import java.util.WeakHashMap;


public class EllipsoidPocket extends Feature<EllipsoidFeatureConfig>
{
	public EllipsoidPocket(Codec<EllipsoidFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	public boolean generate(FeatureContext<EllipsoidFeatureConfig> context) {
		BlockPos.Mutable blockposMutable = new BlockPos.Mutable();
		BlockState blockToReplace;
		float angleOfRotation = (float) (Math.PI * context.getRandom().nextFloat());
		float sinOfAngle = MathHelper.sin(angleOfRotation);
		float cosOfAngle = MathHelper.cos(angleOfRotation);
		float size = context.getConfig().size * 0.5f;
		Chunk cachedChunk;
		float stretchedFactor = 0.7f;
		if(context.getConfig().size < 10) stretchedFactor = 1;
		int maxY = (int) (size / 3);
		int minY = -maxY - 	1;

		for(int y = minY; y <= maxY; y++) {
			float yModified = y;
			if(y < 0){
				yModified = y + 0.25f;
			}
			else if (y > 0){
				y = (int)(y + 0.5f);
			}

			// context.getRandom().nextFloat() took up too much time
//			if(y < 0){
//				yModified = y + context.getRandom().nextFloat() * 0.5f;
//			}
//			else if (y > 0){
//				y = (int) ((y + 0.25f) + (context.getRandom().nextFloat() * 0.5f));
//			}

			float percentageOfRadius = 1f - (yModified / size) * (yModified / size) * 3;
			float majorRadiusSq = (size * percentageOfRadius) * (size * percentageOfRadius);
			float minorRadiusSq = (size * stretchedFactor * percentageOfRadius) * (size * stretchedFactor * percentageOfRadius);
			
			for(int x = (int) -size; x < size; x++) {
				for(int z = (int) -size; z < size; z++) {
					float majorComp;
					float minorComp;

					majorComp = (x + 0.5f) * cosOfAngle - (z + 0.5f) * sinOfAngle;
					minorComp = (x + 0.5f) * sinOfAngle + (z + 0.5f) * cosOfAngle;

					// context.getRandom().nextFloat() took up too much time
//					if(context.getConfig().size > 10){
//						majorComp = (x + 0.275f) * cosOfAngle - (z + 0.275f) * sinOfAngle;
//						minorComp = (x + 0.275f) * sinOfAngle + (z + 0.275f) * cosOfAngle;
//					}
//					else {
//						majorComp = ((x + 0.25f) + (context.getRandom().nextFloat() * 0.5f)) * cosOfAngle - ((z + 0.25f) + (context.getRandom().nextFloat() * 0.5f)) * sinOfAngle;
//						minorComp = ((x + 0.25f) + (context.getRandom().nextFloat() * 0.5f)) * sinOfAngle + ((z + 0.25f) + (context.getRandom().nextFloat() * 0.5f)) * cosOfAngle;
//					}

					float result = ((majorComp * majorComp) / (majorRadiusSq * majorRadiusSq)) +
									((minorComp * minorComp) / (minorRadiusSq * minorRadiusSq));

					if(result * 100f < 1f && !(x == 0 && z == 0 && y * y >= (size * size))) {
						blockposMutable.set(context.getOrigin().getX() + x, context.getOrigin().getY() + y, context.getOrigin().getZ() + z);
						cachedChunk = getCachedChunk(context.getWorld(), blockposMutable);

						blockToReplace = cachedChunk.getBlockState(blockposMutable);
						if(context.getConfig().target.test(blockToReplace, context.getRandom())) {
							boolean solidState = context.getConfig().state.isOpaque();
							if(solidState){
								cachedChunk.setBlockState(blockposMutable, context.getConfig().state, false);
							}

							// if our replacement state is not solid, do not expose any liquids then.
							else {
								boolean touchingLiquid = false;
								for(Direction direction : Direction.values()){
									if(direction != Direction.DOWN){
										blockposMutable.move(direction);
										cachedChunk = getCachedChunk(context.getWorld(), blockposMutable);

										if(!cachedChunk.getBlockState(blockposMutable).getFluidState().isEmpty()){
											touchingLiquid = true;
											blockposMutable.move(direction.getOpposite());
											break;
										}

										blockposMutable.move(direction.getOpposite());
									}
								}

								if(!touchingLiquid){
									cachedChunk = getCachedChunk(context.getWorld(), blockposMutable);
									cachedChunk.setBlockState(blockposMutable, context.getConfig().state, false);
								}
							}
						}
					}
				}
			}
		}
		
		return true;
	}


	private static final WeakHashMap<RegistryKey<World>, Map<Long, Chunk>> CACHED_CHUNKS_ALL_WORLDS = new WeakHashMap<>();
	public static Chunk getCachedChunk(StructureWorldAccess world, BlockPos blockpos) {

		// get the world's cache or make one if map doesnt exist.
		RegistryKey<World> worldKey = world.toServerWorld().getRegistryKey();
		Map<Long, Chunk> worldStorage = CACHED_CHUNKS_ALL_WORLDS.computeIfAbsent(worldKey, k -> new WeakHashMap<>());

		// shrink cache if it is too large to clear out old chunk refs no longer needed.
		if(worldStorage.size() > 9){
			worldStorage.clear();
		}

		// gets the chunk saved or does the expensive .getChunk to get it if it isn't cached yet.
		long posLong = (long) (blockpos.getX() >> 4) & 4294967295L | ((long)(blockpos.getZ() >> 4) & 4294967295L) << 32;
		Chunk cachedChunk = worldStorage.get(posLong);
		if(cachedChunk == null){
			cachedChunk = world.getChunk(blockpos);
			worldStorage.put(posLong, cachedChunk);
		}

		return cachedChunk;
	}
}

package com.telepathicgrunt.ultraamplifieddimension.blocks;

import com.mojang.datafixers.util.Pair;
import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.dimension.AmplifiedPortalCreation;
import com.telepathicgrunt.ultraamplifieddimension.dimension.UADDimension;
import com.telepathicgrunt.ultraamplifieddimension.dimension.UADWorldSavedData;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Random;


public class AmplifiedPortalBlock extends Block
{
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);


	public AmplifiedPortalBlock()
	{
		super(Settings.of(Material.GLASS, MapColor.BLACK).luminance((blockState) -> 15).strength(5.0F, 3600000.0F));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPE;
	}

	@Override
	@SuppressWarnings("deprecation")
	public ActionResult onUse(BlockState thisBlockState, World world, BlockPos position, PlayerEntity playerEntity, Hand playerHand, BlockHitResult raytraceResult)
	{
		// Extra checking to make sure it's just the player alone and not riding, being ridden, etc 
		// Also makes sure player isn't sneaking so players can crouch place blocks on the portal
		// But only teleport if we aren't in UA worldtype
		if (!world.isClient &&
			!playerEntity.hasVehicle() &&
			!playerEntity.hasPassengers() &&
			playerEntity.canUsePortals() &&
			!playerEntity.isInSneakingPose() &&
			playerEntity.getActiveHand() == playerHand)
		{
			MinecraftServer minecraftserver = playerEntity.getServer();


			// Gets previous dimension
			RegistryKey<World> destinationKey;
			float pitch;
			float yaw;
			boolean enteringUA = false;

			// Player is leaving Ultra Amplified dimension
			if (playerEntity.world.getRegistryKey().equals(UADDimension.UAD_WORLD_KEY)) {
				if (UltraAmplifiedDimension.UAD_CONFIG.forceExitToOverworld)
				{
					// Go to Overworld directly because of config option.
					destinationKey = World.OVERWORLD;
				}
				else {
					// Gets stored dimension
					destinationKey = UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).getNonUADimension();

					// Impressive if this is reached...........
					if (destinationKey == null) {
						destinationKey = World.OVERWORLD;
					}
				}

				// Get direction to face for Non-UA dimension
				pitch = UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).getNonUAPitch();
				yaw = UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).getNonUAYaw();

				// Set current UA position and rotations
				UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).setUAPos(playerEntity.getPos());
				UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).setUAPitch(playerEntity.getPitch());
				UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).setUAYaw(playerEntity.getYaw());
			}

			// Otherwise, take us to Ultra Amplified Dimension.
			else {
				destinationKey = UADDimension.UAD_WORLD_KEY;
				pitch = UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).getUAPitch();
				yaw = UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).getUAYaw();
				enteringUA = true;

				// Set current nonUA position, rotations, and dimension before teleporting
				UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).setNonUAPos(playerEntity.getPos());
				UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).setNonUADimension(playerEntity.world.getRegistryKey());
				UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).setNonUAPitch(playerEntity.getPitch());
				UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).setNonUAYaw(playerEntity.getYaw());
			}

			//Get the world itself. If the world doesn't exist, get Overworld instead.
			ServerWorld destinationWorld = minecraftserver.getWorld(destinationKey);
			if(destinationWorld == null){
				destinationKey = World.OVERWORLD;
				destinationWorld = minecraftserver.getWorld(destinationKey);
			}


			//Create portal in UA if it hasn't been made yet in the dimension
			if(destinationKey.equals(UADDimension.UAD_WORLD_KEY)) {
				if (!AmplifiedPortalCreation.checkForGeneratedPortal(destinationWorld)) {
					AmplifiedPortalCreation.generatePortal(destinationWorld);
				}
			}

			// Gets top block in other world or original location
			Vec3d playerVec3Pos;
			if (enteringUA && UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).getUAPos() == null) {
				// If it is player's first time teleporting to UA dimension, 
				// find top block at world origin closest to portal
				BlockPos worldOriginBlockPos = new BlockPos(10, 0, 8);
				int portalY = destinationWorld.getTopY();

				//finds where portal block is
				while (portalY > destinationWorld.getBottomY()) {
					if (destinationWorld.getBlockState(worldOriginBlockPos.up(portalY)) == UADBlocks.AMPLIFIED_PORTAL.getDefaultState()) {
						break;
					}
					portalY--;
				}

				//not sure how the portal block was not found but if so, spawn player at highest piece of land
				if (portalY == destinationWorld.getBottomY()) {
					playerVec3Pos = Vec3d.ofCenter(destinationWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, worldOriginBlockPos)).add(0, 0.5D, 0);
				}
				else {
					//portal was found so try to find 2 air spaces around it that the player can spawn at
					worldOriginBlockPos = worldOriginBlockPos.up(portalY - 1);
					boolean validSpaceFound = false;

					for (int x = -2; x < 3; x++) {
						for (int z = -2; z < 3; z++) {
							if (x == -2 || x == 2 || z == -2 || z == 2) {
								if (destinationWorld.getBlockState(worldOriginBlockPos.add(x, 0, z)).getMaterial() == Material.AIR && destinationWorld.getBlockState(worldOriginBlockPos.add(x, 1, z)).getMaterial() == Material.AIR) {
									//valid space for player is found
									worldOriginBlockPos = worldOriginBlockPos.add(x, 0, z);
									validSpaceFound = true;
									z = 3;
									x = 3;
								}
							}
						}
					}

					if (!validSpaceFound) {
						//no valid space found around portal. get top solid block instead
						worldOriginBlockPos = destinationWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(10, destinationWorld.getTopY(), 8));
					}

					playerVec3Pos = Vec3d.ofCenter(worldOriginBlockPos).add(0, -0.3D, 0); // Set where player spawns
				}

			}
			else {
				// Otherwise, just go to where our stored location is
				if (enteringUA) {
					// Will never be null because we did check above for null already.
					playerVec3Pos = UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).getUAPos();
				}
				else {
					// Check for null which would be impressive if it occurs
					if (destinationWorld != null && (UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).getNonUAPos() == null || UltraAmplifiedDimension.UAD_CONFIG.forceExitToOverworld))
					{
						// Set player at world spawn then with Amplified Portal at feet
						// The portal will try to not replace any block and be at the next air block above non-air blocks.
						BlockPos playerBlockPos = destinationWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING, destinationWorld.getSpawnPos());
						BlockState blockState = destinationWorld.getBlockState(playerBlockPos);
						while (blockState.getMaterial() != Material.AIR && playerBlockPos.getY() < destinationWorld.getTopY() - 2) {
							playerBlockPos = playerBlockPos.up();
							blockState = destinationWorld.getBlockState(playerBlockPos);
						}

						destinationWorld.setBlockState(playerBlockPos, UADBlocks.AMPLIFIED_PORTAL.getDefaultState());

						playerVec3Pos = Vec3d.ofCenter(playerBlockPos).add(0, 0.5D, 0);
					}
					else {
						// Get position in non UA dimension as it isn't null
						playerVec3Pos = UltraAmplifiedDimension.PLAYER_COMPONENT.get(playerEntity).getNonUAPos();
					}
				}
			}

			//dunno how a sleeping player clicked on the portal but if they do, they wake up
			if (playerEntity.isSleeping()) {
				playerEntity.wakeUp();
			}

			UADWorldSavedData.get((ServerWorld) world).addPlayer(playerEntity, destinationKey, playerVec3Pos, new Pair<>(yaw, pitch));
			//particles for other players to see when a player is leaving
			createLotsOfParticles((ServerWorld)world, playerEntity.getPos(), world.random);
			return ActionResult.SUCCESS;
		}
		
		return super.onUse(thisBlockState, world, position, playerEntity, playerHand, raytraceResult);
	}


	/**
	 * mining portal block in ultra amplified dimension will be denied if it is the highest Amplified Portal Block at x=8,
	 * z=8
	 */
	public static boolean removedByPlayer(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity)
	{

		// if player is in creative mode, just remove block completely
		if (player != null && player.isCreative()) {
			return true;
		}

		// otherwise, check to see if we are mining the highest portal block at world
		// origin in UA dimension
		else {
			// if we are in UA dimension
			if (world.getRegistryKey().equals(UADDimension.UAD_WORLD_KEY)) {

				// if we are at default portal coordinate
				if (pos.getX() == 8 && pos.getZ() == 8) {

					// finds the highest portal at world origin
					BlockPos posOfHighestPortal = new BlockPos(pos.getX(), world.getTopY(), pos.getZ());
					while (posOfHighestPortal.getY() >= world.getBottomY()) {
						Block blockToCheck = world.getBlockState(posOfHighestPortal).getBlock();
						if (blockToCheck == UADBlocks.AMPLIFIED_PORTAL) {
							break;
						}

						posOfHighestPortal = posOfHighestPortal.down();
					}

					// if this block being broken is the highest portal, return false to end method and not break the portal block
					return posOfHighestPortal.getY() != pos.getY();
				}
			}
		}

		// otherwise, allow the block to break
		return true;
	}

	// has no item form
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	
	/**
	 * Spawns with tons of particles upon creation
	 */
	@Deprecated
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
		createLotsOfParticles((ServerWorld)world, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), world.random);
	}

	/**
	 * Safe to call serverside as it sends particle packets to clients
	 */
	public static void createLotsOfParticles(ServerWorld world, Vec3d position, Random random) {
		double xPos = position.getX() + 0.5D;
		double yPos = position.getY() + 0.5D;
		double zPos = position.getZ() + 0.5D;
		double xOffset = (random.nextFloat() - 0.4D) * 0.8D;
		double zOffset = (random.nextFloat() - 0.4D) * 0.8D;

		world.spawnParticles(ParticleTypes.FLAME, xPos, yPos, zPos, 50, xOffset, 0, zOffset, random.nextFloat() * 0.1D + 0.05D);
	}

	/**
	 * more frequent particles than normal EndPortal block
	 */
	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState stateIn, World world, BlockPos pos, Random rand) {
		double d0 = pos.getX() + (rand.nextFloat() * 3 - 1);
		double d1 = pos.getY() + (rand.nextFloat() * 3 - 1);
		double d2 = pos.getZ() + (rand.nextFloat() * 3 - 1);
		world.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
	}
}

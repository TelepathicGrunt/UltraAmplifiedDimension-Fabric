package com.telepathicgrunt.ultraamplifieddimension.mixin.dimension;

import com.telepathicgrunt.ultraamplifieddimension.dimension.biomeprovider.UADBiomeProvider;
import net.minecraft.server.command.LocateBiomeCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LocateBiomeCommand.class)
public class LocateBiomeCommandMixin {

    /**
     * Increase biome search radius in UAD if the original search returned no found biome
     * @author - TelepathicGrunt
     */
    @ModifyVariable(
            method = "execute(Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/util/Identifier;)I",
            at = @At(value = "INVOKE_ASSIGN",target = "Lnet/minecraft/server/world/ServerWorld;locateBiome(Lnet/minecraft/world/biome/Biome;Lnet/minecraft/util/math/BlockPos;II)Lnet/minecraft/util/math/BlockPos;"),
            ordinal = 1
    )
    private static BlockPos uad_expandSearch(BlockPos blockPos, ServerCommandSource source, Identifier biomeID) {
        if(blockPos == null && source.getWorld().getChunkManager().getChunkGenerator().getBiomeSource() instanceof UADBiomeProvider){
            // Will never be null as the command already checked and validated that the biome exists
            Biome biome = source.getMinecraftServer().getRegistryManager().get(Registry.BIOME_KEY).get(biomeID);
            return source.getWorld().locateBiome(biome, new BlockPos(source.getPosition()), 36000, 36);
        }

        return blockPos;
    }
}
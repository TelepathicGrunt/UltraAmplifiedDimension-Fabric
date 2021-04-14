# Made for Minecraft v.1.16.5

## Created by TelepathicGrunt



Hello! If you are reading this, you must be on Github then! I strongly encourage you to only download and run the master branch as that should be the most stable version of my mod. 

If you try to use the latest Minecraft version branch, you may run into bugs and issues as I am experimenting and working on that latest version. Older version branches should be alright.

---------------------------------------
# | Ultra Amplified Dimension changelog |


## (V.9.1.0 Changes) (1.16.5 Minecraft)

##### Dependencies:
- Switch to requiring a newer version of Cloth Config that has Autoconfig merged inside. 
  You can remove the Autoconfig mod and only use Cloth for UAD.

##### Dimension:
- The UAD dimension json file now lets you put `"import_all_modded_biomes": true` into the biome_source section.
  This is a quick and dirty way to import all modded biomes into the dimension but those biomes will typically not look good.
  Along with it, you can put `"imported_biome_blacklist": ["mod1:slick_biome", "mod1:crazy_biome"]` into there as well to
  blacklist any biome that `import_all_modded_biomes` will grab. 
  
##### Portal:
- `ultra_amplified_dimension:portal_center_blocks` block tag has been added that lets you change what block is required
  for the center of the portal. This has Polished Diorite by default. if you change this, you might want to change the
  Amplified Portal block's texture and loot table to match the new block you are using.

##### Biomes:
- Fixed sea being covered in terrain blocks if lowered below y = 61. Looks amazing if you put UAD's nether biome into the vanilla nether now!

##### Dungeons:
- Reduced chance of Blue Ice in Snowy Dungeons.

- Fixed Nether Dungeons having Nether Bricks placed in mid-air due to broken processor file.

- Fixed ceiling of Desert Dungeons looking weird when it meets the wall.

- Dungeons will now log error if fed an invalid identifier to a non-existent nbt file.

- Fixed Dungeon Chests being placed on walls instead of floor.

- Dungeons now use Post Processor files to place Vines and other stuff.

##### Misc:
- Made my modifyConstant mixins to the surfacebuilders no longer crash if someone else also modifyConstant the same spot.

- Made Swamp Cross use correct method for setting its chest loottable.


## (V.9.0.4 Changes) (1.16.4 Minecraft)

##### Dimension:
-Switched to a safer mixin to get the world's seed if no seed is specified in the JSON.


## (V.9.0.3 Changes) (1.16.4 Minecraft)

##### Mixins:
-Prefixed all my accessor and invoker mixins due to this bug in mixins that causes a crash for same named mixins.
 https://github.com/SpongePowered/Mixin/issues/430
 
 
## (V.9.0.2 Changes) (1.16.4 Minecraft)

##### Blocks:
-Fixed bug where blocks will not drop anything due to me screwing up a Fabric API event lmao.


## (V.9.0.1 Changes) (1.16.4 Minecraft)

##### Biomes:
-Fixed sky/fog color for nether biomes.

-Made deep warm/frozen oceans more common.

-Fixed mutated biomes not spawning.


## (V.9.0.0 Changes) (1.16.4 Minecraft)

##### MAJOR:

-Ported from Forge to Fabric!



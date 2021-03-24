# Made for Minecraft v.1.16.4

## Created by TelepathicGrunt



Hello! If you are reading this, you must be on Github then! I strongly encourage you to only download and run the master branch as that should be the most stable version of my mod. 

If you try to use the latest Minecraft version branch, you may run into bugs and issues as I am experimenting and working on that latest version. Older version branches should be alright.

---------------------------------------
# | Ultra Amplified Dimension changelog |


## (V.9.0.5 Changes) (1.16.4 Minecraft)

##### Dungeons:
- Reduced chance of Blue Ice in Snowy Dungeons.

- Fixed Nether Dungeons having Nether Bricks placed in mid-air due to broken processor file.

- Fixed ceiling of Desert Dungeons looking weird when it meets the wall.

##### Misc:
- Made my modifyConstant mixins to the surfacebuilders no longer crash if someone else also modifyConstant the same spot.


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


